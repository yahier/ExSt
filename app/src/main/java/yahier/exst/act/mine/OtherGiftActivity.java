package yahier.exst.act.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.MineGiftItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.TitleBar;

/**
 * Created by tnitf on 2016/4/12.
 */
public class OtherGiftActivity extends BaseActivity {

    private UserItem mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_gift);

        mUser = (UserItem) getIntent().getSerializableExtra(EXTRA.USER_ITEM);
        if (mUser == null) {
            ToastUtil.showToast("数据传递错误");
            finish();
            return;
        }

        String userName = (mUser.getAlias() == null || mUser.getAlias().equals("") ? mUser.getNickname() : mUser.getAlias());
        if (mUser.getUserid() == Long.valueOf(SharedToken.getUserId(this))) {
            userName = "我";
        }
        TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
        titleBar.setTitle(userName + "收到的礼物");
//        titleBar.setTitle(mUser.getNickname() + "收到的礼物");
        titleBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CommonGiftFragment fragment = new CommonGiftFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY.SELECT_TYPE, MineGiftItem.selecttype_get);
        bundle.putLong(KEY.USER_ID, mUser.getUserid());
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.layout_container, fragment).commit();
    }

}
