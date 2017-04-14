package yahier.exst.act.ad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.FansListAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.item.ad.AdOrderBaseItem;
import com.stbl.stbl.item.ad.AdOrderItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;

import java.text.DecimalFormat;

/**
 * 支付完成后的订单详情
 * Created by Administrator on 2016/9/29 0029.
 */

public class AdMyOrderActivity extends ThemeActivity implements FinalHttpCallback{
    private TextView tvServiceType; //服务类型
    private TextView tvUsefulLife; //有效期
    private TextView tvPayType; //支付方式
    private TextView tvPayTime; //订购时间
    private TextView tvServicePrice; //服务价格

    private LinearLayout llPayMenRoot;//代付信息
    private TextView tvPayMen; //代付人
    private TextView tvHelpPayOrderno; //代付单号

    private LinearLayout llInvoiceRoot;//发票信息
    private TextView tvInvoiceTitle; //发票抬头
    private TextView tvInvoiceContent; //发票内容
    private TextView tvContactMen; //联系人
    private TextView tvContactAddress; //联系地址
    private TextView tvContactPhone; //服务类型

    private AdOrderItem orderDetail; //订单信息
    private final static int CREATE_INVOICE = 1110;//创建发票成功刷新
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_my_order_layout);
        setLabel(getString(R.string.ad_order_detail));
        initView();
        getOrderDetail();
    }

    private void initView(){
        tvServiceType = (TextView) findViewById(R.id.tv_service_type);
        tvUsefulLife = (TextView) findViewById(R.id.tv_useful_life);
        tvPayType = (TextView) findViewById(R.id.tv_pay_type);
        tvPayTime = (TextView) findViewById(R.id.tv_pay_time);
        tvServicePrice = (TextView) findViewById(R.id.tv_service_price);

        llPayMenRoot = (LinearLayout) findViewById(R.id.ll_pay_men_root);
        tvPayMen = (TextView) findViewById(R.id.tv_pay_men);
        tvHelpPayOrderno = (TextView) findViewById(R.id.tv_help_pay_orderno);

        llInvoiceRoot = (LinearLayout) findViewById(R.id.ll_invoice_root);
        tvInvoiceTitle = (TextView) findViewById(R.id.tv_invoice_title);
        tvInvoiceContent = (TextView) findViewById(R.id.tv_invoice_content);
        tvContactMen = (TextView) findViewById(R.id.tv_contact_men);
        tvContactAddress = (TextView) findViewById(R.id.tv_contact_address);
        tvContactPhone = (TextView) findViewById(R.id.tv_contact_phone);
    }

    private void showData(){
        if (orderDetail != null){
            if (orderDetail.getGoods() != null && orderDetail.getGoods().get(0) != null){
                AdGoodsItem good = orderDetail.getGoods().get(0);
                tvServiceType.setText(good.getGoodsname());
//                String usefulListTime = DateUtil.getYMD(String.valueOf(good.getServicetime()));
                String usefulLife = String.format(getString(R.string.ad_to_yyyy_mm_dd),good.getDeadline());
                tvUsefulLife.setText(usefulLife);
                tvServicePrice.setText("￥"+good.getPrice());
//                tvServicePrice.setText("￥"+new DecimalFormat("0.00").format(good.getPrice()));
            }
            if (orderDetail.getPaytype() == AdOrderBaseItem.TYPE_ALIPAY){
                tvPayType.setText(R.string.ad_alipay);
            }else if (orderDetail.getPaytype() == AdOrderBaseItem.TYPE_WECHATPAY){
                tvPayType.setText(R.string.ad_wechat_pay);
            }else if (orderDetail.getPaytype() == AdOrderBaseItem.TYPE_OTHER){
                tvPayType.setText(R.string.ad_daifu);
            }
            String paidtime = DateUtil.getYMD(String.valueOf(orderDetail.getPaidtime()));
            tvPayTime.setText(paidtime);

            if (orderDetail.getOtherpayinfo() != null ){
                llPayMenRoot.setVisibility(View.VISIBLE);
                tvPayMen.setText(orderDetail.getOtherpayinfo().getPaidusername());
                tvHelpPayOrderno.setText(orderDetail.getOtherpayinfo().getOtherpayno());
            }

            if (orderDetail.getOrderinvoice() != null){
                llInvoiceRoot.setVisibility(View.VISIBLE);
                tvInvoiceTitle.setText(orderDetail.getOrderinvoice().getOrderinvoicetitle());
                tvInvoiceContent.setText(orderDetail.getOrderinvoice().getOrderinvoicecontent());
                tvContactMen.setText(orderDetail.getOrderinvoice().getContactname());
                tvContactAddress.setText(orderDetail.getOrderinvoice().getContactaddr());
                tvContactPhone.setText(orderDetail.getOrderinvoice().getContactphone());
            }
            if (System.currentTimeMillis()/1000 < orderDetail.getPaidtime() + 30 * 24 * 60 * 60 && orderDetail.getOrderinvoice() == null){
                findViewById(R.id.ll_invoice_tips).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_create_invoice).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_create_invoice).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (orderDetail == null) return;
                        Intent intent = new Intent(AdMyOrderActivity.this,AdCreateInvoiceActivity.class);
                        intent.putExtra("orderno",orderDetail.getOrderno());
                        startActivityForResult(intent,CREATE_INVOICE);
                    }
                });
            }else{
                findViewById(R.id.ll_invoice_tips).setVisibility(View.GONE);
                findViewById(R.id.btn_create_invoice).setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == CREATE_INVOICE){
                getOrderDetail();
            }
        }
    }

    /**获取订单详情*/
    private void getOrderDetail(){
        WaitingDialog.show(this,true);
        Params params = new Params();
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
        switch (methodName) {
            case Method.adsysOrderDetailGet: //订单详情
                orderDetail = JSONHelper.getObject(json, AdOrderItem.class);
                showData();
                break;
        }
    }
}
