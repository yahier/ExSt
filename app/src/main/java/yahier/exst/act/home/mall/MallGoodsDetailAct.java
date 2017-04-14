package yahier.exst.act.home.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.adapter.mall.MallRecommendAdapter;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.common.RViewHolder;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.GoodsCollect;
import com.stbl.stbl.model.GoodsCount;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.model.HomeLikeItem;
import com.stbl.stbl.model.HorizontalListView;
import com.stbl.stbl.model.MallInfo;
import com.stbl.stbl.model.ShopInfo;
import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.UIUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.ScrollChangeScrollView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imlib.model.Conversation;

/**
 * 商品详细页
 *
 * @author Administrator
 */

public class MallGoodsDetailAct extends STBLBaseActivity implements OnClickListener {

    private AlertDialog mDialog;
    private ArrayList<WishItem> mGoodsData = new ArrayList<WishItem>();
    private WishAdapter mWishAdapter;
    long goodsid;
    TextView tvCollect, tvRemark, tvTitle;
    TextView tvSaleCount, tvIsPack, tvPrice;
    GoodsDetail goods;
    ImageView imgCollect;
    boolean isCollected = false;// 当前没有返回该对应的字段
    ImageView imgUser, ivTopGoodsCart, ivContractMerchant; //商家头像，头部购物车,联系卖家
    ImageView ivScrollTop; //返回顶部
    TextView tvUserName;
    TextView tvCartNum;
    WebView web;
    protected ScrollChangeScrollView scrollView;
    private TextView tvReport;
    private HorizontalListView recommendListView; //推荐商品
    private RelativeLayout rlRecommendParent;//推荐商品父控
    private Button btnAddCart; //加入购物车按钮
    private FrameLayout pointPager; //banner
    private RelativeLayout rlTitleRoot; //标题栏
    private int bannerHeight = 0; //banner高度
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_goods_detail);
        initView();
        EventBus.getDefault().register(this);
        findViewById(R.id.icon_gouwuche).setOnClickListener(this);
        goodsid = getIntent().getLongExtra("goodsid", 0);
        LogUtil.logE("goodsid:" + goodsid);
        if (goodsid == 0) {
            ToastUtil.showToast(this, getString(R.string.mall_no_goods));
        } else {
            getDetail(goodsid);
            getNumInfo();
            getRecommendGoods(goodsid);
        }
    }

    void initView() {
        web = (WebView) findViewById(R.id.webView1);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);

        WebSettings setting = web.getSettings();
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        scrollView = (ScrollChangeScrollView) findViewById(R.id.scroll);
        tvTitle = (TextView) findViewById(R.id.title);
        tvCollect = (TextView) findViewById(R.id.collectText);
        tvRemark = (TextView) findViewById(R.id.tvRemark);
        tvSaleCount = (TextView) findViewById(R.id.goodsSaleCount);
        tvIsPack = (TextView) findViewById(R.id.isPack);
        tvPrice = (TextView) findViewById(R.id.price);
        imgCollect = (ImageView) findViewById(R.id.collectImg);
        tvCartNum = (TextView) findViewById(R.id.tv_top_cart_num);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        ivTopGoodsCart = (ImageView) findViewById(R.id.iv_top_goods_cart);
        ivContractMerchant = (ImageView) findViewById(R.id.iv_contract_merchant);
        ivScrollTop = (ImageView) findViewById(R.id.iv_scroll_top);
        tvReport = (TextView) findViewById(R.id.tv_report);
        recommendListView = (HorizontalListView) findViewById(R.id.list_headmen);
        rlRecommendParent = (RelativeLayout) findViewById(R.id.rl_recommend_parent);
        btnAddCart = (Button) findViewById(R.id.button1);
        pointPager = (FrameLayout) findViewById(R.id.pager_point_fra);
        rlTitleRoot = (RelativeLayout) findViewById(R.id.rl_title_root);

        recommendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeLikeItem item = (HomeLikeItem) parent.getAdapter().getItem(position);
                Intent it = new Intent(MallGoodsDetailAct.this, MallGoodsDetailAct.class);
                it.putExtra("goodsid", item.getBusinessid());
                startActivity(it);
            }
        });

        findViewById(R.id.top_left).setOnClickListener(this);

        ivTopGoodsCart.setOnClickListener(this);
        ivContractMerchant.setOnClickListener(this);
        ivScrollTop.setOnClickListener(this);
        tvReport.setOnClickListener(this);
        tvCollect.setOnClickListener(this);

        scrollView.setOnScrollListener(new ScrollChangeScrollView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y, int oldx, int oldy) {
                if (bannerHeight == 0) bannerHeight = pointPager.getHeight();
                rlTitleRoot.setBackgroundColor(UIUtils.getResColor(y > bannerHeight ? R.color.title_color : R.color.transparent));
            }
        });
    }

    void getDetail(long goodsid) {
        Params params = new Params();
        params.put("goodsid", goodsid);
        new HttpEntity(this).commonPostData(Method.goodsDetail, params, this);
    }

    //获取推荐商品
    private void getRecommendGoods(long goodsid) {
        Params params = new Params();
        params.put("goodsid", goodsid);
        new HttpEntity(this).commonPostData(Method.getRecommendGoods, params, this);
    }

    void doCollect() {
        Params params = new Params();
        params.put("goodsid", goodsid);
        new HttpEntity(this).commonPostData(Method.goodsCollectOrCancel, params, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("deprecation")
    void initPagerPoint() {
        final LinearLayout pointLin;
        ViewPager pager;
        float rateOfPagerWH = 1f;
//		FrameLayout pointPager = (FrameLayout) findViewById(R.id.pager_point_fra);
        pager = (ViewPager) findViewById(R.id.pager);
        final ArrayList<String> imgUrls = goods.getBannerimgurls();
        BannerAdapter adapter = new BannerAdapter(this);
        adapter.setUrlArray(imgUrls);
        pager.setAdapter(adapter);
        int height = (int) (Device.getWidth(this) * rateOfPagerWH);
        pointPager.setLayoutParams(new LinearLayout.LayoutParams(Device.getWidth(this), height));
        pointLin = (LinearLayout) findViewById(R.id.point_lin);
        if (imgUrls != null) {
            for (int i = 0; i < imgUrls.size(); i++) {
                ImageView img = new ImageView(this);
                if (i == 0) {
                    img.setImageResource(R.drawable.icon_list_select);
                } else {
                    img.setImageResource(R.drawable.icon_list_noselect);
                }
                pointLin.addView(img);
            }
            pager.setOnPageChangeListener(new MyOnPageChangeListener() {

                @Override
                public void onPageSelected(int index) {
                    while (index >= imgUrls.size()) {
                        index = index - imgUrls.size();
                    }
                    for (int i = 0; i < imgUrls.size(); i++) {
                        ((ImageView) pointLin.getChildAt(i)).setImageResource(R.drawable.icon_list_noselect);
                    }
                    ((ImageView) pointLin.getChildAt(index)).setImageResource(R.drawable.icon_list_select);
                }
            });
        }
    }

    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, Config.interClickTime);
        Intent it;
        switch (v.getId()) {
            case R.id.top_left:
                finish();
                break;
            case R.id.top_right:
                if(isDealTempAccount()){
                    return;
                }
                if (goods == null) return;
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.SPFX);
                ShareItem shareItem = new ShareItem();
                shareItem.setTitle(goods.getGoodsname());
                shareItem.setContent(getString(R.string.mall_share_tips));
                List<String> banners = goods.getBannerimgurls();
                if (banners.size() > 0) {
                    shareItem.setImgUrl(banners.get(0));
                }
                //new CommonShare().showShareWindow(this, String.valueOf(CommonShare.sharedMiGoods), String.valueOf(goods.getGoodsid()), null, shareItem);
                new ShareDialog(this).shareGoods(goods.getGoodsid(), shareItem);
                break;
            case R.id.btn_wish: //回到首页
//			showWishPop();
                it = new Intent(this, MallAct.class);
                SharedMallType.putType(this, SharedMallType.typeSourceDefault);
                startActivity(it);
                break;
            case R.id.button1: // 加入购物车
                if(isDealTempAccount()){
                    return;
                }
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JRGWC);
                // 该型号库存大于0，并且选择的数量小于或等于库存数量
                if (goods != null && goods.getSkulist() != null && goods.getSkulist().size() == 1) {
                    if (goods.getSkulist().get(0).getStockcount() > 0) {
                        btnAddCart.setEnabled(false);//防止重复点击
                        addShoppoingCart(goods.getSkulist().get(0).getSkuid());
                    } else {
                        ToastUtil.showToast(this, getString(R.string.mall_store_no_goods));
                    }
                    break;
                }
                // 0和1的状态值看进入的页面吧
                it = new Intent(this, MallTypeChoiceAct.class);
                it.putExtra("item", goods);
                it.putExtra("typeOperate", 0);
                it.putExtra("cartNum", Integer.parseInt(tvCartNum.getText().toString()));
                startActivity(it);
                // finish();
                return;
            case R.id.button2: // 立即购买
                if(isDealTempAccount()){
                    return;
                }
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.LJGM);
                it = new Intent(this, MallTypeChoiceAct.class);
                it.putExtra("typeOperate", 1);
                it.putExtra("item", goods);
                it.putExtra("cartNum", Integer.parseInt(tvCartNum.getText().toString()));
                startActivity(it);
                // finish();
                return;
            case R.id.lin_comment:
                if (null != goods) {
                    it = new Intent(this, GoodsRemarkAct.class);
                    it.putExtra("goodsid", goods.getGoodsid());
                    startActivity(it);
                }
                break;
            case R.id.collectText:
            case R.id.collectImg:
                if(isDealTempAccount()){
                    return;
                }
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.SCSP);
                doCollect();
                break;
            case R.id.lin_user:
                if(isDealTempAccount()){
                    return;
                }
                if (goods == null)
                    return;
                it = new Intent(this, TribeMainAct.class);
                it.putExtra("userId", goods.getShopinfoview().getShopid());
                startActivity(it);
                break;
