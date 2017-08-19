package bili.com.app.bili.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import bili.com.app.bili.MainActivity;
import bili.com.app.bili.R;
import bili.com.app.bili.base.RxBaseActivity;
import bili.com.app.bili.utils.CommonUtil;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.utils.PreferenceUtil;
import bili.com.app.bili.utils.ToastUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liulongbing on 17/8/18.
 */

public class LoginActivity extends RxBaseActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_icon_left)
    ImageView mIvIconLeft;
    @BindView(R.id.iv_icon_centre)
    ImageView mIvIconCentre;
    @BindView(R.id.iv_icon_right)
    ImageView mIvIconRight;
    @BindView(R.id.logo_ll)
    RelativeLayout mLogoLl;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.delete_username)
    ImageButton mDeleteUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.login_ll)
    LinearLayout mLoginLl;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mEtUsername.setOnFocusChangeListener(((v, hasFocus) -> {
            if(hasFocus&&mEtUsername.getText().length()>0){
                mDeleteUsername.setVisibility(View.VISIBLE);
            }else{
                mDeleteUsername.setVisibility(View.GONE);
            }
        }));
        mEtPassword.setOnFocusChangeListener(((v, hasFocus) -> {
            if(hasFocus) {
                mIvIconLeft.setImageResource(R.drawable.ic_22_hide);
                mIvIconRight.setImageResource(R.drawable.ic_33_hide);
            }else{
                mIvIconLeft.setImageResource(R.drawable.ic_22);
                mIvIconRight.setImageResource(R.drawable.ic_33);
            }
        }));

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mEtPassword.setText("");
                if (s.length() > 0) {
                    // 如果用户名有内容时候 显示删除按钮
                    mDeleteUsername.setVisibility(View.VISIBLE);
                } else {
                    // 如果用户名有内容时候 显示删除按钮
                    mDeleteUsername.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_cancle);
        mToolbar.setTitle("登录");
        mToolbar.setNavigationOnClickListener(v->finish());
    }

    @OnClick({R.id.btn_login,R.id.delete_username})
    void onClick(View view){
        switch (view.getId()){
            case R.id.btn_login:
                //登录
                boolean isNetConnected = CommonUtil.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ToastUtil.ShortToast("当前网络不可用,请检查网络设置");
                    return;
                }
                login();
                break;
            case R.id.delete_username:
                mEtUsername.setText("");
                mEtPassword.setText("");
                mDeleteUsername.setVisibility(View.GONE);
                mEtUsername.setFocusable(true);
                mEtUsername.setFocusableInTouchMode(true);
                mEtUsername.requestFocus();
                break;
        }
    }

    private void login() {
        String name = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.ShortToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.ShortToast("密码不能为空");
            return;
        }
        PreferenceUtil.putBoolean(ConstantUtil.KEY, true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

}
