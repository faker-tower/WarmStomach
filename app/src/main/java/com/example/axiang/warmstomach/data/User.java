package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobUser;

/**
 * Created by a2389 on 2017/12/10.
 */

public class User extends BmobUser {

    private String name;
    private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
