package yahier.exst.act.home.mall.integral;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.BannerAdapter;
import com.stbl.stbl.act.home.mall.MallOrderCommitAct;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.item.WealthInfo;
import com.stbl.stbl.model.GoodsCount;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.model.ShopInfo;
import com.stbl.stbl.model.Sku;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.MyOnPageChangeListener;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UIUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.ScrollChangeScrollView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 兑换商品详细页
 *
 * @author Administrator
 */

public class MallIntegralGoodsDetailAct extends STBLBaseActivity implements View.OnClickListener {

    private long goodsid;//商品id
    private TextView tvTitle;//商品名称
    private TextView tvPrice; //商品价格
    //    private View llContractMerchant; //联系卖家
    private ImageView ivScrollTop; //返回顶部
    private WebView web; //商品介绍网页
    protected ScrollChangeScrollView scrollView;//用于监听滑动的scrollview
    private FrameLayout pointPager; //banner
    private RelativeLayout rlTitleRoot; //标题栏
    private TextView tvTotalPrice;//总价格
    private Button btnConfirm;//立即兑换
    private TextView tvExpressCost, tvSoldCount, tvRemainCount; //快递费用、已兑换量、剩余数
    private GoodsDetail goods;//商品详细信息
    private int bannerHeight = 0; //banner高度
    private int integralBalance; //师徒票余额值

    private boolean isGoodsSnapshot = false;//是否商品快照

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_integral_goods_detail);
        isGoodsSnapshot = getIntent().getBooleanExtra("isGoodsSnapshot", false);
        initView();
        EventBus.getDefault().register(this);
        goodsid = getIntent().getLongExtra("goodsid", 0);

        //如果没有传，则重新获取.并在获取余额之后 再获取商品详细
        if (integralBalance == 0) {
            getWalletBalance();
        }else{
            LogUtil.logE("goodsid:" + goodsid);
            if (goodsid == 0) {
                ToastUtil.showToast(this, getString(R.string.no_goods));
            } else {
                if(!isGoodsSnapshot) {
                    getDetail(goodsid);
                }else{
                    getSnapDetail(goodsid);
                }
            }

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
        tvPrice = (TextView) findViewById(R.id.price);
        tvExpressCost = (TextView) findViewById(R.id.tv_express_cost);
        tvSoldCount = (TextView) findViewById(R.id.tv_sold_count);
        tvRemainCount = (TextView) findViewById(R.id.tv_remain_count);
//        llContractMerchant =  findViewById(R.id.ll_contract_merchant);
        ivScrollTop = (ImageView) findViewById(R.id.iv_scroll_top);
        pointPager = (FrameLayout) findViewById(R.id.pager_point_fra);
        rlTitleRoot = (RelativeLayout) findViewById(R.id.rl_title_root);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);


        findViewById(R.id.top_left).setOnClickListener(this);

