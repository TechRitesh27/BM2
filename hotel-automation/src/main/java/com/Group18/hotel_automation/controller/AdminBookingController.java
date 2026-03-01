package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.enums.BookingStatus;
import com.Group18.hotel_automation.service.AuditService;
import com.Group18.hotel_automation.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;

    private final AuditService auditService;

    // ✅ REQUIRED constructor for final field injection
    public AdminBookingController(BookingService bookingService, AuditService auditService) {

        this.bookingService = bookingService;
        this.auditService = auditService;

    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {

        return ResponseEntity.ok(
                bookingService.updateBookingStatus(id, status)
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<Booking> checkIn(
            @PathVariable Long id,
            Authentication authentication) {

        Booking booking = bookingService.checkIn(id);

        auditService.log(
                authentication.getName(),  // admin email
                "CHECKED_IN",
                "BOOKING",
                id
        );

        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<Booking> checkOut(
            @PathVariable Long id,
            Authentication authentication) {

        Booking booking = bookingService.checkOut(id);

        auditService.log(
                authentication.getName(),
                "CHECKED_OUT",
                "BOOKING",
                id
        );

        return ResponseEntity.ok(booking);
    }
}
