package yahier.exst.ui.DirectScreen.homeNotify.callback;

/**
 * Created by meteorshower on 16/4/12.
 */
public interface OnRoomGroupCallBack<T, S> {

    public void onRoomGroupError(int errorValue);

    public void onRoomGroupSuccess(String groupId, T t, S s);
}
