package com.example.workwithmaps;

public class User {
    private String userName;
     private String phoneNo;
     private String longitude;

    public User(String userName, String phoneNo, String latitude, String longitude) {
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public User(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String latitude;
}
