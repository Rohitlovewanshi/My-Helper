package com.project.myhelper;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin : enabled", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor ed=context.getSharedPreferences("Admin",Context.MODE_PRIVATE).edit();
        ed.putBoolean("enabled",true);
        ed.apply();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin : disabled", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor ed=context.getSharedPreferences("Admin",Context.MODE_PRIVATE).edit();
        ed.putBoolean("enabled",false);
        ed.apply();
    }
}
