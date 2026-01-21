package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.BookRoomRequest;
import com.Group18.hotel_automation.dto.RoomAvailabilityRequest;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest/rooms")
public class GuestRoomController {

    private final BookingService bookingService;

    public GuestRoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // -------- SEARCH AVAILABLE ROOMS --------
    @PostMapping("/available")
    public ResponseEntity<List<Room>> findAvailableRooms(
            @Valid @RequestBody RoomAvailabilityRequest request) {

        return ResponseEntity.ok(
                bookingService.findAvailableRooms(request)
        );
    }

    // -------- BOOK ROOM --------
    @PostMapping("/book")
    public ResponseEntity<Booking> bookRoom(
            @Valid @RequestBody BookRoomRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                bookingService.bookRoom(request, userEmail)
        );
    }
}
