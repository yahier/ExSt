package yahier.exst.act.im.rong;

import android.os.Parcel;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.message.InformationNotificationMessage;


/**
 * 自定义融云IM消息类
 *
 * @author lsy
 * @date 2015-11-19
 * <p/>
 * MessageTag 中 flag 中参数的含义： 1.NONE，空值，不表示任何意义.在会话列表不会显示出来。
 * 2.ISPERSISTED，消息需要被存储到消息历史记录。 3.ISCOUNTED，消息需要被记入未读消息数。
 * <p/>
 * value：消息对象名称。 请不要以 "RC:" 开头， "RC:" 为官方保留前缀。
 */
@MessageTag(value = "notification", flag = MessageTag.ISPERSISTED)
public class MyNotiMessage extends InformationNotificationMessage {//NotificationMessage

    //private JSONObject ex_params;// html格式的字符串
    private String type;
    private String opid;//操作者的id
    private String opname;
    private String name;
    private String opedid;//被操作者的id



    public MyNotiMessage() {
    }

    public static MyNotiMessage obtain(String type) {
        MyNotiMessage message = new MyNotiMessage();
        message.type = type;
        return message;
    }

//    public static MyNotiMessage obtain(String type, String opname, String name) {
//        MyNotiMessage message = new MyNotiMessage();
//        message.type = type;
//        message.opname = opname;
//        message.name = name;
//        return message;
//    }


    public MyNotiMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("type"))
                type = jsonObj.optString("type");
            if (jsonObj.has("opname"))
                opname = jsonObj.optString("opname");
            if (jsonObj.has("name"))
                name = jsonObj.optString("name");
            if (jsonObj.has("opedid"))
                opedid = jsonObj.optString("opedid");
            if (jsonObj.has("opid"))
                opid = jsonObj.optString("opid");
        } catch (JSONException e) {
            // RLog.e(this, "JSONException", e.getMessage());
        }

    }

    /**
     * 构造函数。
     *
     * @param in 初始化传入的 Parcel。
     */
    public MyNotiMessage(Parcel in) {
        type = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        opname = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        name = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        opedid = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        opid = ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<MyNotiMessage> CREATOR = new Creator<MyNotiMessage>() {

        @Override
        public MyNotiMessage createFromParcel(Parcel source) {
            return new MyNotiMessage(source);
        }

        @Override
        public MyNotiMessage[] newArray(int size) {
            return new MyNotiMessage[size];
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
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 这里可继续增加你消息的属性
        ParcelUtils.writeToParcel(dest, type);
        ParcelUtils.writeToParcel(dest, opname);
        ParcelUtils.writeToParcel(dest, name);
        ParcelUtils.writeToParcel(dest, opedid);
        ParcelUtils.writeToParcel(dest, opid);
    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("type", type);
            jsonObj.put("opname", opname);
            jsonObj.put("name", name);
            jsonObj.put("opedid", opedid);
            jsonObj.put("opid", opid);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpname() {
        return opname;
    }

    public void setOpname(String opname) {
        this.opname = opname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpedid() {
        return opedid;
    }

    public void setOpedid(String opedid) {
        this.opedid = opedid;
    }

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }
}
