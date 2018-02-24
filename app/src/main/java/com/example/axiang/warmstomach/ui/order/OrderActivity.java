package com.example.axiang.warmstomach.ui.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.data.Cart;
import com.example.axiang.warmstomach.data.Store;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private Store mNeedSettleStore;
    private List<Cart> mNeedSettleCarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initData();
        initView();
    }

    private void initView() {

    }

    private void initData() {
        mNeedSettleStore = (Store) getIntent().getSerializableExtra(C.NEED_SETTLE_STORE);
        if (mNeedSettleStore != null) {
            List<Cart> carts = WarmStomachApplication.getCarts();
            mNeedSettleCarts = new ArrayList<>();
            for (int i = 0; i < carts.size(); i++) {
                if (carts.get(i).getStore().equals(mNeedSettleStore)) {
                    mNeedSettleCarts.add(carts.get(i));
                }
            }
        }
    }
}
