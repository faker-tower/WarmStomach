package com.example.axiang.warmstomach.contracts;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;

/**
 * Created by a2389 on 2017/12/16.
 */

public interface LoginContract {

    interface Presenter extends BasePresenter {

        void setView(View view);

        boolean phoneNumberRegistered(String phoneNumber);

        void loginByVertify(String phoneNumber, String vertify);

        void loginByPasssword(String phoneNumber, String password);
    }

    interface View extends BaseView<Presenter> {

        void loginSuccess();

        void loginFailed();

        void showNetWorkError();
    }
}
