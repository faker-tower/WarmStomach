package com.example.axiang.warmstomach.util;

import android.content.Context;
import android.widget.Toast;

import com.example.axiang.warmstomach.WarmStomachApplication;

/**
 * Created by a2389 on 2017/12/14.
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(WarmStomachApplication.getInstance(),
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
