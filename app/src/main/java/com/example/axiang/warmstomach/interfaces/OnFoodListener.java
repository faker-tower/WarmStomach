package com.example.axiang.warmstomach.interfaces;

import android.widget.ImageView;

/**
 * Created by a2389 on 2018/2/13.
 */

public interface OnFoodListener {

    void onItemClicked(int position);

    // 更新购物车
    void updateShoppingCart(boolean isAdded, int position, ImageView foodAddImage);
}
