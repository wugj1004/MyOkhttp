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
     * JSONArray.toString 转换为 json格式的字符串（字符串中间不会穿插反斜杠）
     * @param list
     * @return
     */
    public static JSONArray createJsonArray(List<Map<String,? extends Object>> list){
        if (null == list || 0 == list.size()){
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (Map<String,? extends Object> map :list){
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
     * JSONObject.toString 转换为 json格式的字符串（字符串中间不会穿插反斜杠）
     */
    public static JSONObject createJsonObject(Map<String, ? extends Object > map){
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


}
