package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.ServiceRequest;
import com.Group18.hotel_automation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByUser(User user);

    List<ServiceRequest> findByStatus(String status);

    List<ServiceRequest> findByAssignedStaff(User staff);
}
