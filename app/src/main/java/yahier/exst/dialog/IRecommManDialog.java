package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.ShareInfo;
import com.stbl.stbl.model.bangyibang.ShareUserInfo;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

public class IRecommManDialog extends Dialog {

    private TextView mTimeTv;

    private RoundImageView mHeadIv;
    private TextView mNickTv;
    private TextView mAgeTv;
    private TextView mLocationTv;
    private TextView mContentTv;

    private TextView mStateTv;

    private ImageView mTagIv;

    private IIRecommManDialog mInterface;

    private long mUserId;

    public IRecommManDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_i_recomm_man);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = Device.getWidth() - DimenUtils.dp2px(48);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        initView();
    }

    private void initView() {
        mTimeTv = (TextView) findViewById(R.id.tv_time);
        mHeadIv = (RoundImageView) findViewById(R.id.iv_head);
        mNickTv = (TextView) findViewById(R.id.tv_nick);
        mAgeTv = (TextView) findViewById(R.id.tv_age);
        mLocationTv = (TextView) findViewById(R.id.tv_location);
        mContentTv = (TextView) findViewById(R.id.tv_content);
        mStateTv = (TextView) findViewById(R.id.tv_state);
        mTagIv = (ImageView) findViewById(R.id.iv_tag);

        findViewById(R.id.btn_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

        mHeadIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onClickHead(mUserId);
                }
            }
        });
    }

    public void setData(BangYiBangItem item) {
        if (item == null) {
            return;
        }
        ShareInfo info = item.getShareinfo();
        ShareUserInfo userInfo = info.getShareuserinfo();
        if (userInfo == null) {
            return;
        }
        mUserId = userInfo.getUserid();
        mTimeTv.setText(DateUtil.getTimeOff(info.getRecommendtime()));
        ImageUtils.loadCircleHead(userInfo.getImgmiddleurl(), mHeadIv);
        mNickTv.setText(userInfo.getNickname());
        mAgeTv.setText(userInfo.getAge() + "");
        if (userInfo.getGender() == 0) {
            mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.dongtai_gender_boy, 0, 0, 0);
            mAgeTv.setBackgroundResource(R.drawable.shape_blue_corner32);
        } else {
            mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.dongtai_gender_girl, 0, 0, 0);
            mAgeTv.setBackgroundResource(R.drawable.shape_red_corner32);
        }
        mLocationTv.setText(userInfo.getCityname());
        mContentTv.setText(info.getSharereason());

        mStateTv.setVisibility(View.INVISIBLE);
        mTagIv.setVisibility(View.INVISIBLE);

        int issuestate = item.getIssuestate();
        int userintype = item.getUserintype();

        if (issuestate == 0) { // 激活（未关闭 未完成）
            mStateTv.setVisibility(View.VISIBLE);
        } else {
            mTagIv.setVisibility(View.VISIBLE);
            if (userintype == 20) { // 未采纳
                mTagIv.setImageResource(R.drawable.ic_unadopt_tag);
            } else if (userintype == 21) { // 已采纳
                mTagIv.setImageResource(R.drawable.ic_adopted_tag);
            }
        }
    }

    public void setInterface(IIRecommManDialog i) {
        mInterface = i;
    }

    public interface IIRecommManDialog {
        void onClickHead(long userId);
    }

}
