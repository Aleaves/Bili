package bili.com.app.bili.adapter.section;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import bili.com.app.bili.R;
import bili.com.app.bili.adapter.ActivityCenterRecyclerAdapter;
import bili.com.app.bili.adapter.helper.AbsRecyclerViewAdapter;
import bili.com.app.bili.entity.recommend.RecommendInfo;
import bili.com.app.bili.module.common.BrowserActivity;
import bili.com.app.bili.widget.sectioned.StatelessSection;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hcc on 16/8/27 19:09
 * 100332338@qq.com
 * <p/>
 * 首页推荐界面活动中心section
 */
public class HomeRecommendActivityCenterSection extends StatelessSection {
    private Context mContext;
    private List<RecommendInfo.ResultBean.BodyBean> activitys;

    public HomeRecommendActivityCenterSection(Context context, List<RecommendInfo.ResultBean.BodyBean> activitys) {
        super(R.layout.layout_home_recommend_activitycenter, R.layout.layout_home_recommend_empty);
        this.mContext = context;
        this.activitys = activitys;
    }


    @Override
    public int getContentItemsTotal() {
        return 1;
    }


    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new EmptyViewHolder(view);
    }


    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new ActivityCenterViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        ActivityCenterViewHolder centerViewHolder = (ActivityCenterViewHolder) holder;
        centerViewHolder.mRecyclerView.setHasFixedSize(false);
        centerViewHolder.mRecyclerView.setNestedScrollingEnabled(false);
//        centerViewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
//                LinearLayoutManager.HORIZONTAL, false));
        centerViewHolder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        ActivityCenterRecyclerAdapter adapter = new ActivityCenterRecyclerAdapter(
                centerViewHolder.mRecyclerView, activitys);
        centerViewHolder.mRecyclerView.setAdapter(adapter);
    }


    static class ActivityCenterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recycle)
        RecyclerView mRecyclerView;

        ActivityCenterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
