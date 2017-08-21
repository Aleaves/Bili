package bili.com.app.bili.module.entry;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxLazyFragment;
import bili.com.app.bili.widget.CustomEmptyView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liulongbing on 17/8/20.
 */

public class IFavoritesFragment extends RxLazyFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.empty_view)
    CustomEmptyView mEmptyView;

    public static IFavoritesFragment newInstance() {
        return new IFavoritesFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_empty;
    }

    @Override
    public void finishCreateView(Bundle state) {
        mToolbar.setTitle("我的收藏");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
//        mToolbar.setNavigationOnClickListener(v -> {
//            Activity activity1 = getActivity();
//            if (activity1 instanceof MainActivity) {
//                ((MainActivity) activity1).toggleDrawer();
//            }
//        });
        mEmptyView.setEmptyImage(R.drawable.img_tips_error_fav_no_data);
        mEmptyView.setEmptyText("没有找到你的收藏哟");
    }

}
