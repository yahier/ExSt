package yahier.exst.ui.DirectScreen.homeNotify.callback;

import com.stbl.stbl.api.utils.Logger;
import com.tencent.TIMConnListener;

/**
 * Created by meteorshower on 16/4/10.
 *
 * 网络监听
 */
public class MsgConnectionListener implements TIMConnListener {

    private Logger logger = new Logger(this.getClass().getSimpleName());

    @Override
    public void onWifiNeedAuth(String s) {
        logger.e(" --------------------------------------------------------------- ");
        logger.e(" -------------------- Connection onWifiNeedAuth "+s+" --------------------- ");
        logger.e(" --------------------------------------------------------------- ");
    }

    @Override
    public void onConnected() {
        logger.e(" --------------------------------------------------------------- ");
        logger.e(" -------------------- Connection 连接建立 --------------------- ");
        logger.e(" --------------------------------------------------------------- ");
    }

    @Override
    public void onDisconnected(int i, String s) {
        logger.e(" --------------------------------------------------------------- ");
        logger.e(" -------------------- Connection 连接断开 : "+ i +" ---- "+ s +" --------------------- ");
        logger.e(" --------------------------------------------------------------- ");
    }
}
