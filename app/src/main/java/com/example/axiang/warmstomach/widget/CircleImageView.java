package com.example.axiang.warmstomach.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.example.axiang.warmstomach.util.ImageUtil;

/**
 * Created by a2389 on 2017/12/2.
 */

public class CircleImageView extends AppCompatImageView {

    // 圆形图的长宽一致
    private int mWidth;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureSize(widthMeasureSpec);
        setMeasuredDimension(mWidth, mWidth);
    }

    // 圆形图长宽一致，统一测试即可
    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 100;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //自定义View实现过程中很重要的onDraw绘制图形的方法
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        //空值判断，必要步骤，避免由于没有设置src导致的异常错误
        if (drawable == null) {
            return;
        }

        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (null == bitmap) {
            return;
        }

        canvas.drawBitmap(ImageUtil.getCircleImage(bitmap, mWidth, mWidth / 2),
                0,
                0,
                null);
    }
}
