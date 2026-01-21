package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffTypeRepository extends JpaRepository<StaffType, Long> {
    Optional<StaffType> findByName(String name);
}
