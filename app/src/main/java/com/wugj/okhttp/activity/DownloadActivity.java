package com.wugj.okhttp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wugj.okhttp.R;
import com.wugj.okhttp.download.DownloadManager;
import com.wugj.okhttp.download.OnDownloadListener;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/10
 * </br>
 * version:
 */
public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{

    String download_url = "https://hz-app.oss-cn-beijing.aliyuncs.com/update/zhushou/non_custom/officeb-release-4.3.1.apk";
    String Tag = DownloadActivity.class.getSimpleName();
    DownloadManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mDownloadManager = new DownloadManager(this);

        findViewById(R.id.down1).setOnClickListener(this);
        findViewById(R.id.down2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.down1:
                fileDownLoad();
                break;
            case R.id.down2:

                break;
        }
    }

    private void fileDownLoad(){
        mDownloadManager.downloadFile(download_url, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //下载完成

                    }
                });
            }

            @Override
            public void onDownloading(int progress) {
                Log.e(Tag,"下载进度:"+progress);

            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }


    private void showToast(String msg){
        Toast.makeText(DownloadActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
