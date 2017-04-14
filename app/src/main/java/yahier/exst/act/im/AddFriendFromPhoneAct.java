package yahier.exst.act.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;

public class AddFriendFromPhoneAct extends ThemeActivity {

    private TextView mRewardTv;
    private TextView mInviteCodeTv;
    private String mInviteCode;
    private String mInviteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_add_contact_friend);

        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_i_want_to_accept_student));

        mRewardTv = (TextView) findViewById(R.id.tv_reward);
        String reward = getString(R.string.me_you_will_get_award);
        mRewardTv.setText(Html.fromHtml(reward));

        final UserItem userItem = SharedUser.getUserItem();
        mInviteCodeTv = (TextView) findViewById(R.id.tv_invite_code);
        mInviteCode = userItem.getInvitecode();
        String code = String.format(getString(R.string.me_copy_your_invite_code_s), mInviteCode);
        mInviteCodeTv.setText(Html.fromHtml(code));

        mInviteContent = getString(R.string.me_invite_you_join_in_stbl);
        if (!TextUtils.isEmpty(userItem.getNickname())){
            mInviteContent = String.format(mInviteContent,userItem.getNickname());
        }
//        mInviteContent = String.format(getString(R.string.me_message_invite_content), mInviteCode);

        findViewById(R.id.layout_sms_invite).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                String mobile = "";
//                Uri smsToUri = Uri.parse("smsto:" + mobile);
//                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//                intent.putExtra("sms_body", mInviteContent);
//                startActivity(intent);
                getLinkAndShare(String.valueOf(ShareDialog.sharedMiTribe),SharedToken.getUserId(AddFriendFromPhoneAct.this),null);
            }
        });
        findViewById(R.id.layout_share_to_friend).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareItem shareItem = new ShareItem();
                String content = getString(R.string.me_i_wait_for_you_at_stbl);
                String title = getString(R.string.me_invite_you_join_in_stbl);
                if (!TextUtils.isEmpty(userItem.getNickname())){
//                    content = getString(R.string.me_hi_i_am) + userItem.getNickname() + "，" + content;
                    title = String.format(title,userItem.getNickname());
                }
                shareItem.setTitle(title);
                shareItem.setContent(content);
                shareItem.setImgUrl(userItem.getImgmiddleurl());
                //new CommonShare().showShareWindow(AddFriendFromPhoneAct.this,
                //        String.valueOf(CommonShare.sharedMiTribe),
                //        String.valueOf(SharedToken.getUserId(AddFriendFromPhoneAct.this)), null, shareItem);
                new ShareDialog(AddFriendFromPhoneAct.this).shareInvite(Long.parseLong(SharedToken.getUserId(AddFriendFromPhoneAct.this)), shareItem);
            }
        });
        findViewById(R.id.layout_copy_invite_code).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                StringUtil.copyToClipboard(mInviteCode);
            }
        });
    }

    /**
     * 获取分享链接后分享
     */
    public void getLinkAndShare(String mi, final String bi, final String ex) {
        if (mTaskManager != null) {
            WaitingDialog.show(this,"");
            mTaskManager.start(CommonTask.getShareLink(mi, bi, null)
                    .setCallback(new HttpTaskCallback<String>(mActivity) {
                        @Override
                        public void onError(TaskError e) {
                            super.onError(e);
                            WaitingDialog.dismiss();
                            ToastUtil.showToast(e.msg);
                        }

                        @Override
                        public void onSuccess(String result) {
                            WaitingDialog.dismiss();
                            String mobile = "";
                            Uri smsToUri = Uri.parse("smsto:" + mobile);
                            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                            intent.putExtra("sms_body", mInviteContent + result + "[师徒部落]");
                            startActivity(intent);
                        }

                        @Override
                        public void onFinish() {
                            WaitingDialog.dismiss();
                        }
                    }));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
