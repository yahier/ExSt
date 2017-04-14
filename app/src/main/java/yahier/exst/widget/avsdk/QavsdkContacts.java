package yahier.exst.widget.avsdk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.tencent.av.sdk.AVConstants;

public class QavsdkContacts {
	private static final String TAG = "Util";
	private static final String PACKAGE = "com.tencent.avsdk";
	
	public static String inputYuvFilePath = "/sdcard/123.yuv";
	public static int yuvWide = 320;
	public static int yuvHigh = 240;
	public static int yuvFormat = 100;
	public static String outputYuvFilePath = "/sdcard/123";
	public volatile static long auth_bits = 0;
	
	public static final int DEMO_ERROR_BASE = -99999999;
	/** 空指针 */
	public static final int DEMO_ERROR_NULL_POINTER = DEMO_ERROR_BASE + 1;
	public static final int AUTO_EXIT_ROOM = 101;

	public static final String ACTION_START_CONTEXT_COMPLETE = PACKAGE
			+ ".ACTION_START_CONTEXT_COMPLETE";
	public static final String ACTION_CLOSE_CONTEXT_COMPLETE = PACKAGE
			+ ".ACTION_CLOSE_CONTEXT_COMPLETE";
	public static final String ACTION_ROOM_CREATE_COMPLETE = PACKAGE
			+ ".ACTION_ROOM_CREATE_COMPLETE";
	public static final String ACTION_CLOSE_ROOM_COMPLETE = PACKAGE
			+ ".ACTION_CLOSE_ROOM_COMPLETE";
	public static final String ACTION_SURFACE_CREATED = PACKAGE
			+ ".ACTION_SURFACE_CREATED";
	public static final String ACTION_MEMBER_CHANGE = PACKAGE
			+ ".ACTION_MEMBER_CHANGE";
	public static final String ACTION_VIDEO_SHOW = PACKAGE
			+ ".ACTION_VIDEO_SHOW";
	public static final String ACTION_VIDEO_CLOSE = PACKAGE
			+ ".ACTION_VIDEO_CLOSE";
	public static final String ACTION_ENABLE_CAMERA_COMPLETE = PACKAGE
			+ ".ACTION_ENABLE_CAMERA_COMPLETE";
	public static final String ACTION_SWITCH_CAMERA_COMPLETE = PACKAGE
			+ ".ACTION_SWITCH_CAMERA_COMPLETE";
	public static final String ACTION_OUTPUT_MODE_CHANGE = PACKAGE
			+ ".ACTION_OUTPUT_MODE_CHANGE";
	public static final String ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE = PACKAGE
			+ ".ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE";

	public static final String ACTION_CHANGE_AUTHRITY = PACKAGE
			+ ".ACTION_CHANGE_AUTHRITY";
	public static final String EXTRA_RELATION_ID = "relationId";
	public static final String EXTRA_AV_ERROR_RESULT = "av_error_result";
	public static final String EXTRA_VIDEO_SRC_TYPE = "videoSrcType";
	public static final String EXTRA_IS_ENABLE = "isEnable";
	public static final String EXTRA_IS_FRONT = "isFront";
	public static final String EXTRA_IDENTIFIER = "identifier";	
	public static final String EXTRA_SELF_IDENTIFIER = "selfIdentifier";	
	public static final String EXTRA_ROOM_ID = "roomId";
	public static final String EXTRA_IS_VIDEO = "isVideo";

	public static final String ROOM_ROLE_VALUE = "";//流程角色名

	public static int getScreenWidth(Context c) {
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context c) {
		DisplayMetrics dm = c.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static String getRootDir(Context context, String subDir) {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"/tencent/com/tencent/mobileqq/avsdk/" + subDir);// context.getExternalFilesDir(null);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return dir.toString();
	}

	public static ProgressDialog newProgressDialog(Context context, int titleId) {
		ProgressDialog result = new ProgressDialog(context);
		result.setTitle(titleId);
		result.setIndeterminate(true);
		result.setCancelable(false);


		return result;
	}

	/** 
     * 网络是否正常 
     * @param context Context 
     * @return true 表示网络可用 
     */  
    public static int getNetWorkType(Context context) {        
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
  
        if (networkInfo != null && networkInfo.isConnected()) {  
            String type = networkInfo.getTypeName();  
  
            if (type.equalsIgnoreCase("WIFI")) {  
            	return AVConstants.NETTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {  
    			NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 		
    			if(mobileInfo != null) {
    	            switch (mobileInfo.getType()) {  
    	            case ConnectivityManager.TYPE_MOBILE:// 手机网络  
    	                switch (mobileInfo.getSubtype()) {  
    	                case TelephonyManager.NETWORK_TYPE_UMTS:
    	                case TelephonyManager.NETWORK_TYPE_EVDO_0:
    	                case TelephonyManager.NETWORK_TYPE_EVDO_A:
    	                case TelephonyManager.NETWORK_TYPE_HSDPA:
    	                case TelephonyManager.NETWORK_TYPE_HSUPA:
    	                case TelephonyManager.NETWORK_TYPE_HSPA:
    	                case TelephonyManager.NETWORK_TYPE_EVDO_B:
    	                case TelephonyManager.NETWORK_TYPE_EHRPD:
    	                case TelephonyManager.NETWORK_TYPE_HSPAP:
    		            	return AVConstants.NETTYPE_3G;	                	 
    	                case TelephonyManager.NETWORK_TYPE_CDMA:	                    
    	                case TelephonyManager.NETWORK_TYPE_GPRS:  
    	                case TelephonyManager.NETWORK_TYPE_EDGE:  
	                    case TelephonyManager.NETWORK_TYPE_1xRTT: 
	                    case TelephonyManager.NETWORK_TYPE_IDEN: 	                	
    		            	return AVConstants.NETTYPE_2G;	                	
    	                case TelephonyManager.NETWORK_TYPE_LTE:
    		            	return AVConstants.NETTYPE_4G;	                	
    	                default:  
    		            	return AVConstants.NETTYPE_NONE;		                	
    	                } 
    	            }
    			}
            }  
        } 
        
    	return AVConstants.NETTYPE_NONE;	
    }  
    
    /*
     * 获取网络类型
     */
    public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected())   
            {  
                // 当前网络是连接的  
                if (info.getState() == NetworkInfo.State.CONNECTED)   
                {  
                    // 当前所连接的网络可用  
                    return true;  
                }  
            }  
        }  
        return false;  
    }    

	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd.HH");
		String date = format.format(new Date(System.currentTimeMillis()));
		return date;// 2012年10月03日 23:41:31
	}

	public static String getDateEN() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		String date1 = format1.format(new Date(System.currentTimeMillis()));
		return date1;// 2012-10-03 23:41:31
	}

}
