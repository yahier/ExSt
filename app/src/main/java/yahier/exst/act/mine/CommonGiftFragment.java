package yahier.exst.act.mine;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.mine.GiftListAdapter;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.GiftTask;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonGiftFragment extends Fragment {

    private static final int COUNT = 15;

    private EmptyView mEmptyView;
    private RefreshListView mRefreshListView;
    private GiftListAdapter mAdapter;
    private ArrayList<MineGiftItem> mDataList;
    private Activity mActivity;

    private long mUserId;
    private int mSelectType;

    private int mPage = 1;
    private boolean mIsDestroy;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        Bundle bundle = getArguments();
        mUserId = bundle.getLong(KEY.USER_ID, 0);
        mSelectType = bundle.getInt(KEY.SELECT_TYPE, MineGiftItem.selecttype_get);
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

        getGiftList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mEmptyView.setRetryText(getString(R.string.me_pull_to_retry));

        mRefreshListView = (RefreshListView) getView().findViewById(R.id.refresh_list_view);
        mRefreshListView.setDivider1px();

        mDataList = new ArrayList<>();
        mAdapter = new GiftListAdapter(mDataList);
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getGiftList();
            }
        });
        mRefreshListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getGiftList();
            }
        });
        mAdapter.setInterface(new GiftListAdapter.IGiftListAdapter() {
            @Override
            public void onClickHead(int position) {
                MineGiftItem gift = mDataList.get(position);
                Intent intent = new Intent(mActivity, TribeMainAct.class);
                intent.putExtra("userId", gift.getObjuser().getUserid());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getGiftList() {
        GiftTask.getGiftList(mSelectType, mUserId, mPage, COUNT).setCallback(mActivity, mGetGiftListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<MineGiftItem>> mGetGiftListCallback = new SimpleTask.Callback<ArrayList<MineGiftItem>>() {
        @Override
        public void onError(TaskError e) {
            mRefreshListView.reset();
            ToastUtil.showToast(e.getMessage());
            if (mPage == 1 && mAdapter.getCount() == 0) {
                mEmptyView.showRetry();
            }
        }

        @Override
        public void onCompleted(ArrayList<MineGiftItem> result) {
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
