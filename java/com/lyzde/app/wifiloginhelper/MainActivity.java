package com.lyzde.app.wifiloginhelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Main", "Init started.");
        setContentView(R.layout.activity_main);
        Button mSaveButton = (Button) findViewById(R.id.save);
        Button mLoginButton = (Button) findViewById(R.id.login);
        final EditText mUsernameEdit = (EditText) findViewById(R.id.username);
        final EditText mPasswordEdit = (EditText) findViewById(R.id.password);

        SharedPreferences credential = getApplicationContext()
                .getSharedPreferences("credential", Context.MODE_PRIVATE);

        final String username = credential.getString("username", "");
        final String password = credential.getString("password", "");

        if (!username.isEmpty() && !password.isEmpty()) {
            mUsernameEdit.setText(username);
            mPasswordEdit.setText(password);
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences credential = getApplicationContext()
                        .getSharedPreferences("credential", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = credential.edit();
                editor.putString("username", mUsernameEdit.getText().toString());
                editor.putString("password", mPasswordEdit.getText().toString());
                editor.apply();

                LoginService.getInstance().username = mUsernameEdit.getText().toString();
                LoginService.getInstance().password = mPasswordEdit.getText().toString();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginService.login(mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString());
            }
        });
    }
}
