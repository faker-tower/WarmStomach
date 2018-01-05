package com.example.axiang.warmstomach.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.axiang.warmstomach.C;

import java.io.File;
import java.io.IOException;

import retrofit2.http.PUT;

/**
 * Created by a2389 on 2017/12/2.
 */

public class ImageUtil {


    /**
     * 将普通图片加工成圆形图
     *
     * @param bitmap：代处理图片
     * @param outWidth：最终输出宽度，圆形图高度宽度一样，一般为控件宽度
     * @param radius：圆形图的半径，一般是最终输出宽度的一半
     * @return：圆形图
     */
    public static Bitmap getCircleImage(Bitmap bitmap, int outWidth, int radius) {
        if (bitmap == null) {
            throw new NullPointerException("The bitmap can't be null!");
        }

        // 先将图片裁剪成正方形并压缩拉升到最终大小
        int originalWidth = bitmap.getWidth();  // 初始宽度
        int originalHeight = bitmap.getHeight();//初始高度
        int btWidhtCutSize = 0;  // 水平方向开始裁剪的位置
        int btHeightCutSize = 0;    // 竖直方向开始裁剪的位置
        int finalWidth = 0; // 正方形宽度
        if (originalWidth > originalHeight) {
            btWidhtCutSize = (originalWidth - originalHeight) / 2;
            finalWidth = originalHeight;
        } else if (originalHeight > originalWidth) {
            btHeightCutSize = (originalHeight - originalWidth) / 2;
            finalWidth = originalWidth;
        } else {
            finalWidth = originalWidth;
        }
        float scale = outWidth * 1.0f / finalWidth;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap squareBitmap = Bitmap.createBitmap(bitmap,
                btWidhtCutSize,
                btHeightCutSize,
                finalWidth,
                finalWidth,
                matrix,
                true);

        // 初始化纹理绘制图
        BitmapShader shader = new BitmapShader(squareBitmap,
                BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);

        // 初始化目标Bitmap
        Bitmap targetBitmap = Bitmap.createBitmap(outWidth, outWidth, Bitmap.Config.ARGB_8888);

        // 初始化画布
        Canvas canvas = new Canvas(targetBitmap);

        // 初始化画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        // 利用画笔绘制圆形图
        canvas.drawRoundRect(new RectF(0, 0, outWidth, outWidth), radius, radius, paint);
        return targetBitmap;
    }

    /**
     * 图片剪裁
     */
    public static Intent cutForPhoto(Uri uri,
                                     int cutWidth,
                                     int cutHeight,
                                     File titleImageCutFile) throws IOException {
        // 直接裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (titleImageCutFile.exists()) {
            titleImageCutFile.delete();
        }
        titleImageCutFile.createNewFile();
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", true);
        // aspectX,aspectY 是宽高的比例，这里设置正方形
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 设置要裁剪的宽高
        intent.putExtra("outputX", cutWidth);
        intent.putExtra("outputY", cutHeight);
        intent.putExtra("scale",true);
        // 如果图片过大，会导致oom，这里设置为false
        intent.putExtra("return-data", false);
        if (uri != null) {
            intent.setDataAndType(uri, "image/*");
        }
        if (titleImageCutFile.exists()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(titleImageCutFile));
        }
        intent.putExtra("noFaceDetection", true);
        // 压缩图片
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        return intent;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale  + 0.5f);
    }

}
