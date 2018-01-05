package com.example.axiang.warmstomach.util;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.data.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by a2389 on 2017/12/15.
 */

public class VertifyUtil {

    public static boolean vertifyPhone(String phoneNumber) {
        return phoneNumber
                .matches("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$");
    }

    public static boolean vertifyPassword(String password) {
        return password
                .matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9a-zA-Z]{8,16}$");
    }
}
