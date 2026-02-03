package com.Group18.hotel_automation.service;

//import com.Group18.hotel_automation.entity.Role;
import com.Group18.hotel_automation.enums.Role;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsersByRole(com.Group18.hotel_automation.enums.Role role) {
        return userRepository.findByRole_Name(role.name());
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }
}

