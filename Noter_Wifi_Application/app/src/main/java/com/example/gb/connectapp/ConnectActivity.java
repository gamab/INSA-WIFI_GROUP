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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;


public class ConnectActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private RatingBar ratingBar;
    private TextView ssid;
    private TextView key;
    private TextView txtRatingValue;
    private TextView t_Debut;
    private TextView t_Fin;
    private String bssid ="";
    private BDD mBdd = new BDD(this);
    private WifiManager mWifiManager;
    WifiScanReceiver mWifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);


        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiScanReceiver(mWifiManager);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ssid = (TextView) findViewById(R.id.user);
        key = (TextView) findViewById(R.id.user_password);
        txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
        t_Debut = (TextView) findViewById(R.id.hk_time_Deb);
        t_Fin = (TextView) findViewById(R.id.hk_time_Fin);
        mConnectBtn = (Button) findViewById(R.id.btnSubmit);

        addListenerOnRatingBar();
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateMe();
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

    public void addListenerOnRatingBar()
    {
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));

            }
        });
    }



    public void wifiScanning() {
        Log.d(TAG, "Scanning Nets");
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    public void getInformation()
    {
        wifiScanning();
        List<ScanResult> wifiScanList = mWifiManager.getScanResults();
        for (int i = 0; i < wifiScanList.size(); i++)
        {
            if(ssid.getText().toString().equals(wifiScanList.get(i).SSID))
            {
                bssid=""+wifiScanList.get(i).BSSID;
                Log.w(TAG, "found bssid :"+bssid);
            }

        }
        if(bssid.equals(""))
        {
            Log.w(TAG, "not found bssid");
        }
    }

    public void rateMe()
    {
        Log.d(TAG, "********** rateMe ***********");
        mBdd.deleteEverything();

        getInformation();

        // ADD NETWORK AND RETURN ITS ID
        long net1 = mBdd.addNetwork(ssid.getText().toString(), bssid, key.getText().toString());
        // ADD Qos AND RETURN ITS ID
        long qos1 = mBdd.addQos(txtRatingValue.getText().toString(), t_Debut.getText().toString(), t_Fin.getText().toString());
        //Fait le lien entre les deux tables
        mBdd.enrollSettingClass((int) net1, (int) qos1);
        //Print DataBase
        mBdd.printDatabase();

        Toast.makeText(ConnectActivity.this, "Well Done.", Toast.LENGTH_SHORT).show();
    }
}
