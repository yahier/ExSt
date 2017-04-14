package yahier.exst.api.http;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author meteorshower
 *
 *	OK Http封装类
 */
public abstract class AsyncOkHttpRequest {

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private OkHttpClient httpClicent = new OkHttpClient();
	private Request httpRequest;
	private Call httpCall;

	private static final int REQUEST_TIME_OUT = 30;//从30*1000 改成30

	public abstract String getReuqestUrl();

	public abstract void onSuccess(Response response,String methodName);

	public abstract void onFailture(Request request, IOException error,String methodName );

	public AsyncOkHttpRequest() {
		httpClicent.setConnectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
		httpClicent.setWriteTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
		httpClicent.setReadTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
	}

	//注册Get请求
	protected void registerGetRequest(final String methodName,Request request){
//		httpRequest = new Request.Builder()
//		.url(AsyncOkHttpRequest.this.getReuqestUrl())
//		.headers(headers)
//		.build();
		httpCall = httpClicent.newCall(request);
		httpCall.enqueue(new AsyncResponseListener(methodName));
	}


	//注册Post请求
	protected void registerPostReqeust(String methodName,RequestBody requestBody){

		httpRequest = new Request.Builder()
				.url(AsyncOkHttpRequest.this.getReuqestUrl())
		.post(requestBody)
		.build();

		httpCall = httpClicent.newCall(httpRequest);
		httpCall.enqueue(new AsyncResponseListener(methodName));
	}

	//注册Post请求
	protected void registerPostRequest(String methodName, Headers headers, RequestBody requestBody){
		httpRequest = new Request.Builder()
		.url(AsyncOkHttpRequest.this.getReuqestUrl())
		.headers(headers)
		.post(requestBody)
		.build();
		//验证https
//		httpClicent.setHostnameVerifier(new HostnameVerifier() {
//			@Override
//			public boolean verify(String hostname, SSLSession session) {
//				boolean isPassed = HttpsURLConnection.getDefaultHostnameVerifier().verify(Config.reserveHostIp, session);
//				//LogUtil.logE(tag, isPassed + "");
//				return isPassed;
//			}
//		});
		httpCall = httpClicent.newCall(httpRequest);
		httpCall.enqueue(new AsyncResponseListener(methodName));
	}

	//注册取消请求
	protected void registerCancelRequest(){
		executor.schedule(new Runnable() {
		      @Override
		      public void run() {
		    	  if(httpCall != null && !httpCall.isCanceled()){
		  			httpCall.cancel();
		  		}
		      }
		    }, 1, TimeUnit.SECONDS);
	}

	private class AsyncResponseListener implements Callback{

		private String methodName;

		public AsyncResponseListener(String methodName){
			this.methodName = methodName;
		}

		@Override
		public void onFailure(Request request, IOException error) {
			AsyncOkHttpRequest.this.onFailture(request, error,methodName);
		}

		@Override
		public void onResponse(Response response) throws IOException {
			AsyncOkHttpRequest.this.onSuccess(response, methodName);
		}

	}
}
