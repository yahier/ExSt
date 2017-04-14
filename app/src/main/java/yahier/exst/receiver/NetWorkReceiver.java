package yahier.exst.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stbl.stbl.util.NetUtil;
import com.stbl.stbl.util.SharedDevice;

/**
 * Created by lenovo on 2016/4/20.
 */
public class NetWorkReceiver extends BroadcastReceiver {
    String netTypeValue = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            netTypeValue = NetUtil.getNetType(context);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        SharedDevice.putNet(context, netTypeValue);
    }

}