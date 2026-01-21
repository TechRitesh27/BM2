package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateServiceRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    private Long serviceTypeId;

    public @NotNull Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(@NotNull Long bookingId) {
        this.bookingId = bookingId;
    }

    public @NotNull Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(@NotNull Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }
}
