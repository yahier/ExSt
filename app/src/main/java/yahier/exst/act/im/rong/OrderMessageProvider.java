package yahier.exst.act.im.rong;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 自定义融云IM消息提供者
 *
 * @author lsy
 *
 */
@ProviderTag(messageContent = OrderMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class OrderMessageProvider extends IContainerItemProvider.MessageProvider<OrderMessage> implements FinalHttpCallback {

	Context mContext;

	/**
	 * 初始化View
	 */
	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.im_order_provider, null);
		ViewHolder holder = new ViewHolder();
		holder.webView = (WebView) view.findViewById(R.id.web);
		holder.view = (View) view.findViewById(R.id.rc_img);
		view.setTag(holder);
		mContext = view.getContext();
		return view;
	}

	@Override
	public void bindView(View v, int position, OrderMessage content, UIMessage message) {
		ViewHolder holder = (ViewHolder) v.getTag();
		// 更改气泡样式
		if (message.getMessageDirection() == Message.MessageDirection.SEND) {
			// 消息方向，自己发送的
			holder.view.setBackgroundResource(R.drawable.im_message_white_bg_right);
		} else {
			// 消息方向，别人发送的
			holder.view.setBackgroundResource(R.drawable.im_message_white_bg_left);
		}

		LogUtil.logE("接受内容" + content.getEx_params());
		String urlContent = content.getEx_params();
		// String s = "http://www.baidu.com";
		// String contents =
		// "<html><body><h1>My First Heading</h1><p>My first paragraph.</p></body></html>";
		// holder.webView.loadUrl(contents);
		// holder.webView.seth
		holder.webView.getSettings().setDefaultTextEncodingName("utf-8");
		// holder.webView.loadData(urlContent, "text/html", "GBK") ;
		holder.webView.loadDataWithBaseURL(null, urlContent, "text/html", "utf-8", null);
		//新设置属性
//		holder.webView.getSettings().setJavaScriptEnabled(true);
//		// 设置可以支持缩放
//		holder.webView.getSettings().setSupportZoom(true);
//		// 设置出现缩放工具
//		holder.webView.getSettings().setBuiltInZoomControls(true);
//		//扩大比例的缩放
//		holder.webView.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		holder.webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

		holder.webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("http:") || url.startsWith("https:")) {
					return false;
				}


//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//				mContext.startActivity(intent);

				Intent intent = new Intent(mContext, CommonWebInteact.class);
				intent.setData(Uri.parse(url));
				mContext.startActivity(intent);
				return true;
			}
		});

	}

	@Override
	public Spannable getContentSummary(OrderMessage data) {
		return null;
	}

	@Override
	public void onItemClick(View view, int position, OrderMessage content, UIMessage message) {

	}

	class ViewHolder {
		WebView webView;
		View view;

	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(mContext, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
			case Method.imOpenRedPacket:
				ToastUtil.showToast(mContext, mContext.getString(R.string.im_congratulation_to_you));
				break;
		}

	}

	@Override
	public void onItemLongClick(View arg0, int arg1, OrderMessage arg2, UIMessage arg3) {
		// TODO Auto-generated method stub

	}

}