package yahier.exst.act.dongtai.tribe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.InputAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.TribeUserItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

import io.rong.eventbus.EventBus;

/**
 * 判断是否关注，是否是好友，是否是徒弟，是否粉丝
 *
 * @author lenovo
 */
public class RelationSettingAct extends ThemeActivity implements FinalHttpCallback {
    TribeUserItem userItem;
    Relation relation;
    ImageView userImg;
    ImageView ivGender;
    TextView tvname, user_signature, tv_gender_age, tv_city;

    //final String TipFollowMe = "移除后,Ta不能看到你的动态";// 关注我的提示
    Button btnUnfollow, btnDelete;
    TextView tvTip;
    Context mContext;

    ImageView checkNotSee;
    ImageView checkNotBeSee;
    long userId;
    RelativeLayout rlEditAlias;//别名父控
    TextView tvAlias; //别名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relation_setting_act);

        userItem = (TribeUserItem) getIntent().getSerializableExtra("userItem");
        relation = (Relation) getIntent().getSerializableExtra("relation");
        if (userItem == null || relation == null) {
            return;
        }

        EventBus.getDefault().register(this);

        mContext = this;
        setLabel(userItem.getNickname());
        userId = userItem.getUserid();
        initViews();
        setValue(userItem);
        checkRelation();
        setCheckListener();
    }

    public void onEvent(EventUpdateAlias event){
        if (event != null){
            tvAlias.setText(event.getAlias());
            if (userItem != null)
                userItem.setAlias(event.getAlias());
        }
    }

    OnClickListener clickEditAlias = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("targetUserId",String.valueOf(userId));
            bundle.putString("alias",userItem.getAlias());
           enterActBundle(InputAct.class,bundle);
        }
    };
    OnClickListener clickDelete = new OnClickListener() {

        @Override
        public void onClick(final View view) {
            TipsDialog.popup(mContext, R.string.confirm_delete_friend, R.string.cancel, R.string.queding, new TipsDialog.OnTipsListener() {

                @Override
                public void onConfirm() {
                    view.setEnabled(false);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, Config.interClickTime);
                    deleteRelation();
                }

                @Override
                public void onCancel() {

                }
            });

        }
    };

    OnClickListener clickUnFollow = new OnClickListener() {

        @Override
        public void onClick(final View view) {
            TipsDialog.popup(mContext, R.string.confirm_unfollow, R.string.cancel, R.string.queding, new TipsDialog.OnTipsListener() {

                @Override
                public void onConfirm() {
                    view.setEnabled(false);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(true);
                        }
                    }, Config.interClickTime);
                    unFollow();
                }

                @Override
                public void onCancel() {

                }
            });

        }
    };


    void checkRelation() {
        int relationFlag = relation.getRelationflag();
        if (Relation.isFriend(relationFlag)) {
            btnDelete.setOnClickListener(clickDelete);
            //tvTip.setText("删除后,双方成为陌生人");
        } else {
            btnDelete.setVisibility(View.GONE);
            // tvTip.setVisibility(View.GONE);
            findViewById(R.id.divider_alias).setVisibility(View.GONE);
            rlEditAlias.setVisibility(View.GONE);
        }

        if (relation.getIsattention() == Statuses.isattention_yes) {
            btnUnfollow.setOnClickListener(clickUnFollow);
            // tvTip.setText("取消后,再也看不到Ta的动态");
        } else {
            btnUnfollow.setVisibility(View.GONE);
            // tvTip.setVisibility(View.GONE);
        }


        if (relation.getIsconceal() == Relation.isconceal_yes) {
            checkNotBeSee.setImageResource(R.drawable.icon_switch_on);
        } else {
            checkNotBeSee.setImageResource(R.drawable.icon_switch_off);
        }

        if (relation.getIsshield() == Relation.isshield_yes) {
            checkNotSee.setImageResource(R.drawable.icon_switch_on);
        } else {
            checkNotSee.setImageResource(R.drawable.icon_switch_off);
        }

        //没有关注ta。就不显示不看ta
        if (relation.getIsattention() == Relation.isattention_no) {
            findViewById(R.id.linNotSee).setVisibility(View.GONE);
        }

        // 如果没有关注我，就不能设置不让他看了
        if (relation.getIsfans() == Relation.isbeattention_no) {
            findViewById(R.id.linNotBeSee).setVisibility(View.GONE);
        }
    }


    public void unFollow() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.userCancelFollow, params, this);
    }

    void deleteRelation() {
        Params params = new Params();
        params.put("touserid", userId);
        params.put("breaktype", 2);
        new HttpEntity(this).commonPostData(Method.imDeleteRelation, params, this);
    }

    void Follow() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.userFollow, params, this);
    }

    // 屏蔽
    public void doShield() {
        String meId = SharedToken.getUserId();
        Params params = new Params();
        params.put("userid", meId);
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.userIgnore, params, this);
    }

    // 取消屏蔽
    public void doUnShield() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.userCancelgnore, params, this);
    }

    // 不让他看我的动态
    public void statusesNotSee() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.statusesNotSee, params, this);
    }

    // 让他看我的动态
    public void statusesYesSee() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.statusesYesSee, params, this);
    }

    void initViews() {
        checkNotSee = (ImageView) findViewById(R.id.checkNotSee);
        checkNotBeSee = (ImageView) findViewById(R.id.checkNotBeSee);
        btnUnfollow = (Button) findViewById(R.id.btnUnfollow);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        tvTip = (TextView) findViewById(R.id.tvTip);
        userImg = (ImageView) findViewById(R.id.user_img);
        tvname = (TextView) findViewById(R.id.name);
        tv_gender_age = (TextView) findViewById(R.id.user_gender_age);
        tv_city = (TextView) findViewById(R.id.user_city);
        rlEditAlias = (RelativeLayout) findViewById(R.id.rl_edit_alias);
        tvAlias = (TextView) findViewById(R.id.tv_alias);
        ivGender = (ImageView) findViewById(R.id.iv_gender);

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("relation", relation);
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void setCheckListener() {

        rlEditAlias.setOnClickListener(clickEditAlias);
        checkNotSee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                if (relation.getIsshield() == Relation.isshield_yes) {
                    doUnShield();
                } else {
                    doShield();
                }
            }
        });


        checkNotBeSee.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                if (relation.getIsconceal() == Relation.isconceal_yes) {
                    statusesYesSee();
                } else {
                    statusesNotSee();
                }
            }
        });

    }

    void setValue(UserItem user) {
        PicassoUtil.load(this, user.getImgmiddleurl(), userImg);
        tvname.setText(user.getNickname());
        tvAlias.setText(user.getAlias());
//        tv_city.setText(user.getCityname());
        if (TextUtils.isEmpty(user.getCityname())) {
            tv_city.setVisibility(View.GONE);
        } else {
            tv_city.setVisibility(View.VISIBLE);
            tv_city.setText(user.getCityname());
        }
//        tv_gender_age.setText(user.getAge() + "");
        if (user.getAge() == 0) {
            tv_gender_age.setVisibility(View.GONE);
        } else {
            tv_gender_age.setVisibility(View.VISIBLE);
            tv_gender_age.setText(String.format(getString(R.string.im_age), user.getAge()));
        }

        ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
            ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
            ivGender.setImageResource(R.drawable.icon_female);
        } else {
            ivGender.setVisibility(View.GONE);
        }
