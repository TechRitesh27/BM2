package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.CreateStaffRequest;
import com.Group18.hotel_automation.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStaffController {

    private final AuthService authService;

    public AdminStaffController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<String> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        authService.createStaff(request);
        return ResponseEntity.ok("Staff created successfully");
    }
}

