package yahier.exst.act.im.rong;//package com.stbl.stbl.act.im.rong;
//
//import io.rong.imkit.RongContext;
//import io.rong.imkit.RongIM;
//import io.rong.imkit.widget.provider.InputProvider;
//import io.rong.imlib.RongIMClient;
//import io.rong.imlib.model.Conversation;
//import io.rong.message.TextMessage;
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.provider.ContactsContract;
//import android.util.Log;
//import android.view.View;
//
//import com.stbl.stbl.R;
//import com.stbl.stbl.act.dongtai.DongtaiAddBusinessCardAct;
//import com.stbl.stbl.act.mine.MyCollectionActivity;
//import com.stbl.stbl.item.UserItem;
//import com.stbl.stbl.util.LogUtil;
//
///**
// * 收藏
// */
//public class WishProvider extends InputProvider.ExtendProvider {
//
//	HandlerThread mWorkThread;
//	Handler mUploadHandler;
//	private int REQUEST_CONTACT = 20;
//	RongContext context;
//
//	// Contacts
//
//	public WishProvider(RongContext context) {
//		super(context);
//		this.context = context;
//		mWorkThread = new HandlerThread("RongDemo");
//		mWorkThread.start();
//		mUploadHandler = new Handler(mWorkThread.getLooper());
//	}
//
//	/**
//	 * 设置展示的图标
//	 *
//	 * @param context
//	 * @return
//	 */
//	@Override
//	public Drawable obtainPluginDrawable(Context context) {
//		// R.drawable.de_contacts 通讯录图标
//		return context.getResources().getDrawable(R.drawable.icon_square);// 图标随便写的一个
//	}
//
//	/**
//	 * 设置图标下的title
//	 *
//	 * @param context
//	 * @return
//	 */
//	@Override
//	public CharSequence obtainPluginTitle(Context context) {
//		return "愿望";
//	}
//
//	/**
//	 * 进入收藏
//	 *
//	 * @param view
//	 */
//	@Override
//	public void onPluginClick(View view) {
//		Intent intent = new Intent(context, MyCollectionActivity.class);
//		startActivityForResult(intent, REQUEST_CONTACT);
//	}
//
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode != Activity.RESULT_OK)
//			return;
//		UserItem user = (UserItem) data.getSerializableExtra("data");
//		mUploadHandler.post(new MyRunnable(user));
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	class MyRunnable implements Runnable {
//
//		UserItem user;
//
//		public MyRunnable(UserItem user) {
//			this.user = user;
//		}
//
//		@Override
//		public void run() {
//
//		}
//	}
//
//}
