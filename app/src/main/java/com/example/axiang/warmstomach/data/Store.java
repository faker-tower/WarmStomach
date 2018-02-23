package com.example.axiang.warmstomach.data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by a2389 on 2017/12/24.
 */

public class Store extends BmobObject {

    private String storeName;
    private String storeType;
    private String storeAvatar;
    private BmobGeoPoint storeLocation;
    private String storeAnnouncement;
    private Double storeStartingPrice;
    private Float storeDeliveryFee;
    private Integer storePerCapita;
    private Integer storeMonthSoldOn;
    private Boolean storeOnlinePayment;
    private Boolean storeCashOnDelivery;
    private Boolean storePickUp;
    private String storePhone;
    private String storeAddress;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreAvatar() {
        return storeAvatar;
    }

    public void setStoreAvatar(String storeAvatar) {
        this.storeAvatar = storeAvatar;
    }

    public BmobGeoPoint getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(BmobGeoPoint storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreAnnouncement() {
        return storeAnnouncement;
    }

    public void setStoreAnnouncement(String storeAnnouncement) {
        this.storeAnnouncement = storeAnnouncement;
    }

    public Double getStoreStartingPrice() {
        return storeStartingPrice;
    }

    public void setStoreStartingPrice(Double storeStartingPrice) {
        this.storeStartingPrice = storeStartingPrice;
    }

    public Float getStoreDeliveryFee() {
        return storeDeliveryFee;
    }

    public void setStoreDeliveryFee(Float storeDeliveryFee) {
        this.storeDeliveryFee = storeDeliveryFee;
    }

    public Integer getStorePerCapita() {
        return storePerCapita;
    }

    public void setStorePerCapita(Integer storePerCapita) {
        this.storePerCapita = storePerCapita;
    }

    public Integer getStoreMonthSoldOn() {
        return storeMonthSoldOn;
    }

    public void setStoreMonthSoldOn(Integer storeMonthSoldOn) {
        this.storeMonthSoldOn = storeMonthSoldOn;
    }

    public Boolean getStoreOnlinePayment() {
        return storeOnlinePayment;
    }

    public void setStoreOnlinePayment(Boolean storeOnlinePayment) {
        this.storeOnlinePayment = storeOnlinePayment;
    }

    public Boolean getStoreCashOnDelivery() {
        return storeCashOnDelivery;
    }

    public void setStoreCashOnDelivery(Boolean storeCashOnDelivery) {
        this.storeCashOnDelivery = storeCashOnDelivery;
    }

    public Boolean getStorePickUp() {
        return storePickUp;
    }

    public void setStorePickUp(Boolean storePickUp) {
        this.storePickUp = storePickUp;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Store) {
            Store store = (Store) obj;
            if (this.getObjectId().equals(((Store) obj).getObjectId())) {
                return true;
            }
        }
        return false;
    }
}
