package com.example.gb.connectapp;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class ConnectActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private Button mDisconnectBtn;

    private int netId;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);

        wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);

        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG,"Click sur le bouton Connect");
                Connect();
            }
        });

        mDisconnectBtn = (Button) findViewById(R.id.btn_disconnect);
        mDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG,"Click sur le bouton Disconnect");
                Disconnect();
            }
        });
    }


    public void Connect() {
        String networkSSID = "JCsWifi2";
        String networkPass = "MDP genial";
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        Log.d(TAG,"Configure wifi config (" + networkSSID + " | " + networkPass + ")");

        //remember id
        netId = wifiManager.addNetwork(wifiConfig);
        Log.d(TAG,"Disconnecting");
        wifiManager.disconnect();
        Log.d(TAG,"Enabling network");
        wifiManager.enableNetwork(netId, true);
        Log.d(TAG,"Connecting");
        wifiManager.reconnect();
    }

    public void Disconnect() {
        String networkSSID = "JCsWiFi";
        Log.d(TAG,"Get rid of wifi config (" + networkSSID + " | )");

        Log.d(TAG,"Disconnecting");
        wifiManager.disconnect();
        Log.d(TAG,"Removing Network");
        wifiManager.removeNetwork(this.netId);
    }

}
