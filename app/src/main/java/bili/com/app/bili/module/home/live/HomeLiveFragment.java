package bili.com.app.bili.module.home.live;

import android.os.Bundle;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxLazyFragment;
import bili.com.app.bili.entity.gank.GirlData;
import bili.com.app.bili.network.RetrofitHelper;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liulongbing on 17/8/21.
 */

public class HomeLiveFragment extends RxLazyFragment{


    public static HomeLiveFragment newInstance(){
        return new HomeLiveFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_live;
    }

    @OnClick(R.id.bt)
    void getGankData(){
        RetrofitHelper.getGankApi().getGirls(1)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GirlData>() {
                    @Override
                    public void call(GirlData girlData) {

                    }
                });
    }

    @Override
    public void finishCreateView(Bundle state) {

    }

}
