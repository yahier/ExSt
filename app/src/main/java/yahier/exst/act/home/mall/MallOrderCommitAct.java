package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.address.MallAddressAct;
import com.stbl.stbl.act.home.mall.integral.MallIntegralOrderDetailAct;
import com.stbl.stbl.adapter.mall.MallCreateOrderGoodsAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.model.Address;
import com.stbl.stbl.model.DiscountTicket;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.model.OrderCreateResult;
import com.stbl.stbl.model.PrePaydata;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.Payment.OnPayResult;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.util.WheelString;
import com.stbl.stbl.util.WheelString.OnTimeWheelListener;
import com.stbl.stbl.utils.StringUtils;
import com.tencent.mm.sdk.modelbase.BaseResp;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class MallOrderCommitAct extends ThemeActivity implements OnClickListener, FinalHttpCallback, OnTimeWheelListener, OnPayResult {

    TextView tvAddress, tvName, tvPhone;
    TextView tvAddressEmptyTips;//空地址时提示语
    View llAddressRoot;//地址栏
    MallCart mallCart;
    final int requestCoupon = 100;
    final int requestAddress = 101;
    final int requestInvoice = 102;
    int addressId;
    LinearLayout linPay;
    TextView tvGoodsMoneyCount, tvDiscoutMoney, tvPayMoneyCount, tvLastPayCount;
    //	TextView tv_fapiao, tv_fapiao_type;
    float goodsMoneyCount;

    TextView tvPayType;
    Goods goods; // 要从选择类别页面传入
    LinearLayout linShop;
    int payType = 1;
    //public final static int payTypeWeixin = 1;
    //public final static int payTypeAlipay = 2;
    //public final static int payTypeBalance = 3;
    //public final static int payTypeDou = 4;
    public final static int payTypeIntegral = 6;//积分（独立开）
    int ismergepay; // 是否合并
    String[] cartidarr;
    List<Integer> listCarts = new ArrayList<Integer>();
    long orderNo;
    long payNo;
    String transactionNo; // 支付平台的交易单号
    String goodsName = "";
    String goodsDesc = "";
    boolean hasInvoice = false;
    private static final long PAUSETIME = 20 * 60 * 1000; //20分钟
    private Handler mHandler;
    private RelativeLayout rlCoupon; //优惠卷列
    private TextView tvCouponTips; //优惠

    private EditText etYDGYId;//一点公益id
    /**
     * 每个商品最多只能添加留言的字数
     */
    private static final int itemRemarkTextCount = 140;
    private Payment payment;

    //以下是积分兑换所用，有关积分操作都围绕这个标记
    private boolean isIntegralExchange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_order_commit);
        setLabel(getString(R.string.mall_confirm_order));
        mallCart = (MallCart) getIntent().getSerializableExtra("item");
        isIntegralExchange = getIntent().getBooleanExtra("isIntegralExchange", false);
        if (mallCart == null) {
            showToast(getString(R.string.mall_no_mallcart));
            return;
        }
        goodsMoneyCount = getGoodMoneyCount();
        initView();
        getDefaultAddress();
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, PAUSETIME);
    }


    float getGoodMoneyCount() {
        float count = 0;
        List<MallCartShop> list = mallCart.getCartshops();
        for (int i = 0; i < list.size(); i++) {
            count += list.get(i).getTotalamount();
        }
        String countStr = StringUtil.get2ScaleString(count);
        if (countStr.equals("")) return count;
        return Float.valueOf(countStr);
        //return count;
    }

    void initView() {
        etYDGYId = (EditText) findViewById(R.id.et_ydgy_id);
        linShop = (LinearLayout) findViewById(R.id.linShop);
        llAddressRoot = findViewById(R.id.ll_address_root);
        tvAddressEmptyTips = (TextView) findViewById(R.id.tv_address_empty_tips);
        tvAddress = (TextView) findViewById(R.id.tv_dizhi);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAddress = (TextView) findViewById(R.id.tv_dizhi);
        tvGoodsMoneyCount = (TextView) findViewById(R.id.tvGoodsMoneyCount);
        tvDiscoutMoney = (TextView) findViewById(R.id.tvDiscoutMoney);
        // tvPayMoneyCount = (TextView) findViewById(R.id.tvPayMoneyCount);
        tvLastPayCount = (TextView) findViewById(R.id.tvLastPayCount);
        tvGoodsMoneyCount.setText(String.valueOf(goodsMoneyCount));
        tvPayType = (TextView) findViewById(R.id.tvPayType);
//		tv_fapiao = (TextView) findViewById(R.id.tv_fapiao);
//		tv_fapiao_type = (TextView) findViewById(R.id.tv_fapiao_type);
        rlCoupon = (RelativeLayout) findViewById(R.id.rl_coupon);
        tvCouponTips = (TextView) findViewById(R.id.tv_coupon_tips);
        rlCoupon.setOnClickListener(this);
        findViewById(R.id.address_lin).setOnClickListener(this);
//		findViewById(R.id.linInvoice).setOnClickListener(this);
        findViewById(R.id.btnPay).setOnClickListener(this);
        findViewById(R.id.linPay).setOnClickListener(this);
        if (Payment.getPayTypeNames(this).size() == 1) {
            tvPayType.setText(getString(R.string.mall_alipay));
            payType = Payment.TYPE_ALIPAY;
        }
        // listView.setAdapter(new MallCreateOrderShopAdapter(this, mallCart));
        adjustPrice();
        setShopGoodsData();
        if (isIntegralExchange) //积分兑换
            integral();
    }

    /**
     * 当为积分兑换时
     */
    private void integral() {
        tvPayType.setText(getString(R.string.shitupiao_exchange));
        tvPayType.setCompoundDrawables(null, null, null, null);
        payType = payTypeIntegral;
        findViewById(R.id.linPay).setOnClickListener(null);
        findViewById(R.id.bottombar).setVisibility(View.GONE);
        findViewById(R.id.bottom_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        RelativeLayout.LayoutParams rlparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        rlparams.addRule(RelativeLayout.ABOVE,R.id.bottom_bar);
        findViewById(R.id.scrollView1).setLayoutParams(rlparams);
        TextView tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_total_price.setText(String.valueOf((int)goodsMoneyCount));
    }

    void adjustPrice() {
        float discountValue = 0;
        tvDiscoutMoney.setText(String.valueOf(-discountValue));
        // tvPayMoneyCount.setText(String.valueOf(goodsMoneyCount -
        // discountValue));
        String moneyCount = StringUtil.get2ScaleString(goodsMoneyCount - discountValue);
        String countText = moneyCount.equals("") ? String.valueOf(goodsMoneyCount - discountValue) : moneyCount;
        tvLastPayCount.setText("￥" + countText);
//		tvLastPayCount.setText("￥" + String.valueOf(goodsMoneyCount - discountValue));
    }

    void setShopGoodsData() {
        List<MallCartShop> list = mallCart.getCartshops();
        LogUtil.logE("o size:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            MallCartShop shop = list.get(i);
            View con = LayoutInflater.from(this).inflate(R.layout.mall_create_order_shop, null);

            LinearLayout llPage = (LinearLayout) con.findViewById(R.id.llPage);

            MallCreateOrderGoodsAdapter adapter = new MallCreateOrderGoodsAdapter(this, list.get(i).getCartgoods(), isIntegralExchange);
            adapter.setDividerColor(R.color.line_bgs);
            if (list.size() > 1)
                adapter.setDividerHeight(0.5f);
            adapter.setAdapter(llPage);
            TextView userName = (TextView) con.findViewById(R.id.userName);
            TextView moneyCount = (TextView) con.findViewById(R.id.moneyCount);
            userName.setText(TextUtils.isEmpty(shop.getAlias()) ? shop.getShopname() : shop.getAlias());
//            userName.setText(shop.getShopname());
            if (!isIntegralExchange) {
                String totalText = StringUtil.get2ScaleString(shop.getTotalamount());
                totalText = totalText.equals("") ? String.valueOf(shop.getTotalamount()) : totalText;
                moneyCount.setText("￥" + totalText);
            } else {
                con.findViewById(R.id.ll_shop_total).setVisibility(View.GONE);
            }

            final TextView tvTextcountTips = (TextView) con.findViewById(R.id.tv_textcount_tips);
            EditText etGiveSellerMsg = (EditText) con.findViewById(R.id.et_give_seller_msg);
            if (isIntegralExchange) {
                //一点公益id，兑换商品把横线和id隐藏
                con.findViewById(R.id.v_id_line);
                con.findViewById(R.id.ll_id_layout);
                etGiveSellerMsg.setHint(R.string.mall_service_remark);
            }
            tvTextcountTips.setText(0 + "/" + itemRemarkTextCount);
            etGiveSellerMsg.addTextChangedListener(new TextListener() {
                @Override
                public void afterTextChanged(Editable arg0) {
                    tvTextcountTips.setText(arg0.toString().length() + "/" + itemRemarkTextCount);
                }
            });

            linShop.addView(con);
        }

    }

    void getDefaultAddress() {
        new HttpEntity(this).commonPostData(Method.getDefaultAddress, null, this);
    }

    void clearAddressValue() {
        llAddressRoot.setVisibility(View.INVISIBLE);
        tvAddressEmptyTips.setVisibility(View.VISIBLE);
        addressId = 0;
        tvAddress.setText(R.string.mall_address);
        tvName.setText(R.string.mall_consignee);
        tvPhone.setText("--");
    }

    void setAddressValue(Address address) {
        tvAddressEmptyTips.setVisibility(View.GONE);
        llAddressRoot.setVisibility(View.VISIBLE);
        addressId = address.getAddressid();
        tvAddress.setText(address.getCountryname() + address.getProvincename() + address.getCityname() + address.getDistrictname() + address.getAddress());
        tvName.setText(address.getUsername());
        tvPhone.setText(address.getPhone());
    }

    private void doCommit(String pwd) {
        if (payType == 0) {
            return;
        }
        WaitingDialog.show(this, getString(R.string.waiting), false);
        JSONObject json = new JSONObject();
        try {
            json.put("addressid", addressId);
            json.put("paytype", payType);
            json.put("paypwd", pwd);
            if (hasInvoice) {
//				json.put("invoicetitle", tv_fapiao.getText().toString());
//				json.put("invoicetype", 1);
                // json.put("userremark", bannerBm.get(0));
            }
            json.put("totalamount", goodsMoneyCount);
            json.put("realamount", goodsMoneyCount);
            json.put("suborders", generateSubOrders());
            Integer[] carts = (Integer[]) listCarts.toArray(new Integer[listCarts.size()]);
            json.put("cartidarr", carts);// 购物车数组
            LogUtil.logE("数据:" + json.toString());
        } catch (JSONException t) {
            StackTraceElement[] tracks = t.getStackTrace();
            for (StackTraceElement el : tracks) {
                LogUtil.logE("error:", el.getClassName());
                LogUtil.logE("error:", el.getLineNumber() + "");
            }

        }
        if (!isIntegralExchange) {
            new HttpEntity(this).commonPostJson(Method.orderCommit, json.toString(), this);
        }else {
            new HttpEntity(this).commonPostJson(Method.orderPointsmallOrderCreate, json.toString(), this);
        }
    }

    /**
     * 生成subOrders的级别
     *
     * @return
     * @throws JSONException
     */
    JSONArray generateSubOrders() throws JSONException {
        listCarts.clear();
        JSONArray array = new JSONArray();
        List<MallCartShop> shops = mallCart.getCartshops();
        for (int i = 0; i < shops.size(); i++) {
            LogUtil.logE("i:" + i);
            MallCartShop shop = shops.get(i);
            JSONObject json = new JSONObject();

            json.put("shopid", shop.getShopid());
            json.put("totalamount", getFormatFlaotMoney(shop.getTotalamount()));// 订单总金额
            json.put("realamount", getFormatFlaotMoney(shop.getTotalamount()));// 实际支付金额

            // json.put("ticketId", shop.getShopid());//优惠券
            json.put("userremark", getUserRemark(i)); //给卖家的留言
            json.put("courierid",getYDGYID()); //给每个卖家的一点公益信使id
            List<MallCartGoods> goodsList = shop.getCartgoods();
            JSONArray array2 = new JSONArray();
            for (int j = 0; j < goodsList.size(); j++) {
                MallCartGoods goods = goodsList.get(j);
                JSONObject json2 = new JSONObject();
                LogUtil.logE("json:goodsid" + goods.getGoodsid());
                json2.put("goodsId", goods.getGoodsid());//
                json2.put("goodsname", goods.getGoodsname());//
                json2.put("skuname", goods.getSkuname());//
                json2.put("skuId", goods.getSkuid());//
                json2.put("qty", goods.getGoodscount());
                json2.put("price", goods.getRealprice());
                listCarts.add(goods.getCartid());
                // array2.put(json2);
                array2.add(json2);

                goodsName = goods.getGoodsname();
                goodsDesc += goods.getGoodsname() + "";
            }

            json.put("products", array2);
            // array.put(json);
            array.add(json);
        }

        return array;

    }

    private float getFormatFlaotMoney(float money) {
        String countStr = StringUtil.get2ScaleString(money);
        if (StringUtils.isEmpty(countStr))
            return money;
        return Float.valueOf(countStr);
    }
    //获取给每个卖家的一点公益的信使id
    private String getYDGYID() {
        return etYDGYId.getText().toString();
    }
    //获取给每个卖家的留言
    private String getUserRemark(int index) {
        if (linShop == null) return "";
        View shopView = linShop.getChildAt(index);
        EditText etRemark = (EditText) shopView.findViewById(R.id.et_give_seller_msg);
        return etRemark.getText().toString();
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            WaitingDialog.dismiss();
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        Intent intent;
        switch (methodName) {
            case Method.getDefaultAddress:
                Address address = JSONHelper.getObject(obj, Address.class);
                if (address == null) {
                    break;
                }
                setAddressValue(address);
                break;
            case Method.orderPointsmallOrderCreate: //兑换礼物
                WaitingDialog.dismiss();
                EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_INTEGRAL));
                long orderid = 0;
                try {
                    org.json.JSONObject json = new org.json.JSONObject(obj);
                    orderid = json.optLong("orderno");
                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                }
                if (orderid != 0) {
                    Intent it = new Intent(this, MallIntegralOrderDetailAct.class);
                    it.putExtra("orderid", orderid);
                    startActivity(it);
                }
                showToast(getString(R.string.exchange_success));
                finish();
                break;
            case Method.orderCommit://购买商品
                WaitingDialog.dismiss();
                OrderCreateResult orderResult = JSONHelper.getObject(obj, OrderCreateResult.class);
                orderNo = orderResult.getOrderno();
                LogUtil.logE("_orderNo:" + orderNo);
                LogUtil.logE("LogUtil", "ordercreateresult : " + obj);

                if (orderResult.getOrderstate() == OrderCreateResult.orderstateSucceed) { //支付成功
                    setResult(Activity.RESULT_OK);
                    finish();
                    showToast(getString(R.string.mall_buy_success));
                    //关闭商品详情
                    EventBus.getDefault().post(new EventType(EventType.TYPE_FINISH_GOODS_DETAIL));
                    // 通知我的模块，订单数量更新
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                    // 通知商城侧边栏，我的订单数更新
                    EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                    // 跳转到订单
                    intent = new Intent(this, MallOrderAct.class);
                    intent.putExtra("index", 2);
                    startActivity(intent);
                    return;
                } else if (orderResult.getOrderstate() == OrderCreateResult.orderstateWaitingPay) { //待支付
                    setResult(Activity.RESULT_OK);
                    PrePaydata prePayData = orderResult.getPrepaydata();
                    payNo = prePayData.getOrderpayno();
                    LogUtil.logE("orderCommit payType:" + payType);
                    // 通知商城侧边栏，我的订单数更新
                    EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                    // 通知我的模块，订单数量更新
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshMine));
                    payment = new Payment(Payment.PAY_FOR_ORDER, this, this);
                    payment.pay(payType, prePayData.getOrderno(), prePayData.getOrderpayno(), prePayData.getPayfee(), goodsDesc,
                            prePayData.getWeixinjsonparameters());

                } else {
//				showToast(orderResult.getMsg());
                    TipsDialog.popup(this, orderResult.getMsg(), getString(R.string.mall_confirm2));
                }

                break;
        }

    }

    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, Config.interClickTime);
        Intent intent;
        switch (view.getId()) {
            case R.id.address_lin:
                intent = new Intent(this, MallAddressAct.class);
                intent.putExtra("typeSource", MallAddressAct.typeSourceSelect);
                if (addressId == 0)
                    intent.putExtra("isNull", true);
                startActivityForResult(intent, requestAddress);
                break;
//		case R.id.linInvoice:
//			intent = new Intent(this, InvoiceAddAct.class);
//			startActivityForResult(intent, requestInvoice);
//			break;
            case R.id.linPay:
                WheelString list = new WheelString();
                list.setOnTimeWheelListener(this);
                List<String> listType = new ArrayList<String>();
                //listType.add("微信支付");
                //listType.add("支付宝支付");
                //listType.add("余额支付");
                //listType.add("金豆");
//			listType = Arrays.asList(Payment.payTypesName);
                listType = Payment.getPayTypeNames(this);
                //Payment.payTypesName.
                list.chooseTime(this, listType);
                break;
            case R.id.rl_coupon: //优惠卷
//			Intent it = new Intent(MallOrderCommitAct.this, MallCouponAct.class);
//			it.putExtra("isUser", true);
//			MallOrderCommitAct.this.startActivityForResult(it, requestCoupon);
                break;
            case R.id.btn_confirm: //积分兑换
            case R.id.btnPay:
                if (addressId == 0) {
                    TipsDialog.popup(this, getString(R.string.mall_input_address), getString(R.string.mall_confirm2));
                    return;
                }
                if (orderNo != 0) { //订单号不为0代表已经生成过订单
                    payment.clearOrderId();
                    payment = null;
                    // 跳转到订单
                    intent = new Intent(this, MallOrderAct.class);
                    intent.putExtra("index", 0);
                    startActivity(intent);
                    finish();
                    return;
                }
                switch (payType) {
                    //case Payment.TYPE_BALANCE:
                    case payTypeIntegral: //积分兑换
                        String tips = getString(R.string.mall_confirm_use)+(int)goodsMoneyCount+getString(R.string.mall_shitupiao_exchange);
                        checkPwd(tips);
                        break;
                    case Payment.TYPE_DOU://金豆
                        String tips2 = getString(R.string.mall_confirm_use)+goodsMoneyCount+getString(R.string.mall_gold_exchange);
                        checkPwd(tips2);
                        break;
                    default:
                        doCommit("null");
                        break;
                }
                break;
        }

    }
    /**验证密码*/
    private void checkPwd(String tips){
        TipsDialog.popup(this, tips, getString(R.string.mall_cancel), getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                // 填写密码
                Payment.getPassword(MallOrderCommitAct.this, goodsMoneyCount, new OnInputListener() {
                    @Override
                    public void onInputFinished(String pwd) {
                        doCommit(pwd);
                    }
                });
            }
            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从地址列表中返回
        if (requestCode == requestAddress) {
            if (resultCode == 1) {
                Address address = (Address) data.getSerializableExtra("item");
                boolean address_empty = data.getBooleanExtra("address_empty",false);
                if (address_empty) clearAddressValue();
                if (address == null) {
                    if (addressId == 0)
                        getDefaultAddress();
                } else {
                    setAddressValue(address);
                }
            }
//		} else if (requestCode == requestInvoice) {
//			if (resultCode == RESULT_OK) {
//				String title = data.getStringExtra("invoiceTitle");
//				String type = data.getStringExtra("invoiceType");
////				tv_fapiao.setText(title);
////				tv_fapiao_type.setText(type);
//				hasInvoice = true;
//			}
        } else if (requestCode == requestCoupon) {
            if (resultCode == RESULT_OK) {
                DiscountTicket coupon = (DiscountTicket) data.getSerializableExtra("item");
                if (coupon != null) {
                    int index = data.getIntExtra("index", 0);
                    View shopView = linShop.getChildAt(index);
                    TextView tv_title = (TextView) shopView.findViewById(R.id.title);
                    tv_title.setText(coupon.getTitle());
                }
            }
        }

    }


    //微信与支付宝的回调
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.ALI_PAY_CALLBACK:
                    ExternalPayResult result = (ExternalPayResult) intent.getSerializableExtra(EXTRA.PAY_RESULT);
                    LogUtil.logE(("支付宝:code:" + result.getErrCode()));
                    switch (result.getErrCode()) {
                        case ExternalPayResult.errCodeSucceed:
                            break;
                        case ExternalPayResult.errCodeFailed:
                            break;
                    }
                    break;
                case ACTION.WX_PAY_CALLBACK:
                    ExternalPayResult resultWX = (ExternalPayResult) intent.getSerializableExtra(EXTRA.PAY_RESULT);
                    LogUtil.logE(("微信 commit:code:" + resultWX.getErrCode()));
                    switch (resultWX.getErrCode()) {
                        case BaseResp.ErrCode.ERR_OK:
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            break;
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                            break;
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalBroadcastHelper.getInstance().unregister(mReceiver);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onTagOk(int index, String tag) {
        //payType = index + 1;
//		payType = Payment.payTypes[index];
        payType = Payment.getPayType(tag);
        tvPayType.setText(tag);
        if (payType == Payment.TYPE_DOU) {
            float jindou2rmb = (float) SharedPrefUtils.getFromPublicFile(KEY.jindou2rmb, 0f);
            float totalMoney = goodsMoneyCount;
            if (jindou2rmb != 0f)
                totalMoney = goodsMoneyCount / jindou2rmb;
            tvLastPayCount.setText(String.valueOf(totalMoney) + "("+getString(R.string.mall_gold)+")");
        } else {
            tvLastPayCount.setText("￥" + String.valueOf(goodsMoneyCount));
        }

        LogUtil.logE("onTagOk payType:" + payType);
    }

    @Override
    public void onPayResult(boolean isSuccess) {
        LogUtil.logE("commit onPayResult:isSuccess:" + isSuccess);
        if (isSuccess) {
            // 跳转到订单
            Intent intent = new Intent(this, MallOrderAct.class);
            intent.putExtra("index", 2);
            startActivity(intent);
        } else {
            // 跳转到订单
            Intent intent = new Intent(this, MallOrderAct.class);
            intent.putExtra("index", 1);
            startActivity(intent);
        }
        //关闭商品详情
        EventBus.getDefault().post(new EventType(EventType.TYPE_FINISH_GOODS_DETAIL));
        finish();
    }
}
