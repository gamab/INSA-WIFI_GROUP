package com.example.gb.connectapp;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class ConnectActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private Button mDisconnectBtn;
    private Button mScanListWifiBtn;

    private int netId;

    private BDD mBdd = new BDD(this);

    private WifiManager mWifiManager;

    WifiScanReceiver mWifiReceiver;

    //ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);

        bdd_test_wifi();


        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiScanReceiver(mWifiManager);

        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFromScan();
            }
        });

        mDisconnectBtn = (Button) findViewById(R.id.btn_disconnect);
        mDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG, "Click sur le bouton Deconnexion");
                Disconnect();
            }
        });

        mScanListWifiBtn = (Button) findViewById(R.id.btn_listwifi);
        mScanListWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG, "Click sur le bouton ListerLesWifis");
                wifiScanning();
            }
        });
    }

    //When the app is going on pause then we stop the broadcast receiver
    protected void onPause() {
        //unregisterReceiver(mWifiReceiver);
        super.onPause();
    }

    //When the app is starting again then we start the broadcast receiver back
    protected void onResume() {
        //registerReceiver(mWifiReceiver, new IntentFilter(
        //        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    public void Connect(String networkSSID, String networkPass) {
        Log.d(TAG, "********** FONCTION CONNECT");
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        Log.d(TAG, "Configure wifi config (" + networkSSID + " | " + networkPass + ")");

        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        //remember id
        netId = wifiManager.addNetwork(wifiConfig);
        boolean mNetConnected = false;
        Log.d(TAG, "Disconnecting");
        wifiManager.disconnect();

        while (mNetConnected == false) {
            if (netId != -1) {
                Log.d(TAG, "Enabling association to a previously configured Net");
                mNetConnected = wifiManager.enableNetwork(netId, true);
                Log.d(TAG, "Reconnecting to the currently active AP ");
                mNetConnected = wifiManager.reconnect();
            }
        }
    }

    public void Disconnect() {
        //Log.d(TAG,"Get rid of wifi config (" + mWifiManager.getConfiguredNetworks().get(netId).SSID + " | )");

        Log.d(TAG, "Disconnecting");
        mWifiManager.disconnect();
        Log.d(TAG, "Removing Network");
        mWifiManager.removeNetwork(this.netId);
    }

    public void wifiScanning() {
        Log.d(TAG, "Scanning Nets");
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }


    public void bdd_test_wifi() {
        mBdd.deleteEverything();

        long net1 = mBdd.addNetwork("Yann Mb", "36:5a:05:a4:de:67", "pass12345__");
        long net2 = mBdd.addNetwork("gifi", "f8:e0:79:3d:33:3c", "pass12345__");
        long net3 = mBdd.addNetwork("JCsWiFi", "f0:72:8c:9d:7c:dd", "usxx5247");

        // ADD Qos AND RETURN THEIR IDS
        long qos1 = mBdd.addQos("5", "16:00:00", "16:10:00");
        long qos2 = mBdd.addQos("6", "17:00:00", "18:20:00");
        long qos3 = mBdd.addQos("7", "17:00:00", "18:30:00");

        //Fait le lien entre les deux tables
        mBdd.enrollSettingClass((int) net1, (int) qos1);
        mBdd.enrollSettingClass((int) net2, (int) qos2);
        mBdd.enrollSettingClass((int) net3, (int) qos3);

        mBdd.printDatabase();
    }

    public void connectFromScan() {
        NetworkDesc mScanNet;
        Log.d(TAG, "********** FONCTION connectFromScan");
        wifiScanning();
        mScanNet = mBdd.getNetworkByNoteFrom(mWifiReceiver.getSsidList());


        if (mScanNet.getmName() != null && mScanNet.getmBSSID() != null && mScanNet.getmPass() != null) {
            Toast.makeText(ConnectActivity.this, "Best network is : SSID : " +
                    mScanNet.getmName() + " PKEY : " + mScanNet.getmPass() + "\nConnecting.", Toast.LENGTH_SHORT).show();
            Connect(mScanNet.getmName(), mScanNet.getmPass());
        }
        else {
            Toast.makeText(ConnectActivity.this, "Best network is hidden or Best network could not be found", Toast.LENGTH_SHORT).show();
        }

    }
}
