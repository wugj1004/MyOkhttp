package com.wugj.okhttp.json;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 定义适应服务器端解析数组
 * @author wugj
 *
 */
public class ArrayJsonFactory <T>{

    private static ArrayJsonFactory arrayJsonFactory;
    public static ArrayJsonFactory getInstance(){
        if (null == arrayJsonFactory){
            synchronized (ArrayJsonFactory.class){
                if (null == arrayJsonFactory)
                    arrayJsonFactory = new ArrayJsonFactory();
            }
        }

        return arrayJsonFactory;
    }

    public static String createArrayJson(Object[] objs) {
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        String array = "[";
        for (int i = 0; i < objs.length; i++) {
            if (null != objs[i]) {
                String jsonStr = JsonByObject.createjson(objs[i]);
                array += jsonStr+",";
            }
        }
        if (isEmpty(array)) {
            return null;
        }
        if (array.length() <3 ) {
            return null;
        }
        String jsonArray = array.substring(0, array.length()-1) +"]";
        if (!isEmpty(jsonArray)) {
            return jsonArray;
        }else{
            return null;
        }
    }


    public static JSONArray createArrayJsons(Object[] objs) {
        JSONArray jsonArray = new JSONArray();
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        for (int i = 0; i < objs.length; i++) {
            if (null != objs[i]) {
                jsonArray.put(JsonByObject.createJsonObject(objs[i]));
            }
        }
        return jsonArray;
    }

    public JSONArray createArrayJsons(List<Object> arrayList) {
        JSONArray jsonArray = new JSONArray();
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        for (Object obj : arrayList) {
            if (null != obj) {
                jsonArray.put(JsonByObject.createJsonObject(obj));
            }
        }
        return jsonArray;
    }

    /**
     * 便利集合map拼接jsonarray
     * @param list <Map<String,String>>  list
     * @return
     */
    public static JSONArray createJsonArray(List<Map<String,String>> list){
        if (null == list || 0 == list.size()){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (Map<String,String> map :list){
            JSONObject jsonObject = new JSONObject();
            for (String key : map.keySet()){
                try {
                    jsonObject.put(key,map.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /**
     * map创建json串
     * @param map
     * @return
     */
    public static JSONObject createJSON(Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        for (String key : map.keySet()){
            try {
                jsonObject.put(key,map.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
    public static JSONObject createJSONStr(Map<String,String> map){
        JSONObject jsonObject = new JSONObject();
        for (String key : map.keySet()){
            try {
                jsonObject.put(key,map.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }



    public static boolean isEmpty(String arg0) {
        if (null == arg0 || TextUtils.isEmpty(arg0) || arg0.equals("[]")
                || arg0.equals("null") || arg0.equals(" ")) {
            return true;
        } else {
            return false;
        }
    }
}
