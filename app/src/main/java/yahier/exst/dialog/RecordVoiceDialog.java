package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;

import java.io.File;

public class RecordVoiceDialog extends Dialog {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ImageView mRecordIv;
    private TextView mTip1Tv;
    private TextView mTip2Tv;

    private MediaRecorder recorder;

    private int mCount;

    private IRecordDialog mInterface;

    private File mRecordingFile;

    private boolean mIsRecording;

    public RecordVoiceDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_record_voice);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mRecordIv = (ImageView) findViewById(R.id.iv_start_record);
        mTip1Tv = (TextView) findViewById(R.id.tv_tip1);
        mTip2Tv = (TextView) findViewById(R.id.tv_tip2);

        mRecordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsRecording) {
                    startRecord();
                } else {
                    stopRecord();
                }
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordingFile != null && mRecordingFile.exists()) {
                    if (mCount <= 2) {
                        stopRecord();
                        ToastUtil.showToast(R.string.me_record_time_too_short_please_record_again);
                        return;
                    }
                    dismiss();
                    if (mInterface != null) {
                        mInterface.onConfirm(mRecordingFile);
                    }
                } else {
                    ToastUtil.showToast(R.string.me_not_yet_start_record);
                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopRecord();
            }
        });
    }

    private Runnable mTicker = new Runnable() {
        @Override
        public void run() {
            if (mIsRecording) {
                onTick();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        }
    };

    private void onTick() {
        mTip1Tv.setText(Res.getString(R.string.me_recording) + mCount + Res.getString(R.string.me_second));
        if (mCount >= 60) {
            stopRecord();
            mTip1Tv.setText(R.string.me_record_done_please_upload_or_click_rerecord);
        }
        mCount++;
    }

    private void startRecord() {
        mIsRecording = true;
        mCount = 0;
        String fileName = "record_raw_" + SharedToken.getUserId(MyApplication.getContext()) + ".amr";
        mRecordingFile = new File(FileUtils.getAppDir(), fileName);
        if (mRecordingFile.exists()) {
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(mRecordingFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();

            mHandler.post(mTicker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        mIsRecording = false;
        mTip1Tv.setText(R.string.me_record_done_please_upload_or_click_rerecord);
        if (recorder == null) {
            return;
        }
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInterface(IRecordDialog i) {
        mInterface = i;
    }

    public interface IRecordDialog {
        void onConfirm(File file);
    }
}
