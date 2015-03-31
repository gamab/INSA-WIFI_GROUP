package com.example.gb.connectapp;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


public class ConnectActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private Button mDisconnectBtn;

    private int netId;

    private BDD sh = new BDD(this);

    private WifiManager wifiManager;
    //WifiScanReceiver wifiReceiver;

    ListView list;

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
                Log.d(TAG, "Click sur le bouton Connect");
                //Connect(networkSSID ,networkPass);
                sh.deleteEverything();

                // ADD Networks AND RETURN THEIR IDS
                long net2 = Ajout_Reseau_BDD("GabMab", "12345_wifi");
                long net1 = Ajout_Reseau_BDD("Yann Mb", "12345_wifi");

                // ADD Qos AND RETURN THEIR IDS
                long qos1 = Evaluation_Reseau(net1, "5", "16:00:00");
                long qos2 = Evaluation_Reseau(net2, "4", "17:00:00");

                String ssid=sh.getNetworkByMark();
                Connect(ssid, sh.getPresharKeyBySSID(ssid));

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
                Log.d(TAG,"Click sur le bouton Disconnect");
                Disconnect();
            }
        });
    }

    public void Connect(String networkSSID ,String networkPass) {
        Log.d(TAG,"********** FONCTION CONNECT");
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        Log.d(TAG,"Configure wifi config (" + networkSSID + " | " + networkPass + ")");

        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        //remember id
        netId = wifiManager.addNetwork(wifiConfig);
        Log.d(TAG,"Disconnecting");
        wifiManager.disconnect();
        Log.d(TAG,"Enabling network");
        wifiManager.enableNetwork(netId, true);
        Log.d(TAG,"Connecting");
        wifiManager.reconnect();
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

    public long Ajout_Reseau_BDD(String networkSSID ,String networkPass)
    {
        Log.d(TAG,"********** FONCTION Association_Liste_reseau");
        // ADD Networks AND RETURN THEIR IDS
        long net = sh.addNetwork(networkSSID, networkPass);
        //Connect(networkSSID, networkPass);

        return net;
    }

    public void Classement_Reseau_par_Note(long netID ,int Note)
    {
        Log.d(TAG,"********** FONCTION Classement_Reseau_par_Note");
        // GET Qos Note FOR Networks AND FILTER BY Note

    }

    public long Evaluation_Reseau(long netID, String note, String time)
    {
        Log.d(TAG,"********** Evaluation_Reseau");
        // ADD Qos AND RETURN THEIR IDS
        long qos = sh.addQos(note,time);
        // ENROLL Networks IN Qos
        sh.enrollSettingClass((int) netID,(int) qos);

        return qos;
    }

    public void Visualisation_Parametres_Qos_reseau(long netID )
    {
        Log.d(TAG,"********** FONCTION Visualisation_Parametres_Qos_reseau");
        // GET Qos FOR network
        sh.getSettingsForNetwork((int) netID);
    }

    public int TimeStamp_Insa()
    {
        Log.d(TAG,"********** FONCTION TimeStamp_Insa");
        String time=java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        if(time.equals("16:45:00"))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int TimeStamp_edurom()
    {
        String time=java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
        if("16:46:00"==time)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
