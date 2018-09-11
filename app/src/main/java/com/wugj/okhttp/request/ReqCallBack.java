package com.wugj.okhttp.request;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/10
 * </br>
 * version:
 */
public interface ReqCallBack<T> {
    /**
     * 响应成功
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     */
    void onReqFailed(String errorMsg);
}