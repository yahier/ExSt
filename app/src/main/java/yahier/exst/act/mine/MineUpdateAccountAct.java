package yahier.exst.act.mine;

import android.os.Bundle;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;

/**
 * Created by lenovo on 2016/4/27.
 */
public class MineUpdateAccountAct extends ThemeActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_update_account_act);
        setLabel("升级账户");
        findViewById(R.id.item1).setOnClickListener(this);
        findViewById(R.id.item2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item1:
                //判断是否够要求申请成功大酋长
                break;
            case R.id.item2:
                String roleflag = SharedToken.getRoleFlag(this);
                try {
                    if (roleflag != null && UserRole.isSeller(Integer.parseInt(roleflag))) {
                        TipsDialog.popup(this, "你已经是商家", "确定");
                    }else{
                        //跳转网页
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
