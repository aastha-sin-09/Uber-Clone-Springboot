package com.uberclone.ride_service.controller;

import com.uberclone.ride_service.dto.BookRideRequest;
import com.uberclone.ride_service.dto.BookRideResponse;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.service.RideService;
import com.uberclone.ride_service.service.SurgePricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@Tag(name = "Ride Controller", description = "Ride booking, tracking and lifecycle management")
public class RideController {

    private final RideService rideService;
    private final SurgePricingService surgePricingService;

    @PostMapping("/book")
    @Operation(summary = "Book a ride",
            description = "Creates ride request, applies surge pricing, assigns nearest driver")
    public ResponseEntity<BookRideResponse> bookRide(
            @Valid @RequestBody BookRideRequest request) {
        Ride ride = rideService.bookRide(request);

        BookRideResponse response = new BookRideResponse(
                ride.getId(),
                ride.getDriverId(),
                ride.getFare() / ride.getSurgeMultiplier(),  // base fare
                ride.getSurgeMultiplier(),
                ride.getFare(),                               // final fare
                ride.getStatus().name(),
                ride.getSurgeMultiplier() > 1.0
                        ? "Surge pricing active: " + ride.getSurgeMultiplier() + "x"
                        : "Normal pricing"
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{rideId}/accept")
    @Operation(summary = "Accept ride", description = "Driver accepts the ride request")
    public ResponseEntity<Ride> acceptRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.acceptRide(rideId));
    }

    @PutMapping("/{rideId}/start")
    @Operation(summary = "Start ride", description = "Driver starts the ride")
    public ResponseEntity<Ride> startRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PutMapping("/{rideId}/complete")
    @Operation(summary = "Complete ride",
            description = "Completes ride, frees driver, triggers Kafka notification event")
    public ResponseEntity<Ride> completeRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @PutMapping("/{rideId}/cancel")
    @Operation(summary = "Cancel ride", description = "Cancels ride and frees assigned driver")
    public ResponseEntity<Ride> cancelRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }

    @GetMapping("/{rideId}")
    @Operation(summary = "Get ride details")
    public ResponseEntity<Ride> getRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user ride history")
    public ResponseEntity<List<Ride>> getRidesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(rideService.getRidesByUser(userId));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get driver ride history")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    @GetMapping("/surge")
    @Operation(summary = "Get surge multiplier",
            description = "Returns current surge pricing based on driver/rider demand ratio")
    public ResponseEntity<Map<String, Object>> getCurrentSurge() {
        double multiplier = surgePricingService.getSurgeMultiplier();
        Map<String, Object> response = new HashMap<>();
        response.put("surgeMultiplier", multiplier);
        response.put("isSurge", multiplier > 1.0);
        response.put("message", multiplier > 1.0
                ? "High demand! Prices are " + multiplier + "x normal"
                : "Normal pricing available");
        return ResponseEntity.ok(response);
    }
}