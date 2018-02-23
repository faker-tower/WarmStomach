package com.example.axiang.warmstomach.interfaces;

/**
 * Created by a2389 on 2018/2/21.
 */

public interface OnCartListener {

    void onStoreChecked(boolean isChecked, int position);

    void onStoreClicked(int position);

    void onFoodChecked(boolean isChecked, int position);

    void onFoodLongClicked(int position);

    void onFoodClicked(int position);

    void onGoSettleClicked(int position);
}
