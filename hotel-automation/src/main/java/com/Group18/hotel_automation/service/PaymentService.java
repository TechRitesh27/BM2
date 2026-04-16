package com.Group18.hotel_automation.service;

import com.razorpay.*;
import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.entity.Payment;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.repository.BillRepository;
import com.Group18.hotel_automation.repository.BookingRepository;
import com.Group18.hotel_automation.repository.PaymentRepository;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BillRepository billRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          BillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
    }

    // -------- CREATE ORDER --------
    public Map<String, Object> createOrder(Long bookingId, Double amount) throws Exception {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        RazorpayClient client = new RazorpayClient(key, secret);

        JSONObject options = new JSONObject();
        options.put("amount", (long)(amount * 100)); // paise, must be integer
        options.put("currency", "INR");
        options.put("receipt", "txn_" + bookingId);

        Order order = client.orders.create(options);

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setStatus("CREATED");
        payment.setPaymentMode("RAZORPAY");

        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("key", key);

        return response;
    }

    // -------- VERIFY PAYMENT --------
    @Transactional
    public boolean verifyPayment(Map<String, String> data) throws Exception {

        String orderId   = data.get("razorpayOrderId");
        String paymentId = data.get("razorpayPaymentId");
        String signature = data.get("razorpaySignature");

        String payload = orderId + "|" + paymentId;
        boolean isValid = Utils.verifySignature(payload, signature, secret);

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (isValid) {
            // Update payment record
            payment.setStatus("SUCCESS");
            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);

            // Update booking payment status
            booking.setPaymentStatus("PAID");
            booking.setPaymentMode("RAZORPAY");

            // Close the associated bill
            billRepository.findByBooking(booking).ifPresent(bill -> {
                bill.setStatus(BillStatus.PAID);
                billRepository.save(bill);
            });

        } else {
            payment.setStatus("FAILED");
            booking.setPaymentStatus("FAILED");
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return isValid;
    }
}
