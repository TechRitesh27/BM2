package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.*;
import com.Group18.hotel_automation.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // -------- REGISTER (GUEST) --------
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity.ok("Guest registered successfully");
    }

    // -------- REFRESH TOKEN --------
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // -------- CREATE STAFF (ADMIN) --------
    @PostMapping("/create-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        authService.createStaff(request);
        return ResponseEntity.ok("Staff created successfully");
    }
}
