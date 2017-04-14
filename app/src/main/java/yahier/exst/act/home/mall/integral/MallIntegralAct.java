package yahier.exst.act.home.mall.integral;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.TicketDetailActivity;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.adapter.CommonBannerAdapter;
import com.stbl.stbl.adapter.mall.integral.MallIntegralAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.MallIntegralAll;
import com.stbl.stbl.model.MallIntegralProduct;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelString;
import com.stbl.stbl.widget.NestedGridView;
import com.stbl.stbl.widget.ScrollChangeScrollView;
import com.stbl.stbl.widget.TitleBar;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 积分商城首页
 * Created by Administrator on 2016/7/23 0023.
 */
public class MallIntegralAct extends BaseActivity implements View.OnClickListener, FinalHttpCallback,WheelString.OnTimeWheelListener {
    private TitleBar mBar; //标题栏
//    private MallIntegralScrollView mScrollView;//用于控制悬浮效果的scrollview
    private ScrollChangeScrollView mScrollView;//
    private SwipeToLoadLayout mSwipeToLoadLayout;//用于gridview上拉刷新
    private NestedGridView xGridView;//商品列表
    private LinearLayout pointLin;//banner point
    private ViewPager pager;//banner
    private View rlIntegralBalance;//积分余额父控
    private TextView tvIntegralBalance;//师徒票余额
    private View llExchangeHistory;//兑换记录
    private View llIntegralHelp;//帮助说明
    private TextView tvFilter;//筛选
    private TextView tvFooter;//提示没有更多商品
    private MallIntegralAdapter mAdapter;//商品列表适配器
    private List<MallIntegralProduct> mData;//商品数据
    private int integralBalance; //师徒票余额值
    private boolean mIsDestroy;

    private int page = 1;//当前页面
    private int querytype = 0;//筛选类型
    private int count = 12; //每页商品数

    //自动加载是否完成
    private boolean isAutoUpdateComplete = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_integral_layout);
        mData = new ArrayList<>();
        EventBus.getDefault().register(this);
        initView();
        getAllDate();
        pagerHandler = new PagerHanlder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pagerHandler.sendEmptyMessageDelayed(pagerMsgWhat, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pagerHandler.removeMessages(pagerMsgWhat);
    }

    private void initView() {
        mBar = (TitleBar) findViewById(R.id.bar);
        pager = (ViewPager) findViewById(R.id.pager);
        mBar.setCenterTitle(R.string.integral_exchage_mall);
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mScrollView = (ScrollChangeScrollView) findViewById(R.id.swipe_target);
        rlIntegralBalance = findViewById(R.id.rl_integral_balance);
        tvIntegralBalance = (TextView) findViewById(R.id.tv_integral_balance);
        llExchangeHistory = findViewById(R.id.ll_exchange_history);
        llIntegralHelp = findViewById(R.id.ll_integral_help);
        tvFilter = (TextView) findViewById(R.id.tv_filter);
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page ++;
                getGoodsDate();
            }
        });
        xGridView = (NestedGridView) findViewById(R.id.gv_swipe_target);
        tvFooter = (TextView) findViewById(R.id.tv_footer);

//        mScrollView.setRefreshView(xGridView);
        rlIntegralBalance.setOnClickListener(this);
        llExchangeHistory.setOnClickListener(this);
        llIntegralHelp.setOnClickListener(this);
        tvFilter.setOnClickListener(this);
        mAdapter = new MallIntegralAdapter(this, mData);
        xGridView.setAdapter(mAdapter);
