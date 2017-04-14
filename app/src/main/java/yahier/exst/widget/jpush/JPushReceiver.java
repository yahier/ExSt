package yahier.exst.widget.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.stbl.stbl.act.dongtai.DongtaiNotify;
import com.stbl.stbl.act.login.StartActivity;
import com.stbl.stbl.api.pushServer.PushServerManager;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private static final int OPEN_DONGTAI_NEW_MSG = 2;//点击通知栏打开新消息提醒

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		LogUtil.logE(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.logE(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			LogUtil.logE(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			LogUtil.logE(TAG, "[JPushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			LogUtil.logE(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			LogUtil.logE(TAG, "[JPushReceiver] 用户点击打开了通知");
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
			LogUtil.logE(TAG,"用户点开了通知"+json);
			try {
				String value = json.replace("{\"stbl_ex\":\"{", "{\"stbl_ex\":{").replace("}\"}", "}}").replace("\\","");
				LogUtil.logE("JPushReceiver ", "用户点开了通知"+value);
				JSONObject jsonvalue = new JSONObject(value);
				LogUtil.logE("JPushReceiver ", "用户点开了通知"+jsonvalue.optString("stbl_ex", ""));

				JSONObject jsonObject = jsonvalue.optJSONObject("stbl_ex");
				if (jsonObject != null) {
					int modelType = jsonObject.optInt("pushtype", 0);
					clickNotify(context,modelType);
				}
//				clickNotify(context,type);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//打开自定义的Activity
//        	Intent i = new Intent(context, TestActivity.class);
//        	i.putExtras(bundle);
//        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[JPushReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to Other where
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

		Log.e("JPushReceiver", message + " --- " + extras);
		if(!StringUtils.isEmpty(extras)){
			try {
				Log.e("JPushReceiver", "extras not null");
				JSONObject json = new JSONObject(extras);
				if (null != json && json.length() > 0) {
					PushServerManager.getInstance().sendPushReceiver(extras);
				}

			}catch (Exception e){
				e.printStackTrace();
//				ToastUtil.showToast(MyApplication.getStblContext(), e.getMessage());
			}
		}else{
			Log.e("JPushReceiver", "extras is null");
		}
	}
	//点击通知栏后的处理
	private void clickNotify(Context context,int type){
		switch (type){
			case OPEN_DONGTAI_NEW_MSG:
				if (AppUtils.isTopActivity(context)){
					Intent intent = new Intent(context, DongtaiNotify.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}else{
					Intent intent = new Intent(context, StartActivity.class);
					intent.putExtra(KEY.StatusesGetNotification,true);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
				}
				break;
		}
	}
}
