package com.example.gb.wifibackgroundservice.WiFi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.gb.wifibackgroundservice.DB.NetworkDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Samanthol on 31/03/2015.
 */
public class WifiScanReceiver extends BroadcastReceiver {

    WifiManager mainWifiObj;
    ArrayList<NetworkDesc> ssidList = new ArrayList<NetworkDesc>();
    private static final String TAG = "ScanActivity";

    public WifiScanReceiver(WifiManager mainWifiObj) {
        this.mainWifiObj = mainWifiObj;
    }

    //This class is called when receiving a signal from the scan
    @SuppressLint("UseValueOf")

    public void onReceive(Context c, Intent intent) {
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        ListIterator<ScanResult> lit = wifiScanList.listIterator();
        ScanResult sr;
        NetworkDesc nd;
        ssidList.clear();
        while (lit.hasNext()) {
            sr = lit.next();
            if (! sr.SSID.isEmpty()) {
                //Log.d(TAG,"Ajout d'un reseau");
                nd = new NetworkDesc(sr.SSID, sr.BSSID, null);
                ssidList.add(nd);
            }
            else {
                Log.d(TAG,"SSID is Empty");
            }
        }
        showSSID();
        Log.d(TAG,"Unregister this receiver");
        c.unregisterReceiver(this);
    }

    /*
    Fonction permettant de parcours la liste des SSID et les afficher
     */
    public void showSSID() {
        Log.d(TAG, "Liste des SSID disponibles");
        for (NetworkDesc parcoursList : ssidList) {
            Log.d(TAG, parcoursList.getmSsid() + " " + parcoursList.getmBSSID() + '\n');
        }
    }

    public ArrayList<NetworkDesc> getSsidList() {
        return ssidList;
    }
}
