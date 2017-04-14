package yahier.exst.act.im.rong;

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
import com.stbl.stbl.act.im.CastBeanAct;
import com.stbl.stbl.item.im.RedPacket;
import com.stbl.stbl.util.LogUtil;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;

/**
 * 参照 ContactsProvider
 *
 * @author yahier
 */
public class CastBeanProvider implements IPluginModule {

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    private int REQUESTHongBaoCode = 21;
    private Context mContext;

    public CastBeanProvider(RongContext context) {
        this.mContext = context;
        mWorkThread = new HandlerThread("RongDemo");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());
    }

    /**
     * 设置展示的图标
     *
     * @param context
     * @return
     */
    @Override
    public Drawable obtainDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.icon_im_sadou);
    }

    /**
     * 设置图标下的title
     *
     * @param context
     * @return
     */
    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.cast_bean);
    }



    RongExtension rongExtension;

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        this.rongExtension= rongExtension;
        Intent intent = new Intent(mContext, DongtaiAddBusinessCardAct.class);
        fragment.startActivityForResult(intent, REQUESTHongBaoCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUESTHongBaoCode) {
            return;
        }
        if (resultCode != Activity.RESULT_OK)
            return;

        if (data != null) {
            RedPacket redPackect = (RedPacket) data.getSerializableExtra("item");
            LogUtil.logE("onActivityResult " + redPackect.getHongbaoid());
            mUploadHandler.post(new MyRunnable(redPackect));
        }
        onActivityResult(requestCode, resultCode, data);
    }

    class MyRunnable implements Runnable {

        RedPacket redPackect;

        public MyRunnable(RedPacket redPackect) {
            this.redPackect = redPackect;
        }

        @Override
        public void run() {
            final String targetId = rongExtension.getTargetId();
            CastBeanMessage rongRedPacketMessage = CastBeanMessage.obtain(redPackect);
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