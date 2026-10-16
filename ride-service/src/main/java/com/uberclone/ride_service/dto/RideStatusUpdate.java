package com.uberclone.ride_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideStatusUpdate {
    private String rideId;
    private String status;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private String message;
}