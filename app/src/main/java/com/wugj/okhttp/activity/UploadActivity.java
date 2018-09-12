package com.wugj.okhttp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wugj.okhttp.R;
import com.wugj.okhttp.upload.UploadCallBack;
import com.wugj.okhttp.upload.UploadManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传
 */
public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar pro1;
    TextView tvPro1;
    UploadManager uploadManager;
    private String actionUrl = "upload/image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);
        uploadManager = UploadManager.getInstance(this);
        findViewById(R.id.up1).setOnClickListener(this);
        pro1 = findViewById(R.id.pro1);
        tvPro1 = findViewById(R.id.tvPro1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.up1:

                Map<String,String> paramsMap = new HashMap<>();
                paramsMap.put("versionId","4.3.1");
                paramsMap.put("token","YUhwMFpYTjBZbW8xJE1UVXpOamMwTmprMk55NHpNVEkzJGE3MzU5ODBmZDA5YmVkODAwMTIwYmE1NWVmNWM1ZDYx");
                paramsMap.put("uuid","865970034723757");
                paramsMap.put("devicename","MHA-AL00");
                paramsMap.put("type","1");


                uploadManager.upLoadFile(actionUrl, paramsMap, new UploadCallBack<String>() {
                    @Override
                    public void onReqSuccess(String result) {

                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }

                    @Override
                    public void onProgress(long total, long current) {

                    }

                });
                break;
        }
    }
}
