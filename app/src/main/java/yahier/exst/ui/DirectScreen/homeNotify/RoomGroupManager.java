package yahier.exst.ui.DirectScreen.homeNotify;

import android.util.Log;
import android.widget.Toast;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.ui.DirectScreen.homeNotify.callback.OnRoomGroupCallBack;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StringUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupAddOpt;
import com.tencent.TIMGroupBaseInfo;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMGroupSelfInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/4/10.
 * <p/>
 * 群组管理
 */
public class RoomGroupManager {

    private static RoomGroupManager roomGroupManager = null;

    public static RoomGroupManager getInstance() {
        if (roomGroupManager == null)
            roomGroupManager = new RoomGroupManager();
        return roomGroupManager;
    }

    private Logger logger = new Logger(this.getClass().getSimpleName());
    private TIMGroupManager timGroupManager = null;
    private String roomGroupId;
    private int resetNumber = 0;//重试次数

    private TIMGroupManager getTIMGroupManager() {
        if (timGroupManager == null)
            timGroupManager = TIMGroupManager.getInstance();
        return timGroupManager;
    }

    /**
     * 群组创建
     */
    public void createGroup(String groupName, List<String> memberList, final OnRoomGroupCallBack<String, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().createGroup("Public", memberList, groupName, new TIMValueCallBack<String>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(String s) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(s, s, RoomGroupMethod.CREATE_GROUP.getValue());
                modifyGroupAddOpt(s, null);
            }
        });
    }

    /**
     * 申请加入群组
     */
    public void applyJoinGroup(final String groupId, String reason, final OnRoomGroupCallBack<Object, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().applyJoinGroup(groupId, reason, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess() {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, null, RoomGroupMethod.APPLY_JOIN_GROUP.getValue());
            }
        });
    }

    /**
     * 邀请成员
     */
    public void inviteGroupMember(final String groupId, List<String> memberList, final OnRoomGroupCallBack<List<TIMGroupMemberResult>, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().inviteGroupMember(groupId, memberList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, timGroupMemberResults, RoomGroupMethod.INVITE_GROUP_MEMBER.getValue());
            }
        });
    }

    /** 删除群组 */
    public void deleteGroup(final String groupId, final OnRoomGroupCallBack<Boolean, Integer> callBack){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().deleteGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if(callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess() {
                if(callBack != null)
                    callBack.onRoomGroupSuccess(groupId, true, RoomGroupMethod.DELETE_GROUP.getValue());
            }
        });
    }

    /**
     * 删除群组成员
     */
    public void deleteGroupMember(final String groupId, List<String> memberList, final OnRoomGroupCallBack<List<TIMGroupMemberResult>, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().deleteGroupMember(groupId, memberList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, timGroupMemberResults, RoomGroupMethod.DELETE_GROUP_MEMBER.getValue());
            }
        });
    }

    /**
     * 退出群组
     */
    public void quitGroup(final String groupId, final OnRoomGroupCallBack<Boolean, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().quitGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess() {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, true, RoomGroupMethod.QUITE_GROUP.getValue());
            }
        });
    }

    /**
     * 获取加入群组列表
     */
    public void getGroupList(final OnRoomGroupCallBack<List<TIMGroupBaseInfo>, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupBaseInfos) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(null, timGroupBaseInfos, RoomGroupMethod.GET_GROUP_LIST.getValue());
            }
        });
    }

    /**
     * 获取在群组中的信息
     */
    public void getSelfInfo(final String groupId, final OnRoomGroupCallBack<TIMGroupSelfInfo, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().getSelfInfo(groupId, new TIMValueCallBack<TIMGroupSelfInfo>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(TIMGroupSelfInfo timGroupSelfInfo) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, timGroupSelfInfo, RoomGroupMethod.GROUP_SELF_INFO.getValue());
            }
        });
    }

    /**
     * 获取群组成员
     */
    public void getGroupMembers(final String groupId, final OnRoomGroupCallBack<List<TIMGroupMemberInfo>, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().getGroupMembers(groupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, timGroupMemberInfos, RoomGroupMethod.GROUP_MEMBERS.getValue());
            }
        });
    }

    /**
     * 加群类型:默认任何人都可以加入
     */
    public void modifyGroupAddOpt(final String groupId, final OnRoomGroupCallBack<Boolean, Integer> callBack) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().modifyGroupAddOpt(groupId, TIMGroupAddOpt.TIM_GROUP_ADD_ANY, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
                Toast.makeText(MyApplication.getStblContext(), IMError.getErrorValue(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(groupId, true, RoomGroupMethod.MODIFY_GROUP_ADD_OPT.getValue());
            }
        });
    }

    /** 获取群组详细信息 */
    public void getGroupPublicInfo(List<String> groupIdList, final OnRoomGroupCallBack<List<TIMGroupDetailInfo>, Integer> callBack){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getTIMGroupManager().getGroupPublicInfo(groupIdList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                if (callBack != null)
                    callBack.onRoomGroupError(i);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (callBack != null)
                    callBack.onRoomGroupSuccess(null, timGroupDetailInfos, RoomGroupMethod.GET_GROUP_PUBLIC_INFO.getValue());
            }
        });
    }

    /***********************************************************************************************/
    /** 初始化群组数据 */
    public void initGroupData() {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        STBLWession.getInstance().setGroupId("");
        getGroupList(new OnRoomGroupCallBack<List<TIMGroupBaseInfo>, Integer>() {
            @Override
            public void onRoomGroupError(int errorValue) {
                switch(errorValue){
                    case 6013:
                    case 6014:
                    case 6018:
                        if(resetNumber < 3) {
                            RoomMsgManager.getInstance().loginIM();
                            initGroupData();
                            resetNumber++;
                            return;
                        }else{
                            resetNumber = 0;
                        }
                        break;
                    default:
                        break;
                }
                ToastUtil.showToast(IMError.getErrorValue(errorValue));
            }

            @Override
            public void onRoomGroupSuccess(String groupId, List<TIMGroupBaseInfo> timGroupBaseInfos, Integer integer) {
                logger.e(" ----------------- groupListSuccess ------------------- ");
                if (timGroupBaseInfos == null)
                    return;
                logger.e(" --------------------- groupListSuccess: " + timGroupBaseInfos.size() + " --------------------------- ");
                initGroupSelfData(timGroupBaseInfos);
            }
        });

    }

    private void initGroupSelfData(List<TIMGroupBaseInfo> arrayList) {
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        for (TIMGroupBaseInfo info : arrayList) {
            getSelfInfo(info.getGroupId(), new OnRoomGroupCallBack<TIMGroupSelfInfo, Integer>() {
                @Override
                public void onRoomGroupError(int errorValue) {
                    ToastUtil.showToast(IMError.getErrorValue(errorValue));
                }

                @Override
                public void onRoomGroupSuccess(String groupId, TIMGroupSelfInfo timGroupSelfInfo, Integer integer) {
                    logger.e(" --------- Owner GroupId : " + timGroupSelfInfo.getRole() +" ------------- ");

                    switch (timGroupSelfInfo.getRole()){
                        case Owner:{
                            logger.e(" --------- Owner GroupId : " + groupId + " ------------- ");
                            if(StringUtils.isEmpty(STBLWession.getInstance().getGroupId())){
                                STBLWession.getInstance().setGroupId(groupId);
                                modifyGroupAddOpt(groupId, null);
                                clearOtherMember(timGroupSelfInfo.getUser(), groupId);
                            }else {
                                deleteGroup(groupId, null);
                            }
                        }
                            break;
                        case Admin:
                        case Normal:{
                            quitGroup(groupId, null);
                        }
                            break;
                        case NotMember:
                            break;
                    }
                }
            });
        }
    }

    /** 清理群成员 */
    private void clearOtherMember(final String userId, String groupId){
        if (!RoomMsgManager.getInstance().isOpenMsgService())
            return;

        getGroupMembers(groupId, new OnRoomGroupCallBack<List<TIMGroupMemberInfo>, Integer>() {
            @Override
            public void onRoomGroupError(int errorValue) {
                logger.e(" -------- Error : "+IMError.getErrorValue(errorValue)+" -------");
            }

            @Override
            public void onRoomGroupSuccess(String groupId, List<TIMGroupMemberInfo> timGroupMemberInfos, Integer integer) {

                if(timGroupMemberInfos == null || timGroupMemberInfos.size() <= 1)
                    return;

                List<String> arrayList = new ArrayList<String>();
                for(int i = 0; i < timGroupMemberInfos.size(); i++){
                    TIMGroupMemberInfo info = timGroupMemberInfos.get(i);
                    if(!info.getUser().equals(userId)){
                        arrayList.add(info.getUser());
                    }
                }

                deleteGroupMember(groupId, arrayList, null);
            }
        });
    }

    public void clearRoomGroupIdAndQuiteGroup(){
        setRoomGroupId("");
//        quitGroup(getRoomGroupId(), new OnRoomGroupCallBack<Boolean, Integer>() {
//            @Override
//            public void onRoomGroupError(int errorValue) {
//                setRoomGroupId("");
//            }
//
//            @Override
//            public void onRoomGroupSuccess(String groupId, Boolean aBoolean, Integer integer) {
//                setRoomGroupId("");
//            }
//        });
    }

    /**
     * 保存房间GroupId
     */
    public void setRoomGroupId(String roomGroupId) {
        this.roomGroupId = roomGroupId;
    }

    /**
     * 获取房间GroupId
     */
    public String getRoomGroupId() {
        return roomGroupId;
    }

    public enum RoomGroupMethod {
        CREATE_GROUP(0),
        APPLY_JOIN_GROUP(1),
        INVITE_GROUP_MEMBER(2),
        DELETE_GROUP_MEMBER(3),
        QUITE_GROUP(4),
        GET_GROUP_LIST(5),
        GROUP_SELF_INFO(6),
        GROUP_MEMBERS(7),
        MODIFY_GROUP_ADD_OPT(8),
        GET_GROUP_PUBLIC_INFO(9),
        DELETE_GROUP(10);

        private int value;

        RoomGroupMethod(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
