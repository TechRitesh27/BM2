package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.ServiceRequest;
import com.Group18.hotel_automation.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/services")
@RequiredArgsConstructor
public class StaffServiceController {

    private final ServiceRequestService service;

    public StaffServiceController(ServiceRequestService service) {
        this.service = service;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ServiceRequest>> pending() {
        return ResponseEntity.ok(service.getPendingRequests());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id, Authentication auth) {
        service.completeServiceRequest(id, auth.getName());
        return ResponseEntity.ok("Service completed & billed");
    }
}
