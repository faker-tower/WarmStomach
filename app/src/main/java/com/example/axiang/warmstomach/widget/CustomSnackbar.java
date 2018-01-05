package com.example.axiang.warmstomach.widget;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by a2389 on 2017/12/24.
 */

public class CustomSnackbar {

    private Snackbar snackbar;

    private CustomSnackbar(Builder builder) {
        snackbar = Snackbar.make(builder.parentView,
                builder.messageText,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(builder.actionText, builder.listener);
        if (builder.messageColorId != 0) {
            ((TextView) snackbar.getView()
                    .findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(builder.messageColorId);
        }
        if (builder.actionColorId != 0) {
            snackbar.setActionTextColor(builder.actionColorId);
        }
    }

    public void dismiss() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public void show() {
        if (!snackbar.isShown()) {
            snackbar.show();
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
