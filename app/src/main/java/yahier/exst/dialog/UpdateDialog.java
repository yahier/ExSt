package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Process;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.model.UpdateInfo;
import com.stbl.stbl.task.UpdateTask;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PkgUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * Created by tnitf on 2016/3/21.
 */
public class UpdateDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mVersionTv;
    private TextView mContentTv;

    private UpdateInfo mUpdateInfo;

    private boolean mIsForceUpdate = false;
    private boolean mIsDownloading = false;
    private boolean mCanIgnore = true;

    public UpdateDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_update);

        initView();
    }

    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mVersionTv = (TextView) findViewById(R.id.tv_version);
        mContentTv = (TextView) findViewById(R.id.tv_content);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsForceUpdate) {
                    dismiss();
                    ignoreThisVersion();
                } else {
                    ToastUtil.showToast("更新新版本才能正常使用APP");
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsForceUpdate) {
                    dismiss();
                    ignoreThisVersion();
                } else {
                    ToastUtil.showToast("更新新版本才能正常使用APP");
                }
            }
        });

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsForceUpdate) {
                    dismiss();
                }
                if (mIsDownloading) {
                    ToastUtil.showToast("正在下载中...");
                } else {
                    update();
                }
            }
        });

        setCanceledOnTouchOutside(false);

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ignoreThisVersion();
            }
        });
    }

    public void show(UpdateInfo info) {
        mUpdateInfo = info;
        int currentVersionCode = PkgUtils.getVersionCode();
        int latestVersionCode = info.getVersioncode();
        if (currentVersionCode < latestVersionCode) {   //当前app的版本不是最新版本
            int forceVersionCode = info.getForceversioncode();
            if (currentVersionCode < forceVersionCode) { //强制更新，每次都弹dialog
                mIsForceUpdate = true;
                setCancelable(false);
                setCanceledOnTouchOutside(false);
                setData();
            } else { //可忽略
                int ignoreVersionCode = (int) SharedPrefUtils.getFromPublicFile(KEY.IGNORE_VERSION_CODE, 0);
                if (ignoreVersionCode != latestVersionCode) {  //没有忽略最新版本就弹出dialog
                    SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_TIMES, 0);
                    //设置dialog内容后弹出
                    setData();
                } else {
                    int times = (int) SharedPrefUtils.getFromPublicFile(KEY.IGNORE_VERSION_TIMES, 0);
                    if (times < 3) {
                        long now = System.currentTimeMillis();
                        long timestamp = (long) SharedPrefUtils.getFromPublicFile(KEY.IGNORE_VERSION_TIMESTAMP, now);
                        if (now - timestamp >= 6 * 60 * 60 * 1000) {
                            //弹出dialog
                            setData();
                        }
                    }
                }
            }
        }
    }

    public void showEveryTime(UpdateInfo info) {
        mUpdateInfo = info;
        mCanIgnore = false;
        int currentVersionCode = PkgUtils.getVersionCode();
        int latestVersionCode = info.getVersioncode();
        if (currentVersionCode < latestVersionCode) {   //当前app的版本不是最新版本
            setData();
        } else {
            ToastUtil.showToast("已经是最新版本了");
        }
    }

    private void setData() {
        mTitleTv.setText(mUpdateInfo.getTitle());
        mVersionTv.setText(mUpdateInfo.getVersionname());
        mContentTv.setText(mUpdateInfo.getUpdatecontent());
        show();
    }

    private void ignoreThisVersion() {
        if (!mCanIgnore) {
            return;
        }
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_CODE, mUpdateInfo.getVersioncode());
        int times = (int) SharedPrefUtils.getFromPublicFile(KEY.IGNORE_VERSION_TIMES, 0);
        times++;
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_TIMES, times);
        SharedPrefUtils.putToPublicFile(KEY.IGNORE_VERSION_TIMESTAMP, System.currentTimeMillis());
    }

    private void update() {
        mIsDownloading = true;
        ToastUtil.showToast("后台下载中...");
        LogUtil.logE("updateUrl = " + mUpdateInfo.getDownloadurl());
        UpdateTask.download(mUpdateInfo.getDownloadurl(), new OnDownloadCallback() {

            @Override
            public void onDownloadSuccess(File file) {
                mIsDownloading = false;
                dismiss();
                PkgUtils.installApk(file);
                if (mIsForceUpdate) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MobclickAgent.onKillProcess(getContext());
                            Process.killProcess(Process.myPid());
                        }
                    }, 500);
                }
            }

            @Override
            public void onDownloadError() {
                mIsDownloading = false;
                if (mIsForceUpdate) {
                    ToastUtil.showToast("下载失败，请重试");
                }
            }

        });
    }

    public interface OnDownloadCallback {
        void onDownloadSuccess(File file);

        void onDownloadError();
    }
}
