package yahier.exst.act.im.rong;

import android.os.Parcel;
import android.util.Log;

import com.stbl.stbl.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;


/**
 * 找人代付消息
 */

@MessageTag(value = "adotherpay", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class AdOtherPayMessage extends MessageContent {

	private String orderno; //订单号
	private String fromusername; //发送者昵称
	private String tousername; //目标昵称
	private double price; //价格

	public AdOtherPayMessage() {

	}

	public static AdOtherPayMessage obtain(String orderno, String fromusername, String tousername,float price) {
		AdOtherPayMessage rongRedPacketMessage = new AdOtherPayMessage();
		rongRedPacketMessage.orderno = orderno;
//		rongRedPacketMessage.fromusername = tousername;//是为了兼容ios才把值调换的
//		rongRedPacketMessage.tousername = fromusername;
		rongRedPacketMessage.fromusername = fromusername;
		rongRedPacketMessage.tousername = tousername;
		rongRedPacketMessage.price = price;
		return rongRedPacketMessage;
	}

	// 给消息赋值。
	public AdOtherPayMessage(byte[] data) {
		try {
			String jsonStr = new String(data, "UTF-8");
			LogUtil.logE("LogUtil","AdOtherPayMessage--receive json-"+jsonStr);
			JSONObject jsonObj = new JSONObject(jsonStr);
			setOrderno(jsonObj.optString("orderno"));
			setFromusername(jsonObj.optString("fromusername"));
			setTousername(jsonObj.optString("tousername"));
			setPrice(jsonObj.optDouble("price"));
			if (jsonObj.has("user")) {
				setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
			}
		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		} catch (UnsupportedEncodingException e1) {

		}
	}

	/**
	 * 构造函数。
	 *
	 * @param in
	 *            初始化传入的 Parcel。
	 */
	public AdOtherPayMessage(Parcel in) {
		setOrderno(ParcelUtils.readFromParcel(in));
		setTousername(ParcelUtils.readFromParcel(in));
		setFromusername(ParcelUtils.readFromParcel(in));
		setPrice(ParcelUtils.readDoubleFromParcel(in));
		setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
	}

	/**
	 * 将类的数据写入外部提供的 Parcel 中。
	 *
	 * @param dest
	 *            对象被写入的 Parcel。
	 * @param flags
	 *            对象如何被写入的附加标志。
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// 这里可继续增加你消息的属性
		ParcelUtils.writeToParcel(dest, orderno);
		ParcelUtils.writeToParcel(dest, tousername);
		ParcelUtils.writeToParcel(dest, fromusername);
		ParcelUtils.writeToParcel(dest, price);
		ParcelUtils.writeToParcel(dest, getUserInfo());
	}

	/**
	 * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	 */
	public static final Creator<AdOtherPayMessage> CREATOR = new Creator<AdOtherPayMessage>() {

		@Override
		public AdOtherPayMessage createFromParcel(Parcel source) {
			return new AdOtherPayMessage(source);
		}

		@Override
		public AdOtherPayMessage[] newArray(int size) {
			return new AdOtherPayMessage[size];
		}
	};

	/**
	 * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
	 * 
	 * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
	 */
	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();
		try {

			jsonObj.put("orderno", orderno);
			jsonObj.put("fromusername", fromusername);
			jsonObj.put("tousername", tousername);
			jsonObj.put("price", price);
			if (getJSONUserInfo() != null)
				jsonObj.putOpt("user", getJSONUserInfo());

		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}

		try {
			LogUtil.logE("LogUtil","AdOtherPayMessage--send json-"+jsonObj.toString());
			return jsonObj.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getFromusername() {
		return fromusername;
	}

	public void setFromusername(String fromusername) {
		this.fromusername = fromusername;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getTousername() {
		return tousername;
	}

	public void setTousername(String tousername) {
		this.tousername = tousername;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
