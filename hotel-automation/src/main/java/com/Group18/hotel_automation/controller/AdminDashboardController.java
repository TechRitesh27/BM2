package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.AdminDashboardResponse;
import com.Group18.hotel_automation.dto.MonthlyRevenueDTO;
import com.Group18.hotel_automation.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/revenue-trend")
    public ResponseEntity<List<MonthlyRevenueDTO>> getRevenueTrend() {
        return ResponseEntity.ok(dashboardService.getLast12MonthsRevenue());
    }
}
