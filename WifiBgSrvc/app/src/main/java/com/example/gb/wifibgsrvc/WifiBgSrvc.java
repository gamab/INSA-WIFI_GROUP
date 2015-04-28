package com.example.gb.wifibgsrvc;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gb on 28/04/15.
 */
public class WifiBgSrvc extends Service {

    private static int periodicity = 5000;

    private static Timer mTimer = new Timer();
    private Context mCtx;

    //Le handler affiche une notification indiquant pour l'instant que le service est lancé.
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentText("Wifi Service Launched")
                    .setContentTitle("Wifi");
            NotificationManager manager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
    };


    //La tache Maintask envoie un message vide au Handler pour le réveiller.
    private class MainTask extends TimerTask {
        public void run()
        {
            mHandler.sendEmptyMessage(0);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //Création du toast//
        mCtx = getApplicationContext();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mTimer.scheduleAtFixedRate(new MainTask(), 0, periodicity);


        return START_STICKY;
    }
}
