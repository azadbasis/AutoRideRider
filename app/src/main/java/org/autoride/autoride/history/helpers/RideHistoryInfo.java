package org.autoride.autoride.history.helpers;

import com.google.android.gms.maps.model.LatLng;

public class RideHistoryInfo {

    private String rideId;
    private String rideDate;
    private String amount;
    private String vehicleDesc;
    private String vehicleType;
    private String paymentType;
    private String rideStatus;
    private String rideDriverName;
    private String rideDriverPhoto;
    private LatLng pickupLatLng;
    private LatLng dropLatLng;
    private Double baseFare;
    private Double distanceFare;
    private Double timeFare;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getRideDate() {
        return rideDate;
    }

    public void setRideDate(String rideDate) {
        this.rideDate = rideDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVehicleDesc() {
        return vehicleDesc;
    }

    public void setVehicleDesc(String vehicleDesc) {
        this.vehicleDesc = vehicleDesc;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getRideDriverName() {
        return rideDriverName;
    }

    public void setRideDriverName(String rideDriverName) {
        this.rideDriverName = rideDriverName;
    }

    public String getRideDriverPhoto() {
        return rideDriverPhoto;
    }

    public void setRideDriverPhoto(String rideDriverPhoto) {
        this.rideDriverPhoto = rideDriverPhoto;
    }

    public LatLng getPickupLatLng() {
        return pickupLatLng;
    }

    public void setPickupLatLng(LatLng pickupLatLng) {
        this.pickupLatLng = pickupLatLng;
    }

    public LatLng getDropLatLng() {
        return dropLatLng;
    }

    public void setDropLatLng(LatLng dropLatLng) {
        this.dropLatLng = dropLatLng;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getDistanceFare() {
        return distanceFare;
    }

    public void setDistanceFare(Double distanceFare) {
        this.distanceFare = distanceFare;
    }

    public Double getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(Double timeFare) {
        this.timeFare = timeFare;
    }
}