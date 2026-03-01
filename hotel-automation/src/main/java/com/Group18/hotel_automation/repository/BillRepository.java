package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.Booking;
import com.Group18.hotel_automation.entity.User;
import com.Group18.hotel_automation.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByUserAndStatus(User user, BillStatus status);

    List<Bill> findByUserOrderByCreatedAtDesc(User user);

    Optional<Bill> findByBooking(Booking booking);

    @Query("SELECT COALESCE(SUM(b.totalAmount),0) FROM Bill b WHERE b.status='PAID'")
    double sumTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.totalAmount),0) FROM Bill b WHERE b.status='PAID' AND MONTH(b.createdAt)=:month AND YEAR(b.createdAt)=:year")
    double sumMonthlyRevenue(int month, int year);

    @Query("""
       SELECT MONTH(b.createdAt), YEAR(b.createdAt), SUM(b.totalAmount)
       FROM Bill b
       WHERE b.status = 'PAID'
       GROUP BY YEAR(b.createdAt), MONTH(b.createdAt)
       ORDER BY YEAR(b.createdAt), MONTH(b.createdAt)
       """)
    List<Object[]> getMonthlyRevenueData();

}
