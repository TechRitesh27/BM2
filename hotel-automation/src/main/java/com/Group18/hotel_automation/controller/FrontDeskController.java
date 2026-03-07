package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.service.BookingService;
import com.Group18.hotel_automation.service.StaffSecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/frontdesk")
@PreAuthorize("hasRole('STAFF')")
public class FrontDeskController {

    private final BookingService bookingService;
    private final StaffSecurityService staffSecurityService;

    public FrontDeskController(BookingService bookingService,
                               StaffSecurityService staffSecurityService) {
        this.bookingService = bookingService;
        this.staffSecurityService = staffSecurityService;
    }

    // VIEW ALL BOOKINGS
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings(Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "FRONT_DESK"
        );

        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // CHECK-IN
    @PutMapping("/bookings/{id}/check-in")
    public ResponseEntity<String> checkIn(@PathVariable Long id,
                                          Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "FRONT_DESK"
        );

        bookingService.checkIn(id);
        return ResponseEntity.ok("Guest checked in successfully");
    }

    // CHECK-OUT
    @PutMapping("/bookings/{id}/check-out")
    public ResponseEntity<String> checkOut(@PathVariable Long id,
                                           Authentication auth) {

        staffSecurityService.validateDepartmentAccess(
                auth.getName(),
                "FRONT_DESK"
        );

        bookingService.checkOut(id);
        return ResponseEntity.ok("Guest checked out successfully");
    }
}