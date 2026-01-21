package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    Optional<RoomType> findByName(String name);
}
