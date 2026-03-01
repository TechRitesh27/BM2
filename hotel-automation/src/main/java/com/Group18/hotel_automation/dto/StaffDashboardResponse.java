package com.Group18.hotel_automation.dto;

public class StaffDashboardResponse {

    private long pendingRequests;
    private long myAssignedRequests;
    private long completedToday;
    private double averageCompletionMinutes;
    private double serviceRevenueGenerated;
    private String departmentName;

    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }

    public long getMyAssignedRequests() { return myAssignedRequests; }
    public void setMyAssignedRequests(long myAssignedRequests) { this.myAssignedRequests = myAssignedRequests; }

    public long getCompletedToday() { return completedToday; }
    public void setCompletedToday(long completedToday) { this.completedToday = completedToday; }

    public double getAverageCompletionMinutes() { return averageCompletionMinutes; }
    public void setAverageCompletionMinutes(double averageCompletionMinutes) { this.averageCompletionMinutes = averageCompletionMinutes; }

    public double getServiceRevenueGenerated() { return serviceRevenueGenerated; }
    public void setServiceRevenueGenerated(double serviceRevenueGenerated) { this.serviceRevenueGenerated = serviceRevenueGenerated; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

}