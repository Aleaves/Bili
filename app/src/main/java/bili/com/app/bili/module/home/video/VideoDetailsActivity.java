package bili.com.app.bili.module.home.video;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxBaseActivity;
import bili.com.app.bili.entity.video.VideoDetailsInfo;
import bili.com.app.bili.event.AppBarStateChangeEvent;
import bili.com.app.bili.network.RetrofitHelper;
import bili.com.app.bili.network.auxiliary.UrlHelper;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.utils.DisplayUtil;
import bili.com.app.bili.utils.SystemBarHelper;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liulongbing on 17/8/24.
 */

public class VideoDetailsActivity extends RxBaseActivity{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.video_preview)
    ImageView mVideoPreview;
    @BindView(R.id.tab_layout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFAB;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_player)
    TextView mTvPlayer;
    @BindView(R.id.tv_av)
    TextView mAvText;

    private int av;
    private String imgUrl;
    private VideoDetailsInfo.DataBean mVideoDetailsInfo;
    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();


    public static void launch(Activity activity, int aid, String imgUrl) {
        Intent intent = new Intent(activity, VideoDetailsActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ConstantUtil.EXTRA_AV, aid);
        intent.putExtra(ConstantUtil.EXTRA_IMG_URL, imgUrl);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_details;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            av = intent.getIntExtra(ConstantUtil.EXTRA_AV, -1);
            imgUrl = intent.getStringExtra(ConstantUtil.EXTRA_IMG_URL);
        }
        Glide.with(VideoDetailsActivity.this)
                .load(UrlHelper.getClearVideoPreviewUrl(imgUrl))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(mVideoPreview);
        loadData();
        mFAB.setClickable(false);
        mFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_20)));
        //mFAB.setTranslationY(-getResources().getDimension(R.dimen.floating_action_button_size_half));
        mFAB.setOnClickListener(v -> {

        });
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> setViewsTranslation(verticalOffset));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeEvent(){
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
                if (state == State.EXPANDED) {
                    //展开状态
                    mTvPlayer.setVisibility(View.GONE);
                    mAvText.setVisibility(View.VISIBLE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(VideoDetailsActivity.this, 15), 0);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    mTvPlayer.setVisibility(View.VISIBLE);
                    mAvText.setVisibility(View.GONE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(VideoDetailsActivity.this, 150), 0);
                } else {
                    mTvPlayer.setVisibility(View.GONE);
                    mAvText.setVisibility(View.VISIBLE);
                    mToolbar.setContentInsetsRelative(DisplayUtil.dp2px(VideoDetailsActivity.this, 15), 0);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewsTranslation(int target) {
        mFAB.setTranslationY(target);
        if (target == 0) {
            showFAB();
        } else if (target < 0) {
            hideFAB();
        }
    }

    private void showFAB() {
        mFAB.animate().scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .start();
        mFAB.setClickable(true);
    }

    private void hideFAB() {
        mFAB.animate().scaleX(0f).scaleY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .start();
        mFAB.setClickable(false);
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        //设置StatusBar透明
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setHeightAndPadding(this, mToolbar);
        mAvText.setText("av" + av);
    }

    @Override
    public void loadData() {

        RetrofitHelper.getBiliAppAPI()
                .getVideoDetails(av)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoDetailsInfo -> {
                    mVideoDetailsInfo = videoDetailsInfo.getData();
                    finishTask();
                },throwable -> {
                    mFAB.setClickable(false);
                    mFAB.setBackgroundTintList(ColorStateList.valueOf(
                            getResources().getColor(R.color.gray_20)));
                });

    }

    @Override
    public void finishTask() {
        mFAB.setClickable(true);
        mFAB.setBackgroundTintList(
                ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        mCollapsingToolbarLayout.setTitle("");
        if (TextUtils.isEmpty(imgUrl)) {
            Glide.with(VideoDetailsActivity.this)
                    .load(mVideoDetailsInfo)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bili_default_image_tv)
                    .dontAnimate()
                    .into(mVideoPreview);
        }

        VideoCommentFragment mVideoCommentFragment = VideoCommentFragment.newInstance(av);
        VideoIntroductionFragment mVideoIntroductionFragment = VideoIntroductionFragment.newInstance(av);
        fragments.add(mVideoIntroductionFragment);
        fragments.add(mVideoCommentFragment);
        setPagerTitle(String.valueOf(mVideoDetailsInfo.getStat().getReply()));
    }

    private void setPagerTitle(String num){
        titles.add("简介");
        titles.add("评论" + "(" + num + ")");
        VideoDetailsPagerAdapter mAdapter = new VideoDetailsPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setViewPager(mViewPager);
        measureTabLayoutTextWidth(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                measureTabLayoutTextWidth(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void measureTabLayoutTextWidth(int position) {
        String title = titles.get(position);
        TextView titleView = mSlidingTabLayout.getTitleView(position);
        TextPaint paint = titleView.getPaint();
        float textWidth = paint.measureText(title);
        mSlidingTabLayout.setIndicatorWidth(textWidth / 3);
    }

    public static class VideoDetailsPagerAdapter extends FragmentStatePagerAdapter{

        private List<Fragment> fragments;
        private List<String> titles;

        public VideoDetailsPagerAdapter(FragmentManager fm,List<Fragment> fragments,List<String> titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
