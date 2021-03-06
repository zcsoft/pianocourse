package com.zconly.pianocourse.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zconly.pianocourse.R;
import com.zconly.pianocourse.base.BaseMvpActivity;
import com.zconly.pianocourse.base.SingleClick;
import com.zconly.pianocourse.bean.BaseBean;
import com.zconly.pianocourse.bean.InvitationBean;
import com.zconly.pianocourse.bean.UserBean;
import com.zconly.pianocourse.event.SignInEvent;
import com.zconly.pianocourse.mvp.presenter.SignUpPresenter;
import com.zconly.pianocourse.mvp.view.SignUpView;
import com.zconly.pianocourse.util.ActionUtil;
import com.zconly.pianocourse.util.CountDownTimerTool;
import com.zconly.pianocourse.util.StringTool;
import com.zconly.pianocourse.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 找回密码
 * Created by dengbin
 */
public class FindPassActivity extends BaseMvpActivity<SignUpPresenter> implements SignUpView {

    private static final int GET_CODE_SUCCESS = 0;

    @BindView(R.id.email)
    EditText emailEt;
    @BindView(R.id.verification_code)
    EditText codeEt;
    @BindView(R.id.pass)
    EditText passEt;
    @BindView(R.id.pass_confirm)
    EditText pass2Et;
    @BindView(R.id.re_send)
    TextView sendTv;

    private CountDownTimerTool timer;
    private String emailOrPhone;
    private String pass;
    private String pass2;
    private String code;

    public static void start(Context context, String mobile) {
        Intent intent = new Intent(context, FindPassActivity.class);
        intent.putExtra("email", mobile);
        context.startActivity(intent);
    }

    private void doFindPass() {
        emailOrPhone = emailEt.getText().toString();
        if (!StringTool.isEmailOrPhone(mContext, emailOrPhone))
            return;

        code = codeEt.getText().toString();
        if (!StringTool.isCodeLength(code)) {
            ToastUtil.toast(getString(R.string.toast_not_code_length));
            return;
        }

        pass = passEt.getText().toString();
        if (!StringTool.isPassLength(pass)) {
            ToastUtil.toast(getString(R.string.toast_not_pass_length));
            return;
        }

        pass2 = pass2Et.getText().toString();
        if (!TextUtils.equals(pass, pass2.trim())) {
            ToastUtil.toast(getString(R.string.toast_pass_not_equal));
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", emailOrPhone);
        map.put("code", code);
        map.put("password", pass);
        map.put("behavior", "2");
        mPresenter.resetPassword(map);
        // mPresenter.verify(emailOrPhone, code, "2");
    }

    // 倒计时
    private void startTimer(int time) {
        timer = new CountDownTimerTool(mHandler, time * 1000, 1000);
        timer.start();
    }

    @SingleClick
    @OnClick({R.id.re_send, R.id.finish, R.id.help})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_send:
                emailOrPhone = emailEt.getText().toString();
                if (!StringTool.isEmailOrPhone(mContext, emailOrPhone))
                    return;
                mPresenter.retrieve(emailOrPhone);
                break;
            case R.id.finish:
                doFindPass();
                break;
            case R.id.help:
                ActionUtil.startAct(mContext, ContactCSActivity.class);
                break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_find_pass;
    }

    @Override
    protected SignUpPresenter getPresenter() {
        return new SignUpPresenter(this);
    }

    @Override
    protected boolean isBindEventBus() {
        return true;
    }

    @Override
    protected void initData() {

    }

    @SuppressLint("HandlerLeak")
    @Override
    protected boolean initView() {
        mTitleView.setTitle(getString(R.string.title_find_pass));
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CountDownTimerTool.TIMER_TICK:
                        sendTv.setEnabled(false);
                        sendTv.setText(String.valueOf(msg.arg1));
                        break;
                    case CountDownTimerTool.TIMER_FINISH:
                        sendTv.setEnabled(true);
                        sendTv.setText(getString(R.string.btn_re_send));
                        break;
                    case GET_CODE_SUCCESS:
                        codeEt.setHint(getString(R.string.hint_verification_code));
                        startTimer(60);
                        break;
                }
            }
        };

        return true;
    }

    @Override
    protected boolean hasTitleView() {
        return true;
    }

    @Override
    public void sendCodeSuccess(BaseBean response) {
        mHandler.sendEmptyMessage(GET_CODE_SUCCESS);
    }

    @Override
    public void verifySuccess(BaseBean response) {

    }

    @Override
    public void resetSuccess(UserBean.UserResult response) {
        ToastUtil.toast(getString(R.string.toast_find_pass_success));
        SignInActivity.start(mContext);
        finish();
    }

    @Override
    public void getInvitationSuccess(InvitationBean response) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SignInEvent event) {
        finishDown();
    }
}
