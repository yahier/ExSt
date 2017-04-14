package yahier.exst.act.im.rong;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

//import io.rong.common.ParcelUtils;

/**
 * 订单消息
 */

@MessageTag(value = "order", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class OrderMessage extends MessageContent {

	private String orderid;
	private String ex_params;// html格式的字符串

	public OrderMessage() {

	}

	public static OrderMessage obtain(String orderid, String ex_params) {
		OrderMessage rongRedPacketMessage = new OrderMessage();
		rongRedPacketMessage.orderid = orderid;
		rongRedPacketMessage.ex_params = ex_params;
		return rongRedPacketMessage;
	}

	// 给消息赋值。
	public OrderMessage(byte[] data) {
		try {
			String jsonStr = new String(data, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonStr);
			setOrderid(jsonObj.getString("orderid"));
			setEx_params(jsonObj.getString("ex_params"));
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
	public OrderMessage(Parcel in) {
		setOrderid(ParcelUtils.readFromParcel(in));
		setEx_params(ParcelUtils.readFromParcel(in));
		setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
	}

	/**
	 * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	 */
	public static final Creator<OrderMessage> CREATOR = new Creator<OrderMessage>() {

		@Override
		public OrderMessage createFromParcel(Parcel source) {
			return new OrderMessage(source);
		}

		@Override
		public OrderMessage[] newArray(int size) {
			return new OrderMessage[size];
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
		ParcelUtils.writeToParcel(dest, orderid);
		ParcelUtils.writeToParcel(dest, ex_params);
		ParcelUtils.writeToParcel(dest, getUserInfo());
	}

	/**
	 * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
	 */
	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();
		try {

			jsonObj.put("orderid", orderid);
			jsonObj.put("ex_params", ex_params);
			if (getJSONUserInfo() != null)
				jsonObj.putOpt("user", getJSONUserInfo());

		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}

		try {
			return jsonObj.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getEx_params() {
		return ex_params;
	}

	public void setEx_params(String ex_params) {
		this.ex_params = ex_params;
	}

}
