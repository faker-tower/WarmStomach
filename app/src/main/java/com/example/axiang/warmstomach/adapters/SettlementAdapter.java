package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Settle;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.enums.CartCheckState;
import com.example.axiang.warmstomach.interfaces.OnCartListener;
import com.example.axiang.warmstomach.widget.CircleImageView;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/2/19.
 */

public class SettlementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Object> mDatas;
    private List<CartCheckState> mStates;
    private boolean isEditing;
    private OnCartListener mOnCartListener;

    public SettlementAdapter(Context context,
                             List<Object> datas,
                             List<CartCheckState> states,
                             boolean isEditing) {
        this.mContext = context;
        this.mDatas = datas;
        this.mStates = states;
        this.isEditing = isEditing;
    }

    public void setDatas(List<Object> datas) {
        this.mDatas = datas;
    }

    public void setSingleData(int position, Object data) {
        this.mDatas.set(position, data);
    }

    public void setStates(List<CartCheckState> states) {
        this.mStates = states;
    }

    public void setSingleState(int position, CartCheckState state) {
        this.mStates.set(position, state);
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public void setOnCartListener(OnCartListener onCartListener) {
        this.mOnCartListener = onCartListener;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = mDatas.get(position);
        if (obj instanceof Store) {
            return C.SETTLE_TYPE_STORE;
        } else if (obj instanceof Cart) {
            return C.SETTLE_TYPE_CART;
        } else {
            return C.SETTLE_TYPE_SETTLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case C.SETTLE_TYPE_STORE:
                return new StoreViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_cart_store, parent, false));
            case C.SETTLE_TYPE_CART:
                return new CartViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_cart_food, parent, false));
            case C.SETTLE_TYPE_SETTLE:
                return new SettleViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_cart_settle, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StoreViewHolder) {
            StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
            initStore(storeViewHolder, position);
        } else if (holder instanceof CartViewHolder) {
            CartViewHolder cartViewHolder = (CartViewHolder) holder;
            initCart(cartViewHolder, position);
        } else {
            SettleViewHolder settleViewHolder = (SettleViewHolder) holder;
            initSettle(settleViewHolder, position);
        }
    }

    private void initSettle(SettleViewHolder settleViewHolder, int position) {
        Settle settle = (Settle) mDatas.get(position);
        settleViewHolder.cartSettleAllPrice
                .setText(settleViewHolder.moneySymbol + String.valueOf(settle.getNowAllPrice()));
        settleViewHolder.cartSettleBt.setText(settle.getSettleButtonText());
        if (settle.isSettleButtonClickable()) {
            settleViewHolder.cartSettleBt.setTextColor(settleViewHolder.canClickTextColor);
            settleViewHolder.cartSettleBt.setBackgroundColor(settleViewHolder.canClickBgColor);
        } else {
            settleViewHolder.cartSettleBt.setTextColor(settleViewHolder.canNotClickTextColor);
            settleViewHolder.cartSettleBt.setBackgroundColor(settleViewHolder.canNotClickBgColor);
        }
        settleViewHolder.cartSettleBt.setClickable(settle.isSettleButtonClickable());
        if (isEditing) {
            settleViewHolder.cartSettleLayout.setVisibility(View.GONE);
        } else {
            settleViewHolder.cartSettleLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initCart(final CartViewHolder cartViewHolder, final int position) {
        Cart cart = (Cart) mDatas.get(position);
        StoreFood food = cart.getStoreFood();
        Glide.with(mContext)
                .load(food.getFoodPicture())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(cartViewHolder.cartFoodAvatar);
        cartViewHolder.cartFoodName.setText(food.getFoodName());
        cartViewHolder.cartFoodPrice.setText(cartViewHolder.moneySymbol
                + String.valueOf(food.getFoodPrice().doubleValue()));
        cartViewHolder.cartFoodNumber.setText("x " + String.valueOf(cart.getNumber()));
        cartViewHolder.cartFoodAllPrice.setText(cartViewHolder.moneySymbol
                + (String.valueOf(food.getFoodPrice().doubleValue()
                * cart.getNumber())));
        cartViewHolder.cartFoodCheck.setChecked(isCheckAllFoods(position));
        cartViewHolder.cartFoodPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onFoodClicked(position);
                }
            }
        });
        cartViewHolder.cartFoodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onFoodClicked(position);
                }
            }
        });
        cartViewHolder.cartFoodLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onFoodLongClicked(position);
                }
                return true;
            }
        });
        cartViewHolder.cartFoodCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onFoodChecked(cartViewHolder.cartFoodCheck.isChecked(),
                            position);
                }
            }
        });
    }

    private void initStore(final StoreViewHolder storeViewHolder, final int position) {
        Store store = (Store) mDatas.get(position);
        Glide.with(mContext)
                .load(store.getStoreAvatar())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(storeViewHolder.cartStoreAvatar);
        storeViewHolder.cartStoreName.setText(store.getStoreName());
        storeViewHolder.checkAll.setChecked(isCheckAllFoods(position));
        storeViewHolder.cartStoreAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onStoreClicked(position);
                }
            }
        });
        storeViewHolder.cartStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onStoreClicked(position);
                }
            }
        });
        storeViewHolder.cartStoreSharp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onStoreClicked(position);
                }
            }
        });
        storeViewHolder.checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCartListener != null) {
                    mOnCartListener.onStoreChecked(storeViewHolder.checkAll.isChecked(), position);
                }
            }
        });
    }

    private boolean isCheckAllFoods(int position) {
        if (mStates.get(position) == CartCheckState.CHECK_STATE) {
            return true;
        } else {
            return false;
        }
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.check_all)
        CheckBox checkAll;
        @BindView(R.id.cart_store_avatar)
        CircleImageView cartStoreAvatar;
        @BindView(R.id.cart_store_name)
        TextView cartStoreName;
        @BindView(R.id.cart_store_sharp)
        TextView cartStoreSharp;

        StoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_food_check)
        CheckBox cartFoodCheck;
        @BindView(R.id.cart_food_avatar)
        ImageView cartFoodAvatar;
        @BindView(R.id.cart_food_name)
        TextView cartFoodName;
        @BindView(R.id.cart_food_price)
        TextView cartFoodPrice;
        @BindView(R.id.cart_food_number)
        TextView cartFoodNumber;
        @BindView(R.id.cart_food_all_price)
        TextView cartFoodAllPrice;
        @BindView(R.id.cart_food_layout)
        RelativeLayout cartFoodLayout;

        @BindString(R.string.money_symbol)
        String moneySymbol;

        CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class SettleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_settle_all_price)
        TextView cartSettleAllPrice;
        @BindView(R.id.cart_settle_bt)
        Button cartSettleBt;
        @BindView(R.id.cart_settle_layout)
        RelativeLayout cartSettleLayout;

        @BindColor(R.color.position_loading_layout_bg)
        int canNotClickBgColor;
        @BindColor(R.color.colorAccent)
        int canClickBgColor;
        @BindColor(R.color.colorSecondaryText)
        int canNotClickTextColor;
        @BindColor(R.color.colorPrimaryText)
        int canClickTextColor;

        @BindString(R.string.money_symbol)
        String moneySymbol;

        SettleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
