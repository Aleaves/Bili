package bili.com.app.bili.module.home;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import bili.com.app.bili.MainActivity;
import bili.com.app.bili.R;
import bili.com.app.bili.adapter.pager.HomePagerAdapter;
import bili.com.app.bili.base.RxLazyFragment;
import bili.com.app.bili.widget.CircleImageView;
import bili.com.app.bili.widget.NoScrollViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liulongbing on 17/8/20.
 */

public class HomePageFragment extends RxLazyFragment {


    @BindView(R.id.toolbar_user_avatar)
    CircleImageView mToolbarUserAvatar;
    @BindView(R.id.navigation_layout)
    LinearLayout mNavigationLayout;
    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabs;
    @BindView(R.id.view_pager)
    NoScrollViewPager mViewPager;
    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        setHasOptionsMenu(true);
        initToolbar();
        initSearchView();
        initViewPager();
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbarUserAvatar.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
    }

    private void initSearchView(){
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initViewPager(){
        HomePagerAdapter mHomeAdapter = new HomePagerAdapter(getChildFragmentManager(),getApplicationContext());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomeAdapter);
        mSlidingTabs.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
    }

    public boolean isOpenSearchView(){
        return mSearchView.isSearchOpen();
    }

    public void closeSearchView(){
        mSearchView.closeSearch();
    }

    @OnClick(R.id.navigation_layout)
    void toggleDrawer(){
        Activity activity = getActivity();
        if(activity instanceof MainActivity){
            ((MainActivity)activity).toggleDrawer();
        }
    }

}
