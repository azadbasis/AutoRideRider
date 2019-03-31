package org.autoride.autoride.TrackRider;

/**
 * Created by goldenreign on 6/26/2018.
 */

public class Contact {

    private String contactName;
    private String contactPhoneNumber;
    private int id;
    public boolean selected;

    private String checkBoxStatus;

    public Contact() {
    }

    public Contact(String checkBoxStatus) {
        this.checkBoxStatus = checkBoxStatus;
    }

    public Contact(int id, String checkBoxStatus) {
        this.id = id;
        this.checkBoxStatus = checkBoxStatus;
    }

    public String getCheckBoxStatus() {
        return checkBoxStatus;
    }

    public Contact(String contactName, String contactPhoneNumber) {
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
    }


    public Contact(String contactName, String contactPhoneNumber, int id) {
        this.contactName = contactName;
        this.contactPhoneNumber = contactPhoneNumber;
        this.id = id;
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
