package com.example.gb.wifibackgroundservice.WiFi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gb.wifibackgroundservice.DB.BDD;
import com.example.gb.wifibackgroundservice.DB.NetworkDesc;

import java.util.List;
import java.util.ListIterator;


public class ConnectionIntelligence {

    private static final String TAG = "ConnectionIntelligence";
    private static final int MAX_TRIES = 3;

    private int netId;

    private BDD mBdd;

    private Context mCtxt;

    private WifiManager mWifiManager;

    WifiScanReceiver mWifiReceiver;


    public ConnectionIntelligence(Context ctxt) {
        mBdd = new BDD(ctxt);
        netId = -1;
        mCtxt = ctxt;

        bdd_test_wifi();

        mWifiManager = (WifiManager) ctxt.getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiScanReceiver(mWifiManager);

    }

    public void Connect(NetworkDesc nd) {
        Log.d(TAG, "====FONCTION CONNECT====");
        int numberTries;

        //creating a wifi conf for this wifi with ssid, bssid, pass
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = nd.getmSsid();
        wifiConfig.BSSID = nd.getmBSSID();
        wifiConfig.preSharedKey = nd.getmPass();
        Log.d(TAG, "Configure wifi config (" + nd.getmSsid() + " | " + nd.getmBSSID() + ")");

        //searching for the network in the configured ones
        netId = getNetIdFromConfiguredNetworks(nd.getmBSSID());
        //if we cannot find it then adding one
        if (netId==-1) {
            netId = mWifiManager.addNetwork(wifiConfig);
        }

        if (netId != -1) {
            //trying to disconnect at least MAX_TRIES
            numberTries = 0;
            Log.d(TAG, "Disabling last connected wifi");
            mWifiManager.enableNetwork(mWifiManager.getConnectionInfo().getNetworkId(), false);
            Log.d(TAG, "Disconnecting");
            while(!mWifiManager.disconnect() && numberTries < MAX_TRIES) {
                numberTries ++;
            }

            //trying to connect to the network at least MAX_TRIES
            numberTries = 0;
            while (!areWeConnectedTo(nd) && numberTries < MAX_TRIES) {
                Log.d(TAG, "Enabling association to a previously configured Net");
                if (mWifiManager.enableNetwork(netId, true)) {
                    Log.d(TAG, "Reconnecting to the currently active AP ");
                    mWifiManager.reconnect();
                }
                numberTries ++;
            }
        }
    }

    /**
     * This function allows disconnection and removing the network from the registered networks
     * Just here to show that we can.
     */
    public void Disconnect() {
        //Log.d(TAG,"Get rid of wifi config (" + mWifiManager.getConfiguredNetworks().get(netId).SSID + " | )");

        Log.d(TAG, "Disconnecting");
        mWifiManager.disconnect();
        Log.d(TAG, "Removing Network");
        mWifiManager.removeNetwork(this.netId);
    }

    public void wifiScanning() {
        Log.d(TAG, "Scanning Nets");
        mCtxt.registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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

        //Links both tables
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

        if (mScanNet.getmSsid() != null && mScanNet.getmBSSID() != null && mScanNet.getmPass() != null) {
            Toast.makeText(mCtxt, "Best network is : SSID : " +
                    mScanNet.getmSsid() + " PKEY : " + mScanNet.getmPass() + "\nConnecting.", Toast.LENGTH_SHORT).show();
            Connect(mScanNet);
        }
        else {
            Toast.makeText(mCtxt, "Best network is hidden or Best network could not be found", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Connects to the best wifi we have in the DataBase if we are not already connected to it
     * @return the newly connected wifi SSID if we changed
     */
    public String connectWithoutScan() {
        NetworkDesc net;
        Log.d(TAG, "====FONCTION connectWithoutScan====");
        net = mBdd.getBestNetworkForCurrentTime();


        if (net.getmSsid() != null && net.getmBSSID() != null && net.getmPass() != null) {
            //if it is not the last wifi to which we already connected
            if (areWeConnectedTo(net)) {
                Log.d(TAG, "Already connected to the best wifi");
                return null;
            } else {
                Connect(net);
                return net.getmSsid();
            }
        }
        return null;
    }


    public int getNetIdFromConfiguredNetworks(String networkSSID) {
        int netId = -1;
        WifiConfiguration currentConf;
        List<WifiConfiguration> lWifi = mWifiManager.getConfiguredNetworks();

        ListIterator<WifiConfiguration> litWifi = lWifi.listIterator();

        while (litWifi.hasNext() && netId==-1) {
            currentConf = litWifi.next();
            //Log.d(TAG,"Compare " + currentConf.SSID + " to " + networkSSID);
            if (currentConf.SSID.equals(networkSSID)) {
                Log.d(TAG,"Wifi " + currentConf.SSID + " already in conf.");
                netId = currentConf.networkId;
            }
        }
        return netId;
    }

    private NetworkDesc getCurrentlyConnectedNet() {
        WifiInfo wifiInf = mWifiManager.getConnectionInfo();
        NetworkDesc nd = new NetworkDesc(wifiInf.getSSID(),wifiInf.getBSSID(),"");
        return nd;
    }

    private boolean areWeConnectedTo(NetworkDesc nd) {
        NetworkDesc currentNet = getCurrentlyConnectedNet();
        if (currentNet.getmSsid().equals(nd.getmSsid()) && currentNet.getmBSSID().equals(nd.getmBSSID())) {
            Log.d(TAG,currentNet + " is equal to " + nd);
            return true;
        }
        else {
            Log.d(TAG,currentNet + " is not equal to " + nd);
            return false;
        }
    }

    /*public void testgetNetIdFromConfiguredNetworks() {
        if (getNetIdFromConfiguredNetworks("JC's Wifi") == -1) {
            Log.d(TAG,"Did not pass the test \"JC's Wifi\" was not found");
        }
        else {
            Log.d(TAG,"Did pass the test \"JC's Wifi\" was found");
        }
    }*/
}
