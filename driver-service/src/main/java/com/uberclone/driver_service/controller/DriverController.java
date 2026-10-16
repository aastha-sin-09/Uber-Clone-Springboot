package com.uberclone.driver_service.controller;

import com.uberclone.driver_service.dto.*;
import com.uberclone.driver_service.entity.Driver;
import com.uberclone.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Controller", description = "Driver management and availability")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    @Operation(summary = "Register new driver",
            description = "Creates driver account with vehicle details")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(driverService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Driver login",
            description = "Authenticates driver and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(driverService.login(request));
    }

    @PutMapping("/availability")
    @Operation(summary = "Toggle driver availability",
            description = "Set driver online or offline status")
    public ResponseEntity<Driver> updateAvailability(
            @RequestParam String driverId,
            @RequestParam boolean available) {
        return ResponseEntity.ok(driverService.updateAvailability(driverId, available));
    }

    @PutMapping("/location")
    @Operation(summary = "Update driver location",
            description = "Updates real-time GPS coordinates of driver")
    public ResponseEntity<Driver> updateLocation(
            @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(request));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available drivers",
            description = "Returns all drivers currently online — cached in Redis for 30s")
    public ResponseEntity<List<Driver>> getAvailableDrivers() {
        return ResponseEntity.ok(driverService.getAvailableDrivers());
    }

    @GetMapping("/profile/{driverId}")
    @Operation(summary = "Get driver profile")
    public ResponseEntity<Driver> getProfile(@PathVariable String driverId) {
        return ResponseEntity.ok(driverService.getProfile(driverId));
    }
}