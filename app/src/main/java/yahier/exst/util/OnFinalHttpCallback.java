package yahier.exst.util;

public interface OnFinalHttpCallback {
	
	public void onHttpResponse(String methodName, String json, Object handle);
	public void onHttpError(String methodName, String msg, Object handle);
}
