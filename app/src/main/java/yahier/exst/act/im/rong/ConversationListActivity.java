package yahier.exst.act.im.rong;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * 如果有多条通知信息，点开后，会进入到此页面。但为什么群组和讨论组都打开了空白页面呢。
 */
public class ConversationListActivity extends BaseActivity {
	TextView imgLeft;
	TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation_list);
		initView();
		testData();
	}

	void initView() {
		imgLeft = (TextView) findViewById(R.id.theme_top_banner_left);
		imgLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		tvTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
		tvTitle.setText(R.string.im_conversation);
	}

	void testData() {
		ConversationListFragment listFragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.conversationlist);
		Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationlist")
				.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") // 设置私聊会话是否聚合显示
				.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")// 群组
				.appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")// 讨论组
				.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")// 应用公众服务。
				.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")// 公共服务号
				.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")// 系统
				.build();
		listFragment.setUri(uri);
	}

}
