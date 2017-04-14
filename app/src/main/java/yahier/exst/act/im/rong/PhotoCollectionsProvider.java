package yahier.exst.act.im.rong;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.rong.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

public class PhotoCollectionsProvider  implements IPluginModule {
	final String tag = "PhotoCollectionsProvider";
	HandlerThread mWorkThread;

	Handler mUploadHandler;

	private RongContext mContext;
	final int requestCode = 86;

	public PhotoCollectionsProvider(RongContext context) {
		this.mContext = context;
		mWorkThread = new HandlerThread("RongDemo");
		mWorkThread.start();
		mUploadHandler = new Handler(mWorkThread.getLooper());


	}

	@Override
	public Drawable obtainDrawable(Context arg0) {
		// TODO Auto-generated method stub
		return arg0.getResources().getDrawable(R.drawable.icon_im_album);
	}

	@Override
	public String obtainTitle(Context arg0) {
		return mContext.getString(R.string.ablum);
	}

	RongExtension rongExtension;
	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		this.rongExtension= rongExtension;
		//test9Grid();

		Intent intent = new Intent(mContext, AlbumActivity.class);
		//参数需要修改
		//intent.putExtra("conversation",rongExtension.getConversationType());
		fragment.startActivityForResult(intent, requestCode);
	}




//	//接收数据有大难题
//	void test9Grid(){
//		Intent intent = new Intent(mContext, AlbumActivity.class);
//		intent.putExtra("conversation",getCurrentConversation());
//		startActivityForResult(intent, requestCode);
//	}



	@Override
	public void onActivityResult(int requestcode, int resultCode, Intent data) {
		// 根据选择完毕的图片返回值，直接上传文件
		if (requestcode == requestCode && data != null) {
			// LogUtil.logE(tag + " onActivityResult");
			try {
				Uri originalUri = data.getData();
				if (originalUri != null) {
					String[] proj = {MediaStore.Images.Media.DATA};
					CursorLoader cursorLoader = new CursorLoader(mContext, originalUri, proj, null, null, null);
					Cursor cursor = cursorLoader.loadInBackground();
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					// 最后根据索引值获取图片路径
					String path = cursor.getString(column_index);// 这才是我们要的路径
					path = "file://" + path;
					LogUtil.logE("onActivityResult path——" + path);
					Uri pathUri = Uri.parse(path);
					// mUploadHandler.post(new MyRunnable(pathUri));
					mUploadHandler.post(new MyRunnable(pathUri));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		// file:///storage/emulated/0/revoeye/4c8dafef-2e5e-447f-9c7f-2c3071683a84.jpeg
		onActivityResult(requestCode, resultCode, data);
	}

	// 原本的回调方法
	public void onActivityResultOld(int requestcode, int resultCode, Intent data) {

		// 根据选择完毕的图片返回值，直接上传文件
		if (requestcode == requestCode && data != null) {
			LogUtil.logE(tag + " onActivityResult");
			ArrayList<String> pathList = data.getStringArrayListExtra("data");
			if (pathList != null && pathList.size() > 0) {
				int intSize = pathList.size();
				for (int i = 0; i <= intSize - 1; i++) {
					String localStrPath = pathList.get(i);
					// localStrPath = "file:/" + localStrPath;
					byte[] compressBitmap = BitmapUtils.compressBitmap(480 * 480, localStrPath);
					if (null != compressBitmap) {
						Bitmap bmPhoto = BitmapUtils.Bytes2Bimap(compressBitmap);
						if (null != bmPhoto) {
							String strTempPhotoPath;
							try {
								strTempPhotoPath = BitmapUtils.saveFile(bmPhoto, UUID.randomUUID() + ".jpeg");
								if (bmPhoto != null) {
									bmPhoto.recycle();
									bmPhoto = null;
								}
								if (null != strTempPhotoPath && !"".equals(strTempPhotoPath)) {
									localStrPath = strTempPhotoPath;
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					localStrPath = "file://" + localStrPath;
					Uri pathUri = Uri.parse(localStrPath);
					mUploadHandler.post(new MyRunnable(pathUri));
				}
			}
		}
		onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 用于显示文件的异步线程
	 *
	 * @ClassName: MyRunnable
	 * @Description: 用于显示文件的异步线程
	 *
	 */
	class MyRunnable implements Runnable {

		Uri mUri;

		public MyRunnable(Uri uri) {
			mUri = uri;
		}

		@Override
		public void run() {

			// 封装image类型的IM消息
			final ImageMessage content = ImageMessage.obtain(mUri, mUri);

			if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null)
				RongIM.getInstance().getRongIMClient()
						.sendImageMessage(rongExtension.getConversationType(), rongExtension.getTargetId(), content, null, null, new RongIMClient.SendImageMessageCallback() {
							@Override
							public void onAttached(Message message) {

							}

							@Override
							public void onError(Message message, RongIMClient.ErrorCode code) {

							}

							@Override
							public void onSuccess(Message message) {

							}

							@Override
							public void onProgress(Message message, int progress) {

							}
						});

		}
	}

}