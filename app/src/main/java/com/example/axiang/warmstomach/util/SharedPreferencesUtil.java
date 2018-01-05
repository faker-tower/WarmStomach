package com.example.axiang.warmstomach.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.axiang.warmstomach.WarmStomachApplication;

/**
 * Created by a2389 on 2017/12/16.
 */

public class SharedPreferencesUtil {

    private static SharedPreferences sp;

    public static SharedPreferences getSharedPreferences() {
        if (sp == null) {
            sp = PreferenceManager
                    .getDefaultSharedPreferences(WarmStomachApplication.getInstance());
        }
        return sp;
    }
}
