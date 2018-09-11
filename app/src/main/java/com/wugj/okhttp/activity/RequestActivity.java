package com.wugj.okhttp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wugj.okhttp.R;
import com.wugj.okhttp.request.RequestManager;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Response;

/**
 * description:同步，异步网络请求
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/10
 * </br>
 * version:
 */
public class RequestActivity extends AppCompatActivity implements View.OnClickListener{

    private String actionUrl = "user/login";
    private static final String TAG = RequestActivity.class.getSimpleName();
    private RequestActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        activity = this;

        findViewById(R.id.req1).setOnClickListener(this);
        findViewById(R.id.req2).setOnClickListener(this);
        findViewById(R.id.req3).setOnClickListener(this);
        findViewById(R.id.req4).setOnClickListener(this);
        findViewById(R.id.req5).setOnClickListener(this);
        findViewById(R.id.req6).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        RequestManager requestManager = RequestManager.getInstance(RequestActivity.this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("user_name","2810962818@qq.com");//haozu1@haozu.com
        paramsMap.put("password","123456abc");//1234qwer

        switch (view.getId()){
            case R.id.req1:
                requestSyn(requestManager,paramsMap,RequestManager.TYPE_GET);
                break;
            case R.id.req2:
                requestSyn(requestManager,paramsMap,RequestManager.TYPE_POST_JSON);
                break;
            case R.id.req3:
                requestSyn(requestManager,paramsMap,RequestManager.TYPE_POST_FORM);
                break;
            case R.id.req4:

                break;
            case R.id.req5:

                break;
            case R.id.req6:

                break;
        }
    }

    /**
     * 接口调试不通，排查原因：1、请求头信息；2、请求体封装是否正确
     * @param requestManager
     * @param paramsMap
     * @param requestType
     */
    private void requestSyn(final RequestManager requestManager, final HashMap<String, String> paramsMap, final int requestType){
        new Thread(new Runnable(){
            @Override
            public void run() {
                final Response response= requestManager.requestSyn(actionUrl,requestType,paramsMap);
                //抛向主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            //获取返回数据 可以是String，bytes ,byteStream
                            try {
                                Toast.makeText(activity, "返回成功，body信息"+response.body().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }



}
