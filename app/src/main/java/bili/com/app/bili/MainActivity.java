package bili.com.app.bili;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import bili.com.app.bili.base.RxBaseActivity;
import bili.com.app.bili.module.entry.IFavoritesFragment;
import bili.com.app.bili.module.home.HomePageFragment;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.utils.PreferenceUtil;
import bili.com.app.bili.utils.ToastUtil;
import bili.com.app.bili.widget.CircleImageView;
import butterknife.BindView;

public class MainActivity extends RxBaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.container)
    FrameLayout mContainer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private Fragment[] fragments;
    private int currentTabIndex;
    private int index;
    private long exitTime;
    private HomePageFragment mHomePageFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        initFragments();

        initNavigationView();

    }

    @Override
    public void setStatusBarColor() {

    }

    private void initFragments() {
        mHomePageFragment = HomePageFragment.newInstance();
        IFavoritesFragment mFavoritesFragment = IFavoritesFragment.newInstance();
        IFavoritesFragment mHistoryFragment = IFavoritesFragment.newInstance();
        IFavoritesFragment mAttentionPeopleFragment = IFavoritesFragment.newInstance();
        IFavoritesFragment mConsumeHistoryFragment = IFavoritesFragment.newInstance();
        IFavoritesFragment mSettingFragment = IFavoritesFragment.newInstance();
        fragments = new Fragment[]{
                mHomePageFragment,
                mFavoritesFragment,
                mHistoryFragment,
                mAttentionPeopleFragment,
                mConsumeHistoryFragment,
                mSettingFragment
        };

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mHomePageFragment)
                .show(mHomePageFragment).commit();
    }

    private void initNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        CircleImageView mUserAvatarView = (CircleImageView) headerView.findViewById(R.id.user_avatar_view);
        TextView mUserName = (TextView) headerView.findViewById(R.id.user_name);
        TextView mUserSign = (TextView) headerView.findViewById(R.id.user_other_info);
        ImageView mSwitchMode = (ImageView) headerView.findViewById(R.id.iv_head_switch_mode);
        //设置头像
        mUserAvatarView.setImageResource(R.drawable.ic_hotbitmapgg_avatar);
        //设置用户名 签名
        mUserName.setText(getResources().getText(R.string.hotbitmapgg));
        mUserSign.setText(getResources().getText(R.string.about_user_head_layout));

        mSwitchMode.setOnClickListener((v -> switchNightMode()));
        boolean flag = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (flag) {
            mSwitchMode.setImageResource(R.drawable.ic_switch_daily);
        } else {
            mSwitchMode.setImageResource(R.drawable.ic_switch_night);
        }
    }

    @Override
    public void initToolBar() {

    }

    private void switchNightMode() {
        boolean isNight = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (isNight) {
            // 日间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        } else {
            // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, true);
        }
        recreate();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.item_home:
                changeFragmentIndex(item,0);
                break;
            case R.id.item_download:
                break;
            case R.id.item_vip:
                break;
            case R.id.item_favourite:
                changeFragmentIndex(item,1);
                break;
            case R.id.item_history:
                changeFragmentIndex(item,2);
                break;
            case R.id.item_group:
                changeFragmentIndex(item,3);
                break;
            case R.id.item_tracker:
                changeFragmentIndex(item,4);
                break;
            case R.id.item_theme:
                break;
            case R.id.item_app:
                break;
            case R.id.item_settings:
                changeFragmentIndex(item,5);
                break;
        }
        return true;
    }

    /**
     * 切换Fragment的下标
     */
    private void changeFragmentIndex(MenuItem item, int currentIndex) {
        index = currentIndex;
        switchFragment();
        item.setChecked(true);
    }

    private void switchFragment(){
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.container, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentTabIndex = index;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mDrawerLayout.isDrawerOpen(mDrawerLayout.getChildAt(1))){
                mDrawerLayout.closeDrawers();
            }else{
                if(mHomePageFragment!=null){
                    if(mHomePageFragment.isOpenSearchView()){
                        mHomePageFragment.closeSearchView();
                    }else{
                        exitApp();
                    }
                }else{
                     exitApp();
                }
            }
        }

        return true;
    }

    private void exitApp(){
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtil.ShortToast("再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            PreferenceUtil.remove(ConstantUtil.SWITCH_MODE_KEY);
            finish();
        }
    }

    /**
     * DrawerLayout侧滑菜单开关
     */
    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

}
