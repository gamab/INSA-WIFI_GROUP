package com.example.gb.wifibackgroundservice.DB;

/**
 * Created by gb on 17/03/15.
 */
public class NetworkDesc {

    private String mSsid;
    private String mBSSID;
    private String mPass;

    public NetworkDesc(String name, String BSSID, String pass) {
        this.mSsid = name;
        this.mBSSID = BSSID;
        this.mPass = pass;
    }

    public String getmSsid() {
        return mSsid;
    }

    public void setmSsid(String mSsid) {
        this.mSsid = mSsid;
    }

    public String getmPass() {
        return mPass;
    }

    public void setmPass(String mPass) {
        this.mPass = mPass;
    }

    public String getmBSSID() {
        return mBSSID;
    }

    public void setmBSSID(String mBSSID) {
        this.mBSSID = mBSSID;
    }

    public String toString() {
        return "(" + mSsid + " : " + mBSSID + " | " + mPass + ")";
    }
}
