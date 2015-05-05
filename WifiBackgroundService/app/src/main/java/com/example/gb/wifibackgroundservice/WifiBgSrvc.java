package com.example.gb.wifibackgroundservice;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.gb.wifibackgroundservice.WiFi.ConnectionIntelligence;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gb on 28/04/15.
 */
public class WifiBgSrvc extends Service {

    private static final int PERIOD = 10000;

    private static Timer mTimer = new Timer();
    private Context mCtx;
    private ConnectionIntelligence mConnI;


    /**
     * The handler is woken up by the timer task and ... TO BE COMPLETED ...
     */
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String wifiSsid = mConnI.connectWithoutScan();
            if (wifiSsid != null) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentText("Wifi Service : " + wifiSsid)
                        .setContentTitle("Wifi");
                NotificationManager manager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
            }
        }
    };


    /**
     * This timer task sends an empty message to the handler
     */
    private class MainTask extends TimerTask {
        public void run()
        {
            mHandler.sendEmptyMessage(0);
        }
    }


    /**
     * On create we just retrieve the application context
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //Création du toast//
        mCtx = getApplicationContext();
        mConnI = new ConnectionIntelligence(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On start we launch the timer at the given periodicity.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimer.scheduleAtFixedRate(new MainTask(), 0, PERIOD);

        return START_STICKY;
    }
}
