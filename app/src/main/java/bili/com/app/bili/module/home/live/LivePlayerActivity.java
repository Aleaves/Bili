package bili.com.app.bili.module.home.live;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.stetho.common.LogUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import bili.com.app.bili.R;
import bili.com.app.bili.base.RxBaseActivity;
import bili.com.app.bili.network.RetrofitHelper;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.widget.CircleImageView;
import bili.com.app.bili.widget.lovelike.LoveLikeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by liulongbing on 17/8/23.
 */

public class LivePlayerActivity extends RxBaseActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.video_view)
    SurfaceView mVideoView;
    @BindView(R.id.bili_anim)
    ImageView mBiliAnim;
    @BindView(R.id.video_start_info)
    TextView mVideoStartInfo;
    @BindView(R.id.right_play)
    ImageView mRightPlay;
    @BindView(R.id.bottom_play)
    ImageView mBottomPlay;
    @BindView(R.id.bottom_love)
    ImageView mBottomLove;
    @BindView(R.id.bottom_fullscreen)
    ImageView mBottomFullscreen;
    @BindView(R.id.bottom_layout)
    RelativeLayout mBottomLayout;
    @BindView(R.id.love_layout)
    LoveLikeLayout mLoveLayout;
    @BindView(R.id.live_layout)
    FrameLayout mLiveLayout;
//    @BindView(R.id.user_pic)
//    CircleImageView mUserPic;
//    @BindView(R.id.user_name)
//    TextView mUserName;
//    @BindView(R.id.live_num)
//    TextView mLiveNum;
//    @BindView(R.id.user_info_layout)
//    RelativeLayout mUserInfoLayout;

    private int flag = 0;
    private int cid;
    private String title;
    private int online;
    private String face;
    private String name;
    private int mid;
    private SurfaceHolder holder;
    private IjkMediaPlayer ijkMediaPlayer;
    private AnimationDrawable mAnimViewBackground;
    private boolean isPlay = false;


    public static void launch(Activity activity, int cid, String title, int online, String face, String name, int mid) {
        Intent mIntent = new Intent(activity, LivePlayerActivity.class);
        mIntent.putExtra(ConstantUtil.EXTRA_CID, cid);
        mIntent.putExtra(ConstantUtil.EXTRA_TITLE, title);
        mIntent.putExtra(ConstantUtil.EXTRA_ONLINE, online);
        mIntent.putExtra(ConstantUtil.EXTRA_FACE, face);
        mIntent.putExtra(ConstantUtil.EXTRA_NAME, name);
        mIntent.putExtra(ConstantUtil.EXTRA_MID, mid);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_live_details;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            cid = intent.getIntExtra(ConstantUtil.EXTRA_CID, 0);
            title = intent.getStringExtra(ConstantUtil.EXTRA_TITLE);
            online = intent.getIntExtra(ConstantUtil.EXTRA_ONLINE, 0);
            face = intent.getStringExtra(ConstantUtil.EXTRA_FACE);
            name = intent.getStringExtra(ConstantUtil.EXTRA_NAME);
            mid = intent.getIntExtra(ConstantUtil.EXTRA_MID, 0);
        }
        initVideo();
        initUserInfo();
        startAnim();
    }

    private void startAnim() {
        mAnimViewBackground = (AnimationDrawable) mBiliAnim.getBackground();
        mAnimViewBackground.start();
    }

    private void stopAnim() {
        mAnimViewBackground.stop();
        mBiliAnim.setVisibility(View.GONE);
        mVideoStartInfo.setVisibility(View.GONE);
    }

    private void initVideo(){
        holder = mVideoView.getHolder();
        ijkMediaPlayer = new IjkMediaPlayer();
        getLiveUrl();
    }

    private void getLiveUrl(){
        RetrofitHelper.getLiveAPI()
                .getLiveUrl(cid)
                .compose(bindToLifecycle())
                .map(responseBody -> {
                    try {
                        String str = responseBody.string();
                        str = str.substring(0,str.lastIndexOf("!"));
                        String result = str.substring(str.lastIndexOf("[") + 1, str.lastIndexOf("]") - 1);
                        return result;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        Log.i("========",s);
                        playVideo(s);
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    stopAnim();
                    isPlay = true;
                    mVideoView.setVisibility(View.VISIBLE);
                    mRightPlay.setImageResource(R.drawable.ic_tv_stop);
                    mBottomPlay.setImageResource(R.drawable.ic_portrait_stop);
                },throwable -> {
                    LogUtil.d("直播地址url获取失败" + throwable.getMessage());
                });
    }

    private void playVideo(String uri) {
        try {
            ijkMediaPlayer.setDataSource(this, Uri.parse(uri));
            ijkMediaPlayer.setDisplay(holder);
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    ijkMediaPlayer.setDisplay(holder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }
            });
            ijkMediaPlayer.prepareAsync();
            ijkMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ijkMediaPlayer.setKeepInBackground(false);
    }

    private void initUserInfo(){
//        Glide.with(LivePlayerActivity.this)
//                .load(face)
//                .centerCrop()
//                .dontAnimate()
//                .placeholder(R.drawable.ico_user_default)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(mUserPic);
//        mUserName.setText(name);
//        mLiveNum.setText(String.valueOf(online));
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick({R.id.right_play, R.id.bottom_play, R.id.bottom_fullscreen,
            R.id.video_view, R.id.bottom_love})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_play:
                ControlVideo();
                break;
            case R.id.bottom_play:
                ControlVideo();
                break;
            case R.id.bottom_fullscreen:
                break;
            case R.id.video_view:
                if (flag == 0) {
                    startBottomShowAnim();
                    flag = 1;
                } else {
                    startBottomHideAnim();
                    flag = 0;
                }
                break;
            //case R.id.user_pic:
//                UserInfoDetailsActivity.launch(LivePlayerActivity.this, name, mid, face);
//                ControlVideo();
//                mRightPlayBtn.setVisibility(View.VISIBLE);
            //    break;
            case R.id.bottom_love:
                mLoveLayout.addLove();
                break;
        }
    }
    private void startBottomShowAnim() {
        mBottomLayout.setVisibility(View.VISIBLE);
        mRightPlay.setVisibility(View.VISIBLE);
    }


    private void startBottomHideAnim() {
        mBottomLayout.setVisibility(View.GONE);
        mRightPlay.setVisibility(View.GONE);
    }

    private void ControlVideo() {
        if (isPlay) {
            ijkMediaPlayer.pause();
            isPlay = false;
            mRightPlay.setImageResource(R.drawable.ic_tv_play);
            mBottomPlay.setImageResource(R.drawable.ic_portrait_play);
        } else {
            ijkMediaPlayer.start();
            isPlay = true;
            mRightPlay.setImageResource(R.drawable.ic_tv_stop);
            mBottomPlay.setImageResource(R.drawable.ic_portrait_stop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkMediaPlayer.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
