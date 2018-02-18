package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.interfaces.AdColumnOnClickListener;
import com.example.axiang.warmstomach.interfaces.OnAdColumnItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a2389 on 2017/12/31.
 */

public class CarouselAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mImageUrls;
    private OnAdColumnItemListener mListener;

    public CarouselAdapter(Context context, @NonNull List<String> imageUrls) {
        super();
        this.mContext = context;
        this.mImageUrls = imageUrls;
    }

    public void setListener(OnAdColumnItemListener listener) {
        this.mListener = listener;
    }

    public void setImageUrls(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            this.mImageUrls = imageUrls;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView adsPicture = new ImageView(mContext);
        adsPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext)
                .load(mImageUrls.get(position % mImageUrls.size()))
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.color.position_loading_layout_bg)
                        .error(R.color.net_work_error))
                .into(adsPicture);
        adsPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClicked(position % mImageUrls.size());
                }
            }
        });
        ((ViewPager) container).addView(adsPicture);
        return adsPicture;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
