package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.AdminDashboardResponse;
import com.Group18.hotel_automation.dto.MonthlyRevenueDTO;
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

    public AdminDashboardService(RoomRepository roomRepository,
                                 BookingRepository bookingRepository,
                                 BillRepository billRepository,
                                 UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.userRepository = userRepository;
    }

    public AdminDashboardResponse getDashboardStats() {

        AdminDashboardResponse response = new AdminDashboardResponse();

        // Rooms
        response.setTotalRooms(roomRepository.count());
        response.setAvailableRooms(
                roomRepository.countByStatus(RoomStatus.AVAILABLE)
        );
        response.setOccupiedRooms(
                roomRepository.countByStatus(RoomStatus.OCCUPIED)
        );

        // Bookings
        response.setTotalBookings(bookingRepository.count());
        response.setActiveBookings(
                bookingRepository.countByStatus(BookingStatus.CHECKED_IN)
        );

        // Revenue
        response.setTotalRevenue(
                billRepository.sumTotalRevenue()
        );

        LocalDate now = LocalDate.now();
        response.setMonthlyRevenue(
                billRepository.sumMonthlyRevenue(now.getMonthValue(), now.getYear())
        );

        // Users & Staff
        response.setTotalUsers(
                userRepository.countByRole("ROLE_GUEST")
        );

        response.setTotalStaff(
                userRepository.countByRole("ROLE_STAFF")
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

            response.add(new MonthlyRevenueDTO(monthName, revenue));
        }

        return response;
    }
}