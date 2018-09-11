package com.wugj.okhttp.json;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 根据类属性创建json串
 * @author wugj
 */
public class JsonByObject<T> {


    /**
     * 根据数组获取JSONArray类型字符串
     * @param objs
     * @return
     */
    public static String createJsonArrayStr(Object[] objs) {
        JSONArray jsonArray = createJsonArray(objs);
        if (null != jsonArray) {
            return jsonArray.toString();
        } else {
            return null;
        }
    }

    /**
     * 根据数组获取JSONArray
     * @param objs
     * @return
     */
    public static JSONArray createJsonArray(Object[] objs) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < objs.length; i++) {
            if (null != objs[i]) {
                jsonArray.put(createJsonObject(objs[i]));
            }
        }
        return jsonArray;
    }

    /**
     * 根据集合获取JSONArray类型字符串
     * @param arrayList
     * @return
     */
    public static String createJsonArrayStr(ArrayList<Object> arrayList) {
        JSONArray jsonArray = createJsonArray(arrayList);
        if (null != jsonArray) {
            return jsonArray.toString();
        } else {
            return null;
        }
    }

    /**
     * 根据集合获取JSONArray
     * @param arrayList
     * @return
     */
    public static JSONArray createJsonArray(ArrayList<Object> arrayList) {
        JSONArray jsonArray = new JSONArray();
        for (Object obj : arrayList) {
            if (null != obj) {
                jsonArray.put(createJsonObject(obj));
            }
        }
        return jsonArray;
    }

    /**
     * Object 转换 JSONObject类型字符串
     * @param model
     * @return
     */
    public static String createjson(Object model) {
        JSONObject jsonObject = createJsonObject(model);
        if (null != jsonObject) {
            return jsonObject.toString();
        } else {
            return null;
        }
    }

    /**
     * Object 转换 JSONObject
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
}
