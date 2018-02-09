package com.example.axiang.warmstomach.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by a2389 on 2018/2/2.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public MainViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments == null || mFragments.size() == 0 ? null : mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null? 0 : mFragments.size();
    }
}
