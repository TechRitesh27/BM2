package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.CreateServiceRequest;
import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.enums.*;
import com.Group18.hotel_automation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
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

    // ===============================
    // GUEST CREATE REQUEST
    // ===============================
    @Transactional
    public ServiceRequest createServiceRequest(String guestEmail, CreateServiceRequest request) {

        User user = userRepository.findByEmail(guestEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This booking does not belong to you");
        }

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Services can only be requested during active stay");
        }

        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid service type"));

        if (!serviceType.getActive()) {
            throw new RuntimeException("Service type is inactive");
        }

        ServiceRequest sr = new ServiceRequest();
        sr.setUser(user);
        sr.setBooking(booking);
        sr.setServiceType(serviceType);
        sr.setStatus(ServiceRequestStatus.PENDING);
        sr.setRequestedAt(LocalDateTime.now());

        return serviceRequestRepository.save(sr);
    }

    // ===============================
    // STAFF ASSIGN REQUEST
    // ===============================
    @Transactional
    public void assignServiceRequest(Long requestId, String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceRequest sr = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // 🔒 Department validation
        if (!sr.getServiceType().getStaffType()
                .getId()
                .equals(staff.getStaffType().getId())) {

            throw new RuntimeException("You cannot assign services outside your department");
        }

        if (sr.getStatus() != ServiceRequestStatus.PENDING) {
            throw new RuntimeException("Only PENDING requests can be assigned");
        }

        sr.setAssignedStaff(staff);
        sr.setStatus(ServiceRequestStatus.ASSIGNED);

        serviceRequestRepository.save(sr);
    }

    // ===============================
    // STAFF COMPLETE REQUEST
    // ===============================
    @Transactional
    public void completeServiceRequest(Long requestId, String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceRequest sr = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!sr.getServiceType().getStaffType()
                .getId()
                .equals(staff.getStaffType().getId())) {

            throw new RuntimeException("You cannot complete services outside your department");
        }

        if (sr.getStatus() != ServiceRequestStatus.ASSIGNED) {
            throw new RuntimeException("Only ASSIGNED requests can be completed");
        }

        if (sr.getAssignedStaff() == null ||
                !sr.getAssignedStaff().getEmail().equals(staffEmail)) {
            throw new RuntimeException("You are not assigned to this request");
        }

        sr.setStatus(ServiceRequestStatus.COMPLETED);
        sr.setCompletedAt(LocalDateTime.now());

        serviceRequestRepository.save(sr);

        // ===============================
        // AUTO ADD TO BILL
        // ===============================

        Bill bill = billRepository
                .findByUserAndStatus(sr.getUser(), BillStatus.OPEN)
                .orElseThrow(() -> new RuntimeException("Open bill not found"));

        if (bill.getStatus() == BillStatus.PAID) {
            throw new RuntimeException("Cannot add service to a closed bill");
        }

        BillItem item = new BillItem();
        item.setBill(bill);
        item.setDescription("Service: " + sr.getServiceType().getName());
        item.setAmount(sr.getServiceType().getPrice());
        item.setSourceType(BillItemSourceType.SERVICE_REQUEST);
        item.setSourceId(sr.getId());

        billItemRepository.save(item);

        bill.setTotalAmount(bill.getTotalAmount() + item.getAmount());
        billRepository.save(bill);
    }

    // ===============================
    // GUEST CANCEL REQUEST
    // ===============================
    @Transactional
    public void cancelServiceRequest(Long requestId, String guestEmail) {

        ServiceRequest sr = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!sr.getUser().getEmail().equals(guestEmail)) {
            throw new RuntimeException("You cannot cancel this request");
        }

        if (sr.getStatus() == ServiceRequestStatus.COMPLETED) {
            throw new RuntimeException("Completed request cannot be cancelled");
        }

        sr.setStatus(ServiceRequestStatus.CANCELLED);
        serviceRequestRepository.save(sr);
    }

    // ===============================
    // VIEW MY REQUESTS (GUEST)
    // ===============================
    public List<ServiceRequest> getMyRequests(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return serviceRequestRepository.findByUser(user);
    }

    // ===============================
    // STAFF VIEW PENDING
    // ===============================
    public List<ServiceRequest> getPendingRequestsForStaff(String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        StaffType staffType = staff.getStaffType();

        return serviceRequestRepository
                .findByStatusAndServiceType_StaffType(
                        ServiceRequestStatus.PENDING,
                        staffType
                );
    }

    @Transactional
    public void rejectServiceRequest(Long requestId,
                                     String staffEmail,
                                     String reason) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceRequest sr = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (sr.getStatus() == ServiceRequestStatus.COMPLETED) {
            throw new RuntimeException("Completed request cannot be rejected");
        }

        if (sr.getStatus() == ServiceRequestStatus.CANCELLED) {
            throw new RuntimeException("Cancelled request cannot be rejected");
        }

        sr.setAssignedStaff(staff);
        sr.setStatus(ServiceRequestStatus.REJECTED);
        sr.setRejectionReason(reason);
        sr.setCompletedAt(LocalDateTime.now());

        serviceRequestRepository.save(sr);
    }

    public List<ServiceRequest> getMyAssignedRequests(String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        return serviceRequestRepository.findByAssignedStaff(staff);
    }
}