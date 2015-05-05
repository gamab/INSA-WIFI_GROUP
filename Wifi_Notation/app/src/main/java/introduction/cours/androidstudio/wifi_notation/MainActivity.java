package introduction.cours.androidstudio.wifi_notation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.content.Context;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.wifi.WifiInfo;



public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
    private Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
    private BDD mBdd = new BDD(this);
    private TextView ssid = (TextView) findViewById(R.id.user);
    private TextView key = (TextView) findViewById(R.id.user_password);
    private TextView txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);
    private TextView t_Debut = (TextView) findViewById(R.id.hk_time_Deb);
    private TextView t_Fin = (TextView) findViewById(R.id.hk_time_Fin);
    WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    WifiScanReceiver wifiReciever = new WifiScanReceiver();
    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
    private String bssid ="";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager.startScan();

        addListenerOnRatingBar();
        addListenerOnButton();
    }

    protected void onPause() {
       // unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
       // registerReceiver(wifiReciever, new IntentFilter(
         //       WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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


    public void addListenerOnButton()
    {
        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateMe();
            }
        });
    }




    public void rateMe()
    {
        Log.d(TAG, "********** rateMe ***********");
        mBdd.deleteEverything();

        // ADD NETWORK AND RETURN ITS ID
        long net1 = mBdd.addNetwork(ssid.getText().toString(), bssid, key.getText().toString());
        // ADD Qos AND RETURN ITS ID
        long qos1 = mBdd.addQos(txtRatingValue.getText().toString(), t_Debut.getText().toString(), t_Fin.getText().toString());
        //Fait le lien entre les deux tables
        mBdd.enrollSettingClass((int) net1, (int) qos1);
        //Print DataBase
        mBdd.printDatabase();

        Toast.makeText(MainActivity.this, "Well Done.", Toast.LENGTH_SHORT).show();
    }



    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
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
           // wifiManager.startScan(); //start a new scan to update values faster
        }
    }
}
