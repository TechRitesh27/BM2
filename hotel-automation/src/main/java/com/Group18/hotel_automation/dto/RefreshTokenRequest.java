package com.Group18.hotel_automation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public @NotBlank String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(@NotBlank String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
