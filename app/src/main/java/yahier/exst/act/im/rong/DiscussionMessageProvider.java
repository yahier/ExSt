package yahier.exst.act.im.rong;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.DiscussionInviteAct;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 自定义融云IM消息提供者 讨论组消息
 *
 * @author lsy
 *
 */
@ProviderTag(messageContent = DiscussionInviteMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class DiscussionMessageProvider extends IContainerItemProvider.MessageProvider<DiscussionInviteMessage> {

	Context mContext;

	/**
	 * 初始化View
	 */
	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.de_customize_message_invite, null);
		ViewHolder holder = new ViewHolder();
		holder.message = (TextView) view.findViewById(R.id.tvName);
		holder.view =  view.findViewById(R.id.rc_img);
		view.setTag(holder);
		mContext = view.getContext();
		return view;
	}

	@Override
	public void bindView(View v, int position, DiscussionInviteMessage content, UIMessage message) {
		ViewHolder holder = (ViewHolder) v.getTag();

		// 更改气泡样式
		if (message.getMessageDirection() == Message.MessageDirection.SEND) {
			// 消息方向，自己发送的
			//holder.view.setBackgroundResource(R.drawable.de_ic_bubble_right);
		} else {
			// 消息方向，别人发送的
			//holder.view.setBackgroundResource(R.drawable.de_ic_bubble_left);
		}

		LogUtil.logE("extra:"+content.getEx_params());//extra是null
		ItemInvite item = JSONHelper.getObject(content.getEx_params(), ItemInvite.class);
		holder.message.setText(item.getMessage()); // 设置消息内容
	}

	@Override
	public Spannable getContentSummary(DiscussionInviteMessage data) {
		return null;
	}

	@Override
	public void onItemClick(View view, int position, DiscussionInviteMessage content, UIMessage message) {
		LogUtil.logE("extra:"+content.getEx_params());//extra是null

		ItemInvite item = JSONHelper.getObject(content.getEx_params(), ItemInvite.class);
		Intent intent = new Intent(mContext,DiscussionInviteAct.class);
		intent.putExtra("inviteid", item.getInviteid());
		mContext.startActivity(intent);
	}

	@Override
	public void onItemLongClick(View view, int position, DiscussionInviteMessage content, UIMessage message) {

	}

	class ViewHolder {
		TextView message;
		View view;
	}

}