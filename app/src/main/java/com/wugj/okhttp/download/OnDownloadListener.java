package com.wugj.okhttp.download;

/**
 * description:
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/11
 * </br>
 * version:
 */
public interface OnDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess();

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed();
}