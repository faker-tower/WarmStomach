package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by a2389 on 2017/12/25.
 */

public class StoreAd extends BmobObject {

    private String StoreId;
    private String StoreAdPicture;

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public String getStoreAdPicture() {
        return StoreAdPicture;
    }

    public void setStoreAdPicture(String storeAdPicture) {
        StoreAdPicture = storeAdPicture;
    }
}
