package org.autoride.autoride.SMSAuth;

import android.os.Parcel;
import android.os.Parcelable;

public class SMSItem implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SMSItem> CREATOR = new Parcelable.Creator<SMSItem>() {
        @Override
        public SMSItem createFromParcel(Parcel in) {
            return new SMSItem(in);
        }

        @Override
        public SMSItem[] newArray(int size) {
            return new SMSItem[size];
        }
    };

    public String statusCode;
    public String status;
    public String success;
    public String errors;
    public String code;
    public String message;

    public SMSItem() {
    }

    public SMSItem(String statusCode, String status, String success, String errors, String code, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.success = success;
        this.errors = errors;
        this.code = code;
        this.message = message;
    }

    protected SMSItem(Parcel in) {
        statusCode = in.readString();
        status = in.readString();
        success = in.readString();
        errors = in.readString();
        code = in.readString();
        message = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(statusCode);
        dest.writeString(status);
        dest.writeString(success);
        dest.writeString(errors);
        dest.writeString(code);
        dest.writeString(message);
    }
}