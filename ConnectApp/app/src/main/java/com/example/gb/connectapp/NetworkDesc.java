package com.example.gb.connectapp;

/**
 * Created by gb on 17/03/15.
 */
public class NetworkDesc {

    private String mName;
    private String mPass;

    public NetworkDesc(String mName, String mPass) {
        this.mName = mName;
        this.mPass = mPass;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPass() {
        return mPass;
    }

    public void setmPass(String mPass) {
        this.mPass = mPass;
    }
}
