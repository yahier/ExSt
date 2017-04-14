package yahier.exst.act.im.rong;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

@MessageTag(value = "common", flag = MessageTag.ISPERSISTED)
public class CommonMessageItem<T> extends TextMessage {

	// long opid;// 操作者的id
	// long opedid;// 被操作者的id
	// T ex_params;// 所属对象的item
	String title;// 商品、心愿、动态请等的标题
	String img;// 商品、心愿、动态url

	// String detail;
	// String sub;// 商品、心愿、动态右下角的文字
	// long price;// 商品价格
	public CommonMessageItem() {

	}

	@Override
	public void setExtra(String extra) {
		// TODO Auto-generated method stub
		super.setExtra(extra);
	}

	@Override
	public String getExtra() {
		// TODO Auto-generated method stub
		return super.getExtra();
	}
//
//	public static CommonMessageItem obtain(CommonMessageItem item) {
//		CommonMessageItem rongRedPacketMessage = new CommonMessageItem();
//		rongRedPacketMessage.title = item.getTitle();
//		rongRedPacketMessage.img = item.getImg();
//		return rongRedPacketMessage;
//	}
//
//	// 给消息赋值。
//	public CommonMessageItem(byte[] data) {
//		try {
//			String jsonStr = new String(data, "UTF-8");
//			JSONObject jsonObj = new JSONObject(jsonStr);
//			setTitle(jsonObj.getString("title"));
//			setImg(jsonObj.getString("img"));
//		} catch (JSONException e) {
//			Log.e("JSONException", e.getMessage());
//		} catch (UnsupportedEncodingException e1) {
//
//		}
//	}
//
//	/**
//	 * 构造函数。
//	 * 
//	 * @param in
//	 *            初始化传入的 Parcel。
//	 */
//	public CommonMessageItem(Parcel in) {
//		setTitle(ParcelUtils.readFromParcel(in));
//		setImg(ParcelUtils.readFromParcel(in));
//		// setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
//	}
//
//	/**
//	 * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
//	 */
//	public static final Creator<OrderMessage> CREATOR = new Creator<OrderMessage>() {
//
//		@Override
//		public OrderMessage createFromParcel(Parcel source) {
//			return new OrderMessage(source);
//		}
//
//		@Override
//		public OrderMessage[] newArray(int size) {
//			return new OrderMessage[size];
//		}
//	};
//
//	/**
//	 * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
//	 * 
//	 * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
//	 */
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	/**
//	 * 将类的数据写入外部提供的 Parcel 中。
//	 * 
//	 * @param dest
//	 *            对象被写入的 Parcel。
//	 * @param flags
//	 *            对象如何被写入的附加标志。
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		ParcelUtils.writeToParcel(dest, title);
//		ParcelUtils.writeToParcel(dest, img);
//	}
//
//	/**
//	 * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
//	 */
//	@Override
//	public byte[] encode() {
//		JSONObject jsonObj = new JSONObject();
//		try {
//
//			jsonObj.put("title", title);
//			jsonObj.put("img", img);
//		} catch (JSONException e) {
//			Log.e("JSONException", e.getMessage());
//		}
//
//		try {
//			return jsonObj.toString().getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
