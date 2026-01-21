package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.dto.CreateRoomTypeRequest;
import com.Group18.hotel_automation.entity.RoomType;
import com.Group18.hotel_automation.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Transactional
    public RoomType createRoomType(CreateRoomTypeRequest request) {

        if (roomTypeRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Room type already exists");
        }

        RoomType roomType = new RoomType();
        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setCapacity(request.getCapacity());
        roomType.setActive(true);

        return roomTypeRepository.save(roomType);
    }

    public List<RoomType> getAllActiveRoomTypes() {
        return roomTypeRepository.findAll()
                .stream()
                .filter(RoomType::getActive)
                .toList();
    }

    @Transactional
    public void deactivateRoomType(Long id) {

        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        roomType.setActive(false);
        roomTypeRepository.save(roomType);
    }
}
