package com.wugj.okhttp.request;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * description:网络请求管理工具
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/10
 * </br>
 * version:
 */
public class RequestManager {

    private static volatile RequestManager mInstance;//单利引用
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和M主线程通信

    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单

    //mdiatype 这个需要和服务端保持一致application/x-www-json-urlencoded; charset=utf-8
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String BASE_URL = "https://spaceone-app.haozu.com";//请求接口根地址

    private static Context ctx;

    /**
     * 初始化RequestManager
     */
    public RequestManager(Context context) {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
        //初始化Handler
        okHttpHandler = new Handler(context.getMainLooper());
    }

    /**
     * 获取单例引用
     * @return
     */
    public static RequestManager getInstance(Context context) {
        ctx = context;
        RequestManager inst = mInstance;
        //双重锁判定，防止多线程调用时发生错乱
        if (inst == null) {
            synchronized (RequestManager.class) {
                inst = mInstance;
                if (inst == null) {
                    //context.getApplicationContext()防止内存泄漏
                    inst = new RequestManager(context.getApplicationContext());
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 统一为请求添加头信息
     * @return
     */
    private Request.Builder addHeaders() {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        Map<String, Object> deviceMap = deviceInfo.getConstants();

        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "application/json")
                .addHeader("imei", (String) deviceMap.get("uniqueId"))
                .addHeader("appVersion", "2.0.1")
                .addHeader("screenSize", "480*800")
                .addHeader("deviceInfo", (String) deviceMap.get("brand")+(String) deviceMap.get("model"))
                .addHeader("systemVersion", Build.VERSION.RELEASE);
        return builder;
    }


    /*--------------------------------同步请求--------开始--------------------------------*/
    /**
     *  okHttp同步请求统一入口
     * @param actionUrl  接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     */
    public Response requestSyn(String actionUrl, int requestType, HashMap<String, String> paramsMap) {
        switch (requestType) {
            case TYPE_GET:
                return requestGetBySyn(actionUrl, paramsMap);

            case TYPE_POST_JSON:
                return requestPostBySyn(actionUrl, paramsMap);

            case TYPE_POST_FORM:
                return requestPostBySynWithForm(actionUrl, paramsMap);

            default:
                return null;
        }

    }

    /**
     * okHttp get同步请求
     * @param actionUrl  接口地址
     * @param paramsMap   请求参数
     */
    private Response requestGetBySyn(String actionUrl, HashMap<String, String> paramsMap) {
        Response response = null;
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            response = call.execute();
            /*if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                Log.e(TAG, "response ----->" + response.body().string());
            }*/
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return response;
    }

    /**
     * okHttp post同步请求
     * @param actionUrl  接口地址
     * @param paramsMap   请求参数
     */
    private Response requestPostBySyn(String actionUrl, HashMap<String, String> paramsMap) {
        Response response = null;
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }

            //生成参数
            String params = tempParams.toString();

            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            response = call.execute();
            //请求执行成功
            /*if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                Log.e(TAG, "response ----->" + response.body().string());
            }*/
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return response;
    }

    /**
     * okHttp post同步请求表单提交
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private Response requestPostBySynWithForm(String actionUrl, HashMap<String, String> paramsMap) {
        Response response = null;
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
            }
            //生成表单实体对象
            RequestBody formBody = builder.build();
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            response = call.execute();
            /*if (response.isSuccessful()) {
                Log.e(TAG, "response ----->" + response.body().string());
            }*/
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return response;
    }

    /*--------------------------------同步请求--------结束--------------------------------*/


    /*--------------------------------异步请求--------开始--------------------------------*/
    /**
     * okHttp异步请求统一入口
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     **/
    public <T> Call requestAsyn(String actionUrl, int requestType, HashMap<String, String> paramsMap, ReqCallBack<T> callBack) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsyn(actionUrl, paramsMap, callBack);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsyn(actionUrl, paramsMap, callBack);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(actionUrl, paramsMap, callBack);
                break;
        }
        return call;
    }


    /**
     * okHttp get异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestGetByAsyn(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            final Request request = addHeaders().url(requestUrl).build();
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
     * okHttp post异步请求
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsyn(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String params = tempParams.toString();
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            final Request request = addHeaders().url(requestUrl).post(body).build();
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
     * okHttp post异步请求表单提交
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param callBack 请求返回数据回调
     * @param <T> 数据泛型
     * @return
     */
    private <T> Call requestPostByAsynWithForm(String actionUrl, HashMap<String, String> paramsMap, final ReqCallBack<T> callBack) {
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

    /*--------------------------------异步请求--------结束--------------------------------*/
}
