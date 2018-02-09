package com.example.axiang.warmstomach.presenters;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.WarmStomachApplication;
import com.example.axiang.warmstomach.contracts.MainContract;
import com.example.axiang.warmstomach.data.User;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

/**
 * Created by a2389 on 2018/2/5.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    public LocationClient mLocationClient;
    private BDLocationListener myListener = new MyLocationListener();

    @Override
    public void setView(MainContract.View view) {
        this.mView = view;
    }

    // 开始定位
    @Override
    public void start() {
        mView.onPositioning();
        mLocationClient = new LocationClient(WarmStomachApplication.getInstance());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        // 可选，设置返回经纬度坐标类型，默认gcj02
        // gcj02：国测局坐标
        // bd09ll：百度经纬度坐标
        // bd09：百度墨卡托坐标
        // 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setCoorType("bd09ll");
        // 可选，设置发起定位请求的间隔，int类型，单位ms
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        option.setScanSpan(0);
        // 可选，设置是否使用gps，默认false
        // 使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setOpenGps(true);
        // 可选，定位SDK内部是一个service，并放到了独立进程
        // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIgnoreKillProcess(false);
        // 可选，7.2版本新增能力
        // 如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期
        // 若超出有效期，会先重新扫描WiFi，然后定位
        option.SetIgnoreCacheException(false);
        // 是否返回详细的描述地址
        option.setIsNeedAddress(true);
        // // 是否返回位置描述信息
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void syncUserData() {
        if (BmobUser.getCurrentUser(User.class) != null) {
            BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        mView.syncUserDataFailed();
                    } else {
                        mView.syncUserDataSuccess();
                    }
                }
            });
        } else {
            mView.syncUserDataFailed();
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //定位sdk获取位置后回调
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                // 纬度
                double latitude = location.getLatitude();
                // 经度
                double longitude = location.getLongitude();
                // 国家名称
                String county = location.getCountry();
                // 城市
                String city = location.getCity();
                // 区
                String district = location.getDistrict();
                // 街道
                String street = location.getStreet();
                // 地址信息
                String addrStr = location.getAddrStr();
                // 位置描述信息
                String locationDescribe = location.getLocationDescribe();
                mLocationClient.unRegisterLocationListener(myListener);
                mLocationClient.stop();
                if (county == null || city == null
                        || district == null
                        || street == null
                        || addrStr == null
                        || locationDescribe == null
                        || TextUtils.isEmpty(county)
                        || TextUtils.isEmpty(city)
                        || TextUtils.isEmpty(district)
                        || TextUtils.isEmpty(street)
                        || TextUtils.isEmpty(addrStr)
                        || TextUtils.isEmpty(locationDescribe)
                        ) {
                    mView.onPositionFailed();
                } else {
                    String address = county + city + district + street + addrStr + locationDescribe;
                    SharedPreferencesUtil.getSharedPreferences()
                            .edit()
                            .putString(C.LONGITUDE, longitude + "")
                            .putString(C.LATITUDE, latitude + "")
                            .putString(C.ADDRESS, address)
                            .commit();
                    mView.onPositionSuccess(address);
                }
            }
        }
    }
}
