package com.example.gb.wifibackgroundservice;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class WifiBgSrvcLauncher extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_bg_srvc_launcher);
        startService(new Intent(this, com.example.gb.wifibackgroundservice.WifiBgSrvc.class));
    }
}
