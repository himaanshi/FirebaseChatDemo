package com.paxcel.firebasechatdemo;

public class User {

    public String phoneNumber;
    public String uId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User( String uId,String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.uId = uId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}