package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RoomAvailabilityRequest {

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;

    // getters & setters

    public @NotNull LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(@NotNull LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public @NotNull LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(@NotNull LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}
