package com.wugj.okhttp.upload;

import com.wugj.okhttp.request.ReqCallBack;

public interface UploadCallBack<T>  extends ReqCallBack<T> {
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);

    /**
     * 响应成功
     */
    void onUploadSuccess(T result);

    /**
     * 响应失败
     */
    void onUploadFailed(String errorMsg);
}