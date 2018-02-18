package com.example.axiang.warmstomach.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.contracts.HomeContract;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreAd;
import com.example.axiang.warmstomach.data.SuperStore;
import com.example.axiang.warmstomach.util.CalculateUtil;
import com.example.axiang.warmstomach.util.NetWorkUtil;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by a2389 on 2017/12/2.
 */

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mView;

    @Override
    public void start() {
        loadStoreAdData();
        loadSuperStoreData();
        loadInterfaceStoreData(0);
    }

    @Override
    public void setView(HomeContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadStoreAdData() {
        BmobQuery<StoreAd> query = new BmobQuery<>();
        query.setLimit(C.CAROUSEL_IMAGE_NUMBER)
                .setSkip(0)
                .findObjects(new FindListener<StoreAd>() {
                    @Override
                    public void done(List<StoreAd> list, BmobException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            Log.e("loadStoreAdData", e.toString());
                            e.printStackTrace();
                            handleLoadError();
                        } else {
                            mView.updateStoreAd(list);
                        }
                    }
                });
    }

    @Override
    public void loadSuperStoreData() {
        double longitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""));
        double latitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""));
        BmobQuery<SuperStore> query = new BmobQuery<>();
        query.setSkip(0)
                .findObjects(new FindListener<SuperStore>() {
                    @Override
                    public void done(List<SuperStore> list, BmobException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            Log.e("loadStoreAdData", e.toString());
                            e.printStackTrace();
                            handleLoadError();
                        } else {
                            quiteSortSuperStoreList(list, 0, list.size() - 1);
                            loadStoreBySuperStoreId(list.get(0).getSuperStoreId());
                        }
                    }
                });
    }

    // 快速算法排序SuperStoreList数组
    private void quiteSortSuperStoreList(List<SuperStore> superStores,
                                         int start,
                                         int end) {
        if (start < end) {
            int temp = sortSuperStoreList(superStores, start, end);
            quiteSortSuperStoreList(superStores, start, temp - 1);
            quiteSortSuperStoreList(superStores, temp + 1, end);
        }
    }

    private int sortSuperStoreList(List<SuperStore> superStores, int start, int end) {
        double localLongitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""));
        double localLatitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""));
        SuperStore superStore = superStores.get(start);
        while (start < end) {
            while (start < end && CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    superStore.getSuperStoreLocation().getLongitude(),
                    superStore.getSuperStoreLocation().getLatitude())
                    <= CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    superStores.get(end).getSuperStoreLocation().getLongitude(),
                    superStores.get(end).getSuperStoreLocation().getLatitude())) {
                end--;
            }
            if (start < end) {
                superStores.set(start++, superStores.get(end));
            }
            while (start < end && CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    superStore.getSuperStoreLocation().getLongitude(),
                    superStore.getSuperStoreLocation().getLatitude())
                    >= CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    superStores.get(start).getSuperStoreLocation().getLongitude(),
                    superStores.get(start).getSuperStoreLocation().getLatitude())) {
                start++;
            }
            if (start < end) {
                superStores.set(end--, superStores.get(start));
            }
        }
        superStores.set(start, superStore);
        return start;
    }

    @Override
    public void loadInterfaceStoreData(final int offset) {
        BmobQuery<Store> query = new BmobQuery<>();
        query.setSkip(offset)
                .findObjects(new FindListener<Store>() {
                    @Override
                    public void done(List<Store> list, BmobException e) {
                        if (e != null || list == null || list.isEmpty()) {
                            Log.e("loadStoreAdData", e.toString());
                            e.printStackTrace();
                            handleLoadError();
                        } else {
                            quiteSortStoreList(list, 0, list.size() - 1);
                            reverseSelf(list);
                            mView.updateStore(list);
                        }
                    }
                });
    }

    // 数组元素倒序
    private void reverseSelf(List<Store> list) {
        for (int i = 0, j = list.size() - 1; i < j; i++, j--) {
            Store store = list.get(i);
            list.set(i, list.get(j));
            list.set(j, store);
        }
    }

    // 快速算法排序StoreList数组
    private void quiteSortStoreList(List<Store> stores,
                                    int start,
                                    int end) {
        if (start < end) {
            int temp = sortStoreList(stores, start, end);
            quiteSortStoreList(stores, start, temp - 1);
            quiteSortStoreList(stores, temp + 1, end);
        }
    }

    private int sortStoreList(List<Store> stores, int start, int end) {
        double localLongitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""));
        double localLatitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""));
        Store store = stores.get(start);
        while (start < end) {
            while (start < end && CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    store.getStoreLocation().getLongitude(),
                    store.getStoreLocation().getLatitude())
                    <= CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    stores.get(end).getStoreLocation().getLongitude(),
                    stores.get(end).getStoreLocation().getLatitude())) {
                end--;
            }
            if (start < end) {
                stores.set(start++, stores.get(end));
            }
            while (start < end && CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    store.getStoreLocation().getLongitude(),
                    store.getStoreLocation().getLatitude())
                    >= CalculateUtil.getDistance(localLongitude,
                    localLatitude,
                    stores.get(start).getStoreLocation().getLongitude(),
                    stores.get(start).getStoreLocation().getLatitude())) {
                start++;
            }
            if (start < end) {
                stores.set(end--, stores.get(start));
            }
        }
        stores.set(start, store);
        return start;
    }

    public void loadStoreBySuperStoreId(String storeId) {
        BmobQuery<Store> query = new BmobQuery<>();
        query.getObject(storeId, new QueryListener<Store>() {
            @Override
            public void done(Store store, BmobException e) {
                if (e != null || store == null) {
                    Log.e("loadStoreAdData", e.toString());
                    e.printStackTrace();
                    handleLoadError();
                } else {
                    mView.updateSuperStore(store);
                }
            }
        });
    }

    @Override
    public void loadMoreStoreData(List<Store> stores, int offset) {
        try {
            if (!NetWorkUtil.isNetWorkConnected()) {
                throw new Exception();
            }
            List<Store> responseStores = new ArrayList<>();
            for (int i = 0; i < C.QUERY_STORE_NUMBER; i++) {
                if (i + offset < stores.size()) {
                    responseStores.add(stores.get(i + offset));
                }
            }
            if (responseStores.size() > 0) {
                mView.updateStore(responseStores);
                if (responseStores.size() < C.QUERY_STORE_NUMBER) {
                    mView.updateFooterWhenNoData();
                }
            } else {
                mView.updateFooterWhenNoData();
            }
        } catch (Exception e) {
            mView.updateFooterWhenError();
            handleLoadError();
        } finally {
            if (offset > 0) {
                mView.loadMoreStoreDataFinish();
            }
        }
    }

    private void handleLoadError() {
        if (!NetWorkUtil.isNetWorkConnected()) {
            mView.showNetWorkError();
        } else {
            mView.showUnknownError();
        }
    }
}
