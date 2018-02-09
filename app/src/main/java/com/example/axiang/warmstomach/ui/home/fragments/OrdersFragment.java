package com.example.axiang.warmstomach.ui.home.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.contracts.OrdersContract;

/**
 * Created by a2389 on 2017/12/2.
 */

public class OrdersFragment extends Fragment implements OrdersContract.View {

    private static final String TAG = OrdersFragment.class.getSimpleName();

    private OrdersContract.Presenter mPresenter;

    @Override
    public void setPresenter(OrdersContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        initView();
        return view;
    }

    @Override
    public void initView() {
    }
}
