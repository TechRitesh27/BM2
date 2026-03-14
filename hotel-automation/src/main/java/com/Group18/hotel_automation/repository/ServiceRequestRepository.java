package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.ServiceRequest;
import com.Group18.hotel_automation.entity.StaffType;
import com.Group18.hotel_automation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Group18.hotel_automation.enums.ServiceRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByUser(User user);

    List<ServiceRequest> findByStatus(ServiceRequestStatus status);

    List<ServiceRequest> findByAssignedStaff(User staff);

    long countByStatus(ServiceRequestStatus status);

    long countByAssignedStaffAndStatus(User staff, ServiceRequestStatus status);

    long countByAssignedStaffAndStatusAndCompletedAtBetween(
            User staff,
            ServiceRequestStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    List<ServiceRequest> findByStatusAndServiceType_StaffType(
            ServiceRequestStatus status,
            StaffType staffType
    );

    List<ServiceRequest> findByAssignedStaffAndStatusIn(
            User staff,
            List<ServiceRequestStatus> statuses
    );

    long countByUserAndStatus(User user, ServiceRequestStatus status);
}
