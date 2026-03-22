package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.BookRoomRequest;
import com.Group18.hotel_automation.dto.RoomAvailabilityRequest;
import com.Group18.hotel_automation.dto.RoomTypeAvailabilityResponse;
import com.Group18.hotel_automation.dto.UpgradeSuggestionResponse;
import com.Group18.hotel_automation.entity.*;
import com.Group18.hotel_automation.enums.BillItemSourceType;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.enums.BookingStatus;
import com.Group18.hotel_automation.enums.RoomStatus;
import com.Group18.hotel_automation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;

    public BookingService(RoomRepository roomRepository,
                          BookingRepository bookingRepository,
                          BillRepository billRepository,
                          BillItemRepository billItemRepository,
                          UserRepository userRepository,
                          RoomTypeRepository roomTypeRepository) {

        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
        this.userRepository = userRepository;
        this.roomTypeRepository = roomTypeRepository;
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

        if (!room.getActive()) return false;

        if (room.getStatus() != RoomStatus.AVAILABLE) return false;

        List<Booking> overlappingBookings =
                bookingRepository
                        .findByRoomAndStatusInAndCheckOutAfterAndCheckInBefore(
                                room,
                                List.of(
                                        BookingStatus.BOOKED,
                                        BookingStatus.CHECKED_IN
                                ),
                                checkIn,
                                checkOut
                        );

        return overlappingBookings.isEmpty();
    }

    // -------- BOOK ROOM (WITH BILLING HOOK) --------
    @Transactional
    public Booking bookRoom(BookRoomRequest request, String userEmail) {

        // 1️⃣ Validate dates
        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new RuntimeException("Invalid date range");
        }

        // 2️⃣ Get user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3️⃣ Prevent duplicate active booking
        boolean hasActiveBooking = bookingRepository
                .existsByUserAndStatusIn(
                        user,
                        List.of(BookingStatus.BOOKED, BookingStatus.CHECKED_IN)
                );

        if (hasActiveBooking) {
            throw new RuntimeException("You already have an active booking");
        }

        // 4️⃣ Get room type
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        // 5️⃣ Allocate room (SMART)
        Room room = allocateRoom(
                roomType.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        // 6️⃣ Validate availability again (double safety)
        if (!isRoomAvailable(room, request.getCheckIn(), request.getCheckOut())) {
            throw new RuntimeException("Room not available");
        }

        // 7️⃣ Nights calculation (IMPORTANT FIX)
        long nights = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (nights <= 0) {
            throw new RuntimeException("Invalid stay duration");
        }

        // 8️⃣ Dynamic pricing
        double pricePerNight = calculateDynamicPrice(roomType);
        double totalAmount = pricePerNight * nights;

        // 9️⃣ Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.BOOKED);

        // 🔥 QR Token
        booking.setQrToken(UUID.randomUUID().toString());
        booking.setQrUsed(false);

        bookingRepository.save(booking);

        // 🔟 Billing
        Bill bill = billRepository
                .findByUserAndStatus(user, BillStatus.OPEN)
                .orElseGet(() -> {
                    Bill newBill = new Bill();
                    newBill.setUser(user);
                    newBill.setBooking(booking);
                    newBill.setStatus(BillStatus.OPEN);
                    newBill.setTotalAmount(0.0);
                    return billRepository.save(newBill);
                });

        BillItem item = new BillItem();
        item.setBill(bill);
        item.setDescription(
                "Room " + room.getRoomNumber() +
                        " (" + nights + " nights @ ₹" + pricePerNight + ")"
        );
        item.setAmount(totalAmount);
        item.setSourceType(BillItemSourceType.ROOM_BOOKING);
        item.setSourceId(booking.getId());

        billItemRepository.save(item);

        bill.setTotalAmount(bill.getTotalAmount() + totalAmount);
        billRepository.save(bill);

        return booking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkOut(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Only CHECKED_IN bookings can be checked out");
        }

        LocalDate today = LocalDate.now();

        if (today.isBefore(booking.getCheckIn())) {
            throw new RuntimeException("Cannot check-out before check-in date");
        }

        if (today.isAfter(booking.getCheckOut())) {
            throw new RuntimeException("Booking stay period already ended");
        }

        booking.setStatus(BookingStatus.COMPLETED);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.DIRTY);
        roomRepository.save(room);

        // 🔥 Close bill automatically
        User user = booking.getUser();

        Bill bill = billRepository
                .findByUserAndStatus(user, BillStatus.OPEN)
                .orElse(null);

        if (bill != null) {
            bill.setStatus(BillStatus.PAID);
            billRepository.save(bill);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking checkIn(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Only BOOKED bookings allowed");
        }

        LocalDate today = LocalDate.now();

        // ❌ Too early
        if (today.isBefore(booking.getCheckIn())) {
            throw new RuntimeException("Too early to check-in");
        }

        // ⚠ Late arrival (industry behavior)
        if (today.isAfter(booking.getCheckIn())) {
            System.out.println("Late check-in for booking " + booking.getId());
        }

        // ❌ Expired
        if (!today.isBefore(booking.getCheckOut())) {
            throw new RuntimeException("Booking expired");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);

        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

//    Smart Room Allocation Method
    private Room allocateRoom(Long roomTypeId,
                              LocalDate checkIn,
                              LocalDate checkOut) {

        List<Room> candidateRooms =
                roomRepository.findByRoomTypeIdAndActiveTrue(roomTypeId);

        List<Room> availableRooms = candidateRooms.stream()
                .filter(room -> isRoomAvailable(room, checkIn, checkOut))
                .toList();

        if (availableRooms.isEmpty()) {
            throw new RuntimeException("No available room for selected type");
        }

        // Simple smart allocation rule
        return availableRooms.stream()
                .sorted((r1, r2) -> r1.getFloor().compareTo(r2.getFloor()))
                .findFirst()
                .orElseThrow();
    }

//    Room Upgrade Suggestion
    public UpgradeSuggestionResponse checkUpgradeSuggestion(Long selectedRoomTypeId,
                                                            LocalDate checkIn,
                                                            LocalDate checkOut) {

        RoomType selectedType = roomTypeRepository.findById(selectedRoomTypeId)
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        List<RoomType> higherTypes =
                roomTypeRepository.findByPriorityGreaterThanOrderByPriorityAsc(
                        selectedType.getPriority()
                );

        for (RoomType higherType : higherTypes) {

            List<Room> rooms =
                    roomRepository.findByRoomTypeIdAndActiveTrue(higherType.getId());

            long availableCount = rooms.stream()
                    .filter(room -> isRoomAvailable(room, checkIn, checkOut))
                    .count();

            if (availableCount > 2) {

                UpgradeSuggestionResponse response = new UpgradeSuggestionResponse();
                response.setAvailable(true);
                response.setRoomTypeId(higherType.getId());
                response.setRoomTypeName(higherType.getName());

                double difference =
                        higherType.getBasePrice() - selectedType.getBasePrice();

                response.setPriceDifference(difference);

                return response;
            }
        }

        UpgradeSuggestionResponse response = new UpgradeSuggestionResponse();
        response.setAvailable(false);
        return response;
    }

    public List<RoomTypeAvailabilityResponse> findAvailableRoomTypes(RoomAvailabilityRequest request) {

        if (request.getCheckOut().isBefore(request.getCheckIn()) ||
                request.getCheckOut().isEqual(request.getCheckIn())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        List<RoomType> roomTypes = roomTypeRepository.findAll()
                .stream()
                .filter(RoomType::getActive)
                .toList();

        return roomTypes.stream().map(type -> {

            List<Room> rooms = roomRepository
                    .findByRoomTypeIdAndActiveTrue(type.getId());

            long availableCount = rooms.stream()
                    .filter(room -> isRoomAvailable(
                            room,
                            request.getCheckIn(),
                            request.getCheckOut()))
                    .count();

            if (availableCount == 0) return null;

            RoomTypeAvailabilityResponse response =
                    new RoomTypeAvailabilityResponse();

            response.setRoomTypeId(type.getId());
            response.setName(type.getName());
            response.setDescription(type.getDescription());
            double dynamicPrice = calculateDynamicPrice(type);
            response.setPrice(dynamicPrice);
            response.setCapacity(type.getCapacity());
            response.setBedType(type.getBedType());
            response.setRoomSize(type.getRoomSize());
            response.setAmenities(type.getAmenities());
            response.setImageUrl(type.getImageUrl());
            response.setAvailableRooms(availableCount);

            return response;

        }).filter(r -> r != null).toList();
    }

    private double calculateDynamicPrice(RoomType roomType) {

        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        long totalRooms = roomRepository.count();

        double occupancyRate = (double) occupiedRooms / totalRooms;

        double price = roomType.getBasePrice();

        if (occupancyRate > 0.7) {
            price *= 1.2;
        }
        else if (occupancyRate < 0.4) {
            price *= 0.9;
        }

        return price;
    }
}
