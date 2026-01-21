package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.BookRoomRequest;
import com.Group18.hotel_automation.dto.RoomAvailabilityRequest;
import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.enums.BillItemSourceType;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.enums.BookingStatus;
import com.Group18.hotel_automation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final UserRepository userRepository;

    public BookingService(RoomRepository roomRepository,
                          BookingRepository bookingRepository,
                          BillRepository billRepository,
                          BillItemRepository billItemRepository,
                          UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.userRepository = userRepository;
    }

    // -------- SEARCH AVAILABLE ROOMS --------
    public List<Room> findAvailableRooms(RoomAvailabilityRequest request) {

        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        List<Room> rooms = roomRepository.findAll()
                .stream()
                .filter(Room::getActive)
                .toList();

        return rooms.stream()
                .filter(room -> isRoomAvailable(room,
                        request.getCheckIn(),
                        request.getCheckOut()))
                .toList();
    }

    private boolean isRoomAvailable(Room room,
                                    LocalDate checkIn,
                                    LocalDate checkOut) {

        List<Booking> overlappingBookings =
                bookingRepository.findByRoomAndStatusAndCheckOutAfterAndCheckInBefore(
                        room,
                        BookingStatus.BOOKED,
                        checkIn,
                        checkOut
                );

        return overlappingBookings.isEmpty();
    }

    // -------- BOOK ROOM (WITH BILLING HOOK) --------
    @Transactional
    public Booking bookRoom(BookRoomRequest request, String userEmail) {

        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new RuntimeException("Invalid date range");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getActive()) {
            throw new RuntimeException("Room is not active");
        }

        boolean available = isRoomAvailable(
                room,
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (!available) {
            throw new RuntimeException("Room is not available for selected dates");
        }

        long nights = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        double amount = nights * room.getRoomType().getBasePrice();

        // 1️⃣ Create Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setTotalAmount(amount);
        booking.setStatus(BookingStatus.BOOKED);

        bookingRepository.save(booking);

        // 2️⃣ Get or Create OPEN Bill
        Bill bill = billRepository
                .findByUserAndStatus(user, BillStatus.OPEN)
                .orElseGet(() -> {
                    Bill newBill = new Bill();
                    newBill.setUser(user);
                    newBill.setStatus(BillStatus.OPEN);
                    newBill.setTotalAmount(0.0);
                    return billRepository.save(newBill);
                });

        // 3️⃣ Create BillItem
        BillItem billItem = new BillItem();
        billItem.setBill(bill);
        billItem.setDescription(
                "Room booking – " + room.getRoomNumber() +
                        " (" + nights + " nights)"
        );
        billItem.setAmount(amount);
        billItem.setSourceType(BillItemSourceType.ROOM_BOOKING);
        billItem.setSourceId(booking.getId());

        billItemRepository.save(billItem);

        // 4️⃣ Update Bill Total
        bill.setTotalAmount(bill.getTotalAmount() + amount);
        billRepository.save(bill);

        return booking;
    }
}
