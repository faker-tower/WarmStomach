package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.interfaces.OnFoodListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/2/12.
 */

public class StoreFoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<Object> mStoreFoodsBySorts;
    private OnFoodListener mListener;

    private Map<StoreFood, Integer> mCarts;

    public StoreFoodAdapter(Context context,
                            @NonNull List<Object> storeFoodsBySorts,
                            Map<StoreFood, Integer> carts) {
        this.mContext = context;
        this.mStoreFoodsBySorts = storeFoodsBySorts;
        this.mCarts = carts;
    }

    public void setCarts(Map<StoreFood, Integer> carts) {
        this.mCarts = carts;
    }

    public void setListener(OnFoodListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mStoreFoodsBySorts.size();
    }

    public void updateFoodByCart(RecyclerView.ViewHolder viewHolder, boolean isAdd) {
        ContentViewHolder holder = (ContentViewHolder) viewHolder;
        if (isAdd) {
            holder.foodNumber
                    .setText(String.valueOf(Integer.parseInt(holder.foodNumber
                            .getText().toString()) + 1));
        } else {
            if (Integer.parseInt(holder.foodNumber.getText().toString()) == 1) {
                holder.foodNumber.setText("0");
                holder.foodNumber.setVisibility(View.GONE);
                holder.foodCut.setVisibility(View.GONE);
            } else {
                holder.foodNumber
                        .setText(String.valueOf(Integer.parseInt(holder.foodNumber
                                .getText().toString()) - 1));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mStoreFoodsBySorts.get(position) instanceof StoreFood) {
            return C.STORE_FOOD_TYPE_CONTENT;
        } else {
            return C.STORE_FOOD_TYPE_TITLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case C.STORE_FOOD_TYPE_TITLE:
                return new TitleViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_food_title, parent, false));
            case C.STORE_FOOD_TYPE_CONTENT:
                return new ContentViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_food_content, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object object = mStoreFoodsBySorts.get(position);
        if (object instanceof StoreFood) {
            StoreFood food = (StoreFood) object;
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            initContentView(food,  contentViewHolder, position);
        } else {
            ((TitleViewHolder) holder).foodTitle.setText((String) object);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    private void initContentView(final StoreFood food,
                                 final ContentViewHolder viewHolder,
                                 final int position) {
        viewHolder.foodCut.setVisibility(View.GONE);
        viewHolder.foodNumber.setVisibility(View.GONE);
        Glide.with(mContext)
                .load(food.getFoodPicture())
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(viewHolder.foodPicture);
        viewHolder.foodName.setText(food.getFoodName());
        viewHolder.foodSummary
                .setText(mContext.getResources().getString(R.string.app_name));
        viewHolder.foodMonthSoldOn.setText(String.valueOf(food.getFoodMonthSoldOn().intValue()));
        viewHolder.foodLike.setText(String.valueOf(food.getFoodLikeNumber().intValue()));
        viewHolder.foodPrice.setText(String.valueOf(food.getFoodPrice().doubleValue()));
        viewHolder.layoutFoodContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(position);
                }
            }
        });
        viewHolder.foodAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.foodCut.getVisibility() == View.GONE) {
                    viewHolder.foodNumber.setText(String.valueOf(1));
                    viewHolder.foodNumber.setVisibility(View.VISIBLE);
                    viewHolder.foodCut.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.foodNumber
                            .setText(String.valueOf(
                                    Integer.parseInt(
                                            viewHolder.foodNumber.getText().toString()) + 1));
                }
                if (mListener != null) {
                    mListener.updateShoppingCart(true, position, viewHolder.foodAdd);
                }
            }
        });
        viewHolder.foodCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(viewHolder.foodNumber.getText().toString()) == 1) {
                    viewHolder.foodNumber.setText("0");
                    viewHolder.foodNumber.setVisibility(View.GONE);
                    viewHolder.foodCut.setVisibility(View.GONE);
                } else {
                    viewHolder.foodNumber
                            .setText(String.valueOf(
                                    Integer.parseInt(
                                            viewHolder.foodNumber.getText().toString()) - 1));
                }
                if (mListener != null) {
                    mListener.updateShoppingCart(false, position, null);
                }
            }
        });

        if (mCarts != null && !mCarts.isEmpty()) {
            if (mCarts.containsKey(food)) {
                int foodCount = mCarts.get(food).intValue();
                if (foodCount > 0) {
                    viewHolder.foodCut.setVisibility(View.VISIBLE);
                    viewHolder.foodNumber.setVisibility(View.VISIBLE);
                    viewHolder.foodNumber.setText(String.valueOf(foodCount));
                }
            }
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.food_title)
        TextView foodTitle;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.food_picture)
        ImageView foodPicture;
        @BindView(R.id.food_name)
        TextView foodName;
        @BindView(R.id.food_summary)
        TextView foodSummary;
        @BindView(R.id.food_month_sold_on)
        TextView foodMonthSoldOn;
        @BindView(R.id.food_like)
        TextView foodLike;
        @BindView(R.id.food_price)
        TextView foodPrice;
        @BindView(R.id.food_add)
        ImageView foodAdd;
        @BindView(R.id.food_number)
        TextView foodNumber;
        @BindView(R.id.food_cut)
        ImageView foodCut;
        @BindView(R.id.layout_food_content)
        RelativeLayout layoutFoodContent;

        ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
