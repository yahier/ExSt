package yahier.exst.act.home.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.model.OrderProduct;
import com.stbl.stbl.model.OrderState;
import com.stbl.stbl.model.SellerOrderInfo;
import com.stbl.stbl.model.express.ExpressInfo;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LoadingView;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.utils.UISkipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;

/**
 * 订单详情
 *
 * @author ruilin
 */
public class OrderDetailAct extends ThemeActivity implements OnClickListener, OnFinalHttpCallback {
    public final static String INTENT_ORDER_INFO = "INTENT_ORDER_INFO";
    public final static String INTENT_CLASS_1 = "INTENT_CLASS_1";
    public final static String INTENT_CLASS_2 = "INTENT_CLASS_2";

    protected OrderDetailAct mAct;
    protected SimpleGoodsAdapter mAdapter;
    protected ArrayList<OrderProduct> mDatas;
    protected ScrollView mScrollView;
    protected ListView mListView;
    protected AlertDialog mDialog;

    protected View mView_wuliu;
    protected Button mView_notagree;
    protected Button mView_agree;
    protected Button mView_refund;//申请退款
    protected View mView_tuihuo;
    protected View mView_shuoming;
    protected View mView_bottombar;
    protected View mView_fapiao;

    protected TextView tv_state;
    protected TextView tv_state2;

    protected View mView_cancelReason; //取消原因or退款原因or关闭原因父控
    protected TextView tvReasonType; //取消原因or退款原因or关闭原因
    protected TextView tvReason; //取消原因or退款原因or关闭原因  内容

    protected ProgressDialog loadingDialog;

    private int mTotalquerytype = 1;    //查询类型(进行中 = 1, 已完成 = 2, 已关闭 = 3)
    private int mQuerytype = 1;            //查询类型(待付款 = 1,待发货 = 2,待收货 = 3,售后 = 4)

