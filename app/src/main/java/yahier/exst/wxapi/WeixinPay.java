package yahier.exst.wxapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.stbl.stbl.R;
import com.stbl.stbl.model.PayParam;
import com.stbl.stbl.model.WeixinPayItem;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ToastUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WeixinPay {
	private static final String TAG = "WeixinPay";
	PayReq req;

	Map<String, String> resultunifiedorder;

	Context mContext;
	String totalFee;
	String goodsDesc;
	long payNo;
	IWXAPI msgApi;
	WeixinPayItem weixinItem;
	
	/**
	 * 已经作废
	 * @param mContext
	 * @param payParams
	 */
	public void invoke(Context mContext, PayParam payParams) {
		this.mContext = mContext;
		msgApi = WXAPIFactory.createWXAPI(mContext, null);
		req = new PayReq();
		msgApi.registerApp(Constants.APP_ID);
		totalFee = String.valueOf((int) (payParams.getTotalFee() * 100));
		LogUtil.logE("真实数据是:totalFee:" + totalFee);
		// 下面是测试价钱
		goodsDesc = payParams.getGoodsDesc();
		payNo = payParams.getPayNo();
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
		//invokeBack();
	}

	/**
	 * 新的调用。
	 * @param mContext
	 * @param weixinjsonparameters
	 */
	public void invokeBack(Context mContext,String weixinjsonparameters) {
		this.mContext = mContext;
		msgApi = WXAPIFactory.createWXAPI(mContext, null);
		req = new PayReq();
		weixinItem = JSONHelper.getObject(weixinjsonparameters, WeixinPayItem.class);
		if (weixinItem != null) {
			LogUtil.logE("appid" + weixinItem.getAppid());
			// ToastUtil.showToast(this, "调起微信：" + weixinItem.getAppid());
			doInVoke();
		}
	}

	public void doInVoke() {
		// 经过打印 这7个数据都有
		Log.e("appId", weixinItem.getAppid());
		Log.e("partnerId", weixinItem.getPartnerid());
		Log.e("prepayId", weixinItem.getPrepayid());
		Log.e("packageValue", "Sign=WXPay");
		Log.e("nonceStr", weixinItem.getNoncestr());
		Log.e("timeStamp", weixinItem.getTimestamp());
		Log.e("sign", weixinItem.getSign());
		try {

			req.appId = weixinItem.getAppid();
			req.partnerId = weixinItem.getPartnerid();
			req.prepayId = weixinItem.getPrepayid();
			req.packageValue = "Sign=WXPay";
			req.nonceStr = weixinItem.getNoncestr();
			req.timeStamp = weixinItem.getTimestamp();
			req.sign = weixinItem.getSign();
			req.extData = "app data";
			// invoke
			msgApi.registerApp(Constants.APP_ID);
			msgApi.sendReq(req);
		} catch (Exception t) {
			// LogUtil.logE(methodName + " onFailure", t.toString());
			StackTraceElement[] tracks = t.getStackTrace();
			for (StackTraceElement el : tracks) {
				LogUtil.logE("error:", el.getClassName());
			}

		}
	}

	/**
	 * 生成预支付订单。后台如果也生成预支付订单的话，此处就不调用
	 * 
	 * @author lenovo
	 * 
	 */
	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(mContext, mContext.getString(R.string.app_tip), mContext.getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			resultunifiedorder = result;
			genPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			ToastUtil.showToast("支付 取消");
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = WeixinPayHelper.genProductArgs(totalFee, goodsDesc, String.valueOf(payNo));
			Log.e("orion", entity);
			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			Log.e("orion", content);
			Map<String, String> xml = WeixinPayHelper.decodeXml(content);

			return xml;
		}
	}

	private void genPayReq() {
		ToastUtil.showToast(mContext, "生成参数");
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
		req.nonceStr = WeixinPayHelper.genNonceStr();
		req.timeStamp = String.valueOf(WeixinPayHelper.genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = WeixinPayHelper.genAppSign(signParams);
		Log.e("orion", signParams.toString());
		// 调起微信
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

}
