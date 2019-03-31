package org.autoride.autoride.TrackRider.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goldenreign on 7/4/2018.
 */

public class TrackMeItem implements Parcelable {



    public String trackerId;
    public String name;
    public String phone;
    public String role;
    public String imageUrl;
    public String trakingStatus;
    public String authStatus;


    public TrackMeItem() {
    }

    public TrackMeItem(String trackerId, String name, String phone,
                       String role, String imageUrl, String trakingStatus, String authStatus) {
        this.trackerId = trackerId;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.imageUrl = imageUrl;
        this.trakingStatus = trakingStatus;
        this.authStatus = authStatus;
    }

    protected TrackMeItem(Parcel in) {
        trackerId = in.readString();
        name = in.readString();
        phone = in.readString();
        role = in.readString();
        imageUrl = in.readString();
        trakingStatus = in.readString();
        authStatus = in.readString();
    }

    public static final Creator<TrackMeItem> CREATOR = new Creator<TrackMeItem>() {
        @Override
        public TrackMeItem createFromParcel(Parcel in) {
            return new TrackMeItem(in);
        }

        @Override
        public TrackMeItem[] newArray(int size) {
            return new TrackMeItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(trackerId);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(role);
        parcel.writeString(imageUrl);
        parcel.writeString(trakingStatus);
        parcel.writeString(authStatus);
    }
}
