package bili.com.app.bili.module.home.video;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import bili.com.app.bili.R;
import bili.com.app.bili.adapter.VideoCommentAdapter;
import bili.com.app.bili.base.RxLazyFragment;
import bili.com.app.bili.entity.video.VideoCommentInfo;
import bili.com.app.bili.network.RetrofitHelper;
import bili.com.app.bili.utils.ConstantUtil;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liulongbing on 17/8/26.
 */

public class VideoCommentFragment extends RxLazyFragment{

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private int aid;
    private int pageNum = 1;
    private int pageSize = 20;
    private ArrayList<VideoCommentInfo.List> comments = new ArrayList<>();

    public static VideoCommentFragment newInstance(int aid){
        VideoCommentFragment videoCommentFragment = new VideoCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.AID,aid);
        videoCommentFragment.setArguments(bundle);
        return videoCommentFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_comment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        aid = getArguments().getInt(ConstantUtil.AID);
        initRecyclerView();
        loadData();
    }
    VideoCommentAdapter mRecyclerAdapter;
    @Override
    protected void initRecyclerView() {
        mRecyclerAdapter = new VideoCommentAdapter(mRecyclerView, comments);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mAdapter = new HeaderViewRecyclerAdapter(mRecyclerAdapter);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    protected void loadData() {
        int ver = 3;
        RetrofitHelper.getBiliAPI()
                .getVideoComment(aid,pageNum, pageSize, ver)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoCommentInfo -> {
                    ArrayList<VideoCommentInfo.List> list = videoCommentInfo.list;
                    ArrayList<VideoCommentInfo.HotList> hotList = videoCommentInfo.hotList;
                    if (list.size() < pageSize) {
                        //loadMoreView.setVisibility(View.GONE);
                        //mAdapter.removeFootView();
                    }
                    comments.addAll(list);
                    //hotComments.addAll(hotList);
                    finishTask();
                },throwable -> {

                });
    }

    @Override
    protected void finishTask() {
        if (pageNum * pageSize - pageSize - 1 > 0) {
            mRecyclerAdapter.notifyItemRangeChanged(pageNum * pageSize - pageSize - 1, pageSize);
        } else {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
