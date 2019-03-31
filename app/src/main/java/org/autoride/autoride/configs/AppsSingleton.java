package org.autoride.autoride.configs;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class AppsSingleton {

    private static AppsSingleton instance;
    private static OkHttpClient okHttpClient;

    private String accessToken;
    private String rememberToken;
    private String riderId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String role;
    private String profilePhoto;
    private String promotionCode;
    private String lastLatitude;
    private String lastLongitude;
    private String riderSearchVehicle;
    private String riderRating;

    private AppsSingleton() {

    }

    public static AppsSingleton getInstance() {
        if (instance == null) {
            instance = new AppsSingleton();
        }

        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            okHttpClient = builder.build();
            AppsSingleton.getInstance().setOkHttpClient(okHttpClient);
        }
        return instance;
    }

    public static void setInstance(AppsSingleton instance) {
        AppsSingleton.instance = instance;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRiderSearchVehicle() {
        return riderSearchVehicle;
    }

    public void setRiderSearchVehicle(String riderSearchVehicle) {
        this.riderSearchVehicle = riderSearchVehicle;
    }

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    public String getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(String riderRating) {
        this.riderRating = riderRating;
    }
}