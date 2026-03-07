package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.StaffDashboardResponse;
import com.Group18.hotel_automation.entity.ServiceRequest;
import com.Group18.hotel_automation.service.ServiceRequestService;
import com.Group18.hotel_automation.service.StaffDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffServiceController {

    private final ServiceRequestService service;
    private final StaffDashboardService staffDashboardService;

    public StaffServiceController(ServiceRequestService service,
                                  StaffDashboardService staffDashboardService) {
        this.service = service;
        this.staffDashboardService = staffDashboardService;
    }

    // ===============================
    // 1️⃣ VIEW DEPARTMENT PENDING REQUESTS
    // ===============================
    @GetMapping("/services/pending")
    public ResponseEntity<List<ServiceRequest>> pending(Authentication auth) {
        return ResponseEntity.ok(
                service.getPendingRequestsForStaff(auth.getName())
        );
    }

    // ===============================
    // 2️⃣ VIEW MY ASSIGNED REQUESTS
    // ===============================
    @GetMapping("/services/my-assignments")
    public ResponseEntity<List<ServiceRequest>> myAssignments(Authentication auth) {
        return ResponseEntity.ok(
                service.getMyAssignedRequests(auth.getName())
        );
    }

    // ===============================
    // 3️⃣ ASSIGN REQUEST TO SELF
    // ===============================
    @PutMapping("/services/{id}/assign")
    public ResponseEntity<String> assign(@PathVariable Long id,
                                         Authentication auth) {

        service.assignServiceRequest(id, auth.getName());
        return ResponseEntity.ok("Service assigned successfully");
    }

    // ===============================
    // 4️⃣ COMPLETE ASSIGNED REQUEST
    // ===============================
    @PutMapping("/services/{id}/complete")
    public ResponseEntity<String> complete(@PathVariable Long id,
                                           Authentication auth) {

        service.completeServiceRequest(id, auth.getName());
        return ResponseEntity.ok("Service completed & billed");
    }

    // ===============================
    // 5️⃣ REJECT REQUEST
    // ===============================
    @PutMapping("/services/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable Long id,
                                         @RequestParam String reason,
                                         Authentication auth) {

        service.rejectServiceRequest(id, auth.getName(), reason);
        return ResponseEntity.ok("Service request rejected");
    }

    // ===============================
    // 6️⃣ STAFF DASHBOARD SUMMARY
    // ===============================
    @GetMapping("/dashboard")
    public ResponseEntity<StaffDashboardResponse> dashboard(Authentication auth) {
        return ResponseEntity.ok(
                staffDashboardService.getDashboard(auth.getName())
        );
    }

    // ===============================
// 7️⃣ SERVICE HISTORY
// ===============================
    @GetMapping("/services/history")
    public ResponseEntity<List<ServiceRequest>> history(Authentication auth) {

        return ResponseEntity.ok(
                service.getServiceHistory(auth.getName())
        );
    }
}