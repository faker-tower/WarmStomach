package com.example.axiang.warmstomach.util;

import android.widget.Toast;

import com.example.axiang.warmstomach.WarmStomachApplication;

/**
 * Created by a2389 on 2017/12/14.
 */

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(WarmStomachApplication.getInstance(),
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }
}
