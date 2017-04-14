package yahier.exst.act.dongtai;

import com.stbl.stbl.act.im.ChoiceFriendsAct;
import com.stbl.stbl.act.im.GroupSendAct;
import com.stbl.stbl.item.im.UserList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *不再被调用 内部分享
 * @author ruilin
 *
 */
public class ShareAct extends Activity {
	boolean isClosed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent it = new Intent(this, ChoiceFriendsAct.class);
		startActivityForResult(it, 99);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 99 && resultCode == RESULT_OK) {
			UserList users = (UserList) data.getSerializableExtra("users");
			if (null != users) {
				Intent it = new Intent(this, GroupSendAct.class);
				it.putExtra("users", users);
				startActivity(it);
				finish();
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isClosed) finish();
		else isClosed = true;
	}
}
