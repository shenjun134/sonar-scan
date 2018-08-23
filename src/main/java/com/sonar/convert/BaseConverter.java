package com.sonar.convert;

import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.sonar.wsclient.JdkUtils;

import java.util.Date;

public class BaseConverter {

    protected static JdkUtils jdkUtils = new JdkUtils();

    protected static int getInt(JSONObject jsonObj, String key) {
        Object o = jsonObj.get(key);
        if (o == null) {
            return 0;
        }
        return NumberUtils.toInt(o.toString(), 0);
    }

    protected static String getString(JSONObject jsonObj, String key) {
        Object o = jsonObj.get(key);
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    protected static boolean getBoolean(JSONObject jsonObj, String key) {
        Object o = jsonObj.get(key);
        if (o == null) {
            return false;
        }
        return Boolean.valueOf(o.toString());
    }

    protected static Date getDate(JSONObject jsonObj, String key) {
        return jdkUtils.getDateTime(jsonObj, key);
    }
}
