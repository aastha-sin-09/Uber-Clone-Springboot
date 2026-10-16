package com.uberclone.driver_service.service;

import com.uberclone.driver_service.config.JwtConfig;
import com.uberclone.driver_service.dto.*;
import com.uberclone.driver_service.entity.Driver;
import com.uberclone.driver_service.exception.DriverAlreadyExistsException;
import com.uberclone.driver_service.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final DriverRepository driverRepository;
    private final JwtConfig jwtConfig;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new DriverAlreadyExistsException("Driver already exists: " + request.getEmail());
        }

        Driver driver = Driver.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber())
                .available(false)          // offline by default when registered
                .rating(5.0)              // starts with perfect rating
                .totalRides(0)
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .build();

        Driver savedDriver = driverRepository.save(driver);
        String token = jwtConfig.generateToken(
                savedDriver.getId(),
                savedDriver.getEmail(),
                savedDriver.getVehicleType()
        );

        return new AuthResponse(token, savedDriver.getId(),
                savedDriver.getName(), savedDriver.getVehicleType());
    }

    public AuthResponse login(LoginRequest request) {
        Driver driver = driverRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtConfig.generateToken(
                driver.getId(),
                driver.getEmail(),
                driver.getVehicleType()
        );

        return new AuthResponse(token, driver.getId(),
                driver.getName(), driver.getVehicleType());
    }

    @CacheEvict(value = "availableDrivers", allEntries = true)
    public Driver updateAvailability(String driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setAvailable(available);
        return driverRepository.save(driver);
    }

    @CacheEvict(value = "availableDrivers", allEntries = true)
    public Driver updateLocation(LocationUpdateRequest request) {
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        return driverRepository.save(driver);
    }

    @Cacheable(value = "availableDrivers")
    public List<Driver> getAvailableDrivers() {
        log.info("Fetching available drivers from MongoDB");
        return driverRepository.findByAvailableTrue();
    }

    public Driver getProfile(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }
}