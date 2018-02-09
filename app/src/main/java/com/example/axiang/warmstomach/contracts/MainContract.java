package com.example.axiang.warmstomach.contracts;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;

/**
 * Created by a2389 on 2018/2/5.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {
        // 定位中
        void onPositioning();
        // 定位成功
        void onPositionSuccess(String address);
        // 定位失败
        void onPositionFailed();

        // 同步成功
        void syncUserDataSuccess();
        // 同步失败
        void syncUserDataFailed();
    }

    interface Presenter extends BasePresenter {
        // 同步用户数据
        void syncUserData();

        // 绑定View
        void setView(View view);
    }
}
