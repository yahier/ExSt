package yahier.exst.ui.DirectScreen.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.stbl.stbl.R;
import com.stbl.stbl.ui.DirectScreen.service.floating.FloatView;
import com.stbl.stbl.ui.DirectScreen.service.floating.NoDuplicateClickListener;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UISkipUtils;

/**
 * Created by meteorshower on 16/3/7.
 *
 * 直播悬浮框
 */
public class FloatingDirectScreenService extends Service {

    private final String TAG = "FloatingDirectScreenService";
    private LinearLayout mFloatLayout;//浮动框
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatLayout != null){
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    private void initFloatWindow(){
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 500;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        createFloatView();

        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);

    }

    //定义浮动窗口布局
    private void createFloatView(){
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.widget_float_service_view, null);

        FloatView floatView = (FloatView)mFloatLayout.findViewById(R.id.float_view);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        floatView.setNoDuplicateClickListener(new NoDuplicateClickListener() {
            @Override
            public void onNoDulicateClick(View v) {
                QavsdkServiceApi.startDirectScreenService(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), FloatingDirectScreenService.class);
                getApplicationContext().stopService(intent);
            }
        });

        floatView.setOnUpdateViewLayoutListener(new FloatView.OnUpdateViewLayoutListener() {
            @Override
            public void updateViewLayout(Point point) {
                wmParams.x = point.x;
                wmParams.y = point.y;
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
        });

    }
}
