package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.CreateServiceRequest;
import com.Group18.hotel_automation.entity.ServiceRequest;
import com.Group18.hotel_automation.service.ServiceRequestService;
//import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/guest/services")
//@RequiredArgsConstructor
public class GuestServiceController {

    private final ServiceRequestService service;

    public GuestServiceController(ServiceRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ServiceRequest> create(
            @Valid @RequestBody CreateServiceRequest request,
            Authentication auth) {

        return ResponseEntity.ok(
                service.createServiceRequest(auth.getName(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequest>> myRequests(Authentication auth) {
        return ResponseEntity.ok(service.getMyRequests(auth.getName()));
    }
}
