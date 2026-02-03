package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.Role;
import com.Group18.hotel_automation.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Get all staff
    @GetMapping("/staff")
    public ResponseEntity<List<User>> getStaff() {
        return ResponseEntity.ok(userService.getUsersByRole(Role.STAFF));
    }

    // Get all customers (GUEST)
    @GetMapping("/customers")
    public ResponseEntity<List<User>> getCustomers() {
        return ResponseEntity.ok(userService.getUsersByRole(Role.GUEST));
    }

    // Deactivate user
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated");
    }

    // Activate user
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated");
    }
}
