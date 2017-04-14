package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.MyTrackAdapter;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.model.Footprint;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.VisitTrackTask;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/20.
 */
public class MyTrackFragment extends Fragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private MyTrackAdapter mAdapter;
    private ArrayList<Footprint> mDataList;
    private Activity mActivity;

    private int mPage = 1;
    private boolean mIsDestroy;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receive_gift, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        getMyTrackList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();

        mDataList = new ArrayList<>();
        mAdapter = new MyTrackAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getMyTrackList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getMyTrackList();
            }
        });

        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Footprint footprint = mDataList.get(position);
                Intent intent = new Intent(mActivity, CommonWebInteact.class);
                intent.setData(Uri.parse(footprint.getActionurl()));
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getMyTrackList() {
        VisitTrackTask.getMyTrackList(mPage, COUNT).setCallback(mActivity, mGetMyTrackListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Footprint>> mGetMyTrackListCallback = new SimpleTask.Callback<ArrayList<Footprint>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mRefreshListView.reset();
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<Footprint> result) {
            mRefreshListView.reset();
            if (mPage == 1) {
                if (result.size() == 0) {
                    mEmptyView.showEmpty();
                } else {
                    mEmptyView.hide();
                }
                mDataList.clear();
            }
            mDataList.addAll(result);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
}
