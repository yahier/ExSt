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
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.AdOtherPayOrderActivity;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.text.DecimalFormat;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 自定义融云IM消息提供者
 * 广告订单找人代付
 *
 */
@ProviderTag(messageContent = AdOtherPayMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class AdOtherPayMessageProvider extends IContainerItemProvider.MessageProvider<AdOtherPayMessage>{

	Context mContext;

	/**
	 * 初始化View
	 */
	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.im_other_pay_provider, null);
		ViewHolder holder = new ViewHolder();
		holder.tvUsernameText = (TextView) view.findViewById(R.id.tv_username_text);
		holder.tvPriceText = (TextView) view.findViewById(R.id.tv_price_text);
		holder.view = view.findViewById(R.id.rc_img);
		view.setTag(holder);
		mContext = view.getContext();
		return view;
	}

	@Override
	public void bindView(View v, int position, AdOtherPayMessage content, UIMessage message) {
		ViewHolder holder = (ViewHolder) v.getTag();
		// 更改气泡样式
		if (message.getMessageDirection() == Message.MessageDirection.SEND) {
			// 消息方向，自己发送的
			holder.view.setBackgroundResource(R.drawable.im_message_red_bg_right);
			//是为了兼容ios才把值调换的
//			holder.tvUsernameText.setText(String.format(mContext.getString(R.string.ad_please_you_pay2),content.getFromusername()));
			holder.tvUsernameText.setText(String.format(mContext.getString(R.string.ad_please_you_pay2),content.getTousername()));
		} else {
			// 消息方向，别人发送的
			holder.view.setBackgroundResource(R.drawable.im_message_red_bg_left);
			//是为了兼容ios才把值调换的
			holder.tvUsernameText.setText(String.format(mContext.getString(R.string.ad_please_you_pay),content.getFromusername()));
//			holder.tvUsernameText.setText(String.format(mContext.getString(R.string.ad_please_you_pay),content.getTousername()));
		}
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			String price = df.format(content.getPrice());
			holder.tvPriceText.setText(String.format(mContext.getString(R.string.yuan), price));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public Spannable getContentSummary(AdOtherPayMessage data) {
		return null;
	}

	@Override
	public void onItemClick(View view, int position, AdOtherPayMessage content, UIMessage message) {
//		ToastUtil.showToast(content.getOrderno());
		LogUtil.logE("LogUtil","orderno -- "+content.getOrderno());
		Intent intent = new Intent(mContext, AdOtherPayOrderActivity.class);
		intent.putExtra("orderno",content.getOrderno());
		mContext.startActivity(intent);
	}

	class ViewHolder {
		View view;
		TextView tvUsernameText;
		TextView tvPriceText;
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, AdOtherPayMessage arg2, UIMessage arg3) {
		// TODO Auto-generated method stub

	}

}