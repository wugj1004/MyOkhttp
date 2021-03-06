package com.wugj.okhttp.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 判断app是否第一次启动
 */
public class RNDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.android.vending.INSTALL_REFERRER")) {
            SharedPreferences sharedPref = context.getSharedPreferences("sdevice-info", Context.MODE_PRIVATE);
            Editor editor = sharedPref.edit();
            editor.putString("installReferrer", intent.getStringExtra("referrer"));
            editor.commit();
        }
    }
}
