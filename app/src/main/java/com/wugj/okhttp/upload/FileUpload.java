package com.wugj.okhttp.upload;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * Title:HttpURLConnection底层上传文件工具
 * <p>Description:</p>
 * Copyright (c) HeHuaChuan 2014
 * @author Administrator on 2015-8-1
 */
public final class FileUpload {
    public static final int connectTimeout = 60 * 1000;

    /**
     * Title:上传文件,只能单个文件上传
     * <p>Description:</p>
     * @author Administrator on 2015-8-1
     * @param urlstr 服务器地址
     * @param urlpars 关键值及值
     * @param keyValue 上传文件的关键值
     * @param path 上传文件的路径
     * @return 返回服务器响应的数据
     */
    public static String uploadFiles(String urlstr, List<NameValuePair> urlpars, String keyValue, String path){
        try {
            // 定义数据分隔线
            String BOUNDARY = "------------------------7dc2fd5c0894";
            // 定义最后数据分隔线
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            //conn.setRequestProperty("user-agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            conn.setConnectTimeout(connectTimeout);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            //关键字转换
            if (urlpars != null) {
                StringBuffer params = new StringBuffer();
                for (NameValuePair nv : urlpars) {
                    params.append("--" + BOUNDARY + "\r\n");
                    params.append("Content-Disposition: form-data; name=\""+nv.getName()+"\"\r\n\r\n");
                    params.append(nv.getValue());
                    params.append("\r\n");
                }
                out.write(params.toString().getBytes());
                params=null;
            }

            //文件流转换
            File file = new File(path);
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\""+keyValue+"\";filename=\""+ file.getName() + "\"\r\n");
            // 这里不能漏掉，根据文件类型来来做处理，由于上传的是图片，所以这里可以写成image/pjpeg
            sb.append("Content-Type:image/pjpeg\r\n\r\n");
            out.write(sb.toString().getBytes());
            sb=null;
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            int fileLength = (int)(file.length()+1);
            byte[] bufferOut = new byte[fileLength];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            bufferOut=null;
            file=null;
            out.write("\r\n".getBytes());
            in.close();
            in=null;
            //关闭
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            InputStreamReader inputStreamReader=new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            StringBuffer buffer=new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            inputStreamReader.close();
            Log.e("fileUpload","上传成功");
            if(buffer.toString()!=null||buffer.toString().equals("")){
                return buffer.toString();
            }else{
                return null;
            }

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Title:批量上传文件
     * <p>Description:</p>
     * @author Administrator on 2015-8-1
     * @param urlstr 服务器地址
     * @param urlpars 关键值及值
     * @param keyValue 上传文件的关键值
     * @param listPath 上传文件的路劲集
     * @return 返回服务器响应的数据
     * @return
     */
    public static String uploadBatchFiles(String urlstr,List<NameValuePair> urlpars,String keyValue,List<String> listPath) {
        try {
            // 定义数据分隔线
            String BOUNDARY = "------------------------7dc2fd5c0894";
            // 定义最后数据分隔线
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            //关键字转换
            if (urlpars != null) {
                StringBuffer params = new StringBuffer();
                for (NameValuePair nv : urlpars) {
                    params.append("--" + BOUNDARY + "\r\n");
                    params.append("Content-Disposition: form-data; name=\""+nv.getName()+"\"\r\n\r\n");
                    params.append(nv.getValue());
                    params.append("\r\n");
                }
                out.write(params.toString().getBytes());
                params=null;
            }

            //文件流转换
            for(String path:listPath){
                File file = new File(path);
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data;name=\""+keyValue+"\";filename=\""+ file.getName() + "\"\r\n");
                // 这里不能漏掉，根据文件类型来来做处理，由于上传的是图片，所以这里可以写成image/pjpeg
                sb.append("Content-Type:image/pjpeg\r\n\r\n");
                out.write(sb.toString().getBytes());
                sb=null;
                DataInputStream in = new DataInputStream(new FileInputStream(file));
                int bytes = 0;
                int fileLength = (int) (file.length()+1);
                byte[] bufferOut = new byte[fileLength];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                bufferOut=null;
                file=null;
                out.write("\r\n".getBytes());
                in.close();
                in=null;
            }
            //关闭
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            InputStreamReader inputStreamReader=new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            StringBuffer buffer=new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            inputStreamReader.close();
            if(buffer.toString()!=null||buffer.toString().equals("")){
                return buffer.toString();
            }else{
                return null;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
            return null;
        }
    }
}
