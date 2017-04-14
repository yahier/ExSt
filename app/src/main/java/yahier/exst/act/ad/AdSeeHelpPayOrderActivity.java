package yahier.exst.act.ad;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

import java.text.DecimalFormat;
import java.util.List;

import io.rong.eventbus.EventBus;


/**
 * 查看我申请的代付订单
 * Created by Administrator on 2016/9/29 0029.
 */

public class AdSeeHelpPayOrderActivity extends ThemeActivity implements FinalHttpCallback{
    private TextView tvRestpaytime;//剩余时间
    private TextView tvServiceName;//服务名称
    private TextView tvServicePrice;//服务价格
    private TextView tvOrderStatus;//订单状态
    private TextView tvResetOrder;//订单状态
    private View item1;//时间

    private String orderno = ""; //订单id
    private AdOrderItem orderDetail; //订单信息
//    private boolean isToBack = true;//是否去到广告介绍页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_see_help_pay_order_layout);
        tvRestpaytime = (TextView) findViewById(R.id.tv_restpaytime);
        tvServiceName = (TextView) findViewById(R.id.tv_service_name);
        tvServicePrice = (TextView) findViewById(R.id.tv_service_price);
        tvOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        tvResetOrder = (TextView) findViewById(R.id.tv_reset_order);
        item1 = findViewById(R.id.item1);
        setLabel(getString(R.string.ad_order_detail));
        tvResetOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTipsDialog();
            }
        });
        orderno = getIntent().getStringExtra("orderno");
        EventBus.getDefault().register(this);
        getOrderDetail();
    }

    public void onEvent(EventTypeCommon type){
        if (type != null && type.getType() == EventTypeCommon.typeCloseHelpOrder){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
//        if (!isToBack) return;
//        Intent intent = new Intent(this,IntroduceAdAct.class);
//        startActivity(intent);
    }

    private void showTipsDialog(){
        if (orderDetail != null && (orderDetail.getOrderstate() == AdOrderBaseItem.PAYCANCEL || orderDetail.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT)){
            Intent intent = new Intent(AdSeeHelpPayOrderActivity.this,PurchaseAdAualifyAct.class);
            startActivity(intent);
//            finish();
            return;
        }
        TipsDialog2.popup(this, getString(R.string.ad_reset_order_tips), getString(R.string.ad_cancel),
                getString(R.string.queding), new TipsDialog2.OnTipsListener() {
                    @Override
                    public void onConfirm() {
                        String orderNo = orderDetail != null ? orderDetail.getOrderno() : "";
                        //取消订单
                        if (!TextUtils.isEmpty(orderNo))
                            cancelOrder(orderNo);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).setCancelable(false);
    }

    private void showData(){
        String statusTips = "";
        long hour = orderDetail.getRestpaytime()/60/60;
        long minutes = (orderDetail.getRestpaytime() - hour*60*60)/60;
        statusTips = String.format(getString(R.string.ad_hour_minute),hour,minutes);
        if (hour <= 0) {
            statusTips = String.format(getString(R.string.ad_minute), minutes);
        }
        tvRestpaytime.setText(statusTips);
        String status = "";
        if (orderDetail.getOrderstate() == AdOrderBaseItem.HASPAY){
            status = getString(R.string.ad_haspay);
        }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYCANCEL ||
                orderDetail.getOrderstate() == AdOrderBaseItem.PAYTIMEOUT){
            status = getString(R.string.ad_order_is_pastdue2);
        }else if(orderDetail.getOrderstate() == AdOrderBaseItem.PAYVERIFY){
            status = getString(R.string.ad_order_pay_verify);
        }else if(orderDetail.getOrderstate() == AdOrderBaseItem.WaitPay){
            status = getString(R.string.ad_wait_pay);
            item1.setVisibility(View.VISIBLE);
        }
        tvOrderStatus.setText(status);
        List<AdGoodsItem> goods = orderDetail.getGoods();
        if (goods != null && goods.get(0) != null){
            AdGoodsItem goodsItem = goods.get(0);
            tvServiceName.setText(goodsItem.getGoodsname());
//            DecimalFormat df = new DecimalFormat("0.00");
//            try {
//                String price = df.format(goodsItem.getTotalamount());
            tvServicePrice.setText("￥"+goodsItem.getTotalamount());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }

    //取消订单
    private void cancelOrder(String orderNo){
        WaitingDialog.show(this,false);
        Params params = new Params();
        params.put("orderno",orderNo);
        new HttpEntity(this).commonPostData(Method.adsysOrderCancel,params,this);
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
            case Method.adsysOrderCancel: //取消订单
                Intent intent = new Intent(this,PurchaseAdAualifyAct.class);
                startActivity(intent);
//                isToBack = false;
//                finish();
                break;
        }
    }
}
