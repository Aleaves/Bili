package bili.com.app.bili.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import bili.com.app.bili.R;
import bili.com.app.bili.utils.SystemBarHelper;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by liulongbing on 17/8/18.
 */

public abstract class RxBaseActivity extends RxAppCompatActivity{

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setStatusBarColor();
        bind = ButterKnife.bind(this);
        initViews(savedInstanceState);
        initToolBar();
    }

    public void setStatusBarColor(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            SystemBarHelper.tintStatusBar(this, ContextCompat.getColor(this,R.color.colorPrimary));
        }
    }

    public abstract int getLayoutId();

    public abstract void initViews(Bundle savedInstanceState);

    public abstract void initToolBar();

    public void loadData(){};

    public void showProgressBar(){};

    public void hideProgressBar(){};

    public void initRecyclerView(){};

    public void initRefreshLayout(){};

    public void finishTask(){};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
