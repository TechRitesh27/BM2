package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestAccountService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public GuestAccountService(UserRepository userRepository,
                               BookingRepository bookingRepository,
                               BillRepository billRepository,
                               BillItemRepository billItemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
    }

    // -------- BOOKING HISTORY --------
    public List<Booking> getBookingHistory(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    // -------- CURRENT OPEN BILL --------
    public Bill getCurrentBill(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return billRepository.findByUserAndStatus(user, BillStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("No open bill found"));
    }

    // -------- BILL ITEMS --------
    public List<BillItem> getBillItems(Long billId, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!bill.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized bill access");
        }

        return billItemRepository.findByBillOrderByCreatedAtAsc(bill);
    }
}
