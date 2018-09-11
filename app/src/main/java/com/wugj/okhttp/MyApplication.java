package com.wugj.okhttp;

import android.app.Application;
import android.content.Context;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/11
 * </br>
 * version:
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
