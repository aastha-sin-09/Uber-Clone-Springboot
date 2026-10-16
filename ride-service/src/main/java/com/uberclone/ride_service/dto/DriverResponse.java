package com.uberclone.ride_service.dto;

import lombok.Data;

@Data
public class DriverResponse {
    private String id;
    private String name;
    private String vehicleType;
    private String vehicleNumber;
    private double latitude;
    private double longitude;
    private boolean available;
    private double rating;
}