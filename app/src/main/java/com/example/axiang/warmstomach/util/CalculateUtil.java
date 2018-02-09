package com.example.axiang.warmstomach.util;

import com.example.axiang.warmstomach.C;

/**
 * Created by a2389 on 2018/1/1.
 */

public class CalculateUtil {

    private static double rad(double d){
        return d * Math.PI / 180.0;
    }

    // Google给出根据经纬度计算两点距离的公式,返回单位为米
    public static double getDistance(double lon1,double lat1,double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2),2)));
        s = s * C.EARTH_RADIUS;
        return s;
    }
}
