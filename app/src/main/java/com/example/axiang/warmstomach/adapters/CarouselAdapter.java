package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.interfaces.AdColumnOnClickListener;

import java.util.List;

/**
 * Created by a2389 on 2017/12/31.
 */

public class CarouselAdapter extends PagerAdapter {

    private Context context;
    private List<String> imageUrlList;
    private AdColumnOnClickListener listener;

    public CarouselAdapter(Context context, List<String> imageUrlList) {
        super();
        this.context = context;
        this.imageUrlList = imageUrlList;
    }

    public void setListener(AdColumnOnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ImageView adsPicture = new ImageView(context);
        adsPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .load(imageUrlList.get(position % imageUrlList.size()))
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.color.position_loading_layout_bg)
                        .error(R.color.net_work_error))
                .into(adsPicture);
        if (listener != null) {
            adsPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position % imageUrlList.size());
                }
            });
        }
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
