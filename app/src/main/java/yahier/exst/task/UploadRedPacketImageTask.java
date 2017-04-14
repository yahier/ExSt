package yahier.exst.task;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/26.
 */

public class UploadRedPacketImageTask {

    private static final int WHAT_ONE_IMAGE_UPLOAD = 1;
    private static final int WHAT_ALL_IMAGE_UPLOAD = 2;

    private volatile Looper mLooper;
    private volatile MsgHandler mHandler;

    public UploadRedPacketImageTask() {
        HandlerThread thread = new HandlerThread("HandlerThread[UploadRedPacketImageTask]");
        thread.start();

        mLooper = thread.getLooper();
        mHandler = new MsgHandler(mLooper);
    }

    public void startTask(ArrayList<String> list) {
        for (String path : list) {
            Message msg = mHandler.obtainMessage();
            msg.what = WHAT_ONE_IMAGE_UPLOAD;
            msg.obj = path;
            mHandler.sendMessage(msg);
        }
    }

    public void allImageUploadFinish() {
        Message msg = mHandler.obtainMessage();
        msg.what = WHAT_ALL_IMAGE_UPLOAD;
        mHandler.sendMessage(msg);
    }

    public void quit() {
        mLooper.quit();
    }

    /**
     * 单线程队列
     */
    private final class MsgHandler extends Handler {

        private HashMap<String, String> mDoneMap; //上传成功

        public MsgHandler(Looper looper) {
            super(looper);
            mDoneMap = new HashMap<>();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ALL_IMAGE_UPLOAD:
                    Intent intent = new Intent(ACTION.ALL_IMAGE_UPLOAD_FINISH);
                    LocalBroadcastHelper.getInstance().send(intent);
                    break;
                case WHAT_ONE_IMAGE_UPLOAD:
                    String path = (String) msg.obj;
                    if (!mDoneMap.containsKey(path)) {
                        uploadImage(path);
                    }
                    break;
            }
        }

        private void uploadImage(String path) {
            try {
                File temp = BitmapUtil.createUploadTempFile(new File(path), "publish_shopping_circle_temp");
                JSONObject json = new JSONObject();
                json.put("type", "adsys/square");
                HttpResponse response = OkHttpHelper.getInstance().uploadImage(Method.commonUpload, json, temp);
                if (response.error != null) {
                    return;
                }
                ImgUrl imgUrl = JSON.parseObject(response.result, ImgUrl.class);
                String fileName = imgUrl.getFilename();
                mDoneMap.put(path, fileName);
                LogUtil.logE("upload image success");
                Intent intent = new Intent(ACTION.ONE_IMAGE_UPLOAD_SUCCESS);
                intent.putExtra(KEY.SDCARD_FILE_PATH, path);
                intent.putExtra(KEY.SERVER_FILE_PATH, fileName);
                LocalBroadcastHelper.getInstance().send(intent);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
