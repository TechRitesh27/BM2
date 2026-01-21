package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.CreateServiceRequest;
import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final UserRepository userRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 ServiceTypeRepository serviceTypeRepository,
                                 BookingRepository bookingRepository,
                                 BillRepository billRepository,
                                 BillItemRepository billItemRepository,
                                 UserRepository userRepository) {

        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.userRepository = userRepository;
    }

    // -------- GUEST CREATE REQUEST --------
    @Transactional
    public ServiceRequest createServiceRequest(String guestEmail, CreateServiceRequest request) {

        System.out.println("👉 Incoming bookingId = " + request.getBookingId());
        System.out.println("👉 Incoming serviceTypeId = " + request.getServiceTypeId());

        User user = userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This booking does not belong to you");
        }

        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid service type"));

        ServiceRequest sr = new ServiceRequest();
        sr.setUser(user);
        sr.setBooking(booking);
        sr.setServiceType(serviceType);
        sr.setStatus("PENDING");
        sr.setRequestedAt(LocalDateTime.now());

        return serviceRequestRepository.save(sr);
    }

    // -------- STAFF COMPLETE REQUEST --------
    @Transactional
    public void completeServiceRequest(Long requestId, String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceRequest sr = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        sr.setAssignedStaff(staff);
        sr.setStatus("COMPLETED");
        sr.setCompletedAt(LocalDateTime.now());

        serviceRequestRepository.save(sr);

        // 🔥 AUTO-ADD TO BILL
        Bill bill = billRepository.findByBooking(sr.getBooking())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        BillItem item = new BillItem();
        item.setBill(bill);
        item.setDescription("Service: " + sr.getServiceType().getName());
        item.setAmount(sr.getServiceType().getPrice());

        billItemRepository.save(item);

        bill.setTotalAmount(bill.getTotalAmount() + item.getAmount());
        billRepository.save(bill);
    }

    // -------- VIEW MY REQUESTS (GUEST) --------
    public List<ServiceRequest> getMyRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return serviceRequestRepository.findByUser(user);
    }

    // -------- STAFF VIEW PENDING --------
    public List<ServiceRequest> getPendingRequests() {
        return serviceRequestRepository.findByStatus("PENDING");
    }
}
