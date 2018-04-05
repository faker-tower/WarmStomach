package com.example.axiang.warmstomach.ui.settlement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.axiang.warmstomach.ActivityCollector;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.adapters.SettlementAdapter;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Settle;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.enums.CartCheckState;
import com.example.axiang.warmstomach.interfaces.OnCartListener;
import com.example.axiang.warmstomach.ui.home.MainActivity;
import com.example.axiang.warmstomach.ui.order.OrderActivity;
import com.example.axiang.warmstomach.ui.store.StoreActivity;
import com.example.axiang.warmstomach.widget.CustomPopupWindow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettlementActivity extends AppCompatActivity {

    @BindView(R.id.settle_edit)
    TextView settleEdit;
    @BindView(R.id.settle_toolbar)
    Toolbar settleToolbar;
    @BindView(R.id.settle_recycler_view)
    RecyclerView settleRecyclerView;
    @BindView(R.id.settle_content_layout)
    FrameLayout settleContentLayout;
    @BindView(R.id.button_delete)
    Button buttonDelete;

    // 绑定Color
    @BindColor(R.color.colorSecondaryText)
    int canNotClickBgColor;
    @BindColor(R.color.colorAccent)
    int canClickBgColor;

    // 绑定String
    @BindString(R.string.lack)
    String lackText;
    @BindString(R.string.to_send)
    String toSendText;
    @BindString(R.string.money_symbol)
    String moneySymbolText;
    @BindString(R.string.to_settle)
    String toSettleText;
    @BindString(R.string.edit)
    String editText;
    @BindString(R.string.cancel)
    String cancelText;

    private View mNullCartView;
    private SettlementAdapter mSettlementAdapter;
    private List<Object> mDatas;
    private List<CartCheckState> mStates;
    private boolean isEditing = false;
    private List<CartCheckState> mDeteleStates;
    private boolean isInitData = false;
    private CustomPopupWindow mPopupWindow;

    // 是否是由Store界面回来的
    private boolean isStartFromStore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        setContentView(R.layout.activity_settlement);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(settleToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        initData();
        isInitData = true;
    }

    private void initData() {
        List<Cart> carts = WarmStomachApplication.getCarts();
        if (carts.isEmpty()) {
            addNullCartView();
        } else {
            mDatas = getSortDatas(carts);
            if (mSettlementAdapter == null) {
                isEditing = false;
                mSettlementAdapter = new SettlementAdapter(this,
                        mDatas,
                        mStates,
                        isEditing);
                mSettlementAdapter.setOnCartListener(new OnCartListener() {
                    @Override
                    public void onStoreChecked(boolean isChecked, int position) {
                        if (isEditing) {
                            storeCheckedOfEditing(isChecked, position);
                            isCanDelete(isChecked);
                        } else {
                            storeCheckedOutOfEditing(isChecked, position);
                        }
                    }

                    @Override
                    public void onStoreClicked(int position) {
                        if (isEditing) {
                            boolean isChecked = false;
                            if (mDeteleStates.get(position) == CartCheckState.NO_CHECK_STATE) {
                                isChecked = true;
                            }
                            storeCheckedOfEditing(isChecked, position);
                            isCanDelete(isChecked);
                        } else {
                            isStartFromStore = true;
                            Store store = (Store) mDatas.get(position);
                            startActivity(new Intent(SettlementActivity.this,
                                    StoreActivity.class).putExtra(C.NEED_LOAD_STORE, store));
                        }
                    }

                    @Override
                    public void onFoodChecked(boolean isChecked, int position) {
                        if (isEditing) {
                            if (isChecked) {
                                cartCheckedDelete(position, true);
                            } else {
                                cartCheckedCancel(position, true);
                            }
                            isCanDelete(isChecked);
                        } else {
                            if (isChecked) {
                                addSettleList(position);
                            } else {
                                removeSettleList(position);
                            }
                        }
                    }

                    @Override
                    public void onFoodLongClicked(int position) {
                        if (isEditing) {
                            boolean isChecked = false;
                            if (mDeteleStates.get(position) == CartCheckState.NO_CHECK_STATE) {
                                isChecked = true;
                            }
                            if (isChecked) {
                                cartCheckedDelete(position, false);
                            } else {
                                cartCheckedCancel(position, false);
                            }
                            isCanDelete(isChecked);
                        } else {
                            showDialogBox(position);
                        }
                    }

                    @Override
                    public void onFoodClicked(int position) {
                        if (isEditing) {
                            boolean isChecked = false;
                            if (mDeteleStates.get(position) == CartCheckState.NO_CHECK_STATE) {
                                isChecked = true;
                            }
                            if (isChecked) {
                                cartCheckedDelete(position, false);
                            } else {
                                cartCheckedCancel(position, false);
                            }
                            isCanDelete(isChecked);
                        } else {

                        }
                    }

                    @Override
                    public void onGoSettleClicked(int position) {
                        Store store = ((Cart) mDatas.get(position - 1)).getStore();
                        List<Cart> settleFoods = new ArrayList<>();
                        for (int i = position - 1; i > 0; i--) {
                            if (mDatas.get(i) instanceof Cart
                                    && mStates.get(i) == CartCheckState.CHECK_STATE) {
                                settleFoods.add((Cart) mDatas.get(i));
                            } else if (mDatas.get(i) instanceof Store) {
                                break;
                            }
                        }
                        startActivity(new Intent(SettlementActivity.this,
                                OrderActivity.class)
                                .putExtra(C.NEED_SETTLE_STORE, store)
                                .putExtra(C.NEED_SETTLE_STORE_FOOD, (Serializable) settleFoods));
                    }
                });
                settleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                settleRecyclerView.setAdapter(mSettlementAdapter);
            } else {
                mSettlementAdapter.setDatas(mDatas);
                mSettlementAdapter.setStates(mStates);
                mSettlementAdapter.setIsEditing(isEditing);
                mSettlementAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showDialogBox(final int position) {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mPopupWindow = new CustomPopupWindow.Builder()
                .setContext(this)
                .setContentViewId(R.layout.cart_delete_popup)
                .setWidth(5 * metrics.widthPixels / 6)
                .setHeight(metrics.heightPixels / 4)
                // 设置获取聚焦，点击Back物理键可关闭
                .setFocusable(true)
                // 设置PopupWindow外的触摸事件不传递到下面的窗口
                .setTouchable(false)
                // 设置被遮挡的视图的透明度
                .setActivityAndAlpha(this, 0.5f)
                // 设置入场退场动画
                .setAnimationStyle(R.style.popup_anim_style)
                .build()
                // 设置对于父控件的布局位置
                .showAtLocation(R.layout.activity_main, Gravity.CENTER, 0, 0);
        mPopupWindow.setChildOnCilickListener(R.id.cart_popup_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.onDismiss();
            }
        });
        mPopupWindow.setChildOnCilickListener(R.id.cart_popup_determine,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.onDismiss();
                        List<Cart> carts = WarmStomachApplication.getCarts();
                        for (int i = 0; i < mStates.size(); i++) {
                            Object object = mDatas.get(i);
                            if (object instanceof Store) {
                                Store store = (Store) mDatas.get(i);
                                if (((Cart) mDatas.get(position)).getStore().equals(store)) {
                                    boolean isAlsoCartOfStore = false;
                                    for (int j = i + 1; j < mStates.size(); j++) {
                                        if (mStates.get(j) == CartCheckState.NON_STATE) {
                                            break;
                                        } else {
                                            if (mDatas.get(j) instanceof Cart) {
                                                Cart cart = (Cart) mDatas.get(j);
                                                if (!cart.equals(mDatas.get(position))) {
                                                    isAlsoCartOfStore = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!isAlsoCartOfStore) {
                                        mStates.remove(position + 1);
                                        mStates.remove(position);
                                        mStates.remove(i);
                                    } else {
                                        mStates.remove(position);
                                    }
                                }
                            }
                        }
                        carts.remove(mDatas.get(position));
                        initData();
                        if (!carts.isEmpty()) {
                            deleteSingleChecking();
                        }
                    }
                });
    }

    private void isCanDelete(boolean isChecked) {
        if (isChecked) {
            buttonDelete.setClickable(true);
            buttonDelete.setBackgroundColor(canClickBgColor);
        } else {
            boolean isCanDelete = false;
            for (int i = 0; i < mDeteleStates.size(); i++) {
                if (mDeteleStates.get(i) == CartCheckState.CHECK_STATE) {
                    isCanDelete = true;
                    break;
                }
            }
            if (isCanDelete) {
                buttonDelete.setClickable(true);
                buttonDelete.setBackgroundColor(canClickBgColor);
            } else {
                buttonDelete.setClickable(false);
                buttonDelete.setBackgroundColor(canNotClickBgColor);
            }
        }
    }

    // Edit状态下的Cart类型退出Finish队列
    private void cartCheckedCancel(int position, boolean isFromCheckBox) {
        mDeteleStates.set(position, CartCheckState.NO_CHECK_STATE);
        mSettlementAdapter.setSingleState(position, CartCheckState.NO_CHECK_STATE);
        if (!isFromCheckBox) {
            updateRvLayout(position);
        }
        for (int i = position - 1; i >= 0; i--) {
            if (mDatas.get(i) instanceof Store) {
                mDeteleStates.set(i, CartCheckState.NO_CHECK_STATE);
                mSettlementAdapter.setSingleState(i, CartCheckState.NO_CHECK_STATE);
                updateRvLayout(i);
                break;
            }
        }
    }

    // Edit状态下的Cart类型加入Finish队列
    private void cartCheckedDelete(int position, boolean isFromCheckBox) {
        mDeteleStates.set(position, CartCheckState.CHECK_STATE);
        mSettlementAdapter.setSingleState(position, CartCheckState.CHECK_STATE);
        if (!isFromCheckBox) {
            updateRvLayout(position);
        }
        int storePosition = 0;
        boolean checkAllTop = true;
        boolean checkAllBottom = true;
        for (int i = position - 1; i >= 0; i--) {
            if (mDatas.get(i) instanceof Store) {
                storePosition = i;
                break;
            } else {
                if (mDeteleStates.get(i) != CartCheckState.CHECK_STATE) {
                    checkAllTop = false;
                }
            }
        }
        for (int i = position + 1; i < mDatas.size(); i++) {
            if (mDatas.get(i) instanceof Settle) {
                break;
            } else {
                if (mDeteleStates.get(i) != CartCheckState.CHECK_STATE) {
                    checkAllBottom = false;
                }
            }
        }
        if (checkAllTop && checkAllBottom) {
            mDeteleStates.set(storePosition, CartCheckState.CHECK_STATE);
            mSettlementAdapter.setSingleState(storePosition, CartCheckState.CHECK_STATE);
        } else {
            mDeteleStates.set(storePosition, CartCheckState.NO_CHECK_STATE);
            mSettlementAdapter.setSingleState(storePosition, CartCheckState.NO_CHECK_STATE);
        }
        updateRvLayout(storePosition);
    }

    // 非Edit状态下的Store类型被点击
    private void storeCheckedOutOfEditing(boolean isChecked, int position) {
        Store store = (Store) mDatas.get(position);
        double nowAllPrice = 0.0;
        for (int i = position; i < mDatas.size(); i++) {
            if (mDatas.get(i) instanceof Settle) {
                Settle settle = (Settle) mDatas.get(i);
                if (isChecked) {
                    settle.setNowAllPrice(nowAllPrice);
                    double differencePrice = store.getStoreStartingPrice()
                            .doubleValue() - nowAllPrice;
                    if (differencePrice > 0) {
                        settle.setSettleButtonText(lackText
                                + moneySymbolText
                                + differencePrice
                                + toSendText);
                        settle.setSettleButtonClickable(false);
                    } else {
                        settle.setSettleButtonText(toSettleText);
                        settle.setSettleButtonClickable(true);
                    }
                } else {
                    settle.setNowAllPrice(0);
                    settle.setSettleButtonText(moneySymbolText
                            + store.getStoreStartingPrice().doubleValue()
                            + toSendText);
                    settle.setSettleButtonClickable(false);
                }
                break;
            } else {
                if (mDatas.get(i) instanceof Cart) {
                    nowAllPrice += ((Cart) mDatas.get(i))
                            .getStoreFood()
                            .getFoodPrice()
                            .doubleValue()
                            * ((Cart) mDatas.get(i)).getNumber();
                }
                if (isChecked) {
                    mStates.set(i, CartCheckState.CHECK_STATE);
                } else {
                    mStates.set(i, CartCheckState.NO_CHECK_STATE);
                }
            }
        }
        mSettlementAdapter.setDatas(mDatas);
        mSettlementAdapter.setStates(mStates);
        settleRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mSettlementAdapter.notifyDataSetChanged();
            }
        });
    }

    // Edit状态下的Store类型被点击
    private void storeCheckedOfEditing(boolean isChecked, int position) {
        Store store = (Store) mDatas.get(position);
        for (int i = position; i < mDatas.size(); i++) {
            if (!(mDatas.get(i) instanceof Settle)) {
                if (isChecked) {
                    mDeteleStates.set(i, CartCheckState.CHECK_STATE);
                } else {
                    mDeteleStates.set(i, CartCheckState.NO_CHECK_STATE);
                }
            } else {
                break;
            }
        }
        mSettlementAdapter.setStates(mDeteleStates);
        settleRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mSettlementAdapter.notifyDataSetChanged();
            }
        });
    }

    private void removeSettleList(int position) {
        mStates.set(position, CartCheckState.NO_CHECK_STATE);
        Cart cart = (Cart) mDatas.get(position);
        double nowAllPrice = 0.0;
        double startingPrice = 0.0;
        for (int i = position - 1; i >= 0; i--) {
            if (mDatas.get(i) instanceof Store) {
                startingPrice = ((Store) mDatas.get(i)).getStoreStartingPrice();
                mStates.set(i, CartCheckState.NO_CHECK_STATE);
                mSettlementAdapter.setSingleState(i, CartCheckState.NO_CHECK_STATE);
                updateRvLayout(i);
                break;
            }
        }
        for (int i = position + 1; i < mDatas.size(); i++) {
            if (mDatas.get(i) instanceof Settle) {
                Settle settle = (Settle) mDatas.get(i);
                nowAllPrice = settle.getNowAllPrice()
                        - cart.getStoreFood()
                        .getFoodPrice()
                        .doubleValue()
                        * cart.getNumber();
                settle.setNowAllPrice(nowAllPrice);
                double differencePrice = startingPrice - nowAllPrice;
                if (differencePrice > 0) {
                    settle.setSettleButtonText(lackText
                            + moneySymbolText
                            + differencePrice
                            + toSendText);
                    settle.setSettleButtonClickable(false);
                } else {
                    settle.setSettleButtonText(toSettleText);
                    settle.setSettleButtonClickable(true);
                }
                mSettlementAdapter.setSingleData(i, settle);
                updateRvLayout(i);
                break;
            }
        }
    }

    private void addSettleList(int position) {
        mStates.set(position, CartCheckState.CHECK_STATE);
        Cart cart = (Cart) mDatas.get(position);
        double nowAllPrice = 0.0;
        double startingPrice = 0.0;
        boolean checkAllTop = true;
        boolean checkAllBottom = true;
        int storePosition = 0;
        for (int i = position - 1; i >= 0; i--) {
            if (mDatas.get(i) instanceof Store) {
                startingPrice = ((Store) mDatas.get(i)).getStoreStartingPrice();
                storePosition = i;
                break;
            } else {
                if (mStates.get(i) != CartCheckState.CHECK_STATE) {
                    checkAllTop = false;
                }
            }
        }
        for (int i = position + 1; i < mDatas.size(); i++) {
            if (mDatas.get(i) instanceof Settle) {
                Settle settle = (Settle) mDatas.get(i);
                nowAllPrice = settle.getNowAllPrice()
                        + cart.getStoreFood()
                        .getFoodPrice()
                        .doubleValue()
                        * cart.getNumber();
                settle.setNowAllPrice(nowAllPrice);
                double differencePrice = startingPrice - nowAllPrice;
                if (differencePrice > 0) {
                    settle.setSettleButtonText(lackText
                            + moneySymbolText
                            + differencePrice
                            + toSendText);
                    settle.setSettleButtonClickable(false);
                } else {
                    settle.setSettleButtonText(toSettleText);
                    settle.setSettleButtonClickable(true);
                }
                mSettlementAdapter.setSingleData(i, settle);
                updateRvLayout(i);
                break;
            } else {
                if (mStates.get(i) != CartCheckState.CHECK_STATE) {
                    checkAllBottom = false;
                }
            }
        }
        if (checkAllTop && checkAllBottom) {
            mStates.set(storePosition, CartCheckState.CHECK_STATE);
            mSettlementAdapter.setSingleState(storePosition, CartCheckState.CHECK_STATE);
        } else {
            mStates.set(storePosition, CartCheckState.NO_CHECK_STATE);
            mSettlementAdapter.setSingleState(storePosition, CartCheckState.NO_CHECK_STATE);
        }
        updateRvLayout(storePosition);
    }

    private void updateRvLayout(final int position) {
        settleRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mSettlementAdapter.notifyItemChanged(position);
            }
        });
    }

    private List<Object> getSortDatas(@NonNull List<Cart> carts) {
        List<Object> datas = new ArrayList<>();
        if (!isInitData) {
            mStates = new ArrayList<>();
        }
        List<Store> stores = new ArrayList<>();
        for (Cart cart : carts) {
            Store store = cart.getStore();
            if (!stores.isEmpty() && stores.contains(cart.getStore())) {
                continue;
            }
            stores.add(store);
            datas.add(store);
            if (!isInitData) {
                mStates.add(CartCheckState.CHECK_STATE);
            }
            double nowAllPrice = 0.0;
            for (int i = 0; i < carts.size(); i++) {
                if (carts.get(i).getStore().equals(store)) {
                    datas.add(carts.get(i));
                    if (!isInitData) {
                        mStates.add(CartCheckState.CHECK_STATE);
                    }
                    nowAllPrice += carts.get(i).getStoreFood().getFoodPrice()
                            * carts.get(i).getNumber();
                }
            }
            double differencePrice = store.getStoreStartingPrice().doubleValue() - nowAllPrice;
            String settleButtonText;
            boolean settleButtonClickable;
            if (differencePrice > 0) {
                settleButtonText = lackText + moneySymbolText + differencePrice + toSendText;
                settleButtonClickable = false;
            } else {
                settleButtonText = toSettleText;
                settleButtonClickable = true;
            }
            Settle settle = new Settle(nowAllPrice, settleButtonText, settleButtonClickable);
            datas.add(settle);
            if (!isInitData) {
                mStates.add(CartCheckState.NON_STATE);
            }
        }
        return datas;
    }

    @OnClick({R.id.settle_edit, R.id.button_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settle_edit:
                if (settleEdit.getText().toString().equals(editText)) {
                    isEditing = true;
                    settleEdit.setText(cancelText);
                    buttonDelete.setVisibility(View.VISIBLE);
                    mSettlementAdapter.setIsEditing(isEditing);
                    if (mDeteleStates == null) {
                        mDeteleStates = new ArrayList<>();
                    } else {
                        mDeteleStates.clear();
                    }
                    for (int i = 0; i < mDatas.size(); i++) {
                        if (mDatas.get(i) instanceof Settle) {
                            mDeteleStates.add(CartCheckState.NON_STATE);
                        } else {
                            mDeteleStates.add(CartCheckState.NO_CHECK_STATE);
                        }
                    }
                    mSettlementAdapter.setStates(mDeteleStates);
                    mSettlementAdapter.notifyDataSetChanged();
                } else {
                    cancelDeleteState();
                    mSettlementAdapter.setIsEditing(isEditing);
                    mSettlementAdapter.setStates(mStates);
                    mSettlementAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.button_delete:
                List<Cart> carts = WarmStomachApplication.getCarts();
                for (int i = mDeteleStates.size() - 1; i >= 0; i--) {
                    if (mDeteleStates.get(i) == CartCheckState.CHECK_STATE) {
                        Object object = mDatas.get(i);
                        if (object instanceof Store) {
                            for (int j = i + 1; j < mStates.size(); j++) {
                                if (mStates.get(j) == CartCheckState.NON_STATE) {
                                    mStates.remove(j);
                                    break;
                                }
                            }
                        }
                        mStates.remove(i);
                        if (object instanceof Cart) {
                            carts.remove(object);
                        }
                    }
                }
                cancelDeleteState();
                initData();
                deleteSingleChecking();
                break;
        }
    }

    // 删除单个数据之后检查剩下的数据是否全是已点击状态，是则将Store数据也设置为点击状态
    private void deleteSingleChecking() {
        if (!mStates.isEmpty()) {
            for (int i = 0; i < mStates.size(); i++) {
                if (mDatas.get(i) instanceof Store
                        && mStates.get(i) == CartCheckState.NO_CHECK_STATE) {
                    boolean isAllSeleted = true;
                    for (int j = i + 1; j < mStates.size(); j++) {
                        if (mStates.get(j) == CartCheckState.NON_STATE) {
                            break;
                        } else {
                            if (mStates.get(j) == CartCheckState.NO_CHECK_STATE) {
                                isAllSeleted = false;
                                break;
                            }
                        }
                    }
                    if (isAllSeleted) {
                        mStates.set(i, CartCheckState.CHECK_STATE);
                        mSettlementAdapter.setSingleState(i, CartCheckState.CHECK_STATE);
                        updateRvLayout(i);
                    }
                }
            }
        }
    }

    // 取消删除状态，退出编辑界面
    private void cancelDeleteState() {
        isEditing = false;
        settleEdit.setText(editText);
        buttonDelete.setClickable(false);
        buttonDelete.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                ActivityCollector.remove(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNullCartView() {
        if (mDatas != null) {
            mDatas = null;
        }
        if (mStates != null) {
            mStates = null;
        }
        isEditing = false;
        settleEdit.setVisibility(View.GONE);
        if (mNullCartView == null) {
            mNullCartView = LayoutInflater.from(this)
                    .inflate(R.layout.layout_null_cart,
                            settleContentLayout,
                            false);
            mNullCartView.findViewById(R.id.bt_go_shopping)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SettlementActivity.this,
                                    MainActivity.class));
                        }
                    });
        }
        settleContentLayout.addView(mNullCartView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStartFromStore) {
            updateWhenStoreBack();
            isStartFromStore = false;
        }
    }

    private void updateWhenStoreBack() {
        List<Cart> carts = WarmStomachApplication.getCarts();
        if (carts.isEmpty()) {
            addNullCartView();
        } else {
            List<Cart> noSelectCarts = new ArrayList<>();
            if (mDatas != null) {
                for (int i = 0; i < mDatas.size(); i++) {
                    if (mDatas.get(i) instanceof Cart
                            && mStates.get(i) == CartCheckState.NO_CHECK_STATE) {
                        noSelectCarts.add((Cart) mDatas.get(i));
                    }
                }
            }
            isInitData = false;
            mDatas = getSortDatas(carts);
            isInitData = true;
            boolean isCheckAll = true;
            int storeIndex = 0;
            double needCutPrice = 0.0;
            double startingPrice = 0.0;
            for (int i = 0; i < mDatas.size(); i++) {
                if (mDatas.get(i) instanceof Store) {
                    storeIndex = i;
                    isCheckAll = true;
                    storeIndex = i;
                    needCutPrice = 0.0;
                    startingPrice = ((Store) mDatas.get(i)).getStoreStartingPrice();
                } else if (mDatas.get(i) instanceof Cart) {
                    Cart cart = (Cart) mDatas.get(i);
                    if (noSelectCarts.contains(cart)) {
                        if (noSelectCarts.get(noSelectCarts.indexOf(cart)).getNumber()
                                == cart.getNumber()) {
                            isCheckAll = false;
                            needCutPrice += cart.getStoreFood().getFoodPrice()
                                    * cart.getNumber();
                            mStates.set(i, CartCheckState.NO_CHECK_STATE);
                        }
                    }
                } else {
                    if (isCheckAll) {
                        mStates.set(storeIndex, CartCheckState.CHECK_STATE);
                    } else {
                        mStates.set(storeIndex, CartCheckState.NO_CHECK_STATE);
                        if (needCutPrice != 0.0) {
                            Settle settle = (Settle) mDatas.get(i);
                            settle.setNowAllPrice(settle.getNowAllPrice() - needCutPrice);
                            double differencePrice = startingPrice
                                    - settle.getNowAllPrice();
                            if (differencePrice > 0) {
                                settle.setSettleButtonText(lackText
                                        + moneySymbolText
                                        + differencePrice
                                        + toSendText);
                                settle.setSettleButtonClickable(false);
                            } else {
                                settle.setSettleButtonText(toSettleText);
                                settle.setSettleButtonClickable(true);
                            }
                        }
                    }
                }
            }
            mSettlementAdapter.setDatas(mDatas);
            mSettlementAdapter.setStates(mStates);
            mSettlementAdapter.setIsEditing(isEditing);
            mSettlementAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
    }
}
