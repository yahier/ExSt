package yahier.exst.external.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import io.rong.eventbus.EventBus;


public class AliPay {
//	private long payNo;
	private String payNo; //广告支付改的
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	// 商户PID
	public static final String PARTNER = "2088121658295162";//
	// 商户收款账号
	public static final String SELLER = "francis@stbl.cc";//
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANiR8fOmMWZOALDnSQ7Ef2gboMHHC3Rx65k09WkBt/tBrjSNmu/sWxBfMN1EqolvJuiSxV5M5oN5y84ZWtTSQB+Zcsg96ZHzaxJx2/SZeadKOh9/IJSOr6nKP9n5MK0STgrQ3EbZSePv6Xs0CvwLoIYsYOLm2wjh4mBORuq5F9snAgMBAAECgYEApiVhP5ESePACq7TkYYXyUIZRkBgJ+62iF4l/dK2y3fmHkh3I7aOgQTEmJbqWMpWpfeeLr0rD1b3M0zK0cm1SbwJAVQAgnaFOB5BcVc1z/Nh1x5K4V0AnVSmI0Rhe/H8eBjrxC+FUB1Ub3K548VynhfQ8Z93DQxzK0tSmHfgxK8ECQQDxQAXO+6onkNNZ/1IQ15Z7Sha5os7q6jWhD8MH968CchYd6AqRzpvjjLTOS+UqNYK5dyn15GvacSW99kZcOx6XAkEA5c+jZvfGPhIwhNB5oM86OM0GDMkb3CIi3NAGzkXZgMpNGzHfDGiCljVwCmniyM96x2mvrwfHMrtv3cE58eFJ8QJBAO1BjXTVw61NnM1xb3/oxbFSV68REnWtCDjxi1iWmaLKGD4pKdvjWSQcCOSpdyJfpcRIERgmIHufswJy5RdnfNkCQGC/JyVEDjs3YSGoumuOkg4zkeZ0C7yCynZ3RUY1dbmYcRmPxzzsCn2+BWr85LJxVlj5DzL6bUvYSOHsZL6Ff6ECQQCS27D72Zn6CeJyd/xqAi7c1Mfagn0yJOGNoI04hSlkFsCvcDiAslOz2T8/I/XMwqEy5wYoMH86HAaUxUFZlCPX";
	// 支付宝公钥
	// public static final String RSA_PUBLIC =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
	// 上面是默认的
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

	public boolean isShowToast = true; //是否弹支付结果toast

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();
				LogUtil.logE("alipay resultStatus:"+resultStatus);
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					if (isShowToast) ToastUtil.showToast("支付成功");
					ExternalPayResult result = new ExternalPayResult(ExternalPayResult.TYPE_ALIPAY, ExternalPayResult.errCodeSucceed, "" + payNo);
					Intent intent = new Intent(ACTION.ALI_PAY_CALLBACK);
					intent.putExtra(EXTRA.PAY_RESULT, result);
					LocalBroadcastHelper.getInstance().send(intent);
					//发通知
					EventBus.getDefault().post(result);
		  		} else {

					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						ExternalPayResult result = new  ExternalPayResult(ExternalPayResult.TYPE_ALIPAY, ExternalPayResult.errCodeSucceed, ""+payNo);
						if (isShowToast) ToastUtil.showToast("支付结果确认中");
						//ExternalPayResult result = new ExternalPayResult(ExternalPayResult.TYPE_ALIPAY, ExternalPayResult.errCodeSucceed, "" + payNo);
						Intent intent = new Intent(ACTION.ALI_PAY_CALLBACK);
						intent.putExtra(EXTRA.PAY_RESULT, result);
						LocalBroadcastHelper.getInstance().send(intent);
						//发通知
						EventBus.getDefault().post(result);
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						if (isShowToast) ToastUtil.showToast("支付失败");
						ExternalPayResult result = new  ExternalPayResult(ExternalPayResult.TYPE_ALIPAY, ExternalPayResult.errCodeFailed, ""+payNo);
						Intent intent = new Intent(ACTION.ALI_PAY_CALLBACK);
						intent.putExtra(EXTRA.PAY_RESULT, result);
						LocalBroadcastHelper.getInstance().send(intent);
						//发通知
						EventBus.getDefault().post(result);
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
//				Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
//						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
	//设置是否弹支付toast
	public AliPay setIsShowToast(boolean isShowToast){
		this.isShowToast = isShowToast;
		return this;
	}
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * goodsName 商品名称
	 * describe 商品描述
	 * price 商品价格 
	 */
//	public void pay(final Activity act, String goodsName, String describe, String price, long payNo) {
	public void pay(final Activity act, String goodsName, String describe, String price, String payNo) {
		this.payNo = payNo;
		// 订单
		String orderInfo = getOrderInfo(goodsName, describe, price, payNo);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();
//		final String payInfo = "partner=\"2088121658295162\"&seller_id=\"francis@stbl.cc\"&out_trade_no=\"1016122710172468201\"&subject=\"师徒部落广告位服务一年期(后台)\"&body=\"123\"&total_fee=\"0.03\"&notify_url=\"http://d1-api-pm.shifugroup.net/api/pay/notify/ali\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"&sign=\"iwkU4ZNLlC7vhrtJafZf%2bShFCEuyTsUp9jCvvpx4npuiTPQsfahVerFwScMu2AHCeF4qbaWm1Tq1LxMTruw3a8exl4Ysi4fRgIFOz2EOou%2f0BCeFAd%2b%2bDP5pTR3AeReYNkO2bQhDogLt%2fOYvuGoZ3dLNmyiu7DAH21ik6h3Zoq8%3d\"&sign_type=\"RSA\"";
		LogUtil.logE("ALIPAY","payinfo---:"+payInfo);
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(act);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * goodsName 商品名称
	 * describe 商品描述
	 * price 商品价格
	 */
	public void payNew(final Activity act, final String payInfo) {
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(act);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
//	public void check(final Activity act) {
//		Runnable checkRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//				// 构造PayTask 对象
//				PayTask payTask = new PayTask(act);
//				// 调用查询接口，获取查询结果
//				boolean isExist = payTask.checkAccountIfExist();
//
//				Message msg = new Message();
//				msg.what = SDK_CHECK_FLAG;
//				msg.obj = isExist;
//				mHandler.sendMessage(msg);
//			}
//		};
//
//		Thread checkThread = new Thread(checkRunnable);
//		checkThread.start();
//
//	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public String getSDKVersion(Activity act) {
		PayTask payTask = new PayTask(act);
		String version = payTask.getVersion();
//		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
		return version;
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price, String payno) {
//	public String getOrderInfo(String subject, String body, String price, long payno) {
       // final String notifyUrlDefault = "http://120.25.150.37:10000/pay/ali/notify";
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
//		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		orderInfo += "&out_trade_no=" + "\"" + payno + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
//		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
//				+ "\"";
		orderInfo += "&notify_url=" + "\"" + Config.hostMain+"pay/ali/notify"
				+ "\"";
		
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
