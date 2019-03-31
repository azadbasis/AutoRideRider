package org.autoride.autoride.TrackRider.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goldenreign on 7/3/2018.
 */

public class TrackingPeopleListItem implements Parcelable {

    public String userId;
    public String name;
    public String phone;
    public String role;
    public String imageUrl;
    public String lat;
    public String lng;
    public String trakingStatus;
    public String authStatus;

    public TrackingPeopleListItem(String userId, String name, String phone, String role,
                                  String imageUrl, String lat, String lng, String trakingStatus, String authStatus) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.trakingStatus = trakingStatus;
        this.authStatus = authStatus;
    }

    public TrackingPeopleListItem(String name, String authStatus) {
        this.name = name;
        this.authStatus = authStatus;
    }

    public TrackingPeopleListItem() {
    }

    protected TrackingPeopleListItem(Parcel in) {
        userId = in.readString();
        name = in.readString();
        phone = in.readString();
        role = in.readString();
        imageUrl = in.readString();
        lat = in.readString();
        lng = in.readString();
        trakingStatus = in.readString();
        authStatus = in.readString();
    }

    public static final Creator<TrackingPeopleListItem> CREATOR = new Creator<TrackingPeopleListItem>() {
        @Override
        public TrackingPeopleListItem createFromParcel(Parcel in) {
            return new TrackingPeopleListItem(in);
        }

        @Override
        public TrackingPeopleListItem[] newArray(int size) {
            return new TrackingPeopleListItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(role);
        parcel.writeString(imageUrl);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(trakingStatus);
        parcel.writeString(authStatus);
    }
}
