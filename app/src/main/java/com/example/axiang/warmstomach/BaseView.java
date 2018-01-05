package com.example.axiang.warmstomach;

/**
 * Created by a2389 on 2017/12/2.
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    void initView();
}
