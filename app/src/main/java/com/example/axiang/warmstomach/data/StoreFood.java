package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by a2389 on 2017/12/24.
 */

public class StoreFood extends BmobObject {

    private String foodName;
    private String foodOwnSortId;
    private String foodOwnStoreId;
    private Double foodPrice;
    private Integer foodMonthSoldOn;
    private Integer foodLikeNumber;
    private String foodPicture;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodOwnSortId() {
        return foodOwnSortId;
    }

    public void setFoodOwnSortId(String foodOwnSortId) {
        this.foodOwnSortId = foodOwnSortId;
    }

    public String getFoodOwnStoreId() {
        return foodOwnStoreId;
    }

    public void setFoodOwnStoreId(String foodOwnStoreId) {
        this.foodOwnStoreId = foodOwnStoreId;
    }

    public Double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(Double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public Integer getFoodMonthSoldOn() {
        return foodMonthSoldOn;
    }

    public void setFoodMonthSoldOn(Integer foodMonthSoldOn) {
        this.foodMonthSoldOn = foodMonthSoldOn;
    }

    public Integer getFoodLikeNumber() {
        return foodLikeNumber;
    }

    public void setFoodLikeNumber(Integer foodLikeNumber) {
        this.foodLikeNumber = foodLikeNumber;
    }

    public String getFoodPicture() {
        return foodPicture;
    }

    public void setFoodPicture(String foodPicture) {
        this.foodPicture = foodPicture;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreFood) {
            StoreFood food = (StoreFood) obj;
            if (this.getObjectId().equals(food.getObjectId())
                    && this.getFoodName().equals(food.getFoodName())) {
                return true;
            }
        }
        return false;
    }
}
