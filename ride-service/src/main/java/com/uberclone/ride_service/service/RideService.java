package com.uberclone.ride_service.service;

import com.uberclone.ride_service.client.DriverClient;
import com.uberclone.ride_service.controller.WebSocketController;
import com.uberclone.ride_service.dto.BookRideRequest;
import com.uberclone.ride_service.dto.DriverResponse;
import com.uberclone.ride_service.dto.RideCompletedEvent;
import com.uberclone.ride_service.dto.RideStatusUpdate;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.exception.NoDriverAvailableException;
import com.uberclone.ride_service.exception.RideNotFoundException;
import com.uberclone.ride_service.kafka.RideEventProducer;
import com.uberclone.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverClient driverClient;
    private final RideEventProducer rideEventProducer;
    private final SurgePricingService surgePricingService;
    private final WebSocketController webSocketController;

    @CircuitBreaker(name = "driverService", fallbackMethod = "bookRideFallback")
    @Retry(name = "driverService")
    public Ride bookRide(BookRideRequest request) {

        // Step 1: Find available drivers via Feign call to Driver Service
        List<DriverResponse> availableDrivers = driverClient.getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            throw new NoDriverAvailableException("No drivers available at the moment");
        }

        DriverResponse assignedDriver = availableDrivers.get(0);

        double surgeMultiplier = surgePricingService.getSurgeMultiplier();

        double baseFare = calculateFare(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropLatitude(), request.getDropLongitude()
        );
        double finalFare = Math.round(baseFare * surgeMultiplier * 100.0) / 100.0;

        double fare = calculateFare(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDropLatitude(), request.getDropLongitude()
        );

        // Step 4: Create the ride
        Ride ride = Ride.builder()
                .userId(request.getUserId())
                .driverId(assignedDriver.getId())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .pickupAddress(request.getPickupAddress())
                .dropLatitude(request.getDropLatitude())
                .dropLongitude(request.getDropLongitude())
                .dropAddress(request.getDropAddress())
                .status(Ride.RideStatus.REQUESTED)
                .fare(finalFare)
                .surgeMultiplier(surgeMultiplier)
                .bookedAt(LocalDateTime.now())
                .build();

        Ride savedRide = rideRepository.save(ride);

        // Step 5: Mark driver as unavailable via Feign call
        driverClient.updateAvailability(assignedDriver.getId(), false);

        log.info("Ride {} booked for user {} with driver {}",
                savedRide.getId(), request.getUserId(), assignedDriver.getId());

        return savedRide;
    }

    public Ride acceptRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(Ride.RideStatus.ACCEPTED);
        Ride saved = rideRepository.save(ride);

        // Push WebSocket update to rider
        webSocketController.sendRideUpdate(rideId, new RideStatusUpdate(
                rideId,
                "ACCEPTED",
                saved.getDriverId(),
                0.0, 0.0,
                "Your driver has accepted the ride!"
        ));

        return saved;
    }

    public Ride startRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(Ride.RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());
        Ride saved = rideRepository.save(ride);

        // Push WebSocket update
        webSocketController.sendRideUpdate(rideId, new RideStatusUpdate(
                rideId,
                "IN_PROGRESS",
                saved.getDriverId(),
                0.0, 0.0,
                "Your ride has started!"
        ));

        return saved;
    }

    public Ride cancelRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(Ride.RideStatus.CANCELLED);

        // Free up the driver if one was assigned
        if (ride.getDriverId() != null) {
            driverClient.updateAvailability(ride.getDriverId(), true);
        }

        return rideRepository.save(ride);
    }

    public Ride getRideById(String rideId) {
        return getRideOrThrow(rideId);
    }

    public List<Ride> getRidesByUser(String userId) {
        return rideRepository.findByUserId(userId);
    }

    public List<Ride> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    // Simple fare calculation using Haversine distance formula
    private double calculateFare(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS * c;

        // Base fare: ₹50 + ₹12 per km
        return Math.round((50 + (distanceKm * 12)) * 100.0) / 100.0;
    }

    private Ride getRideOrThrow(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found: " + rideId));
    }

    public Ride completeRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.setStatus(Ride.RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        driverClient.updateAvailability(ride.getDriverId(), true);
        Ride savedRide = rideRepository.save(ride);

        // Push WebSocket update
        webSocketController.sendRideUpdate(rideId, new RideStatusUpdate(
                rideId,
                "COMPLETED",
                savedRide.getDriverId(),
                0.0, 0.0,
                "Ride completed! Total fare: ₹" + savedRide.getFare()
        ));

        // Publish Kafka event
        RideCompletedEvent event = new RideCompletedEvent(
                savedRide.getId(),
                savedRide.getUserId(),
                savedRide.getDriverId(),
                savedRide.getFare(),
                savedRide.getPickupAddress(),
                savedRide.getDropAddress()
        );
        rideEventProducer.publishRideCompleted(event);

        log.info("Ride {} completed and event published", rideId);
        return savedRide;
    }

    public Ride bookRideFallback(BookRideRequest request, Exception ex) {
        log.error("Circuit breaker activated for bookRide. Cause: {}", ex.getMessage());
        throw new RuntimeException(
                "Driver service is currently unavailable. Please try again in a moment."
        );
    }
}