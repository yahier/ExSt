package yahier.exst.ui.BaseClass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.util.Device;

/**
 * Created by meteorshower on 16/3/2.
 */
public abstract class STBLActivity extends BaseActivity {

    protected Logger logger;
   // public final int pageHome = 0;
    public final int pageStatuses = 0;
    public final int pageIM = 1;
   // public final int pageMine = 3;
    public final int pageMall = 2;

    //int[] pageHomes = {};
    int[] pageStatusess = {R.drawable.guide_statuses1};
    int[] pageIMs = {R.drawable.guide_im1, R.drawable.guide_im2};
    //int[] pageMines = {R.drawable.guide_mine1, R.drawable.guide_mine2};
    int[] pageMalls = {R.drawable.guide_mall};
    int[][] draws = {pageStatusess, pageIMs,pageMalls};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.logger = new Logger(this.getClass().getSimpleName());
    }

    int indexDraw = 0;
    public void show(final int pageindex) {
        Activity activity = getParent();
        if (activity == null) activity = this;
        if (activity == null || activity.isFinishing()) return;
        final View viewWindow = LayoutInflater.from(STBLActivity.this).inflate(R.layout.home_ad_window, null);
        final Dialog dialog = new Dialog(STBLActivity.this, R.style.dialog);
        dialog.setContentView(viewWindow, new LinearLayout.LayoutParams(Device.getWidth(), Device.getHeight()-50));//50写定状态栏高度
        Window window = dialog.getWindow();
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setGravity(Gravity.NO_GRAVITY);
        dialog.show();
        viewWindow.setBackgroundResource(draws[pageindex][indexDraw]);//越界 2 2
//       viewWindow.setBackgroundColor(getResources().getColor(R.color.transparent));

        viewWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                indexDraw++;
                if (indexDraw < draws[pageindex].length) {
                    viewWindow.setBackgroundResource(draws[pageindex][indexDraw]);
                } else {
//                    if(pageindex==pageHome){
//                        GuideUtil.guidePage1Finished(STBLActivity.this);
//                    }
                    dialog.dismiss();
                }

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
