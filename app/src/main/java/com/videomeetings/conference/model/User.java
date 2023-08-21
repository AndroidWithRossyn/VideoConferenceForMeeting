package com.videomeetings.conference.model;

public class User {
    private long coins = 500;
    String mFullName;
    String mUserName;
    String mAge;
    String mGender;
    String mProfilePath;

    public User(String mFullName, String mUserName, String mAge, String mGender,String mProfilePath,long coins) {
        this.coins = coins;
        this.mFullName = mFullName;
        this.mUserName = mUserName;
        this.mAge = mAge;
        this.mGender = mGender;
        this.mProfilePath = mProfilePath;
    }

    public String getmProfilePath() {
        return mProfilePath;
    }

    public void setmProfilePath(String mProfilePath) {
        this.mProfilePath = mProfilePath;
    }
    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmAge() {
        return mAge;
    }

    public void setmAge(String mAge) {
        this.mAge = mAge;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public User() {

    }

}
