package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.AdDongtaiDetailAct;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.util.DimenUtils;

import java.text.DecimalFormat;

import io.rong.eventbus.EventBus;

/**
 * 广告模块公用结果页
 * Created by Administrator on 2016/9/24.
 */

public class CommonResultAct extends ThemeActivity {
    public final static int typeOpenAdService = 1;//开通了广告服务
    public final static int typeCommitAdApply = 2;//提交广告
    public final static int typeOtherPay = 3;//求助他人
    public final static int typeHelpTaPay = 4;//替ta支付
    public final static int typeApplyAdBusinessCoop = 5;//申请商务合作
    public final static int typeAdOrderVerify = 6;//广告订单支付确认中
    int type = 0;
    boolean isJumpToManager = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commom_result_act);

        TextView tvResult = (TextView) findViewById(R.id.tvResult);
        TextView tvTip = (TextView) findViewById(R.id.tvTip);
        TextView tvOperate = (TextView) findViewById(R.id.tvOperate);
        ImageView ivResultIcon = (ImageView) findViewById(R.id.iv_result_icon);

        type = getIntent().getIntExtra("type", 0);
        String name = getIntent().getStringExtra("name");
        isJumpToManager = getIntent().getBooleanExtra("isJumpToManager", false);
        float price = getIntent().getFloatExtra("price", 0f);
        switch(type) {
            case typeCommitAdApply:
                setLabel(getString(R.string.ad_upload_result));
                tvResult.setText(R.string.ad_uoload_success);
                tvTip.setText(R.string.ad_verify_result_toxiaomishu);
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeClosePublishAd));
                break;
            case typeOpenAdService:
                setLabel(getString(R.string.ad_pay_result));
                tvResult.setText(R.string.ad_pay_success);
                tvTip.setText(R.string.ad_open_ad_service_success);
                tvOperate.setVisibility(View.VISIBLE);
                //获取是否广告主
                AdTask.getIsAdvertiser(this);
                break;
            case typeOtherPay:
                setLabel(getString(R.string.ad_send_result));
                tvResult.setText(R.string.ad_help_success);
                tvTip.setText(String.format(getString(R.string.ad_help_pay_tips), name));
                tvOperate.setVisibility(View.VISIBLE);
//                tvOperate.setText(getString(R.string.queding));
                break;
            case typeHelpTaPay:
                setLabel(String.format(getString(R.string.ad_tade_order), name));
                tvResult.setText(R.string.ad_pay_success);
                tvTip.setText(getHelpTaPayString(name,price));
                tvOperate.setVisibility(View.VISIBLE);
                break;
            case typeApplyAdBusinessCoop:
                setLabel(getString(R.string.ad_business_coop));
                tvResult.setText(R.string.ad_business_coop_tip);
                tvTip.setText(R.string.ad_wait_contact);
                break;
            case typeAdOrderVerify:
                setLabel(getString(R.string.ad_pay_result));
                ivResultIcon.setImageResource(R.drawable.icon_feedback_wait);
                tvResult.setText(getString(R.string.ad_order_pay_verify));
                tvTip.setText(R.string.ad_wait_pay_result);
                tvOperate.setVisibility(View.VISIBLE);
                break;
        }
        tvOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        if (type != typeOtherPay && type != typeOpenAdService) {
            autoBack();
        }
    }

    /**获取支付确认中的提示语*/
    public SpannableString getHelpTaPayString(String name, float price){
        DecimalFormat df = new DecimalFormat("0.00");
        String dfPrice = df.format(price);
        String str = String.format(getString(R.string.ad_pay_success_tips), name, dfPrice);
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)),str.indexOf(name),str.indexOf(name)+name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(DimenUtils.sp2px(14)),str.indexOf(name),str.indexOf(name)+name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void next(){
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        switch(type) {
            case typeCommitAdApply:
                finish();
                break;
            case typeOpenAdService:
                Intent intent = new Intent(CommonResultAct.this, AdMyPrerogativeActivity.class);
                intent.putExtra("isJumpToManager", isJumpToManager);
                startActivity(intent);
                finish();
                break;
            case typeOtherPay:
                intent = new Intent(CommonResultAct.this, AdSeeHelpPayOrderActivity.class);
                startActivity(intent);
                finish();
                break;
            case typeHelpTaPay:
                finish();
                break;
            case typeAdOrderVerify:
                finish();
                break;
            case typeApplyAdBusinessCoop:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        next();
    }

    @Override
    public void finish() {
        super.finish();
    }

    //3s自动跳转
    final int timeToBack = 3;
    void autoBack() {
        handler = new ProgressHandler();
        handler.sendEmptyMessageDelayed(1,1000);
    }


    ProgressHandler handler;
    int seconds = 0;

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (seconds == timeToBack) {
//                finish();
                next();
                return;
            }
            seconds++;
            sendEmptyMessageDelayed(1, 1000);
        }
    }
}
