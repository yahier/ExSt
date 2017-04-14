package yahier.exst.wxapi;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import com.stbl.stbl.util.LogUtil;

import android.util.Log;
import android.util.Xml;

public class WeixinPayHelper {
	public final static  String TAG = "WeixinPayHelper";

	public static String genProductArgs(String totleFee, String productDes,String payNo) {
		LogUtil.logE("WeixinPayHelper:"+payNo);
		StringBuffer xml = new StringBuffer();
		try {
			String nonceStr = genNonceStr();
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));// 微信分配的公众账号ID
			packageParams.add(new BasicNameValuePair("body", productDes));// 商品或支付单简要描述
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));// 微信支付分配的商户号
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));// 随机字符串
			packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));// 接收微信支付异步通知回调地址
			packageParams.add(new BasicNameValuePair("out_trade_no", payNo));// 商户系统内部的订单号,32个字符内 genOutTradNo()
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));// APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
			packageParams.add(new BasicNameValuePair("total_fee", totleFee));// 订单总金额，单位为分
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));// 签名

			String xmlstring = toXml(packageParams);
			return new String(xmlstring.toString().getBytes(), "ISO8859-1");//至关重要的转码步骤
			//return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	public static String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	public static long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	public static String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	public static String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	public static String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	public static Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}
	public  static String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		Log.e("genAppSign 115", appSign);
		return appSign;
	}
}
