package yahier.exst.api.imChatPush;

import com.stbl.stbl.utils.StringUtils;

import org.json.JSONObject;

/**
 * Created by meteorshower on 16/4/12.
 */
public class ImChatPushServer {

    private final String modelType = "modelType";
    private ImDirectScreenPushServer imDirectScreenPushServer = null;

    public ImChatPushServer(){

    }

    public void initChatPushData(String vlaue){
        try{
            if(StringUtils.isEmpty(vlaue))
                return;

            JSONObject json = new JSONObject(vlaue);
            int modelTypeValue = json.optInt("modelType", -1);
            switch(modelTypeValue){
                case 1://直播消息分发
                    if(imDirectScreenPushServer == null)
                        imDirectScreenPushServer = new ImDirectScreenPushServer();
                    imDirectScreenPushServer.initDirectScreenPushData(json.optJSONObject("result"), json.optInt("roomId", 0),
                            json.optString("roomGroupId", ""));
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
