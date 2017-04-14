package yahier.exst.ui.DirectScreen.widget;

import android.os.Handler;

import com.stbl.stbl.util.ToastUtil;

/**
 * Created by meteorshower on 16/3/30.
 */
public class ReloadAudioTimer {

    private boolean isDownMicrePhone = false;//下麦操作
    private TimerRunnable timerRunable;
    private static final int DELAYED_TIME = 1000;
    private int totalCountNum = 0;

    public ReloadAudioTimer(){
        timerRunable = new TimerRunnable();
    }

    public void setDownMicrePhone(boolean isDownMicrePhone){
        this.isDownMicrePhone = isDownMicrePhone;
        this.totalCountNum = 15;
        if(this.isDownMicrePhone){
            handler.postDelayed(timerRunable, DELAYED_TIME);
        }
    }

    public boolean getReloadFlag(){
        if(totalCountNum > 0){
            ToastUtil.showToast(totalCountNum+"秒后才可以继续抢麦!!");
        }
        return totalCountNum <= 0;
    }

    private Handler handler = new Handler();

    private class TimerRunnable implements Runnable{
        @Override
        public void run() {
            if(totalCountNum == 0){
                handler.removeCallbacks(timerRunable);
                return;
            }else{
                handler.postDelayed(timerRunable, DELAYED_TIME);
            }
            totalCountNum --;
        }
    }

}
