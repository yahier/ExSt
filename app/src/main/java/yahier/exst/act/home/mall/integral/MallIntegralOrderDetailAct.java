package yahier.exst.act.home.mall.integral;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.seller.ExpressDetailAct;
import com.stbl.stbl.act.home.seller.SimpleGoodsAdapter;
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
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.rong.imkit.RongIM;

/**
 * 积分兑换订单详情
 *
 * @author ruilin
 */
public class MallIntegralOrderDetailAct extends ThemeActivity implements OnFinalHttpCallback, View.OnClickListener {
    public final static String INTENT_ORDER_INFO = "INTENT_ORDER_INFO";

    protected MallIntegralOrderDetailAct mAct;
    protected SimpleGoodsAdapter mAdapter;//商品适配器
    protected ArrayList<OrderProduct> mDatas;//商品数据
    protected ScrollView mScrollView;
    protected ListView mListView;

    protected Button btnSeeLogistics;//查看物流
    protected View llService;//客服热线
    protected View mView_shuoming;//订单、下单时间等
    protected View mView_bottombar;//底部栏

    protected TextView tv_state; //订单状态名称
    protected TextView tv_state2; //订单状态说明

    protected ProgressDialog loadingDialog;

    // Data
    protected SellerOrderInfo mInfo; //订单详情
    private long orderId; //订单id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_integral_order_detail);
        setLabel(getString(R.string.order_detail));
        mAct = this;
        mDatas = new ArrayList<>();

