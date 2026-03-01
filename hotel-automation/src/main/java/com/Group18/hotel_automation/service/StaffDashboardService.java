package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.StaffDashboardResponse;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.ServiceRequestStatus;
import com.Group18.hotel_automation.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StaffDashboardService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final BillItemRepository billItemRepository;
    private final UserRepository userRepository;

    public StaffDashboardService(ServiceRequestRepository serviceRequestRepository,
                                 BillItemRepository billItemRepository,
                                 UserRepository userRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.billItemRepository = billItemRepository;
        this.userRepository = userRepository;
    }

    public StaffDashboardResponse getDashboard(String staffEmail) {

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        StaffDashboardResponse response = new StaffDashboardResponse();

        response.setPendingRequests(
                serviceRequestRepository.countByStatus(ServiceRequestStatus.PENDING)
        );

        response.setMyAssignedRequests(
                serviceRequestRepository.countByAssignedStaffAndStatus(
                        staff, ServiceRequestStatus.ASSIGNED)
        );

        response.setDepartmentName(
                staff.getStaffType().getName()
        );

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        response.setCompletedToday(
                serviceRequestRepository
                        .countByAssignedStaffAndStatusAndCompletedAtBetween(
                                staff,
                                ServiceRequestStatus.COMPLETED,
                                startOfDay,
                                endOfDay
                        )
        );

        response.setServiceRevenueGenerated(
                billItemRepository.getServiceRevenueByStaff(staff)
        );

        // Average completion time calculation skipped for simplicity (can add later)
        response.setAverageCompletionMinutes(0);

        return response;
    }
}