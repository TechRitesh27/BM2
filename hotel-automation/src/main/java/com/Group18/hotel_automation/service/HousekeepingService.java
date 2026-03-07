package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.enums.RoomStatus;
import com.Group18.hotel_automation.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HousekeepingService {

    private final RoomRepository roomRepository;

    public HousekeepingService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getDirtyRooms() {
        return roomRepository.findByStatus(RoomStatus.DIRTY);
    }

    public void markCleaning(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getStatus() != RoomStatus.DIRTY) {
            throw new RuntimeException("Only DIRTY rooms can be cleaned");
        }

        room.setStatus(RoomStatus.CLEANING);
        roomRepository.save(room);
    }

    public void markClean(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getStatus() != RoomStatus.CLEANING) {
            throw new RuntimeException("Room must be in CLEANING state");
        }

        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }
}