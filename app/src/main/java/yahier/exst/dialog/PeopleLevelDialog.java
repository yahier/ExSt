package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.AddFriendFromPhoneAct;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;

/**
 * Created by tnitf on 2016/3/21.
 */
public class PeopleLevelDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContentTv;

    public PeopleLevelDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_people_level);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        initView();
    }

    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mContentTv = (TextView) findViewById(R.id.tv_content);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public void setContentText(final Context context, String content) {

        mContentTv.setText(content);

        findViewById(R.id.btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(context, AddFriendFromPhoneAct.class));
            }
        });

        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.contacts_level_introd, "");
                Intent intent = new Intent(context, CommonWeb.class);
                intent.putExtra("url", url);
                intent.putExtra("title", Res.getString(R.string.me_level_intro));
                context.startActivity(intent);
            }
        });
    }
}
