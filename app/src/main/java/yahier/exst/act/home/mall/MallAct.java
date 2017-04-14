package yahier.exst.act.home.mall;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.address.MallAddressAct;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.act.mine.MineWalletAct;
import com.stbl.stbl.adapter.BannerPagerAdapter;
import com.stbl.stbl.adapter.CommonBannerAdapter;
import com.stbl.stbl.adapter.mall.MallAdapter;
import com.stbl.stbl.adapter.mall.MallClassAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.GoodsClass;
import com.stbl.stbl.model.HomeLikeItem;
import com.stbl.stbl.model.MallAll;
import com.stbl.stbl.model.MallInfo;
import com.stbl.stbl.ui.BaseClass.STBLActivity;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.GuideUtil;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.AutoScrollViewPager;
import com.stbl.stbl.widget.BannerView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class MallAct extends STBLActivity implements OnClickListener, FinalHttpCallback, OnInputListener {
    SwipeRefreshLayout swipeRefreshLayout;

    private BannerView mBannerView;
    private BannerPagerAdapter mBannerAdapter;
    private ImageView btn_topRight;

    private PopupWindow popupWindow;
    private ViewGroup mainLayout;
    /**
     * 半透明遮罩
     */
    private ImageView mMask;

    private MallGoods[] mDataHot;
    private MallGoods[] mDataLike;

    private GridView hotGrid, maylikeGrid;
    private MallInfo mallInfo;

    View rightPopupWindow;

    int typeSource;
    GridView gridClass;
    private DataCacheDB cacheDB = new DataCacheDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.mall_main, null, true);
        setContentView(mainLayout);
        EventBus.getDefault().register(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe);
        // 设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getMallAll();
            }
        });

        mMask = new ImageView(this);
        mMask.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mMask.setBackgroundColor(Color.parseColor("#86222222"));

        findViewById(R.id.top_left).setOnClickListener(this);
        findViewById(R.id.et_search).setOnClickListener(this);
        btn_topRight = (ImageView) findViewById(R.id.top_right);
        btn_topRight.setOnClickListener(this);

        initView();
        typeSource = SharedMallType.getType(this);

        swipeRefreshLayout.setProgressViewOffset(false, 0, Util.dip2px(this, 24));
        swipeRefreshLayout.setRefreshing(true);
        updateMallAll(cacheDB.getMallHomePageCacheJson());
        getMallAll();
        if (!GuideUtil.guidePageMall(this)) {
            show(pageMall);
        }
    }

    MallClassAdapter adapter;

    void initView() {
        gridClass = (GridView) findViewById(R.id.gridClass);
        gridClass.setSelector(getResources().getDrawable(R.color.transparent));
        hotGrid = (GridView) findViewById(R.id.mall_hot_grid);
        maylikeGrid = (GridView) findViewById(R.id.mall_maylike_grid);
        mBannerView = (BannerView) findViewById(R.id.banner);
        mBannerView.post(new Runnable() {
            @Override
            public void run() {
                mBannerView.getLayoutParams().height = (int) (Device.getWidth(mActivity) * CommonBannerAdapter.rateOfWH);
            }
        });
        mBannerView.getViewPager().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

    void showPayPwdWindow() {
        PayingPwdDialog dialog = new PayingPwdDialog();
        dialog.setOnInputListener(this);
        dialog.show(this);
    }

    /**
     * 获取数据
     */
    void getMallAll() {
        LogUtil.logE("malltype:" + typeSource);
        Params params = new Params();
        params.put("malltype", typeSource);
        new HttpEntity(this).commonPostData(Method.getMallAll, params, this);
    }

    public void onEvent(EventType type) {
        switch (type.getType()) {
            case EventType.TYPE_MALL_NUM_CHANGE:
                getNumInfo();
                break;
        }
    }

    // 获取数量
    void getNumInfo() {
        new HttpEntity(this).commonPostData(Method.getMallInfo, null, this);
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
                    startActivity(new Intent(MallAct.this, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        }
        return isTemp;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left:
                // showPayPwdWindow();
                finish();
                break;
            case R.id.top_right:
                if (isDealTempAccount())
                    return;
                createPopWindow(mainLayout);
                break;
            case R.id.et_search:
                MobclickAgent.onEvent(this, UmengClickEventHelper.SCSS);
                startActivity(MallSearchAct2.class);
                break;
//            case R.id.btn_dianzi:
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha() {
        if (null == mMask.getParent()) {
            mainLayout.addView(mMask);
        } else {
            ((ViewGroup) mMask.getParent()).removeView(mMask);
        }
    }

    public void createPopWindow(View parent) {
        backgroundAlpha();// 更改到这里 至关重要
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
            return;
        }
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        rightPopupWindow = getLayoutInflater().inflate(R.layout.mall_right_pop, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(rightPopupWindow, Util.dip2px(this, 260), Device.getHeight(this), true);
        // 给popupWindow添加背景才能响应返回键
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popupWindow.setBackgroundDrawable(dw);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.ani_mall_right_pop);
        // 这里是位置显示方式,在屏幕的左侧
        popupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
        // 点击其他地方消失
        rightPopupWindow.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // TODO Auto-generated method stub
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha();
            }
        });

        final UserItem user = ((MyApplication) getApplication()).getUserItem();
        OnClickListener onClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入之前，就先去掉遮罩层
                popupWindow.dismiss();
                switch (v.getId()) {
                    case R.id.btn_order:
                        startActivity(MallOrderAct.class);
                        break;
                    case R.id.btn_shoppingcart:
                        startActivity(MallCartAct.class);
                        break;
                    case R.id.btn_adress:
                        startActivity(MallAddressAct.class);
                        break;
//                    case R.id.btn_coupon:
//                        startActivity(MallCouponAct.class);
//                        break;
                    case R.id.btn_wallet:
                        startActivity(MineWalletAct.class);
                        break;
                    case R.id.imgUser:
                        Intent intent = new Intent(MallAct.this, TribeMainAct.class);
                        intent.putExtra("userId", user.getUserid());
                        startActivity(intent);
                        break;
                }

            }
        };
        View btn_order = rightPopupWindow.findViewById(R.id.btn_order);
        btn_order.setOnClickListener(onClick);
        View btn_shoppingcart = rightPopupWindow.findViewById(R.id.btn_shoppingcart);
        btn_shoppingcart.setOnClickListener(onClick);
        View btn_adress = rightPopupWindow.findViewById(R.id.btn_adress);
        btn_adress.setOnClickListener(onClick);
