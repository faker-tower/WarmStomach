package com.example.axiang.warmstomach.contracts;

import android.content.Context;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;

import java.util.List;

/**
 * Created by a2389 on 2017/12/12.
 */

public interface RegisterContract {

    interface Presenter extends BasePresenter {

        void setView(View view);

        void saveData(String name,
                         String password,
                         String mobilePhoneNumber);

        boolean phoneNumberIsRegistered(String phoneNumber);
    }

    interface View extends BaseView<Presenter> {

        void registerSuccess();

        void RegisterFailed();
    }
}
