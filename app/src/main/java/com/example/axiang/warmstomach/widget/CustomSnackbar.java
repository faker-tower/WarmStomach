package com.example.axiang.warmstomach.widget;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.axiang.warmstomach.R;

/**
 * Created by a2389 on 2017/12/24.
 */

public class CustomSnackbar {

    private Snackbar mSnackbar;

    private CustomSnackbar(Builder builder) {
        mSnackbar = Snackbar.make(builder.parentView,
                builder.messageText,
                Snackbar.LENGTH_LONG);
        mSnackbar.getView().setBackgroundColor(Color.WHITE);
        mSnackbar.setAction(builder.actionText, builder.listener);
        if (builder.messageColorId != 0) {
            ((TextView) mSnackbar.getView()
                    .findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(builder.messageColorId);
        }
        if (builder.actionColorId != 0) {
            mSnackbar.setActionTextColor(builder.actionColorId);
        }
    }

    public void dismiss() {
        if (mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    public void show() {
        if (!mSnackbar.isShown()) {
            mSnackbar.show();
        }
    }

    public static class Builder {
        private View parentView;
        private String messageText;
        private int messageColorId;
        private String actionText;
        private int actionColorId;
        private View.OnClickListener listener;

        public Builder setParentView(View parentView) {
            this.parentView = parentView;
            return this;
        }

        public Builder setMessageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public Builder setMessageColorId(int messageColorId) {
            this.messageColorId = messageColorId;
            return this;
        }

        public Builder setActionText(String actionText) {
            this.actionText = actionText;
            return this;
        }

        public Builder setActionColorId(int actionColorId) {
            this.actionColorId = actionColorId;
            return this;
        }

        public Builder setListener(View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public CustomSnackbar build() {
            return new CustomSnackbar(this);
        }
    }
}
