package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.service.HousekeepingService;
import com.Group18.hotel_automation.service.StaffSecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/housekeeping")
@PreAuthorize("hasRole('STAFF')")
public class HousekeepingController {

    private final HousekeepingService housekeepingService;
    private final StaffSecurityService staffSecurityService;

    public HousekeepingController(HousekeepingService housekeepingService,
                                  StaffSecurityService staffSecurityService) {
        this.housekeepingService = housekeepingService;
        this.staffSecurityService = staffSecurityService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getDirtyRooms(Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "HOUSEKEEPING"
        );

        return ResponseEntity.ok(housekeepingService.getDirtyRooms());
    }

    @PutMapping("/{roomId}/start-cleaning")
    public ResponseEntity<String> startCleaning(@PathVariable Long roomId,
                                                Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "HOUSEKEEPING"
        );

        housekeepingService.markCleaning(roomId);
        return ResponseEntity.ok("Cleaning started");
    }

    @PutMapping("/{roomId}/mark-clean")
    public ResponseEntity<String> markClean(@PathVariable Long roomId,
                                            Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "HOUSEKEEPING"
        );

        housekeepingService.markClean(roomId);
        return ResponseEntity.ok("Room marked clean and available");
    }
}