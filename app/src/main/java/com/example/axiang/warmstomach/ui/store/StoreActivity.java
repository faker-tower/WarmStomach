package com.example.axiang.warmstomach.ui.store;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.ActivityCollector;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.adapters.StoreCartAdapter;
import com.example.axiang.warmstomach.adapters.StoreFoodAdapter;
import com.example.axiang.warmstomach.adapters.StoreFoodSortAdapter;
import com.example.axiang.warmstomach.contracts.StoreContract;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.data.StoreFoodSort;
import com.example.axiang.warmstomach.interfaces.OnFoodListener;
import com.example.axiang.warmstomach.interfaces.OnPWClosedListener;
import com.example.axiang.warmstomach.interfaces.OnSortListener;
import com.example.axiang.warmstomach.interfaces.onStoreCartListener;
import com.example.axiang.warmstomach.presenters.StorePresenter;
import com.example.axiang.warmstomach.ui.settlement.SettlementActivity;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.widget.CustomPopupWindow;
import com.example.axiang.warmstomach.widget.CustomSnackbar;
import com.example.axiang.warmstomach.widget.CyclicalTextView;
import com.example.axiang.warmstomach.widget.FoodTitleItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreActivity extends AppCompatActivity implements StoreContract.View {

    // 绑定视图
    @BindView(R.id.iv_store_toolbar_bg)
    ImageView ivStoreToolbarBg;
    @BindView(R.id.tv_store_announcement)
    TextView tvStoreAnnouncement;
    @BindView(R.id.tv_store_address)
    TextView tvStoreAddress;
    @BindView(R.id.store_toolbar)
    Toolbar storeToolbar;
    @BindView(R.id.rv_store_food_sort)
    RecyclerView rvStoreFoodSort;
    @BindView(R.id.rv_store_food)
    RecyclerView rvStoreFood;
    @BindView(R.id.cl_activity_store)
    CoordinatorLayout clActivityStore;
    @BindView(R.id.store_coll_toolbar)
    CollapsingToolbarLayout storeCollToolbar;
    @BindView(R.id.store_food_count)
    TextView storeFoodCount;
    @BindView(R.id.store_all_price)
    TextView storeAllPrice;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    @BindView(R.id.store_bottom_layout)
    RelativeLayout storeBottomLayout;
    @BindView(R.id.to_settle)
    Button toSettle;
    @BindView(R.id.store_shopping_cart)
    FloatingActionButton storeShoppingCart;

    // 绑定integer
    @BindInt(R.integer.integer_500)
    int foodAddAnimDuration;

    // 绑定Color
    @BindColor(R.color.position_loading_layout_bg)
    int canNotClickBgColor;
    @BindColor(R.color.colorAccent)
    int canClickBgColor;
    @BindColor(R.color.colorSecondaryText)
    int canNotClickTextColor;
    @BindColor(R.color.colorPrimaryText)
    int canClickTextColor;

    // 绑定String
    @BindString(R.string.loading_data)
    String loadingDataText;
    @BindString(R.string.network_error)
    String networkErrorText;
    @BindString(R.string.go_check_it_out)
    String goCheckItOutText;
    @BindString(R.string.unknown_error)
    String unknownErrorText;
    @BindString(R.string.unknown_error_refresh)
    String unknownErrorRefreshText;
    @BindString(R.string.get_business_phone_error)
    String getBusinessPhoneErrorText;
    @BindString(R.string.announcement)
    String announcementText;
    @BindString(R.string.address)
    String addressText;
    @BindString(R.string.lack)
    String lackText;
    @BindString(R.string.to_send)
    String toSendText;
    @BindString(R.string.money_symbol)
    String moneySymbolText;
    @BindString(R.string.to_settle)
    String toSettleText;

    // 要加载的商家
    private Store mStore;
    // 要加载的商家Id
    private String mStoreId;
    // 是否成功加载商家信息
    private boolean isLoadStoreSuccess = false;
    // 是否成功加载商家食物分类信息
    private boolean isLoadSortSuceess = false;
    // 是否成功加载商家食物信息
    private boolean isLoadFoodSuceess = false;

    private StoreContract.Presenter mPresenter;
    private CustomSnackbar mSnackbar;

    // 商家食物分类列表
    private List<StoreFoodSort> mStoreFoodSorts;
    private StoreFoodSortAdapter mSortAdapter;
    private int visibilityItemsHalf = 0;

    // 商家食物列表
    private List<StoreFood> mStoreFoods;
    // 根据食物分类信息排序的食物列表
    private List<Object> mStoreFoodsBySorts;
    private StoreFoodAdapter mFoodAdapter;
    private boolean isScrollDown = false;
    private PathMeasure mPathMeasure;
    // 贝塞尔曲线中间过程的点的坐标
    private float[] mCurrentPosition = new float[2];

    // 底部购物车和结算模块
    private double mNowAllPrice = 0.0;
    private int mFoodCount = 0;
    private CustomPopupWindow mPopupWindow;

    // 等待加载模块
    private View mLoadingView;
    private LoadingViewHolder mLoadingViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);
        setPresenter(new StorePresenter());
        mPresenter.start();
        initData();
    }

    private void initData() {
        mStore = (Store) getIntent().getSerializableExtra(C.NEED_LOAD_STORE);
        if (mStore != null) {
            mStoreId = mStore.getObjectId();
            loadStoreSuccess(mStore);
            mPresenter.loadSort(mStoreId);
            mPresenter.loadFood(mStoreId);
        } else {
            mStoreId = getIntent().getStringExtra(C.NEED_LOAD_STORE_ID);
            if (mStoreId != null && !mStoreId.isEmpty()) {
                mPresenter.loadStore(mStoreId);
                mPresenter.loadSort(mStoreId);
                mPresenter.loadFood(mStoreId);
            } else {
                showUnknownError();
            }
        }
    }

    @Override
    public void setPresenter(StoreContract.Presenter presenter) {
        this.mPresenter = presenter;
        mPresenter.setView(this);
    }

    @Override
    public void initView() {
        setSupportActionBar(storeToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        mLoadingView = getLayoutInflater().inflate(R.layout.home_loading,
                clActivityStore,
                false);
        mLoadingView.setFitsSystemWindows(true);
        mLoadingViewHolder = new LoadingViewHolder(mLoadingView);
        storeShoppingCart.setVisibility(View.GONE);
        mLoadingViewHolder.btRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        updateLoadingView(loadingDataText, View.GONE);
        clActivityStore.addView(mLoadingView);
        isLoadStoreSuccess = true;
    }

    @Override
    public void loadStoreSuccess(Store store) {
        this.mStore = store;
        storeCollToolbar.setTitle(store.getStoreName());
        storeToolbar.setTitle(store.getStoreName());
        Glide.with(this)
                .load(store.getStoreAvatar())
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.error))
                .into(ivStoreToolbarBg);
        tvStoreAnnouncement.setText(announcementText + store.getStoreAnnouncement());
        tvStoreAddress.setText(addressText + store.getStoreAddress());
        toSettle.setText(moneySymbolText + mStore.getStoreStartingPrice() + toSendText);
        isLoadStoreSuccess = true;
    }

    @Override
    public void loadSortSuccess(List<StoreFoodSort> storeFoodSorts) {
        mStoreFoodSorts = storeFoodSorts;
        isLoadSortSuceess = true;
        updateContentView();
    }

    @Override
    public void loadFoodSuccess(List<StoreFood> storeFoods) {
        mStoreFoods = storeFoods;
        isLoadFoodSuceess = true;
        updateContentView();
    }

    private void updateContentView() {
        if (isLoadStoreSuccess && (isLoadSortSuceess && isLoadFoodSuceess)) {
            if (mLoadingView != null) {
                clActivityStore.removeView(mLoadingView);
                storeShoppingCart.setVisibility(View.VISIBLE);
            }
            sortFoodsBySorts();

            Map<StoreFood, Integer> map = getFoodCartMap(true);

            mSortAdapter = new StoreFoodSortAdapter(this, mStoreFoodSorts);
            mSortAdapter.setListener(new OnSortListener() {
                @Override
                public void onItemClicked(int position) {
                    for (int i = 0; i < mStoreFoodsBySorts.size(); i++) {
                        if (mStoreFoodsBySorts.get(i) instanceof String) {
                            String sortName = (String) mStoreFoodsBySorts.get(i);
                            if (sortName.equals(mStoreFoodSorts.get(position).getSortName())) {
                                LinearLayoutManager manager = (LinearLayoutManager) rvStoreFood
                                        .getLayoutManager();
                                manager.scrollToPositionWithOffset(i, 0);
                                return;
                            }
                        }
                    }
                }
            });
            rvStoreFoodSort.setLayoutManager(new LinearLayoutManager(this));
            rvStoreFoodSort.setAdapter(mSortAdapter);

            mFoodAdapter = new StoreFoodAdapter(this, mStoreFoodsBySorts, map);
            mFoodAdapter.setListener(new OnFoodListener() {
                @Override
                public void onItemClicked(int position) {

                }

                @Override
                public void updateShoppingCart(boolean isAdded,
                                               int position,
                                               ImageView foodAddImage) {
                    StoreFood food = (StoreFood) mStoreFoodsBySorts.get(position);
                    if (isAdded) {
                        // 添加进购物车
                        mNowAllPrice += food.getFoodPrice();
                        ++mFoodCount;
                        startCartAnim(foodAddImage);
                    } else {
                        // 从购物车移除
                        mNowAllPrice -= food.getFoodPrice();
                        --mFoodCount;
                    }
                    updateCarts(isAdded, food);
                    updatePriceLayout();
                    updateShoppingCartLayout();
                }
            });
            rvStoreFood.setLayoutManager(new LinearLayoutManager(this));
            rvStoreFood.setItemViewCacheSize(mStoreFoodsBySorts.size());
            rvStoreFood.setAdapter(mFoodAdapter);
            rvStoreFood.addItemDecoration(new FoodTitleItemDecoration(this,
                    mStoreFoodsBySorts));
            rvStoreFood.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        LinearLayoutManager manager = (LinearLayoutManager) recyclerView
                                .getLayoutManager();
                        int firstItem = manager.findFirstCompletelyVisibleItemPosition();
                        Object object = mStoreFoodsBySorts.get(firstItem);
                        if (object instanceof String) {
                            String sortName = (String) object;
                            for (int i = 0; i < mStoreFoodSorts.size(); i++) {
                                if (mStoreFoodSorts.get(i).getSortName().equals(sortName)) {
                                    mSortAdapter.updateSelection(rvStoreFoodSort
                                            .findViewHolderForAdapterPosition(i), i);
                                    return;
                                }
                            }
                            mSortAdapter.updateSelection(rvStoreFoodSort
                                    .findViewHolderForAdapterPosition(firstItem), firstItem);
                        } else {
                            updateSortByContent(firstItem);
                        }
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        isScrollDown = true;
                    } else {
                        isScrollDown = false;
                    }
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView
                            .getLayoutManager();
                    int position = manager.findFirstCompletelyVisibleItemPosition();
                    if (mStoreFoodsBySorts.get(position) instanceof String) {
                        String sortName = (String) mStoreFoodsBySorts.get(position);
                        for (int i = 0; i < mStoreFoodSorts.size(); i++) {
                            if (sortName.equals(mStoreFoodSorts.get(i).getSortName())) {
                                LinearLayoutManager sortManager = (LinearLayoutManager)
                                        rvStoreFoodSort.getLayoutManager();
                                if (visibilityItemsHalf == 0) {
                                    visibilityItemsHalf = sortManager
                                            .findLastVisibleItemPosition() / 2;
                                }
                                if (isScrollDown) {
                                    if (sortManager.findLastCompletelyVisibleItemPosition()
                                            - visibilityItemsHalf < i) {
                                        sortManager.scrollToPositionWithOffset(sortManager
                                                        .findFirstCompletelyVisibleItemPosition() + 1,
                                                0);
                                    }
                                } else {
                                    if (sortManager.findFirstCompletelyVisibleItemPosition()
                                            + visibilityItemsHalf > i) {
                                        sortManager.scrollToPositionWithOffset(sortManager
                                                        .findFirstCompletelyVisibleItemPosition() - 1,
                                                0);
                                    }
                                }
                                if (rvStoreFoodSort.findViewHolderForAdapterPosition(i) != null) {
                                    mSortAdapter.updateSelection(rvStoreFoodSort
                                            .findViewHolderForAdapterPosition(i), i);
                                }
                                return;
                            }
                        }
                    } else {
                        if (!isScrollDown) {
                            updateSortByContent(position);
                        }
                    }
                }
            });
        }
    }

    private Map<StoreFood, Integer> getFoodCartMap(boolean isInit) {
        List<Cart> carts = WarmStomachApplication.getCarts();
        Map<StoreFood, Integer> map = new HashMap<>();
        if (!carts.isEmpty()) {
            for (int i = 0; i < carts.size(); i++) {
                if (carts.get(i).getStore().equals(mStore)) {
                    for (int j = 0; j < mStoreFoodsBySorts.size(); j++) {
                        if (mStoreFoodsBySorts.get(j) instanceof StoreFood) {
                            StoreFood food = (StoreFood) mStoreFoodsBySorts.get(j);
                            if (food.equals(carts.get(i).getStoreFood())) {
                                map.put(food, carts.get(i).getNumber());
                            }
                        }
                    }
                    if (isInit) {
                        mFoodCount += carts.get(i).getNumber();
                        mNowAllPrice += carts.get(i).getStoreFood().getFoodPrice()
                                * carts.get(i).getNumber();
                    }
                }
            }
            if (isInit) {
                updatePriceLayout();
                updateShoppingCartLayout();
            }
        } else {
            toSettle.setClickable(false);
            storeShoppingCart.setClickable(false);
        }
        return map;
    }

    private void showDialogBox() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mPopupWindow = new CustomPopupWindow.Builder()
                .setContext(this)
                .setContentViewId(R.layout.layout_store_cart)
                .setWidth(metrics.widthPixels)
                .setHeight(metrics.heightPixels / 3)
                // 设置获取聚焦，点击Back物理键可关闭
                .setFocusable(true)
                // 设置PopupWindow外的触摸事件不传递到下面的窗口
                .setTouchable(false)
                // 设置被遮挡的视图的透明度
                .setActivityAndAlpha(this, 0.5f)
                // 设置入场退场动画，-1表示没有
                .setAnimationStyle(R.style.cart_anim_style)
                .setOnPWColsedListener(new OnPWClosedListener() {
                    @Override
                    public void onPwClosed() {
                        Map<StoreFood, Integer> carts = getFoodCartMap(false);
                        mFoodAdapter.setCarts(carts);
                        mFoodAdapter.notifyDataSetChanged();
                    }
                })
                .build()
                // 设置对于父控件的布局位置
                .showAtLocation(R.layout.activity_store, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setChildOnCilickListener(R.id.store_clear_cart, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Cart> carts = WarmStomachApplication.getCarts();
                if (!carts.isEmpty()) {
                    List<Cart> removeCart = new ArrayList<>();
                    int size = carts.size();
                    for (int i = 0; i < size; i++) {
                        if (carts.get(i).getStore().equals(mStore)) {
                            removeCart.add(carts.get(i));
                        }
                    }
                    carts.removeAll(removeCart);
                }
                mPopupWindow.onDismiss();
                mFoodCount = 0;
                mNowAllPrice = 0.0;
                mFoodAdapter.setCarts(null);
                mFoodAdapter.notifyDataSetChanged();
                updatePriceLayout();
                updateShoppingCartLayout();
            }
        });
        RecyclerView rvCart = (RecyclerView) mPopupWindow.getChildView(R.id.rv_cart);
        initRvCart(rvCart);
        mPopupWindow.setChildOnCilickListener(R.id.cart_to_settle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.onDismiss();
                toSettleLayout();
            }
        });
        updateCartSettleLayout();
    }

    private void toSettleLayout() {
        startActivity(new Intent(this, SettlementActivity.class));
    }

    private void initRvCart(RecyclerView rvCart) {
        Map<StoreFood, Integer> cartFoods = getFoodCartMap(false);
        final StoreCartAdapter cartAdapter = new StoreCartAdapter(this, cartFoods);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setItemViewCacheSize(cartFoods.size());
        rvCart.setAdapter(cartAdapter);
        cartAdapter.setOnItemClickListener(new onStoreCartListener() {
            @Override
            public void updateShoppingCart(boolean isAdded, StoreFood food) {
                updateCarts(isAdded, food);
                if (isAdded) {
                    // 添加进购物车
                    mNowAllPrice += food.getFoodPrice();
                    ++mFoodCount;
                } else {
                    // 从购物车移除
                    mNowAllPrice -= food.getFoodPrice();
                    --mFoodCount;
                }
                updatePriceLayout();
                updateShoppingCartLayout();
                updateCartSettleLayout();
                updateFoodAdapter(isAdded, food);
                if (cartAdapter.getItemCount() == 0) {
                    mPopupWindow.onDismiss();
                }
            }
        });
    }

    private void updateCartSettleLayout() {
        ((TextView) mPopupWindow.getChildView(R.id.cart_all_price))
                .setText(storeAllPrice.getText().toString());
        Button settle = (Button) mPopupWindow.getChildView(R.id.cart_to_settle);
        settle.setText(toSettle.getText().toString());
        if (toSettle.getText().toString().equals(toSettleText)) {
            settle.setBackgroundColor(canClickBgColor);
            settle.setTextColor(canClickTextColor);
            settle.setClickable(true);
        } else {
            settle.setBackgroundColor(canNotClickBgColor);
            settle.setTextColor(canNotClickTextColor);
            settle.setClickable(false);
        }
    }

    private void updateFoodAdapter(boolean isAdded, StoreFood food) {
        for (int i = 0; i < mStoreFoodsBySorts.size(); i++) {
            if (mStoreFoodsBySorts.get(i) instanceof StoreFood) {
                StoreFood storeFood = (StoreFood) mStoreFoodsBySorts.get(i);
                if (food.equals(storeFood)) {
                    RecyclerView.ViewHolder holder = rvStoreFood
                            .findViewHolderForAdapterPosition(i);
                    LinearLayoutManager manager = (LinearLayoutManager) rvStoreFood
                            .getLayoutManager();
                    if (manager.findLastCompletelyVisibleItemPosition() >= i
                            && manager.findFirstCompletelyVisibleItemPosition() <= i) {
                        mFoodAdapter.updateFoodByCart(holder, isAdded);
                    }
                }
            }
        }
    }

    private void updateCarts(boolean isAdded, StoreFood food) {
        List<Cart> carts = WarmStomachApplication.getCarts();
        if (!carts.isEmpty()) {
            int cartSize = carts.size();
            for (int i = 0; i < cartSize; i++) {
                if (carts.get(i).getStoreFood().equals(food)) {
                    if (isAdded) {
                        carts.get(i).setNumber(carts.get(i).getNumber() + 1);
                    } else {
                        carts.get(i).setNumber(carts.get(i).getNumber() - 1);
                        if (carts.get(i).getNumber() == 0) {
                            carts.remove(i);
                        }
                    }
                    return;
                } else {
                    if (i == cartSize - 1) {
                        carts.add(new Cart(mStore, food, 1));
                    }
                }
            }
        } else {
            carts.add(new Cart(mStore, food, 1));
        }
    }

    private void startCartAnim(ImageView foodAddImage) {
        // 创造出执行动画的主题---ImageView
        // 代码new一个ImageView，图片资源是上面的ImageView的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        final ImageView goods = new ImageView(this);
        goods.setScaleType(ImageView.ScaleType.CENTER_CROP);
        goods.setImageDrawable(foodAddImage.getDrawable());
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout
                .LayoutParams(100, 100);
        clActivityStore.addView(goods, params);

        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        clActivityStore.getLocationInWindow(parentLocation);

        //得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        foodAddImage.getLocationInWindow(startLoc);

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        storeShoppingCart.getLocationInWindow(endLoc);

        // 正式开始计算动画开始/结束的坐标
        float startX = startLoc[0] - parentLocation[0];
        float startY = startLoc[1] - parentLocation[1] - foodAddImage.getHeight();

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + storeShoppingCart.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

        // 计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        // 使用二次萨贝尔曲线：第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        //属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(foodAddAnimDuration);
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要
                //mCurrentPosition此时就是中间距离点的坐标值
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });
        // 开始执行动画
        valueAnimator.start();
        // 动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // 把移动的图片ImageView从父布局里移除
                clActivityStore.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void updateShoppingCartLayout() {
        if (mFoodCount == 0) {
            storeShoppingCart.setBackgroundTintList(getResources()
                    .getColorStateList(R.color.bottom_nav_bar_no_select));
            storeShoppingCart.setClickable(false);
            storeFoodCount.setVisibility(View.GONE);
        } else {
            storeShoppingCart.setBackgroundTintList(getResources()
                    .getColorStateList(R.color.colorAccent));
            storeShoppingCart.setClickable(true);
            storeFoodCount.setVisibility(View.VISIBLE);
            if (mFoodCount > 99) {
                storeFoodCount.setText("99+");
            } else {
                storeFoodCount.setText(String.valueOf(mFoodCount));
            }
        }
    }

    // 更新价格布局
    private void updatePriceLayout() {
        if (mNowAllPrice < mStore.getStoreStartingPrice().doubleValue()) {
            // 达不到起送价
            toSettle.setBackgroundColor(canNotClickBgColor);
            toSettle.setTextColor(canNotClickTextColor);
            toSettle.setClickable(false);
            double differencePrice = mStore.getStoreStartingPrice().doubleValue() - mNowAllPrice;
            StringBuilder content = new StringBuilder();
            if (mNowAllPrice != 0.0) {
                content.append(lackText);
            }
            content.append(moneySymbolText);
            content.append(differencePrice);
            content.append(toSendText);
            toSettle.setText(content.toString());
        } else {
            toSettle.setText(toSettleText);
            toSettle.setBackgroundColor(canClickBgColor);
            toSettle.setTextColor(canClickTextColor);
            toSettle.setClickable(true);
        }

        if (mNowAllPrice == 0.0) {
            storeAllPrice.setVisibility(View.GONE);
        } else {
            storeAllPrice.setVisibility(View.VISIBLE);
            storeAllPrice.setText(moneySymbolText + mNowAllPrice);
        }
    }

    @OnClick({R.id.to_settle, R.id.store_shopping_cart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.to_settle:
                toSettleLayout();
                break;
            case R.id.store_shopping_cart:
                showDialogBox();
                break;
        }
    }

    private void updateSortByContent(int position) {
        String sortId = ((StoreFood) mStoreFoodsBySorts.get(position))
                .getFoodOwnSortId();
        for (int i = 0; i < mStoreFoodSorts.size(); i++) {
            if (mStoreFoodSorts.get(i).getObjectId().equals(sortId)) {
                mSortAdapter.updateSelection(rvStoreFoodSort
                        .findViewHolderForAdapterPosition(i), i);
                return;
            }
        }
    }

    private void sortFoodsBySorts() {
        mStoreFoodsBySorts = new ArrayList<>();
        for (int i = 0; i < mStoreFoodSorts.size(); i++) {
            StoreFoodSort sort = mStoreFoodSorts.get(i);
            String sortName = sort.getSortName();
            mStoreFoodsBySorts.add(sortName);
            String sortId = sort.getObjectId();
            for (StoreFood food : mStoreFoods) {
                if (sortId.equals(food.getFoodOwnSortId())) {
                    mStoreFoodsBySorts.add(food);
                }
            }
        }
    }

    @Override
    public void showNetWorkError() {
        updateLoadingView(unknownErrorText, View.VISIBLE);
        if (mSnackbar == null) {
            mSnackbar = new CustomSnackbar.Builder()
                    .setParentView(mLoadingView)
                    .setMessageText(networkErrorText)
                    .setMessageColorId(ContextCompat
                            .getColor(this, R.color.net_work_error))
                    .setActionText(goCheckItOutText)
                    .setActionColorId(ContextCompat
                            .getColor(this, R.color.register_get_vertify))
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

    @Override
    public void showUnknownError() {
        updateLoadingView(unknownErrorText, View.VISIBLE);
        ToastUtil.showToast(unknownErrorRefreshText);
    }

    private void updateLoadingView(String content, int isVisibility) {
        if (mLoadingViewHolder != null && mLoadingView != null) {
            mLoadingViewHolder.tvLoading.setText(content);
            mLoadingViewHolder.btRetry.setVisibility(isVisibility);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                ActivityCollector.remove(this);
                break;
            case R.id.collect:

                break;
            case R.id.call:
                if (mStore != null) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + mStore.getStorePhone());
                    callIntent.setData(data);
                    startActivity(callIntent);
                } else {
                    ToastUtil.showToast(getBusinessPhoneErrorText);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        ActivityCollector.remove(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
        mLoadingViewHolder = null;
    }

    // 等待加载数据模块
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
