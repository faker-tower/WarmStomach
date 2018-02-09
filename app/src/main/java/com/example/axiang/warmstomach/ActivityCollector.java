package com.example.axiang.warmstomach;

import android.app.Activity;

import com.example.axiang.warmstomach.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a2389 on 2018/2/5.
 */

public class ActivityCollector {

    private static List<Activity> mActivities = new ArrayList<>();

    public static void add(Activity activity) {
        mActivities.add(activity);
    }

    public static void remove(Activity activity) {
        mActivities.remove(activity);
    }

    public static void finishAll() {
        SharedPreferencesUtil.getSharedPreferences().edit()
                .putString(C.LONGITUDE, "")
                .putString(C.LATITUDE, "")
                .commit();
        for (Activity activity : mActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
