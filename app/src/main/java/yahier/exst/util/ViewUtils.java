package yahier.exst.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonBannerAdapter;
import com.stbl.stbl.model.Banner;

import java.util.List;

public class ViewUtils {
    @SuppressLint ("NewApi")
    public static int getListHeight(ListView list) {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null) {
            return 0;
        }

        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, list);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        return totalHeight;

    }

    public void setEmptyView(Activity mActivity) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.common_empty_view, null);
        mActivity.setContentView(view);
    }

    /**
     * 被弃用，请调用setListViewHeightBasedOnChildren
     *
     * @param list
     */
    @SuppressLint ("NewApi")
    public static void setAdapterViewHeight(AdapterView<ListAdapter> list) {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = listAdapter.getCount();// 2代表每一行有两个item
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, list);
            listItem.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED); // UNSPECIFIEDEXACTLYAT_MOST
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight + 30;
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        LogUtil.logE("高度:" + params.height);
        list.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // listItem.measure(0, 0); // 计算子项View 的宽高

            int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
            if (listItem != null) {
                // 然后改成调用
                listItem.measure(desiredWidth, 0);

                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        //LogUtil.logE("setListViewHeightBasedOnChildren:" + params.height);
        listView.setLayoutParams(params);
    }

    private static float pre_x;
    private static float pre_y;
    private static float dis_x;
    private static float dis_y;
    private static boolean isClick;

    public static void scroll(final View istview) {
        istview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pre_x = event.getX();
                        pre_y = event.getY();
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        dis_x = Math.abs(event.getX() - pre_x);
                        dis_y = Math.abs(event.getY() - pre_y);
                        if (dis_x > dis_y) {
                            istview.getParent().requestDisallowInterceptTouchEvent(true);
                            isClick = false;
                        } else if (dis_x < 5 && dis_y < 5) {
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            return true;
                        }
                }
                return false;
            }

        });
    }

    /**
     * 第二个参数表示每一行的item数目
     *
     * @param grid
     * @param columnNum
     */
    @SuppressLint ("NewApi")
    public static void setAdapterViewHeight(GridView grid, int columnNum) {
        ListAdapter listAdapter = grid.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = (listAdapter.getCount() + columnNum - 1) / columnNum;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, grid);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = grid.getLayoutParams();
        params.height = totalHeight + 30;
        ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        grid.setLayoutParams(params);
    }

    /**
     * 第二个参数表示每一行的item数目
     * 不额外加高度和margin
     *
     * @param grid
     * @param columnNum
     */
    @SuppressLint ("NewApi")
    public static void setAdapterViewHeightNoMargin(GridView grid, int columnNum) {
        ListAdapter listAdapter = grid.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = (listAdapter.getCount() + columnNum - 1) / columnNum;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, grid);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        grid.setPadding(grid.getPaddingLeft(), grid.getPaddingTop(), grid.getPaddingRight(), grid.getPaddingBottom());
        ViewGroup.LayoutParams params = grid.getLayoutParams();
        params.height = totalHeight;
        grid.setLayoutParams(params);
    }


//    /**
//     * 第二个参数表示每一行的item数目
//     * 不额外加高度和margin
//     *
//     * @param grid
//     * @param columnNum
//     */
//    @SuppressLint ("NewApi")
//    public static void setAdapterViewHeightNoMargin(GridView grid, int extraSize, int columnNum) {
//        ListAdapter listAdapter = grid.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//
//        int totalHeight = 0;
//        int allSize = listAdapter.getCount() + columnNum - 1 + extraSize;  //30+7-1+3 = 39
//
//        int count = allSize / columnNum;
//        LogUtil.logE("setAdapterViewHeightNoMargin1", listAdapter.getCount() + ":" + columnNum + ":" + extraSize);
//        LogUtil.logE("setAdapterViewHeightNoMargin", count + ":" + allSize + ":" + columnNum);
//        for (int i = 0; i < count; i++) {
//            View listItem = listAdapter.getView(i, null, grid);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        grid.setPadding(grid.getPaddingLeft(), grid.getPaddingTop(), grid.getPaddingRight(), grid.getPaddingBottom());
//        ViewGroup.LayoutParams params = grid.getLayoutParams();
//        params.height = totalHeight;
//        grid.setLayoutParams(params);
//    }

    /**
     * 设置动态GridView.和上面的setAdapterViewHeight区别是。本方法没有额外增加高度
     *
     * @param grid
     * @param columnNum
     */
    public static void setStatusesAdapterViewHeight(GridView grid, int columnNum) {
        ListAdapter listAdapter = grid.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = (listAdapter.getCount() + columnNum - 1) / columnNum;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, grid);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = grid.getLayoutParams();
        params.height = totalHeight;// + 30;
        //((MarginLayoutParams) params).setMargins(10, 10, 10, 10);//2017-2-7日注释 by yahier
        grid.setLayoutParams(params);
    }


    public static void setAdapterViewHeight(GridView grid, int columnNum, int verticalSpcaingDp) {
        ListAdapter listAdapter = grid.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int count = (listAdapter.getCount() + columnNum - 1) / columnNum;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, grid);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = grid.getLayoutParams();
        params.height = totalHeight + 30 + Util.dip2px(grid.getContext(), verticalSpcaingDp);
//		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        //LogUtil.logE("height:" + params.height);
        grid.setLayoutParams(params);
    }


    //新建方法
    boolean isRemoveMsg = false;
    public final static int pagerMsgWhat = 300;
    private LinearLayout pointLin;
    int pageIndex = 0;//此值与Act页面共享。值应该只在一边赋值
    /*
    public void setBannerPager(Activity mContext, List<Banner> listBanner, ViewPager pager, final Handler pagerHandler) {
        final int size = listBanner.size();
        int height = (int) (Device.getWidth() * CommonBannerAdapter.rateOfWH);
        FrameLayout pointPager = (FrameLayout) mContext.findViewById(R.id.pager_point_lin);
        pager.setAdapter(new CommonBannerAdapter(mContext, listBanner));
        pointPager.setLayoutParams(new LinearLayout.LayoutParams(Device.getWidth(), height));
        if (listBanner != null && size > 1) {
            pointLin = (LinearLayout) mContext.findViewById(R.id.point_lin);
            pointLin.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView img = new ImageView(mContext);
                if (i == 0) {
                    img.setImageResource(R.drawable.icon_list_select);
                } else {
                    img.setImageResource(R.drawable.icon_list_noselect);
                }
                pointLin.addView(img);
            }
        }

        //控制触摸滑动时把定时滑动去掉
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (pagerHandler != null) {
                            pagerHandler.removeCallbacksAndMessages(null);
                            isRemoveMsg = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (pagerHandler != null && isRemoveMsg)
                            pagerHandler.sendEmptyMessageDelayed(pagerMsgWhat, 4000);
                        break;
                }
                return false;
            }
        });

        pager.setOnPageChangeListener(new MyOnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                pageIndex = index;
                while (index >= size) {
                    index = index - size;
                }
                for (int i = 0; i < size; i++) {
                    ((ImageView) pointLin.getChildAt(i)).setImageResource(R.drawable.icon_list_noselect);
                }
                ((ImageView) pointLin.getChildAt(index)).setImageResource(R.drawable.icon_list_select);
            }
        });

    }
*/

    public static void setEnabled(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 2000);
    }

}
