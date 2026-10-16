package com.uberclone.ride_service.service;

import com.uberclone.ride_service.client.DriverClient;
import com.uberclone.ride_service.dto.DriverResponse;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurgePricingService {

    private final DriverClient driverClient;
    private final RideRepository rideRepository;

    public double getSurgeMultiplier() {

        // Get current available drivers
        List<DriverResponse> availableDrivers = driverClient.getAvailableDrivers();
        int driverCount = availableDrivers.size();

        // Get current pending ride requests
        List<Ride> pendingRides = rideRepository
                .findByStatus(Ride.RideStatus.REQUESTED);
        int pendingCount = pendingRides.size();

        // Calculate surge multiplier
        double multiplier = calculateMultiplier(driverCount, pendingCount);

        log.info("Surge Pricing — Drivers: {}, Pending Rides: {}, Multiplier: {}x",
                driverCount, pendingCount, multiplier);

        return multiplier;
    }

    private double calculateMultiplier(int drivers, int pendingRides) {

        // No drivers at all → max surge
        if (drivers == 0) {
            log.warn("No drivers available — applying maximum surge 2.5x");
            return 2.5;
        }

        double demandRatio = (double) pendingRides / drivers;

        if (demandRatio < 0.5) {
            return 1.0;   // normal pricing
        } else if (demandRatio < 1.0) {
            return 1.2;   // low surge
        } else if (demandRatio < 2.0) {
            return 1.5;   // medium surge
        } else if (demandRatio < 3.0) {
            return 2.0;   // high surge
        } else {
            return 2.5;   // very high surge
        }
    }
}