//        llContractMerchant.setOnClickListener(this);
        ivScrollTop.setOnClickListener(this);

        scrollView.setOnScrollListener(new ScrollChangeScrollView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y, int oldx, int oldy) {
                if (bannerHeight == 0) bannerHeight = pointPager.getHeight();
//                rlTitleRoot.setBackgroundColor(UIUtils.getResColor(y > bannerHeight ? R.color.theme_yellow_50 : R.color.transparent));
                if (y <= 0 || y <= bannerHeight/2) {
                    rlTitleRoot.setBackgroundColor(Color.argb(0, 255, 255, 255));//AGB
                } else if (y > 0 && y <= bannerHeight) {
//                    float scale = (float) y / (bannerHeight - rlTitleRoot.getHeight());
                    float scale = (float) (y - bannerHeight/2) / (bannerHeight/2);
                    if (scale < 0) {
                        scale = 0;
                    }
                    float alpha = (255 * scale);
                    // 只是layout背景透明
                    rlTitleRoot.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {
                    rlTitleRoot.setBackgroundColor(Color.argb(255, 252, 255, 255));
                }
            }
        });
        tvExpressCost.setText(String.format(getString(R.string.express_cost), "0"));
        tvSoldCount.setText(String.format(getString(R.string.exchange_count), 0));
        tvRemainCount.setText(String.format(getString(R.string.remain_count), 0));

        if (isGoodsSnapshot) {
            //隐藏底部栏、联系卖家、分享，设置标题“商品快照”
            TextView title = (TextView) findViewById(R.id.tv_top_title);
            title.setText(getString(R.string.goods_snapshot));
            findViewById(R.id.bottom_bar).setVisibility(View.GONE);
//            llContractMerchant.setVisibility(View.GONE);
            //设置返回顶部按钮位置
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlParams.rightMargin = DimenUtils.dp2px(15);
            rlParams.bottomMargin = DimenUtils.dp2px(15);
            ivScrollTop.setLayoutParams(rlParams);
            findViewById(R.id.top_right).setVisibility(View.GONE);
            //设置scollview底部margin
            RelativeLayout.LayoutParams rlParamsV2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParamsV2.bottomMargin = 0;
            scrollView.setLayoutParams(rlParamsV2);
        }
    }

    /**
     * 获取商品快照详情
     * @param goodsid
     */
    void getSnapDetail(long goodsid) {
        Params params = new Params();
        params.put("goodsid", goodsid);
        new HttpEntity(this).commonPostData(Method.getPointsmallOrderSnapShow, params, this);
    }
    /**
     * 获取商品详情
     *
     * @param goodsid
     */
    void getDetail(long goodsid) {
        Params params = new Params();
        params.put("goodsid", goodsid);
        new HttpEntity(this).commonPostData(Method.getPointsmallProductGet, params, this);
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
        Intent it;
        switch (v.getId()) {
            case R.id.top_left:
                finish();
                break;
            case R.id.top_right:
                if (isDealTempAccount()) {
                    return;
                }
                if (goods == null) return;
                MobclickAgent.onEvent(this, UmengClickEventHelper.DHSPFX);
                ShareItem shareItem = new ShareItem();
                shareItem.setTitle(goods.getGoodsname());
                shareItem.setContent(getString(R.string.mall_share_integral_tips));
                List<String> banners = goods.getBannerimgurls();
                if (banners.size() > 0) {
                    shareItem.setImgUrl(banners.get(0));
                }
                //new CommonShare().showShareWindow(this, String.valueOf(CommonShare.sharedMiGoods), String.valueOf(goods.getGoodsid()), null, shareItem);
                new ShareDialog(this).shareIntegralGoods(goods.getGoodsid(), shareItem);
                break;
            case R.id.btn_confirm: // 立即购买
                if (isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(this, UmengClickEventHelper.LJDHAN);
                Intent intent = new Intent(this, MallOrderCommitAct.class);
                MallCart mall = new MallCart();
                mall.setCartshops(generateBuyData());
                intent.putExtra("item", mall);
                intent.putExtra("isIntegralExchange", true);
                startActivity(intent);
                finish();
                return;
            case R.id.iv_scroll_top: //返回头部
                scrollView.scrollTo(0, 0);
                break;
//            case R.id.ll_contract_merchant: //联系卖家
//                if(isDealTempAccount()){
//                    return;
//                }
//                if (goods == null)
//                    return;
//                ShopInfo shopInfo = goods.getShopinfoview();
//                ThemeActivity.isMerchant(shopInfo.getContactshopid());
//                chat(shopInfo.getContactshopid(), shopInfo.getShopname(), goods);
//                break;
        }
    }

    // 生成购买的数据
    List<MallCartShop> generateBuyData() {
        Sku skuSelected = null;
        if (goods.getSkulist() != null && goods.getSkulist().size() > 0) {
            skuSelected = goods.getSkulist().get(0);
            if (skuSelected == null) return null;
        }
        List<MallCartShop> listShop = new ArrayList<MallCartShop>();
        List<MallCartGoods> listGoods = new ArrayList<MallCartGoods>();
        // goods
        MallCartGoods good = new MallCartGoods();
        good.setGoodsid(goods.getGoodsid());
        good.setGoodsname(goods.getGoodsname());
        good.setImgurl(goods.getFimgurl());
        good.setSkuid(skuSelected.getSkuid());
        good.setSkuname(skuSelected.getSkuname());
        LogUtil.logE("realPrice:" + skuSelected.getOutprice());
        good.setRealprice(skuSelected.getOutprice());
        good.setGoodscount(1);
        good.setSelected(true);
        listGoods.add(good);
        // shop
        MallCartShop shop = new MallCartShop();
        shop.setShopid(goods.getShopid());
        ShopInfo info = goods.getShopinfoview();
        shop.setShopname(info == null ? "" : info.getAlias() == null || info.getAlias().equals("") ? info.getShopname() : info.getAlias());
//        shop.setShopname(info == null ? "" : info.getShopname());
        shop.setCartgoods(listGoods);
        shop.setSelected(true);
        String totalAount = StringUtil.get2ScaleString(skuSelected.getOutprice());
        totalAount = totalAount.equals("") ? String.valueOf(skuSelected.getOutprice()) : totalAount;
        shop.setTotalamount(Float.valueOf(totalAount));
        LogUtil.logE(skuSelected.getOutprice());
        listShop.add(shop);
        return listShop;
    }

//    void chat(String targetUserId, String title, GoodsDetail goods) {
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
//                .appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
//        Intent intent = new Intent("android.intent.action.VIEW", uri);
//        intent.putExtra("goods", goods);
//        startActivity(intent);
//    }


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
            getDetail(goodsid);


        }

        @Override
        public boolean onDestroy() {
            return false;
        }
    };


    //是否临时账户。是则并加入处理。
    boolean isDealTempAccount() {
        boolean isTemp = false;
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
        if (UserRole.isTemp(roleFlag)) {
            isTemp = true;
            TipsDialog.popup(this, getString(R.string.temp_account_msg), getString(R.string.temp_account_cancel), getString(R.string.temp_account_ok), new TipsDialog.OnTipsListener() {
                @Override
                public void onConfirm() {
                    startActivity(new Intent(MallIntegralGoodsDetailAct.this, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        }
        return isTemp;
    }

    public void onEvent(EventType type) {
        switch (type.getType()) {
            case EventType.TYPE_FINISH_GOODS_DETAIL://支付完了，结束商品详情页
                finish();
                break;
        }
    }

    void setListener() {
        findViewById(R.id.top_right).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        switch (methodName) {
//            case Method.addShoppingCart:
//                break;
        }

    }

    @Override
    public void httpParseResult(String methodName, String result, String obj) {
        switch (methodName) {
            case Method.getPointsmallProductGet: //商品详细
                goods = JSONHelper.getObject(obj, GoodsDetail.class);
                tvTitle.setText(goods.getGoodsname());
                setListener();
                float minPrice = goods.getMinprice();
//                float maxPrice = goods.getMaxprice();
                tvPrice.setText("" + (int) minPrice);
                tvTotalPrice.setText("" + (int) minPrice);
                int roleFlag = Integer.valueOf(SharedToken.getRoleFlag());


                if (minPrice < integralBalance || UserRole.isTemp(roleFlag)) {
                    btnConfirm.setEnabled(true);
                    btnConfirm.setText(getString(R.string.immediately_exchange));
                }
                web.loadUrl(goods.getGoodsinfourl());

                initPagerPoint();

                GoodsCount account = goods.getAccount();
                if (account == null) {
                    ToastUtil.showToast(this, getString(R.string.mall_no_count));
                    break;
                }
                tvExpressCost.setText(String.format(getString(R.string.express_cost), goods.getDeliverycosts()));
                LogUtil.logE("saleCount",account.getSalecount());
                tvSoldCount.setText(String.format(getString(R.string.exchange_count), account.getSalecount()));
                tvRemainCount.setText(String.format(getString(R.string.remain_count), account.getStockcount()));
                break;
            case Method.getPointsmallOrderSnapShow:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}

