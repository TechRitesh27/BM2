package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.GuestDashboardResponse;
import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.enums.BookingStatus;
import com.Group18.hotel_automation.enums.ServiceRequestStatus;
import com.Group18.hotel_automation.repository.BillRepository;
import com.Group18.hotel_automation.repository.BookingRepository;
import com.Group18.hotel_automation.repository.ServiceRequestRepository;
import com.Group18.hotel_automation.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GuestDashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final BillRepository billRepository;

    public GuestDashboardService(UserRepository userRepository,
                                 BookingRepository bookingRepository,
                                 ServiceRequestRepository serviceRequestRepository,
                                 BillRepository billRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.billRepository = billRepository;
    }

    public GuestDashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalBookings =
                bookingRepository.countByUser(user);

        long activeBooking =
                bookingRepository.countByUserAndStatus(
                        user,
                        BookingStatus.CHECKED_IN
                );

        long pendingServices =
                serviceRequestRepository
                        .countByUserAndStatus(
                                user,
                                ServiceRequestStatus.PENDING
                        );

        Bill bill =
                billRepository
                        .findByUserAndStatus(user, BillStatus.OPEN)
                        .orElse(null);

        double billAmount =
                bill != null ? bill.getTotalAmount() : 0;

        return new GuestDashboardResponse(
                activeBooking,
                totalBookings,
                pendingServices,
                billAmount
        );
    }
}