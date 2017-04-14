package yahier.exst.act.mine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.stbl.stbl.R;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedToken;

/**
 * Created by Administrator on 2016/11/9.
 */

public class EarnShituTicketActivity extends ThemeActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_shitu_ticket);

        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_earn_shitu_ticket));
        setRightImage(R.drawable.earn_shitu_ticket_rule, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuleDialog dialog = new RuleDialog(mActivity);
                dialog.show();
            }
        });
        findViewById(R.id.layout_sign).setOnClickListener(this);
        findViewById(R.id.layout_status).setOnClickListener(this);
        findViewById(R.id.layout_student).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_sign: {
                Intent intent = new Intent(this, SignAct.class);
                intent.putExtra(KEY.USER_ID, Long.valueOf(SharedToken.getUserId()));
                startActivity(intent);
            }
            break;
            case R.id.layout_status:
                Intent intent = new Intent(mActivity, TabHome.class);
                intent.setAction(ACTION.GO_TO_STATUS_PAGE);
                startActivity(intent);
                break;
            case R.id.layout_student:
                startActivity(new Intent(mActivity, MyQrcodeActivity.class));
                break;
        }
    }

    private class RuleDialog extends Dialog implements View.OnClickListener {

        public RuleDialog(Context context) {
            super(context, R.style.Common_Dialog);
            setContentView(R.layout.dialog_earn_shitu_ticket_rule);

            Window window = getWindow();
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);

            findViewById(R.id.btn_got_it).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            dismiss();
        }
    }
}
