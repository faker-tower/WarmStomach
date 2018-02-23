package com.example.axiang.warmstomach.ui.home.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.adapters.HomeAdapter;
import com.example.axiang.warmstomach.contracts.HomeContract;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreAd;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.data.StoreType;
import com.example.axiang.warmstomach.enums.PositionState;
import com.example.axiang.warmstomach.interfaces.OnAdColumnItemListener;
import com.example.axiang.warmstomach.interfaces.OnPositionStatedListener;
import com.example.axiang.warmstomach.interfaces.OnStoreItemListener;
import com.example.axiang.warmstomach.ui.home.MainActivity;
import com.example.axiang.warmstomach.ui.settlement.SettlementActivity;
import com.example.axiang.warmstomach.ui.store.StoreActivity;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.widget.CustomSnackbar;
import com.example.axiang.warmstomach.widget.CyclicalTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by a2389 on 2017/12/2.
 */

public class HomeFragment extends Fragment implements HomeContract.View {

    // 绑定视图
    @BindView(R.id.home_refresh_layout)
    SwipeRefreshLayout homeRefreshLayout;
    @BindView(R.id.home_recycler_view)
    RecyclerView homeRecyclerView;
    @BindView(R.id.home_shopping_cart)
    FloatingActionButton homeShoppingCart;
    @BindView(R.id.home_frame_layout)
    FrameLayout homeFrameLayout;
    @BindView(R.id.home_coordinator_layout)
    CoordinatorLayout homeCoordinatorLayout;
    @BindView(R.id.home_food_count)
    TextView homeFoodCount;

    // 绑定dimen
    @BindDimen(R.dimen.dp_12)
    int shoppingCartMaginRightSize;

    // 绑定integer
    @BindInt(R.integer.integer_500)
    int totalAnimDuration;
    @BindInt(R.integer.integer_250)
    int alphaAnimDuration;

    // 绑定String
    @BindString(R.string.positioning)
    String positionText;
    @BindString(R.string.positioning_failed_retry)
    String positioningFailedRetryText;
    @BindString(R.string.loading_data)
    String loadingDataText;
    @BindString(R.string.network_error)
    String networkErrorText;
    @BindString(R.string.go_check_it_out)
    String goCheckItOutText;
    @BindString(R.string.unknown_error)
    String unknownErrorText;
    @BindString(R.string.unknown_error_refresh)
    String unKnownErrorRefreshText;
    @BindString(R.string.search_business)
    String searchBusiness;
    @BindString(R.string.search_business_failed)
    String searchBusinessFailed;
    @BindString(R.string.no_data)
    String noData;

    private String[] mStoreTypeNames = new String[]{"米粉米线", "甜品", "中式简餐", "西式快餐",
            "饺子馄饨", "炸鸡炸串", "奶茶果汁", "中式烤肉",
            "面馆", "日本料理", "意面披萨", "木桶饭"};

    private int[] mStoreTypeIcons = new int[]{R.drawable.icon_meter, R.drawable.icon_desserts,
            R.drawable.icon_simple, R.drawable.icon_fast, R.drawable.icon_dumplings,
            R.drawable.icon_fried, R.drawable.icon_tea, R.drawable.icon_roast,
            R.drawable.icon_noodle, R.drawable.cuisine, R.drawable.icon_pisa,
            R.drawable.icon_rice};

    // 判断当前界面是否在前台
    private boolean isFragmentShowing = false;
    // 判断界面数据是否初始化成功
    private boolean isInitDataSuccess = false;
    // 是否正在加载数据或刷新视图
    private boolean isLoadOrRefresh = false;

