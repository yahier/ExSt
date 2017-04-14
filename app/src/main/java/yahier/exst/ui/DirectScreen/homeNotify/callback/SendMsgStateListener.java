package yahier.exst.ui.DirectScreen.homeNotify.callback;

import com.tencent.TIMMessage;

/**
 * Created by meteorshower on 16/4/10.
 */
public interface SendMsgStateListener {

    public void sendMsgError(int errorValue);

    public void sendMsgSuccess(TIMMessage timMessage);
}
