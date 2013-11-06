package com.wwc.jajing.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.wwc.jajing.R;

public class Splash extends Activity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstTime = prefs.getBoolean(getString(R.string.isFirstTime), true);
                if(isFirstTime){
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean(getString(R.string.isFirstTime), Boolean.FALSE);
                    edit.commit();
                    i = new Intent(Splash.this, HelpActivity.class);

                } else {
                    i = new Intent(Splash.this, MainActivity.class);
                }

                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}