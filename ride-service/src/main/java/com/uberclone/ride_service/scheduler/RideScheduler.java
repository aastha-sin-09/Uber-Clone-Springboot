package com.uberclone.ride_service.scheduler;

import com.uberclone.ride_service.client.DriverClient;
import com.uberclone.ride_service.entity.Ride;
import com.uberclone.ride_service.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideScheduler {

    private final RideRepository rideRepository;
    private final DriverClient driverClient;

    // Runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void cancelStalePendingRides() {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        // Find all rides stuck in REQUESTED for more than 5 minutes
        List<Ride> staleRides = rideRepository
                .findByStatusAndBookedAtBefore(
                        Ride.RideStatus.REQUESTED,
                        fiveMinutesAgo
                );

        if (staleRides.isEmpty()) {
            log.info("Scheduler: No stale rides found");
            return;
        }

        log.warn("Scheduler: Found {} stale ride(s) — auto-cancelling", staleRides.size());

        for (Ride ride : staleRides) {
            ride.setStatus(Ride.RideStatus.CANCELLED);
            rideRepository.save(ride);

            // Free up the driver if one was assigned
            if (ride.getDriverId() != null) {
                try {
                    driverClient.updateAvailability(ride.getDriverId(), true);
                } catch (Exception e) {
                    log.error("Could not free driver {} for stale ride {}",
                            ride.getDriverId(), ride.getId());
                }
            }

            log.warn("Scheduler: Auto-cancelled ride {} booked at {}",
                    ride.getId(), ride.getBookedAt());
        }
    }

    // Runs every day at midnight — log daily ride summary
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyRideSummary() {
        long totalRides = rideRepository.count();
        log.info("Daily Summary: Total rides in system: {}", totalRides);
    }
}