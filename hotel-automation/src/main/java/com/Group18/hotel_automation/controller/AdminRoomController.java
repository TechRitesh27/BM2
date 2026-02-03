package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.dto.CreateRoomRequest;
import com.Group18.hotel_automation.entity.Room;
import com.Group18.hotel_automation.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final RoomService roomService;

    public AdminRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        return ResponseEntity.ok(roomService.getAllActiveRooms());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateRoom(@PathVariable Long id) {

        roomService.deactivateRoom(id);
        return ResponseEntity.ok("Room deactivated successfully");
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateRoom(@PathVariable Long id) {
        roomService.activateRoom(id);
        return ResponseEntity.ok("Room activated successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Room>> getAllRoomsIncludingInactive() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }


}
