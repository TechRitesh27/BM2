package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.BillItem;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.service.GuestAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest/account")
public class GuestAccountController {

    private final GuestAccountService guestAccountService;

    public GuestAccountController(GuestAccountService guestAccountService) {
        this.guestAccountService = guestAccountService;
    }

    // -------- BOOKING HISTORY --------
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookingHistory(Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                guestAccountService.getBookingHistory(userEmail)
        );
    }

    // -------- CURRENT BILL --------
    @GetMapping("/bill")
    public ResponseEntity<Bill> getCurrentBill(Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                guestAccountService.getCurrentBill(userEmail)
        );
    }

    // -------- BILL ITEMS --------
    @GetMapping("/bill/{billId}/items")
    public ResponseEntity<List<BillItem>> getBillItems(
            @PathVariable Long billId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                guestAccountService.getBillItems(billId, userEmail)
        );
    }
}
