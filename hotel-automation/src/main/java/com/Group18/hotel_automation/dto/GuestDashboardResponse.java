package com.Group18.hotel_automation.dto;

public class GuestDashboardResponse {

    private long activeBooking;
    private long totalBookings;
    private long pendingServices;
    private double currentBill;

    public GuestDashboardResponse(long activeBooking,
                                  long totalBookings,
                                  long pendingServices,
                                  double currentBill) {
        this.activeBooking = activeBooking;
        this.totalBookings = totalBookings;
        this.pendingServices = pendingServices;
        this.currentBill = currentBill;
    }

    public long getActiveBooking() {
        return activeBooking;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getPendingServices() {
        return pendingServices;
    }

    public double getCurrentBill() {
        return currentBill;
    }
}