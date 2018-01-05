package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by a2389 on 2017/12/24.
 */

public class StoreFoodSort extends BmobObject {

    private String sortName;
    private String ownStoreId;

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getOwnStoreId() {
        return ownStoreId;
    }

    public void setOwnStoreId(String ownStoreId) {
        this.ownStoreId = ownStoreId;
    }
}
