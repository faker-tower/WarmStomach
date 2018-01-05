package com.example.axiang.warmstomach.ui.home.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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
import com.example.axiang.warmstomach.contracts.RegisterContract;
import com.example.axiang.warmstomach.interfaces.LoginSuccessListener;
import com.example.axiang.warmstomach.interfaces.RegisterSuccessListener;
import com.example.axiang.warmstomach.ui.home.MainActivity;
import com.example.axiang.warmstomach.util.CreateUtil;
import com.example.axiang.warmstomach.util.ToastUtil;
import com.example.axiang.warmstomach.util.VertifyUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterFragment extends Fragment implements RegisterContract.View {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private Unbinder unbinder;

    // 绑定视图
    @BindView(R.id.register_input_phone_et)
    EditText inputPhoneEt;
    @BindView(R.id.register_input_vertify_et)
    EditText inputVertifyEt;
    @BindView(R.id.get_phone_vertify_bt)
    Button getPhoneVertifyBt;
    @BindView(R.id.register_password_et)
    EditText passwordEt;
    @BindView(R.id.register_bt)
    Button registerBt;
    @BindView(R.id.register_to_login)
    TextView registerToLogin;
    @BindView(R.id.password_visibility)
    ImageView passwordVisibility;

    // 绑定String
    @BindString(R.string.malformed_phone_number)
    String malformedPhoneNumber;
    @BindString(R.string.app_name)
    String appNameText;
    @BindString(R.string.request_vertify_success)
    String requestVertifySuccessText;
    @BindString(R.string.request_vertify_failed)
    String requestVertifyFailedText;
    @BindString(R.string.incorrect_password_format)
    String incorrectPasswordFormatText;
    @BindString(R.string.phone_number_registered)
    String phoneNumberRegisteredText;
    @BindString(R.string.incorrect_verification_code)
    String incorrectVerificationCodeText;
    @BindString(R.string.registration_success)
    String registrationSuccessText;
    @BindString(R.string.resend)
    String resendText;
    @BindString(R.string.registration_failed)
    String registrationFailedText;

    private RegisterContract.Presenter presenter;
    private RegisterSuccessListener listener;

    private boolean isPasswordVisibility = false;

    private final RegisterCountDownTimer countDownTimer =
            new RegisterCountDownTimer(60 * 1000, 1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.start();
        return view;
    }

    @OnClick(R.id.get_phone_vertify_bt)
    public void onGetPhoneVertifyBtClicked() {
        String phoneNumber = inputPhoneEt.getText()
                .toString()
                .replace(" ", "");
        if (!VertifyUtil.vertifyPhone(phoneNumber)) {
            ToastUtil.showToast(malformedPhoneNumber);
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

    @OnClick(R.id.register_bt)
    public void onRegisterBtClicked() {
        registerBt.setClickable(false);
        registerBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.colorSecondaryText));

        // 判断密码格式是否正确
        final String password = passwordEt.getText().toString();
        if (!VertifyUtil.vertifyPassword(password)) {
            ToastUtil.showToast(incorrectPasswordFormatText);
            registerBtRestore();
            return;
        }

        final String phoneNumber = inputPhoneEt.getText()
                .toString()
                .replace(" ", "");
        // 判断该手机号是否已经被注册
        if (presenter.phoneNumberIsRegistered(phoneNumber)) {
            ToastUtil.showToast(phoneNumberRegisteredText);
            registerBtRestore();
            return;
        }

        // 判断验证码是否正确
        BmobSMS.verifySmsCode(phoneNumber,
                inputVertifyEt.getText().toString().replace(" ", ""),
                new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            String name = CreateUtil.createUserName(10);
                            presenter.saveData(name, password, phoneNumber);
                        } else {
                            ToastUtil.showToast(incorrectVerificationCodeText);
                            registerBtRestore();
                        }
                    }
                });
    }

    @OnClick(R.id.password_visibility)
    public void onPasswordVisibilityClicked() {
        if (isPasswordVisibility) {
            passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                    | InputType.TYPE_CLASS_TEXT);
            passwordVisibility.setImageResource(R.drawable.password_visibility_off);
            isPasswordVisibility = false;
        } else {
            passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisibility.setImageResource(R.drawable.password_visibility);
            isPasswordVisibility = true;
        }
        // 设置光标到最后
        passwordEt.setSelection(passwordEt.getText().length());
    }

    private void registerBtRestore() {
        registerBt.setClickable(true);
        registerBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                R.color.register_get_vertify));
    }

    @Override
    public void registerSuccess() {
        ToastUtil.showToast(registrationSuccessText);
        listener.success();
    }

    @Override
    public void RegisterFailed() {
        ToastUtil.showToast(registrationFailedText);
        registerBtRestore();
    }

    @OnClick(R.id.register_to_login)
    public void onRegisterToLoginClicked() {
        listener.goToLogin();
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setListener(RegisterSuccessListener listener) {
        this.listener = listener;
    }

    @Override
    public void initView() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String localPhoneNumber = manager.getLine1Number();
            if (localPhoneNumber != null && !TextUtils.isEmpty(localPhoneNumber)) {
                inputPhoneEt.setText(localPhoneNumber);
            }
        } else {
            ActivityCompat.requestPermissions((MainActivity) getActivity(),
                    new String[] {Manifest.permission.READ_PHONE_STATE},
                    C.REQUEST_PREMISSION_PHONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == C.REQUEST_PREMISSION_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    class RegisterCountDownTimer extends CountDownTimer {

        public RegisterCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            getPhoneVertifyBt.setClickable(false);
            getPhoneVertifyBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.colorSecondaryText));
            getPhoneVertifyBt.setText(l / 1000 + "s " + resendText);
        }

        @Override
        public void onFinish() {
            getPhoneVertifyBt.setClickable(true);
            getPhoneVertifyBt.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.register_get_vertify));
            getPhoneVertifyBt.setText(resendText);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
