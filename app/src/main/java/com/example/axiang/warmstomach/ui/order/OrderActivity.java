package com.example.axiang.warmstomach.ui.order;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.ActivityCollector;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.adapters.OrderAdapter;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Store;
import com.example.axiang.warmstomach.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends AppCompatActivity {

    // 绑定视图
    @BindView(R.id.order_toolbar)
    Toolbar orderToolbar;
    @BindView(R.id.order_address_layout)
    FrameLayout orderAddressLayout;
    @BindView(R.id.order_send_immediately)
    TextView orderSendImmediately;
    @BindView(R.id.order_send_time)
    TextView orderSendTime;
    @BindView(R.id.et_your_phone)
    EditText etYourPhone;
    @BindView(R.id.clear_edit_phone)
    ImageView clearEditPhone;
    @BindView(R.id.et_greeting_card)
    EditText etGreetingCard;
    @BindView(R.id.clear_all_card)
    ImageView clearAllCard;
    @BindView(R.id.order_store_avatar)
    CircleImageView orderStoreAvatar;
    @BindView(R.id.order_store_name)
    TextView orderStoreName;
    @BindView(R.id.rv_order_food)
    RecyclerView rvOrderFood;
    @BindView(R.id.tv_order_show_all_food)
    TextView tvOrderShowAllFood;
    @BindView(R.id.iv_order_show_all_food)
    ImageView ivOrderShowAllFood;
    @BindView(R.id.order_show_all_food)
    LinearLayout orderShowAllFood;
    @BindView(R.id.tv_order_delivery_fee)
    TextView tvOrderDeliveryFee;
    @BindView(R.id.tv_order_red_envelope)
    TextView tvOrderRedEnvelope;
    @BindView(R.id.rl_order_red_envelope)
    RelativeLayout rlOrderRedEnvelope;
    @BindView(R.id.tv_order_merchant_gold_roll)
    TextView tvOrderMerchantGoldRoll;
    @BindView(R.id.rl_order_merchant_gold_roll)
    RelativeLayout rlOrderMerchantGoldRoll;
    @BindView(R.id.tv_order_payment_method)
    TextView tvOrderPaymentMethod;
    @BindView(R.id.rl_order_payment_method)
    RelativeLayout rlOrderPaymentMethod;
    @BindView(R.id.tv_order_note)
    TextView tvOrderNote;
    @BindView(R.id.rl_order_payment_note)
    RelativeLayout rlOrderPaymentNote;
    @BindView(R.id.tv_order_tableware_quantity)
    TextView tvOrderTablewareQuantity;
    @BindView(R.id.rl_order_tableware_quantity)
    RelativeLayout rlOrderTablewareQuantity;
    @BindView(R.id.iv_order_invoice)
    ImageView ivOrderInvoice;
    @BindView(R.id.tv_order_invoice)
    TextView tvOrderInvoice;
    @BindView(R.id.rl_order_invoice)
    RelativeLayout rlOrderInvoice;
    @BindView(R.id.order_all_price)
    TextView orderAllPrice;

    // 绑定String
    @BindString(R.string.click_to_expand)
    String clickToExpandText;
    @BindString(R.string.click_to_collapse)
    String clickToCollapseText;

    private Store mNeedSettleStore;
    private List<Cart> mNeedSettleCarts;

    private OrderAdapter mAdapter;
    private List<Cart> mNoShowAllCarts; // 不需要全部显示时的List
    private boolean isShowAll;  // 是否需要全部显示
    private final int mCount = 3;   // 收起时只显示3个

    private double mAllPrice;   // 总价

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        initToolbar();
        initPhone();
        initStore();
    }

    private void initStore() {
        if (mNeedSettleStore != null) {
            Glide.with(this)
                    .load(mNeedSettleStore.getStoreAvatar())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.error))
                    .into(orderStoreAvatar);
            orderStoreName.setText(mNeedSettleStore.getStoreName());
            tvOrderDeliveryFee.setText(mNeedSettleStore.getStoreDeliveryFee().floatValue() + "");
        }
        if (mNeedSettleCarts != null) {
            if (!mNeedSettleCarts.isEmpty()) {
                if (mNeedSettleCarts.size() > 3) {
                    // List长度大于3，需要收起来，让展开图标显示
                    orderShowAllFood.setVisibility(View.VISIBLE);
                    isShowAll = false;
                    mNoShowAllCarts = new ArrayList<>();
                    for (int i = 0; i < mCount; i++) {
                        mNoShowAllCarts.add(mNeedSettleCarts.get(i));
                    }
                } else {
                    orderShowAllFood.setVisibility(View.GONE);
                    isShowAll = true;
                }

                for (int i = 0; i < mNeedSettleCarts.size(); i++) {
                    mAllPrice += mNeedSettleCarts.get(i).getStoreFood().getFoodPrice().doubleValue()
                            * mNeedSettleCarts.get(i).getNumber();
                }
                orderAllPrice.setText(String.valueOf(mAllPrice));
                initRecy();
            }
        }
    }

    private void initRecy() {
        if (mAdapter == null) {
            if (isShowAll) {
                mAdapter = new OrderAdapter(this, mNeedSettleCarts);
            } else {
                mAdapter = new OrderAdapter(this, mNoShowAllCarts);
            }
            rvOrderFood.setLayoutManager(new LinearLayoutManager(this));
            rvOrderFood.setAdapter(mAdapter);
        }
    }

    private void initPhone() {
        etYourPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == s || "".equals(s.toString())) {
                    clearEditPhone.setVisibility(View.GONE);
                } else {
                    clearEditPhone.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etGreetingCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == s || "".equals(s.toString())) {
                    clearAllCard.setVisibility(View.GONE);
                } else {
                    clearAllCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(orderToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initData() {
        mNeedSettleStore = (Store) getIntent().getSerializableExtra(C.NEED_SETTLE_STORE);
        mNeedSettleCarts = (List<Cart>) getIntent().getSerializableExtra(C.NEED_SETTLE_STORE_FOOD);
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

    @OnClick({R.id.order_address_layout, R.id.order_choose_time_layout,
            R.id.clear_edit_phone, R.id.clear_all_card, R.id.order_show_all_food,
            R.id.rl_order_red_envelope, R.id.rl_order_merchant_gold_roll,
            R.id.rl_order_payment_method, R.id.rl_order_payment_note,
            R.id.rl_order_tableware_quantity, R.id.rl_order_invoice, R.id.order_submit_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.order_address_layout:
                break;
            case R.id.order_choose_time_layout:
                break;
            case R.id.clear_edit_phone:
                etYourPhone.setText("");
                break;
            case R.id.clear_all_card:
                etGreetingCard.setText("");
                break;
            case R.id.order_show_all_food:
                if (isShowAll) {
                    tvOrderShowAllFood.setText(clickToExpandText);
                    ivOrderShowAllFood.setImageResource(R.drawable.put_away);
                    mAdapter.setCarts(mNoShowAllCarts);
                } else {
                    tvOrderShowAllFood.setText(clickToCollapseText);
                    ivOrderShowAllFood.setImageResource(R.drawable.expand);
                    mAdapter.setCarts(mNeedSettleCarts);
                }
                rvOrderFood.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                isShowAll = !isShowAll;
                break;
            case R.id.rl_order_red_envelope:
                break;
            case R.id.rl_order_merchant_gold_roll:
                break;
            case R.id.rl_order_payment_method:
                break;
            case R.id.rl_order_payment_note:
                break;
            case R.id.rl_order_tableware_quantity:
                break;
            case R.id.rl_order_invoice:
                break;
            case R.id.order_submit_order:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
    }
}
