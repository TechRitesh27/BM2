package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.CreateRoomRequest;
import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.entity.RoomType;
import com.Group18.hotel_automation.enums.RoomStatus;
import com.Group18.hotel_automation.repository.RoomRepository;
import com.Group18.hotel_automation.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository,
                       RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @Transactional
    public Room createRoom(CreateRoomRequest request) {

        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RuntimeException("Room number already exists");
        }

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        if (!roomType.getActive()) {
            throw new RuntimeException("Cannot add room for inactive room type");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);

        return roomRepository.save(room);
    }

    public List<Room> getAllActiveRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::getActive)
                .toList();
    }

    @Transactional
    public void deactivateRoom(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setActive(false);
        roomRepository.save(room);
    }
}
