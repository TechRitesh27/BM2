package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.AdminDashboardResponse;
import com.Group18.hotel_automation.dto.MonthlyRevenueDTO;
import com.Group18.hotel_automation.entity.Role;
import com.Group18.hotel_automation.enums.BookingStatus;
import com.Group18.hotel_automation.enums.RoomStatus;
import com.Group18.hotel_automation.repository.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminDashboardService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminDashboardService(RoomRepository roomRepository,
                                 BookingRepository bookingRepository,
                                 BillRepository billRepository,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository) {

        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public AdminDashboardResponse getDashboardStats() {

        AdminDashboardResponse response = new AdminDashboardResponse();

        // =========================
        // Rooms
        // =========================
        response.setTotalRooms(roomRepository.count());

        response.setAvailableRooms(
                roomRepository.countByStatus(RoomStatus.AVAILABLE)
        );

        response.setOccupiedRooms(
                roomRepository.countByStatus(RoomStatus.OCCUPIED)
        );

        // =========================
        // Bookings
        // =========================
        response.setTotalBookings(
                bookingRepository.count()
        );

        response.setActiveBookings(
                bookingRepository.countByStatus(BookingStatus.CHECKED_IN)
        );

        // =========================
        // Revenue
        // =========================
        response.setTotalRevenue(
                billRepository.sumTotalRevenue()
        );

        LocalDate now = LocalDate.now();

        response.setMonthlyRevenue(
                billRepository.sumMonthlyRevenue(
                        now.getMonthValue(),
                        now.getYear()
                )
        );

        // =========================
        // Users & Staff
        // =========================

        Role guestRole = roleRepository
                .findByName("GUEST")
                .orElseThrow(() ->
                        new RuntimeException("GUEST role not found"));

        Role staffRole = roleRepository
                .findByName("STAFF")
                .orElseThrow(() ->
                        new RuntimeException("STAFF role not found"));

        response.setTotalUsers(
                userRepository.countByRole(guestRole)
        );

        response.setTotalStaff(
                userRepository.countByRole(staffRole)
        );

        return response;
    }

    public List<MonthlyRevenueDTO> getLast12MonthsRevenue() {

        List<Object[]> results = billRepository.getMonthlyRevenueData();

        List<MonthlyRevenueDTO> response = new ArrayList<>();

        for (Object[] row : results) {

            int month = ((Number) row[0]).intValue();
            int year = ((Number) row[1]).intValue();
            double revenue = ((Number) row[2]).doubleValue();

            String monthName = Month.of(month).name() + " " + year;

            response.add(
                    new MonthlyRevenueDTO(monthName, revenue)
            );
        }

        return response;
    }
}