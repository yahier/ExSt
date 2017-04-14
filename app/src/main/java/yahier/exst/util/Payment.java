package yahier.exst.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.WalletChargeActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.external.alipay.AliPay;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.model.OrderCreateResult;
import com.stbl.stbl.model.PrePaydata;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.wxapi.MD5Util;
import com.stbl.stbl.wxapi.WeixinPay;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 支付模块
 * @author ruilin
 *
 */
public class Payment implements OnFinalHttpCallback {
	public final static int TYPE_WEIXIN = 1;
	public final static int TYPE_ALIPAY = 2;
	//public final static int TYPE_BALANCE = 3;
	public final static int TYPE_DOU = 4;

	public final static int[] payTypes = {
			TYPE_WEIXIN,TYPE_ALIPAY,TYPE_DOU
	};
//	public final static String[] payTypesName = {
//			"微信支付","支付宝支付"/*,"金豆支付"*/
//	};
	public static ArrayList<String> payTypesName = null;
	public final static int PAY_FOR_ORDER = 0;
	public final static int PAY_FOR_RECHARGE = 1;
	
	Activity mActivity;
	int payfor; // 支付用途
	String password;
	int payType;
	long orderId;
	long orderPayNo;
	float price;
	PrePaydata prePayData;

	int isNeedPassword = -1;
	
	public Payment(int payfor, Activity act, OnPayResult onPayResult) {
		this.payfor = payfor;
		this.mActivity = act;
		this.onPayResult = onPayResult;
		
		EventBus.getDefault().register(this);
		WalletTask.getWalletBalanceBackground(null);
	}
	/**可支付方式*/
	public static List<String> getPayTypeNames(Context context){
		if (payTypesName != null) return payTypesName;
		payTypesName = new ArrayList<>();
		if (context != null && AppUtils.isWeixinAvilible(context)){
			payTypesName.add(context.getString(R.string.mall_wechat_pay));
		}
		payTypesName.add(context.getString(R.string.mall_alipay));
		return payTypesName;
	}
	/**如果没有微信，则要特殊处理*/
	private void setPayType(Context context,int payType){
		if (context != null && AppUtils.isWeixinAvilible(context)){
			this.payType = payType;
		}else{
			this.payType = TYPE_ALIPAY;
		}
	}
	/**根据支付类型文字获得支付类型*/
	public static int getPayType(String payTypeName){
		Context context = MyApplication.getContext();
		String ALIPAY = context.getString(R.string.mall_alipay);
		String WECHATPAY = context.getString(R.string.mall_wechat_pay);
		String GOLDPAY = context.getString(R.string.mall_gold_pay);
		int payType = TYPE_WEIXIN;
		if (payTypeName.equals(ALIPAY)) {
			payType = TYPE_ALIPAY;
		}else if (payTypeName.equals(WECHATPAY)){
			payType = TYPE_WEIXIN;
		}else if (payTypeName.equals(GOLDPAY)){
			payType = TYPE_DOU;
		}
		return payType;
	}
	/**
	 * 预支付
	 * @param payType
	 * @param orderId
	 */
	public void prePay(final int payType, final long orderId) {
//		this.payType = payType;
		setPayType(mActivity,payType);
		this.orderId = orderId;
		switch (this.payType) {
			//case TYPE_BALANCE:
			case TYPE_DOU:
				//先检查有没有金豆
				getPassword(mActivity, 0, new OnInputListener() {
					@Override
					public void onInputFinished(String pwd) {
						requirePrePay(Payment.this.payType, orderId, pwd);
					}
				});
				return;
		}
		requirePrePay(this.payType, orderId, "");
	}
	
	private void requirePrePay(int payType, long orderId, String pwd) {
		setPayType(mActivity,payType);
		JSONObject json = new JSONObject();
		json.put("paytype", this.payType);
		json.put("orderno", orderId);
		json.put("paypwd", pwd);
		new HttpUtil(mActivity, this.payType).postJson(Method.orderPrePay, json.toJSONString(), this);
	}
	
	/**
	 * 支付
	 * @param payType 支付类型
	 * @param orderId 订单id
	 * @param orderPayNo 支付单号
	 * @param price 支付金额
	 * @param goodsDesc 商品描述
	 */
	public void pay(final int payType, final long orderId, final long orderPayNo, final float price, String goodsDesc, String...weixinParams) {
//		this.payType = payType;
		setPayType(mActivity,payType);
		this.orderId = orderId;
		this.orderPayNo = orderPayNo;
		this.price = price;
		switch (this.payType) {
		//case TYPE_BALANCE:
		case TYPE_DOU:
			checkHasGoldBean(mActivity,0,new OnCheckGoldsListener() {
				@Override
				public void onComplete() {
					payByBanlanceOrDou(Payment.this.payType, orderId, orderPayNo, price);
				}
			});
			break;
		case TYPE_WEIXIN:
			
			//PayParam params = new PayParam();
			//params.setGoodsDesc(goodsDesc);
			//params.setPayNo(orderPayNo);
			//params.setTotalFee(price);
			String weixinParam = weixinParams[0];
			new WeixinPay().invokeBack(mActivity, weixinParam);
			break;
		case TYPE_ALIPAY:
			new AliPay().pay(mActivity, mActivity.getString(R.string.mall_stbl_mall), goodsDesc, price+"", String.valueOf(orderPayNo));
			break;
		default:
			break;
		}
	}
	
