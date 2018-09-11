package com.wugj.okhttp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wugj.okhttp.activity.DownloadActivity;
import com.wugj.okhttp.activity.RequestActivity;
import com.wugj.okhttp.activity.RxRequestActivity;
import com.wugj.okhttp.activity.UploadActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        //网络请求
        findViewById(R.id.bt1).setOnClickListener(this);
        //文件下载
        findViewById(R.id.bt2).setOnClickListener(this);
        //文件上传
        findViewById(R.id.bt3).setOnClickListener(this);
        //配合RxJava网络请求
        findViewById(R.id.bt4).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.bt1:
                intent.setClass(activity, RequestActivity.class);
                break;
            case R.id.bt2:
                intent.setClass(activity, DownloadActivity.class);
                break;
            case R.id.bt3:
                intent.setClass(activity, UploadActivity.class);
                break;
            case R.id.bt4:
                intent.setClass(activity, RxRequestActivity.class);
                break;
        }
        startActivity(intent);
    }
}
