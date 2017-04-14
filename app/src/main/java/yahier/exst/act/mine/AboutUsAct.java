package yahier.exst.act.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.UpdateLogDialog;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.UpdateInfo;
import com.stbl.stbl.task.UpdateTask;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;


public class AboutUsAct extends ThemeActivity {
    TextView tvVersion;
    private LoadingDialog mLoadingDialog;
    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        setLabel(getString(R.string.me_about_us));
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText(ConfigControl.getAboutVersionTime(this));

        findViewById(R.id.setting_item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.func_instrod, "");
                Intent intent = new Intent(AboutUsAct.this, CommonWeb.class);
                intent.putExtra("url", url);
                intent.putExtra("title", getString(R.string.me_function_intro));
                startActivity(intent);
            }
        });

        findViewById(R.id.setting_item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUpdateInfo();
            }
        });

        findViewById(R.id.setting_item3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ((TextView)findViewById(R.id.tv_official_website)).getText().toString();
                Intent intent = new Intent(AboutUsAct.this,CommonWeb.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });

        mLoadingDialog = new LoadingDialog(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    private void getUpdateInfo() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        UpdateTask.getUpdateInfo().setCallback(this, mGetUpdateInfoCallback).start();
    }

    private SimpleTask.Callback<UpdateInfo> mGetUpdateInfoCallback = new SimpleTask.Callback<UpdateInfo>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(UpdateInfo result) {
            mLoadingDialog.dismiss();
            UpdateLogDialog updateDialog = new UpdateLogDialog(AboutUsAct.this);
            updateDialog.show(result, false);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
