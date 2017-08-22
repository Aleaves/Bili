package bili.com.app.bili.module.home.recommend;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import bili.com.app.bili.R;
import bili.com.app.bili.adapter.section.HomeRecommendActivityCenterSection;
import bili.com.app.bili.adapter.section.HomeRecommendBannerSection;
import bili.com.app.bili.adapter.section.HomeRecommendTopicSection;
import bili.com.app.bili.base.RxLazyFragment;
import bili.com.app.bili.entity.recommend.RecommendBannerInfo;
import bili.com.app.bili.entity.recommend.RecommendInfo;
import bili.com.app.bili.network.RetrofitHelper;
import bili.com.app.bili.utils.ConstantUtil;
import bili.com.app.bili.widget.CustomEmptyView;
import bili.com.app.bili.widget.banner.BannerEntity;
import bili.com.app.bili.widget.sectioned.SectionedRecyclerViewAdapter;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liulongbing on 17/8/21.
 */

public class HomeRecommendedFragment extends RxLazyFragment {

    @BindView(R.id.recycle)
    RecyclerView mRecycle;
    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsRefreshing = false;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private List<BannerEntity> banners = new ArrayList<>();
    private List<RecommendBannerInfo.DataBean> recommendBanners = new ArrayList<>();
    private List<RecommendInfo.ResultBean> results = new ArrayList<>();

    public static HomeRecommendedFragment newInstance() {
        return new HomeRecommendedFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_recommended;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if(!isPrepared||!isVisible){
            return;
        }
        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(()->{
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearData();
                loadData();
            }
        });
    }

    private void clearData(){
        banners.clear();
        recommendBanners.clear();
        results.clear();
        mIsRefreshing = true;
        mSectionedAdapter.removeAllSections();
    }

    @Override
    protected void initRecyclerView() {
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        mRecycle.setLayoutManager(mLayoutManager);
        mRecycle.setAdapter(mSectionedAdapter);
        setRecycleNoScroll();
    }
    private void setRecycleNoScroll(){
        mRecycle.setOnTouchListener(((v, event) -> mIsRefreshing));
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBiliAppAPI()
                .getRecommendedBannerInfo()
                .compose(bindToLifecycle())
                .map(RecommendBannerInfo::getData)
                .flatMap(new Func1<List<RecommendBannerInfo.DataBean>, Observable<RecommendInfo>>() {
                    @Override
                    public Observable<RecommendInfo> call(List<RecommendBannerInfo.DataBean> dataBeen) {
                        recommendBanners.addAll(dataBeen);
                        return RetrofitHelper.getBiliAppAPI().getRecommendedInfo();
                    }
                })
                .compose(bindToLifecycle())
                .map(RecommendInfo::getResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeen -> {
                    results.addAll(resultBeen);
                    finishTask();
                },throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    initEmptyView();
                });
    }

    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        convertBanner();
        mSectionedAdapter.addSection(new HomeRecommendBannerSection(banners));
        int size = results.size();
        for (int i = 0; i < size; i++) {
            String type = results.get(i).getType();
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case ConstantUtil.TYPE_TOPIC:
                        //话题
                        mSectionedAdapter.addSection(new HomeRecommendTopicSection(getActivity(),
                                results.get(i).getBody().get(0).getCover(),
                                results.get(i).getBody().get(0).getTitle(),
                                results.get(i).getBody().get(0).getParam()));
                        break;
                    case ConstantUtil.TYPE_ACTIVITY_CENTER:
                        //活动中心
                        mSectionedAdapter.addSection(new HomeRecommendActivityCenterSection(
                                getActivity(),
                                results.get(i).getBody()));
                        break;
                    default:
//                        mSectionedAdapter.addSection(new HomeRecommendedSection(
//                                getActivity(),
//                                results.get(i).getHead().getTitle(),
//                                results.get(i).getType(),
//                                results.get(1).getHead().getCount(),
//                                results.get(i).getBody()));
                        break;
                }
            }
            String style = results.get(i).getHead().getStyle();
            if (style.equals(ConstantUtil.STYLE_PIC)) {
//                mSectionedAdapter.addSection(new HomeRecommendPicSection(getActivity(),
//                        results.get(i).getBody().get(0).getCover(),
//                        results.get(i).getBody().get(0).getParam()));
            }
        }
        mSectionedAdapter.notifyDataSetChanged();
    }

    private void convertBanner(){
        Observable.from(recommendBanners)
                .compose(bindToLifecycle())
                .forEach(dataBean -> banners.add(new BannerEntity(dataBean.getValue(),
                        dataBean.getTitle(),dataBean.getImage())));
    }

    public void initEmptyView(){

    }

    public void hideEmptyView(){
        mEmptyLayout.setVisibility(View.GONE);
        mRecycle.setVisibility(View.VISIBLE);
    }


}
