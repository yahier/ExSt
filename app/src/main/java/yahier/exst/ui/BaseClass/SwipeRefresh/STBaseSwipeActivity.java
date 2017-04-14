package yahier.exst.ui.BaseClass.SwipeRefresh;

import android.support.v4.widget.SwipeRefreshLayout;

import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.util.Util;

/**
 * Created by meteorshower on 16/5/3.
 */
public abstract class STBaseSwipeActivity extends STBLBaseActivity {

    private SwipeRefreshLayout refreshLayout;

    public void bindSwipeRefreshLayout(int resourcesId){
        bindSwipeRefreshLayout((SwipeRefreshLayout) findViewById(resourcesId));
    }

    public void bindSwipeRefreshLayout(SwipeRefreshLayout refreshLayout){
        this.refreshLayout = refreshLayout;
        this.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                STBaseSwipeActivity.this.onSwipeRefresh();
            }
        });
    }

    public abstract void onSwipeRefresh();

    public SwipeRefreshLayout getRefreshLayout(){
        return refreshLayout;
    }

    public void startSwipeRefresh(){
        startSwipeRefresh(false);
    }

    /** 启动刷新 */
    public void startSwipeRefresh(boolean isFirstRefresh){
        if(refreshLayout != null){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });

            if(isFirstRefresh){
                refreshLayout.setProgressViewOffset(false, 0, Util.dip2px(this, 24));
                STBaseSwipeActivity.this.onSwipeRefresh();
            }
        }
    }

    /** 关闭刷新 */
    public void stopSwipeRefresh(){
        if(refreshLayout != null){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }
}
