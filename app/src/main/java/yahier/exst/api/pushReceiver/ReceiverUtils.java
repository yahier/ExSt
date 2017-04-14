package yahier.exst.api.pushReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.common.MyApplication;

public class ReceiverUtils {
	
	private static Logger logger = new Logger("ReceiverUtils");
	private final static String RECEIVER_ACTION = "com.stbl.stbl.action.receiver";
	private final static String ACTION_TYPE_NAME = "ReceiverTypeName";
	private final static String ACTION_BUNDLE = "bundle";
	
	//请在以下位置声明不同类型的ReceiverType 如： public final static int LOGIN_SUCCESS = 1;//登陆成功
	public final static int DIRECT_PUSH_SERVER_TYPE = 1;//直播内容
	public final static int DIRECT_GUEST_CHOOSE_TYPE = 2;//嘉宾邀请点击
	public final static int DIRECT_UPDATE_TITLE_TYPE = 3;//更新直播间标题
	public final static int DIRECT_UPDATE_ONLINE_NUM_TYPE = 4;//直播间在线人数
	public final static int CLOSE_MICEPHONE_TYPE = 5;//下麦
	public final static int OPEN_MICEPHONE_TYPE = 6;//上麦
	public final static int DIRECT_OWNER_CLICK_TYPE = 7;//直播房主头像事件
	public final static int TAB_HOME_IN_ROOM = 0x1001;//直播间接到直播邀请，结束服务，到tabhome执行下一步
	public final static int DIRECT_IMSDK_SERVER_TYPE = 8;//直播内容（Im通信）

	public final static int PUSH_TYPE_DONGTAI_NEW_MESSAGE = 0x2000;//动态新消息


	static class MessageObserverReceiver extends BroadcastReceiver{
		
		public MessageObserverReceiver(){
			MyApplication.getStblContext().registerReceiver(this, new IntentFilter(RECEIVER_ACTION));
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			int receiverType = intent.getIntExtra(ACTION_TYPE_NAME, 0);
			Bundle bundle = intent.getBundleExtra(ACTION_BUNDLE);
			for(MessageReceiver receiver : receiverList){
				receiver.onMessage(receiverType, bundle);
			}
		}
		
	}
	
	private static MessageObserverReceiver broadCast = new MessageObserverReceiver();
	
	/** 发送广播 */
	public static void sendReceiver(int receiverType,Bundle bundle){
		if(!receiverList.isEmpty()){
			logger.i("SendReceiver : "+String.valueOf(receiverType));
			Intent intent = new Intent(RECEIVER_ACTION);
			intent.putExtra(ACTION_TYPE_NAME, receiverType);
			intent.putExtra(ACTION_BUNDLE, bundle);
			MyApplication.getStblContext().sendBroadcast(intent);
		}
	}
	
	/** 添加广播监听器 */
	public static void addReceiver(MessageReceiver receiver){
		receiverList.add(receiver);
		logger.i(" --- addReceiver --- ");
	}
	
	/** 移除广播监听器 */
	public static void removeReceiver(MessageReceiver receiver){
		receiverList.remove(receiver);
		logger.i(" --- removeReceiver --- ");
	}
	
	public interface MessageReceiver{
		
		public void onMessage(int receiverType, Bundle bundle);
	}
	
	private static List<MessageReceiver> receiverList;
	
	static{
		receiverList = Collections.synchronizedList(new ArrayList<MessageReceiver>());
	}
}