//        View btn_coupon = rightPopupWindow.findViewById(R.id.btn_coupon);
//        btn_coupon.setOnClickListener(onClick);
        View btn_wallet = rightPopupWindow.findViewById(R.id.btn_wallet);
        btn_wallet.setOnClickListener(onClick);
        // 显示数据信息
        setTextNum();
        // 显示个人信息
        ImageView imgUser = (ImageView) rightPopupWindow.findViewById(R.id.imgUser);
        imgUser.setOnClickListener(onClick);
        TextView tvName = (TextView) rightPopupWindow.findViewById(R.id.tvName);
        TextView tvGenderAge = (TextView) rightPopupWindow.findViewById(R.id.tvGenderAge);
        TextView tvCity = (TextView) rightPopupWindow.findViewById(R.id.tvCity);

        if (user == null)
            return;
        String imgUrl = user.getImgurl();
        if (imgUrl != null && !imgUrl.equals("")) {
            PicassoUtil.load(this, user.getImgurl(), imgUser);
        }
        tvName.setText(user.getNickname());

        tvCity.setText(user.getCityname());
        tvGenderAge.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            tvGenderAge.setBackgroundResource(R.drawable.shape_boy_bg);
            tvGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
        } else if (user.getGender() == UserItem.gender_girl) {
            tvGenderAge.setBackgroundResource(R.drawable.shape_girl_bg);
            tvGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
        } else {
            tvGenderAge.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
            tvGenderAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

    }

    private void startActivity(Class<?> cls) {
        Intent it = new Intent(this, cls);
        it.putExtra("typeSource", typeSource);
        startActivity(it);
    }

    @Override
    public void parse(String methodName, String result) {

        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.getMallAll:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getMallAll: {
                cacheDB.saveMallHomePageCacheJson(obj);
                updateMallAll(obj);
            }
            break;
            case Method.getMallInfo:
                mallInfo = JSONHelper.getObject(obj, MallInfo.class);
                setTextNum();
                break;
        }

    }

    private void updateMallAll(String json) {
        if (json == null || json.equals("")) return;
        MallAll mall = JSONHelper.getObject(json, MallAll.class);
        swipeRefreshLayout.setRefreshing(false);
        // 热门
        List<HomeLikeItem> listHot = mall.getHotproductview();
        if (listHot != null) {
            if (listHot.size() > 0) {
                hotGrid.setAdapter(new MallAdapter(this, listHot));
                ViewUtils.setAdapterViewHeight(hotGrid, 2);
            }
        }

        // 猜你喜欢
        List<HomeLikeItem> listLike = mall.getLikeproductview();
        if (listLike != null) {
            if (listLike.size() > 0) {
                MallAdapter adapter = new MallAdapter(this, listLike);
                adapter.recommend = false;
                maylikeGrid.setAdapter(adapter);
                ViewUtils.setAdapterViewHeight(maylikeGrid, 2);
            }
        }

        // 数据
        mallInfo = mall.getMallinfoview();
        List<Banner> banners = mall.getBannerview();
        if (banners != null) {
            setBannerPager(banners);
        }

        // 类别
        final List<GoodsClass> listGoodsType = mall.getMallclassview();
        if (listGoodsType != null && listGoodsType.size() > 0) {
            MallClassAdapter adapter = new MallClassAdapter(this, listGoodsType);
            gridClass.setAdapter(adapter);
            // adapter.setData(listGoodsType);
            ViewUtils.setAdapterViewHeight(gridClass, 4, 10);
        }
    }

    /**
     * 显示banner数据
     *
     * @param listBanner
     */
    void setBannerPager(final List<Banner> listBanner) {
        mBannerAdapter = new BannerPagerAdapter((ArrayList<Banner>) listBanner);
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager pager, int position) {
                if (isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.SCBN);
                Banner banner = listBanner.get(position);
                CommonBannerAdapter.dealBannerJump(mActivity, banner);
            }
        });
    }

    // 方法有破绽，rightPopupWindow可能为
    void setTextNum() {
        if (mallInfo != null && rightPopupWindow != null) {
            if (mallInfo.getOrdercount() > 0) {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvOrderCount);
                tv.setVisibility(View.VISIBLE);
                tv.setText(String.valueOf(mallInfo.getOrdercount()));
            } else {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvOrderCount);
                tv.setVisibility(View.GONE);
            }
            if (mallInfo.getCartgoodscount() > 0) {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvCartCount);
                tv.setVisibility(View.VISIBLE);
                tv.setText(String.valueOf(mallInfo.getCartgoodscount()));
            } else {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvCartCount);
                tv.setVisibility(View.GONE);
            }
            if (mallInfo.getTicketscount() > 0) {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvDiscountCount);
                tv.setVisibility(View.VISIBLE);
                tv.setText(String.valueOf(mallInfo.getTicketscount()));
            } else {
                TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvDiscountCount);
                tv.setVisibility(View.GONE);
            }

            TextView tv = (TextView) rightPopupWindow.findViewById(R.id.tvMoneyCount);
//			tv.setText(String.valueOf(mallInfo.getWalletamount()));
            tv.setText(Math.round(mallInfo.getJindoucount()) + getString(R.string.mall_gold));

        }
    }

    @Override
    public void onInputFinished(String pwd) {
        LogUtil.logE("MallAct:" + pwd);
    }

}
