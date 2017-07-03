package com.lyzde.app.wifiloginhelper;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpWrap extends AsyncTask<Void, Void, HttpWrap.HttpResult> {

    public String url;
    public enum Method {
        GET,
        POST
    }
    public Method method;

    public String postData;

    public class HttpResult {
        public int responseCode;
        public String content;
    }

    @Override
    protected HttpResult doInBackground(Void... params) {
        HttpResult hr = new HttpResult();
        try {
            URL url = new URL(this.url);
            HttpURLConnection conn;
            if (url.getProtocol().equals("https")) {
                conn = (HttpsURLConnection) url.openConnection();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setReadTimeout(10000); // 10 seconds
            conn.setConnectTimeout(10000); // 10 seconds
            if (method == Method.GET) {
                conn.setDoInput(false);
                conn.setRequestMethod("GET");
            } else if (method == Method.POST) {
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
            } else {
                throw new Exception("Unimplemented method for http.");
            }

            conn.setDoOutput(true);

            if (method == Method.POST) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();
            }

            hr.responseCode = conn.getResponseCode();
            hr.content = "";
            if (hr.responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    hr.content += line;
                }
            }
        } catch (Exception e) {
            Log.e("HttpWrap", e.getMessage());
            hr.responseCode = -1;
        }
        return hr;
    }

    @Override
    protected void onPostExecute(HttpResult result) {
        LoginService.getInstance().showResult(result);
        if (result.responseCode == HttpURLConnection.HTTP_OK) {
            Log.i("HttpWrap", "Http request done.");
            Log.i("HttpWrap", "Content is as followed:");
            Log.i("HttpWrap", result.content);
        } else {
            Log.i("HttpWrap", "Unexpected http response code " + result.responseCode);
        }
    }
}
