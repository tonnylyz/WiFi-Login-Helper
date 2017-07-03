package com.lyzde.app.wifiloginhelper;

import android.app.Application;
import android.content.Intent;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, LoginService.class);
        startService(intent);
    }
}