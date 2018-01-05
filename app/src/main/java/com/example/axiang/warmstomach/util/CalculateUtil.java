package com.example.axiang.warmstomach.util;

import com.example.axiang.warmstomach.C;

/**
 * Created by a2389 on 2018/1/1.
 */

public class CalculateUtil {

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    // 通过经纬度计算距离
    public static double getDistance(double goalLongitude, double goalLatitude) {
        double longitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""));
        double latitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""));
        double radLat1 = rad(goalLatitude);
        double radLat2 = rad(latitude);
        double a = radLat1 - radLat2;
        double b = rad(goalLongitude) - rad(longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }
}