//        ViewUtils.setAdapterViewHeightNoMargin(xGridView, 2);
        xGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mData.size()) {
                    MallIntegralProduct goods = mData.get(position);
                    if (goods == null) return;
                    //进入商品详情
                    Intent it = new Intent(MallIntegralAct.this, MallIntegralGoodsDetailAct.class);
                    it.putExtra("goodsid", goods.getGoodsid());
                    it.putExtra("integralBalance",integralBalance);
                    startActivity(it);
                }
            }
        });
        mScrollView.setOnScrollListener(new ScrollChangeScrollView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y, int oldx, int oldy) {
                float gridY = xGridView.getY();
                int height = xGridView.getHeight();
                //相当于快到底部的屏幕的一半
                if ((gridY+height)-y <= Device.getHeight()*1.5 && isAutoUpdateComplete && mSwipeToLoadLayout.isLoadMoreEnabled()){
//					LogUtil.logE("LogUtil","加载新数据。。。。。。");
                    isAutoUpdateComplete = false;
                    page ++;
                    getGoodsDate();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_integral_balance: //余额
                if (isDealTempAccount()){ //临时账户判断
                    return;
                }
                intent = new Intent(this, TicketDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_exchange_history: //兑换记录
                if (isDealTempAccount()){ //临时账户判断
                    return;
                }
                intent = new Intent(this, MallExchangeHistoryAct.class);
                startActivity(intent);
                break;
            case R.id.ll_integral_help: //帮助说明
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.stticket_mall_instrod,"");
                if (url == null || url.equals("")){
                    CommonTask.getCommonDicBackground();
                    return;
                }

                intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url", url);
                intent.putExtra("title", getString(R.string.help_notes));
                startActivity(intent);
                break;
            case R.id.tv_filter: //筛选
                showFilter();
                break;
        }
    }

    public void onEvent(EventType type){
        switch(type.getType()){
            case EventType.TYPE_REFRESH_INTEGRAL://刷新积分
                getWalletBalance();
                break;
        }
    }

    //是否临时账户。是则并加入处理。
    boolean isDealTempAccount() {
        boolean isTemp = false;
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        if (UserRole.isTemp(roleFlag)) {
            isTemp = true;
            TipsDialog.popup(this, getString(R.string.temp_account_msg), getString(R.string.temp_account_cancel), getString(R.string.temp_account_ok), new TipsDialog.OnTipsListener() {
                @Override
                public void onConfirm() {
                    startActivity(new Intent(MallIntegralAct.this, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        }
        return isTemp;
    }

    /**
     * 显示筛选的类型
     */
    private void showFilter(){
        String json = (String) SharedPrefUtils.getFromPublicFile(KEY.productquerytype,"");
        List<String> listtype = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            org.json.JSONArray arr = obj.optJSONArray(KEY.productquerytype);
            if (arr != null){
                for (int i=0; i<arr.length(); i++){
                    JSONObject item = arr.optJSONObject(i);
                    listtype.add(item.optString("name"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listtype.size() == 0) return;
        WheelString list = new WheelString();
        list.setOnTimeWheelListener(this);
        list.setCurrentItem(querytype);
        list.chooseTime(this, listtype,getString(R.string.integral_unit));
        list.setOkText(getString(R.string.button_ok));
    }

    /**
     * 筛选弹框监听
     * @param index
     * @param tag
     */
    @Override
    public void onTagOk(int index, String tag) {
        querytype = index;
        page = 1;
        getGoodsDate();
    }

    private void getWalletBalance() {
        WalletTask.getWalletBalance().setCallback(this, mGetAccountBalanceCallback).start();
    }

    private SimpleTask.Callback<WealthInfo> mGetAccountBalanceCallback = new SimpleTask.Callback<WealthInfo>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(WealthInfo result) {
            integralBalance = result.getJifen();
            tvIntegralBalance.setText("" + integralBalance);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
    /**
     * 获取数据
     */
    private void getAllDate() {
        Params params = new Params();
        params.put("page", page);
        params.put("count", count);
        new HttpEntity(this).commonPostData(Method.getPointsmallHomeShow, params, this);
    }
    /**
     * 获取数据
     */
    private void getGoodsDate() {
        Params params = new Params();
        params.put("querytype", querytype);
        params.put("page", page);
        params.put("count", count);
        new HttpEntity(this).commonPostData(Method.getPointsmallProductQuery, params, this);
    }

    /**
     * 显示banner数据
     *
     * @param listBanner
     */
    @SuppressWarnings("deprecation")
    void setBannerPager(List<Banner> listBanner) {
        final int size = listBanner.size();
        int height = (int) (Device.getWidth(this) * CommonBannerAdapter.rateOfWH);
        FrameLayout pointPager = (FrameLayout) findViewById(R.id.pager_point_fra);

        pager.setAdapter(new CommonBannerAdapter(this, listBanner));
        pointPager.setLayoutParams(new LinearLayout.LayoutParams(Device.getWidth(this), height));
        if ( size > 1) {
            pointLin = (LinearLayout) findViewById(R.id.point_lin);
            pointLin.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView img = new ImageView(this);
                if (i == 0) {
                    img.setImageResource(R.drawable.icon_list_select);
                } else {
                    img.setImageResource(R.drawable.icon_list_noselect);
                }
                pointLin.addView(img);
            }
        }
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
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
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
    }

    boolean isRemoveMsg;
    int pageIndex;
    PagerHanlder pagerHandler;
    final int pagerMsgWhat = 100;
    class PagerHanlder extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendEmptyMessageDelayed(pagerMsgWhat, 4000);
            pageIndex++;
            pager.setCurrentItem(pageIndex, true);
            // LogUtil.logE("滑动。");k
        }

    }
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
    }

    @Override
    public void parse(String methodName, String result) {
        isAutoUpdateComplete = true;
        if (mSwipeToLoadLayout != null) mSwipeToLoadLayout.setLoadingMore(false);
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getPointsmallHomeShow: //所有数据
                MallIntegralAll all = JSONHelper.getObject(obj, MallIntegralAll.class);
                if (all == null) return;
                //banner
                List<Banner> banners = all.getBannerview();
                if (banners != null)
                    setBannerPager(banners);

                integralBalance = all.getStticketamount();
                tvIntegralBalance.setText("" + integralBalance);

                List<MallIntegralProduct> productList = all.getProductview();
                if (productList != null) mData.addAll(productList);
                mAdapter.notifyDataSetChanged();
                mScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                     mScrollView.scrollTo(0,0);
                    }
                },100);
//                ViewUtils.setAdapterViewHeightNoMargin(xGridView, 2);
//                //提示没有更多了
                if (productList == null || (productList != null && mData.size() >= 2 && productList.size() < count) || mData.size() == 0){
                    tvFooter.setVisibility(View.VISIBLE);
                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                }else{
                    tvFooter.setVisibility(View.GONE);
                    mSwipeToLoadLayout.setLoadMoreEnabled(true);
                }
                break;
            case Method.getPointsmallProductQuery: //商品列表
                List<MallIntegralProduct> productview = JSONHelper.getList(obj,MallIntegralProduct.class);
                if (productview != null) {
                    if (page == 1) {
                        mData.clear();
                        mData.addAll(productview);
                    }else{
                        mData.addAll(productview);
                    }
                    mAdapter.notifyDataSetChanged();
//                    ViewUtils.setAdapterViewHeightNoMargin(xGridView, 2);
                }
                //提示没有更多了
                if (productview == null || (productview != null && mData.size() >= 2 && productview.size() < count) || mData.size() == 0){
                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                    tvFooter.setVisibility(View.VISIBLE);
                }else{
                    mSwipeToLoadLayout.setLoadMoreEnabled(true);
                    tvFooter.setVisibility(View.GONE);
                }
                break;
        }
    }
}
