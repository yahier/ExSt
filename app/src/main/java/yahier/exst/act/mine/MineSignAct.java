package yahier.exst.act.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.SignAward;
import com.stbl.stbl.item.SignListResultOld;
import com.stbl.stbl.item.SignResult;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
@Deprecated
public class MineSignAct extends ThemeActivity implements FinalHttpCallback, OnClickListener {
    TextView signText;
    TextView nextDateText, nextAwardText;

    LinearLayout signlin;
    List<ImageView> listImage = new ArrayList<ImageView>(31);
    private long mUserId;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_sign_act);
        mUserId = getIntent().getLongExtra(EXTRA.USER_ID, 0);
        if (mUserId == 0) {
            finish();
            return;
        }
        setLabel(getString(R.string.me_sign_in_free_get_award));
        mContext = this;
        initViews();

        adjuestSignBg();
        getSignList();
    }

    /**
     * 判断是否自己进入
     *
     * @return
     */
    private boolean isSelfEnter() {
        if (mUserId == Long.valueOf(SharedToken.getUserId(this))) {
            return true;
        }
        return false;
    }

    // 调整签到结果背景
    void adjuestSignBg() {
        Bitmap bitmap = BitmapUtil.decodeBitmapSafely(getResources(), R.drawable.sign_table_bg);
        if (bitmap == null) {
            return;
        }
        int viewWidth = Device.getWidth(this);
        int viewHeight = viewWidth * bitmap.getHeight() / bitmap.getWidth();
        signlin.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, viewHeight));
    }

    void initViews() {
        signlin = (LinearLayout) findViewById(R.id.sign_lin);
        signText = (TextView) findViewById(R.id.sign_btn);
        nextDateText = (TextView) findViewById(R.id.nextDateText);
        // nextDateText.setText(DateUtil.getNextDay());
        nextAwardText = (TextView) findViewById(R.id.nextAwardText);

        signText.setOnClickListener(this);

        int childLinCount = signlin.getChildCount();

        for (int i = 0; i < childLinCount; i++) {
            ViewGroup group = (ViewGroup) signlin.getChildAt(i);
            int childCount = group.getChildCount();
            //从左到右
            if (i % 2 == 0) {
                for (int j = 0; j < childCount; j++) {
                    listImage.add((ImageView) group.getChildAt(j));
                }//从右到左
            } else {
                for (int j = childCount - 1; j >= 0; j--) {
                    listImage.add((ImageView) group.getChildAt(j));
                }
            }

        }

        findViewById(R.id.tv_sign_rule).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.sign_instrod, "");
                Intent intent = new Intent(MineSignAct.this, CommonWeb.class);
                intent.putExtra("url", url);
                intent.putExtra("title", getString(R.string.me_sign_in_rule));
                startActivity(intent);
            }
        });
    }

    public void signIn() {
        Params params = new Params();
        params.put("target_userid", mUserId);
        new HttpEntity(this).commonPostData(Method.userSignin, params, this);
    }

    public void getSignList() {
        Params params = new Params();
        params.put("target_userid", mUserId);
        new HttpEntity(this).commonPostData(Method.userSignList, params, this);
    }

    // 开启宝箱
    public void signOpenBox() {
        Params params = new Params();
        // params.put("target_userid", app.getUserId());
        new HttpEntity(this).commonPostData(Method.userSignOpenBox, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        String con = JSONHelper.getStringFromObject(item.getResult());
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }

        switch (methodName) {
            case Method.userSignin:
                SignResult st = JSONHelper.getObject(con, SignResult.class);
                if (isSelfEnter()) {
                    ToastUtil.showToast(st.getAwardmsg());
                } else {
                    ToastUtil.showToast("签到成功");
                }
                getSignList();
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeSignIn, mUserId));
                break;
            case Method.userSignList:
                SignListResultOld listResult = JSONHelper.getObject(con, SignListResultOld.class);
                if (listResult == null) return;
                nextDateText.setText(String.format(getString(R.string.me_s_already_continue_sign_d_day), DateUtil.getNextDay(), listResult.getSigndays()));
                if (listResult.getSignin() == SignListResultOld.issignYes) {
                    signText.setText(R.string.me_already_sign_in);
                    nextAwardText.setText(listResult.getNextawardmsg());
                } else {
                    nextAwardText.setText(listResult.getNextawardmsg());
                }
                List<SignAward> signdays_award = listResult.getSignnode();
                if (signdays_award != null && signdays_award.size() > 0) {
                    showListResult(signdays_award);
                }
                ArrayList<UserItem> listUser = (ArrayList<UserItem>) listResult.getProxyusers();
                if (listUser != null && listUser.size() > 0) {
                    findViewById(R.id.tvSignTip).setVisibility(View.GONE);
                    findViewById(R.id.iv_arrow).setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_help_sign).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, SignHelpMeAct.class);
                            intent.putExtra(EXTRA.USER_ID, mUserId);
                            mContext.startActivity(intent);
                        }
                    });
                    addHelpSignUser(listUser);
                    Intent intent = new Intent(ACTION.UPDATE_SIGN_IN_LIST);
                    intent.putExtra(EXTRA.USER_ID, mUserId);
                    intent.putExtra(EXTRA.USER_ITEM_LIST, listUser);
                    LocalBroadcastHelper.getInstance().send(intent);
                }
                break;
            case Method.userSignOpenBox:
                SignResult boxResult = JSONHelper.getObject(con, SignResult.class);
                TipsDialog.popup(mContext, boxResult.getAwardmsg(), getString(R.string.me_confirm), new TipsDialog.OnTipsListener() {

                    @Override
                    public void onConfirm() {
                        getSignList();
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub

                    }
                });
                break;
        }

    }

    void showListResult(List<SignAward> signdays_award) {
        if (signdays_award == null) {
            return;
        }
        int count = signdays_award.size();
        for (int i = 0; i < count; i++) {
            listImage.get(i).setVisibility(View.VISIBLE);
            final SignAward award = signdays_award.get(i);
            switch (award.getType()) {
                case SignAward.TypeNotSign:
                    listImage.get(i).setVisibility(View.INVISIBLE);
                    break;
                case SignAward.TypeScore:
                    listImage.get(i).setImageResource(R.drawable.icon_sign_score);
                    break;
                case SignAward.TypeGreenBean:
                    listImage.get(i).setImageResource(R.drawable.icon_buy_lvdou);
                    break;
                case SignAward.TypeBoxOpenable:
                    listImage.get(i).setImageResource(R.drawable.icon_baoxiang_openable);
                    // TipsDialog.popup(this, msg, btnOk, listener)
                    break;
                case SignAward.TypeBoxOpened:
                    listImage.get(i).setImageResource(R.drawable.icon_baoxiang_opened);
                    break;
                case SignAward.TypeBoxCloseed:
                    listImage.get(i).setImageResource(R.drawable.icon_baoxiang_closeed);
                    break;
                case SignAward.TypeBoxExpire:
                    listImage.get(i).setImageResource(R.drawable.icon_baoxiang_expire);
                    break;
                default:
                    break;
            }

            listImage.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    switch (award.getType()) {
                        case SignAward.TypeNotSign:
                            break;
                        case SignAward.TypeGoldBean:
                            break;
                        case SignAward.TypeGreenBean:
                            break;
                        case SignAward.TypeBoxOpenable:
                            // 只准可以打开宝箱
                            if (!isSelfEnter()) {
                                return;
                            }
                            TipsDialog.popup(mContext, getString(R.string.me_open_treasure_box), getString(R.string.me_confirm), new TipsDialog.OnTipsListener() {

                                @Override
                                public void onConfirm() {
                                    // ToastUtil.showToast(mContext, "增加调用打开方法");
                                    signOpenBox();
                                }

                                @Override
                                public void onCancel() {
                                    // TODO Auto-generated method stub

                                }
                            });
                            break;
                        case SignAward.TypeBoxOpened:
                            // listImage.get(i).setImageResource(R.drawable.icon_baoxiang_opened);

                            break;
                        case SignAward.TypeBoxCloseed:
                            // listImage.get(i).setImageResource(R.drawable.icon_baoxiang_closeed);
                            break;
                        case SignAward.TypeBoxExpire:
                            // listImage.get(i).setImageResource(R.drawable.icon_baoxiang_expire);
                            TipsDialog.popup(mContext, getString(R.string.me_treasure_box_already_overdue), getString(R.string.me_confirm));
                            break;
                        default:
                            break;
                    }

                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_btn:
                signIn();
                break;
        }
    }

    private void addHelpSignUser(ArrayList<UserItem> listUser) {
        LinearLayout container = (LinearLayout) findViewById(R.id.layout_help_sign_user_group);
        container.removeAllViews();
        int imgSize = DimenUtils.dp2px(40);
        int padding = DimenUtils.dp2px(8);
        int width = container.getWidth();
        int maxUserSize = width / (imgSize + padding);
        int size = Math.min(maxUserSize, listUser.size());
        for (int i = 0; i < size; i++) {
            RoundImageView imageView = new RoundImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgSize, imgSize);
            params.setMargins(0, 0, padding, 0);
            ImageUtils.loadCircleHead(listUser.get(i).getImgurl(), imageView, imgSize);
            container.addView(imageView, params);
        }
    }
}
