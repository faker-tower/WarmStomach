package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by a2389 on 2017/12/25.
 */

public class SuperStore extends BmobObject {

    private String superStoreId;
    private BmobGeoPoint superStoreLocation;

    public String getSuperStoreId() {
        return superStoreId;
    }

    public void setSuperStoreId(String superStoreId) {
        this.superStoreId = superStoreId;
    }

    public BmobGeoPoint getSuperStoreLocation() {
        return superStoreLocation;
    }

    public void setSuperStoreLocation(BmobGeoPoint superStoreLocation) {
        this.superStoreLocation = superStoreLocation;
    }
}
