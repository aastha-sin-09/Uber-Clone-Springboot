package com.uberclone.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRideRequest {

    @NotBlank
    private String userId;

    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}