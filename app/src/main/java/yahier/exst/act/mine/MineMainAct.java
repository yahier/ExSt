package yahier.exst.act.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.help.GiveHelpActivity;
import com.stbl.stbl.act.home.mall.MallOrderAct;
import com.stbl.stbl.act.login.LoginActivity;
import com.stbl.stbl.act.login.RegisterStep1ActNew;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.ui.BaseClass.STBLActivity;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import io.rong.eventbus.EventBus;

public class MineMainAct extends STBLActivity implements OnClickListener {

    private static final String exit_action = "exit";

    private ScrollView mScrollView;
    private TextView mNickTv;
    private ImageView mHeadIv;
    private TextView mInviteCodeTv;

    private ImageView mTopLeftIv;

    private ExitReceiver exitReceiver;

    private ScrollView mNoLoginScrollView;
    private Button mRegisterBtn;
    private Button mLoginBtn;
    private View ivNewStudentPoint;
    private UserItem mUserItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_main);
        initView();
        getMyInfo();

        EventBus.getDefault().register(this);
        exitReceiver = new ExitReceiver();
        registerReceiver(exitReceiver, new IntentFilter(exit_action));
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.UPDATE_USER_INFO);
    }

    @Override
    protected void setStatusBar() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        judgeNewStudent();
    }

    void initView() {
        ivNewStudentPoint = findViewById(R.id.ivNewStudentPoint);
        ivNewStudentPoint.setOnClickListener(this);
        mNoLoginScrollView = (ScrollView) findViewById(R.id.scr_no_login);
        mRegisterBtn = (Button) findViewById(R.id.btn_register);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mRegisterBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mTopLeftIv = (ImageView) findViewById(R.id.top_left);
        mTopLeftIv.setOnClickListener(this);
        findViewById(R.id.top_right).setOnClickListener(this);

        mHeadIv = (ImageView) findViewById(R.id.iv_head);
        mHeadIv.setOnClickListener(this);
        mNickTv = (TextView) findViewById(R.id.tv_nick);
        mInviteCodeTv = (TextView) findViewById(R.id.tv_invite_code);
        mInviteCodeTv.setOnClickListener(this);
        mInviteCodeTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringUtil.copyToClipboard(mUserItem.getInvitecode());
                return false;
            }
        });

        findViewById(R.id.layout_my_connection).setOnClickListener(this);
        findViewById(R.id.layout_my_wallet).setOnClickListener(this);
        findViewById(R.id.layout_my_homepage).setOnClickListener(this);
        findViewById(R.id.layout_my_order).setOnClickListener(this);
        findViewById(R.id.layout_my_status).setOnClickListener(this);
        findViewById(R.id.layout_my_help).setOnClickListener(this);
        findViewById(R.id.layout_my_link).setOnClickListener(this);
        findViewById(R.id.layout_my_giftbox).setOnClickListener(this);
        findViewById(R.id.layout_my_album).setOnClickListener(this);
        findViewById(R.id.layout_my_collection).setOnClickListener(this);
        findViewById(R.id.layout_my_footprint).setOnClickListener(this);

        try {
            if (UserRole.isTemp(Integer.parseInt(SharedToken.getRoleFlag()))) {
                mScrollView.setVisibility(View.GONE);
                mNoLoginScrollView.setVisibility(View.VISIBLE);
                mTopLeftIv.setVisibility(View.GONE);
            } else {
                mNoLoginScrollView.setVisibility(View.GONE);
                mTopLeftIv.setVisibility(View.VISIBLE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    //判断是否有新徒弟
    void judgeNewStudent() {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AdHelper.isShowNewStudent()) {
                    ivNewStudentPoint.setVisibility(View.VISIBLE);
                } else {
                    ivNewStudentPoint.setVisibility(View.GONE);
                }
            }
        }, 1000);
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_register: //注册
                startActivity(new Intent(this, RegisterStep1ActNew.class));
                break;
            case R.id.btn_login: //登录
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.top_left: //我的二维码
                enterAct(MyQrcodeActivity.class);
                break;
            case R.id.top_right: //设置
                enterAct(SettingMainAct.class);
                break;
            case R.id.iv_head: { //个人资料
                Intent i = new Intent(this, UserInfoMain.class);
                i.putExtra(EXTRA.USER_ITEM, mUserItem);
                startActivity(i);
            }
            break;
            case R.id.tv_invite_code:
                //StringUtil.copyToClipboard(mUserItem.getInvitecode());
                break;
            case R.id.layout_my_connection: //我的人脉
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDRM);
                enterAct(MinePeopleResourceAct.class);
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AdHelper.setShowNewStudent(false);
                    }
                }, 200);
                break;
            case R.id.layout_my_wallet: //我的钱包
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDQB);
                enterAct(MineWalletAct.class);
                break;
            case R.id.layout_my_homepage: //个人部落
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDZY);
                intent = new Intent(this, TribeMainAct.class);
                intent.putExtra("userId", Long.valueOf(SharedToken.getUserId()));
                startActivity(intent);
                break;
            case R.id.layout_my_order://查看全部订单
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDDD);
                enterOrderAct(0);
                break;
            case R.id.layout_my_status: //我的动态
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDDT);
                enterAct(MyDongtaiActivity.class);
                break;
            case R.id.layout_my_help: //帮一帮
                MobclickAgent.onEvent(this, UmengClickEventHelper.BYB);
                enterAct(GiveHelpActivity.class);
                break;
            case R.id.layout_my_link: //精彩链接
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDJCLJ);
                enterAct(MyLinkActivity.class);
                break;
            case R.id.layout_my_giftbox: //我的礼盒
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDLH);
                enterAct(MyGiftActivity.class);
                break;
            case R.id.layout_my_album: //我的相册
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDXC);
                enterAct(MyAlbumActivity.class);
                break;
            case R.id.layout_my_collection: //我的收藏
                MobclickAgent.onEvent(this, UmengClickEventHelper.WDSC);
                intent = new Intent(this, MyCollectionActivity.class);
                intent.putExtra("mode", MyCollectionActivity.mode_look);
                startActivity(intent);
                break;
            case R.id.layout_my_footprint: //访问足迹
                enterAct(VisitTrackActivity.class);
                break;

        }
    }

    void enterOrderAct(int index) {
        Intent intent = new Intent(this, MallOrderAct.class);
        intent.putExtra("index", index);
        startActivity(intent);
    }

    public void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    private void setUserView() {
        mNickTv.setText(mUserItem.getNickname());
        ImageUtils.loadCircleHead(mUserItem.getImgurl(), mHeadIv);
        if (!TextUtils.isEmpty(mUserItem.getInvitecode())) {
            mInviteCodeTv.setText(Html.fromHtml(String.format(getString(R.string.me_invite_code_s), mUserItem.getInvitecode())));
            mInviteCodeTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TabHome home = (TabHome) getParent();
        return home.onKeyDown(keyCode, event);
    }

    private void getMyInfo() {
        if (UserRole.isTemp(Integer.parseInt(SharedToken.getRoleFlag()))) {
            return;
        }
        mTaskManager.start(UserTask.getMyInfo()
                .setCallback(new HttpTaskCallback<UserItem>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(UserItem result) {
                        mUserItem = result;
                        SharedUser.putUserValue(result);
                        setUserView();
                    }
                }));
    }

    public void onEvent(EventTypeCommon type) {
        switch (type.getType()) {
            case EventTypeCommon.typeRefreshMine:
                mScrollView.smoothScrollTo(0, 0);
                getMyInfo();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(exitReceiver);
        EventBus.getDefault().unregister(this);
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    class ExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent.getAction().equals(exit_action)) {
                finish();
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION.UPDATE_USER_INFO:
                    getMyInfo();
                    break;
                case ACTION.SHOW_NEW_STUDENT_REMIND:
                    judgeNewStudent();
                    break;
            }
        }
    };

}
