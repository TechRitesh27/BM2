package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Only authenticated guests can initiate payment
    @PostMapping("/create-order")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) throws Exception {

        Long bookingId = Long.valueOf(request.get("bookingId").toString());
        Double amount  = Double.valueOf(request.get("amount").toString());

        return ResponseEntity.ok(paymentService.createOrder(bookingId, amount));
    }

    // Verify is also authenticated — called right after Razorpay callback
    @PostMapping("/verify")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> request) throws Exception {

        boolean success = paymentService.verifyPayment(request);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Payment Successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Payment Failed"));
        }
    }
}
