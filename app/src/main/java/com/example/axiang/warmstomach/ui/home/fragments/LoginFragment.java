package com.example.axiang.warmstomach.ui.home.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.axiang.warmstomach.C;
import com.example.axiang.warmstomach.R;
import com.example.axiang.warmstomach.contracts.LoginContract;
import com.example.axiang.warmstomach.data.User;
import com.example.axiang.warmstomach.interfaces.LoginSuccessListener;
import com.example.axiang.warmstomach.util.SharedPreferencesUtil;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.util.VertifyUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by a2389 on 2017/12/16.
 */

public class LoginFragment extends Fragment implements LoginContract.View {

    private Unbinder unbinder;

    // 绑定视图
    @BindView(R.id.login_input_phone_et)
    EditText loginInputPhoneEt;
    @BindView(R.id.login_input_vertify_et)
    EditText loginInputVertifyEt;
    @BindView(R.id.login_get_phone_vertify_bt)
    Button loginGetPhoneVertifyBt;
    @BindView(R.id.login_password_et)
    EditText loginPasswordEt;
    @BindView(R.id.login_password_visibility)
    ImageView loginPasswordVisibility;
    @BindView(R.id.login_bt)
    Button loginBt;
    @BindView(R.id.use_password_login)
    TextView usePasswordLogin;

    // 绑定String
    @BindString(R.string.malformed_phone_number)
    String malformedPhoneNumberText;
    @BindString(R.string.app_name)
    String appNameText;
    @BindString(R.string.wrong_password)
    String wrongPasswordText;
    @BindString(R.string.request_vertify_success)
    String requestVertifySuccessText;
    @BindString(R.string.request_vertify_failed)
    String requestVertifyFailedText;
    @BindString(R.string.phone_number_no_registered)
    String phoneNumberNoRegisteredText;
    @BindString(R.string.incorrect_password_format)
    String incorrectPasswordFormatText;
    @BindString(R.string.resend)
    String resendText;
    @BindString(R.string.use_vertify_login)
    String useVertifyLoginText;
    @BindString(R.string.use_password_login)
    String usePasswordLoginText;
    @BindString(R.string.login_success)
    String loginSuccessText;
    @BindString(R.string.sysnc_failed)
    String sysncFailedText;
    @BindString(R.string.incorrect_verification_code)
    String incorrectVerificationCodeText;

    private LoginContract.Presenter presenter;
    private LoginSuccessListener listener;

    private boolean isPasswordVisibility = false;

    private boolean isUseVertifyLogin = true;

    private final LoginCountDownTimer countDownTimer =
            new LoginCountDownTimer(60 * 1000, 1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.start();
        return view;
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initView() {
        String phoneNumber = SharedPreferencesUtil
                .getSharedPreferences().getString(C.LAST_LOGIN_PHONE_NUMBER, "");
        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber)) {
            loginInputPhoneEt.setText(phoneNumber);
        }
    }

    @OnClick(R.id.login_get_phone_vertify_bt)
    public void onLoginGetPhoneVertifyBtClicked() {
        String phoneNumber = loginInputPhoneEt.getText()
                .toString()
                .replace(" ", "");
        if (!VertifyUtil.vertifyPhone(phoneNumber)) {
            ToastUtil.showToast(malformedPhoneNumberText);
        }

        // 接收验证码到手机
        BmobSMS.requestSMSCode(phoneNumber,
                appNameText,
                new QueryListener<Integer>() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if (e == null) {
                            countDownTimer.start();
                            ToastUtil.showToast(requestVertifySuccessText);
                        } else {
                            ToastUtil.showToast(requestVertifyFailedText);
                        }
                    }
                });
    }

    @OnClick(R.id.login_password_visibility)
    public void onLoginPasswordVisibilityClicked() {
        if (isPasswordVisibility) {
            loginPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                    | InputType.TYPE_CLASS_TEXT);
            loginPasswordVisibility.setImageResource(R.drawable.password_visibility_off);
            isPasswordVisibility = false;
        } else {
            loginPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            loginPasswordVisibility.setImageResource(R.drawable.password_visibility);
            isPasswordVisibility = true;
        }
        // 设置光标到最后
        loginPasswordEt.setSelection(loginPasswordEt.getText().length());
    }

    @OnClick(R.id.login_bt)
    public void onLoginBtClicked() {
        loginBt.setClickable(false);
        loginBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.colorSecondaryText));

        final String phoneNumber = loginInputPhoneEt.getText()
                .toString()
                .replace(" ", "");
        // 判断该手机号是否已经注册
        if (!presenter.phoneNumberRegistered(phoneNumber)) {
            ToastUtil.showToast(phoneNumberNoRegisteredText);
            loginBtRestore();
            return;
        }

        if (isUseVertifyLogin) {
            // 验证码登陆
            String vertify = loginInputVertifyEt.getText()
                    .toString()
                    .replace(" ", "");
            presenter.loginByVertify(phoneNumber, vertify);
        } else {
            // 密码登陆
            String password = loginPasswordEt.getText().toString();
            if (!VertifyUtil.vertifyPassword(password)) {
                ToastUtil.showToast(incorrectPasswordFormatText);
                loginBtRestore();
                return;
            }
            presenter.loginByPasssword(phoneNumber, password);
        }
    }

    private void loginBtRestore() {
        loginBt.setClickable(true);
        loginBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.register_get_vertify));
    }

    @OnClick(R.id.use_password_login)
    public void onUsePasswordLoginClicked() {
        if (isUseVertifyLogin) {
            loginInputVertifyEt.setVisibility(View.GONE);
            loginGetPhoneVertifyBt.setVisibility(View.GONE);
            loginPasswordEt.setVisibility(View.VISIBLE);
            loginPasswordVisibility.setVisibility(View.VISIBLE);
            isUseVertifyLogin = false;
            usePasswordLogin.setText(useVertifyLoginText);
        } else {
            loginInputVertifyEt.setVisibility(View.VISIBLE);
            loginGetPhoneVertifyBt.setVisibility(View.VISIBLE);
            loginPasswordEt.setVisibility(View.GONE);
            loginPasswordVisibility.setVisibility(View.GONE);
            isUseVertifyLogin = true;
            usePasswordLogin.setText(usePasswordLoginText);
        }
    }

    @Override
    public void loginSuccess() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            ToastUtil.showToast(loginSuccessText);
            listener.success();
        } else {
            loginBtRestore();
            ToastUtil.showToast(sysncFailedText);
        }
    }

    @Override
    public void loginFailed() {
        if (isUseVertifyLogin) {
            ToastUtil.showToast(incorrectVerificationCodeText);
        } else {
            ToastUtil.showToast(wrongPasswordText);
        }
        loginBtRestore();
    }

    public void setListener(LoginSuccessListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    class LoginCountDownTimer extends CountDownTimer {

        public LoginCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            loginGetPhoneVertifyBt.setClickable(false);
            loginGetPhoneVertifyBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.colorSecondaryText));
            loginGetPhoneVertifyBt.setText(l / 1000 + "s " + resendText);
        }

        @Override
        public void onFinish() {
            loginGetPhoneVertifyBt.setClickable(true);
            loginGetPhoneVertifyBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.register_get_vertify));
            loginGetPhoneVertifyBt.setText(resendText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
