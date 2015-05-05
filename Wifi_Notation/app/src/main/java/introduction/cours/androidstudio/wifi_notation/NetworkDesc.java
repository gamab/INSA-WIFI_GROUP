package introduction.cours.androidstudio.wifi_notation;

/**
 * Created by Yann MB on 04/05/2015.
 */
public class NetworkDesc {
    private String mName;
    private String mBSSID;
    private String mPass;

    public NetworkDesc(String name, String BSSID, String pass) {
        this.mName = name;
        this.mBSSID = BSSID;
        this.mPass = pass;
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

    public String getmBSSID() {
        return mBSSID;
    }

    public void setmBSSID(String mBSSID) {
        this.mBSSID = mBSSID;
    }
}

