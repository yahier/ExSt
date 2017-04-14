package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.model.UpdateInfo;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.NetUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.UpdateManager;

/**
 * Created by tnitf on 2016/7/7.
 */
public class UpdateAlertDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContentTv;

    private UpdateInfo mUpdateInfo;

    private boolean mHasIgnoreTimes = true; //默认有忽略次数，可忽略5次

    public UpdateAlertDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_update_alert);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        initLayout();
    }

    private void initLayout() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mContentTv = (TextView) findViewById(R.id.tv_content);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                UpdateManager.getInstance().update(mUpdateInfo.getDownloadurl(), mUpdateInfo.getVersioncode(), true);
            }
        });

        setCanceledOnTouchOutside(false);

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mHasIgnoreTimes) {
                    ignoreThisVersion();
                }
                if (NetUtil.isWiFi()) {
                    UpdateManager.getInstance().update(mUpdateInfo.getDownloadurl(), mUpdateInfo.getVersioncode(), false);
                }
            }
        });
    }

    private void ignoreThisVersion() {
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_CODE, mUpdateInfo.getVersioncode());
        int times = (int) SharedPrefUtils.getFromPublicFile(KEY.IGNORE_VERSION_TIMES, 0);
        times++;
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_TIMES, times);
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_TIMESTAMP, System.currentTimeMillis());
    }

    public void show(UpdateInfo info, boolean hasIgnoreTimes) {
        mUpdateInfo = info;
        mHasIgnoreTimes = hasIgnoreTimes;
        show();
    }

}
