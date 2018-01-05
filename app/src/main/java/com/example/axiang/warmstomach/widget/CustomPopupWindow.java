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

    private PopupWindow popupWindow;

    private Context context;

    private View contentView;

    private Activity activity;

    public CustomPopupWindow(Builder builder) {
        this.context = builder.context;
        this.contentView = LayoutInflater.from(context).inflate(builder.contentViewId, null);
        if (builder.width == 0) {
            builder.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (builder.height == 0) {
            builder.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        popupWindow = new PopupWindow(contentView,
                builder.width,
                builder.height,
                builder.focusable);
        if (builder.backgroupAlpha >= 0 && builder.backgroupAlpha < 1) {
            this.activity = builder.activity;
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.alpha = builder.backgroupAlpha;
            builder.activity.getWindow().setAttributes(params);
        }
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(builder.touchable);
        popupWindow.setAnimationStyle(builder.animationStyle);
        popupWindow.setOnDismissListener(this);
    }

    public CustomPopupWindow showAtLocation(int rootViewId, int gravity, int x, int y) {
        if (popupWindow != null) {
            View rootView = LayoutInflater.from(context).inflate(rootViewId, null);
            popupWindow.showAtLocation(rootView, gravity, x, y);
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
        if (popupWindow != null) {
            return this.contentView.findViewById(childViewId);
        }
        return null;
    }

    @Override
    public void onDismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            if (activity != null) {
                WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                params.alpha = 1.0f;
                activity.getWindow().setAttributes(params);
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