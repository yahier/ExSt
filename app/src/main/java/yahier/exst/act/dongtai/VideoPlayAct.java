package yahier.exst.act.dongtai;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.VideoPlayView;

/**
 * 视频全屏类
 */
public class VideoPlayAct extends ThemeActivity implements VideoPlayView.OnVideoListener {
    final String TAG = "VideoPlayAct";
    public static VideoPlayView videoItemView;
    String url;
    View imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play_act);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPauseVideo = false;
                finish();
            }
        });
        setTopGone();

        if (videoItemView != null) {
            ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();
            if (last != null) {
                last.removeAllViews();
            }
            //videoItemView.restart();//之前是什么状态，进来就是什么状态
            frameLayout.addView(videoItemView);
        } else {
            videoItemView = new VideoPlayView(this);
            frameLayout.removeAllViews();
            frameLayout.addView(videoItemView);
            url = getIntent().getStringExtra("url");
            if (url == null) {
                ToastUtil.showToast("视频地址错误");
                finish();
            }
            videoItemView.start(url);
        }
        videoItemView.setOnListener(this);
        videoItemView.setOnShowBackListener(new VideoPlayView.OnShowBackListener() {
            @Override
            public void show(boolean isShow) {
                imgBack.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

    }

    @Override
    protected void setStatusBar() {

    }

    boolean isPauseVideo = true;

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        videoItemView.stop();
//        LogUtil.logE("video", "onDestroy");
//    }



    @Override
    protected void onPause() {
        super.onPause();
        if (isPauseVideo) {
            if (VideoPlayAct.videoItemView != null && VideoPlayAct.videoItemView.isPlay()) {
                VideoPlayAct.videoItemView.pause();
                LogUtil.logE("pauseVideo");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPauseVideo = true;

    }



    //全屏图标的点击
    @Override
    public void onFullCilck() {
        isPauseVideo = false;
        finish();
    }
}
