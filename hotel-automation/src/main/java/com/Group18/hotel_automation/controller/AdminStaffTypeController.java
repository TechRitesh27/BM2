package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.StaffType;
import com.Group18.hotel_automation.repository.StaffTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/staff-types")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStaffTypeController {

    private final StaffTypeRepository staffTypeRepository;

    public AdminStaffTypeController(StaffTypeRepository staffTypeRepository) {
        this.staffTypeRepository = staffTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<StaffType>> getAllStaffTypes() {
        return ResponseEntity.ok(staffTypeRepository.findAll());
    }

//
}

