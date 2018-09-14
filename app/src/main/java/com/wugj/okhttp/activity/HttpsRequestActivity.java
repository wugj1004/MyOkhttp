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

        private String actionUrl = "auth/nlogin";
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
        paramsMap.put("username","hztestbj5");
        paramsMap.put("password","72c698069b6e2c0518eeffd6798f61dd");


        requestManager.requestPostByAsynWithForm(actionUrl,paramsMap,new ReqCallBack<Object>(){
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
