package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.GuestDashboardResponse;
import com.Group18.hotel_automation.service.GuestDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")
@PreAuthorize("hasRole('GUEST')")
public class GuestDashboardController {

    private final GuestDashboardService service;

    public GuestDashboardController(GuestDashboardService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<GuestDashboardResponse> dashboard(
            Authentication auth
    ) {

        return ResponseEntity.ok(
                service.getDashboard(auth.getName())
        );
    }
}