    private MainActivity mainActivity;
    private Unbinder mUnbinder;
    private HomeContract.Presenter mPresenter;
    private HomeHandler mHomeHandler;
    private CustomSnackbar mSnackbar;
    private HomeAdapter mHomeAdapter;
    private OnPositionStatedListener mPositionStatedListener = new OnPositionStatedListener() {
        @Override
        public void onPosiitonSuccess() {
            mPositionState = PositionState.positionSuccess;
            loadingStateChanged(loadingDataText, View.GONE);
            if (!isInitDataSuccess) {
                initData();
            }
        }

        @Override
        public void onPositionFailed() {
            mPositionState = PositionState.positionFailed;
            loadingStateChanged(positioningFailedRetryText, View.VISIBLE);
        }

        @Override
        public void onPositioning() {
            mPositionState = PositionState.positioning;
            loadingStateChanged(positionText, View.GONE);
        }
    };

    // 商店类型模块
    private SimpleAdapter mStoreTypeAdapter;
    private List<StoreType> mStoreTypes;

    // 等待模块
    private View mLoadingView;
    private LoadingViewHolder mLoadingViewHolder;
    private PositionState mPositionState;
    private boolean isLoadingViewShow = false;

    // 购物车模块
    private AnimatorSet mShoppingCartEnterAnim;
    private AnimatorSet mShoppingCartExitAnim;
    private boolean isShoppingCartEntering = true;
    private long mShoppingCartCurrentTime;
    private Timer mShoppingCartTimer;
    private TimerTask mShoppingCartTask;
    private Map<StoreFood, Integer> mCartFoods = new HashMap<>();

    // 广告栏模块
    private List<StoreAd> mStoreAds;
    private boolean isLoadAdColumnDataSuccessed = false;
    private Timer mCarouselTimer;
    private TimerTask mCarouselTask;

    // 为您推荐模块
    private Store mSuperStore;
    private boolean isLoadSuperStoreDataSuccessed = false;

    // 商店数据模块
    private List<Store> mAllStores;
    private List<Store> mStores;
    private int mOffset = 0;
    private boolean isLoadMoreStoreData = false;
    private boolean isNoData = false;
    private boolean isLoadStoreDataSuccessed = false;

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    public OnPositionStatedListener getOnPositionStatedListener() {
        return mPositionStatedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mHomeHandler = new HomeHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mLoadingView = inflater.inflate(R.layout.home_loading,
                homeFrameLayout,
                false);
        initView();
        return view;
    }

