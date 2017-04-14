package yahier.exst.ui.DirectScreen.homeNotify.callback;

/**
 * Created by meteorshower on 16/4/18.
 */
public interface OnRoomMsgCallBack<T> {

    public void roomMsgError(int errorValue);

    public void roomMsgSuccess(T t);
}
