package com.uberclone.user_service.controller;

import com.uberclone.user_service.dto.AuthResponse;
import com.uberclone.user_service.dto.LoginRequest;
import com.uberclone.user_service.dto.RegisterRequest;
import com.uberclone.user_service.entity.User;
import com.uberclone.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "User registration, login and profile")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Creates a new rider or driver account and returns JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user",
            description = "Authenticates user credentials and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile",
            description = "Returns user profile by ID")
    public ResponseEntity<User> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}