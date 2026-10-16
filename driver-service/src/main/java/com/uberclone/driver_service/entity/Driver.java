package com.uberclone.driver_service.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String phone;

    private String licenseNumber;

    private String vehicleType;        // "BIKE", "CAR", "AUTO"

    private String vehicleNumber;

    private boolean available;         // true = online, false = offline

    private double latitude;           // current location
    private double longitude;

    private double rating;             // average rating (default 5.0)

    private int totalRides;

    private LocalDateTime createdAt;
}