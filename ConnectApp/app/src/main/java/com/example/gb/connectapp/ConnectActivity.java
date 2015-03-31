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

                ArrayList<NetworkDesc> wifiList = mWifiReceiver.getSsidList();

                NetworkDesc nd = mBdd.getNetworkByNoteFrom(wifiList);

                if (nd != null) {
                    if (nd.getmName() != null) {
                        Toast.makeText(ConnectActivity.this, "Best network is : SSID : " +
                                nd.getmName() + " PKEY : " + nd.getmPass() + "\nConnecting.", Toast.LENGTH_SHORT).show();
                        Connect(nd.getmName(),nd.getmPass());
                    }
                    else {
                        Toast.makeText(ConnectActivity.this, "Best network is hidden", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ConnectActivity.this, "Best network could not be found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mDisconnectBtn = (Button) findViewById(R.id.btn_disconnect);
        mDisconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG, "Click sur le bouton Deconnection");
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
        Log.d(TAG, "Disconnecting");
        wifiManager.disconnect();
        Log.d(TAG, "Enabling network");
        wifiManager.enableNetwork(netId, true);
        Log.d(TAG, "Connecting");
        wifiManager.reconnect();
    }

    public void Connect() {
        String networkSSID = "JCsWifi2";
        String networkPass = "MDP genial";
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        Log.d(TAG, "Configure wifi config (" + networkSSID + " | " + networkPass + ")");

        //remember id
        netId = mWifiManager.addNetwork(wifiConfig);
        Log.d(TAG, "Disconnecting");
        mWifiManager.disconnect();
        Log.d(TAG, "Enabling network");
        mWifiManager.enableNetwork(netId, true);
        Log.d(TAG, "Connecting");
        mWifiManager.reconnect();
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

    public long Ajout_Reseau_BDD(String networkSSID, String networkPass) {
        Log.d(TAG, "********** FONCTION Association_Liste_reseau");
        // ADD Networks AND RETURN THEIR IDS
        long net = mBdd.addNetwork(networkSSID, networkPass);
        //Connect(networkSSID, networkPass);

        return net;
    }

    public void Classement_Reseau_par_Note(long netID, int Note) {
        Log.d(TAG, "********** FONCTION Classement_Reseau_par_Note");
        // GET Qos Note FOR Networks AND FILTER BY Note

    }

    public long Evaluation_Reseau(long netID, String note, String time) {
        Log.d(TAG, "********** Evaluation_Reseau");
        // ADD Qos AND RETURN THEIR IDS
        long qos = mBdd.addQos(note, time);
        // ENROLL Networks IN Qos
        mBdd.enrollSettingClass((int) netID, (int) qos);

        return qos;
    }


    public int TimeStamp_Insa() {
        Log.d(TAG, "********** FONCTION TimeStamp_Insa");
        String time = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        if (time.equals("16:45:00")) {
            return 1;
        } else {
            return 0;
        }
    }

    public void bdd_test_wifi() {
        mBdd.deleteEverything();

        long net1 = mBdd.addNetwork("Yann Mb", "36:5a:05:a4:de:67", "pass12345__");
        long net2 = mBdd.addNetwork("gifi", "f8:e0:79:3d:33:3c", "pass12345__");
        long net3 = mBdd.addNetwork("JCsWiFi", "f0:72:8c:9d:7c:dd", "usxx5247");

        // ADD Qos AND RETURN THEIR IDS
        long qos1 = Evaluation_Reseau(net1, "5", "16:00:00");
        long qos2 = Evaluation_Reseau(net2, "6", "17:00:00");
        long qos3 = Evaluation_Reseau(net2, "7", "17:00:00");

        mBdd.printDatabase();
    }
}
