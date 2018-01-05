package com.example.axiang.warmstomach.contracts;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreAd;
import com.example.axiang.warmstomach.data.SuperStore;

import java.util.List;

/**
 * Created by a2389 on 2017/12/2.
 */

public interface HomeContract {

    interface Presenter extends BasePresenter {
        // 绑定View
        void setView(View view);
        // 加载广告栏数据
        void loadStoreAdData();
        // 加载为您优选数据
        void loadSuperStoreData();
        // 加载界面商家数据
        void loadInterfaceStoreData(int offset);
    }

    interface View extends BaseView<Presenter> {
        // 定位错误
        void showPositionError();
        // 网络异常
        void showNetWorkError();
        // 未知异常
        void showUnknownError();
        // 定位成功
        void showPositionSuccess(String county,
                                 String city,
                                 String district,
                                 String street,
                                 String addrStr,
                                 String locationDescribe);
        // 更新广告栏
        void updateStoreAd(List<StoreAd> storeAdList);
        // 更新为您优选
        void updateSuperStore(Store store);
        // 更新商家
        void updateStore(List<Store> storeList);
        // 加载更多商家失败，更新RecyclerView footer
        void updateFooterWhenError();
        // 商家查询尽头，更新RecyclerView footer
        void updateFooterWhenNoData();
        // 加载更多数据操作已完成
        void loadMoreStoreDataFinish();
    }
}