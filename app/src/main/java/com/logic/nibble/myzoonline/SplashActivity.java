package com.logic.nibble.myzoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.logic.nibble.myzoonline.helper.MySession;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final MySession appSession = new MySession(SplashActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Log.d("Parth Debug", String.valueOf(appSession.getShop_id()));
                if (appSession.getShop_id() != "" && appSession.isIs_login()) {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        }, 1000);

    }
}
