package yahier.exst.utils;

import android.content.Context;
import android.content.Intent;

import com.stbl.stbl.act.home.mall.MallGoodsSnapshotAct;
import com.stbl.stbl.act.home.mall.MallRefundCustomerActivity;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.ui.DirectScreen.DirectScreenControlActivity;
import com.stbl.stbl.ui.DirectScreen.OpenNewDirectScreenActivity;
import com.stbl.stbl.ui.DirectScreen.service.FloatingDirectScreenService;

/**
 * @author meteorshower
 * Activity 跳转管理器
 */
public class UISkipUtils {

	/** 启动开启我的直播 */
	public static void startOpenNewDirectScreenActivity(Context context){
		Intent intent = new Intent(context, OpenNewDirectScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/** 启动直播主页 */
	public static void startMainDirectScreenActivity(Context context, int roomId){
		startMainDirectScreenActivity(context, roomId, false);
	}

	/** 启动直播主页 */
	public static void startMainDirectScreenActivity(Context context, int roomId, boolean firstJoinRoom){
		startMainDirectScreenActivity(context, roomId, firstJoinRoom, false);
	}

	/** 启动直播主页 */
	public static void startMainDirectScreenActivity(Context context, int roomId, boolean firstJoinRoom, boolean isGuest){
		Intent intent = new Intent(context, DirectScreenControlActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("roomId", roomId);
		intent.putExtra("firstJoinRoom", firstJoinRoom);
		intent.putExtra("isGuest", isGuest);
		context.startActivity(intent);
	}

	/** 启动直播悬浮窗 */
	public static void startFloatingDirectScreenService(Context context){
		Intent intent = new Intent(context, FloatingDirectScreenService.class);
		context.startService(intent);
	}

	/** 关闭直播悬浮窗 */
	public static void stopFloatingDirectScreenService(Context context){
		Intent intent = new Intent(context, FloatingDirectScreenService.class);
		context.stopService(intent);
	}

	/** 启动直播举报界面 */
	public static void startReportDirectScreenActivity(Context context, String userid){
		Intent intent = new Intent(context, ReportStatusesOrUserAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("userid", userid);
		context.startActivity(intent);
	}

	/** 启动退款/售后 */
	public static void startMallRefundCustomerActivity(Context context){
		Intent intent = new Intent(context, MallRefundCustomerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/** 启动商品快照 */
	public static void startMallGoodsSnapshotAct(Context context, long goodsid){
		Intent intent = new Intent(context, MallGoodsSnapshotAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("goodsid", goodsid);
		context.startActivity(intent);
	}

}
