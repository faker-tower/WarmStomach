package com.example.axiang.warmstomach.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class CustomPopupWindow implements PopupWindow.OnDismissListener {

    private PopupWindow mPopupWindow;

    private Context mContext;

    private View mContentView;

    private Activity mActivity;

    public CustomPopupWindow(Builder builder) {
        this.mContext = builder.context;
        this.mContentView = LayoutInflater.from(mContext).inflate(builder.contentViewId, null);
        if (builder.width == 0) {
            builder.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (builder.height == 0) {
            builder.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mPopupWindow = new PopupWindow(mContentView,
                builder.width,
                builder.height,
                builder.focusable);
        if (builder.backgroupAlpha >= 0 && builder.backgroupAlpha < 1) {
            this.mActivity = builder.activity;
            WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
            params.alpha = builder.backgroupAlpha;
            builder.activity.getWindow().setAttributes(params);
        }
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(builder.touchable);
        mPopupWindow.setAnimationStyle(builder.animationStyle);
        mPopupWindow.setOnDismissListener(this);
    }

    public CustomPopupWindow showAtLocation(int rootViewId, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            View rootView = LayoutInflater.from(mContext).inflate(rootViewId, null);
            mPopupWindow.showAtLocation(rootView, gravity, x, y);
        }
        return this;
    }

    public void setChildOnCilickListener(int childViewId, View.OnClickListener listener) {
        View view = getChildView(childViewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public View getChildView(int childViewId) {
        if (mPopupWindow != null) {
            return this.mContentView.findViewById(childViewId);
        }
        return null;
    }

    @Override
    public void onDismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            if (mActivity != null) {
                WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
                params.alpha = 1.0f;
                mActivity.getWindow().setAttributes(params);
            }
        }
    }

    public static class Builder {
        private Context context;
        private int contentViewId;
        private int width;
        private int height;
        private boolean focusable;
        private boolean touchable;
        private int animationStyle;
        private Activity activity;
        private float backgroupAlpha;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setContentViewId(int contentViewId) {
            this.contentViewId = contentViewId;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFocusable(boolean focusable) {
            this.focusable = focusable;
            return this;
        }

        public Builder setTouchable(boolean touchable) {
            this.touchable = touchable;
            return this;
        }

        public Builder setAnimationStyle(int animationStyle) {
            this.animationStyle = animationStyle;
            return this;
        }

        public Builder setActivityAndAlpha(Activity activity, float backgroupAlpha) {
            this.activity = activity;
            this.backgroupAlpha = backgroupAlpha;
            return this;
        }

        public CustomPopupWindow build() {
            return new CustomPopupWindow(this);
        }
    }
}