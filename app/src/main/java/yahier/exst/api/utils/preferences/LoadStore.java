package yahier.exst.api.utils.preferences;

/**
 * Created by meteorshower on 16/3/22.
 */
public class LoadStore extends BasePreferences {

    private final static String SHARE_NAME = "loadstore";

    public LoadStore() {
        super(SHARE_NAME);
    }

    private static LoadStore loadStore = null;

    public static LoadStore getInstance(){
        if(loadStore == null)
            loadStore = new LoadStore();
        return loadStore;
    }

    /** 设置房间ID */
    public void setRoomId(int roomId){
        writeValue("roomId", roomId);
    }

    /** 房间ID */
    public int getRoomId(){
        return readValue("roomId", 0);
    }

    /** 设置房主ID */
    public void setRoomOwner(long ownerId){
        writeValue("ownerId", ownerId);
    }

    /** 房主ID */
    public long getRoomOwner(){
        return readLongValue("ownerId", 0);
    }

    /** 设置嘉宾ID */
    public void setRoomGuest(long guestId){
        writeValue("guestId", guestId);
    }

    /** 嘉宾ID */
    public long getRoomGuest(){
        return readLongValue("guestId", 0);
    }
}
