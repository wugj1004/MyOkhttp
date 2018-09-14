package com.wugj.okhttp.request.https;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wugj.okhttp.request.ReqCallBack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/14
 * </br>
 * version:
 */
public class HttpsRequestManager {

    private static volatile HttpsRequestManager mInstance;//单利引用
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "https://mob.haozu.com";//请求接口根地址

    private static final String TAG = HttpsRequestManager.class.getSimpleName();

    /**
     * 初始化RequestManager
     */
    public HttpsRequestManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = trustHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static HttpsRequestManager getInstance(Context context) {
        HttpsRequestManager inst = mInstance;
        //双重锁判定，防止多线程调用时发生错乱
        if (inst == null) {
            synchronized (HttpsRequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    //context.getApplicationContext()防止内存泄漏
                    inst = new HttpsRequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /*--------------------------------证书重点--------开始--------------------------------*/
    /**
     * 获取证书定义HttpClient
     *
     * @return
     */
    public OkHttpClient trustHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 添加证书
        List<InputStream> certificates = new ArrayList<>();
        List<byte[]> certs_data = CerConfig.getCertificatesData();
        // 将字节数组转为数组输入流
        if (certs_data != null && !certs_data.isEmpty()) {
            for (byte[] bytes : certs_data) {
                certificates.add(new ByteArrayInputStream(bytes));
            }
        }
        SSLSocketFactory sslSocketFactory = CerConfig.getSocketFactory(certificates);
        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory);
        }
        return builder.build();
    }

    /**
     * 对外提供的获取支持自签名的okhttp客户端
     * @param certificates 自签名证书的输入流
     * @return 支持自签名的客户端
     */
    public OkHttpClient trustHttpClient(List<InputStream> certificates) {
        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{};
            int index = 0;
            for (InputStream certificate : certificates) {
                trustManager = CerConfig.trustManagerForCertificates(certificate);
                trustManagers[index] = trustManager;
                index++;
            }
            //使用构建出的trustManger初始化SSLContext对象
            sslContext.init(null, trustManagers, null);
            //获得sslSocketFactory对象
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }


        return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory).build();
    }



    /*--------------------------------证书重点--------结束--------------------------------*/


    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 7.0; MHA-AL00 Build/HUAWEIMHA-AL00)")
                .addHeader("devicename", "MHA-AL00")
                .addHeader("model", "HUAWEI/MHA-AL00")
                .addHeader("contentformat", "json2")
                .addHeader("clientAgent", "HUAWEI/MHA-AL00#1080*1812#3.0#7.0")
                .addHeader("versionId", "4.3.1")
                .addHeader("uuid", "865970034723757");

        return builder;
    }


    /**
     * okHttp post异步请求表单提交
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    public <T> Call requestPostByAsynWithForm(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            RequestBody formBody = builder.build();
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", callBack);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
                        successCallBack((T) string, callBack);
                    } else {
                        failedCallBack("服务器错误", callBack);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }



    /**
     * 统一同意处理成功信息
     * @param result
     * @param callBack
     * @param <T>
     */
    private <T> void successCallBack(final T result, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param callBack
     * @param <T>
     */
    private <T> void failedCallBack(final String errorMsg, final ReqCallBack<T> callBack) {
        okHttpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }


}
