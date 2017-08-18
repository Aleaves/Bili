package bili.com.app.bili.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.trello.rxlifecycle.components.RxActivity;

import java.util.concurrent.TimeUnit;

import bili.com.app.bili.MainActivity;
import bili.com.app.bili.R;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.utils.PreferenceUtil;
import bili.com.app.bili.utils.SystemUiVisibilityUtil;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by liulongbing on 17/8/17.
 */

public class SplashActivity extends RxActivity{

    private Unbinder bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(),true);
        setUpSplash();
    }

    private void setUpSplash(){
        Observable.timer(2000, TimeUnit.MICROSECONDS)
                .compose(bindToLifecycle())
                .subscribe(along ->finishTask());
    }

    private void finishTask(){
        boolean isLogin = PreferenceUtil.getBoolean(ConstantUtil.KEY,false);
        if(isLogin){
            startActivity(new Intent(this, MainActivity.class));
        }else{

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
