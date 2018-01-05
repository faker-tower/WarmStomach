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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.data.User;
import com.example.axiang.warmstomach.interfaces.LoginSuccessListener;
import com.example.axiang.warmstomach.interfaces.PositionChangedListener;
import com.example.axiang.warmstomach.interfaces.RegisterSuccessListener;
import com.example.axiang.warmstomach.presenters.HomePresenter;
import com.example.axiang.warmstomach.presenters.LoginPresenter;
import com.example.axiang.warmstomach.presenters.RegisterPresenter;
import com.example.axiang.warmstomach.ui.home.fragments.FindFragment;
import com.example.axiang.warmstomach.ui.home.fragments.HomeFragment;
import com.example.axiang.warmstomach.ui.home.fragments.LoginFragment;
import com.example.axiang.warmstomach.ui.home.fragments.OrdersFragment;
import com.example.axiang.warmstomach.ui.home.fragments.RegisterFragment;
import com.example.axiang.warmstomach.util.ImageUtil;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.widget.CircleImageView;
import com.example.axiang.warmstomach.widget.CustomPopupWindow;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainActivity extends AppCompatActivity implements PositionChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // 绑定视图
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.buttom_nav_bar)
    BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_position)
    TextView toolbarPosition;

    // 绑定String
    @BindString(R.string.exit_program_3s)
    String exitProgram3sText;
    @BindString(R.string.exit_account)
    String exitAccountText;
    @BindString(R.string.exit_program)
    String exitProgramText;
    @BindString(R.string.or)
    String or;
    @BindString(R.string.sysnc_failed)
    String sysncFailedText;
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

    // NavigationView
    private View navigationHreaderView;
    private NavHeaderViewHolder navHeaderViewHolder;
    private CustomPopupWindow popupWindow;
    private Uri titleImageCachaUri;
    private File titleImageCutFile;
    private File titleImageFile;
    private BmobFile avatarFile;

    // 主页面
    private HomeFragment homeFragment;
    private HomePresenter homePresenter;

    // 订单页面
    private OrdersFragment ordersFragment;

    // 发现页面
    private FindFragment findFragment;

    // 注册界面
    private RegisterFragment registerFragment;
    private RegisterPresenter registerPresenter;

    // 登陆界面
    private LoginFragment loginFragment;
    private LoginPresenter loginPresenter;

    private int currentErrorBNBItem = 0;

    private int previousBNBItem = 0;

    // 判断当前是否在登陆或注册
    private boolean isRegister = false;
    private boolean isLogin = false;

    // 退出程序
    private long lasExitTime = 0;
    private Handler exitHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case C.EXIT_PROGRAM_TIMING:
                    ToastUtil.showToast(exitProgram3sText);
                    lasExitTime = System.currentTimeMillis();
                    break;
                case C.EXIT_PROGRAM:
                    finish();
                    System.exit(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTransparentStatusBar();
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        initToolBar();
        initNavigationView();

        if (savedInstanceState != null) {
            currentErrorBNBItem = savedInstanceState.getInt(C.CURRENT_ERROR_BNB_ITEM);
            previousBNBItem = currentErrorBNBItem;
            switch (currentErrorBNBItem) {
                case 0:
                    homeFragment = (HomeFragment) getSupportFragmentManager()
                            .getFragment(savedInstanceState,
                                    C.CURRENT_ERROR_FRAGMENT);
                    homePresenter = new HomePresenter();
                    homeFragment.setPresenter(homePresenter);
                    homePresenter.setView(homeFragment);
                    break;
                case 1:
                    ordersFragment = (OrdersFragment) getSupportFragmentManager()
                            .getFragment(savedInstanceState,
                                    C.CURRENT_ERROR_FRAGMENT);
                    break;
                case 2:
                    findFragment = (FindFragment) getSupportFragmentManager()
                            .getFragment(savedInstanceState,
                                    C.CURRENT_ERROR_FRAGMENT);
                    break;
                default:
                    break;
            }
        } else {
            homeFragment = new HomeFragment();
            homePresenter = new HomePresenter();
            homeFragment.setPresenter(homePresenter);
            homePresenter.setView(homeFragment);
        }

        initContentView();
        initUserData();
        mainFragmentReplace(currentErrorBNBItem);
    }

    // 初始化标题栏
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
    }

    @Override
    public void onPositionChanged(String text) {
        toolbarPosition.setText(text);
    }

    // 初始化NavigationView
    private void initNavigationView() {
        navigationView.setItemIconTintList(null);
        navigationHreaderView = navigationView.getHeaderView(0);
        navHeaderViewHolder = new NavHeaderViewHolder(navigationHreaderView);
        navigationView.setNavigationItemSelectedListener (
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
    }

    // 显示注册界面
    private void addRegisterToShow() {
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        if (registerPresenter == null) {
            registerPresenter = new RegisterPresenter();
        }
        registerPresenter.setView(registerFragment);
        registerFragment.setPresenter(registerPresenter);
        registerFragment.setListener(new RegisterSuccessListener() {
            @Override
            public void success() {
                registerOrLoginSuccess();
            }

            @Override
            public void goToLogin() {
                addLoginToShow();
            }
        });
        mainFragmentReplace(C.REPLACE_REGISTER_LAYOUT);
    }

    // 显示登陆界面
    private void addLoginToShow() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        if (loginPresenter == null) {
            loginPresenter = new LoginPresenter();
        }
        loginPresenter.setView(loginFragment);
        loginFragment.setPresenter(loginPresenter);
        loginFragment.setListener(new LoginSuccessListener() {
            @Override
            public void success() {
                registerOrLoginSuccess();
            }
        });
        mainFragmentReplace(C.REPLACE_LOGIN_LAYOUT);
    }

    // 登陆或者注册成功回调
    private void registerOrLoginSuccess() {
        successUpdateLayout();
        mainFragmentReplace(bottomNavigationBar.getCurrentSelectedPosition());
    }

    // 登陆或者注册成功更新界面显示
    private void successUpdateLayout() {
        navHeaderViewHolder.register.setVisibility(View.GONE);
        navHeaderViewHolder.login.setVisibility(View.GONE);
        User user = BmobUser.getCurrentUser(User.class);
        navHeaderViewHolder.or.setText(user.getName());
        SharedPreferencesUtil.getSharedPreferences()
                .edit()
                .putString(C.LAST_LOGIN_PHONE_NUMBER, user.getMobilePhoneNumber());
        if (user.getAvatar() != null) {
            Glide.with(this)
                    .load(user.getAvatar())
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.error))
                    .into(navHeaderViewHolder.navHeaderImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.title_image_no_login)
                    .into(navHeaderViewHolder.navHeaderImage);
        }
    }

    // 退出登陆或者没有登陆的时候，界面重置为初始状态
    private void resetLogin() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            BmobUser.logOut();
        }
        navHeaderViewHolder.register.setVisibility(View.VISIBLE);
        navHeaderViewHolder.login.setVisibility(View.VISIBLE);
        navHeaderViewHolder.or.setText(or);
        Glide.with(this)
                .load(R.drawable.title_image_no_login)
                .into(navHeaderViewHolder.navHeaderImage);
    }

    // 切换界面主Fragment布局
    private void mainFragmentReplace(int itemNumber) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        switch (itemNumber) {
            case 0:
                transaction.replace(R.id.main_frame_layout, homeFragment);
                break;
            case 1:
                transaction.replace(R.id.main_frame_layout, ordersFragment);
                break;
            case 2:
                transaction.replace(R.id.main_frame_layout, findFragment);
                break;
            case C.REPLACE_REGISTER_LAYOUT:
                transaction.replace(R.id.main_frame_layout, registerFragment);
                if (!isLogin && !isRegister) {
                    transaction.addToBackStack(null);
                }
                break;
            case C.REPLACE_LOGIN_LAYOUT:
                transaction.replace(R.id.main_frame_layout, loginFragment);
                if (!isLogin && !isRegister) {
                    transaction.addToBackStack(null);
                }
        }
        transaction.commit();
        switch (itemNumber) {
            case C.REPLACE_REGISTER_LAYOUT:
                isRegister = true;
                isLogin = false;
                break;
            case C.REPLACE_LOGIN_LAYOUT:
                isRegister = false;
                isLogin = true;
                break;
            default:
                isRegister = false;
                isLogin = false;
        }
    }

    // 初始化主内容布局区域
    private void initContentView() {
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
                // 设置点击之后会扩散的背景的特效
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                // 设置选中的颜色
                .setActiveColor(R.color.colorPrimary)
                // 设置未选中的颜色
                .setInActiveColor(R.color.bottom_nav_bar_no_select)
                .addItem(new BottomNavigationItem(R.drawable.home, R.string.home))
                .addItem(new BottomNavigationItem(R.drawable.orders, R.string.orders))
                .addItem(new BottomNavigationItem(R.drawable.find, R.string.find))
                // 默认显示第一个，异常则显示异常那个
                .setFirstSelectedPosition(currentErrorBNBItem)
                // 初始化
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (previousBNBItem < position) {
                    transaction.setCustomAnimations(R.anim.fragment_replace_right_in,
                            R.anim.fragment_replace_left_out);
                } else if (previousBNBItem > position) {
                    transaction.setCustomAnimations(R.anim.fragment_replace_left_in,
                            R.anim.fragment_replace_right_out);
                }
                previousBNBItem = position;
                switch (position) {
                    case 0:
                        if (homeFragment == null) {
                            homeFragment = new HomeFragment();
                            if (homePresenter == null) {
                                homePresenter = new HomePresenter();
                            }
                            homeFragment.setPresenter(homePresenter);
                            homePresenter.setView(homeFragment);
                        }
                        currentErrorBNBItem = 0;
                        transaction.replace(R.id.main_frame_layout, homeFragment);
                        break;
                    case 1:
                        if (ordersFragment == null) {
                            ordersFragment = new OrdersFragment();
                        }
                        currentErrorBNBItem = 1;
                        transaction.replace(R.id.main_frame_layout, ordersFragment);
                        break;
                    case 2:
                        if (findFragment == null) {
                            findFragment = new FindFragment();
                        }
                        currentErrorBNBItem = 2;
                        transaction.replace(R.id.main_frame_layout, findFragment);
                        break;
                    default:
                        break;
                }
                transaction.commit();
                isRegister = false;
                isLogin = false;
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
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
        if (!isRegister && !isLogin) {
            outState.putInt(C.CURRENT_ERROR_BNB_ITEM, currentErrorBNBItem);
            if (currentErrorBNBItem == 0) {
                getSupportFragmentManager().putFragment(outState,
                        C.CURRENT_ERROR_FRAGMENT,
                        homeFragment);
            } else if (currentErrorBNBItem == 1) {
                getSupportFragmentManager().putFragment(outState,
                        C.CURRENT_ERROR_FRAGMENT,
                        ordersFragment);
            } else if (currentErrorBNBItem == 2) {
                getSupportFragmentManager().putFragment(outState,
                        C.CURRENT_ERROR_FRAGMENT,
                        findFragment);
            }
        } else {
            outState.clear();
            outState = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 初始化用户数据更新同步
    private void initUserData() {
        if (BmobUser.getCurrentUser(User.class) != null) {
            BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        ToastUtil.showToast(sysncFailedText);
                        resetLogin();
                    } else {
                        successUpdateLayout();
                    }
                }
            });
        } else {
            Glide.with(this)
                    .load(R.drawable.title_image_no_login)
                    .into(navHeaderViewHolder.navHeaderImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else if (!isRegister && !isLogin) {
            Message msg = new Message();
            if (System.currentTimeMillis() - lasExitTime <= 3000) {
                msg.what = C.EXIT_PROGRAM;
            } else {
                msg.what = C.EXIT_PROGRAM_TIMING;
            }
            exitHandle.handleMessage(msg);
        } else {
            isRegister = false;
            isLogin = false;
            mainFragmentReplace(bottomNavigationBar.getCurrentSelectedPosition());
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
        popupWindow = new CustomPopupWindow.Builder()
                .setContext(this)
                .setContentViewId(R.layout.popup_layout)
                .setWidth(4 * metrics.widthPixels / 5)
                .setHeight(metrics.heightPixels / 4)
                // 设置获取聚焦，点击Back物理键可关闭
                .setFocusable(true)
                // 设置PopupWindo外的触摸事件不传递到下面的窗口
                .setTouchable(false)
                // 设置被遮挡的视图的透明度
                .setActivityAndAlpha(this, 0.5f)
                // 设置入场退场动画
                .setAnimationStyle(R.style.popup_anim_style)
                .build()
                // 设置对于父控件的布局位置
                .showAtLocation(R.layout.activity_main, Gravity.CENTER, 0, 0);
        ((ImageView) popupWindow.getChildView(R.id.first_image)).setImageResource(firstImageId);
        ((TextView) popupWindow.getChildView(R.id.first_text)).setText(firstText);
        ((ImageView) popupWindow.getChildView(R.id.second_image)).setImageResource(secondImageId);
        ((TextView) popupWindow.getChildView(R.id.second_text)).setText(secondText);
        if (exitAccountText.equals(firstText)) {
            popupWindow.setChildOnCilickListener(R.id.first_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetLogin();
                    popupWindow.onDismiss();
                }
            });
            popupWindow.setChildOnCilickListener(R.id.second_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    System.exit(0);
                }
            });
        } else {
            popupWindow.setChildOnCilickListener(R.id.first_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            popupWindow.setChildOnCilickListener(R.id.second_layout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            case C.REQUEST_IMAGE_FROM_CAREMA:
                if (resultCode != RESULT_OK) {
                    ToastUtil.showToast(failedGetPictureText);
                } else {
                    titleImageCutFile = new File(getExternalCacheDir().getPath(),
                            C.TITLE_IMAGE_CUT_NAME);
                    // 开启裁剪
                    Intent intent = null;
                    try {
                        intent = ImageUtil.cutForPhoto(titleImageCachaUri,
                                ImageUtil.px2dip(this,
                                        navHeaderViewHolder.navHeaderImage.getWidth()),
                                ImageUtil.px2dip(this,
                                        navHeaderViewHolder.navHeaderImage.getHeight()),
                                titleImageCutFile);
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
                    titleImageCutFile = new File(getExternalCacheDir().getPath(),
                            C.TITLE_IMAGE_CUT_NAME);
                    // 开启裁剪
                    try {
                        Intent intent = ImageUtil.cutForPhoto(data.getData(),
                                ImageUtil.px2dip(this,
                                        navHeaderViewHolder.navHeaderImage.getWidth()),
                                ImageUtil.px2dip(this,
                                        navHeaderViewHolder.navHeaderImage.getHeight()),
                                titleImageCutFile);
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
                if (titleImageCutFile.exists()) {
                    // 压缩图片
                    titleImageFile = new File(getExternalCacheDir().getPath(),
                            "warm_stomacha_" + new Random().nextInt(100)
                                    + "_" + System.currentTimeMillis() + ".png");
                    if (titleImageFile.exists()) {
                        titleImageFile.delete();
                    }
                    try {
                        titleImageFile.createNewFile();
                    } catch (IOException e) {
                        ToastUtil.showToast(createImageFailedText);
                    }
                    Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                    options.isKeepSampling = true;
                    options.outfile = titleImageFile.getAbsolutePath();
                    Tiny.getInstance()
                            .source(titleImageCutFile)
                            .asFile()
                            .withOptions(options)
                            .compress(new FileCallback() {
                                @Override
                                public void callback(boolean isSuccess,
                                                     String outfile,
                                                     Throwable t) {
                                    if (isSuccess) {
                                        if (titleImageCutFile.exists()) {
                                            titleImageCutFile.delete();
                                        }
                                        //显示文件
                                        Glide.with(MainActivity.this)
                                                .load(outfile)
                                                .apply(new RequestOptions().centerCrop()
                                                        .placeholder(R.drawable.image_loading)
                                                        .error(R.drawable.error))
                                                .into(navHeaderViewHolder.navHeaderImage);
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
        avatarFile = new BmobFile(new File(outFile));
        avatarFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e != null) {
                    ToastUtil.showToast(avatarUpdatedSuccessfulText);
                } else {
                    User user = BmobUser.getCurrentUser(User.class);
                    User updateUser = new User();
                    updateUser.setAvatar(avatarFile.getFileUrl());
                    updateUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                User user = BmobUser.getCurrentUser(User.class);
                                user.setAvatar(avatarFile.getFileUrl());
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
            titleImageCachaUri = FileProvider.getUriForFile(this,
                    "com.axiang.fileprovider",
                    outputFile);
        } else {
            titleImageCachaUri = Uri.fromFile(outputFile);
        }
        // 启动相机程序
        Intent caremaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        caremaIntent.putExtra(MediaStore.EXTRA_OUTPUT, titleImageCachaUri);
        startActivityForResult(caremaIntent, C.REQUEST_IMAGE_FROM_CAREMA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.getSharedPreferences().edit()
                .putString(C.LONGITUDE, "")
                .putString(C.LATITUDE, "")
                .commit();
    }

    // NavigationView的头部布局的ViewHolder
    class NavHeaderViewHolder {

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

        @OnClick(R.id.register)
        public void registerClicked(View view) {
            drawerLayout.closeDrawer(Gravity.START);
            if (isRegister) {
                return;
            }
            addRegisterToShow();
        }

        @OnClick(R.id.login)
        public void loginClicked(View view) {
            drawerLayout.closeDrawer(Gravity.START);
            if (isLogin) {
                return;
            }
            addLoginToShow();
        }

        @OnClick(R.id.nav_header_image)
        public void navHeaderImageClicked(View view) {
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
    }
}
