package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.Cart;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/4/3.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context mContext;
    private List<Cart> mCarts;

    public OrderAdapter(Context context, List<Cart> carts) {
        this.mContext = context;
        this.mCarts = carts;
    }

    public void setCarts(List<Cart> carts) {
        this.mCarts = carts;
    }

    @Override
    public int getItemCount() {
        return mCarts == null || mCarts.isEmpty() ? 0 : mCarts.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_order_food, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cart cart = mCarts.get(position);
        Glide.with(mContext)
                .load(cart.getStoreFood().getFoodPicture())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(holder.cartFoodAvatar);
        holder.orderFoodName.setText(cart.getStoreFood().getFoodName());
        holder.orderFoodNumber.setText("x " + String.valueOf(cart.getNumber()));
        holder.orderFoodPrice.setText(holder.moneySymbol
                + (cart.getStoreFood().getFoodPrice().doubleValue() * cart.getNumber()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_food_avatar)
        ImageView cartFoodAvatar;
        @BindView(R.id.order_food_name)
        TextView orderFoodName;
        @BindView(R.id.order_food_price)
        TextView orderFoodPrice;
        @BindView(R.id.order_food_number)
        TextView orderFoodNumber;

        @BindString(R.string.money_symbol)
        String moneySymbol;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
