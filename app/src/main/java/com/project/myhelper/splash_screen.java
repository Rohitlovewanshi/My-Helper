package com.project.myhelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new CountDownTimer(4000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                SharedPreferences pref=getSharedPreferences("isFirst",MODE_PRIVATE);
                if(pref.getBoolean("LoginActivity",true)) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                else{
                    startActivity(new Intent(getApplicationContext(),screenPinActivity.class));
                }
            }
        }.start();
    }
}
