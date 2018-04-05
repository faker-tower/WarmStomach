package com.example.axiang.warmstomach.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.axiang.warmstomach.ActivityCollector;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.adapters.MainViewPagerAdapter;
import com.example.axiang.warmstomach.contracts.MainContract;
import com.example.axiang.warmstomach.data.StoreFood;
import com.example.axiang.warmstomach.data.User;
import com.example.axiang.warmstomach.enums.PositionState;
import com.example.axiang.warmstomach.presenters.HomePresenter;
import com.example.axiang.warmstomach.presenters.MainPresenter;
import com.example.axiang.warmstomach.ui.home.fragments.FindFragment;
import com.example.axiang.warmstomach.ui.home.fragments.HomeFragment;
import com.example.axiang.warmstomach.ui.home.fragments.OrdersFragment;
import com.example.axiang.warmstomach.ui.register_login.RegisterOrLoginActivity;
import com.example.axiang.warmstomach.util.ImageUtil;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.widget.CircleImageView;
import com.example.axiang.warmstomach.widget.CustomPopupWindow;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    // 绑定视图
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_view_pager)
    ViewPager viewPager;
    @BindView(R.id.main_nav_view)
    NavigationView navigationView;
    @BindView(R.id.main_bottom_nav_bar)
    BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_position)
    TextView toolbarPosition;

    // 绑定String
    @BindString(R.string.positioning)
    String positioningText;
    @BindString(R.string.retry)
    String retryText;
    @BindString(R.string.positioning_failed_retry)
    String positioningFailedRetryText;
    @BindString(R.string.exit_program_3s)
    String exitProgram3sText;
    @BindString(R.string.exit_account)
    String exitAccountText;
    @BindString(R.string.exit_program)
    String exitProgramText;
    @BindString(R.string.or)
    String orText;
    @BindString(R.string.need_permission)
    String needPermissionText;
    @BindString(R.string.failed_set_avatar)
    String failedSetAvatarText;
    @BindString(R.string.take_a_photo)
    String takeAPhotoText;
    @BindString(R.string.album_selection)
    String albumSelectionText;
    @BindString(R.string.failed_get_picture)
    String failedGetPictureText;
    @BindString(R.string.create_image_failed)
    String createImageFailedText;
    @BindString(R.string.crop_picture_failed)
    String cropPictureFailedText;
    @BindString(R.string.compression_avatar_failed)
    String compressionAvatarFailedText;
    @BindString(R.string.avatar_updated_successful)
    String avatarUpdatedSuccessfulText;
    @BindString(R.string.avatar_update_failed)
    String avatarUpdateFailedText;

    private MainContract.Presenter mPresenter;
    private PositionState mPositionState;

    // NavigationView
    private View mNavHeaderView;
    private NavHeaderViewHolder mNavHeaderViewHolder;
    private CustomPopupWindow mPopupWindow;
    private Uri mTitleImageCacheUri;
    private File mTitleImageCutFile;
    private File mTitleImageFile;
    private BmobFile mAvatarFile;

    // ViewPager
    private MainViewPagerAdapter mainViewPagerAdapter;
    private List<Fragment> mFragments;
    private int mCurrentVgItem = 0;

    // 主页面
    private HomeFragment mHomeFragment;
    private HomePresenter mHomePresenter;

    // 订单页面
    private OrdersFragment mOrdersFragment;

    // 发现页面
    private FindFragment mFindFragment;

    // 退出程序
    private long mLastExitTime = 0;
    private ExitHandle mExitHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        getApplication().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mExitHandler = new ExitHandle(this);
        setTransparentStatusBar();
        initView();
        initFragmentsData(savedInstanceState);
        initViewPager();
        initData();
    }

    private void initData() {
        setPresenter(new MainPresenter());
        mPresenter.setView(this);
        mPresenter.syncUserData();
        if (!"".equals(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LONGITUDE, ""))
                && !"".equals(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.LATITUDE, ""))
                && !"".equals(SharedPreferencesUtil.getSharedPreferences()
                .getString(C.ADDRESS, ""))) {
            onPositionSuccess(SharedPreferencesUtil.getSharedPreferences()
                    .getString(C.ADDRESS, ""));
        } else {
            mPresenter.start();
        }
    }

    public MainContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onPositioning() {
        mPositionState = PositionState.positioning;
        toolbarPosition.setText(positioningText);
        if (viewPager.getCurrentItem() == 0) {
            mHomeFragment.getOnPositionStatedListener().onPositioning();
        }
    }

    @Override
    public void onPositionSuccess(String address) {
        mPositionState = PositionState.positionSuccess;
        toolbarPosition.setText(address);
        if (viewPager.getCurrentItem() == 0) {
            mHomeFragment.getOnPositionStatedListener().onPosiitonSuccess();
        }
    }

    @Override
    public void onPositionFailed() {
        mPositionState = PositionState.positionFailed;
        ToastUtil.showToast(positioningFailedRetryText);
        toolbarPosition.setText(retryText);
        if (viewPager.getCurrentItem() == 0) {
            mHomeFragment.getOnPositionStatedListener().onPositionFailed();
        }
    }

    @Override
    public void syncUserDataSuccess() {
        mNavHeaderViewHolder.register.setVisibility(View.GONE);
        mNavHeaderViewHolder.login.setVisibility(View.GONE);
        User user = BmobUser.getCurrentUser(User.class);
        mNavHeaderViewHolder.or.setText(user.getName());
        SharedPreferencesUtil.getSharedPreferences()
                .edit()
                .putString(C.LAST_LOGIN_PHONE_NUMBER, user.getMobilePhoneNumber())
                .commit();
        if (user.getAvatar() != null) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.error))
                    .into(mNavHeaderViewHolder.navHeaderImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.title_image_no_login)
                    .into(mNavHeaderViewHolder.navHeaderImage);
        }
    }

    @Override
    public void syncUserDataFailed() {
        resetLogin();
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(3);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    switch (mPositionState) {
                        case positioning:
                            mHomeFragment.getOnPositionStatedListener().onPositioning();
                            break;
                        case positionFailed:
                            mHomeFragment.getOnPositionStatedListener().onPositionFailed();
                            break;
                        case positionSuccess:
                            mHomeFragment.getOnPositionStatedListener().onPosiitonSuccess();
                            break;
                    }
                }
                mCurrentVgItem = position;
                bottomNavigationBar.selectTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(mCurrentVgItem);
    }

    private void initFragmentsData(Bundle savedInstanceState) {
        mFragments = new ArrayList<>();
        if (savedInstanceState != null) {
            mCurrentVgItem = savedInstanceState.getInt(C.CURRENT_ERROR_BNB_ITEM);
            switch (mCurrentVgItem) {
                case 0:
                    initHomeFragment(savedInstanceState);
                    initOrdersFragment(null);
                    initFindFragment(null);
                    break;
                case 1:
                    initHomeFragment(null);
                    initOrdersFragment(savedInstanceState);
                    initFindFragment(null);
                    break;
                case 2:
                    initHomeFragment(null);
                    initOrdersFragment(null);
                    initFindFragment(savedInstanceState);
                    break;
            }
        } else {
            initHomeFragment(null);
            initOrdersFragment(null);
            initFindFragment(null);
        }
        mFragments.add(mHomeFragment);
        mFragments.add(mOrdersFragment);
        mFragments.add(mFindFragment);
    }

    @Override
    public void initView() {
        initToolBar();
        initNavigationView();
        initContentView();
    }

    private void initContentView() {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                // 设置点击特效
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                // 设置背景颜色
                .setBarBackgroundColor(R.color.bottom_nav_bar_bg)
                // 设置选中的颜色
                .setActiveColor(R.color.colorPrimary)
                // 设置未选中的颜色
                .setInActiveColor(R.color.bottom_nav_bar_no_select)
                .addItem(new BottomNavigationItem(R.drawable.home, R.string.home))
                .addItem(new BottomNavigationItem(R.drawable.orders, R.string.orders))
                .addItem(new BottomNavigationItem(R.drawable.find, R.string.find))
                // 默认显示第一个，异常则显示异常那个
                .setFirstSelectedPosition(mCurrentVgItem)
                // 初始化
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                mCurrentVgItem = position;
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }

    // 初始化NavigationView
    private void initNavigationView() {
        navigationView.setItemIconTintList(null);
        mNavHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawer(Gravity.START);
                        switch (item.getItemId()) {
                            // NavigationView的“个人信息”按钮被点击
                            case R.id.personal_info:

                                break;
                            // NavigationView的“我的收藏”按钮被点击
                            case R.id.my_collection:

                                break;
                            // NavigationView的“我的足迹”按钮被点击
                            case R.id.my_footsteps:

                                break;
                            // NavigationView的“我的评价”按钮被点击
                            case R.id.my_comment:

                                break;
                            // NavigationView的“系统设置”按钮被点击
                            case R.id.system_settings:

                                break;
                            case R.id.exit:
                                showDialogBox(R.drawable.person,
                                        exitAccountText,
                                        R.drawable.exit_program,
                                        exitProgramText);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

        mNavHeaderViewHolder = new NavHeaderViewHolder(mNavHeaderView);
        mNavHeaderViewHolder.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent
                        (MainActivity.this, RegisterOrLoginActivity.class);
                registerIntent.putExtra(C.IS_SHOW_REGISTER, true);
                startActivityForResult(registerIntent, C.GO_TOREGISTER_OR_LOGIN);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });
        mNavHeaderViewHolder.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent
                        (MainActivity.this, RegisterOrLoginActivity.class);
                loginIntent.putExtra(C.IS_SHOW_REGISTER, false);
                startActivityForResult(loginIntent, C.GO_TOREGISTER_OR_LOGIN);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });
        mNavHeaderViewHolder.navHeaderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.START);
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            C.REQUEST_PREMISSION_EXTERNAL_STORAGE);
                } else {
                    if (BmobUser.getCurrentUser(User.class) == null) {
                        ToastUtil.showToast(failedSetAvatarText);
                        return;
                    } else {
                        showDialogBox(R.drawable.carema,
                                takeAPhotoText,
                                R.drawable.album,
                                albumSelectionText);
                    }
                }
            }
        });
    }

    private void initToolBar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_layout_open,
                R.string.drawer_layout_close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
        toolbarPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPositionState == PositionState.positionFailed) {
                    mPresenter.start();
                }
            }
        });
    }

    private void initHomeFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mHomeFragment = new HomeFragment();
        } else {
            mHomeFragment = (HomeFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState,
                            C.CURRENT_ERROR_FRAGMENT);

        }
        mHomePresenter = new HomePresenter();
        mHomeFragment.setPresenter(mHomePresenter);
        mHomePresenter.setView(mHomeFragment);
    }

    private void initOrdersFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mOrdersFragment = new OrdersFragment();
        } else {
            mOrdersFragment = (OrdersFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState,
                            C.CURRENT_ERROR_FRAGMENT);

        }
    }

    private void initFindFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mFindFragment = new FindFragment();
        } else {
            mFindFragment = (FindFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState,
                            C.CURRENT_ERROR_FRAGMENT);

        }
    }

    @OnClick(R.id.toolbar_position)
    public void toolbarPositionClicked() {
        if (retryText.equals(toolbarPosition.getText().toString())) {
            mPresenter.start();
        }
    }

    // 设置沉浸式状态栏
    public void setTransparentStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        if (drawerLayout != null) {
            drawerLayout.setFitsSystemWindows(true);
            drawerLayout.setClipToPadding(false);
        }
    }

    // App状态异常被强制退出时回调的保存界面数据的接口
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(C.CURRENT_ERROR_BNB_ITEM, mCurrentVgItem);
        if (mCurrentVgItem == 0) {
            getSupportFragmentManager().putFragment(outState,
                    C.CURRENT_ERROR_FRAGMENT,
                    mHomeFragment);
        } else if (mCurrentVgItem == 1) {
            getSupportFragmentManager().putFragment(outState,
                    C.CURRENT_ERROR_FRAGMENT,
                    mOrdersFragment);
        } else if (mCurrentVgItem == 2) {
            getSupportFragmentManager().putFragment(outState,
                    C.CURRENT_ERROR_FRAGMENT,
                    mFindFragment);
        }
    }

    // 退出登陆或者没有登陆的时候，界面重置为初始状态
    private void resetLogin() {
        mNavHeaderViewHolder.register.setVisibility(View.VISIBLE);
        mNavHeaderViewHolder.login.setVisibility(View.VISIBLE);
        mNavHeaderViewHolder.or.setText(orText);
        Glide.with(this)
                .load(R.drawable.title_image_no_login)
                .into(mNavHeaderViewHolder.navHeaderImage);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            Message msg = mExitHandler.obtainMessage();
            if (System.currentTimeMillis() - mLastExitTime <= 3000) {
                msg.what = C.EXIT_PROGRAM;
            } else {
                msg.what = C.EXIT_PROGRAM_TIMING;
            }
            mExitHandler.handleMessage(msg);
        }
    }

    // 显示PopupWindow
    private void showDialogBox(int firstImageId,
                               String firstText,
                               int secondImageId,
                               String secondText) {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mPopupWindow = new CustomPopupWindow.Builder()
                .setContext(this)
                .setContentViewId(R.layout.popup_layout)
                .setWidth(4 * metrics.widthPixels / 5)
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
        ((ImageView) mPopupWindow.getChildView(R.id.first_image)).setImageResource(firstImageId);
        ((TextView) mPopupWindow.getChildView(R.id.first_text)).setText(firstText);
        ((ImageView) mPopupWindow.getChildView(R.id.second_image)).setImageResource(secondImageId);
        ((TextView) mPopupWindow.getChildView(R.id.second_text)).setText(secondText);
        if (exitAccountText.equals(firstText)) {
            mPopupWindow.setChildOnCilickListener(R.id.first_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.onDismiss();
                    User user = BmobUser.getCurrentUser(User.class);
                    if (user != null) {
                        BmobUser.logOut();
                    }
                    resetLogin();
                }
            });
            mPopupWindow.setChildOnCilickListener(R.id.second_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExitHandler.sendEmptyMessage(C.EXIT_PROGRAM);
                }
            });
        } else {
            mPopupWindow.setChildOnCilickListener(R.id.first_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.onDismiss();
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                C.REQUEST_PREMISSION_CAMERA);
                    } else {
                        openCamera();
                    }
                }
            });
            mPopupWindow.setChildOnCilickListener(R.id.second_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.onDismiss();
                    // 从相册中选取图片
                    Intent albumIntent = new Intent(Intent.ACTION_PICK);
                    albumIntent.setType("image/*");
                    startActivityForResult(albumIntent, C.REQUEST_IMAGE_FROM_PICK);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == C.REQUEST_PREMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ToastUtil.showToast(needPermissionText);
            }
        } else if (requestCode == C.REQUEST_PREMISSION_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (BmobUser.getCurrentUser(User.class) == null) {
                    ToastUtil.showToast(failedSetAvatarText);
                    return;
                } else {
                    showDialogBox(R.drawable.carema,
                            takeAPhotoText,
                            R.drawable.album,
                            albumSelectionText);
                }
            } else {
                ToastUtil.showToast(needPermissionText);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case C.GO_TOREGISTER_OR_LOGIN:
                if (BmobUser.getCurrentUser(User.class) != null) {
                    syncUserDataSuccess();
                }
                break;
            case C.REQUEST_IMAGE_FROM_CAREMA:
                if (resultCode != RESULT_OK) {
                    ToastUtil.showToast(failedGetPictureText);
                } else {
                    mTitleImageCutFile = new File(getExternalCacheDir().getPath(),
                            C.TITLE_IMAGE_CUT_NAME);
                    // 开启裁剪
                    Intent intent = null;
                    try {
                        intent = ImageUtil.cutForPhoto(mTitleImageCacheUri,
                                ImageUtil.px2dip(this,
                                        mNavHeaderViewHolder.navHeaderImage.getWidth()),
                                ImageUtil.px2dip(this,
                                        mNavHeaderViewHolder.navHeaderImage.getHeight()),
                                mTitleImageCutFile);
                    } catch (IOException e) {
                        ToastUtil.showToast(createImageFailedText);
                    }
                    if (intent != null) {
                        startActivityForResult(intent, C.REQUEST_PHOTO_CROP);
                    }
                }
                break;
            case C.REQUEST_IMAGE_FROM_PICK:
                if (resultCode != RESULT_OK) {
                    ToastUtil.showToast(failedGetPictureText);
                } else {
                    mTitleImageCutFile = new File(getExternalCacheDir().getPath(),
                            C.TITLE_IMAGE_CUT_NAME);
                    // 开启裁剪
                    try {
                        Intent intent = ImageUtil.cutForPhoto(data.getData(),
                                ImageUtil.px2dip(this,
                                        mNavHeaderViewHolder.navHeaderImage.getWidth() * 2),
                                ImageUtil.px2dip(this,
                                        mNavHeaderViewHolder.navHeaderImage.getHeight() * 2),
                                mTitleImageCutFile);
                        startActivityForResult(intent, C.REQUEST_PHOTO_CROP);
                    } catch (IOException e) {
                        ToastUtil.showToast(createImageFailedText);
                    }
                }
                break;
            case C.REQUEST_PHOTO_CROP:
                // 获取裁剪后的图片，并显示出来
                if (resultCode != RESULT_OK) {
                    ToastUtil.showToast(cropPictureFailedText);
                }
                if (mTitleImageCutFile.exists()) {
                    // 压缩图片
                    mTitleImageFile = new File(getExternalCacheDir().getPath(),
                            "warm_stomacha_" + new Random().nextInt(100)
                                    + "_" + System.currentTimeMillis() + ".png");
                    if (mTitleImageFile.exists()) {
                        mTitleImageFile.delete();
                    }
                    try {
                        mTitleImageFile.createNewFile();
                    } catch (IOException e) {
                        ToastUtil.showToast(createImageFailedText);
                    }
                    Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                    options.isKeepSampling = true;
                    options.outfile = mTitleImageFile.getAbsolutePath();
                    Tiny.getInstance()
                            .source(mTitleImageCutFile)
                            .asFile()
                            .withOptions(options)
                            .compress(new FileCallback() {
                                @Override
                                public void callback(boolean isSuccess,
                                                     String outfile,
                                                     Throwable t) {
                                    if (isSuccess) {
                                        if (mTitleImageCutFile.exists()) {
                                            mTitleImageCutFile.delete();
                                        }
                                        //显示文件
                                        Glide.with(MainActivity.this)
                                                .load(outfile)
                                                .apply(new RequestOptions().centerCrop()
                                                        .placeholder(R.drawable.image_loading)
                                                        .error(R.drawable.error))
                                                .into(mNavHeaderViewHolder.navHeaderImage);
                                        updateUserTitleImage(outfile);
                                    } else {
                                        ToastUtil.showToast(compressionAvatarFailedText);
                                    }
                                }
                            });
                } else {
                    ToastUtil.showToast(cropPictureFailedText);
                }
                break;
        }
    }

    // 更新用户头像
    private void updateUserTitleImage(String outFile) {
        mAvatarFile = new BmobFile(new File(outFile));
        mAvatarFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    ToastUtil.showToast(avatarUpdatedSuccessfulText);
                } else {
                    User user = BmobUser.getCurrentUser(User.class);
                    User updateUser = new User();
                    updateUser.setAvatar(mAvatarFile.getFileUrl());
                    updateUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                User user = BmobUser.getCurrentUser(User.class);
                                user.setAvatar(mAvatarFile.getFileUrl());
                                ToastUtil.showToast(avatarUpdatedSuccessfulText);
                            } else {
                                ToastUtil.showToast(avatarUpdateFailedText);
                            }
                        }
                    });
                }
            }
        });
    }

    // 打开相机拍照
    public void openCamera() {
        // 创建一个file，用来存储拍照后的照片
        File outputFile = new File(getExternalCacheDir().getPath(), C.TITLE_IMAGE_NO_CUT_NAME);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            ToastUtil.showToast(createImageFailedText);
            return;
        }
        // 适配7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTitleImageCacheUri = FileProvider.getUriForFile(this,
                    "com.axiang.fileprovider",
                    outputFile);
        } else {
            mTitleImageCacheUri = Uri.fromFile(outputFile);
        }
        // 启动相机程序
        Intent caremaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        caremaIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTitleImageCacheUri);
        startActivityForResult(caremaIntent, C.REQUEST_IMAGE_FROM_CAREMA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
        mExitHandler = null;
        mNavHeaderViewHolder = null;
    }

    // NavigationView的头部布局的ViewHolder
    static class NavHeaderViewHolder {

        @BindView(R.id.nav_header_image)
        CircleImageView navHeaderImage;
        @BindView(R.id.register)
        TextView register;
        @BindView(R.id.login)
        TextView login;
        @BindView(R.id.or)
        TextView or;

        NavHeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ExitHandle extends Handler {

        private WeakReference<MainActivity> activityReference;

        public ExitHandle(MainActivity activity) {
            super();
            activityReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityReference.get() != null) {
                switch (msg.what) {
                    case C.EXIT_PROGRAM_TIMING:
                        ToastUtil.showToast(activityReference.get().exitProgram3sText);
                        activityReference.get().mLastExitTime = System.currentTimeMillis();
                        break;
                    case C.EXIT_PROGRAM:
                        ActivityCollector.finishAll();
                        break;
                }
            }
        }
    }
}
