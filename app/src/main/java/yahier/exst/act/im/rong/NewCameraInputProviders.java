package yahier.exst.act.im.rong;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiAddBusinessCardAct;
import com.stbl.stbl.util.rong.BitmapUtils;

/**
 * Created by Bob_ge on 15/8/3.
 */
public class NewCameraInputProviders implements IPluginModule {

	HandlerThread mWorkThread;

	Handler mUploadHandler;

	@SuppressWarnings("unused")
	private RongContext mContext;
	private File photoFile;

	public NewCameraInputProviders(RongContext context) {
		this.mContext = context;
		// 来自融云demo
		mWorkThread = new HandlerThread("RongDemo");
		mWorkThread.start();
		mUploadHandler = new Handler(mWorkThread.getLooper());

	}

	@Override
	public Drawable obtainDrawable(Context arg0) {
		//return arg0.getResources().getDrawable(R.drawable.rc_ic_camera);
		return null;
	}

	@Override
	public String obtainTitle(Context arg0) {
		return arg0.getString(R.string.im_take_a_photo);
	}

//	@Override
//	public void onPluginClick(View arg0) {
//		// 点击跳转至拍照
//		photoFile = new File(Environment.getExternalStorageDirectory() + "/my_camera/" + UUID.randomUUID() + ".jpg");// 图片储存路径
//		if (!photoFile.getParentFile().exists()) {
//			photoFile.getParentFile().mkdirs();
//		}
//		Intent intent = new Intent();
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(intent, 1);
//	}

	RongExtension rongExtension;
	@Override
	public void onClick(Fragment fragment, RongExtension rongExtension) {
		this.rongExtension= rongExtension;
		photoFile = new File(Environment.getExternalStorageDirectory() + "/my_camera/" + UUID.randomUUID() + ".jpg");// 图片储存路径
		if (!photoFile.getParentFile().exists()) {
			photoFile.getParentFile().mkdirs();
		}
		Intent intent = new Intent();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		fragment.startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResult(requestCode, resultCode, data);

		Log.e("TAG", "---requestCode-" + requestCode + "---resultCode--" + resultCode);
		// 根据选择完毕的图片返回值，直接上传文件
		if (requestCode == 1) {// 拍照
			String localStrPath = photoFile.getPath();
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
				localStrPath = "file://" + localStrPath;
				Uri pathUri = Uri.parse(localStrPath);
				mUploadHandler.post(new MyRunnable(pathUri));
			}
		}
	}

	/**
	 * 用于显示文件的异步线程
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
							public void onError(Message message, RongIMClient.ErrorCode errorCode) {

							}

							@Override
							public void onSuccess(Message message) {

							}

							@Override
							public void onProgress(Message message, int i) {

							}
						});

		}
	}

}