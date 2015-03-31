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

import java.util.ArrayList;
import java.util.Calendar;


public class ConnectActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private Button mDisconnectBtn;
    private Button mScanListWifiBtn;

    private int netId;

    private BDD mBdd = new BDD(this);

    private WifiManager mwifiManager;
    //WifiScanReceiver wifiReceiver;

    ArrayList<String> ssidList = new ArrayList<String>();

    WifiScanReceiver wifiReceiver;

    //ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);

        mwifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mConnectBtn = (Button) findViewById(R.id.btn_connect);
        wifiReceiver = new WifiScanReceiver(mwifiManager);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here put the connection code
                Log.d(TAG, "Click sur le bouton Connection");
                //Connect(networkSSID ,networkPass);
                mBdd.deleteEverything();

                // ADD Networks AND RETURN THEIR IDS
                //long net2 = Ajout_Reseau_BDD("GabMab", "12345_wifi");
                //long net1 = Ajout_Reseau_BDD("Yann Mb", "12345_wifi");
                long net1 = mBdd.addNetwork("Yann Mb", "36:5A:05:A4:DE:67", "pass12345__");
                long net2 = mBdd.addNetwork("gifi", "F8:E0:79:3D:33:3C", "pass12345__");

                // ADD Qos AND RETURN THEIR IDS
                long qos1 = Evaluation_Reseau(net1, "5", "16:00:00");
                long qos2 = Evaluation_Reseau(net2, "6", "17:00:00");

                //String ssid=sh.getNetworkByMark();
                //Connect(ssid, sh.getPresharKeyBySSID(ssid));
                //NetworkDesc nd = sh.getNetworkByNote();
                ArrayList<NetworkDesc> wifis = new ArrayList<>();
                wifis.add(new NetworkDesc("Yann Mb", "36:5A:05:A4:DE:67",""));
                wifis.add(new NetworkDesc("gifi", "F8:E0:79:3D:33:3C",""));
                wifis.add(new NetworkDesc("JCwifi", "36:5A:05:C5:DF:89",""));

                NetworkDesc nd = mBdd.getNetworkByNoteFrom(wifis);
                if (nd != null) {
                    if (nd.getmName() != null) {
                        Log.d(TAG, "Best network is : SSID : " + nd.getmName() + " PKEY : " + nd.getmPass());
                    }
                    else {
                        Log.d(TAG, "Could not found best WIFI.");
                    }
                }
                else {
                    Log.d(TAG, "Could not found best WIFI.");
                }
                //Connect(nd.getmName(),nd.getmPass());
                mBdd.printDatabase();

                /*
                while (true) {
                    if (TimeStamp_Insa() != 0) {
                        Connect("Insa", "12345_wifi");
                    } else if (TimeStamp_edurom() != 0) {
                        Connect("Edurom", "12345_wifi");
                    }
                }
                */
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
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    //When the app is starting again then we start the broadcast receiver back
    protected void onResume() {
        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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
        netId = mwifiManager.addNetwork(wifiConfig);
        Log.d(TAG, "Disconnecting");
        mwifiManager.disconnect();
        Log.d(TAG, "Enabling network");
        mwifiManager.enableNetwork(netId, true);
        Log.d(TAG, "Connecting");
        mwifiManager.reconnect();
    }

    public void Disconnect() {
        //Log.d(TAG,"Get rid of wifi config (" + mwifiManager.getConfiguredNetworks().get(netId).SSID + " | )");

        Log.d(TAG, "Disconnecting");
        mwifiManager.disconnect();
        Log.d(TAG, "Removing Network");
        mwifiManager.removeNetwork(this.netId);
    }

    public void wifiScanning() {
        Log.d(TAG, "Scanning Nets");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mwifiManager.startScan();
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
}