    // Data
    protected SellerOrderInfo mInfo;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_order_detail);
        setLabel(getString(R.string.mall_order_detail));
        mAct = this;
        mDatas = new ArrayList<>();

        mScrollView = (ScrollView) findViewById(R.id.scrollView1);
        mListView = (ListView) findViewById(R.id.lv_goods);
        mAdapter = new SimpleGoodsAdapter(this, mDatas,false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UISkipUtils.startMallGoodsSnapshotAct(mAct, mDatas.get(position).getGoodsid());
            }
        });

        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_state2 = (TextView) findViewById(R.id.tv_state2);
        (mView_wuliu = findViewById(R.id.btn_wuliu)).setOnClickListener(this);
        (mView_notagree = (Button) findViewById(R.id.btn_notagree)).setOnClickListener(this);
        (mView_agree = (Button) findViewById(R.id.btn_agree)).setOnClickListener(this);
        (mView_refund = (Button) findViewById(R.id.btn_refund)).setOnClickListener(this);
        mView_tuihuo = findViewById(R.id.block_tuihuo);
        mView_shuoming = findViewById(R.id.block_shuoming);
        mView_bottombar = findViewById(R.id.bottombar);
        mView_fapiao = findViewById(R.id.block_fapiao);
        findViewById(R.id.cope_order_btn).setOnClickListener(this);
        mView_cancelReason = findViewById(R.id.rl_reason);
        tvReasonType = (TextView) findViewById(R.id.tv_reason_type);
        tvReason = (TextView) findViewById(R.id.tv_reason_content);

        mView_bottombar.setVisibility(View.GONE);

        Intent it = getIntent();
        mTotalquerytype = it.getIntExtra(INTENT_CLASS_1, 0);
        mQuerytype = it.getIntExtra(INTENT_CLASS_2, 0);

        mInfo = (SellerOrderInfo) it.getSerializableExtra(INTENT_ORDER_INFO);
        if (null == mInfo) {
            orderId = it.getLongExtra("orderid", 0);
            if (orderId != 0) {
                requireData(orderId);
            }
            return;
        } else {
            fill(mInfo);
        }
    }

    public void setStatus(String title, String tips) {
        tv_state.setText(title);
        tv_state2.setText(tips);
    }

    public ProgressDialog getLoadingDialog() {
        if (null == loadingDialog) {
            loadingDialog = LoadingView.createDefLoading(this);
        }
        return loadingDialog;
    }

    public void fill(SellerOrderInfo info) {
        TextView tv = (TextView) findViewById(R.id.tv_express);
        if (null != info.expresslastinfo) {
            tv.setText(getString(R.string.mall_logistics_process) + info.expresslastinfo.getContext());
            tv = (TextView) findViewById(R.id.tv_riqi);
            tv.setText(info.expresslastinfo.getFtime());
        } else {
            tv.setText(R.string.mall_logistics_process);
            tv = (TextView) findViewById(R.id.tv_riqi);
            tv.setText("");
        }
        tv = (TextView) findViewById(R.id.textView3);
        tv.setText(String.valueOf(info.orderid));
        // consignee
        tv = (TextView) findViewById(R.id.tv_shouhuoren);
        tv.setText(info.addressinfo.getUsername());

        tv = (TextView) findViewById(R.id.tv_shouji);
        tv.setText(info.addressinfo.getPhone());
        // address
        tv = (TextView) findViewById(R.id.tv_dizhi);
        tv.setText(info.addressinfo.getCountryname()+" "
                +info.addressinfo.getProvincename() + " "
                + info.addressinfo.getCityname() + " "
                + info.addressinfo.getDistrictname() + " "
                + info.addressinfo.getAddress());

        TextView tvLeaveMsg = (TextView) findViewById(R.id.leave_msg_tv);
        if(!StringUtils.isEmpty(info.userremark)) {
            tvLeaveMsg.setVisibility(View.VISIBLE);
            tvLeaveMsg.setText(getString(R.string.goods_remark)+ info.userremark);
        }else{
            tvLeaveMsg.setVisibility(View.GONE);
        }

        // produces
        if (null != info.productsList && info.productsList.size() > 0) {
            mDatas.addAll(info.productsList);
            updateListView();
        }
        // invoice
        TextView tv_fapiao = (TextView) findViewById(R.id.tv_fapiao);
        tv = (TextView) findViewById(R.id.tv_gongsi);
        if (null != info.invoicetitle) {
            tv_fapiao.setText(R.string.mall_commercial_sales_invoice);
            tv.setText(info.invoicetitle);
        } else {
            tv_fapiao.setText(R.string.mall_no);
            tv.setText("--");
        }
        // price
        tv = (TextView) findViewById(R.id.tv_goodsprice);
        tv.setText("¥" + info.totalamount);
        tv = (TextView) findViewById(R.id.tv_reduce);
        tv.setText("-¥" + (info.totalamount - info.realpayamount));
        tv = (TextView) findViewById(R.id.tv_pay);
        tv.setText("¥" + info.realpayamount);

        // time
        if (null == info.refundinfo ) {
            tv = (TextView) findViewById(R.id.tv_ordertime);
            tv.setText(info.createtime != 0 ? DateUtil.getTime(String.valueOf(info.createtime),"yyyy-MM-dd HH:mm:ss") : "--");
            tv = (TextView) findViewById(R.id.tv_paytime);
            tv.setText(info.paytime != 0 ? DateUtil.getTime(String.valueOf(info.paytime), "yyyy-MM-dd HH:mm:ss") : "--");
            tv = (TextView) findViewById(R.id.tv_sendtime);
            tv.setText(info.delivertime != 0 ? DateUtil.getTime(String.valueOf(info.delivertime), "yyyy-MM-dd HH:mm:ss") : "--");

            //
//            tv = (TextView) findViewById(R.id.tv_ordertime);
//            tv.setText(info.createtime != 0 ? DateUtil.getTimeValue(info.createtime) : "--");
//            tv = (TextView) findViewById(R.id.tv_paytime);
//            tv.setText(info.paytime != 0 ? DateUtil.getTimeValue(info.paytime) : "--");
//            tv = (TextView) findViewById(R.id.tv_sendtime);
//            tv.setText(info.delivertime != 0 ? DateUtil.getTimeValue(info.delivertime) : "--");
        } else if(info.refundinfo.reasonname != null){
            // return
            mView_tuihuo.setVisibility(View.VISIBLE);
            if (info.orderstate == OrderState.RETURN_PAY_APPLY){
                TextView tvRText = (TextView) findViewById(R.id.tv_return_text);
                tvRText.setText(R.string.mall_refund_apply_content);
                TextView tvReturn = (TextView) findViewById(R.id.tv_return);
                tvReturn.setText(R.string.mall_refund_type);
                TextView tvRemark = (TextView) findViewById(R.id.tv_remark);
                tvRemark.setText(R.string.mall_refund_explain);
            }
            final SellerOrderInfo.Refundinfo refund = info.refundinfo;
            tv = (TextView) findViewById(R.id.tv_return_reason);
            tv.setText(refund.reasonname);
            tv = (TextView) findViewById(R.id.tv_return_remark);
            tv.setText(refund.description);
            GridView gv_return = (GridView) findViewById(R.id.gv_return_img);
            //		ArrayList<String> urlList = new ArrayList<>();
            //		for (int i = 0; i < 4; i++) {
            //			urlList.add("http://www.diadany.com/img/aHR0cDovL3BpYzE4Lm5pcGljLmNvbS8yMDExMTIxMy85MDEyODAwXzE5MjE0MDAyNzMzOF8yLmpwZw==.jpg");
            //		}
            if (null != refund.phonearr && refund.phonearr.size() > 0) { //退货图片
                gv_return.setAdapter(new GridAdapter(this, refund.phonearr));

                gv_return.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(OrderDetailAct.this, GalleryActivity.class);
                        intent.putExtra("position", "1");
                        intent.putExtra("ID", arg2);
                        intent.putExtra("urls", refund.phonearr);
                        startActivity(intent);

                    }
                });
            }else{
                findViewById(R.id.ll_imgs).setVisibility(View.GONE);
            }

            findViewById(R.id.fahuoshijian).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tv_xiadanshijian)).setText(R.string.mall_apply_time);
            ((TextView) findViewById(R.id.tv_fukuanshijian)).setText(R.string.mall_return_goods_time);
            tv = (TextView) findViewById(R.id.tv_ordertime);
            LogUtil.logE("refund.applytime:"+refund.applytime);
            tv.setText(!refund.applytime.equals("0") ? DateUtil.getTimeValue(refund.applytime) : "--");
            tv = (TextView) findViewById(R.id.tv_paytime);
            tv.setText(!refund.expresstime.equals("0") ? DateUtil.getTimeValue(refund.expresstime) : "--");
        }

        setPhoto(info);
        LogUtil.logE("!!! order state: " + info.orderstate);

        fillState(info);
    }

    protected void setPhoto(final SellerOrderInfo info) {
        if (null == info.buyerinfoview) return;

        findViewById(R.id.tv_user).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                RongIM.getInstance().startPrivateChat(mAct, String.valueOf(info.buyerinfoview.getUserid()),
                        info.buyerinfoview.getNickname());
            }
        });
    }

    protected void fillState(SellerOrderInfo info) {
        setStatus(info.orderstatename, info.orderstatedescribe);
        switch (info.orderstate) {
            case OrderState.WAIT_FOR_PAY:
//                setStatus("待付款", "等待买家完成付款");
                mView_wuliu.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                break;
            case OrderState.WAIT_FOR_SEND:
//                setStatus("待发货", "买家已付款，请发货给买家");
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                setBottomButton(null, "发货");
                break;
            case OrderState.WAIT_FOR_RECEIVE:
//                setStatus("待收货", "等待买家收货");
                mView_wuliu.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                break;
            case OrderState.FINISH:
//                setStatus("交易成功", "买家已收货，交易成功");
                mView_wuliu.setVisibility(View.VISIBLE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_APPLY:
//                setStatus("退货待确认", "买家申请了退货");
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                setBottomButton("不同意", "同意退货");
                break;
            case OrderState.RETURN_WAIT_FOR_SEND:
//                setStatus("退货中", "等待买家回邮商品");
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_notagree.setVisibility(View.GONE);
                mView_agree.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_FINISH:
//                setStatus("退货成功", "退货成功，关闭订单");
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_PAY_APPLY:
//                setStatus("退款待确认", "买家申请退款");
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                setBottomButton("不同意", "同意退款");
                break;
            case OrderState.RETURN_DISAGREE_AMOUNT:
//                setStatus("拒绝退款", "已拒绝买家的退款申请");
                mView_wuliu.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.PAYED_CONFIRM:
//                setStatus("支付待确认", "买家已付款，等待系统确认");
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.CLOSE:
//                setStatus("交易关闭", "交易完成，系统关闭订单");
                mView_wuliu.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.FAIL:
//                setStatus("交易失败", "交易失败，订单关闭");
                mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
            case OrderState.RETURN_WAITING:
//                setStatus("退货待收货", "买家已经发货，请耐心等待");
                mView_wuliu.setVisibility(View.VISIBLE);
                mView_tuihuo.setVisibility(View.VISIBLE);
                setBottomButton(null, "确认收货");
                break;
            default:
//                setStatus("未知状态", "" + info.orderstate);
//			mView_wuliu.setVisibility(View.GONE);
                mView_fapiao.setVisibility(View.GONE);
                mView_tuihuo.setVisibility(View.GONE);
                mView_bottombar.setVisibility(View.GONE);
                break;
        }
    }

    protected void setBottomButton(String leftButton, String rightButton) {
        mView_bottombar.setVisibility(View.VISIBLE);
        if (leftButton != null) {
            mView_notagree.setVisibility(View.VISIBLE);
            mView_notagree.setText(leftButton);
        } else {
            mView_notagree.setVisibility(View.GONE);
        }
        if (rightButton != null) {
            mView_agree.setVisibility(View.VISIBLE);
            mView_agree.setText(rightButton);
        } else {
            mView_agree.setVisibility(View.GONE);
        }
    }

    private void updateListView() {
        mAdapter.notifyDataSetChanged();
        ViewUtils.setListViewHeightBasedOnChildren(mListView);
    }

    class GridAdapter extends RCommonAdapter<String> {
        public GridAdapter(Activity act, List<String> mDatas) {
            super(act, mDatas, R.layout.seller_order_detail_image);
        }

        @Override
        public void convert(RViewHolder helper, String item) {
            helper.setImageByUrl(R.id.iv_img, item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onClick(View v) {

        Log.e("OrderDetailAct", "-------------- onClick :"+(v.getId() == R.id.btn_notagree)+"------------------");
        switch (v.getId()) {
            case R.id.btn_wuliu:
                requireExpress(mInfo.expressno,mInfo.orderid);
                break;
            case R.id.cope_order_btn:
                if (mInfo != null && mInfo.orderid > 0) {
                    ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    StringBuffer sb = new StringBuffer();
                    sb.append(getString(R.string.buyer_1)).append(mInfo.buyerinfoview != null ? mInfo.buyerinfoview.getNickname() : "").append("\n");
                    sb.append(getString(R.string.order_no)).append(mInfo.orderid).append("\n");
                    sb.append(getString(R.string.order_create_time)).append(mInfo.createtime != 0 ? DateUtil.getTime(String.valueOf(mInfo.createtime),"yyyy-MM-dd HH:mm:ss") : "--").append("\n");
                    sb.append(getString(R.string.consignee_info));
                    if (mInfo.addressinfo != null){
                        sb.append(mInfo.addressinfo.getUsername() != null ? mInfo.addressinfo.getUsername() : "").append("   ").
                                append(mInfo.addressinfo.getPhone()).append("   ").
                                append(mInfo.addressinfo.getProvincename()).
                                append(mInfo.addressinfo.getCityname()).
                                append(mInfo.addressinfo.getDistrictname()).
                                append(mInfo.addressinfo.getAddress()).append("\n");
                    }
                    sb.append(getString(R.string.goods_info));
                    if (null != mInfo.productsList && mInfo.productsList.size() > 0) {
                        for (int i=0; i<mInfo.productsList.size(); i++){
                            OrderProduct op = (OrderProduct) mInfo.productsList.get(i);
                            if (op != null)
                                sb.append(op.getGoodsname()).append("  ").append(op.getPrice()).append("x").append(op.getCount()).append("\n");
                        }
                    }
                    sb.append(getString(R.string.goods_money)).append("¥" + StringUtil.get2ScaleString(mInfo.totalamount)).append("\n");
                    sb.append(getString(R.string.goods_remark)).append(mInfo.userremark != null ? mInfo.userremark : "");
                    ClipData myClip = ClipData.newPlainText("text", sb.toString());
                    myClipboard.setPrimaryClip(myClip);
                    ToastUtil.showToast(getString(R.string.copy_success));
                } else {
                    ToastUtil.showToast(getString(R.string.copy_order_info_err));
                }
                return;
        }

        onClickByState(v);
    }

    protected void onClickByState(View v) {
        Intent it;
        switch (mInfo.orderstate) {
            case OrderState.RETURN_APPLY:
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 不同意
                        sendOperate(Method.orderDisagreeReturn);
                        break;
                    case R.id.btn_agree:    // 同意退货
                        showReturnOption();
                        break;
                }
                break;
            case OrderState.RETURN_PAY_APPLY:
                switch (v.getId()) {
                    case R.id.btn_notagree:    // 不同意
                        sendDisagreeReturnAmount();
                        break;
                    case R.id.btn_agree:    // 同意退款
                        TipsDialog.popup(this, "确定退款给买家吗？", "取消", "确定", new OnTipsListener() {
                            @Override
                            public void onConfirm() {
                                sendOperate(Method.orderAgreeReturnAmount);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        break;
                }
                break;
            case OrderState.WAIT_FOR_SEND:
                switch (v.getId()) {
                    case R.id.btn_agree:    // 发货
                        it = new Intent(this, SellerExpressAct.class);
                        startActivityForResult(it, 0);
                        break;
                }
                break;
            case OrderState.RETURN_WAITING:
                switch (v.getId()) {
                    case R.id.btn_agree:    // 确认收到退货
                        TipsDialog.popup(this, "亲，确认收到货了吗？", "取消", "确认", new OnTipsListener() {
                            @Override
                            public void onConfirm() {
                                sendOperate(Method.sellerGotReturnGoods);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    String expressNum = data.getStringExtra(SellerExpressAct.KEY_NUM);
                    int companyIndex = data.getIntExtra(SellerExpressAct.KEY_NAME, -1);
                    sendDeliver(companyIndex, expressNum);
                }
                break;
        }
    }

    /**
     * 弹出退货选项
     */
    public void showReturnOption() {
        if (null == mDialog) {
            mDialog = new AlertDialog.Builder(this).create();
            mDialog.setCancelable(true);
            mDialog.show();
            mDialog.getWindow().setContentView(R.layout.seller_pop_return);
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            final RadioButton radio0 = (RadioButton) mDialog.getWindow().findViewById(R.id.radio0);
            final RadioButton radio1 = (RadioButton) mDialog.getWindow().findViewById(R.id.radio1);
            OnClickListener onClick = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 确定
                    switch (v.getId()) {
                        case R.id.btn_normal:
                            radio0.setChecked(true);
                            radio1.setChecked(false);
                            break;
                        case R.id.btn_fast:
                            radio0.setChecked(false);
                            radio1.setChecked(true);
                            break;
                        case R.id.button1:
                            if (radio0.isChecked()) {
                                sendReturn(1);
                            } else {
                                sendReturn(2);
                            }
                            mDialog.dismiss();
                            break;
                    }
                }
            };
            mDialog.getWindow().findViewById(R.id.btn_normal).setOnClickListener(onClick);
            mDialog.getWindow().findViewById(R.id.btn_fast).setOnClickListener(onClick);
            mDialog.getWindow().findViewById(R.id.button1).setOnClickListener(onClick);
            RadioGroup radio = (RadioGroup) mDialog.getWindow().findViewById(R.id.radioGroup1);
            radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                }
            });
        } else if (!mDialog.isShowing() && !isFinishing()) {
            mDialog.show();
        }
    }

    public void sendOperate(String method) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", mInfo.orderid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null).postJson(method, json.toString(), this);
    }

    /**
     * 发送退货请求
     *
     * @param type
     */
    public void sendReturn(int type) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", mInfo.orderid);
            json.put("refundtype", type);    // 退货类型 1-普通退货 2-快速退货
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null).postJson(Method.orderAgreeReturn, json.toString(), this);
    }

    /*
     * 拒绝退款
     */
    public void sendDisagreeReturnAmount() {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", mInfo.orderid);
            json.put("reason", "no reason");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null).postJson(Method.orderDisagreeReturnAmount, json.toString(), this);
    }

    /**
     * 发货
     *
     * @param type
     * @param num
     */
    public void sendDeliver(int type, String num) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", mInfo.orderid);
            json.put("expresscomtype", type);    // 物流公司（韵达快递 = 1,中通快递 = 2）
            json.put("expressno", num);            // 物流单号
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null).postJson(Method.sellerOrderSend, json.toString(), this);
    }

    /**
     * 请求物流详细信息
     */
    /**
     * 请求物流详细信息
     */
    protected void requireExpress(String expressno,long orderId) {
        LogUtil.logE("LogUtil","expressId -- :"+expressno+"--orderId--:"+orderId);
        JSONObject json = new JSONObject();
        try {
            json.put("expressno", expressno);
            json.put("orderid",orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null, getLoadingDialog()).postJson(Method.expressInfo, json.toString(), this);
    }

    public void requireData(long orderId) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null, getLoadingDialog()).postJson(Method.orderInfo, json.toString(), this);
    }


    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        switch (methodName) {
            case Method.orderInfo:
                SellerOrderInfo info = JSONHelper.getObject(json, SellerOrderInfo.class);
                if (null == info) {
                    LogUtil.logE("null == info");
                    finish();
                    return;
                }
                mInfo = info;
                fill(mInfo);
                String jsonProducts = JSONHelper.getStringFromObject(info.products);
                info.products = null;
                info.productsList = JSONHelper.getList(jsonProducts, OrderProduct.class);
                mDatas.clear();
                if (null != info.productsList) {
                    mDatas.addAll(info.productsList);
                }
                updateListView();
                break;
            case Method.orderAgreeReturn:
            case Method.orderDisagreeReturn:
            case Method.orderAgreeReturnAmount:
            case Method.orderDisagreeReturnAmount:
            case Method.sellerOrderSend:
            case Method.sellerGotReturnGoods:
                ToastUtil.showToast(this, getString(R.string.mall_operate_success));
                if (mTotalquerytype != 0 && mQuerytype != 0) {
                    OrderEvent event = new OrderEvent();
                    event.setType(OrderEvent.EVENT_UPDATE);
                    event.setClass1(mTotalquerytype);
                    event.setClass2(mQuerytype);
                    EventBus.getDefault().post(event);
                }
                finish();
                return;
            case Method.expressInfo: { // 物流
                ExpressInfo expressInfo = JSONHelper.getObject(json, ExpressInfo.class);
                if (null == expressInfo) {
                    return;
                }
//                expressInfo.stationList = ExpressInfo.parseStation(expressInfo.expresstext);
                expressInfo.toStationList(expressInfo.express);
                Intent it = new Intent(this, ExpressDetailAct.class);
                //it.putExtra(ExpressDetailAct.KEY_EXPRESS_NUM, expressInfo.expressno);
                //it.putExtra(ExpressDetailAct.KEY_COMPANY, expressInfo.companyname);
                it.putExtra(ExpressDetailAct.KEY_INFO, expressInfo);
                startActivity(it);
            }
            break;
        }
    }

    @Override
    public void onHttpError(String methodName, String msg, Object handle) {
        finish();
    }
}
