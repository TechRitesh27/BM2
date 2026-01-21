package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomAndStatusAndCheckOutAfterAndCheckInBefore(
            Room room,
            BookingStatus status,
            LocalDate checkIn,
            LocalDate checkOut
    );

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

}
