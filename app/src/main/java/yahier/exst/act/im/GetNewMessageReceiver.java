package yahier.exst.act.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stbl.stbl.util.LogUtil;

/**
 * Created by lenovo on 2016/4/6.
 * 用manifest注册的监听消息
 */
public class GetNewMessageReceiver extends BroadcastReceiver {
    final String tag = getClass().getSimpleName();

    @Override
    public void onReceive(Context arg0, Intent intent) {

        switch (intent.getAction()) {
            case MessageMainAct.actionGetOrSentNewMessage_RefreshConversationList:
                //LogUtil.logE("GetNewMessageReceiver onReceive 接收 刷新列表");
                if (mListener != null){
                    mListener.newMessage();
                }
                else
                    LogUtil.logE(tag, "mListener is null");
                break;
            case MessageMainAct.actionGetNewApply:
               // LogUtil.logE("GetNewMessageReceiver onReceive 新的申请来了");
                if (mListener != null) {
                    mListener.newApply();
                }
                break;
        }
    }

    public void setOnReceiverListener(ReceiverListener listener) {
        //LogUtil.logE(tag,"setOnReceiverListener "+listener);
        this.mListener = listener;
    }

    static ReceiverListener mListener;

    public interface ReceiverListener {
        void newMessage();

        void newApply();
    }
}