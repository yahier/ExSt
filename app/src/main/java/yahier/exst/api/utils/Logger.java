package yahier.exst.api.utils;

import java.io.Serializable;

import android.util.Log;


/**
 * @author meteorshower
 * Log信息打印  
 * canWriteLog true 打印状态
 */
public class Logger implements Serializable{

	private static final long serialVersionUID = -8676912171515942481L;
	private String tag;
	
	public Logger(String tag){
		this.tag = tag;
	}
	
	public void setTag(String tag){
		this.tag = tag;
	}
	
	public void v(String msg){
		if(isCanWriteLog())
			Log.v(tag, msg);
	}
	
	public void v(String msg,Throwable tr){
		if(isCanWriteLog())
			Log.v(tag, msg, tr);
	}
	
	public void i(String msg){
		if (isCanWriteLog()) 
			Log.i(tag, msg);
	}
	
	public void test_i(String title,String msg){
		i(" ----------------------------------------------------- ");
		i(title+msg);
		i(" ----------------------------------------------------- ");
	}
	
	public void i(String msg,Throwable tr){
		if(isCanWriteLog())
			Log.i(msg, msg, tr);
	}
	
	public void d(String msg){
		if (isCanWriteLog()) 
			Log.d(tag, msg);
	}
	
	public void d(String msg,Throwable tr){
		if (isCanWriteLog()) 
			Log.d(tag, msg, tr);
	}
	
	public void e(String msg){
		if(isCanWriteLog())
			Log.e(tag, msg);
	}
	
	public void e(String msg,Throwable tr){
		if(isCanWriteLog())
			Log.e(tag, msg, tr);
	}
	
	public void w(Throwable tr){
		if (isCanWriteLog()) 
			Log.w(tag, tr);
	}
	
	public void w(String msg){
		if(isCanWriteLog())
			Log.w(tag, msg);
	}
	
	public void w(String msg,Throwable tr){
		if(isCanWriteLog())
			Log.w(tag, msg, tr);
	}
	
	private static boolean canWriteLog = true;

	public static boolean isCanWriteLog() {
		return canWriteLog;
	}

	public static void setCanWriteLog(boolean canWriteLog) {
		Logger.canWriteLog = canWriteLog;
	}
	
}