//        if (user.getGender() == UserItem.gender_boy) {
//            tv_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
//            tv_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
//        } else if (user.getGender() == UserItem.gender_girl) {
//            tv_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
//            tv_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
//        } else {
//            tv_gender_age.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
//            tv_gender_age.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());

            switch (methodName) {
                case Method.userIgnore:

                    break;

            }
            return;
        }

        switch (methodName) {
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeChangeRelation));
                relation.setIsattention(Relation.isattention_yes);
                break;
            case Method.userCancelFollow:
                ToastUtil.showToast(R.string.unfollow_success);
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeChangeRelation));
                btnUnfollow.setVisibility(View.GONE);
                relation.setIsattention(Relation.isattention_no);
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                relation.setIsshield(Relation.isshield_yes);
                break;
            case Method.userCancelgnore:
                ToastUtil.showToast(R.string.unshield_success);
                relation.setIsshield(Relation.isshield_no);
                break;
            case Method.imDeleteRelation:
                ToastUtil.showToast(R.string.delete_success);
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeChangeRelation));
                EventBus.getDefault().post(new IMEventType(IMEventType.typeChangedContact));
                relation.setRelationflag(relation.getRelationflag() - 2);
                btnDelete.setVisibility(View.GONE);
//              FriendsDB db = new FriendsDB(mContext);
//                FriendsDB db = FriendsDB.getInstance(mContext);
                //               db.delete(String.valueOf(userId));
                break;
            //让不让它看我
            case Method.statusesNotSee:
                ToastUtil.showToast(R.string.deal_success);
                relation.setIsconceal(Relation.isconceal_yes);
                break;
            case Method.statusesYesSee:
                ToastUtil.showToast(R.string.deal_success);
                relation.setIsconceal(Relation.isconceal_no);
                break;

        }
        checkRelation();

    }

}
