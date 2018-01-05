package com.example.axiang.warmstomach.presenters;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.contracts.LoginContract;
import com.example.axiang.warmstomach.data.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by a2389 on 2017/12/17.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;

    private boolean phoneNumberExist = true;

    @Override
    public void start() {
        view.initView();
    }

    @Override
    public void setView(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public boolean phoneNumberRegistered(String phoneNumber) {
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        userBmobQuery.addWhereEqualTo(C.MOBILE_PHONE_NUMBER, phoneNumber);
        userBmobQuery.setLimit(1);
        userBmobQuery.setSkip(0);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e != null || list.isEmpty()) {
                    phoneNumberExist = false;
                }
            }
        });
        return phoneNumberExist;
    }

    @Override
    public void loginByVertify(String phoneNumber, String vertify) {
        BmobUser.loginBySMSCode(phoneNumber, vertify, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    view.loginSuccess();
                } else {
                    view.loginFailed();
                }
            }
        });
    }

    @Override
    public void loginByPasssword(String phoneNumber, String password) {
        BmobUser.loginByAccount(phoneNumber, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    view.loginSuccess();
                } else {
                    view.loginFailed();
                }
            }
        });
    }
}
