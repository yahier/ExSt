package yahier.exst.act.im.rong;

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
import com.stbl.stbl.act.dongtai.EventStatusesType;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.LogUtil;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.MessageContent;

/**
 * 收藏.发送动态和商品
 */
public class CollectProvider implements IPluginModule {

	HandlerThread mWorkThread;
	Handler mUploadHandler;
	private int REQUEST_CONTACT = 20;
	Context context;

	public final static int resultGoodsOk = 10001;
	public final static int resultStatusesOk = 10002;


	public CollectProvider(Context context) {
		this.context = context;
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
		return context.getResources().getDrawable(R.drawable.icon_im_collection);
	}

	/**
	 * 设置图标下的title
	 *
	 * @param context
	 * @return
	 */
	@Override
	public String obtainTitle(Context context) {
		return context.getString(R.string.me_my_collection);
	}


	RongExtension rongExtension;
	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		this.rongExtension = rongExtension;
		Intent intent = new Intent(context, MyCollectionActivity.class);
		intent.putExtra("mode",MyCollectionActivity.mode_im_choose);
		rongExtension.startActivityForPluginResult(intent, REQUEST_CONTACT,this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case resultGoodsOk:
				Goods good = (Goods) data.getSerializableExtra("data");
				LogUtil.logE("name:" + good.getGoodsname());
				mUploadHandler.post(new MyRunnable(good));
				break;
			case resultStatusesOk:
				StatusesCollect statusesCollect = (StatusesCollect) data.getSerializableExtra("data");
				LogUtil.logE("name:" + statusesCollect.getStatuses().getTitle());
				mUploadHandler.post(new MyRunnable(statusesCollect));
				break;
		}
	}

	class MyRunnable implements Runnable {

		Goods good;
		StatusesCollect statusesCollect;

		public MyRunnable(Goods good) {
			this.good = good;
		}

		public MyRunnable(StatusesCollect statusesCollect) {
			this.statusesCollect = statusesCollect;
		}

		@Override
		public void run() {
			final String targetId = rongExtension.getTargetId();
			MessageContent mesage = null;
			if (good != null) {
				mesage = GoodsMessage.obtain(good);
			}
			if (statusesCollect != null) {
				mesage = StatusesMessage.obtain(statusesCollect);
			}
			RongIM.getInstance().getRongIMClient().sendMessage(rongExtension.getConversationType(), targetId, mesage, null, null, new RongIMClient.SendMessageCallback() {
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