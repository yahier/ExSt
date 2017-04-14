package yahier.exst.act.home.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SingleSelectFriendActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.Publisher;
import com.stbl.stbl.model.bangyibang.ShareInfo;
import com.stbl.stbl.model.bangyibang.ShareUserInfo;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;

public class HelpTaActivity extends ThemeActivity {

    private static final int REQUEST_CODE_SELECT_FRIEND = 1;

    private TextView mSelectedFriendTv;
    private EditText mContentEt;
    private TextView mContentCountTv;

    private BangYiBangItem mHelp;

    private boolean mIsDestroy;

    private LoadingDialog mLoadingDialog;

    private UserItem mUser;

    private SystemTipDialog mTipDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_ta);

        mHelp = (BangYiBangItem) getIntent().getSerializableExtra(EXTRA.BANG_YI_BANG_ITEM);
        if (mHelp == null) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_help_ta));

        mSelectedFriendTv = (TextView) findViewById(R.id.tv_select_friend);

        Publisher publisher = mHelp.getPublisher();

        ImageView headIv = (ImageView) findViewById(R.id.iv_head);
        ImageUtils.loadCircleHead(publisher.getImgmiddleurl(), headIv);

        TextView industryTv = (TextView) findViewById(R.id.tv_industry);
        String industry = mHelp.getIssuetype();
        industryTv.setText(industry);

        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText(StringUtil.getWidth(industry.length()) + mHelp.getIssuetitle());

        TextView contentTv = (TextView) findViewById(R.id.tv_content);
        contentTv.setText(mHelp.getIssuedescription());

        TextView timeTv = (TextView) findViewById(R.id.tv_time);
        timeTv.setText(DateUtil.getHelpTime(mHelp
                .getPublishtime()));

        TextView bountyTv = (TextView) findViewById(R.id.tv_bounty);
        bountyTv.setText(String.format(getString(R.string.me_reward_d), mHelp.getRewardbean()));

        findViewById(R.id.layout_select_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpTaActivity.this, SingleSelectFriendActivity.class);
                if (mUser != null) {
                    intent.putExtra(EXTRA.USER_ITEM, mUser);
                }
                intent.putExtra(EXTRA.HAS_SELF, 1); //0-不包含，1-包含
                startActivityForResult(intent, REQUEST_CODE_SELECT_FRIEND);
            }
        });

        mContentEt = (EditText) findViewById(R.id.et_content);
        mContentCountTv = (TextView) findViewById(R.id.tv_content_count);

        mContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentCountTv.setText(s.toString().length() + "/150");
            }
        });

        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeCommit();
            }
        });

        mLoadingDialog = new LoadingDialog(this);
        mTipDialog = new SystemTipDialog(this);
        mTipDialog.setTitle(getString(R.string.me_tip));
        mTipDialog.setContent(getString(R.string.me_is_give_up_this_edit));
        mTipDialog.setInterface(new SystemTipDialog.ISystemTipDialog() {
            @Override
            public void onConfirm() {
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_FRIEND) {
            switch (resultCode) {
                case RESULT_OK:
                    UserItem user = (UserItem) data.getSerializableExtra(EXTRA.USER_ITEM);
                    if (user != null) {
                        if (user.getUserid() == mHelp.getPublisher().getUserid()) {
                            ToastUtil.showToast(R.string.me_cannot_recommend_demand_publisher);
                            return;
                        }
                        mUser = user;
                        String name = TextUtils.isEmpty(mUser.getAlias()) ? mUser.getNickname() : mUser.getAlias();
                        String text = String.format(getString(R.string.me_choosed_friend_s), name);
                        mSelectedFriendTv.setText(Html.fromHtml(text));
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void beforeCommit() {
        long issueid = mHelp.getIssueid();
        if (mUser == null) {
            ToastUtil.showToast(R.string.me_please_choose_friend);
            return;
        }
        long shareuserid = mUser.getUserid();
        String sharereason = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(sharereason)) {
            ToastUtil.showToast(R.string.me_please_input_desc);
            return;
        }
        if (sharereason.length() > 150) {
            ToastUtil.showToast(R.string.me_help_desc_length_limit_tip);
            return;
        }
        commit(issueid, shareuserid, sharereason);
    }

    private void commit(long issueid, long shareuserid, String sharereason) {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        HelpTask.recommend(issueid, shareuserid, sharereason).setCallback(this, mRecommendCallback).start();
    }

    private SimpleTask.Callback<Integer> mRecommendCallback = new SimpleTask.Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(R.string.me_submit_success);
            ShareUserInfo user = new ShareUserInfo();
            user.setAge(mUser.getAge());
            user.setCityname(mUser.getCityname());
            user.setGender(mUser.getGender());
            user.setImgmiddleurl(mUser.getImgmiddleurl());
            user.setImgminurl(mUser.getImgminurl());
            user.setBirthday(mUser.getBirthday());
            user.setUserid(mUser.getUserid());
            user.setNickname(mUser.getNickname());

            ShareInfo info = new ShareInfo();
            info.setIsselected(0);
            info.setRecommendtime(System.currentTimeMillis() / 1000);
            info.setSharereason(mContentEt.getText().toString().trim());
            info.setShareuserinfo(user);

            Intent intent = new Intent(ACTION.HELP_RECOMMEND_SUCCESS);
            intent.putExtra(EXTRA.ISSUE_ID, mHelp.getIssueid());
            intent.putExtra(EXTRA.SHARE_INFO, info);
            LocalBroadcastHelper.getInstance().send(intent);

            LocalBroadcastHelper.getInstance().send(new Intent(ACTION.HELP_TAKE_PART_IN_SUCCESS));
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    @Override
    public void onBackPressed() {
        if (mUser != null) {
            mTipDialog.show();
            return;
        }
        if (!TextUtils.isEmpty(mContentEt.getText().toString())) {
            mTipDialog.show();
            return;
        }
        super.onBackPressed();
    }

}
