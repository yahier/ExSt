package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.HashMap;

/**
 * 好友分类
 *
 */
@Deprecated
public class FriendClassAct extends ThemeActivity {

    private RoundImageView mHeadIv;
    private TextView mNameTv;
    private TextView mAgeTv;
    private TextView mLocationTv;

    private RelativeLayout mMasterLayout;
    private RelativeLayout mStudentLayout;
    private RelativeLayout mFriendLayout;

    private TextView mStudentTv;
    private TextView mFriendTv;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_class_act);
        setLabel(getString(R.string.im_friend_classify));
        initView();
        getFriendCategory();
    }

    void initView() {
        mHeadIv = (RoundImageView) findViewById(R.id.iv_head);
        mNameTv = (TextView) findViewById(R.id.tv_name);
        mAgeTv = (TextView) findViewById(R.id.tv_age);
        mLocationTv = (TextView) findViewById(R.id.tv_location);

        mMasterLayout = (RelativeLayout) findViewById(R.id.layout_master);
        mStudentLayout = (RelativeLayout) findViewById(R.id.layout_student);
        mFriendLayout = (RelativeLayout) findViewById(R.id.layout_friend);

        mStudentTv = (TextView) findViewById(R.id.tv_student);
        mFriendTv = (TextView) findViewById(R.id.tv_friend);

        mLoadingDialog = new LoadingDialog(this);

        mStudentLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendClassAct.this, CommonContactsActivity.class);
                intent.putExtra(CommonContactsActivity.EXTRA_RELATION_TYPE, Relation.relation_type_student);
                intent.putExtra(CommonContactsActivity.EXTRA_RELATION_NAME, getString(R.string.im_my_prentice));
                startActivity(intent);
            }
        });

        mFriendLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendClassAct.this, CommonContactsActivity.class);
                intent.putExtra(CommonContactsActivity.EXTRA_RELATION_TYPE, Relation.relation_type_friend);
                intent.putExtra(CommonContactsActivity.EXTRA_RELATION_NAME, getString(R.string.im_my_friend));
                startActivity(intent);
            }
        });
    }

    private void setView(final UserItem user, int tudicount, int friendcount) {
        if (user != null) {
            mMasterLayout.setVisibility(View.VISIBLE);
            ImageUtils.loadCircleHead(user.getImgmiddleurl(), mHeadIv);
            mNameTv.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//            mNameTv.setText(user.getNickname());
            mAgeTv.setText(user.getAge() + "");
            mLocationTv.setText(user.getCityname());
            if (user.getGender() == UserItem.gender_boy) {
                mAgeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
                mAgeTv.setBackgroundResource(R.drawable.shape_blue_corner32);
            } else if (user.getGender() == UserItem.gender_girl){
                mAgeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
                mAgeTv.setBackgroundResource(R.drawable.shape_red_corner32);
            }else{
                mAgeTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mAgeTv.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
            }
            mMasterLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendClassAct.this, TribeMainAct.class);
                    intent.putExtra("userId", user.getUserid());
                    startActivity(intent);
                }
            });
        }
        mStudentLayout.setVisibility(View.VISIBLE);
        mFriendLayout.setVisibility(View.VISIBLE);
        mStudentTv.setText(getString(R.string.im_my_prentice)+"(" + tudicount + ")");
        mFriendTv.setText(getString(R.string.im_my_friend)+"(" + friendcount + ")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void getFriendCategory() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        ContactsTask.getFriendCategory().setCallback(this, mGetFriendCategoryCallback).start();
    }

    private SimpleTask.Callback<HashMap<String, Object>> mGetFriendCategoryCallback = new SimpleTask.Callback<HashMap<String, Object>>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(HashMap<String, Object> result) {
            mLoadingDialog.dismiss();
            UserItem item = null;
            if (result.containsKey("masterview")) {
                item = (UserItem) result.get("masterview");
            }
            int tudicount = (int) result.get("tudicount");
            int friendcount = (int) result.get("friendcount");
            setView(item, tudicount, friendcount);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
}
