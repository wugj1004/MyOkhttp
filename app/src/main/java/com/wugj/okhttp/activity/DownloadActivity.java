package com.wugj.okhttp.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wugj.okhttp.R;
import com.wugj.okhttp.download.DownLoadObserver;
import com.wugj.okhttp.download.DownloadInfo;
import com.wugj.okhttp.download.DownloadManager;
import com.wugj.okhttp.download.OnDownloadListener;
import com.wugj.okhttp.download.PointDownloadManager;

import org.w3c.dom.Text;

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
    PointDownloadManager pointDownloadManager;


    ProgressBar pro1,pro2;
    TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mDownloadManager = new DownloadManager(this);
        pointDownloadManager = PointDownloadManager.getInstance(this);


        findViewById(R.id.down1).setOnClickListener(this);
        findViewById(R.id.down2).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        pro1 = findViewById(R.id.pro1);
        pro2 = findViewById(R.id.pro2);
        text2 = findViewById(R.id.text2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.down1:
                normalDownLoad();
                break;
            case R.id.down2:
                pointDownload();
                break;
            case R.id.cancel:
                pointDownloadManager.cancel(download_url);
                break;
        }
    }

    /**
     * 普通文件下载
     */
    private void normalDownLoad(){
        mDownloadManager.downloadFile(download_url, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //下载完成
                        showToast("下载完成"+mDownloadManager.getFileUri().toString());
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDownloading(int progress) {
                pro1.setProgress(progress,true);


            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("下载失败");
                    }
                });
            }
        });
    }

    /**
     * 断点下载
     */
    private void pointDownload(){
        pointDownloadManager.downloadFile(download_url, new DownLoadObserver() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(DownloadInfo value) {
                super.onNext(value);
                pro2.setMax((int) value.getTotal());
                text2.setText((value.getProgress()*100)/(value.getTotal()) + "%");
                pro2.setProgress((int) value.getProgress(),true);

            }

            @Override
            public void onComplete() {
                if (downloadInfo != null) {
                    Toast.makeText(DownloadActivity.this,
                            downloadInfo.getFileName() + "-DownloadComplete",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showToast(String msg){
        Toast.makeText(DownloadActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