        mScrollView = (ScrollView) findViewById(R.id.sv_order_detail_root);
        mListView = (ListView) findViewById(R.id.lv_goods);
        mAdapter = new SimpleGoodsAdapter(this, mDatas,true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent it = new Intent(MallIntegralOrderDetailAct.this, MallIntegralGoodsDetailAct.class);
                Intent it = new Intent(MallIntegralOrderDetailAct.this, MallIntegralSnapGoodsDetailAct.class);
                it.putExtra("goodsid", mDatas.get(position).getOrderdetailid());
                it.putExtra("isGoodsSnapshot",true);
                startActivity(it);
            }
        });

        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_state2 = (TextView) findViewById(R.id.tv_state2);
        (btnSeeLogistics = (Button) findViewById(R.id.btn_see_logistics)).setOnClickListener(this);
        llService = findViewById(R.id.ll_service);
        llService.setOnClickListener(this);
        mView_shuoming = findViewById(R.id.block_shuoming);
        mView_bottombar = findViewById(R.id.bottombar);
        findViewById(R.id.copy_order_btn).setOnClickListener(this);

        Intent it = getIntent();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_service: //客服热线
                TipsDialog.showOrderHumanHelp(this);
                break;
            case R.id.btn_see_logistics: //查看物流
                requireExpress(mInfo.expressno, mInfo.orderid);
                break;
            case R.id.copy_order_btn: //复制
                if (mInfo != null && mInfo.orderid > 0) {
                    ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    StringBuffer sb = new StringBuffer();
                    sb.append(getString(R.string.buyer_1)).append(mInfo.buyerinfoview != null ? mInfo.buyerinfoview.getNickname() : "").append("\n");
                    sb.append(getString(R.string.order_no)).append(mInfo.orderid).append("\n");
                    sb.append(getString(R.string.exchange_time)).append(mInfo.paytime != 0 ? DateUtil.getTime(String.valueOf(mInfo.paytime),"yyyy-MM-dd HH:mm:ss") : "--").append("\n");
                    sb.append(getString(R.string.consignee_info));
                    if (mInfo.addressinfo != null){
                        sb.append(mInfo.addressinfo.getUsername() != null ? mInfo.addressinfo.getUsername() : "").append("   ").
                                append(mInfo.addressinfo.getPhone()).append("   ").
                                append(mInfo.addressinfo.getCountryname()).
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
                                sb.append(op.getGoodsname()).append("  ").append("x").append(op.getCount()).append("\n");
                        }
                    }
                    sb.append(getString(R.string.goods_money)).append(StringUtil.get2ScaleString(mInfo.totalamount)+getString(R.string.piao)).append("\n");
                    if (mInfo.userremark != null && !mInfo.userremark.equals(""))
                        sb.append(getString(R.string.goods_remark)).append(mInfo.userremark != null ? mInfo.userremark : "");
                    ClipData myClip = ClipData.newPlainText("text", sb.toString());
                    myClipboard.setPrimaryClip(myClip);
                    ToastUtil.showToast(getString(R.string.copy_success));
                } else {
                    ToastUtil.showToast(getString(R.string.copy_order_info_err));
                }
                break;
        }
    }

    private void updateListView() {
        mAdapter.notifyDataSetChanged();
        ViewUtils.setListViewHeightBasedOnChildren(mListView);
    }

    public void fill(SellerOrderInfo info) {
        tv_state.setText(info.orderstatename);
        tv_state2.setText(info.orderstatedescribe);
        TextView tv = (TextView) findViewById(R.id.tv_order_num);
        tv.setText(String.valueOf(info.orderid));
        // consignee
        tv = (TextView) findViewById(R.id.tv_consignee);
        tv.setText(info.addressinfo.getUsername());

        tv = (TextView) findViewById(R.id.tv_phone);
        tv.setText(info.addressinfo.getPhone());
        // address
        tv = (TextView) findViewById(R.id.tv_address);
        tv.setText(info.addressinfo.getCountryname() + " "
                + info.addressinfo.getProvincename() + " "
                + info.addressinfo.getCityname() + " "
                + info.addressinfo.getDistrictname() + " "
                + info.addressinfo.getAddress());

        TextView tvLeaveMsg = (TextView) findViewById(R.id.leave_msg_tv);
        if (!StringUtils.isEmpty(info.userremark)) {
            tvLeaveMsg.setVisibility(View.VISIBLE);
            tvLeaveMsg.setText(getString(R.string.goods_remark) + info.userremark);
            findViewById(R.id.leave_msg_line).setVisibility(View.VISIBLE);
        } else {
            tvLeaveMsg.setVisibility(View.GONE);
            findViewById(R.id.leave_msg_line).setVisibility(View.GONE);
        }

        // produces
        if (null != info.productsList && info.productsList.size() > 0) {
            mDatas.addAll(info.productsList);
            updateListView();
        }
        // price
        tv = (TextView) findViewById(R.id.tv_goodsprice);
        tv.setText("" + (int)info.totalamount);

        // time
//        tv = (TextView) findViewById(R.id.tv_createtime);
//        tv.setText(info.createtime != 0 ? DateUtil.getTime(String.valueOf(info.createtime), "yyyy-MM-dd HH:mm:ss") : "--");
        tv = (TextView) findViewById(R.id.tv_exchange_time);
        tv.setText(info.paytime != 0 ? DateUtil.getTime(String.valueOf(info.paytime), "yyyy-MM-dd HH:mm:ss") : "--");

//        setPhoto(info);
        LogUtil.logE("!!! order state: " + info.orderstate);

        fillState(info);
    }

    private void fillState(SellerOrderInfo info) {
        switch (info.orderstate) {
            case OrderState.WAIT_FOR_SEND: //待发货
                btnSeeLogistics.setVisibility(View.GONE);
                break;
            case OrderState.WAIT_FOR_RECEIVE: //待收货
                btnSeeLogistics.setVisibility(View.VISIBLE);
                break;
            default:
                btnSeeLogistics.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 请求物流详细信息
     */
    protected void requireExpress(String expressno, long orderId) {
        LogUtil.logE("LogUtil", "expressId -- :" + expressno + "--orderId--:" + orderId);
        JSONObject json = new JSONObject();
        try {
            json.put("expressno", expressno);
            json.put("orderid", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null, getLoadingDialog()).postJson(Method.expressInfo, json.toString(), this);
    }

    /**
     * 订单详情
     *
     * @param orderId
     */
    public void requireData(long orderId) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HttpUtil(this, null, getLoadingDialog()).postJson(Method.orderPointsmallOrderInfo, json.toString(), this);
    }

    public ProgressDialog getLoadingDialog() {
        if (null == loadingDialog) {
            loadingDialog = LoadingView.createDefLoading(this);
        }
        return loadingDialog;
    }

    @Override
    public void onHttpResponse(String methodName, String json, Object handle) {
        switch (methodName) {
            case Method.orderPointsmallOrderInfo:
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
            case Method.expressInfo: { // 物流
                ExpressInfo expressInfo = JSONHelper.getObject(json, ExpressInfo.class);
                if (null == expressInfo) {
                    return;
                }
                expressInfo.toStationList(expressInfo.express);
                Intent it = new Intent(this, ExpressDetailAct.class);
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

