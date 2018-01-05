package com.example.axiang.warmstomach.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.WarmStomachApplication;

/**
 * Created by a2389 on 2017/12/2.
 */

public class NetWorkUtil {

    // 判断是否有网络连接
    public static boolean isNetWorkConnected() {
        Context context = WarmStomachApplication.getInstance();
        if (context != null) {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    // 判断Wifi是否可用
    public static boolean isWifiConnected() {
        Context context = WarmStomachApplication.getInstance();
        if (context != null) {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null && ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    // 判断Mobile是否可用
    public static boolean isMobileConnected() {
        Context context = WarmStomachApplication.getInstance();
        if (context != null) {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null && ConnectivityManager.TYPE_MOBILE == networkInfo.getType()) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    // 查询网络连接类型
    public static int getConnectedType() {
        Context context = WarmStomachApplication.getInstance();
        if (context != null) {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return C.UNKNOWN_NETWORK_TYPE;
    }

    // 获取NetworkInfo
    private static NetworkInfo getNetworkInfo(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo;
            }
        }
        return null;
    }
}