//			case R.id.icon_gouwuche:
//				it = new Intent(this, MallCartAct.class);
//				startActivity(it);
//				break;
            case R.id.iv_top_goods_cart: //头部购物车
                if(isDealTempAccount()){
                    return;
                }
                it = new Intent(this, MallCartAct.class);
                startActivity(it);
                break;
            case R.id.tv_report: //举报
                it = new Intent(this, ReportStatusesOrUserAct.class);
                it.putExtra("type", 5);
                it.putExtra("referenceid", goods.getGoodsid());
                startActivity(it);
                break;
            case R.id.iv_scroll_top: //返回头部
                scrollView.scrollTo(0, 0);
                break;
            case R.id.iv_contract_merchant: //联系卖家
                if(isDealTempAccount()){
                    return;
                }
                if (goods == null)
                    return;
                ShopInfo shopInfo = goods.getShopinfoview();
                ThemeActivity.isMerchant(shopInfo.getContactshopid());
//                chat(shopInfo.getContactshopid(), shopInfo.getShopname(), goods);
                chat(shopInfo.getContactshopid(), shopInfo.getAlias() == null || shopInfo.getAlias().equals("") ? shopInfo.getShopname() : shopInfo.getAlias(), goods);
                break;
        }
    }




    void chat(String targetUserId, String title, GoodsDetail goods) {
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.putExtra("goods", goods);
        startActivity(intent);
    }

    private Runnable showDialogRunnable = new Runnable() {
        @Override
        public void run() {
            showDialog(getString(R.string.mall_adding_to_car));
        }
    };
    //加入购物车
    void addShoppoingCart(long skuId) {
        mHandler.postDelayed(showDialogRunnable,700);
        Params params = new Params();
        params.put("skuid", skuId);
        params.put("goodscount", 1);
        params.put("malltype", 0);
        new HttpEntity(this).commonPostData(Method.addShoppingCart, params, this);
    }

    /**
     * 许愿
     */
