package yahier.exst.act.im.rong;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.message.InformationNotificationMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiAddBusinessCardAct;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.LogUtil;

/**
 * 超级名片
 * 1128日进行了修改
 */
public class BusinessCardProvider implements IPluginModule {

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    private int REQUEST_CONTACT = 20;
    Context context;
    final String TAG = "BusinessCardProvider";

    public BusinessCardProvider(Context context) {
        // super(context);
        this.context = context;
        mWorkThread = new HandlerThread("RongDemo");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());
    }


    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.icon_im_card);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.business_card);
    }

    RongExtension rongExtension;

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.rongExtension = rongExtension;
        Intent intent = new Intent(context, DongtaiAddBusinessCardAct.class);
        //fragment.startActivityForResult(intent, REQUEST_CONTACT);  更换为以下的跳转方法
        rongExtension.startActivityForPluginResult(intent, REQUEST_CONTACT, this);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logE(TAG, "onActivityResult");
        if (resultCode != Activity.RESULT_OK)
            return;
        UserItem user = (UserItem) data.getSerializableExtra("data");
        mUploadHandler.post(new MyRunnable(user));
    }

    class MyRunnable implements Runnable {

        UserItem user;

        public MyRunnable(UserItem user) {
            this.user = user;
        }

        @Override
        public void run() {
            final String targetId = rongExtension.getTargetId();
            LogUtil.logE("nick:" + user.getNickname());
            BusinessCardMessage rongRedPacketMessage = BusinessCardMessage.obtain(user);
            RongIM.getInstance().getRongIMClient().sendMessage(rongExtension.getConversationType(), targetId, rongRedPacketMessage, null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                    Log.e("RongRedPacketProvider", "-----onError--" + errorCode);
                }

                @Override
                public void onSuccess(Integer integer) {
                    Log.e("RongRedPacketProvider", "-----onSuccess--" + integer);
                }
            });
        }
    }


}