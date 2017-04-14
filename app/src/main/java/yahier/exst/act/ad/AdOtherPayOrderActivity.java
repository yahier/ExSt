package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.item.ad.AdUserItem;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.RoundImageView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 查看别人的广告求助订单；
 * 自己查看没有替他支付按钮
 * Created by Administrator on 2016/9/28 0028.
 */

public class AdOtherPayOrderActivity extends ThemeActivity implements FinalHttpCallback{
    private RoundImageView rivUserIcon;//广告主头像
    private TextView tvPrice; //商品价格
    private TextView tvStatus; //订单状态
    private TextView tvName; //申请人昵称
    private TextView tvServiceType; //服务类型
    private TextView tvMessage; //留言
    private TextView tvHelpTaPay; //替他支付

    private String orderno = ""; //订单id
    private AdOrderItem orderDetail; //订单信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_other_pay_order_layout);
        setRightImage(R.drawable.share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderDetail == null) return;
                //分享
                ShareItem shareItem = new ShareItem();
                if (orderDetail.getUser() != null) {
                    shareItem.setTitle(getString(R.string.ad_share_title));
                    shareItem.setContent(String.format(getString(R.string.ad_share_tips), orderDetail.getUser().getNickname()));
                    if (TextUtils.isEmpty(orderDetail.getUser().getImgurl()) || orderDetail.getUser().getIsdefaultimgurl() == AdUserItem.defaultImg){
                        shareItem.setDefaultIcon(R.drawable.icon_adotherpay_share);
                    }else {
                        shareItem.setImgUrl(orderDetail.getUser().getImgurl());
                    }
                }
                new ShareDialog(AdOtherPayOrderActivity.this).shareAdOtherPay(orderDetail.getOrderno(), shareItem);
            }
        });

        orderno = getIntent().getStringExtra("orderno");

        rivUserIcon = (RoundImageView) findViewById(R.id.riv_user_icon);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvServiceType = (TextView) findViewById(R.id.tv_service_type);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvHelpTaPay = (TextView) findViewById(R.id.tv_help_ta_pay);
        getOrderDetail();
        tvHelpTaPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderDetail != null){
                    Intent intent = new Intent(AdOtherPayOrderActivity.this,AdHelpTaPayActivity.class);
                    intent.putExtra("orderno",orderDetail.getOrderno());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void showData(){
        if (orderDetail != null){
            if (orderDetail.getUser() != null){
                AdUserItem user = orderDetail.getUser();
                if (!String.valueOf(user.getUserid()).equals(SharedToken.getUserId()) &&
                        orderDetail.getOrderstate() == AdOrderBaseItem.WaitPay){
                    tvHelpTaPay.setVisibility(View.VISIBLE);
                }
                PicassoUtil.load(this,user.getImgurl(),rivUserIcon,R.drawable.icon_shifu_default);
                tvName.setText(user.getNickname());
                setLabel(String.format(getString(R.string.ad_ta_other_pay_order),user.getNickname()));
            }
            if (orderDetail.getGoods() != null && orderDetail.getGoods().get(0) != null) {
//                DecimalFormat df = new DecimalFormat("0.00");
//                String price = df.format(orderDetail.getGoods().get(0).getPrice());
                tvPrice.setText(orderDetail.getGoods().get(0).getPrice());
                tvServiceType.setText(orderDetail.getGoods().get(0).getGoodsname());
            }
            if (orderDetail.getOtherpayinfo() != null) {
                tvMessage.setText(orderDetail.getOtherpayinfo().getMessage());
            }
            CharSequence statusTips = "";
            if (orderDetail.getRestpaytime() <= 0){
                statusTips = getString(R.string.ad_other_pay_status_tips1);
            }else{
                long hour = orderDetail.getRestpaytime()/60/60;
                long minutes = (orderDetail.getRestpaytime() - hour*60*60)/60;
                String tips1 = getString(R.string.ad_order_pay_time);
                String tips2 = String.format(getString(R.string.ad_hour_minute),hour,minutes);
                String tips3 = tips1 + tips2;

                SpannableString ss = new SpannableString(tips3);
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),tips3.indexOf(tips2),tips3.indexOf(tips2) + tips2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                statusTips = ss;
//                statusTips = String.format(getString(R.string.ad_other_pay_status_tips2),hour,minutes);
                if (hour <= 0) {
                    String tips4 = String.format(getString(R.string.ad_hour_minute),hour,minutes);
                    String tips5 = tips1 + tips4;

                    SpannableString ss2 = new SpannableString(tips5);
                    ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),tips5.indexOf(tips4),tips3.indexOf(tips4) + tips4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    statusTips = ss2;
//                    statusTips = String.format(getString(R.string.ad_other_pay_status_tips2_2), minutes);
                }
            }
            if (orderDetail.getOrderstate() == AdOrderBaseItem.HASPAY){
                statusTips = getString(R.string.ad_other_pay_status_tips3);
            }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYCANCEL){
                statusTips = getString(R.string.ad_other_pay_status_tips4);
            }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYVERIFY){
                statusTips = getString(R.string.ad_pay_verify);
            }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT){
                statusTips = getString(R.string.ad_order_timeout);
            }
            tvStatus.setText(statusTips);
        }
    }
    /**获取订单详情*/
    private void getOrderDetail(){
        WaitingDialog.show(this,true);
        Params params = new Params();
        params.put("orderno",orderno);
        new HttpEntity(this).commonPostData(Method.adsysOrderDetailGet,params,this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag){
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null){
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;
        }
        String json = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName){
            case Method.adsysOrderDetailGet: //订单详情
                orderDetail = JSONHelper.getObject(json,AdOrderItem.class);
                showData();
                break;
        }
    }
}
