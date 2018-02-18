package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.interfaces.onStoreCartListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/2/17.
 */

public class StoreCartAdapter extends RecyclerView.Adapter<StoreCartAdapter.CartViewHolder> {

    private Context mContext;

    private Map<StoreFood, Integer> mCarts;

    private List<StoreFood> mFoods;

    private onStoreCartListener mOnStoreCartListener;

    public StoreCartAdapter(@NonNull Context context,
                            @NonNull Map<StoreFood, Integer> carts) {
        this.mContext = context;
        this.mCarts = carts;
        this.mFoods = new ArrayList<>();
        Iterator<StoreFood> it = carts.keySet().iterator();
        while (it.hasNext()) {
            this.mFoods.add(it.next());
        }
    }

    public void setOnItemClickListener(onStoreCartListener onStoreCartListener) {
        this.mOnStoreCartListener = onStoreCartListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_store_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        final StoreFood food = mFoods.get(position);
        holder.cartFoodName.setText(food.getFoodName());
        holder.cartFoodNumber.setText(String.valueOf(mCarts.get(food)));
        holder.cartFoodAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cartFoodNumber
                        .setText(String.valueOf(Integer.valueOf(holder.cartFoodNumber
                                .getText().toString()) + 1));
                if (mOnStoreCartListener != null) {
                    mOnStoreCartListener.updateShoppingCart(true, food);
                }
            }
        });
        holder.cartFoodCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.valueOf(holder.cartFoodNumber.getText().toString());
                if (count == 1) {
                    mCarts.remove(food);
                    mFoods.remove(position);
                    notifyDataSetChanged();
                } else {
                    holder.cartFoodNumber
                            .setText(String.valueOf(Integer.valueOf(holder.cartFoodNumber
                                    .getText().toString()) - 1));
                }
                if (mOnStoreCartListener != null) {
                    mOnStoreCartListener.updateShoppingCart(false, food);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCarts.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_food_name)
        TextView cartFoodName;
        @BindView(R.id.cart_food_add)
        ImageView cartFoodAdd;
        @BindView(R.id.cart_food_number)
        TextView cartFoodNumber;
        @BindView(R.id.cart_food_cut)
        ImageView cartFoodCut;

        public CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
