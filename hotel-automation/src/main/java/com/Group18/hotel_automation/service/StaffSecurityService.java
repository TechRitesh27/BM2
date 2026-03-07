package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StaffSecurityService {

    private final UserRepository userRepository;

    public StaffSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User validateDepartmentAccess(String email, String requiredDepartment) {

        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (staff.getStaffType() == null) {
            throw new RuntimeException("Staff type not assigned");
        }

        if (!staff.getStaffType().getName().equals(requiredDepartment)) {
            throw new RuntimeException("You are not authorized for this department");
        }

        return staff;
    }
}