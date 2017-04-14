package yahier.exst.act.mine;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cunoraz.tagview.Utils;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.adapter.mine.SignWeekAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.SignAward;
import com.stbl.stbl.item.SignListResult;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ShareUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.WebDialogUtil;
import com.stbl.stbl.util.WebDialogUtil2;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by yahier on 16/11/9.
 * 签到页面
 */

public class SignAct extends BaseActivity implements View.OnClickListener, FinalHttpCallback {
    GridView grid;
    SignWeekAdapter adapter;
    private long mUserId;
    TextView tvSignDayTip, tvSignAwardTip, tvSign;
    TextView tvYM;
    int offSize;
    ObjectAnimator animatorImgBgRotate;
    View linText, linImg;
    ImageView imgSign, linImgBg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_act);
        mUserId = getIntent().getLongExtra(EXTRA.USER_ID, 0);
        initView();
        getSignList();
        calculate();
    }

    private boolean isSelfEnter() {
        if (mUserId == Long.valueOf(SharedToken.getUserId(this))) {
            return true;
        }
        return false;
    }


    void initView() {
        imgSign = (ImageView) findViewById(R.id.imgSign);
        linImgBg = (ImageView) findViewById(R.id.linImgBg);
        linText = findViewById(R.id.linText);
        linImg = findViewById(R.id.linImg);
        linImg.setOnClickListener(this);
        tvSign = (TextView) findViewById(R.id.tvSign);
        tvSign.setOnClickListener(this);
        tvSignDayTip = (TextView) findViewById(R.id.tvSignDayTip);
        tvSignAwardTip = (TextView) findViewById(R.id.tvSignAwardTip);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.imgQ).setOnClickListener(this);

        tvYM = (TextView) findViewById(R.id.tvYM);

        LogUtil.logE("date offSize", offSize);
        grid = (GridView) findViewById(R.id.grid);
        adapter = new SignWeekAdapter(this);
        grid.setAdapter(adapter);
    }


    void calculate() {
        View linSign = findViewById(R.id.linSign);
        int paddind = Device.getWidth() * 50 / 1261;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linSign.getLayoutParams();
        params.setMargins(paddind, 0, paddind, Utils.dipToPx(this, 15));
        linSign.setLayoutParams(params);

    }

    public void getSignList() {
        Params params = new Params();
        params.put("target_userid", mUserId);
        new HttpEntity(this).commonPostData(Method.getSignDataV1, params, this);
    }

    public void signIn() {
        Params params = new Params();
        params.put("target_userid", mUserId);
        new HttpEntity(this).commonPostData(Method.userSignInV1, params, this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgQ:
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.sign_instrod, "");
                new WebDialogUtil2().setUrl(this, url);
                break;
            case R.id.tvSign:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.QDZN);
                signIn();
                break;
            case R.id.linImg:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.QDZN);
                signIn();
                break;
        }
    }

    void showLighBgAnima() {
        linImgBg.setVisibility(View.VISIBLE);
        animatorImgBgRotate = ObjectAnimator.ofFloat(linImgBg, "rotation", 0, 360).setDuration(5000);
        animatorImgBgRotate.setRepeatCount(ObjectAnimator.INFINITE);
        animatorImgBgRotate.setInterpolator(new LinearInterpolator());
        animatorImgBgRotate.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animatorImgBgRotate != null) animatorImgBgRotate.end();
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String con = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getSignDataV1:
                SignListResult signData = JSONHelper.getObject(con, SignListResult.class);
                showSignData(signData, 1);
                break;
            case Method.userSignInV1:
                SignListResult st = JSONHelper.getObject(con, SignListResult.class);
                if (st == null) return;
                if (isSelfEnter()) {
                    ToastUtil.showToast(st.getAwardmsg());
                } else {
                    ToastUtil.showToast("签到成功");
                }
                showSignData(st, 2);
                break;


        }

    }

    /**
     * 1是展示。2是签到时候的展示
     *
     * @param signData
     * @param type
     */
    void showSignData(SignListResult signData, int type) {
        long nowTimeStamp = signData.getNowtimestamp();
        tvYM.setText(DateUtil.getYM(nowTimeStamp));
        int dateOfToday = DateUtil.getDayOfMonth(nowTimeStamp * 1000);
        int dayOff = DateUtil.getFirstDayWeekOfMonth(nowTimeStamp * 1000);
        adapter.setDateData(dayOff, dateOfToday);
        int curNode = signData.getCursignnode();
        boolean isSigned = false;
        switch (curNode) {
            case SignListResult.stateSigned:
                isSigned = true;
                tvSign.setText("已签到");
                linText.setVisibility(View.VISIBLE);
                linImg.setVisibility(View.GONE);
                break;
            case SignListResult.stateBoxOpened:
                isSigned = true;
                if (isSelfEnter()) {
                    linText.setVisibility(View.GONE);
                    imgSign.setImageResource(R.drawable.icon_qiandao_dabaoxiang_open);
                    linImg.setVisibility(View.VISIBLE);
                    showLighBgAnima();
                } else {
                    tvSign.setText("已签到");
                    linText.setVisibility(View.VISIBLE);
                    linImg.setVisibility(View.GONE);
                }

                break;
            case SignListResult.stateBoxToOpen:
                if (isSelfEnter()) {
                    linText.setVisibility(View.GONE);
                    linImg.setVisibility(View.VISIBLE);
                } else {
                    tvSign.setText("签到");
                    linText.setVisibility(View.VISIBLE);
                    linImg.setVisibility(View.GONE);
                }
                break;
            default:
                tvSign.setText("签到");
                linText.setVisibility(View.VISIBLE);
                linImg.setVisibility(View.GONE);
                break;
        }

        if (isSigned) {
            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeSignIn, mUserId));
        }

        tvSignAwardTip.setText(signData.getNextawardmsg());
        tvSignDayTip.setText("已签到" + signData.getSigndays() + "天");
        List<Integer> listSign = signData.getSignnode();
        if (listSign != null && listSign.size() > 0) {
            adapter.setData(listSign);
            ViewUtils.setAdapterViewHeightNoMargin(grid, 7);
        }
    }

}