	public void pay(int payType, long orderPayNo, float price, String goodsDesc,String...weixinParams) {
//		this.payType = payType;
		setPayType(mActivity,payType);
		this.orderPayNo = orderPayNo;
		this.price = price;
		switch (this.payType) {
		case TYPE_WEIXIN:
			String weixinParam = weixinParams[0];
			new WeixinPay().invokeBack(mActivity, weixinParam);
			break;
		case TYPE_ALIPAY:
			new AliPay().pay(mActivity, goodsDesc, goodsDesc, price+"", String.valueOf(orderPayNo));
			break;
		default:
			break;
		}
	}
	/**
	 * @param payMenoy 如果没有就传0，只要余额不为0，就进入下一步
	 * 是否有金豆
	 * */
	public static void checkHasGoldBean(final Context mContext,float payMenoy, final OnCheckGoldsListener listener){
		if (mContext == null) return;
		int goldBean = (int) SharedPrefUtils.getFromUserFile(KEY.jindou,-1);
		if (goldBean == -1){
			WalletTask.getWalletBalanceBackground(new SimpleTask.Callback<WealthInfo>() {
				@Override
				public void onError(TaskError e) {
					listener.onComplete(); //获取失败也让过，给服务端去判断
				}
				@Override
				public void onCompleted(WealthInfo result) {
					//获取失败也让过，给服务端去判断
					if (result == null || result.getJindou() > 0){
						listener.onComplete();
					}else{
						gotoWallet(mContext);
					}
				}

				@Override
				public boolean onDestroy() {
					return false;
				}
			});
			return;
		}
		//传0进来的代表不检测
		if (goldBean == 0 && payMenoy > goldBean){
			gotoWallet(mContext);
			return;
		}
		listener.onComplete();
	}
	/***
	 * 防止微信没有登录的之前的订单在后面还会回调
	 * 以orderid = 0 为标识
	 */
	public void clearOrderId(){
		this.orderId = 0;
	}

	public void onEvent(ExternalPayResult result) {
		switch (result.getType()) {
		case ExternalPayResult.TYPE_WEIXIN:
			if (result.getErrCode() == ExternalPayResult.errCodeSucceed) { //成功
				LogUtil.logE("LogUtil","WEI XIN SUCCESS");
				String transactionNo = result.getTransaction();
				if (orderId != 0)
					payVerify(transactionNo);
			}else{ //失败
				LogUtil.logE("LogUtil","WEI XIN FAILD");
				payFinish(false);
			}
			break;
		case ExternalPayResult.TYPE_ALIPAY:
			if (result.getErrCode() == ExternalPayResult.errCodeSucceed) { //成功
				payVerify("");
			}else{ //失败
				payFinish(false);
			}
			break;
		}
	}


	
	private void payVerify(String transactionNo) {
		switch (payfor) {
		case PAY_FOR_ORDER:
			doVerify(orderId, orderPayNo, transactionNo);
			break;
		case PAY_FOR_RECHARGE:
			doVerify(orderPayNo);
			break;
		}
	}
	
	// 购买商品支付验证
	public void doVerify(long orderid, long orderPayNo, String transationno) {
		JSONObject json = new JSONObject();
		LogUtil.logE("doVerify >> orderid:"+orderid+" orderPayNo:"+orderPayNo+" transationno:"+transationno);
		json.put("ismergepay", 0);// 1是合并订单，0是否
		json.put("orderno", orderid);
		json.put("orderpayno", orderPayNo);// 商城生成的订单支付号
		json.put("transationno", transationno);//支付平台反馈的订单号
		new HttpUtil(mActivity, null).postJson(Method.goodsPayVerify, json.toJSONString(), this);
	}
	// 充值支付验证
	public void doVerify(long orderPayNo) {
		//JSONObject json = new JSONObject();
		//json.put("orderpayno", orderPayNo);// 充值余额订单支付号
		//new HttpUtil(mActivity, null).postJson(Method.rechargePayVerify, json.toJSONString(), this);
	}

	/**
	 * 调起余额支付.或者金豆支付
	 */
	private void payByBanlanceOrDou(int paytype, long orderno, long orderpayno, float realpayfee) {
		setPayType(mActivity,paytype);
		JSONObject json = new JSONObject();
		json.put("paytype", this.payType);
		json.put("orderno", orderno);
		json.put("orderpayno", orderpayno);	// 商城生成的订单支付号
		json.put("realpayfee", realpayfee);	// 商城生成的订单支付号
		json.put("paypwd", password);		// 密码
		new HttpUtil(mActivity, this.payType).postJson(Method.payByBalanceOrDou, json.toJSONString(), this);
	}

