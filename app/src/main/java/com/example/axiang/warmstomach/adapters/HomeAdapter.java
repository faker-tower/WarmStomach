package com.example.axiang.warmstomach.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreAd;
import com.example.axiang.warmstomach.data.StoreType;
import com.example.axiang.warmstomach.interfaces.AdColumnOnClickListener;
import com.example.axiang.warmstomach.interfaces.HomeListener;
import com.example.axiang.warmstomach.interfaces.OnAdColumnItemListener;
import com.example.axiang.warmstomach.interfaces.OnStoreItemListener;
import com.example.axiang.warmstomach.util.CalculateUtil;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by a2389 on 2018/2/3.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private Store mStore;
    private List<Store> mStores;
    private OnStoreItemListener mStoreItemListener;

    // 广告栏模块
    private List<StoreAd> mStoreAds;
    private OnAdColumnItemListener mAdColumnItemListener;
    private AdColumnViewHolder mAdColumnViewHolder;
    private CarouselAdapter mCarouselAdapter;
    private List<String> mCarouselImageUrlList;
    private int mCurrentCarouselItem = 75600;
    private boolean isCarouselScrolling = false;
    private long mLastReplaceTime = System.currentTimeMillis();
    private HomeListener mHomeListener = new HomeListener() {
        @Override
        public void timingToReplaceAdPic() {
            if (!isCarouselScrolling && mAdColumnViewHolder != null) {
                if (System.currentTimeMillis() - mLastReplaceTime >= 3000) {
                    if (mCurrentCarouselItem == 75600) {
                        mAdColumnViewHolder.carousel
                                .setCurrentItem(++mCurrentCarouselItem, false);
                    } else {
                        mAdColumnViewHolder.carousel.setCurrentItem(++mCurrentCarouselItem);
                    }
                    updateDots();
                    mLastReplaceTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void updateFooter(int imageResourceId, String content) {
            Glide.with(mContext)
                    .load(imageResourceId)
                    .apply(new RequestOptions().centerCrop())
                    .into(mFooterViewHolder.recyclerFooterImage);
            mFooterViewHolder.recyclerFooterText.setText(content);
        }
    };

    // 商店类型模块
    private StoreTypeAdapter mStoreTypeAdapter;
    private List<StoreType> mStoreTypes;

    // 底部加载更多模块
    private FooterViewHolder mFooterViewHolder;

    public HomeAdapter(Context context,
                       @NonNull List<StoreAd> storeAds,
                       @NonNull List<StoreType> storeTypes,
                       @NonNull Store store,
                       @NonNull List<Store> stores) {
        super();
        this.mContext = context;
        this.mStoreAds = storeAds;
        this.mStoreTypes = storeTypes;
        this.mStore = store;
        this.mStores = stores;
    }

    public HomeListener getHomeListener() {
        return mHomeListener;
    }

    public void setOnStoreItemListener(OnStoreItemListener storeItemListener) {
        this.mStoreItemListener = storeItemListener;
    }

    public void setOnAdColumnItemListener(OnAdColumnItemListener adColumnItemListener) {
        this.mAdColumnItemListener = adColumnItemListener;
    }

    public void setStoreTypes(List<StoreType> storeTypes) {
        this.mStoreTypes = storeTypes;
        List<Map<String, Object>> storeTypeList = getStoreTypeList();
        mStoreTypeAdapter.setData(storeTypeList);
        mStoreTypeAdapter.notifyDataSetChanged();
    }

    public void setStore(Store store) {
        this.mStore = store;
    }

    public void setStores(List<Store> stores, boolean isClear) {
        if (this.mStores == null) {
            this.mStores = new ArrayList<>();
        }
        if (isClear) {
            this.mStores = stores;
        } else {
            this.mStores.addAll(stores);
        }
    }

    public void setStoreAds(List<StoreAd> storeAds) {
        if (this.mStoreAds == null) {
            this.mStoreAds = new ArrayList<>();
        }
        if (!this.mStoreAds.isEmpty()) {
            this.mStoreAds.clear();
        }
        this.mStoreAds.addAll(storeAds);
        if (mCarouselImageUrlList == null) {
            mCarouselImageUrlList = new ArrayList<>();
        }
        if (!mCarouselImageUrlList.isEmpty()) {
            mCarouselImageUrlList.clear();
        }
        for (int i = 0; i < this.mStoreAds.size(); i++) {
            mCarouselImageUrlList.add(this.mStoreAds.get(i).getStoreAdPicture());
        }
        mCarouselAdapter.setImageUrls(mCarouselImageUrlList);
        mCarouselAdapter.notifyDataSetChanged();
        mCurrentCarouselItem = 75600;
        updateDots();
        mLastReplaceTime = System.currentTimeMillis();
    }

    @Override
    public int getItemCount() {
        return mStores.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return C.ADAPTER_AD_COLUMN;
        } else if (position == 1) {
            return C.ADAPTER_STORE_TYPE;
        } else if (position == 2) {
            return C.ADAPTER_SUPER_STORE;
        } else if (position == mStores.size() + 4 - 1) {
            return C.RECYCLER_VIEW_FOOTER;
        } else {
            return C.ADAPTER_STORE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case C.ADAPTER_AD_COLUMN:
                mAdColumnViewHolder = new AdColumnViewHolder(
                        LayoutInflater.from(mContext)
                                .inflate(R.layout.home_ad_column, parent, false));
                initAdColumnLayout();
                return mAdColumnViewHolder;
            case C.ADAPTER_STORE_TYPE:
                return new StoreTypeViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.home_store_type, parent, false));
            case C.ADAPTER_SUPER_STORE:
                return new SuperStoreViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.home_super_store, parent, false));
            case C.RECYCLER_VIEW_FOOTER:
                mFooterViewHolder = new FooterViewHolder(
                        LayoutInflater.from(mContext)
                                .inflate(R.layout.recycler_footer, parent, false));
                return mFooterViewHolder;
            case C.ADAPTER_STORE:
                return new StoreViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.item_store, parent, false));
            default:
                return null;
        }
    }

    // 初始化广告栏布局界面
    private void initAdColumnLayout() {
        mCarouselImageUrlList = new ArrayList<>();
        for (int i = 0; i < mStoreAds.size(); i++) {
            mCarouselImageUrlList.add(mStoreAds.get(i).getStoreAdPicture());
        }
        mAdColumnViewHolder.mDots = new LinearLayout(mContext);
        FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fllp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        fllp.setMargins(0, 0, 0, mAdColumnViewHolder.dotsMaginBottomSize);
        mAdColumnViewHolder.mDots.setLayoutParams(fllp);
        mAdColumnViewHolder.mDots.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < mCarouselImageUrlList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lllp.setMargins(0, 0, mAdColumnViewHolder.dotsMaginRightSize, 0);
            imageView.setLayoutParams(lllp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (i == 0) {
                imageView.setImageResource(R.drawable.blue_dots);
            } else {
                imageView.setImageResource(R.drawable.white_dots);
            }
            imageView.setTag(i);
            mAdColumnViewHolder.mDots.addView(imageView);
        }
        mAdColumnViewHolder.adColumn.addView(mAdColumnViewHolder.mDots);
        mAdColumnViewHolder.carousel.setCurrentItem(mCurrentCarouselItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mStores.size() + 4 - 1) {
            return;
        }
        switch (position) {
            case 0:
                AdColumnViewHolder adColumnViewHolder = (AdColumnViewHolder) holder;
                initAdColumn(adColumnViewHolder);
                break;
            case 1:
                StoreTypeViewHolder storeTypeViewHolder = (StoreTypeViewHolder) holder;
                initStoreType(storeTypeViewHolder);
                break;
            case 2:
                SuperStoreViewHolder superStoreViewHolder = (SuperStoreViewHolder) holder;
                initSuperStore(superStoreViewHolder);
                break;
            default:
                StoreViewHolder storeViewHolder = (StoreViewHolder) holder;
                initStore(storeViewHolder, position - 3);
                break;
        }
    }

    private void initStore(final StoreViewHolder viewHolder, int position) {
        Store store = mStores.get(position);
        Glide.with(mContext)
                .load(store.getStoreAvatar())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(viewHolder.itemStoreAvatar);
        viewHolder.itemStoreName.setText(store.getStoreName());
        viewHolder.itemStoreType.setText(store.getStoreType());
        viewHolder.itemStoreMonthSoldOn.setText(store.getStoreMonthSoldOn().intValue() + "");
        viewHolder.itemStoreStartingPrice.setText(store.getStoreStartingPrice().intValue() + "");
        viewHolder.itemStoreDelivery.setText(store.getStoreDeliveryFee().floatValue() + "");
        viewHolder.itemStorePerCapita.setText(store.getStorePerCapita().intValue() + "");
        if (store.getStoreOnlinePayment() == null || !store.getStoreOnlinePayment().booleanValue()) {
            viewHolder.itemStoreOnlinePayment.setVisibility(View.GONE);
        }
        if (store.getStoreCashOnDelivery() == null || !store.getStoreCashOnDelivery().booleanValue()) {
            viewHolder.itemStoreCashOnDelivery.setVisibility(View.GONE);
        }
        if (store.getStorePickUp() == null || !store.getStorePickUp().booleanValue()) {
            viewHolder.itemStorePickUp.setVisibility(View.GONE);
        }
        String distanceStr = "";
        double localLongitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""));
        double localLatitude = Double.valueOf(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""));
        double distance = CalculateUtil.getDistance(localLongitude,
                localLatitude,
                store.getStoreLocation().getLongitude(),
                store.getStoreLocation().getLatitude());
        if (distance >= 1000) {
            distance /= 1000;
            distanceStr = Math.round(distance) + "km";
        } else {
            distanceStr = Math.round(distance) + "m";
        }
        viewHolder.itemStoreDistance.setText(distanceStr);

        viewHolder.itemStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStoreItemListener != null) {
                    mStoreItemListener.onItemClicked(viewHolder.getAdapterPosition() - 3);
                }
            }
        });
    }

    private void initSuperStore(SuperStoreViewHolder viewHolder) {
        viewHolder.superStoreName.setText(mStore.getStoreName());
        viewHolder.superStoreType.setText(mStore.getStoreType());
        Glide.with(mContext)
                .load(mStore.getStoreAvatar())
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(viewHolder.superStoreAvatar);
    }

    private void initStoreType(StoreTypeViewHolder viewHolder) {
        List<Map<String, Object>> storeTypeList = getStoreTypeList();
        mStoreTypeAdapter = new StoreTypeAdapter(mContext,
                storeTypeList,
                R.layout.item_store_type,
                new String[]{C.STORE_TYPE_IMAGE, C.STORE_TYPE_TEXT},
                new int[]{R.id.store_type_image, R.id.store_type_text});
        viewHolder.storeType.setAdapter(mStoreTypeAdapter);
        viewHolder.storeType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private List<Map<String, Object>> getStoreTypeList() {
        List<Map<String, Object>> storeTypeList = new ArrayList<>();
        for (int i = 0; i < mStoreTypes.size(); i++) {
            Map<String, Object> storeTypeMap = new HashMap<>();
            storeTypeMap.put(C.STORE_TYPE_IMAGE, mStoreTypes.get(i).getStoreTypeIcon());
            storeTypeMap.put(C.STORE_TYPE_TEXT, mStoreTypes.get(i).getStoreTypeName());
            storeTypeList.add(storeTypeMap);
        }
        return storeTypeList;
    }

    private void initAdColumn(AdColumnViewHolder viewHolder) {
        mCarouselAdapter = new CarouselAdapter(mContext, mCarouselImageUrlList);
        mCarouselAdapter.setListener(mAdColumnItemListener);
        viewHolder.carousel.setAdapter(mCarouselAdapter);
        viewHolder.carousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentCarouselItem = position;
                updateDots();
                isCarouselScrolling = false;
                mLastReplaceTime = System.currentTimeMillis();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1) {
                    isCarouselScrolling = true;
                } else {
                    isCarouselScrolling = false;
                }
            }
        });
    }

    // 更新广告栏圆形底标图片
    private void updateDots() {
        for (int i = 0; i < mCarouselImageUrlList.size(); i++) {
            ImageView imageView = (ImageView) mAdColumnViewHolder.mDots.getChildAt(i);
            if (mCurrentCarouselItem % mCarouselImageUrlList.size() == i) {
                imageView.setImageResource(R.drawable.blue_dots);
            } else {
                imageView.setImageResource(R.drawable.white_dots);
            }
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_footer_image)
        ImageView recyclerFooterImage;
        @BindView(R.id.recycler_footer_text)
        TextView recyclerFooterText;

        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {

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

        StoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class AdColumnViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ad_column)
        FrameLayout adColumn;
        @BindView(R.id.carousel)
        ViewPager carousel;

        @BindDimen(R.dimen.dp_10)
        int dotsMaginBottomSize;
        @BindDimen(R.dimen.dp_5)
        int dotsMaginRightSize;

        LinearLayout mDots;

        AdColumnViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class StoreTypeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.store_type)
        GridView storeType;

        StoreTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class SuperStoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.super_store)
        RelativeLayout superStore;
        @BindView(R.id.super_store_avatar)
        ImageView superStoreAvatar;
        @BindView(R.id.super_store_name)
        TextView superStoreName;
        @BindView(R.id.super_store_type)
        TextView superStoreType;

        SuperStoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