//    private void showWishPop() {
//        if (null == mDialog) {
//            mDialog = new AlertDialog.Builder(this).create();
//            mDialog.show();
//            mDialog.getWindow().setContentView(R.layout.mall_pop_wish);
//            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//            mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//
//            ListView lv_wish = (ListView) mDialog.getWindow().findViewById(R.id.lv_wish);
//            OnClickListener onClick = new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    switch (v.getId()) {
//                        case R.id.button1: // 取消
//                            break;
//                        case R.id.button2: // 确定
//
//                            break;
//                    }
//                    mDialog.dismiss();
//                }
//            };
//            mDialog.getWindow().findViewById(R.id.button1).setOnClickListener(onClick);
//            mDialog.getWindow().findViewById(R.id.button2).setOnClickListener(onClick);
//            WishItem item = new WishItem();
//            item.name = "印花圆领字母套头时尚卫衣";
//            item.price = 234.00f;
//            item.imgUrl = "http://d05.res.meilishuo.net/pic/r/2a/27/9da3ac5e4b664189a42d400ad736_800_800.c6.jpg";
//            mGoodsData.add(item);
//
//            if (null == mWishAdapter)
//                mWishAdapter = new WishAdapter(this, mGoodsData);
//            else
//                mWishAdapter.notifyDataSetChanged();
//            lv_wish.setAdapter(mWishAdapter);
//        } else if (!mDialog.isShowing()) {
//            mDialog.show();
//        }
//    }

    class WishAdapter extends RCommonAdapter<WishItem> {
        public WishAdapter(Activity act, List<WishItem> mDatas) {
            super(act, mDatas, R.layout.seller_wish_item);
        }

        @Override
        public void convert(RViewHolder helper, WishItem item) {
            helper.setImageByUrl(R.id.goods_img, item.imgUrl);
            helper.setText(R.id.name, item.name);
            helper.setText(R.id.tv_price, "" + item.price);
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
                    startActivity(new Intent(MallGoodsDetailAct.this, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        }
        return isTemp;
    }

    void getNumInfo() {
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        if (UserRole.isTemp(roleFlag)){
            return;
        }
        new HttpEntity(this).commonPostData(Method.getMallInfo, null, this);
    }

    public void onEvent(EventType type) {
        switch (type.getType()) {
            case EventType.TYPE_MALL_NUM_CHANGE:
                getNumInfo();
                break;
            case EventType.TYPE_FINISH_GOODS_DETAIL://支付完了，结束商品详情页
                finish();
                break;
        }
    }

    class WishItem {
        public String imgUrl;
        public String name;
        public float price;
    }

    void setListener() {
        findViewById(R.id.top_right).setOnClickListener(this);
        findViewById(R.id.btn_wish).setOnClickListener(this);
        findViewById(R.id.lin_comment).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        imgCollect.setOnClickListener(this);
        findViewById(R.id.lin_user).setOnClickListener(this);
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        switch (methodName) {
            case Method.addShoppingCart:
                mHandler.removeCallbacks(showDialogRunnable);
                dissmissDialog();
                btnAddCart.setEnabled(true);
                break;
        }

    }

    @Override
    public void httpParseResult(String methodName, String result, String obj) {
        switch (methodName) {
            case Method.goodsDetail:
                goods = JSONHelper.getObject(obj, GoodsDetail.class);
                tvTitle.setText(goods.getGoodsname());
                setListener();
                float minPrice = goods.getMinprice();
                float maxPrice = goods.getMaxprice();
                if (minPrice == maxPrice) {
                    tvPrice.setText("￥" + String.valueOf(minPrice));
                } else {
                    tvPrice.setText("￥" + minPrice + "~" + maxPrice);
                }

                if (goods.getIscollect() == Goods.iscollectYes) {
                    isCollected = true;
                    imgCollect.setImageResource(R.drawable.icon_goods_collect_yes);
                } else {
                    imgCollect.setImageResource(R.drawable.icon_goods_collect_no);
                }
                if (goods.getIspinkage() == GoodsDetail.ispinkageYes) {
                    tvIsPack.setText(R.string.mall_exemption_from_postage);
                } else {
                    tvIsPack.setVisibility(View.GONE);
                }
                GoodsCount ccount = goods.getAccount();
                if (ccount == null) {
                    ToastUtil.showToast(this, getString(R.string.mall_no_count));
                    break;
                }

                tvCollect.setText(String.valueOf(ccount.getCollectcount()));
                tvRemark.setText(String.valueOf(ccount.getCommentcount())+getString(R.string.mall_strip));
                tvSaleCount.setText(getString(R.string.mall_sales) + ccount.getSalecount());

                web.loadUrl(goods.getGoodsinfourl());

                // 店铺信息
                ShopInfo shopInfo = goods.getShopinfoview();
                if (shopInfo != null) {
                    PicassoUtil.load(this, shopInfo.getShopimgurl(), imgUser);
//                    tvUserName.setText(shopInfo.getShopname());
                    tvUserName.setText(shopInfo.getAlias() == null || shopInfo.getAlias().equals("") ? shopInfo.getShopname() : shopInfo.getAlias());
                }

                float relPrice = goods.getSkulist().get(0).getRealprice();
                LogUtil.logE("relPrice:" + relPrice);

                initPagerPoint();
                break;
            case Method.goodsCollectOrCancel:
                GoodsCollect collect = JSONHelper.getObject(obj, GoodsCollect.class);
                tvCollect.setText(String.valueOf(collect.getCollectioncount()));
                if (collect.getOptype() == GoodsCollect.optypeSave) {
                    ToastUtil.showToast(this, getString(R.string.mall_collect_success));
                    imgCollect.setImageResource(R.drawable.icon_goods_collect_yes);
                } else {
                    ToastUtil.showToast(this, getString(R.string.mall_cancel_collect_success));
                    imgCollect.setImageResource(R.drawable.icon_goods_collect_no);
                }
                break;
            case Method.getMallInfo:
                MallInfo mallInfo = JSONHelper.getObject(obj, MallInfo.class);
                tvCartNum.setText(String.valueOf(mallInfo.getCartgoodscount()));
                break;
            case Method.getRecommendGoods: //获取推荐商品
                // 猜你喜欢
                List<HomeLikeItem> listLike = JSONHelper.getList(obj, HomeLikeItem.class);
                if (listLike != null) {
                    if (listLike.size() > 0) {
                        rlRecommendParent.setVisibility(View.VISIBLE);
                        recommendListView.setAdapter(new MallRecommendAdapter(this, listLike));
                        recommendListView.setDividerWidth(com.stbl.stbl.util.Util.dip2px(this, 5));
                    }
                }
                break;
            case Method.addShoppingCart:
                mHandler.removeCallbacks(showDialogRunnable);
                dissmissDialog();
                btnAddCart.setEnabled(true);
                ToastUtil.showToast(this, getString(R.string.mall_add_success));
                EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                getNumInfo();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
