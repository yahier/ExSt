package yahier.exst.act.im.rong;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 讨论组邀请
 * 
 * @author lsy
 * @date 2015-11-19
 * 
 *       MessageTag 中 flag 中参数的含义： 1.NONE，空值，不表示任何意义.在会话列表不会显示出来。
 *       2.ISPERSISTED，消息需要被存储到消息历史记录。 3.ISCOUNTED，消息需要被记入未读消息数。
 * 
 *       value：消息对象名称。 请不要以 "RC:" 开头， "RC:" 为官方保留前缀。
 */

@MessageTag(value = "invite", flag = MessageTag.ISPERSISTED | MessageTag.ISCOUNTED)
public class DiscussionInviteMessage extends MessageContent {
	// private int conversationtype;

//	private String userid;
//	private String message;
//	private String targetid;
//	private long inviteid;

	String ex_params;// json格式的字符串

	public DiscussionInviteMessage() {
		
	}
	
//	@Override
//	public String getExtra() {
//		String extra = super.getExtra();
//		LogUtil.logE("extra:"+extra);
//		return extra;
//	}

	/**
	 * 新加 只要一个参数 ex_params
	 * 
	 * @return
	 */
	
	
	public DiscussionInviteMessage obtain(String ex_params) {
		DiscussionInviteMessage inviteMessage = new DiscussionInviteMessage();
		//String extas = getExtra();// 主要是这个的
		inviteMessage.setEx_params(ex_params);
		return inviteMessage;
	}



//	public static DiscussionInviteMessage obtain(String userid, String message, String targetid, int inviteid) {
//		DiscussionInviteMessage inviteMessage = new DiscussionInviteMessage();
//		//inviteMessage.userid = userid;
//		//inviteMessage.message = message;
//		//inviteMessage.targetid = targetid;
//		//inviteMessage.inviteid = inviteid;
//		return inviteMessage;
//	}

	// 给消息赋值。
	public DiscussionInviteMessage(byte[] data) {
		try {
			String jsonStr = new String(data, "UTF-8");
			JSONObject jsonObj = new JSONObject(jsonStr);
			setEx_params(jsonObj.getString("ex_params"));
			//setMessage(jsonObj.getString("message"));
			//setTargetid(jsonObj.getString("targetid"));
			//setInviteid(jsonObj.getLong("inviteid"));
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
	public DiscussionInviteMessage(Parcel in) {
		//setUserid(ParcelUtils.readFromParcel(in));
		//setMessage(ParcelUtils.readFromParcel(in));
		//setTargetid(ParcelUtils.readFromParcel(in));
		setEx_params(ParcelUtils.readFromParcel(in));
		// setUserInfo(ParcelUtils.readFromParcel(in, UserInfo.class));
	}

	/**
	 * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
	 */
	public static final Creator<DiscussionInviteMessage> CREATOR = new Creator<DiscussionInviteMessage>() {

		@Override
		public DiscussionInviteMessage createFromParcel(Parcel source) {
			return new DiscussionInviteMessage(source);
		}

		@Override
		public DiscussionInviteMessage[] newArray(int size) {
			return new DiscussionInviteMessage[size];
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
		//ParcelUtils.writeToParcel(dest, userid);
		//ParcelUtils.writeToParcel(dest, message);
		//ParcelUtils.writeToParcel(dest, targetid);
		ParcelUtils.writeToParcel(dest, ex_params);
		// ParcelUtils.writeToParcel(dest, getUserInfo());

	}

	/**
	 * 将消息属性封装成 json 串，再将 json 串转成 byte 数组，该方法会在发消息时调用
	 */
	@Override
	public byte[] encode() {
		JSONObject jsonObj = new JSONObject();
		try {

			//jsonObj.put("userid", userid);
			//jsonObj.put("message", message);
			//jsonObj.put("targetid", targetid);
			jsonObj.put("ex_params", ex_params);
			// if (getJSONUserInfo() != null)
			// jsonObj.putOpt("user", getJSONUserInfo());

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



	public String getEx_params() {
		return ex_params;
	}

	public void setEx_params(String ex_params) {
		this.ex_params = ex_params;
	}

}
