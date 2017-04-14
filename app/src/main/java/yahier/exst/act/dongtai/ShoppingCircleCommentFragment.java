package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.dongtai.DongtaiCommentAdapter;
import com.stbl.stbl.adapter.dongtai.ShoppingCircleCommentAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.dialog.ActionSheet;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesRemarkDBItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdUserItem2;
import com.stbl.stbl.item.ad.ShoppingCircleComment;
import com.stbl.stbl.task.dongtai.DongtaiDetailTask;
import com.stbl.stbl.task.shoppingCircleTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * Created by tnitf on 2016/6/12.
 */
public class ShoppingCircleCommentFragment extends BaseFragment {

    private ListView mListView;
    private ShoppingCircleCommentAdapter mAdapter;
    private EmptyView mEmptyView;

    private ArrayList<ShoppingCircleComment> mDataList;

    private int mLastid = 0;
    private final int COUNT = 15;
    private String mStatusesid;

    private InputDialog mInputDialog;

    private ShoppingCircleComment mClickComment;

    private SwipeToLoadLayout mSwipeToLoadLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //mStatusesid = getArguments().getLong("statusesId", 0);
        mStatusesid = getArguments().getString("statusesId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dongtai_comment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
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
        mAdapter = new ShoppingCircleCommentAdapter(mDataList);
        mListView.setAdapter(mAdapter);

        mAdapter.setInterface(new ShoppingCircleCommentAdapter.AdapterInterface() {
            @Override
            public void onItemClick(ShoppingCircleComment item) {
                showActionDialog(item);
            }

            @Override
            public void onClickHead(ShoppingCircleComment item) {

//                if (item.getUser().getClicktype() == AdUserItem2.typeClickAd && !TextUtils.isEmpty(item.getUser().getAdurl())) {
//                    Intent intent = new Intent(mActivity, CommonWeb.class);
//                    intent.putExtra("url", item.getUser().getAdurl());
//                    mActivity.startActivity(intent);
//                }else{
//
//                }
                    Intent intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", item.getUser().getAduserid());
                    mActivity.startActivity(intent);


            }

            @Override
            public void praiseOrUnpraise(ShoppingCircleComment item) {
                //praiseOrUnpraiseComment(item);
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
        if (mTaskManager == null) {
            LogUtil.logE("getCommentList", "mTaskManager is null");
            return;
        } else {
            LogUtil.logE("getCommentList", "mTaskManager not null");
        }
        mTaskManager.start(shoppingCircleTask.getCommentList(mStatusesid, mLastid, COUNT)
                .setCallback(new HttpTaskCallback<ArrayList<ShoppingCircleComment>>(mActivity) {

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
                    public void onSuccess(ArrayList<ShoppingCircleComment> result) {
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


    private void showActionDialog(final ShoppingCircleComment comment) {
        final AdUserItem2 user = comment.getUser();
        if (user == null) {
            return;
        }
        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.reply));
        if (SharedToken.getUserId().equals(user.getAduserid() + "")) {
            actionList.add(getString(R.string.delete));
        }
        final ActionSheet actionSheet = new ActionSheet(mActivity);
        actionSheet.setAdapter(actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionSheet.dismiss();
                switch(position) {
                    case 0:
                        mInputDialog.clearContent();
//                        mInputDialog.setHint(getString(R.string.reply) + user.getNickname());
                        mInputDialog.setHint(getString(R.string.reply) + (user.getAdnickname()));
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
        mTaskManager.start(shoppingCircleTask.addComment(mStatusesid, content, commentid)
                .setCallback(new HttpTaskCallback<ShoppingCircleComment>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
//                        ToastUtil.showToast(e.msg);
                        loadingTipsDialog.showFaild(getString(R.string.statuses_reply_faild));
                        loadingTipsDialog.postDissmiss();
                    }

                    @Override
                    public void onSuccess(ShoppingCircleComment result) {
                        loadingTipsDialog.showSuccess(getString(R.string.statuses_reply_success));
                        loadingTipsDialog.postDissmiss();
                        EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeShoppingCircleReplyCommentSuccess));
                        mInputDialog.hideFaceView();
                        afterAddComment(result);
                        mInputDialog.clearContent();
                    }

                    @Override
                    public void onFinish() {
                        mInputDialog.setSendButtonEnable(true);
                    }
                }));
    }

    private void deleteComment(final ShoppingCircleComment comment) {
        final long commentid = comment.getCommentid();
        mTaskManager.start(shoppingCircleTask.deleteComment(commentid)
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
                    }
                }));
    }

    public void afterAddComment(ShoppingCircleComment comment) {
        mEmptyView.hide();
        mDataList.add(0, comment);
        mAdapter.notifyDataSetChanged();
    }


    public ArrayList<ShoppingCircleComment> getData() {
        return mDataList;
    }
}
