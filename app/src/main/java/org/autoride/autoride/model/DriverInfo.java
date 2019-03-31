package org.autoride.autoride.model;

public class DriverInfo {

    private String driverId;
    private String driverFireBaseToken;
    private String driverStatus;
    private String vehicleType;
    private String verificationStatus;
    private String driverDistance;
    private double driverLat;
    private double driverLng;

    private String fullName;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getDriverDistance() {
        return driverDistance;
    }

    public void setDriverDistance(String driverDistance) {
        this.driverDistance = driverDistance;
    }

    public double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(double driverLat) {
        this.driverLat = driverLat;
    }

    public double getDriverLng() {
        return driverLng;
    }

    public void setDriverLng(double driverLng) {
        this.driverLng = driverLng;
    }

    public String getDriverFireBaseToken() {
        return driverFireBaseToken;
    }

    public void setDriverFireBaseToken(String driverFireBaseToken) {
        this.driverFireBaseToken = driverFireBaseToken;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}