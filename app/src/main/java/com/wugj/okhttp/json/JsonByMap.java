package com.wugj.okhttp.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 根据map创建对应的json对象
 * @author wugj
 *
 */
public class JsonByMap {


    /**
     * List<Map<String,String>> 转换 JSONArray
     * @param list
     * @return
     */
    public static JSONArray createJsonArray(List<Map<String,Object>> list){
        if (null == list || 0 == list.size()){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (Map<String,Object> map :list){
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
     * Map<String,Object> 转换 JSONObject
     * @param map
     * @return
     */
    public static JSONObject createJsonObject(Map<String,Object> map){
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

    /*public static JSONObject createJsonObject(Map<String,String> map){
        JSONObject jsonObject = new JSONObject();
        for (String key : map.keySet()){
            try {
                jsonObject.put(key,map.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }*/



}
