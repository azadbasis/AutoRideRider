package org.autoride.autoride.model;

import java.util.List;

public class RiderInfo extends WebMessages {

    private String riderId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String role;
    private String usableDiscount;
    private String password;
    private String email;
    private String dob;
    private String gender;
    private String profilePhoto;
    private String coverPhoto;
    private String promotionCode;
    private String lastLatitude;
    private String lastLongitude;
    private boolean isAvailable;
    private String newPassword;
    private String currentPassword;
    private String mainBalance;
    private String usableBalance;
    private String commission;
    private String totalRide;
    private String totalCommission;
    private String riderAccountNo;
    private String riderRating;
    private String riderSearchVehicle;
    private Address riderAddress;
    private List<DriverInfo> driverInfos;

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(String lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public String getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(String lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getMainBalance() {
        return mainBalance;
    }

    public void setMainBalance(String mainBalance) {
        this.mainBalance = mainBalance;
    }

    public String getUsableBalance() {
        return usableBalance;
    }

    public void setUsableBalance(String usableBalance) {
        this.usableBalance = usableBalance;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getTotalRide() {
        return totalRide;
    }

    public void setTotalRide(String totalRide) {
        this.totalRide = totalRide;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }

    public String getRiderAccountNo() {
        return riderAccountNo;
    }

    public void setRiderAccountNo(String riderAccountNo) {
        this.riderAccountNo = riderAccountNo;
    }

    public Address getRiderAddress() {
        return riderAddress;
    }

    public void setRiderAddress(Address riderAddress) {
        this.riderAddress = riderAddress;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public List<DriverInfo> getDriverInfos() {
        return driverInfos;
    }

    public void setDriverInfos(List<DriverInfo> driverInfos) {
        this.driverInfos = driverInfos;
    }

    public String getRiderSearchVehicle() {
        return riderSearchVehicle;
    }

    public void setRiderSearchVehicle(String riderSearchVehicle) {
        this.riderSearchVehicle = riderSearchVehicle;
    }

    public String getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(String riderRating) {
        this.riderRating = riderRating;
    }

    public String getUsableDiscount() {
        return usableDiscount;
    }

    public void setUsableDiscount(String usableDiscount) {
        this.usableDiscount = usableDiscount;
    }
}