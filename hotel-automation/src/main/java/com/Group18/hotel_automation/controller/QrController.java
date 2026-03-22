package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.repository.BookingRepository;
import com.Group18.hotel_automation.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public QrController(BookingRepository bookingRepository,
                        BookingService bookingService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/checkin")
    public String checkInWithQr(@RequestParam String token) {

        Booking booking = bookingRepository
                .findByQrToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid QR"));

        if (booking.getQrUsed()) {
            throw new RuntimeException("QR already used");
        }

        bookingService.checkIn(booking.getId());

        booking.setQrUsed(true);
        bookingRepository.save(booking);

        return "Check-in successful";
    }

    @GetMapping("/checkout")
    public ResponseEntity<String> checkOut(@RequestParam String token) {

        Booking booking = bookingRepository.findByQrToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid QR"));

        bookingService.checkOut(booking.getId());

        return ResponseEntity.ok("Check-out successful");
    }


}
