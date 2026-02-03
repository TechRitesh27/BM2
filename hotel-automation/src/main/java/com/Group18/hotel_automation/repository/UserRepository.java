package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Role;
import com.Group18.hotel_automation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole_Name(String name);

}
