package com.example.axiang.warmstomach.interfaces;

/**
 * Created by a2389 on 2018/2/7.
 */

public interface HomeListener {

    // 定时切换广告栏图片
    void timingToReplaceAdPic();

    // 更新底部加载更多信息
    void updateFooter(int imageResourceId, String content);
}
