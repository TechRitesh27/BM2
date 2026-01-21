package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRoomTypeRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double basePrice;

    @NotNull
    private Integer capacity;

    // getters & setters

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(@NotNull Double basePrice) {
        this.basePrice = basePrice;
    }

    public @NotNull Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(@NotNull Integer capacity) {
        this.capacity = capacity;
    }
}
