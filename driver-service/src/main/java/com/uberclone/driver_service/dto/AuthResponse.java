package com.uberclone.driver_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String driverId;
    private String name;
    private String vehicleType;
}