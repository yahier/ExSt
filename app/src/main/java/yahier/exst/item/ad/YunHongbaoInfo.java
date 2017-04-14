package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class YunHongbaoInfo implements Serializable {
    String ID;
    String Type;
    String Amount;
    int Count;
    String GroupId;
    String Message;
    String Template;
    String Animation;
    String SenderDuid;

    String ReceiverDuid;
    String SenderNickname;
    String SenderAvatar;
    String Icon;
    YunHongbaoStatistics Statistics;
    public final static String typeAverage = "avg";
    public final static String typeRandom = "rand";


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public YunHongbaoStatistics getStatistics() {
        return Statistics;
    }

    public void setStatistics(YunHongbaoStatistics statistics) {
        Statistics = statistics;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getSenderAvatar() {
        return SenderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        SenderAvatar = senderAvatar;
    }

    public String getSenderNickname() {
        return SenderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        SenderNickname = senderNickname;
    }

    public String getReceiverDuid() {
        return ReceiverDuid;
    }

    public void setReceiverDuid(String receiverDuid) {
        ReceiverDuid = receiverDuid;
    }

    public String getSenderDuid() {
        return SenderDuid;
    }

    public void setSenderDuid(String senderDuid) {
        SenderDuid = senderDuid;
    }

    public String getAnimation() {
        return Animation;
    }

    public void setAnimation(String animation) {
        Animation = animation;
    }

    public String getTemplate() {
        return Template;
    }

    public void setTemplate(String template) {
        Template = template;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
