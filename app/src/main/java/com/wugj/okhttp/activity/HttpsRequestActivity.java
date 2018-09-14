package com.wugj.okhttp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wugj.okhttp.R;
import com.wugj.okhttp.request.ReqCallBack;
import com.wugj.okhttp.request.https.HttpsRequestManager;

import java.util.HashMap;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/14
 * </br>
 * version:
 */
public class HttpsRequestActivity extends AppCompatActivity implements View.OnClickListener{

        private String actionUrl = "user/login";
        private static final String TAG = com.wugj.okhttp.activity.RequestActivity.class.getSimpleName();
        private com.wugj.okhttp.activity.RequestActivity activity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_request_https);

            findViewById(R.id.req1).setOnClickListener(this);
        }

    @Override
    public void onClick(View view) {
        HttpsRequestManager requestManager = HttpsRequestManager.getInstance(HttpsRequestActivity.this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("user_name","2810962818@qq.com");//haozu1@haozu.com
        paramsMap.put("password","123456abc");//1234qwer


        requestManager.requestPostByAsyn(actionUrl,paramsMap,new ReqCallBack<Object>(){
            @Override
            public void onReqSuccess(Object result) {
                Toast.makeText(HttpsRequestActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(HttpsRequestActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
