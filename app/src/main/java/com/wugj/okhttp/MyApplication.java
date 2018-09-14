package com.wugj.okhttp;

import android.app.Application;
import android.content.Context;

import com.wugj.okhttp.request.https.CerConfig;

import java.io.IOException;
import java.io.InputStream;

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
        setCer();
    }

    public static Context getContext() {
        return context;
    }

    private void setCer(){
        // 添加https证书
        try {
            String[]  certFiles=this.getAssets().list("certs");
            if (certFiles != null) {
                for (String cert:certFiles) {
                    InputStream is = getAssets().open("certs/" + cert);
                    CerConfig.addCertificate(is); // 这里将证书读取出来，，放在配置中byte[]里
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
