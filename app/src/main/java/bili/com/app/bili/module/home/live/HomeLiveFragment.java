package bili.com.app.bili.module.home.live;

import android.os.Bundle;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxLazyFragment;

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

    @Override
    public void finishCreateView(Bundle state) {

    }

}
