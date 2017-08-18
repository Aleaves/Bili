package bili.com.app.bili.base;

import android.os.Bundle;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

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
        bind = ButterKnife.bind(this);
        initViews(savedInstanceState);
        initToolBar();
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
