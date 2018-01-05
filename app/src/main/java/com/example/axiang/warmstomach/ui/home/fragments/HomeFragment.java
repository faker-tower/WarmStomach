package com.example.axiang.warmstomach.ui.home.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.adapters.CarouselAdapter;
import com.example.axiang.warmstomach.adapters.StoreAdapter;
import com.example.axiang.warmstomach.contracts.HomeContract;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreAd;
import com.example.axiang.warmstomach.interfaces.AdColumnOnClickListener;
import com.example.axiang.warmstomach.interfaces.StoreItemPressListener;
import com.example.axiang.warmstomach.ui.home.MainActivity;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.widget.CustomNestedScrollView;
import com.example.axiang.warmstomach.widget.CustomSnackbar;
import com.example.axiang.warmstomach.widget.CyclicalTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by a2389 on 2017/12/2.
 */

public class HomeFragment extends Fragment implements HomeContract.View,
        ViewPager.OnPageChangeListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    // 当前Fragment是否显示
    private boolean isShowing = true;

    // 绑定视图
    @BindView(R.id.store_type)
    GridView storeType;
    @BindView(R.id.super_store)
    RelativeLayout superStore;
    @BindView(R.id.home_recycler_view)
    RecyclerView homeRecyclerView;
    @BindView(R.id.home_nested_scroll_view)
    CustomNestedScrollView homeNestedScrollView;
    @BindView(R.id.home_Rrefresh_layout)
    SwipeRefreshLayout homeRrefreshLayout;
    @BindView(R.id.home_shopping_cart)
    FloatingActionButton homeShoppingCart;
    @BindView(R.id.home_frame_layout)
    FrameLayout homeFrameLayout;
    @BindView(R.id.ad_column)
    FrameLayout adColumn;
    @BindView(R.id.carousel)
    ViewPager carousel;
    @BindView(R.id.home_coordinator_layout)
    CoordinatorLayout homeCoordinatorLayout;
    @BindView(R.id.super_store_avatar)
    ImageView superStoreAvatar;
    @BindView(R.id.super_store_name)
    TextView superStoreName;
    @BindView(R.id.super_store_type)
    TextView superStoreType;

    // 绑定dimen
    @BindDimen(R.dimen.dp_12)
    int shoppingCartMaginRightSize;
    @BindDimen(R.dimen.dp_10)
    int dotsMaginBottomSize;
    @BindDimen(R.dimen.dp_4)
    int dotsMaginRightSize;
    @BindDimen(R.dimen.dp_48)
    int footerHeightSize;


    // 绑定integer
    @BindInt(R.integer.integer_200)
    int totalAnimDuration;
    @BindInt(R.integer.integer_100)
    int alphaAnimDuration;

    // 绑定String
    @BindString(R.string.positioning_failed)
    String positioningFailedText;
    @BindString(R.string.waiting_for_positioning)
    String waitingForPositioning;
    @BindString(R.string.network_error)
    String networkErrorText;
    @BindString(R.string.go_check_it_out)
    String goCheckItOutText;
    @BindString(R.string.unknown_error)
    String unknownErrorText;
    @BindString(R.string.search_business)
    String searchBusiness;
    @BindString(R.string.search_business_failed)
    String searchBusinessFailed;
    @BindString(R.string.no_data)
    String noData;

    private String[] storeTypes = new String[]{"米粉米线", "甜品", "中式简餐", "西式快餐",
            "饺子馄饨", "炸鸡炸串", "奶茶果汁", "中式烤肉", "面馆", "日本料理", "意面披萨", "木桶饭"};

    private int[] storeTypeIcons = new int[]{R.drawable.icon_meter, R.drawable.icon_desserts,
            R.drawable.icon_simple, R.drawable.icon_fast, R.drawable.icon_dumplings,
            R.drawable.icon_fried, R.drawable.icon_tea, R.drawable.icon_roast,
            R.drawable.icon_noodle, R.drawable.cuisine, R.drawable.icon_pisa,
            R.drawable.icon_rice};

    private Unbinder unbinder;

    private MainActivity mainActivity;

    private HomeContract.Presenter presenter;

    private CustomSnackbar snackbar;

    // 商店类型模块
    private SimpleAdapter storeTypeAdapter;

    // 定位模块
    private View loadingPositionView;
    private PositionViewHolder positionViewHolder;
    private boolean isPositeSuccess = false;

    // 购物车模块
    private AnimatorSet shoppingCartEnterAnim;
    private AnimatorSet shoppingCartExitAnim;
    private boolean isShoppingCartEntering = true;
    private long shoppingCartCurrentTime;
    private Timer shoppingCartTimer;
    private TimerTask shoppingCartTask;

    // 广告栏模块
    private CarouselAdapter carouselAdapter;
    private List<StoreAd> responseStoreAdList;
    private List<String> carouselImageUrlList;
    private LinearLayout dots;
    private int currentCarouselItem = 75600;
    private Timer carouselTimer;
    private TimerTask carouselTask;
    private boolean isCarouselScrolling = false;
    private long lastReplaceTime;

    // 为您推荐模块
    private Store respnseStore;

    //商店数据模块
    private StoreAdapter storeAdapter;
    private List<Store> responseStoreList;
    private int offset = 0;
    private boolean isLoadMoreStoreData = false;
    private boolean isNoData = false;

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case C.SHOOPING_CART_NORMAL:
                    if (!shoppingCartEnterAnim.isRunning() && !shoppingCartExitAnim.isRunning()) {
                        shoppingCartEnterAnim.start();
                        shoppingCartCurrentTime = System.currentTimeMillis();
                    }
                    break;
                case C.SHOOPING_CART_HIDDEN:
                    if (!shoppingCartEnterAnim.isRunning() && !shoppingCartExitAnim.isRunning()) {
                        shoppingCartExitAnim.start();
                        shoppingCartCurrentTime = System.currentTimeMillis();
                    }
                    break;
                case C.CAROUSEL_IMAGE_REPLACE:
                    if (!isCarouselScrolling && carousel != null) {
                        if (System.currentTimeMillis() - lastReplaceTime >= 3000) {
                            carousel.setCurrentItem(++currentCarouselItem);
                            dotsUpdate();
                            lastReplaceTime = System.currentTimeMillis();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        loadingPositionView = inflater.inflate(R.layout.position_loading,
                homeFrameLayout,
                false);
        initView();
        return view;
    }

    @Override
    public void loadMoreStoreDataFinish() {
        isLoadMoreStoreData = false;
    }

    @Override
    public void showPositionSuccess(String county,
                                    String city,
                                    String district,
                                    String street,
                                    String addrStr,
                                    String locationDescribe) {
        isPositeSuccess = true;
        mainActivity.onPositionChanged(county
                + city
                + district
                + street
                + addrStr
                + locationDescribe);
        if (isShowing) {
            homeFrameLayout.removeView(loadingPositionView);
            homeRrefreshLayout.setRefreshing(true);
            initData();
        }
    }

    @Override
    public void showPositionError() {
        mainActivity.onPositionChanged(positioningFailedText);
        if (isShowing) {
            positionViewHolder.cyclicalTextView.setText(R.string.positioning_failed);
            positionViewHolder.btRetry.setVisibility(View.VISIBLE);
            ToastUtil.showToast(positioningFailedText);
        }
    }

    @Override
    public void initView() {
        initShoppingCartAnim();
        initStoreType();

        homeRrefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.position_loading_layout_bg),
                ContextCompat.getColor(getContext(), R.color.register_get_vertify),
                ContextCompat.getColor(getContext(), R.color.colorAccent));
        homeRrefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                isNoData = false;
            }
        });

        // 设置NestedScrollView滚动时购物车图标隐藏
        homeNestedScrollView.setListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v,
                                       int scrollX,
                                       int scrollY,
                                       int oldScrollX,
                                       int oldScrollY) {
                if (isShoppingCartEntering) {
                    handle.sendEmptyMessage(C.SHOOPING_CART_HIDDEN);
                }
                if (scrollY >= v.getChildAt(0).getMeasuredHeight() - v.getHeight()
                        - footerHeightSize) {
                    if (!homeRrefreshLayout.isRefreshing()
                            && !isLoadMoreStoreData
                            && !isNoData) {
                        isLoadMoreStoreData = true;
                        presenter.loadInterfaceStoreData(offset);
                        storeAdapter.updateFooter(R.drawable.image_loading,
                                searchBusiness);
                    }
                }
            }
        });
        positionViewHolder = new PositionViewHolder(loadingPositionView);
    }

    private void initStoreType() {
        List<Map<String, Object>> storeTypeList = new ArrayList<>();
        for (int i = 0; i < storeTypeIcons.length; i++) {
            Map<String, Object> storeTypeMap = new HashMap<>();
            storeTypeMap.put(C.STORE_TYPE_IMAGE, storeTypeIcons[i]);
            storeTypeMap.put(C.STORE_TYPE_TEXT, storeTypes[i]);
            storeTypeList.add(storeTypeMap);
        }
        storeTypeAdapter = new SimpleAdapter(getContext(),
                storeTypeList,
                R.layout.item_store_type,
                new String[]{C.STORE_TYPE_IMAGE, C.STORE_TYPE_TEXT},
                new int[]{R.id.store_type_image, R.id.store_type_text});
        storeType.setAdapter(storeTypeAdapter);
        storeType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick: " + "item: " + i);
            }
        });
    }

    private void initShoppingCartAnim() {
        // 手机屏幕宽度
        final int windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

        // 初始化购物车进场动画
        ObjectAnimator transluteEnterAnim = ObjectAnimator.ofFloat(homeShoppingCart,
                "translationX",
                windowWidth - homeShoppingCart.getWidth() / 2,
                windowWidth - homeShoppingCart.getWidth() - shoppingCartMaginRightSize);
        transluteEnterAnim.setDuration(totalAnimDuration);
        ObjectAnimator alphaEnterAnim = ObjectAnimator.ofFloat(homeShoppingCart,
                "alpha",
                0.5f,
                1.0f);
        alphaEnterAnim.setDuration(alphaAnimDuration);
        shoppingCartEnterAnim = new AnimatorSet();
        shoppingCartEnterAnim.playTogether(transluteEnterAnim, alphaEnterAnim);
        shoppingCartEnterAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isShoppingCartEntering = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (homeShoppingCart != null) {
                    homeShoppingCart.setX(windowWidth - homeShoppingCart.getWidth()
                            - shoppingCartMaginRightSize);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        // 初始化购物车退场动画
        ObjectAnimator transluteExitAnim = ObjectAnimator.ofFloat(homeShoppingCart,
                "translationX",
                windowWidth - homeShoppingCart.getWidth() - shoppingCartMaginRightSize,
                windowWidth - homeShoppingCart.getWidth() / 2);
        transluteExitAnim.setDuration(totalAnimDuration);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(homeShoppingCart,
                "alpha",
                1.0f,
                0.5f);
        alphaAnim.setDuration(alphaAnimDuration);
        shoppingCartExitAnim = new AnimatorSet();
        shoppingCartExitAnim.playTogether(transluteExitAnim, alphaAnim);
        shoppingCartExitAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isShoppingCartEntering = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (homeShoppingCart != null) {
                    homeShoppingCart.setX(windowWidth - homeShoppingCart.getWidth() / 2);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        homeShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShoppingCartEntering) {
                    handle.sendEmptyMessage(C.SHOOPING_CART_NORMAL);
                }
                Intent intent = new Intent();
            }
        });

        // 开启定时3秒显示购物车图标
        shoppingCartCurrentTime = System.currentTimeMillis();
        shoppingCartTimer = new Timer();
        shoppingCartTask = new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - shoppingCartCurrentTime >= 3000) {
                    if (!isShoppingCartEntering) {
                        handle.sendEmptyMessage(C.SHOOPING_CART_NORMAL);
                    }
                }
            }
        };
        shoppingCartTimer.schedule(shoppingCartTask, 3000, 3000);
        lastReplaceTime = System.currentTimeMillis();
    }

    @Override
    public void onResume() {
        super.onResume();
        isShowing = true;
        if (!isPositeSuccess) {
            if (TextUtils.isEmpty(SharedPreferencesUtil
                    .getSharedPreferences()
                    .getString(C.LONGITUDE, ""))
                    || TextUtils.isEmpty(SharedPreferencesUtil
                    .getSharedPreferences()
                    .getString(C.LATITUDE, ""))) {
                homeFrameLayout.addView(loadingPositionView);
                presenter.start();
            } else {
                if (loadingPositionView.isShown()) {
                    homeFrameLayout.removeView(loadingPositionView);
                }
                initData();
            }
        } else {
            if (loadingPositionView.isShown()) {
                homeFrameLayout.removeView(loadingPositionView);
            }
            initData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowing = false;
    }

    private void initData() {
        if (homeRrefreshLayout.isRefreshing()) {
            if (responseStoreList != null && !responseStoreList.isEmpty()) {
                responseStoreList.clear();
            }
            offset = 0;
            presenter.loadStoreAdData();
            presenter.loadSuperStoreData();
            presenter.loadInterfaceStoreData(offset);
            homeRrefreshLayout.setRefreshing(false);
        } else {
            if (responseStoreAdList == null || responseStoreAdList.isEmpty()) {
                presenter.loadStoreAdData();
            } else {
                updateStoreAd(responseStoreAdList);
                carousel.setCurrentItem(currentCarouselItem);
                dotsUpdate();
            }

            if (respnseStore == null) {
                presenter.loadSuperStoreData();
            } else {
                updateSuperStore(respnseStore);
            }

            if (responseStoreList == null) {
                presenter.loadInterfaceStoreData(offset);
            } else {
                updateStore(responseStoreList);
            }
        }
    }

    @Override
    public void updateStoreAd(List<StoreAd> storeAdList) {
        if (isShowing) {
            responseStoreAdList = storeAdList;
            carouselImageUrlList = new ArrayList<>();
            for (int i = 0; i < storeAdList.size(); i++) {
                carouselImageUrlList.add(storeAdList.get(i).getStoreAdPicture());
            }
            carouselAdapter = new CarouselAdapter(getContext(), carouselImageUrlList);
            carouselAdapter.setListener(new AdColumnOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String storeId = responseStoreAdList.get(position).getStoreId();
                }
            });
            carousel.setAdapter(carouselAdapter);
            carousel.addOnPageChangeListener(this);
            dots = new LinearLayout(getContext());
            FrameLayout.LayoutParams fllp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fllp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            fllp.setMargins(0, 0, 0, dotsMaginBottomSize);
            dots.setLayoutParams(fllp);
            dots.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < carouselImageUrlList.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lllp.setMargins(0, 0, dotsMaginRightSize, 0);
                imageView.setLayoutParams(lllp);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (i == 0) {
                    imageView.setImageResource(R.drawable.blue_dots);
                } else {
                    imageView.setImageResource(R.drawable.white_dots);
                }
                imageView.setTag(i);
                dots.addView(imageView);
            }
            adColumn.addView(dots);
            carousel.setCurrentItem(currentCarouselItem);

            // 开启4秒切换功能
            carouselTimer = new Timer();
            carouselTask = new TimerTask() {
                @Override
                public void run() {
                    handle.sendEmptyMessage(C.CAROUSEL_IMAGE_REPLACE);
                }
            };
            carouselTimer.schedule(carouselTask, 3000, 3000);
        }
    }

    private void dotsUpdate() {
        for (int i = 0; i < carouselImageUrlList.size(); i++) {
            ImageView imageView = (ImageView) dots.getChildAt(i);
            if (currentCarouselItem % carouselImageUrlList.size() == i) {
                imageView.setImageResource(R.drawable.blue_dots);
            } else {
                imageView.setImageResource(R.drawable.white_dots);
            }
        }
    }

    @Override
    public void updateSuperStore(Store store) {
        if (isShowing) {
            respnseStore = store;
            superStoreName.setText(store.getStoreName());
            superStoreType.setText(store.getStoreType());
            Glide.with(this)
                    .load(store.getStoreAvatar())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.error))
                    .into(superStoreAvatar);
        }
    }

    @Override
    public void updateStore(List<Store> storeList) {
        if (isShowing) {
            if (storeAdapter == null) {
                responseStoreList = storeList;
                storeAdapter = new StoreAdapter(getContext(), storeList);
                storeAdapter.setListener(new StoreItemPressListener() {
                    @Override
                    public void onItemClicked(int position) {
                        Log.e(TAG, "onItemClicked: " + position);
                    }

                    @Override
                    public void OnItemLongClicked(int position) {

                    }
                });
                LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL,
                        false);
                homeRecyclerView.setLayoutManager(llm);
                homeRecyclerView.setAdapter(storeAdapter);
                homeRecyclerView.setItemAnimator(new DefaultItemAnimator());
            } else {
                if (storeList.containsAll(responseStoreList)
                        && responseStoreList.containsAll(storeList)) {
                    LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                            LinearLayoutManager.VERTICAL,
                            false);
                    homeRecyclerView.setLayoutManager(llm);
                    homeRecyclerView.setAdapter(storeAdapter);
                    homeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    if (isNoData) {
                        presenter.loadInterfaceStoreData(offset);
                    }
                    return;
                } else {
                    for (Store store : storeList) {
                        responseStoreList.add(store);
                    }
                }
                storeAdapter.updateData(responseStoreList);
            }
            offset += C.QUERY_STORE_NUMBER;
        }
    }

    @Override
    public void updateFooterWhenError() {
        if (storeAdapter != null) {
            storeAdapter.updateFooter(R.drawable.error, searchBusinessFailed);
        }
    }

    @Override
    public void updateFooterWhenNoData() {
        if (isShowing) {
            if (storeAdapter != null) {
                storeAdapter.updateFooter(R.drawable.error, noData);
            }
            isNoData = true;
        }
    }

    @Override
    public void showNetWorkError() {
        if (isShowing) {
            if (snackbar == null) {
                snackbar = new CustomSnackbar.Builder()
                        .setParentView(homeCoordinatorLayout)
                        .setMessageText(networkErrorText)
                        .setMessageColorId(R.color.net_work_error)
                        .setActionText(goCheckItOutText)
                        .setActionColorId(R.color.register_get_vertify)
                        .setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                        .build();
            }
            snackbar.show();
        }
    }

    @Override
    public void showUnknownError() {
        ToastUtil.showToast(unknownErrorText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        shoppingCartTask.cancel();
        shoppingCartTimer.cancel();
        if (carouselTask != null) {
            carouselTask.cancel();
        }
        if (carouselTimer != null) {
            carouselTimer.cancel();
        }
        if (carousel != null) {
            carousel.removeOnPageChangeListener(this);
        }
    }

    @OnClick({R.id.super_store, R.id.home_shopping_cart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.super_store:

                break;
            case R.id.home_shopping_cart:

                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentCarouselItem = position;
        dotsUpdate();
        isCarouselScrolling = false;
        lastReplaceTime = System.currentTimeMillis();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {
            isCarouselScrolling = true;
        } else {
            isCarouselScrolling = false;
        }
    }

    class PositionViewHolder {

        @BindView(R.id.cyclical_text_view)
        CyclicalTextView cyclicalTextView;
        @BindView(R.id.bt_retry)
        Button btRetry;

        PositionViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.bt_retry)
        public void btRetryClicked(View view) {
            mainActivity.onPositionChanged(waitingForPositioning);
            cyclicalTextView.setText(waitingForPositioning);
            presenter.start();
            btRetry.setVisibility(View.GONE);
        }
    }
}