package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookRoomRequest {

    @NotNull
    private Long roomTypeId;

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;

    private Boolean upgradeAccepted = false;

//    Getters and setters

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Boolean getUpgradeAccepted() {
        return upgradeAccepted;
    }

    public void setUpgradeAccepted(Boolean upgradeAccepted) {
        this.upgradeAccepted = upgradeAccepted;
    }
}