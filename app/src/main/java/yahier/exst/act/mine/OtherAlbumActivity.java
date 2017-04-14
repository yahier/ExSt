package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.MyAlbumAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.mine.AlbumTask;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshGridView;

import java.util.ArrayList;

public class OtherAlbumActivity extends ThemeActivity {

    private static final int COUNT = 15;

    private RefreshGridView mRefreshGridView;
    private ArrayList<Photo> mDataList;
    private MyAlbumAdapter mAdapter;

    private EmptyView mEmptyView;

    private long mUserId;
    private UserItem mUserItem;

    private int mPage = 1;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_album);

        mUserItem = (UserItem) getIntent().getSerializableExtra(EXTRA.USER_ITEM);
        if (mUserItem == null) {
            ToastUtil.showToast("数据传递为空");
            finish();
            return;
        }
        mUserId = mUserItem.getUserid();
        if (mUserId == Long.valueOf(SharedToken.getUserId())) {
            setLabel("我的相册");
        } else {
            setLabel((mUserItem.getAlias() == null || mUserItem.getAlias().equals("") ? mUserItem.getNickname() :mUserItem.getAlias()) + "的相册");
        }

        initView();

        getPhotoList();
    }

    private void initView() {
        mRefreshGridView = (RefreshGridView) findViewById(R.id.refresh_grid_view);
        mRefreshGridView.setNumColumns(3);
        mRefreshGridView.setHorizontalSpacing(3);
        mRefreshGridView.setVerticalSpacing(3);
        mDataList = new ArrayList<>();
        mAdapter = new MyAlbumAdapter(mDataList);
        mRefreshGridView.setAdapter(mAdapter);

        mRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = mDataList.get(position);
                Intent intent = new Intent(OtherAlbumActivity.this, PhotoImagePagerAct.class);
                intent.putExtra("index", position);
                intent.putParcelableArrayListExtra("photo", mDataList);
                startActivity(intent);
                overridePendingTransition(R.anim.ani_zoom_in, 0);

            }
        });

        mRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                getPhotoList();
            }
        });
        mRefreshGridView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getPhotoList();
            }
        });

        mRefreshGridView.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);


        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mEmptyView.setRetryText("下拉重试");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getPhotoList() {
        mTaskManager.start(AlbumTask.getPhotoList(mUserId, mPage, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<Photo>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshGridView.reset();
                    }

                    @Override
                    public void onError(com.stbl.base.library.task.TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        if (mPage == 1 && mAdapter.getCount() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<Photo> result) {
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
                }));
    }

    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int imageSpacing = getResources().getDimensionPixelSize(
                    R.dimen.dp_3);
            int columnWidth = (mRefreshGridView.getWidth() - imageSpacing * 2) / 3;
            mAdapter.setItemHeight(columnWidth);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRefreshGridView.getViewTreeObserver().removeOnGlobalLayoutListener(
                        this);
            } else {
                mRefreshGridView.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
            }
        }
    };

}
