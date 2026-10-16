package com.uberclone.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideCompletedEvent {
    private String rideId;
    private String userId;
    private String driverId;
    private double fare;
    private String pickupAddress;
    private String dropAddress;
}