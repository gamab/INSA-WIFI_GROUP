package introduction.cours.androidstudio.mark_wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkActivity extends ActionBarActivity {

    private static final String TAG = "ConnectActivity";

    private Button mConnectBtn;
    private RatingBar ratingBar;
    private TextView ssid;
    private TextView bssid;
    private TextView key;
    private TextView txtRatingValue;
    private TextView t_Debut;
    private TextView t_Fin;
    private BDD mBdd = new BDD(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mark);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ssid = (TextView) findViewById(R.id.user);
        bssid = (TextView) findViewById(R.id.user_bssid);
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
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));

            }
        });
    }


    public void rateMe()
    {
        Log.d(TAG, "********** rateMe ***********");
        mBdd.deleteEverything();
        String h_deb = checkHour(t_Debut.getText().toString());
        String h_fin = checkHour(t_Fin.getText().toString());
        String the_bssid = checkbssid(bssid.getText().toString());

        if (h_deb == null) {
            Toast.makeText(MarkActivity.this,"Time deb is not good",Toast.LENGTH_LONG).show();
        }
        else if (h_fin == null) {
            Toast.makeText(MarkActivity.this,"Time fin is not good",Toast.LENGTH_LONG).show();
        }
        else if (the_bssid == null) {
            Toast.makeText(MarkActivity.this,"Bssid is not good (ex : 1a:2b:3c:4d:5e:6f)",Toast.LENGTH_LONG).show();
        }
        else {
            // ADD NETWORK AND RETURN ITS ID
            long net1 = mBdd.addNetwork(ssid.getText().toString(), the_bssid, key.getText().toString());
            // ADD Qos AND RETURN ITS ID
            long qos1 = mBdd.addQos(txtRatingValue.getText().toString(), h_deb, h_fin);
            //Fait le lien entre les deux tables
            mBdd.enrollSettingClass((int) net1, (int) qos1);
            //Print DataBase
            mBdd.printDatabase();

            Toast.makeText(MarkActivity.this, "Well Done.", Toast.LENGTH_SHORT).show();
        }
    }

    public String checkHour(String heure) {
        Pattern hourPat1 = Pattern.compile("([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");

        Matcher match1 = hourPat1.matcher(heure);
        if (match1.find()) {
            Log.d(TAG,"Found pattern 1 in hour");
            return match1.group(0);
        }
        Log.d(TAG,"Hour does not seem to be good");
        return null;
    }

    public String checkbssid(String bssid) {
        Log.d(TAG, "Checking out bssid : " + bssid);
        Pattern bssidPat = Pattern.compile("^(([0-9a-f]){2}[:]){5}([0-9a-f]){2}$");

        Matcher match1 = bssidPat.matcher(bssid);
        if (match1.find()) {
            Log.d(TAG,"Found pattern 1 in bssid");
            return match1.group(0);
        }
        Log.d(TAG,"bssid does not seem to be good");
        return null;
    }
}
