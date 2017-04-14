package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.dongtai.DongtaiCommentAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.dialog.ActionSheet;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesRemarkDBItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.dongtai.DongtaiDetailTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiCommentFragment extends BaseFragment {

    private ListView mListView;
    private DongtaiCommentAdapter mAdapter;
    private EmptyView mEmptyView;

    private ArrayList<StatusesComment> mDataList;

    private int mLastid = 0;
    private final int COUNT = 15;
    private long mStatusesid;

    private InputDialog mInputDialog;

    private StatusesComment mClickComment;

    private SwipeToLoadLayout mSwipeToLoadLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStatusesid = getArguments().getLong("statusesId", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dongtai_comment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        //getCommentList();
    }

    private void initView() {
        mEmptyView = (EmptyView) getView().findViewById(R.id.empty_view);
        mListView = (ListView) getView().findViewById(R.id.swipe_target);

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastid = 0;
                getCommentList();
            }
        });

        mSwipeToLoadLayout = (SwipeToLoadLayout) getView().findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getCommentList();
            }
        });

        mInputDialog = new InputDialog(mActivity);
        mInputDialog.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReply();
            }
        });

        mDataList = new ArrayList<>();
        mAdapter = new DongtaiCommentAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        mAdapter.setInterface(new DongtaiCommentAdapter.AdapterInterface() {
            @Override
            public void onItemClick(StatusesComment item) {
                showActionDialog(item);
            }

            @Override
            public void onClickHead(StatusesComment item) {
                Intent intent = new Intent(mActivity, TribeMainAct.class);
                intent.putExtra("userId", item.getUser().getUserid());
                mActivity.startActivity(intent);
            }

            @Override
            public void praiseOrUnpraise(StatusesComment item) {
                praiseOrUnpraiseComment(item);
            }
        });
    }

    public ListView getRefreshView() {
        return mListView;
    }

    public void refreshCommentList() {
        mLastid = 0;
        getCommentList();
    }

    // 获取评论列表
    private void getCommentList() {
        mTaskManager.start(DongtaiDetailTask.getCommentList(mStatusesid, mLastid, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<StatusesComment>>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ServerError.codeStatusesNotExist) {
                            //show dialog in DongtaiDetailActivity
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                        if (mLastid == 0 && mDataList.size() == 0) {
                            mEmptyView.showRetry();
                        }
                    }

                    @Override
                    public void onSuccess(ArrayList<StatusesComment> result) {
                        if (mLastid == 0) {
                            if (result.size() == 0) {
                                mEmptyView.showEmpty();
                                return;
                            }
                            mDataList.clear();
                        }
                        mDataList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                        if (result.size() > 0) {
                            mLastid = result.get(result.size() - 1).getCommentid();
                        } else {
                            ToastUtil.showToast(R.string.no_more);
                        }
                        //insert(result);
                    }

                    @Override
                    public void onFinish() {
                        mEmptyView.hide();
                        mSwipeToLoadLayout.setLoadingMore(false);
                    }
                }));
    }


    void insert(ArrayList<StatusesComment> result) {
        int size = result.size();
        DongtaiRemarkDB db = new DongtaiRemarkDB(mActivity);
        for (int i = size - 1; i >= 0; i--) {
            StatusesComment comment = result.get(i);
            StatusesRemarkDBItem itemDB = new StatusesRemarkDBItem();
            itemDB.setCommentId(comment.getCommentid());
            itemDB.setRemarkContent(comment.getContent());
            //itemDB.setRemarkTime(comment.getCreatetime());
            itemDB.setStatusesId(comment.getStatusesid());
            itemDB.setUser1Id(comment.getUser().getUserid());
            itemDB.setUser1Name(comment.getUser().getNickname());

            UserItem lastuser = comment.getLastuser();
            if (lastuser != null) {
                itemDB.setUser2Id(lastuser.getUserid());
                itemDB.setUser2Name(lastuser.getNickname());
            }
            //LogUtil.logE("insert 数据库:"+lastuser.getNickname());
            db.insert(itemDB);
        }
    }

    private void praiseOrUnpraiseComment(final StatusesComment item) {
        long commentid = item.getCommentid();
        mTaskManager.start(DongtaiDetailTask.praiseOrUnpraiseComment(commentid)
                .setCallback(new HttpTaskCallback<PraiseResult>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(PraiseResult result) {
                        PraiseResult praise = result;
                        item.setPraisecount(praise.getCount());
                        if (praise.getType() == PraiseResult.type_add) {
//                    ToastUtil.showToast("点赞成功");
                            item.setIspraise(true);
                        } else if (praise.getType() == PraiseResult.type_cancel) {
//                    ToastUtil.showToast("取消点赞成功");
                            item.setIspraise(false);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void showActionDialog(final StatusesComment comment) {
        final UserItem user = comment.getUser();
        if (user == null) {
            return;
        }
        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.reply));
        if (SharedToken.getUserId().equals(user.getUserid() + "")) {
            actionList.add(getString(R.string.delete));
        }
        final ActionSheet actionSheet = new ActionSheet(mActivity);
        actionSheet.setAdapter(actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionSheet.dismiss();
                switch (position) {
                    case 0:
                        mInputDialog.clearContent();
//                        mInputDialog.setHint(getString(R.string.reply) + user.getNickname());
                        mInputDialog.setHint(getString(R.string.reply) + (user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias()));
                        mInputDialog.show();
                        mClickComment = comment;
                        break;
                    case 1:
                        deleteComment(comment);
                        break;
                }
            }
        });
        actionSheet.show();
    }

    // 回复
    void addReply() {
        String content = mInputDialog.getContent();
        if (content.equals("")) {
            ToastUtil.showToast(R.string.please_input_first);
            return;
        }

        if (content.length() > Config.remarkContentLength) {
            ToastUtil.showToast("内容过长，最多" + Config.remarkContentLength + "个文字");
            return;
        }
        mInputDialog.setSendButtonEnable(false);

        final LoadingTipsDialog loadingTipsDialog = new LoadingTipsDialog(mActivity);
        loadingTipsDialog.showLoading(getString(R.string.statuses_replying));

        long commentid = mClickComment.getCommentid();
        mTaskManager.start(DongtaiDetailTask.addComment(mStatusesid, content, commentid)
                .setCallback(new HttpTaskCallback<StatusesComment>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
//                        ToastUtil.showToast(e.msg);
                        loadingTipsDialog.showFaild(getString(R.string.statuses_reply_faild));
                        loadingTipsDialog.postDissmiss();
                    }

                    @Override
                    public void onSuccess(StatusesComment result) {
                        loadingTipsDialog.showSuccess(getString(R.string.statuses_reply_success));
                        loadingTipsDialog.postDissmiss();
                        mInputDialog.hideFaceView();
                        afterAddComment(result);
                        mInputDialog.clearContent();
                        ((DongtaiDetailActivity) mActivity).afterAddComment();
                        new DongtaiRemarkDB(mActivity).insertToDB(getData());
                    }

                    @Override
                    public void onFinish() {
                        mInputDialog.setSendButtonEnable(true);
                    }
                }));
    }

    private void deleteComment(final StatusesComment comment) {
        final long commentid = comment.getCommentid();
        mTaskManager.start(DongtaiDetailTask.deleteComment(commentid)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.delete_comment_success);
                        mDataList.remove(comment);
                        mAdapter.notifyDataSetChanged();
                        if (mDataList.size() == 0) {
                            mEmptyView.showEmpty();
                        }
                        ((DongtaiDetailActivity) mActivity).afterDeleteComment();
                    }
                }));
    }

    public void afterAddComment(StatusesComment comment) {
        mEmptyView.hide();
        mDataList.add(0, comment);
        mAdapter.notifyDataSetChanged();
    }


    public ArrayList<StatusesComment> getData() {
        return mDataList;
    }
}
