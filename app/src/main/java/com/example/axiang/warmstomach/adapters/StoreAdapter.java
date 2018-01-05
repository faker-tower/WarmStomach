package com.example.axiang.warmstomach.adapters;

import android.content.Context;
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
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.interfaces.StoreItemPressListener;
import com.example.axiang.warmstomach.util.CalculateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/1/1.
 */

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Store> storeList;
    private StoreItemPressListener listener;
    private FooterViewHolder footerViewHolder;

    public StoreAdapter(Context context, List<Store> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    public void updateData(List<Store> storeList) {
        this.storeList = storeList;
        notifyDataSetChanged();
    }

    public void setListener(StoreItemPressListener listener) {
        this.listener = listener;
    }

    public void updateFooter(int imageResourceId, String content) {
         Glide.with(context)
                .load(imageResourceId)
                .apply(new RequestOptions().centerCrop())
                .into(footerViewHolder.recyclerFooterImage);
        footerViewHolder.recyclerFooterText.setText(content);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == storeList.size()) {
            return C.RECYCLER_VIEW_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == C.RECYCLER_VIEW_FOOTER) {
            footerViewHolder = new FooterViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.recycler_footer, parent, false));
            return footerViewHolder;
        }
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_store, parent, false);
        StoreViewHolder holder = new StoreViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position < storeList.size()) {
            StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
            Store store = storeList.get(position);
            Glide.with(context)
                    .load(store.getStoreAvatar())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.error))
                    .into(storeViewHolder.itemStoreAvatar);
            storeViewHolder.itemStoreName.setText(store.getStoreName());
            storeViewHolder.itemStoreType.setText(store.getStoreType());
            storeViewHolder.itemStoreMonthSoldOn.setText(store.getStoreMonthSoldOn().intValue() + "");
            storeViewHolder.itemStoreStartingPrice.setText(store.getStoreStartingPrice().intValue() + "");
            storeViewHolder.itemStoreDelivery.setText(store.getStoreDeliveryFee().floatValue() + "");
            storeViewHolder.itemStorePerCapita.setText(store.getStorePerCapita().intValue() + "");
            if (store.getStoreOnlinePayment() == null || !store.getStoreOnlinePayment().booleanValue()) {
                storeViewHolder.itemStoreOnlinePayment.setVisibility(View.GONE);
            }
            if (store.getStoreCashOnDelivery() == null || !store.getStoreCashOnDelivery().booleanValue()) {
                storeViewHolder.itemStoreCashOnDelivery.setVisibility(View.GONE);
            }
            if (store.getStorePickUp() == null || !store.getStorePickUp().booleanValue()) {
                storeViewHolder.itemStorePickUp.setVisibility(View.GONE);
            }
            String distanceStr = "";
            double distance = CalculateUtil.getDistance(store.getStoreLocation().getLongitude(),
                    store.getStoreLocation().getLatitude());
            if (distance >= 1000) {
                distance /= 1000;
                distanceStr = Math.round(distance) + "km";
            } else {
                distanceStr = Math.round(distance) + "m";
            }
            storeViewHolder.itemStoreDistance.setText(distanceStr);

            storeViewHolder.itemStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClicked(holder.getAdapterPosition());
                    }
                }
            });

            storeViewHolder.itemStore.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null) {
                        listener.OnItemLongClicked(holder.getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return storeList == null || storeList.isEmpty() ? 0 : storeList.size() + 1;
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_store)
        RelativeLayout itemStore;
        @BindView(R.id.item_store_avatar)
        ImageView itemStoreAvatar;
        @BindView(R.id.item_store_name)
        TextView itemStoreName;
        @BindView(R.id.item_store_type)
        TextView itemStoreType;
        @BindView(R.id.item_store_month_sold_on)
        TextView itemStoreMonthSoldOn;
        @BindView(R.id.item_store_starting_price)
        TextView itemStoreStartingPrice;
        @BindView(R.id.item_store_delivery)
        TextView itemStoreDelivery;
        @BindView(R.id.item_store_per_capita)
        TextView itemStorePerCapita;
        @BindView(R.id.item_store_online_payment)
        TextView itemStoreOnlinePayment;
        @BindView(R.id.item_store_cash_on_delivery)
        TextView itemStoreCashOnDelivery;
        @BindView(R.id.item_store_pick_up)
        TextView itemStorePickUp;
        @BindView(R.id.item_store_more)
        ImageView itemStoreMore;
        @BindView(R.id.item_store_distance)
        TextView itemStoreDistance;

        public StoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_footer_image)
        ImageView recyclerFooterImage;
        @BindView(R.id.recycler_footer_text)
        TextView recyclerFooterText;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