    @Override
    public void initView() {
        mLoadingViewHolder = new LoadingViewHolder(mLoadingView);
        mLoadingViewHolder.btRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPositionState == PositionState.positionFailed) {
                    loadingStateChanged(positionText, View.GONE);
                    mainActivity.getPresenter().start();
                } else {
                    loadingStateChanged(loadingDataText, View.GONE);
                    initData();
                }
            }
        });
        switch (mPositionState) {
            case positionSuccess:
                loadingStateChanged(loadingDataText, View.GONE);
                break;
            case positionFailed:
                loadingStateChanged(positioningFailedRetryText, View.VISIBLE);
                break;
            case positioning:
                loadingStateChanged(positionText, View.GONE);
                break;
        }
        if (!isLoadingViewShow) {
            homeFrameLayout.addView(mLoadingView);
            isLoadingViewShow = true;
        }
        initRefreshLayout();
        initRecyclerView();
        initShoppingCartAnim();
    }

    private void initShoppingCartAnim() {
        // 手机屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int windowWidth = displayMetrics.widthPixels;

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
        mShoppingCartEnterAnim = new AnimatorSet();
        mShoppingCartEnterAnim.playTogether(transluteEnterAnim, alphaEnterAnim);
        mShoppingCartEnterAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isShoppingCartEntering = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (homeShoppingCart != null) {
                    homeShoppingCart.setX(windowWidth - homeShoppingCart.getWidth()
                            - shoppingCartMaginRightSize);
                    showCartFoodNumber();
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
        mShoppingCartExitAnim = new AnimatorSet();
        mShoppingCartExitAnim.playTogether(transluteExitAnim, alphaAnim);
        mShoppingCartExitAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isShoppingCartEntering = false;
                homeFoodCount.setVisibility(View.GONE);
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
                    mHomeHandler.sendEmptyMessage(C.SHOOPING_CART_NORMAL);
                }
                startActivity(new Intent(mainActivity, SettlementActivity.class));
            }
        });
    }

    private void showCartFoodNumber() {
        List<Cart> carts = WarmStomachApplication.getCarts();
        int nowCount = 0;
        if (!carts.isEmpty()) {
            for (int i = 0; i < carts.size(); i++) {
                nowCount += carts.get(i).getNumber();
            }
        }
        if (nowCount == 0) {
            homeFoodCount.setVisibility(View.GONE);
        } else {
            homeFoodCount.setVisibility(View.VISIBLE);
            if (nowCount > 99) {
                homeFoodCount.setText("99+");
            } else {
                homeFoodCount.setText(String.valueOf(nowCount));
            }
        }
    }

    private void initRecyclerView() {
        homeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (((!homeRefreshLayout.isRefreshing()
                        && !isLoadOrRefresh)
                        && !isLoadMoreStoreData)
                        && (!isNoData
                        && manager.findLastVisibleItemPosition()
                        == mHomeAdapter.getItemCount() - 1)) {
                    isLoadMoreStoreData = true;
                    mPresenter.loadMoreStoreData(mAllStores, mOffset);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 正在被外部拖拽，一般为用户正在用手指滚动
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (isShoppingCartEntering) {
                        mHomeHandler.sendEmptyMessage(C.SHOOPING_CART_HIDDEN);
                    }
                }
            }
        });
    }

    private void initRefreshLayout() {
        homeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.GRAY);
        homeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isInitDataSuccess && !isLoadOrRefresh) {
                    mOffset = 0;
                    initData();
                } else {
                    homeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadingStateChanged(String positionStateText, int retryVisibility) {
        if (isLoadingViewShow) {
            mLoadingViewHolder.tvLoading.setText(positionStateText);
            mLoadingViewHolder.btRetry.setVisibility(retryVisibility);
        }
    }

    @Override
    public void showNetWorkError() {
        isLoadOrRefresh = false;
        isLoadMoreStoreData = false;
        if (isFragmentShowing) {
            if (homeRefreshLayout.isRefreshing()) {
                homeRefreshLayout.setRefreshing(false);
            }
            if (isLoadingViewShow) {
                loadingStateChanged(networkErrorText, View.VISIBLE);
            }
            if (mSnackbar == null) {
                mSnackbar = new CustomSnackbar.Builder()
                        .setParentView(homeCoordinatorLayout)
                        .setMessageText(networkErrorText)
                        .setMessageColorId(ContextCompat
                                .getColor(getContext(), R.color.net_work_error))
                        .setActionText(goCheckItOutText)
                        .setActionColorId(ContextCompat
                                .getColor(getContext(), R.color.register_get_vertify))
                        .setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mSnackbar.dismiss();
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                        .build();
            }
            mSnackbar.show();
        }
    }

    @Override
    public void showUnknownError() {
        isLoadOrRefresh = false;
        isLoadMoreStoreData = false;
        if (isFragmentShowing) {
            if (homeRefreshLayout.isRefreshing()) {
                homeRefreshLayout.setRefreshing(false);
            }
            if (isLoadingViewShow) {
                loadingStateChanged(unknownErrorText, View.VISIBLE);
            } else {
                ToastUtil.showToast(unKnownErrorRefreshText);
            }
        }
    }

    private void initData() {
        isLoadOrRefresh = true;
        isLoadAdColumnDataSuccessed = false;
        isLoadSuperStoreDataSuccessed = false;
        isLoadStoreDataSuccessed = false;
        isLoadMoreStoreData = false;
        isNoData = false;
        mOffset = 0;
        updateStoreType();
        mPresenter.start();
    }

    @Override
    public void updateStoreAd(List<StoreAd> storeAds) {
        if (isFragmentShowing) {
            mStoreAds = storeAds;
            isLoadAdColumnDataSuccessed = true;
            updateContentView();
        }
    }

    @Override
    public void updateSuperStore(Store store) {
        if (isFragmentShowing) {
            mSuperStore = store;
            isLoadSuperStoreDataSuccessed = true;
            updateContentView();
        }
    }

    @Override
    public void updateStore(List<Store> storeList) {
        if (isFragmentShowing) {
            if (isLoadMoreStoreData) {
                if (mHomeAdapter != null) {
                    mHomeAdapter.setStores(storeList, false);
                    mHomeAdapter.notifyDataSetChanged();
                    mOffset += C.QUERY_STORE_NUMBER;
                }
            } else {
                mAllStores = storeList;
                if (mStores == null) {
                    mStores = new ArrayList<>();
                } else {
                    mStores.clear();
                }
                if (mAllStores.size() > C.QUERY_STORE_NUMBER) {
                    for (int i = 0; i < C.QUERY_STORE_NUMBER; i++) {
                        mStores.add(mAllStores.get(i));
                    }
                } else {
                    mStores.addAll(mAllStores);
                    updateFooterWhenNoData();
                }
                isLoadStoreDataSuccessed = true;
                updateContentView();
            }
        }
    }

    private void updateStoreType() {
        if (isFragmentShowing) {
            if (mStoreTypes == null) {
                mStoreTypes = new ArrayList<>();
            } else {
                mStoreTypes.clear();
            }
            for (int i = 0; i < mStoreTypeIcons.length; i++) {
                StoreType storeType = new StoreType();
                storeType.setStoreTypeName(mStoreTypeNames[i]);
                storeType.setStoreTypeIcon(mStoreTypeIcons[i]);
                mStoreTypes.add(storeType);
            }
        }
    }

    private void updateContentView() {
        if (isFragmentShowing) {
            if (isLoadAdColumnDataSuccessed
                    && isLoadSuperStoreDataSuccessed
                    && isLoadStoreDataSuccessed) {
                if (isLoadingViewShow) {
                    homeFrameLayout.removeView(mLoadingView);
                    mLoadingView = null;
                    isLoadingViewShow = false;
                }
                if (homeRefreshLayout.isRefreshing()) {
                    homeRefreshLayout.setRefreshing(false);
                }
                if (mHomeAdapter == null) {
                    mHomeAdapter = new HomeAdapter(getContext(),
                            mStoreAds,
                            mStoreTypes,
                            mSuperStore,
                            mStores);
                    mHomeAdapter.setOnStoreItemListener(new OnStoreItemListener() {
                        @Override
                        public void onItemClicked(int position) {
                            Store store = mStores.get(position);
                            Intent storeIntent = new Intent(mainActivity, StoreActivity.class);
                            storeIntent.putExtra(C.NEED_LOAD_STORE, store);
                            startActivity(storeIntent);
                        }
                    });
                    mHomeAdapter.setOnAdColumnItemListener(new OnAdColumnItemListener() {
                        @Override
                        public void onItemClicked(int position) {
                            String storeId = mStoreAds.get(position).getStoreId();
                            Intent storeIntent = new Intent(mainActivity, StoreActivity.class);
                            storeIntent.putExtra(C.NEED_LOAD_STORE_ID, storeId);
                            startActivity(storeIntent);
                        }
                    });
                    homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    homeRecyclerView.setAdapter(mHomeAdapter);
                } else {
                    mHomeAdapter.setStoreAds(mStoreAds);
                    mHomeAdapter.setStoreTypes(mStoreTypes);
                    mHomeAdapter.setStore(mSuperStore);
                    mHomeAdapter.setStores(mStores, true);
                    mHomeAdapter.notifyDataSetChanged();
                }

                if (mCarouselTimer == null && mCarouselTask == null) {
                    // 开启3秒定时切换广告栏图片功能
                    mCarouselTimer = new Timer();
                    mCarouselTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (mHomeHandler != null && !isLoadOrRefresh) {
                                mHomeHandler.sendEmptyMessage(C.CAROUSEL_IMAGE_REPLACE);
                            }
                        }
                    };
                    mCarouselTimer.schedule(mCarouselTask, 1500, 1500);
                }

                if (mShoppingCartTimer == null && mShoppingCartTask == null) {
                    // 开启定时3秒显示购物车图标
                    mShoppingCartCurrentTime = System.currentTimeMillis();
                    mShoppingCartTimer = new Timer();
                    mShoppingCartTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (isFragmentShowing) {
                                if (System.currentTimeMillis() - mShoppingCartCurrentTime >= 3000) {
                                    if (!isShoppingCartEntering) {
                                        mHomeHandler.sendEmptyMessage(C.SHOOPING_CART_NORMAL);
                                    }
                                }
                            }
                        }
                    };
                    mShoppingCartTimer.schedule(mShoppingCartTask, 1500, 1500);
                }

                isInitDataSuccess = true;
                mOffset = C.QUERY_STORE_NUMBER;
                isLoadOrRefresh = false;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isFragmentShowing = true;
        } else {
            isFragmentShowing = false;
            if (homeRefreshLayout != null) {
                if (homeRefreshLayout.isRefreshing()) {
                    homeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInitDataSuccess) {
            if (isShoppingCartEntering) {
                showCartFoodNumber();
            } else {
                mHomeHandler.sendEmptyMessage(C.SHOOPING_CART_NORMAL);
            }
        }
    }

    @Override
    public void updateFooterWhenError() {
        if (isFragmentShowing) {
            if (mHomeAdapter != null) {
                mHomeAdapter.getHomeListener().updateFooter(R.drawable.error, searchBusinessFailed);
            }
        }
    }

    @Override
    public void updateFooterWhenNoData() {
        if (isFragmentShowing) {
            if (mHomeAdapter != null) {
                mHomeAdapter.getHomeListener().updateFooter(R.drawable.error, noData);
            }
            isNoData = true;
        }
    }

    @Override
    public void loadMoreStoreDataFinish() {
        isLoadMoreStoreData = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if (mCarouselTask != null) {
            mCarouselTask.cancel();
            mCarouselTask = null;
        }
        if (mCarouselTimer != null) {
            mCarouselTimer.cancel();
            mCarouselTimer = null;
        }
        if (mShoppingCartTask != null) {
            mShoppingCartTask.cancel();
            mShoppingCartTask = null;
        }
        if (mShoppingCartTimer != null) {
            mShoppingCartTimer.cancel();
            mShoppingCartTimer = null;
        }
        mHomeHandler = null;
        mLoadingViewHolder = null;
    }

    static class HomeHandler extends Handler {

        private WeakReference<HomeFragment> fragmentReference;

        public HomeHandler(HomeFragment fragment) {
            super();
            fragmentReference = new WeakReference<HomeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (fragmentReference.get() != null) {
                switch (msg.what) {
                    case C.SHOOPING_CART_NORMAL:
                        if (!fragmentReference.get().mShoppingCartEnterAnim.isRunning()
                                && !fragmentReference.get().mShoppingCartExitAnim.isRunning()) {
                            fragmentReference.get().mShoppingCartEnterAnim.start();
                            fragmentReference.get().mShoppingCartCurrentTime
                                    = System.currentTimeMillis();
                        }
                        break;
                    case C.SHOOPING_CART_HIDDEN:
                        if (!fragmentReference.get().mShoppingCartEnterAnim.isRunning()
                                && !fragmentReference.get().mShoppingCartExitAnim.isRunning()) {
                            fragmentReference.get().mShoppingCartExitAnim.start();
                            fragmentReference.get().mShoppingCartCurrentTime
                                    = System.currentTimeMillis();
                        }
                        break;
                    case C.CAROUSEL_IMAGE_REPLACE:
                        if (fragmentReference.get().isFragmentShowing) {
                            fragmentReference.get()
                                    .mHomeAdapter
                                    .getHomeListener()
                                    .timingToReplaceAdPic();
                        }
                        break;
                }
            }
        }
    }

    // 等待定位和等待加载数据模块
    static class LoadingViewHolder {

        @BindView(R.id.tv_loading)
        CyclicalTextView tvLoading;
        @BindView(R.id.bt_retry)
        Button btRetry;

        LoadingViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}