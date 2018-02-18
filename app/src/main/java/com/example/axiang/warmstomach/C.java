package com.example.axiang.warmstomach;

import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by a2389 on 2017/12/2.
 */

public class C {

    // 未知网络类型
    public static final int UNKNOWN_NETWORK_TYPE = -1;

    // 是否夜间主题
    public static final String IF_NIGHT_MODE = "night_mode";

    // 网络连接超时
    public static final int HTTP_CONNECTION_TIME_OUT = 9;

    // 读写超时
    public static final int READ_TIME_OUT = 10;

    // 请求权限标识
    public static final int REQUEST_PREMISSION_EXTERNAL_STORAGE= 190;
    public static final int REQUEST_PREMISSION_CAMERA = 191;
    public static final int REQUEST_PREMISSION_PHONE = 192;

    // Bmob申请的应用Id
    public static final String BMOB_APPLICATION_ID = "3e38f8c7a848ce7961720ffcef4f7c24";
    // Bmob数据库的_User表里的手机号码字段名
    public static final String MOBILE_PHONE_NUMBER = "mobilePhoneNumber";
    // Bmob数据库的Store表里的地址位置字段名
    public static final String STORE_LOCATION = "storeLocation";
    // Bmob数据库的SuperStore表里的地址位置字段名
    public static final String SUPER_STORE_LOCATION = "superStoreLocation";
    // Bmob数据库查询商店返回数量
    public static final int QUERY_STORE_NUMBER = 10;

    // 上一个登陆的手机号
    public static final String LAST_LOGIN_PHONE_NUMBER = "last_login_phone_number";

    // 用户此时所在的经度
    public static final String LONGITUDE = "longitude";
    // 用户此时所在的纬度
    public static final String LATITUDE = "latitude";
    // 用户此时所在的地理位置描述
    public static final String ADDRESS = "address";

    // 赤道半径
    public static final  double EARTH_RADIUS = 6378137;

    // 异常导致重启的当前Fragment
    public static final String CURRENT_ERROR_FRAGMENT = "current_error_fragment";
    // 异常导致重启的当前BottomNavigationBar item
    public static final String CURRENT_ERROR_BNB_ITEM = "current_error_bnb_item";

    // 规定时间内接受点击事件准备退出程序
    public static final int EXIT_PROGRAM_TIMING = 0;
    // 退出程序
    public static final int EXIT_PROGRAM = 1;

    // 前往登陆注册界面
    public static final int GO_TOREGISTER_OR_LOGIN = 110;
    // 是否显示注册界面
    public static final String IS_SHOW_REGISTER = "is_show_register";
    // 切换到Register界面
    public static final int REPLACE_REGISTER_LAYOUT = 111;
    // 切换到Login界面
    public static final int REPLACE_LOGIN_LAYOUT = 112;

    // 拍照取得头像
    public static final int REQUEST_IMAGE_FROM_CAREMA = 230;
    // 从相册中获取头像
    public static final int REQUEST_IMAGE_FROM_PICK = 231;
    // 启动裁剪
    public static final int REQUEST_PHOTO_CROP = 232;
    // 头像文件夹名
    public static final String PRIVATE_IMAGE_FLODER = "warm_stomach";
    // 未裁剪的头像图片名
    public static final String TITLE_IMAGE_NO_CUT_NAME = "warm_stomach_no_cut_title_image.png";
    // 裁剪过后的头像图片名
    public static final String TITLE_IMAGE_CUT_NAME = "warm_stomach_cut_title_image.png";
    // 图片来源相机
    public static final String PHOTO_FROM_CAMERA = "phone_from_camera";

    // 购物车正常显示
    public static final int SHOOPING_CART_NORMAL = 321;
    // 购物车隐藏
    public static final int SHOOPING_CART_HIDDEN = 322;

    // 广告栏的广告数量
    public static final int CAROUSEL_IMAGE_NUMBER = 5;
    // 切换广告栏伦轮播图图片
    public static final int CAROUSEL_IMAGE_REPLACE = 323;
    // Adapter中代表AdColumn的标识
    public static final int ADAPTER_AD_COLUMN = 420;
    // Adapter中代表StoreType的标识
    public static final int ADAPTER_STORE_TYPE = 421;
    // Adapter中代表SuperStore的标识
    public static final int ADAPTER_SUPER_STORE= 422;
    // Adapter中代表StoreType的标识
    public static final int ADAPTER_STORE = 423;

    // GridView的SimpleAdapter中商店类型图片
    public static final String STORE_TYPE_IMAGE = "store_type_image";
    // GridView的SimpleAdapter中商店类型名
    public static final String STORE_TYPE_TEXT = "store_type_text";

    // RecyclerView垂直布局
    public static final int RECYCLER_VIEW_HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    // RecyclerView水平布局
    public static final int RECYCLER_VIEW_VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    // RecyclerView页尾
    public static final int RECYCLER_VIEW_FOOTER = 442;

    // 要加载的商家对象
    public static final String NEED_LOAD_STORE = "need_load_store";
    // 要加载的商家Id
    public static final String NEED_LOAD_STORE_ID = "need_load_store_id";
    // 商家食物分类对应的商家id字段
    public static final String SORT_TO_STORE = "ownStoreId";
    // 商家食物对应的商家id字段
    public static final String FOOD_TO_STORE = "foodOwnStoreId";
    // StoreSortAdapter中的标题栏类型
    public static final int STORE_FOOD_TYPE_TITLE = 12;
    // StoreSortAdapter中的食物类型
    public static final int STORE_FOOD_TYPE_CONTENT = 13;
}
