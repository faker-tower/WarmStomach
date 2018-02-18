package com.example.axiang.warmstomach.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.example.axiang.warmstomach.R;

import java.util.List;

/**
 * Created by a2389 on 2018/2/13.
 */

public class FoodTitleItemDecoration extends RecyclerView.ItemDecoration {

    private List<Object> mObjects;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字的Rect

    private int mTitleBgColor;  //Title的背景色
    private int mTitleFontColor;   //T Title的字体色
    private int mTitleHeight; // Title的高
    private int mTitleMarginLeftSize;
    private int mTitleTextSize; // Title的字体大小
    private String mLastSortName;

    public FoodTitleItemDecoration(Context context, List<Object> objects) {
        super();
        this.mObjects = objects;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25,
                context.getResources().getDisplayMetrics());
        mTitleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12,
                context.getResources().getDisplayMetrics());
        mTitleMarginLeftSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10,
                context.getResources().getDisplayMetrics());
        mTitleBgColor = ContextCompat.getColor(context, R.color.colorBaseBackground);
        mTitleFontColor = ContextCompat.getColor(context, R.color.colorSecondaryText);
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setAntiAlias(true); // 设置抗锯齿
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int position = ((LinearLayoutManager) (parent.getLayoutManager()))
                .findFirstVisibleItemPosition();
        if (position > -1) {
            String sortName;
            if (mObjects.get(position) instanceof String) {
                sortName = (String) mObjects.get(position);
                mLastSortName = sortName;
            } else {
                sortName = mLastSortName;
            }
            View child = parent.findViewHolderForLayoutPosition(position).itemView;
            mPaint.setColor(mTitleBgColor);
            c.drawRect(parent.getPaddingLeft(),
                    parent.getPaddingTop(),
                    parent.getRight() - parent.getPaddingRight(),
                    parent.getPaddingTop() + mTitleHeight,
                    mPaint);
            mPaint.setColor(mTitleFontColor);
            mPaint.getTextBounds(sortName, 0, sortName.length(), mBounds);
            c.drawText(sortName,
                    child.getPaddingLeft() + mTitleMarginLeftSize,
                    parent.getPaddingTop() + mTitleHeight
                            - (mTitleHeight / 2 - mBounds.height() / 2),
                    mPaint);
        }
    }
}
