package com.uberclone.ride_service.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    private String userId;          // who booked the ride

    private String driverId;        // assigned driver (null until accepted)

    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;

    private RideStatus status;      // current state of the ride

    private double fare;            // calculated fare

    private LocalDateTime bookedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private double surgeMultiplier;

    public enum RideStatus {
        REQUESTED,    // rider just booked
        ACCEPTED,     // driver accepted
        IN_PROGRESS,  // ride started
        COMPLETED,    // ride finished
        CANCELLED     // ride cancelled
    }
}