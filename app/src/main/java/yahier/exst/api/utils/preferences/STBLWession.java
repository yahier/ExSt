package yahier.exst.api.utils.preferences;

import com.stbl.stbl.api.data.LiveRoomToken;
import com.stbl.stbl.utils.NumUtils;

/**
 * Created by meteorshower on 16/3/3.
 */
public class STBLWession extends BasePreferences{

    private final static String SHARE_NAME = "wession";

    public STBLWession() {
        super(SHARE_NAME);
    }

    private static STBLWession stWession = null;

    public static STBLWession getInstance() {
        if (stWession == null)
            stWession = new STBLWession();
        return stWession;
    }

    /** 保存腾讯云直播Token */
    public void writeLiveRoomToken(LiveRoomToken roomToken){
        if(roomToken == null)
            return;
        writeUserSig(roomToken.getUsersig());
        writeSdkAppid(roomToken.getSdkappid());
        writeAccountType(roomToken.getAccounttype());
        writeIdentifier(roomToken.getIdentifier());
    }

    private final String userSigName = "userSig";
    private final String sdkAppIdName = "sdkAppid";
    private final String accountTypeName = "accountType";
    private final String identifierName = "identifier";
    private final String groupIdName = "groupId";

    /** 保存UserSig */
    public void writeUserSig(String userSig){
        writeValue(userSigName, userSig);
    }

    /** 读取UserSig */
    public String readUserSig(){
        return readValue(userSigName, "");
    }

    /** 保存SdkAppId */
    public void writeSdkAppid(String sdkAppid){
        writeValue(sdkAppIdName, sdkAppid);
    }

    /** 读取SdkAppId */
    public int readSdkAppid(){
        return NumUtils.getObjToInt(readValue(sdkAppIdName, ""));
    }

    /** 保存AccountType */
    public void writeAccountType(String accountType){
        writeValue(accountTypeName, accountType);
    }

    /** 读取AccountType */
    public String readAccountType(){
        return readValue(accountTypeName, "");
    }

    /** 保存Identifier */
    public void writeIdentifier(String identifier){
        writeValue(identifierName, identifier);
    }

    /** 读取Identifier */
    public String readIdentifier(){
        return readValue(identifierName, "");
    }

    /** 保存GroupId */
    public void setGroupId(String groupId){
        writeValue(groupIdName, groupId);
    }

    /** 读取GroupId */
    public String getGroupId(){
        return readValue(groupIdName, "");
    }

    public void resetQavsdkSig(){
        writeAccountType("");
        writeIdentifier("");
        writeSdkAppid("");
        writeUserSig("");
    }
}
