package com.zconly.pianocourse.mvp.presenter;

import com.mvp.exception.ApiException;
import com.mvp.observer.HttpRxObservable;
import com.mvp.observer.HttpRxObserver;
import com.mvp.utils.RetrofitUtils;
import com.zconly.pianocourse.activity.LoginActivity;
import com.zconly.pianocourse.bean.result.TokenResult;
import com.zconly.pianocourse.mvp.service.ApiService;
import com.zconly.pianocourse.mvp.view.LoginView;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @Description: java类作用描述
 * @Author: dengbin
 * @CreateDate: 2020/3/23 21:56
 * @UpdateUser: dengbin
 * @UpdateDate: 2020/3/23 21:56
 * @UpdateRemark: 更新说明
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    public LoginPresenter(LoginView mView) {
        super(mView);
    }

    public void signIn(String phone, String pwd) {
        if (!isNetConnect()) return;
        Map<String, String> params = new HashMap<>();
        params.put("phoen", phone);
        params.put("pwd", pwd);
        Observable<TokenResult> observer = RetrofitUtils.create(ApiService.class).signIn(params);
        HttpRxObservable.getObservable(observer, (LoginActivity) mView).subscribe(new HttpRxObserver<TokenResult>() {
            @Override
            protected void onStart(Disposable d) {
                if (mView != null)
                    mView.loading("");
            }

            @Override
            protected void onError(ApiException e) {
                if (mView != null)
                    mView.onError(e);
            }

            @Override
            protected void onSuccess(TokenResult response) {
                if (mView != null)
                    mView.loginSuccess(response);
            }
        });
    }
}
