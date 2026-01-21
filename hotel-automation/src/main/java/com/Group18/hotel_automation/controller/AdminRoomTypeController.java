package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.CreateRoomTypeRequest;
import com.Group18.hotel_automation.entity.RoomType;
import com.Group18.hotel_automation.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/room-types")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    public AdminRoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping
    public ResponseEntity<RoomType> createRoomType(
            @Valid @RequestBody CreateRoomTypeRequest request) {

        return ResponseEntity.ok(roomTypeService.createRoomType(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {

        return ResponseEntity.ok(roomTypeService.getAllActiveRoomTypes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateRoomType(@PathVariable Long id) {

        roomTypeService.deactivateRoomType(id);
        return ResponseEntity.ok("Room type deactivated successfully");
    }
}
