package com.example.axiang.warmstomach.contracts;

import com.example.axiang.warmstomach.BasePresenter;
import com.example.axiang.warmstomach.BaseView;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.data.StoreFoodSort;

import java.util.List;

/**
 * Created by a2389 on 2018/2/10.
 */

public interface StoreContract {

    interface Presenter extends BasePresenter {
        // 绑定视图
        void setView(View view);
        // 根据商店Id加载对应的商店信息
        void loadStore(String storeId);
        // 根据商店Id加载对应的商店食物分类信息
        void loadSort(String storeId);
        // 根据商店Id加载对应的商店食物信息
        void loadFood(String storeId);
    }

    interface View extends BaseView<Presenter> {
        // 加载商家信息成功
        void loadStoreSuccess(Store store);
        // 加载商家食物分类信息成功
        void loadSortSuccess(List<StoreFoodSort> storeFoodSorts);
        // 加载商家食物信息成功
        void loadFoodSuccess(List<StoreFood> storeFoods);
        // 网络异常
        void showNetWorkError();
        // 未知异常
        void showUnknownError();
    }
}
