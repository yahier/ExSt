package yahier.exst.ui.DirectScreen.widget;

/**
 * Created by meteorshower on 16/4/12.
 *
 * 房间发送数据类型标记
 */
public enum IMPushControl {
    IM_PUSH_NORMAL(0),
    IM_PUSH_JOIN_ROOM(1),//加入房间
    IM_PUSH_UP_MICPLACE(2),//上麦消息
    IM_PUSH_DOWN_MICPLACE(3),//下麦消息
    IM_PUSH_DANMAKU(4),//弹幕消息
    IM_PUSH_QUITE_ROOM(5),//退出房间
    IM_PUSH_CLOSE_ROOM(6),//关闭房间
    IM_PUSH_SHOT_OFF_ROOM(7),//踢出房间
    IM_PUSH_MICPLACE_MODEL(8),//麦位状态：开启\关闭
    IM_PUSH_SEND_GIFT(9),//送礼物
    IM_PUSH_REMOVE_MICPLACE(10),//踢下麦位
    IM_PUSH_DANMAKU_MODEL(11),//弹幕状态
    IM_PUSH_GUEST_JOIN_ROOM(12)//嘉宾进入房间
;

    private int value;

    IMPushControl(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static IMPushControl getIMPushEnum(int value){
        switch(value){
            case 1:
                return IM_PUSH_JOIN_ROOM;
            case 2:
                return IM_PUSH_UP_MICPLACE;
            case 3:
                return IM_PUSH_DOWN_MICPLACE;
            case 4:
                return IM_PUSH_DANMAKU;
            case 5:
                return IM_PUSH_QUITE_ROOM;
            case 6:
                return IM_PUSH_CLOSE_ROOM;
            case 7:
                return IM_PUSH_SHOT_OFF_ROOM;
            case 8:
                return IM_PUSH_MICPLACE_MODEL;
            case 9:
                return IM_PUSH_SEND_GIFT;
            case 10:
                return IM_PUSH_REMOVE_MICPLACE;
            case 11:
                return IM_PUSH_DANMAKU_MODEL;
            case 12:
                return IM_PUSH_GUEST_JOIN_ROOM;
            default:
                return IM_PUSH_NORMAL;
        }
    }

}
