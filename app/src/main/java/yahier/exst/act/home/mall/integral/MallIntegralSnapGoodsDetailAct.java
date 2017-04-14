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
import com.stbl.stbl.model.GoodsCount;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.model.MallIntegralSnapGoods;
import com.stbl.stbl.model.ShopInfo;
import com.stbl.stbl.model.Sku;
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
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.ScrollChangeScrollView;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 兑换商品快照详细页
 *
 * @author Administrator
 */

public class MallIntegralSnapGoodsDetailAct extends STBLBaseActivity implements View.OnClickListener {

    private long orderdetailid;//商品id
    private TextView tvTitle;//商品名称
    private TextView tvPrice; //商品价格
    private ImageView ivScrollTop; //返回顶部
    private WebView web; //商品介绍网页
    protected ScrollChangeScrollView scrollView;//用于监听滑动的scrollview
    private FrameLayout pointPager; //banner
    private RelativeLayout rlTitleRoot; //标题栏
    private MallIntegralSnapGoods goods;//商品详细信息
    private int bannerHeight = 0; //banner高度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_integral_snap_goods_detail);
        initView();
        orderdetailid = getIntent().getLongExtra("goodsid", 0);
        LogUtil.logE("goodsid:" + orderdetailid);
        if (orderdetailid == 0) {
            ToastUtil.showToast(this, getString(R.string.no_goods));
        } else {
            getSnapDetail(orderdetailid);
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
        ivScrollTop = (ImageView) findViewById(R.id.iv_scroll_top);
        pointPager = (FrameLayout) findViewById(R.id.pager_point_fra);
        rlTitleRoot = (RelativeLayout) findViewById(R.id.rl_title_root);

        findViewById(R.id.top_left).setOnClickListener(this);

        ivScrollTop.setOnClickListener(this);

        scrollView.setOnScrollListener(new ScrollChangeScrollView.OnScrollListener() {
            @Override
            public void onScroll(int x, int y, int oldx, int oldy) {
                if (bannerHeight == 0) bannerHeight = pointPager.getHeight();
                if (y <= 0 || y <= bannerHeight/2) {
                    rlTitleRoot.setBackgroundColor(Color.argb(0, 252, 213, 50));//AGB
                } else if (y > 0 && y <= bannerHeight) {
                    float scale = (float) (y - bannerHeight/2) / (bannerHeight/2);
                    if (scale < 0) {
                        scale = 0;
                    }
                    float alpha = (255 * scale);
                    // 只是layout背景透明
                    rlTitleRoot.setBackgroundColor(Color.argb((int) alpha, 252, 213, 50));
                } else {
                    rlTitleRoot.setBackgroundColor(Color.argb(255, 252, 213, 50));
                }
            }
        });
    }

    /**
     * 获取商品快照详情
     * @param orderdetailid
     */
    void getSnapDetail(long orderdetailid) {
        Params params = new Params();
        params.put("orderdetailid", orderdetailid);
        new HttpEntity(this).commonPostData(Method.getPointsmallOrderSnapShow, params, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    void initPagerPoint() {
        final LinearLayout pointLin;
        ViewPager pager;
        float rateOfPagerWH = 1f;
        pager = (ViewPager) findViewById(R.id.pager);
        ArrayList<String> urls = new ArrayList<>();
        urls.add(goods.getFimgurl());
        final ArrayList<String> imgUrls = urls;
        BannerAdapter adapter = new BannerAdapter(this);
        adapter.setUrlArray(imgUrls);
        pager.setAdapter(adapter);
        int height = (int) (Device.getWidth(this) * rateOfPagerWH);
        pointPager.setLayoutParams(new LinearLayout.LayoutParams(Device.getWidth(this), height));
//        pointLin = (LinearLayout) findViewById(R.id.point_lin);
//        if (imgUrls != null) {
//            for (int i = 0; i < imgUrls.size(); i++) {
//                ImageView img = new ImageView(this);
//                if (i == 0) {
//                    img.setImageResource(R.drawable.icon_list_select);
//                } else {
//                    img.setImageResource(R.drawable.icon_list_noselect);
//                }
//                pointLin.addView(img);
//            }
//            pager.setOnPageChangeListener(new MyOnPageChangeListener() {
//
//                @Override
//                public void onPageSelected(int index) {
//                    while (index >= imgUrls.size()) {
//                        index = index - imgUrls.size();
//                    }
//                    for (int i = 0; i < imgUrls.size(); i++) {
//                        ((ImageView) pointLin.getChildAt(i)).setImageResource(R.drawable.icon_list_noselect);
//                    }
//                    ((ImageView) pointLin.getChildAt(index)).setImageResource(R.drawable.icon_list_select);
//                }
//            });
//        }
    }

    @Override
    public void onClick(final View v) {
        Intent it;
        switch (v.getId()) {
            case R.id.top_left:
                finish();
                break;
            case R.id.iv_scroll_top: //返回头部
                scrollView.scrollTo(0, 0);
                break;
        }
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
            case Method.getPointsmallOrderSnapShow: //商品详细
                goods = JSONHelper.getObject(obj, MallIntegralSnapGoods.class);
                if (goods == null) return;
                tvTitle.setText(goods.getGoodsname());
                float minPrice = goods.getPrice();
                tvPrice.setText(""+(int)minPrice);
                web.loadUrl(goods.getGoodsinfourl());

                initPagerPoint();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

