package yahier.exst.ui.DirectScreen.util;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.api.data.directScreen.RoomInfo;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.LoadStore;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.DirectScreen.homeNotify.IMError;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomGroupManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.RoomMsgManager;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomMsgCallBack;
import com.stbl.stbl.ui.DirectScreen.service.QavsdkServiceApi;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UISkipUtils;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;


/**
 * 进入直播间工具类
 * Created by Administrator on 2016/4/12 0012.
 */
public class LiveRoomHelper {

    private Logger logger = new Logger(this.getClass().getSimpleName());
    private Context mContext;
    private QavsdkManger.OnQavsdkRoomCloseListener closeListener;
    private STProgressDialog pressageDialog;
    private String roomGroupId;

    public LiveRoomHelper(Context context,String roomGroupId){
        this.mContext = context;
        if (pressageDialog == null)
            pressageDialog = new STProgressDialog(mContext);
        this.roomGroupId = roomGroupId;
    }
    public void setOnQavsdkRoomCloseListener(QavsdkManger.OnQavsdkRoomCloseListener closeListener){
        this.closeListener = closeListener;
        QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(closeListener);
    }
    /**
     * 调用前，请先设置OnQavsdkRoomCloseListener
     * 如果服务运行，房间id相同，直接进入房间；如果服务运行，房间id不一致，关闭服务，进入到新的房间
     * @param roomId 房间id
     * @return
     */
    public void isStartServiceAndRoomIdEquals(int roomId){

        if (mContext == null) return;
        if (QavsdkServiceApi.isQavsdkScreenServiceRuning(mContext)){

            if(roomId == QavsdkServiceApi.getRoomId()){//自己已经进入的房间
                QavsdkServiceApi.startDirectScreenService(mContext);
                if (closeListener != null) closeListener.roomStatus(1);
                QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(null);
                closeListener = null;
            }else{
                TipsDialog dialog = new TipsDialog(mContext,"温馨提示","确定关闭之前房间，进入新房间吗？","取消","确认");
                dialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
                    @Override
                    public void onConfirm() {
                        QavsdkServiceApi.stopQavsdkScreenService(mContext);
                        UISkipUtils.stopFloatingDirectScreenService(mContext);
                        //等房间关闭的广播回调
                    }

                    @Override
                    public void onCancel() {
                        if (closeListener != null)
                            closeListener.roomStatus(4);
                        QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(null);
                        closeListener = null;
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (closeListener != null)
                            closeListener.roomStatus(4);
                        QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(null);
                        closeListener = null;
                    }
                });
                dialog.show();
                return;
            }
        }

        if (closeListener != null) closeListener.roomStatus(3);
        QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(null);
        closeListener = null;
    }

    /** 判断是否之前进入了房间 */
    public void checkInRoom(final int roomId) {
        checkInRoom(roomId, false);
    }

    /** 判断是否之前进入了房间 */
    public void checkInRoom(final int roomId, final boolean isGuest) {
        showDialog("请稍后");
        closeListener = new QavsdkManger.OnQavsdkRoomCloseListener() {
            @Override
            public void roomStatus(int status) {
                dialogDismiss();
                switch (status) {
                    case 1: // 1:房间存在并进入；
                        break;
                    case 2: // 2：房间存在需关闭回调；
//								break;
                    case 3: //  3：房间不存在，可以创建新房间
                        inRoom(roomId,null, isGuest);
                        break;
                    case 4:
                        break;
                }
            }
        };
        QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(closeListener);
        isStartServiceAndRoomIdEquals(roomId);
    }

    /** 进入房间 */
    public void inRoom(int roomId, String pwd) {
        inRoom(roomId, pwd, false);
    }

    /** 进入房间 */
    public void inRoom(final int roomId, final String pwd, final boolean isGuest) {

        showDialog("正在加入房间,请稍候...");
        logger.e(" ------------------------- inRoom GroupId:" + roomGroupId + " ------------------------- ");
        RoomGroupManager.getInstance().setRoomGroupId(roomGroupId);
        RoomGroupManager.getInstance().getSelfInfo(roomGroupId, new OnRoomGroupCallBack<TIMGroupSelfInfo, Integer>() {
            @Override
            public void onRoomGroupError(int errorValue) {
                switch(errorValue) {
                    case 6013:
                    case 6014:
                    case 6018:
                        loginTIM(roomId, pwd, isGuest);
                        break;
                    default:
                        addRoomGroup(roomGroupId, null, roomId, pwd, isGuest);
                        break;
                }
            }

            @Override
            public void onRoomGroupSuccess(String groupId, TIMGroupSelfInfo timGroupSelfInfo, Integer integer) {
                addRoomGroup(groupId, timGroupSelfInfo, roomId, pwd, isGuest);
            }
        });
    }

    public void loginTIM(final int roomId, final String pwd, final boolean isGuest){

        QavsdkManger.getInstance().formatAvContext();
        RoomMsgManager.getInstance().loginIM(new OnRoomMsgCallBack<Boolean>() {
            @Override
            public void roomMsgError(int errorValue) {
                dialogDismiss();
            }

            @Override
            public void roomMsgSuccess(Boolean aBoolean) {
                inRoom(roomId, pwd, isGuest);
            }
        });
    }

    private void addRoomGroup(String groupId, TIMGroupSelfInfo timGroupSelfInfo, final int roomId, final String pwd, final boolean isGuest){
        if (timGroupSelfInfo == null || timGroupSelfInfo.getRole() == TIMGroupMemberRoleType.NotMember) {//不是群成员，需走申请加入
            logger.e(" -----------------------不是群成员，申请加入！！"+groupId+" -------------------- ");
            RoomGroupManager.getInstance().applyJoinGroup(groupId, "万年帅哥万年帅", new OnRoomGroupCallBack<Object, Integer>() {
                @Override
                public void onRoomGroupError(int errorValue) {

                    switch(errorValue){
                        case 6013:
                        case 6014:
                        case 6018:
                            RoomMsgManager.getInstance().loginIM();
                            break;
                        case 10013://已是群成员
                            enterRoom(roomId, pwd, isGuest);
                            return;
                    }
                    dialogDismiss();
                    ToastUtil.showToast(IMError.getErrorValue(errorValue));
                }

                @Override
                public void onRoomGroupSuccess(String groupId, Object o, Integer integer) {
                    enterRoom(roomId, pwd, isGuest);
                }
            });
        } else {
            logger.e(" ------------------------ 本群成员，直接进入房间！！ -------------------- ");
            enterRoom(roomId, pwd, isGuest);
        }
    }

    public void enterRoom(int roomId, String pwd, final boolean isGuest) {
        try {
            JSONObject json = new JSONObject();
            json.put("roomid", roomId);
            if (pwd != null) json.put("pwd", pwd);
            new HttpEntity(mContext).commonPostJson(Method.imLiveroomIn, json.toJSONString(), new FinalHttpCallback() {
                @Override
                public void parse(String methodName, String result) {
                    Log.e(methodName, result);
                    BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                    if (item.getIssuccess() != BaseItem.successTag) {
                        dialogDismiss();
                        ToastUtil.showToast(mContext, item.getErr().getMsg());
                        return;
                    }
                    String valueObj = JSONHelper.getStringFromObject(item.getResult());

                    RoomInfo info = JSONHelper.getObject(valueObj, RoomInfo.class);

                    if (info == null || info.getRoomid() == 0) {
                        dialogDismiss();
                        ToastUtil.showToast(mContext, "房间数据错误!!");
                        return;
                    }

                    QavsdkManger.getInstance().createRoomEnter(info.getRoomid(), true, isGuest, new QavsdkManger.OnQavsdkUpdateListener() {
                        @Override
                        public void startSdkSuccess() { //成功
                            dialogDismiss();
                        }

                        @Override
                        public void closeProgress() {
                            dialogDismiss();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Log.w("DongTaiMainAdapter", e);
            ToastUtil.showToast(mContext, "直播链接类型发生变化了");
            dialogDismiss();
        }
    }

    private void showDialog(String message) {
        if (pressageDialog == null)
            pressageDialog = new STProgressDialog(mContext);

        if (pressageDialog.isShowing())
            pressageDialog.dismiss();
        else {
            pressageDialog.setMsgText(message);
            pressageDialog.show();
        }
    }

    private void dialogDismiss() {
        if (pressageDialog != null)
            pressageDialog.dismiss();
    }
}
