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
import com.stbl.stbl.act.im.SendRedPackectAct;
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
public class RedPacketProvider  implements IPluginModule {

	HandlerThread mWorkThread;
	Handler mUploadHandler;
	private int REQUESTHongBaoCode = 21;
	private Context mContext;

	public RedPacketProvider(RongContext context) {
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
		return context.getResources().getDrawable(R.drawable.icon_im_redpacket);
	}

	/**
	 * 设置图标下的title
	 *
	 * @param context
	 * @return
	 */
	@Override
	public String obtainTitle(Context context) {
		return context.getString(R.string.red_packet);
	}

	/**
	 * click 事件，在这里做跳转
	 *
	 * @param view
	 */
//	@Override
//	public void onPluginClick(View view) {
//		LogUtil.logE("RongRedPacketProvider  onPluginClick");
//		if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
//			// 跳转到设置设置撒豆页面
//			String targetId = getCurrentConversation().getTargetId();
//			Intent intent = new Intent(mContext, SendRedPackectAct.class);
//			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.putExtra("userId",targetId);
//			startActivityForResult(intent, REQUESTHongBaoCode);
//		}
//
//	}


	RongExtension rongExtension;
	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		this.rongExtension = rongExtension;
		if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
			// 跳转到设置设置撒豆页面
			String targetId = rongExtension.getTargetId();
			Intent intent = new Intent(mContext, SendRedPackectAct.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("userId",targetId);
			fragment.startActivityForResult(intent, REQUESTHongBaoCode);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUESTHongBaoCode) {
			return;
		}
		if (resultCode != Activity.RESULT_OK)
			return;

		RedPacket redPackect = (RedPacket) data.getSerializableExtra("item");
		LogUtil.logE("onActivityResult " + redPackect.getHongbaoid());
		mUploadHandler.post(new MyRunnable(redPackect));
		onActivityResult(requestCode, resultCode, data);
	}

	class MyRunnable implements Runnable {

		RedPacket redPackect;

		public MyRunnable(RedPacket redPackect) {
			this.redPackect = redPackect;
		}

		@Override
		public void run() {
			String targetId = rongExtension.getTargetId();
			RedPackectMessage rongRedPacketMessage = RedPackectMessage.obtain(redPackect);
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