package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRoomRequest {

    @NotBlank
    private String roomNumber;

    private Integer floor;

    @NotNull
    private Long roomTypeId;

    // getters & setters

    public @NotBlank String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(@NotBlank String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public @NotNull Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(@NotNull Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
