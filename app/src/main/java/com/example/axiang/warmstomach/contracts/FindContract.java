package com.example.axiang.warmstomach.contracts;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;

/**
 * Created by a2389 on 2017/12/2.
 */

public interface FindContract {

    interface Presenter extends BasePresenter {
        void setView(View view);
    }

    interface View extends BaseView<Presenter> {
    }
}
