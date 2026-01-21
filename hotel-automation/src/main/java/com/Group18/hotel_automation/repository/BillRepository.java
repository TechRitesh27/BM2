package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByUserAndStatus(User user, BillStatus status);

    List<Bill> findByUserOrderByCreatedAtDesc(User user);

    Optional<Bill> findByBooking(Booking booking);

}
