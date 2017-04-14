package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;

public class FriendAddAct extends ThemeActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_friend_add);
		setLabel(R.string.add_friend);
		setRightText(R.string.tongbu_phone, new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FriendAddAct.this, PhoneContactListAct.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.linInput).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FriendAddAct.this, SearchUserAct.class);
				startActivity(intent);

			}
		});
	}

}
