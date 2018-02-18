package com.example.axiang.warmstomach.presenters;

import android.util.Log;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.contracts.StoreContract;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.data.StoreFoodSort;
import com.example.axiang.warmstomach.util.NetWorkUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by a2389 on 2018/2/10.
 */

public class StorePresenter implements StoreContract.Presenter {

    private StoreContract.View mView;

    @Override
    public void start() {
        mView.initView();
    }

    @Override
    public void setView(StoreContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadStore(final String storeId) {
        BmobQuery<Store> storeBmobQuery = new BmobQuery<>();
        storeBmobQuery.getObject(storeId, new QueryListener<Store>() {
            @Override
            public void done(Store store, BmobException e) {
                if (e != null || store == null) {
                    handleLoadError();
                } else {
                    mView.loadStoreSuccess(store);
                }
            }
        });
    }

    @Override
    public void loadSort(String storeId) {
        BmobQuery<StoreFoodSort> sortBmobQuery = new BmobQuery<>();
        sortBmobQuery
                .addWhereEqualTo(C.SORT_TO_STORE, storeId)
                .setSkip(0)
                .findObjects(new FindListener<StoreFoodSort>() {
                    @Override
                    public void done(List<StoreFoodSort> list, BmobException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            handleLoadError();
                        } else {
                            mView.loadSortSuccess(list);
                        }
                    }
                });
    }

    @Override
    public void loadFood(String storeId) {
        BmobQuery<StoreFood> foodBmobQuery = new BmobQuery<>();
        foodBmobQuery
                .addWhereEqualTo(C.FOOD_TO_STORE, storeId)
                .setLimit(500)
                .setSkip(0)
                .findObjects(new FindListener<StoreFood>() {
                    @Override
                    public void done(List<StoreFood> list, BmobException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            handleLoadError();
                        } else {
                            mView.loadFoodSuccess(list);
                        }
                    }
                });
    }

    private void handleLoadError() {
        if (!NetWorkUtil.isNetWorkConnected()) {
            mView.showNetWorkError();
        } else {
            mView.showUnknownError();
        }
    }
}
