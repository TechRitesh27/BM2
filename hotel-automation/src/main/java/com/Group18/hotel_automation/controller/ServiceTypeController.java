package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.ServiceType;
import com.Group18.hotel_automation.repository.ServiceTypeRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-types")
public class ServiceTypeController {

    private final ServiceTypeRepository repository;

    public ServiceTypeController(ServiceTypeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServiceType> getActiveServiceTypes() {
        return repository.findByActiveTrue();
    }
}