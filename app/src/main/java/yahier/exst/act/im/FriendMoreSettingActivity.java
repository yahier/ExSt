package yahier.exst.act.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.model.TinyTribeInfo;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2016/8/31.
 */

public class FriendMoreSettingActivity extends ThemeActivity {

    private CheckBox seeMyCb;
    private CheckBox seeHisCb;

    private Button unfollowBtn;
    private Button deleteFriendBtn;

    private long mUserId;
    private Relation mRelation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = getIntent().getLongExtra(KEY.USER_ID, 0L);
        mRelation = (Relation) getIntent().getSerializableExtra(KEY.RELATION);
        if (mUserId == 0 || mRelation == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_friend_more_setting);
        setLabel(getString(R.string.me_more_setting));
        initView();
        getTinyTribeInfo();
    }

    private void initView() {
        int relationFlag = mRelation.getRelationflag();
        deleteFriendBtn = (Button) findViewById(R.id.btn_delete_friend);
        if (Relation.isFriend(relationFlag)) {
            deleteFriendBtn.setOnClickListener(mClickListener);
        } else {
            deleteFriendBtn.setVisibility(View.GONE);
        }

        unfollowBtn = (Button) findViewById(R.id.btn_unfollow);
        if (mRelation.getIsattention() == Statuses.isattention_yes) {
            unfollowBtn.setOnClickListener(mClickListener);
        } else {
            unfollowBtn.setVisibility(View.GONE);
        }

        seeMyCb = (CheckBox) findViewById(R.id.cb_see_my);
        seeMyCb.setChecked(mRelation.getIsconceal() == Relation.isconceal_yes);

        seeHisCb = (CheckBox) findViewById(R.id.cb_see_his);
        seeHisCb.setChecked(mRelation.getIsshield() == Relation.isshield_yes);

        //没有关注ta。就不显示不看ta
        if (mRelation.getIsattention() == Relation.isattention_no) {
            findViewById(R.id.layout_see_his).setVisibility(View.GONE);
        }

        // 如果没有关注我，就不能设置不让他看了
        if (mRelation.getIsfans() == Relation.isbeattention_no) {
            findViewById(R.id.layout_see_my).setVisibility(View.GONE);
        }

        seeMyCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRelation.getIsconceal() == Relation.isconceal_yes) {
                    unconceal();
                } else {
                    conceal();
                }
            }
        });

        seeHisCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRelation.getIsshield() == Relation.isshield_yes) {
                    unshield();
                } else {
                    shield();
                }
            }
        });
    }

    private void getTinyTribeInfo() {
        mTaskManager.start(UserTask.getTinyTribeInfo(mUserId)
                .setCallback(new HttpTaskCallback<TinyTribeInfo>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(TinyTribeInfo result) {
                        mRelation = result.relation;
                        initView();
                    }
                }));
    }

    private void conceal() {
        seeMyCb.setEnabled(false);
        mTaskManager.start(UserTask.conceal(mUserId)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.deal_success);
                        mRelation.setIsconceal(Relation.isconceal_yes);
                        seeMyCb.setChecked(true);
                    }

                    @Override
                    public void onFinish() {
                        seeMyCb.setEnabled(true);
                    }
                }));
    }

    private void unconceal() {
        seeMyCb.setEnabled(false);
        mTaskManager.start(UserTask.unconceal(mUserId)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.deal_success);
                        mRelation.setIsconceal(Relation.isconceal_no);
                        seeMyCb.setChecked(false);
                    }

                    @Override
                    public void onFinish() {
                        seeMyCb.setEnabled(true);
                    }
                }));
    }

    private void shield() {
        seeHisCb.setEnabled(false);
        mTaskManager.start(UserTask.shield(mUserId)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.shield_success);
                        mRelation.setIsshield(Relation.isshield_yes);
                        seeHisCb.setChecked(true);
                    }

                    @Override
                    public void onFinish() {
                        seeHisCb.setEnabled(true);
                    }
                }));
    }

    private void unshield() {
        seeHisCb.setEnabled(false);
        mTaskManager.start(UserTask.unshield(mUserId)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.unshield_success);
                        mRelation.setIsshield(Relation.isshield_no);
                        seeHisCb.setChecked(false);
                    }

                    @Override
                    public void onFinish() {
                        seeHisCb.setEnabled(true);
                    }
                }));
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_unfollow:
                    alertUnfollow();
                    break;
                case R.id.btn_delete_friend:
                    alertDeleteFriend();
                    break;
            }
        }
    };

    private void alertUnfollow() {
        TipsDialog.popup(mActivity, R.string.confirm_unfollow, R.string.cancel, R.string.queding, new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                unfollow();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void unfollow() {
        unfollowBtn.setEnabled(false);
        mTaskManager.start(UserTask.unfollow(mUserId)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.unfollow_success);
                        unfollowBtn.setVisibility(View.GONE);
                        mRelation.setIsattention(Relation.isattention_no);
                        finish();
                    }

                    @Override
                    public void onFinish() {
                        unfollowBtn.setEnabled(true);
                    }
                }));

    }

    private void alertDeleteFriend() {
        TipsDialog.popup(mActivity, R.string.confirm_delete_friend, R.string.cancel, R.string.queding, new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                deleteFriend();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void deleteFriend() {
        deleteFriendBtn.setEnabled(false);
        mTaskManager.start(UserTask.deleteFriend(mUserId, 2)
                .setCallback(new HttpTaskCallback<Long>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(Long result) {
                        ToastUtil.showToast(R.string.delete_success);
                        EventBus.getDefault().post(new IMEventType(IMEventType.typeChangedContact));
                        deleteFriendBtn.setVisibility(View.GONE);
                        mRelation.setRelationflag(mRelation.getRelationflag() - 2);
                        finish();
                    }

                    @Override
                    public void onFinish() {
                        deleteFriendBtn.setEnabled(true);
                    }
                }));
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(KEY.RELATION, mRelation);
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }
}
