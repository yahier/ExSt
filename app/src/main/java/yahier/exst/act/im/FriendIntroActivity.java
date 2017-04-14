package yahier.exst.act.im;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.mine.MineDongtai;
import com.stbl.stbl.act.mine.OtherAlbumActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.model.TinyTribeInfo;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imlib.model.Conversation;

/**
 * 个人信息过渡页
 * Created by lr on 2016/8/26.
 */

public class FriendIntroActivity extends BaseActivity {

    private static final int REQUEST_CODE_MORE_SETTING = 100;

    private TextView mAliasTv;
    private Button mSendMsgBtn;

    private UserItem mUser;
    private TinyTribeInfo mTinyTribeInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = (UserItem) getIntent().getSerializableExtra(KEY.USER_ITEM);
        if (mUser == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_friend_intro);
        EventBus.getDefault().register(this);

        initView();
        getTinyTribeInfo();
    }

    private void initView() {
        RoundImageView headIv = (RoundImageView) findViewById(R.id.iv_head);
        ImageUtils.loadHead(mUser.getImgmiddleurl(), headIv);

        final RelativeLayout headLayout = (RelativeLayout) findViewById(R.id.layout_head);
        final int height = Device.getWidth() * 3 / 4;
        headLayout.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        height);
                headLayout.setLayoutParams(params);
                ImageView headBgIv = (ImageView) findViewById(R.id.iv_head_bg);
                ImageUtils.fastBlur(mUser.getImgmiddleurl(), headBgIv);
            }
        });

        TextView nickTv = (TextView) findViewById(R.id.tv_nick);
        nickTv.setText(mUser.getNickname());

        mSendMsgBtn = (Button) findViewById(R.id.btn_send_msg);

        findViewById(R.id.iv_back).setOnClickListener(mClickListener);

        mAliasTv = (TextView) findViewById(R.id.tv_alias);
    }

    private void getTinyTribeInfo() {
        mTaskManager.start(UserTask.getTinyTribeInfo(mUser.getUserid())
                .setCallback(new HttpTaskCallback<TinyTribeInfo>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(TinyTribeInfo result) {
                        mTinyTribeInfo = result;
                        setView();
                    }
                }));
    }

    private void setView() {
        findViewById(R.id.layout_remark).setOnClickListener(mClickListener);
        findViewById(R.id.layout_more).setOnClickListener(mClickListener);
        findViewById(R.id.btn_view_tribe).setOnClickListener(mClickListener);
        mSendMsgBtn.setOnClickListener(mClickListener);

        UserItem user = mTinyTribeInfo.user;
        mAliasTv.setText(user.getAlias());

        int margin = DimenUtils.dp2px(10);
        LinearLayout albumLayout = (LinearLayout) findViewById(R.id.layout_album_container);
        int width = (albumLayout.getWidth() - DimenUtils.dp2px(8) - margin * 4) / 4;
        int maxWidth = DimenUtils.dp2px(70);
        if (width > maxWidth) {
            width = maxWidth;
        }

        List<Photo> photoList = mTinyTribeInfo.userphotoview;
        if (photoList != null && photoList.size() > 0) {
            findViewById(R.id.tv_album_empty).setVisibility(View.GONE);
            int size = Math.min(photoList.size(), 4);
            for (int i = 0; i < size; i++) {
                Photo photo = photoList.get(i);
                ImageView iv = new ImageView(mActivity);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
                params.setMargins(0, 0, margin, 0);
                albumLayout.addView(iv, params);
                ImageUtils.loadIcon(photo.getMiddleurl(), iv);
            }
            findViewById(R.id.layout_album).setOnClickListener(mClickListener);
        }

        Statuses statuses = mTinyTribeInfo.lateststatusesview;
        if (statuses != null) {
            findViewById(R.id.tv_status_empty).setVisibility(View.GONE);
            findViewById(R.id.layout_status).setOnClickListener(mClickListener);

            ImageView iv = (ImageView) findViewById(R.id.iv_status_image);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
            params.width = width;
            params.height = width;
            iv.setLayoutParams(params);
            if (TextUtils.isEmpty(statuses.getStatusespic().getDefaultpic())) {
                ImageUtils.loadIcon(mUser.getImgmiddleurl(), iv);
            } else {
                String url = statuses.getStatusespic().getMiddlepic() + statuses.getStatusespic().getDefaultpic();
                ImageUtils.loadIcon(url, iv);
            }
            TextView titleTv = (TextView) findViewById(R.id.tv_status_title);
//            TextView contentTv = (TextView) findViewById(R.id.tv_status_content);
            EmojiconTextView contentTv = (EmojiconTextView) findViewById(R.id.tv_status_content);

            String nameValue = mUser.getAlias();
            if (nameValue == null || nameValue.equals("")) {
                nameValue = mUser.getNickname();
            }
            titleTv.setText(nameValue + getString(R.string.statuses_by_someone));
            if (TextUtils.isEmpty(statuses.getTitle())) {
                contentTv.setText(statuses.getContent());
            } else {
                contentTv.setText(statuses.getTitle());
            }
        } else {
            findViewById(R.id.divider_status).setVisibility(View.GONE);
            findViewById(R.id.layout_status).setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.iv_back:
                    onBackPressed();
                    break;
                case R.id.layout_more: {
                    Intent intent = new Intent(mActivity, FriendMoreSettingActivity.class);
                    intent.putExtra(KEY.USER_ID, mUser.getUserid());
                    intent.putExtra(KEY.RELATION, mTinyTribeInfo.relation);
                    startActivityForResult(intent, REQUEST_CODE_MORE_SETTING);
                }
                break;
                case R.id.layout_remark: {
                    Intent intent = new Intent(mActivity, InputAct.class);
                    intent.putExtra("targetUserId", mUser.getUserid() + "");
                    intent.putExtra("alias", mTinyTribeInfo.user.getAlias());
                    startActivity(intent);
                }
                break;
                case R.id.btn_view_tribe: {
                    Intent intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", mUser.getUserid());
                    startActivity(intent);
                }
                break;
                case R.id.btn_send_msg:
                    sendMsgOrAddFriend();
                    break;
                case R.id.layout_album: {
                    Intent intent = new Intent(mActivity, OtherAlbumActivity.class);
                    intent.putExtra(EXTRA.USER_ITEM, mUser);
                    startActivity(intent);
                }
                break;
                case R.id.layout_status: {
                    Intent intent = new Intent(mActivity, MineDongtai.class);
                    intent.putExtra("userItem", mUser);
                    startActivity(intent);
                }
                break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(IMEventType type) {
        switch(type.getType()) {
            case IMEventType.typeUpdateContactList:
            case IMEventType.typeChangedContact:
                getTinyTribeInfo();
                break;
        }
    }

    public void onEvent(EventUpdateAlias event) {
        if (event != null && event.getUserid() == mUser.getUserid() && event.getAlias() != null) {
            String alias = event.getAlias();
            mAliasTv.setText(alias);
            mUser.setAlias(alias);
            mTinyTribeInfo.user.setAlias(alias);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_MORE_SETTING) {
                Relation relation = (Relation) data.getSerializableExtra(KEY.RELATION);
                if (relation != null) {
                    mTinyTribeInfo.relation = relation;
                    onRelationChange();
                }
            }
        }
    }

    private void onRelationChange() {
        Relation relation = mTinyTribeInfo.relation;
        int relationFlag = relation.getRelationflag();
        if (Relation.isFriend(relationFlag)) {
            mSendMsgBtn.setText(R.string.send_message);
        } else {
            mSendMsgBtn.setText(R.string.plus_friend);
            mAliasTv.setText("");
            findViewById(R.id.iv_alias_arrow).setVisibility(View.GONE);
            findViewById(R.id.layout_remark).setClickable(false);
        }
    }

    private void sendMsgOrAddFriend() {
        if (Relation.isFriend(mTinyTribeInfo.relation.getRelationflag())) {
            ThemeActivity.isMerchant(mUser.getUserid());
            String nickName = mUser.getNickname();
            String alias = mUser.getAlias();
            if (alias != null && !alias.equals("")) {
                nickName = alias;
            }
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                    .appendQueryParameter("targetId", String.valueOf(mUser.getUserid())).appendQueryParameter("title", nickName).build();
            startActivity(new Intent("android.intent.action.VIEW", uri));
        } else {
            new AddFriendUtil(mActivity, new FinalHttpCallback() {
                @Override
                public void parse(String methodName, String result) {
                    switch(methodName) {
                        case Method.imAddFriendDirect:
                            mSendMsgBtn.setText(R.string.send_message);
                            mTinyTribeInfo.relation.setRelationflag(Relation.relation_type_friend);
                            findViewById(R.id.iv_alias_arrow).setVisibility(View.VISIBLE);
                            findViewById(R.id.layout_remark).setClickable(true);
                            break;
                    }
                }
            }).addFriendDirect(mUser);
        }
    }

}
