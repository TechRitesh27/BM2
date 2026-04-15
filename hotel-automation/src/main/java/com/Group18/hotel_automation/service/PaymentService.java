package com.Group18.hotel_automation.service;

import com.razorpay.*;
import com.Group18.hotel_automation.entity.Payment;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.repository.PaymentRepository;
import com.Group18.hotel_automation.repository.BookingRepository;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    // CREATE ORDER
    public Map<String, Object> createOrder(Long bookingId, Double amount) throws Exception {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100);
        options.put("currency", "INR");
        options.put("receipt", "txn_" + bookingId);

        Order order = client.orders.create(options);

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setStatus("CREATED");
        payment.setPaymentMode("RAZORPAY");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("key", key);

        return response;
    }

    // VERIFY PAYMENT
    public boolean verifyPayment(Map<String, String> data) throws Exception {

        String orderId = data.get("razorpayOrderId");
        String paymentId = data.get("razorpayPaymentId");
        String signature = data.get("razorpaySignature");

        String payload = orderId + "|" + paymentId;

        boolean isValid = Utils.verifySignature(payload, signature, secret);

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (isValid) {

            payment.setStatus("SUCCESS");
            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);

            booking.setPaymentStatus("PAID");
            booking.setPaymentMode("RAZORPAY");

        } else {

            payment.setStatus("FAILED");
            booking.setPaymentStatus("FAILED");
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return isValid;
    }
}
