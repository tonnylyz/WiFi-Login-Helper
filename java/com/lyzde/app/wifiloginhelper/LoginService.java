package com.lyzde.app.wifiloginhelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;

public class LoginService extends Service {
    private static LoginService ls;
    public static LoginService getInstance() {
        return ls;
    }

    private static final String url = "https://gw.buaa.edu.cn:801/include/auth_action.php";
    private static final String essid = "Gigabyte";
    private static final String essid_buaa = "BUAA-WiFi";

    public String username;
    public String password;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info.getSSID().contains(essid) || info.getSSID().contains(essid_buaa)) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    login(username, password);
                }
            }
        }
    };

    public static void login(String username, String password) {
        if (username != null && password != null) {
            HttpWrap hw = new HttpWrap();
            hw.url = url;
            hw.method = HttpWrap.Method.POST;
            hw.postData = "action=login";
            hw.postData += "&username=" + username;
            hw.postData += "&password={B}" + Base64.encodeToString(password.getBytes(), Base64.URL_SAFE);
            hw.postData += "&ac_id=1";
            hw.postData += "&save_me=0";
            hw.postData += "&ajax=1";

            Log.i("LoginService", hw.postData);

            hw.execute();
        }
    }


    public void showResult(HttpWrap.HttpResult result) {
        if (result.responseCode == HttpURLConnection.HTTP_OK) {
            if (result.content.startsWith("login_ok")) {
                Toast.makeText(getApplication(), R.string.login_succeed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplication(), result.content, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplication(), R.string.network_error + " : " + result.responseCode, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        Log.i("LoginService", "Service started!");
        ls = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(mReceiver, filter);

        SharedPreferences credential = getApplicationContext()
                .getSharedPreferences("credential", Context.MODE_PRIVATE);

        String username = credential.getString("username", "");
        String password = credential.getString("password", "");

        if (!username.isEmpty() && !password.isEmpty()) {
            this.username = username;
            this.password = password;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        Log.i("LoginService", "Service ended!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        LoginService getService() {
            return LoginService.this;
        }
    }
}
