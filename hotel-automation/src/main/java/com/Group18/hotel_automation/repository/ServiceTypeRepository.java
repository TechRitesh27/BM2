package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    Optional<ServiceType> findByName(String name);
}
