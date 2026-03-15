package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.RoomType;
import com.Group18.hotel_automation.service.RoomTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/guest/room-types")
public class GuestRoomTypeController {

    private final RoomTypeService roomTypeService;

    public GuestRoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public List<RoomType> getRoomTypes() {
        return roomTypeService.getAllActiveRoomTypes();
    }
}
