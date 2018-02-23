package com.example.axiang.warmstomach.data;

/**
 * Created by a2389 on 2018/2/20.
 */

public class Settle {

    private double nowAllPrice;
    private String settleButtonText;
    private boolean settleButtonClickable;

    public Settle() {}

    public Settle(double nowAllPrice, String settleButtonText, boolean settleButtonClickable) {
        this.nowAllPrice = nowAllPrice;
        this.settleButtonText = settleButtonText;
        this.settleButtonClickable = settleButtonClickable;
    }

    public double getNowAllPrice() {
        return nowAllPrice;
    }

    public void setNowAllPrice(double nowAllPrice) {
        this.nowAllPrice = nowAllPrice;
    }

    public String getSettleButtonText() {
        return settleButtonText;
    }

    public void setSettleButtonText(String settleButtonText) {
        this.settleButtonText = settleButtonText;
    }

    public boolean isSettleButtonClickable() {
        return settleButtonClickable;
    }

    public void setSettleButtonClickable(boolean settleButtonClickable) {
        this.settleButtonClickable = settleButtonClickable;
    }
}
