package com.uberclone.ride_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRideResponse {
    private String rideId;
    private String driverId;
    private double baseFare;
    private double surgeMultiplier;
    private double finalFare;
    private String status;
    private String message;
}