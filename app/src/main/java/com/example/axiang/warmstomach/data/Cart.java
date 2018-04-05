package com.example.axiang.warmstomach.data;

import java.io.Serializable;

/**
 * Created by a2389 on 2018/2/16.
 */

public class Cart implements Serializable {

    private static final long serialVersionUID = 7247714666080613254L;

    private Store store;
    private StoreFood storeFood;
    private int number;

    public Cart() {}

    public Cart(Store store, StoreFood storeFood, int number) {
        this.store = store;
        this.storeFood = storeFood;
        this.number = number;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public StoreFood getStoreFood() {
        return storeFood;
    }

    public void setStoreFood(StoreFood storeFood) {
        this.storeFood = storeFood;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
