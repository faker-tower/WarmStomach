package com.example.axiang.warmstomach.interfaces;

import com.example.axiang.warmstomach.data.StoreFood;

/**
 * Created by a2389 on 2018/2/17.
 */

public interface onStoreCartListener {

    void updateShoppingCart(boolean isAdded, StoreFood food);
}
