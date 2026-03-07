package com.Group18.hotel_automation.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String role;
    private String staffType; // ✅ NEW

    public LoginResponse(String accessToken,
                         String refreshToken,
                         String role,
                         String staffType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.staffType = staffType;
    }

    // getters
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getRole() { return role; }
    public String getStaffType() { return staffType; }
}