	/**
	 * 获取密码，并判断是否足够金豆；(没有金豆支付了)
	 * 如果有重新启用了绿豆，把判断去掉，直接请求拿密码（交给服务器检测）
	 * @param ctx
	 * @param paymoney
	 * @param pwdListener
     */
	public static void getPassword(final Context ctx,float paymoney, final OnInputListener pwdListener) {
//		checkHasGoldBean(ctx, paymoney, new OnCheckGoldsListener() {
//			@Override
//			public void onComplete() {
				new HttpUtil(ctx, null).post(Method.userPasswordCheck, new OnFinalHttpCallback() {
					@Override
					public void onHttpResponse(String methodName, String json, Object handle) {
						LogUtil.logE("LogUtil", "methodName－－：" + methodName + "--json--:" + json);
						if (methodName.equals(Method.userPasswordCheck)) {
							boolean needPwd = JSONHelper.getObject(json, Boolean.class);
							if (needPwd) {
								PayingPwdDialog dialog = new PayingPwdDialog();
								dialog.setOnInputListener(new OnInputListener() {

									@Override
									public void onInputFinished(String pwd) {
										if (pwd != null) {
											String pwdMd5 = MD5Util.MD5Encode(pwd, null);
											pwdListener.onInputFinished(pwdMd5);
										}
									}
								});
//						dialog.setOnInputListener(pwdListener);
								dialog.show(ctx);
//								dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//									@Override
//									public void onCancel(DialogInterface dialog) {
//										pwdListener.onInputFinished(null);
//									}
//								});
							} else {
								pwdListener.onInputFinished("");
							}
						}
					}

					@Override
					public void onHttpError(String methodName, String msg, Object handle) {
						if (methodName.equals(Method.userPasswordCheck)) {
							pwdListener.onInputFinished(null);
						}
					}
				});
//			}
//		});
	}
	
	public static void gotoWallet(final Context ctx) {
		TipsDialog.popup(ctx, ctx.getString(R.string.mall_gold_no_tips), ctx.getString(R.string.mall_cancel), ctx.getString(R.string.mall_confirm2), new OnTipsListener() {
			
			@Override
			public void onConfirm() {
				Intent it = new Intent(ctx, WalletChargeActivity.class);
				ctx.startActivity(it);					
			}
			
			@Override
			public void onCancel() {}
		});
	}
	//支付结果回调
	private void payFinish(boolean isSuccess) {
		if (!mActivity.isFinishing()) {
			//ToastUtil.showToast(mActivity, "支付完成");
		}

		if(isSuccess){
			ToastUtil.showToast(mActivity, mActivity.getString(R.string.mall_pay_finish));
		}
		if (!isSuccess) orderId = 0;
		if (onPayResult != null) onPayResult.onPayResult(isSuccess);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onHttpResponse(String methodName, String json, Object handle) {
		if(methodName.equals(Method.orderPrePay)){
			int payType = NumUtils.getObjToInt(handle);
			prePayData = JSONHelper.getObject(json, PrePaydata.class);
			orderPayNo = prePayData.getOrderpayno();

			if (prePayData.getOrderstate() == OrderCreateResult.orderstateSucceed) {
				payFinish(true);

			} else if (prePayData.getOrderstate() == OrderCreateResult.orderstateWaitingPay) {
//				PrePaydata prePayData = orderResult.getPrepaydata();
				switch (payType) {
					case TYPE_WEIXIN:
						new WeixinPay().invokeBack(mActivity, prePayData.getWeixinjsonparameters());
						break;
					case TYPE_ALIPAY:
						// 支付宝
						new AliPay().pay(mActivity, prePayData.getOrderno()+"", mActivity.getString(R.string.mall_order_pay2), prePayData.getPayfee()+"", String.valueOf(prePayData.getOrderpayno()));
						break;
//					case TYPE_BALANCE:
//						payByBanlanceOrDou(TYPE_BALANCE, prePayData.getOrderno(), prePayData.getOrderpayno(), prePayData.getPayfee());
//						break;
					case TYPE_DOU:  //这里暂时没地方用到，成功失败都没做回到
						payByBanlanceOrDou(TYPE_DOU, prePayData.getOrderno(), prePayData.getOrderpayno(), prePayData.getPayfee());
						break;
				}
			} else {
				ToastUtil.showToast(mActivity.getString(R.string.mall_pay_err));
			}
		}else if(methodName.equals(Method.goodsPayVerify)){
			payFinish(true);
		}else if(methodName.equals(Method.rechargePayVerify)){
			payFinish(true);
		}
	}

	@Override
	public void onHttpError(String methodName, String msg, Object handle) {
		if(methodName.equals(Method.goodsPayVerify)){ //支付确认接口是支付成功才会调用的；所以，失败也要回调
			payFinish(false);
		}
	}

	OnPayResult onPayResult;
	public interface OnPayResult {
		public void onPayResult(boolean isSuccess);
	}

	public interface OnCheckGoldsListener{
		void onComplete();
	}
}
