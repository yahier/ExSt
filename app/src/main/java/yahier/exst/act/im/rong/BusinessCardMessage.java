package yahier.exst.act.im.rong;

import android.os.Parcel;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

//

/**
 * 自定义融云IM消息类
 * 
 * @author lsy
 * @date 2015-11-19
 * 
 *       MessageTag 中 flag 中参数的含义： 1.NONE，空值，不表示任何意义.在会话列表不会显示出来。
 *       2.ISPERSISTED，消息需要被存储到消息历史记录。 3.ISCOUNTED，消息需要被记入未读消息数。
 * 
 *       value：消息对象名称。 请不要以 "RC:" 开头， "RC:" 为官方保留前缀。
 */
@MessageTag(value = "card",flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class BusinessCardMessage extends MessageContent {

	private JSONObject ex_params;// html格式的字符串

	public BusinessCardMessage() {
	}

	public static BusinessCardMessage obtain(JSONObject ex_params) {
		BusinessCardMessage rongRedPacketMessage = new BusinessCardMessage();
		rongRedPacketMessage.ex_params = ex_params;
		return rongRedPacketMessage;
	}


	public static BusinessCardMessage obtain(UserItem goods) {
		BusinessCardMessage rongRedPacketMessage = new BusinessCardMessage();
		String str = JSON.toJSONString(goods);//对象转成String
		try {
			JSONObject itemJson = new JSONObject(str);
			JSONObject json = new JSONObject();
			json.put("ex_params", itemJson);
			LogUtil.logE("json:" + json);
			rongRedPacketMessage.ex_params = json;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return rongRedPacketMessage;

	}

	// 给消息赋值。
	public BusinessCardMessage(byte[] data) {
		try {
			String jsonStr = new String(data, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonStr);
			setEx_params(jsonObj);

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
	public BusinessCardMessage(Parcel in) {
		String str = ParcelUtils.readFromParcel(in);
		JSONObject json;
		try {
			json = new JSONObject(str);
			setEx_params(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	 */
	public static final Creator<BusinessCardMessage> CREATOR = new Creator<BusinessCardMessage>() {

		@Override
		public BusinessCardMessage createFromParcel(Parcel source) {
			return new BusinessCardMessage(source);
		}

		@Override
		public BusinessCardMessage[] newArray(int size) {
			return new BusinessCardMessage[size];
		}
	};

	//
	// /**
	// * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
	// *
	// * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
	// */
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
		ParcelUtils.writeToParcel(dest, ex_params.toString());
	}

	/**
	 * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
	 */
	@Override
	public byte[] encode() {
		try {
			return ex_params.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getEx_params() {
		return ex_params;
	}

	public void setEx_params(JSONObject ex_params) {
		this.ex_params = ex_params;
	}

}
