package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by a2389 on 2018/2/7.
 */

public class StoreTypeAdapter extends SimpleAdapter {

    private Context mContext;
    private int mResource;
    private List<Map<String, Object>> mData;
    private String[] mFrom;
    private int[] mTo;

    public StoreTypeAdapter(Context context,
                            List<Map<String, Object>> data,
                            int resource,
                            String[] from,
                            int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.mResource = resource;
        this.mData = data;
        this.mFrom = from;
        this.mTo = to;
    }

    public void setData(List<Map<String, Object>> data) {
        if (data != null && !data.isEmpty()) {
            this.mData = data;
        }
    }

    @Override
    public Object getItem(int position) {
        return mData == null || mData.isEmpty() ? null : mData.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.storeTypeImage = (ImageView) view.findViewById(mTo[0]);
            viewHolder.storeTypeText = (TextView) view.findViewById(mTo[1]);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load((int) (mData.get(position).get(mFrom[0])))
                .apply(new RequestOptions().centerCrop()
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.error))
                .into(viewHolder.storeTypeImage);
        viewHolder.storeTypeText.setText((String) (mData.get(position).get(mFrom[1])));
        return view;
    }

    static class ViewHolder {
        private ImageView storeTypeImage;
        private TextView storeTypeText;
    }
}
