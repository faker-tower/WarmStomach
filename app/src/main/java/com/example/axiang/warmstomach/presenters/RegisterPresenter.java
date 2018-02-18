package com.example.axiang.warmstomach.presenters;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.contracts.RegisterContract;
import com.example.axiang.warmstomach.data.User;
import com.example.axiang.warmstomach.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by a2389 on 2017/12/15.
 */

public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mView;

    private boolean phoneNumberExist = false;

    @Override
    public void start() {
        mView.initView();
    }

    @Override
    public void setView(RegisterContract.View view) {
        this.mView = view;
    }

    @Override
    public boolean phoneNumberIsRegistered(String phoneNumber) {
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        userBmobQuery.addWhereEqualTo(C.MOBILE_PHONE_NUMBER, phoneNumber)
                .setLimit(1)
                .setSkip(0)
                .findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null && !list.isEmpty()) {
                            phoneNumberExist = true;
                        }
                    }
                });
        return phoneNumberExist;
    }

    @Override
    public void saveData(String name,
                         String password,
                         String mobilePhoneNumber) {
        // 上传到Bmob数据库
        User user = new User();
        user.setUsername(mobilePhoneNumber);
        user.setPassword(password);
        user.setMobilePhoneNumber(mobilePhoneNumber);
        user.setMobilePhoneNumberVerified(true);
        user.setName(name);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    mView.registerSuccess();
                } else {
                    if (!NetWorkUtil.isNetWorkConnected()) {
                        mView.showNetWorkError();
                    }
                    mView.RegisterFailed();
                }
            }
        });
    }
}
