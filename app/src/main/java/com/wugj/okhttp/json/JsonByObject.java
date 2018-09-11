package com.wugj.okhttp.json;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 根据data类创建对应的Json对象；（受创建类的get方法限制）
 * @author wugj
 */
public class JsonByObject {

    /**
     * Object[] 转换 JSONArray；依赖对象的get方法
     * @param objs
     * @return
     */
    public static JSONArray createJsonArray(Object[] objs) {
        JSONArray jsonArray = new JSONArray();
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        for (int i = 0; i < objs.length; i++) {
            if (null != objs[i]) {
                jsonArray.put(createJsonObject(objs[i]));
            }
        }
        return jsonArray;
    }


    /**
     * ArrayList<Object> 转换 JSONArray；依赖对象的get方法
     * @param arrayList
     * @return
     */
    public static JSONArray createJsonArray(ArrayList<Object> arrayList) {
        JSONArray jsonArray = new JSONArray();
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        for (Object obj : arrayList) {
            if (null != obj) {
                jsonArray.put(createJsonObject(obj));
            }
        }
        return jsonArray;
    }

    /**
     * Object 转换 JSONObject；依赖对象的get方法
     * @param model
     * @return
     */
    public static JSONObject createJsonObject(Object model) {
        JSONObject jsonObject = new JSONObject();
        try {
            Field[] field = model.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
            for (int j = 0; j < field.length; j++) {
                String name = field[j].getName(); // 获取属性的名字
                String nameArg0 = name.substring(0, 1).toUpperCase()
                        + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m;

                    m = model.getClass().getMethod("get" + nameArg0);
                    String value = (String) m.invoke(model); // 调用getter方法获取属性值
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("class java.lang.Integer")
                        || type.equals("int")) {
                    Method m = model.getClass().getMethod("get" + nameArg0);
                    Integer value = (Integer) m.invoke(model);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("class java.lang.Short")) {
                    Method m = model.getClass().getMethod("get" + nameArg0);
                    Short value = (Short) m.invoke(model);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("class java.lang.Double")) {
                    Method m = model.getClass().getMethod("get" + nameArg0);
                    Double value = (Double) m.invoke(model);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("class java.lang.Boolean")) {
                    Method m = model.getClass().getMethod("get" + nameArg0);
                    Boolean value = (Boolean) m.invoke(model);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
                if (type.equals("class java.util.Date")) {
                    Method m = model.getClass().getMethod("get" + nameArg0);
                    Date value = (Date) m.invoke(model);
                    if (value != null) {
                        jsonObject.put(name, value);
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 对象 转换 JSONObject 转换 String
     * @param objs
     * @return
     */
    public static String createJsonString(Object[] objs) {
        //jsonarray会带有反斜杠 ，所以采用拼接字符串方式
        String array = "[";
        for (int i = 0; i < objs.length; i++) {
            if (null != objs[i]) {
                String jsonStr = createJsonObject(objs[i]).toString();
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



    private static boolean isEmpty(String arg0) {
        if (null == arg0 || TextUtils.isEmpty(arg0) || arg0.equals("[]")
                || arg0.equals("null") || arg0.equals(" ")) {
            return true;
        } else {
            return false;
        }
    }
}
