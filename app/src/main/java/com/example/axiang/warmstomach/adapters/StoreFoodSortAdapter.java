package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.StoreFoodSort;
import com.example.axiang.warmstomach.interfaces.OnSortListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/2/13.
 */

public class StoreFoodSortAdapter extends RecyclerView.Adapter<StoreFoodSortAdapter.SortViewHolder> {

    private Context mContext;

    private List<StoreFoodSort> mSorts;

    private OnSortListener mListener;

    private SortViewHolder mLastViewHolder;
    private int mLastSelection;
    private boolean isInitFirstItem = false;

    public StoreFoodSortAdapter(Context context, List<StoreFoodSort> sorts) {
        this.mContext = context;
        this.mSorts = sorts;
    }

    public void setListener(OnSortListener listener) {
        this.mListener = listener;
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SortViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_food_sort, parent, false));
    }

    @Override
    public void onBindViewHolder(final SortViewHolder holder, final int position) {
        holder.sortName.setText(mSorts.get(position).getSortName());
        holder.layoutFoodSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(position);
                    if (position != mLastSelection) {
                        updateSelection(holder, position);
                    }
                }
            }
        });
        if (!isInitFirstItem && position == 0) {
            mLastSelection = position;
            mLastViewHolder = holder;
            holder.sortName.setTextColor(ContextCompat.getColor(mContext,
                    R.color.colorPrimaryText));
            isInitFirstItem = true;
        }
    }

    public void updateSelection(RecyclerView.ViewHolder holder, int position) {
        if (position != mLastSelection) {
            SortViewHolder nowViewHolder = (SortViewHolder) holder;
            mLastViewHolder.sortName.setTextColor(ContextCompat.getColor(mContext,
                    R.color.colorSecondaryText));
            nowViewHolder.sortName.setTextColor(ContextCompat.getColor(mContext,
                    R.color.colorPrimaryText));
            mLastViewHolder = nowViewHolder;
            mLastSelection = position;
        }
    }

    @Override
    public int getItemCount() {
        return mSorts.size();
    }

    static class SortViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_food_sort)
        RelativeLayout layoutFoodSort;
        @BindView(R.id.sort_name)
        TextView sortName;

        public SortViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
