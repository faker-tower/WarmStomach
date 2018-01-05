package com.example.axiang.warmstomach;

import android.support.multidex.MultiDex;
import 	android.support.multidex.MultiDexApplication;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.example.axiang.warmstomach.util.SharedPreferencesUtil;
import com.zxy.tiny.Tiny;

import cn.bmob.sms.BmobSMS;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by a2389 on 2017/12/2.
 */

public class WarmStomachApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        // 设置是否夜间主题
        if (SharedPreferencesUtil.getSharedPreferences().getBoolean(C.IF_NIGHT_MODE, false)) {
            // 设置为夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // 设置为白天模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 初始化BmobSDK
        BmobConfig config =new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId(C.BMOB_APPLICATION_ID)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024*1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(7 * 24 * 60 * 60)
                .build();
        Bmob.initialize(config);
        BmobSMS.initialize(this, C.BMOB_APPLICATION_ID);

        // 初始化Tiny
        Tiny.getInstance().init(this);

        // 重新定位
        SharedPreferencesUtil.getSharedPreferences()
                .edit()
                .putString(C.LONGITUDE, "")
                .putString(C.LATITUDE, "")
                .commit();
    }

    public static Context getInstance() {
        return mContext;
    }
}
