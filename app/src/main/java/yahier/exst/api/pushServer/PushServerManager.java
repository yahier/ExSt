package yahier.exst.api.pushServer;

import com.stbl.stbl.util.LogUtil;

import org.json.JSONObject;

/**
 * Created by meteorshower on 16/3/10.
 */
public class PushServerManager {

    private final String MODEL_TYPE_VALUE = "pushtype";//推送类型
    private final String PUSH_MODEL_TIME = "pushtime";//推送时间
    private final String PUSH_NEXT_CONTENT = "content";//内容
    private final String PUSH_TRAGET_ID = "pushtargetid";//pushtargetid
    /**直播类型*/
    private final int DISTRIBUTE_PUSH_DIRECT_SCREEN = 1;
    /**动态新消息*/
    private final int PUSH_DONGTAI_NEW_MESSAGE = 2;

    private static PushServerManager pushServerManager = null;

    public static PushServerManager getInstance(){
        if(pushServerManager == null)
            pushServerManager = new PushServerManager();
        return pushServerManager;
    }

    public void sendPushReceiver(String extras) {

        try{
            String value = extras.replace("{\"stbl_ex\":\"{", "{\"stbl_ex\":{").replace("}\"}", "}}").replace("\\","");
            LogUtil.logE("JPushReceiver ", value);
            JSONObject json = new JSONObject(value);
            LogUtil.logE("JPushReceiver ", json.optString("stbl_ex", ""));

//            if(!json.isNull("stbl_ex")){
//                JSONArray jsonArr = json.optJSONArray("stbl_ex");
//            }

            pushModeDistribute(json.optJSONObject("stbl_ex"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** 类型分发 */
    private void pushModeDistribute(JSONObject json) throws Exception{

        int modelType = json.optInt(MODEL_TYPE_VALUE, -1);

        LogUtil.logE("JPushReceiver", "Model Type Vlue : "+ modelType);
        switch(modelType){
            case DISTRIBUTE_PUSH_DIRECT_SCREEN://直播
                new DirectScreenPushServer(json.optJSONObject(PUSH_NEXT_CONTENT),json.optLong(PUSH_TRAGET_ID, 0));
                break;
            case PUSH_DONGTAI_NEW_MESSAGE: //动态新消息
                new DongtaiPushServer(json.optJSONObject(PUSH_NEXT_CONTENT),json.optLong(PUSH_TRAGET_ID, 0));
                break;
        }
    }
}
