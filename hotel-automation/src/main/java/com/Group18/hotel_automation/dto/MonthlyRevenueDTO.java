package com.Group18.hotel_automation.dto;

public class MonthlyRevenueDTO {

    private String month;
    private double revenue;

    public MonthlyRevenueDTO(String month, double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() { return month; }
    public double getRevenue() { return revenue; }
}