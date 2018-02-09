package com.example.axiang.warmstomach.ui.register_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.axiang.warmstomach.ActivityCollector;
import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.interfaces.OnLoginListener;
import com.example.axiang.warmstomach.interfaces.OnRegisterListener;
import com.example.axiang.warmstomach.presenters.LoginPresenter;
import com.example.axiang.warmstomach.presenters.RegisterPresenter;
import com.example.axiang.warmstomach.ui.register_login.fragments.LoginFragment;
import com.example.axiang.warmstomach.ui.register_login.fragments.RegisterFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterOrLoginActivity extends AppCompatActivity {

    // 绑定视图
    @BindView(R.id.rol_toolbar)
    Toolbar rolToolbar;

    @BindString(R.string.register)
    String registerText;
    @BindString(R.string.login)
    String loginText;

    private boolean isShowRegister;

    // 注册界面
    private RegisterFragment mRegisterFragment;
    private RegisterPresenter mRegisterPresenter;

    // 登陆界面
    private LoginFragment mLoginFragment;
    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
        setContentView(R.layout.activity_register_or_login);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            isShowRegister = Boolean.parseBoolean(getIntent().getStringExtra(C.IS_SHOW_REGISTER));
        } else {
            isShowRegister = savedInstanceState.getBoolean(C.IS_SHOW_REGISTER);
            if (isShowRegister) {
                mRegisterFragment = (RegisterFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, C.CURRENT_ERROR_FRAGMENT);
            } else {
                mLoginFragment = (LoginFragment) getSupportFragmentManager()
                        .getFragment(savedInstanceState, C.CURRENT_ERROR_FRAGMENT);
            }
        }
        initView();
    }

    private void initView() {
        setSupportActionBar(rolToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.rol_back);
        if (isShowRegister) {
            showRegisterLayout();
        } else {
            showLoginLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backToMainActivity();
    }

    // 显示登陆界面
    private void showLoginLayout() {
        if (mLoginFragment == null) {
            mLoginFragment = new LoginFragment();
        }
        if (mLoginPresenter == null) {
            mLoginPresenter = new LoginPresenter();
        }
        mLoginPresenter.setView(mLoginFragment);
        mLoginFragment.setPresenter(mLoginPresenter);
        mLoginFragment.setListener(new OnLoginListener() {
            @Override
            public void success() {
                backToMainActivity();
            }

            @Override
            public void goToRegister() {
                showRegisterLayout();
            }
        });
        isShowRegister = false;
        rolToolbar.setTitle(loginText);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rol_frame_layout, mLoginFragment)
                .commit();
    }

    // 显示注册界面
    private void showRegisterLayout() {
        if (mRegisterFragment == null) {
            mRegisterFragment = new RegisterFragment();
        }
        if (mRegisterPresenter == null) {
            mRegisterPresenter = new RegisterPresenter();
        }
        mRegisterPresenter.setView(mRegisterFragment);
        mRegisterFragment.setPresenter(mRegisterPresenter);
        mRegisterFragment.setListener(new OnRegisterListener() {
            @Override
            public void success() {
                backToMainActivity();
            }

            @Override
            public void goToLogin() {
                showLoginLayout();
            }
        });
        isShowRegister = true;
        rolToolbar.setTitle(registerText);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rol_frame_layout, mRegisterFragment)
                .commit();
    }

    private void backToMainActivity() {
        setResult(RESULT_OK, new Intent());
        finish();
        ActivityCollector.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShowRegister) {
            rolToolbar.setTitle(registerText);
        } else {
            rolToolbar.setTitle(loginText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(C.IS_SHOW_REGISTER, isShowRegister);
        if (isShowRegister) {
            getSupportFragmentManager().putFragment(outState,
                    C.CURRENT_ERROR_FRAGMENT,
                    mRegisterFragment);
        } else {
            getSupportFragmentManager().putFragment(outState,
                    C.CURRENT_ERROR_FRAGMENT,
                    mLoginFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
    }
}
