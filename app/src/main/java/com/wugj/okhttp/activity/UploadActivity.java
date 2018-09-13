package com.wugj.okhttp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wugj.okhttp.R;
import com.wugj.okhttp.upload.UploadCallBack;
import com.wugj.okhttp.upload.UploadManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    File outputImagepath;

    public static final int TAKE_PHOTO = 1;//启动相机标识
    public static final int SELECT_PHOTO = 2;//启动相册标识
    public static final int CREATE_DIR = 3;//启动相册标识



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
        switch (view.getId()) {
            case R.id.up1:
                showDialog();
                break;
        }
    }


    /**-----------------------------调用系统相机-----------开始-----------------------------**/
    private void showDialog() {
        final String[] items = new String[]{"拍照"};//创建item
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("选择照片")
                .setIcon(R.mipmap.ic_launcher)
                .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                cameraPhoto();
                                break;
                            case 1:

                                break;
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    private void cameraPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //权限发生了改变 true  //  false,没有权限时
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this).setTitle("title")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 请求授权
                                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //不请求权限的操作
                    }
                }).create().show();


            } else {
                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            /*已经授权了就调用打开相机的方法
            获取系統版本
             判断存储卡是否可以用，可用进行存储*/
            if (hasSdcard()) {
                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{android
                        .Manifest.permission.WRITE_EXTERNAL_STORAGE}, CREATE_DIR);

            }
        }
    }


    /*
   * 判断sdcard是否被挂载
   */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case CREATE_DIR:
                File storageDir;
                //文件路径是公共的DCIM目录下的/camerademo目录
                String storagePath = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + File.separator + "camera";
                storageDir = new File(storagePath);

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        if (!storageDir.exists()) {
                            storageDir.mkdirs();
                        }

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        try {
                            outputImagepath = File.createTempFile(timeStamp, ".jpg", storageDir);
                            Log.e("UploadActivity", outputImagepath.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            // 从文件中创建uri
                            Uri uri = Uri.fromFile(outputImagepath);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        } else {
                            //兼容android7.0 使用共享文件的形式
                            Uri uri = FileProvider.getUriForFile(UploadActivity.this,
                                    "com.wugj.okhttp.fileProvider", outputImagepath);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        }
                        startActivityForResult(intent, TAKE_PHOTO);


                    }
                    break;
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //打开相机后返回
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    /**
                     * 这种方法是通过内存卡的路径进行读取图片，所以的到的图片是拍摄的原图
                     */
                    Log.i("tag", "拍照图片路径>>>>" + outputImagepath);
                    uploadFile();
                }
                break;
        }
    }

    /**-----------------------------调用系统相机-----------结束-----------------------------**/

    private void uploadFile() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("versionId", "4.3.1");
        paramsMap.put("token", "YUhwMFpYTjBZbW8xJE1UVXpOamMwTmprMk55NHpNVEkzJGE3MzU5ODBmZDA5YmVkODAwMTIwYmE1NWVmNWM1ZDYx");
        paramsMap.put("uuid", "865970034723757");
        paramsMap.put("devicename", "MHA-AL00");
        paramsMap.put("type", "1");
        paramsMap.put("file", outputImagepath);


        uploadManager.upLoadFile(actionUrl, paramsMap, new UploadCallBack<String>() {
            @Override
            public void onReqSuccess(String result) {
                Toast.makeText(UploadActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(UploadActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onProgress(long total, long current) {
                int progress = (int) (current*100/total);
                pro1.setProgress(progress,true);
                tvPro1.setText(progress+"%");
            }
        });
    }

}
