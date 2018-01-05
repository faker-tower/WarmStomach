package com.example.axiang.warmstomach.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 设置循环变色的TextView
 * Created by a2389 on 2017/12/5.
 */

public class CyclicalTextView extends AppCompatTextView {

    private Paint paint;

    private int viewWidth = 0;

    private Matrix gradientMatrix;

    private LinearGradient linearGradient;

    private boolean animating = true;

    private int translate = 0;

    private int delta = 15;

    public CyclicalTextView(Context context) {
        super(context, null);
    }

    public CyclicalTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (viewWidth == 0) {
            viewWidth = getMeasuredWidth();
            if (viewWidth > 0) {
                paint = getPaint();
                String text = getText().toString();
                int size;
                if (text.length() > 0) {
                    size = viewWidth * 2 / text.length();
                } else {
                    size = viewWidth;
                }
                linearGradient = new LinearGradient(-size, 0, 0, 0,
                        new int[] {0x33ffffff, 0xffffffff, 0x33ffffff},
                        new float[] {0, 0.5f, 1},
                        //边缘融合
                        Shader.TileMode.CLAMP);
                paint.setShader(linearGradient);
                gradientMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = Math.max(length(), 1);
        if (animating && gradientMatrix != null) {
            float textWidth = getPaint().measureText(getText().toString());
            translate += delta;
            if (translate > textWidth + 1 || translate < 1) {
                delta = -delta;
            }
            gradientMatrix.setTranslate(translate, 0);
            linearGradient.setLocalMatrix(gradientMatrix);
            postInvalidateDelayed(30);
        }
    }
}