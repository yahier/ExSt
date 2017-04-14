package yahier.exst.act.home;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.SearchUserAct;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.adapter.BrandPlusAdapter;
import com.stbl.stbl.barcoe.CaptureActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.EnterAd;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.Headmen;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.home.HomeTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.GuideUtil;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedAd;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.HomeHeaderView;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.RefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import io.rong.eventbus.EventBus;

/**
 * 首页模块的主页
 */
public class HomeMainAct extends BaseActivity implements OnClickListener {

    private HomeHeaderView mHeaderView;
    private RefreshListView mRefreshListView;
    private ArrayList<Ad> mAdList;
    private BrandPlusAdapter mAdAdapter;

    private boolean isActRunning = true;
    private boolean isShowAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        initView();

        MyApplication.getContext().setNeedShowAd(true);
        EventBus.getDefault().register(this);
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.SHOW_AD);

        getHomeData(true);
        if (!GuideUtil.isGuidePage1Finished(this))
            show();
    }

    @Override
    protected void setStatusBar() {
    }

    private void initView() {
        findViewById(R.id.iv_search).setOnClickListener(this);
        findViewById(R.id.imgScan).setOnClickListener(this);

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_list_view);
        mHeaderView = new HomeHeaderView(this);
        mRefreshListView.getTargetView().addHeaderView(mHeaderView);

        mAdList = new ArrayList<>();
        mAdAdapter = new BrandPlusAdapter(this, mAdList);
        mRefreshListView.setAdapter(mAdAdapter);

        mRefreshListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeData(false);
            }
        });

        mAdAdapter.setInterface(new BrandPlusAdapter.AdapterInterface() {
            @Override
            public void onItemClick(Ad ad) {
                if (isDealTempAccount()) {
                    return;
                }
                UmengClickEventHelper.onAdClickEvent(HomeMainAct.this);

                Intent intent = new Intent(mActivity, CommonWeb.class);
                intent.putExtra("ad", ad);
                intent.putExtra("type", CommonWeb.typeAd);
                intent.putExtra("isHomeAd", true);
                intent.putExtra("title", getString(R.string.me_ad_detail));
                startActivity(intent);
            }

            @Override
            public void toAdCooperate(Ad ad) {
                AdHelper.toApplyAdCooperateAct(ad, HomeMainAct.this);
            }

        });

        mHeaderView.setChangeBitChiefListener(new HomeHeaderView.ChangeBigChiefDataListener() {
            @Override
            public void change() {
                //换一换操作
                getBigChiefList();
            }
        });
    }

    private void getHomeData(boolean isFirst) {
        mTaskManager.start(HomeTask.getHomeData(isFirst)
                .setCallback(new HttpTaskCallback<HashMap<String, Object>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mRefreshListView.reset();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(HashMap<String, Object> result) {
                        ArrayList<Banner> bannerList = (ArrayList<Banner>) result.get("bannerList");
                        ArrayList<Headmen> menList = (ArrayList<Headmen>) result.get("menList");
                        ArrayList<Ad> adList = (ArrayList<Ad>) result.get("adList");
                        if (bannerList != null && bannerList.size() > 0) {
                            mHeaderView.setBannerView(bannerList);
                        }
                        if (menList != null && menList.size() > 0) {
                            mHeaderView.setBigChiefGridView(menList);
                        }
                        if (adList != null && adList.size() > 0) {
                            mAdList.clear();
                            mAdList.addAll(adList);
                            mAdAdapter.notifyDataSetChanged();
                        }
                    }
                }));
    }

    //换一换大酋长
    private void getBigChiefList() {
        mTaskManager.start(HomeTask.getBigChiefList()
                .setCallback(new HttpTaskCallback<ArrayList<Headmen>>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<Headmen> result) {
                        mHeaderView.setBigChiefGridView(result);
                    }
                }));
    }

    private void show() {
        final View viewWindow = LayoutInflater.from(HomeMainAct.this).inflate(R.layout.home_guide_window, null);
        final Dialog dialog = new Dialog(HomeMainAct.this, R.style.dialog);
        dialog.setContentView(viewWindow, new LinearLayout.LayoutParams(Device.getWidth(), Device.getHeight() - 50));//50写定状态栏高度
        Window window = dialog.getWindow();
        window.setGravity(Gravity.NO_GRAVITY);
        dialog.show();
        ImageView ivTop = (ImageView) viewWindow.findViewById(R.id.iv_top);
        ImageView ivBottom = (ImageView) viewWindow.findViewById(R.id.iv_bottom);
        ivTop.setImageResource(R.drawable.home_guide1);
        ivBottom.setImageResource(R.drawable.home_guide2);

        viewWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GuideUtil.guidePage1Finished(HomeMainAct.this);
                dialog.dismiss();
            }

        });
    }

    private void showAd() {
        LogUtil.logE("showad");
        if (isShowAd) {
            return;
        }
        final EnterAd ad = SharedAd.getAd();
        if (ad == null || ad.getAdid().equals("") || ad.getAdimgurl() == null || ad.getAdimgurl().equals("")) {
            return;
        }

        final View viewWindow = LayoutInflater.from(HomeMainAct.this).inflate(R.layout.home_ad_window, null);
        final ImageView img = (ImageView) viewWindow.findViewById(R.id.imgAd);
        img.getLayoutParams().width = Device.getWidth() - DimenUtils.dp2px(80);
        ImageUtils.loadBitmap(ad.getAdimgurl(), img, new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                LogUtil.logE("ad onLoadingFailed");
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                if (bitmap == null) return false;
                isShowAd = true;
                if (HomeMainAct.this.isFinishing()) return false;
                int containerWidth = Device.getWidth() - DimenUtils.dp2px(80);
                int containerHeight = containerWidth * bitmap.getHeight() / bitmap.getWidth();
                img.setLayoutParams(new LinearLayout.LayoutParams(containerWidth, containerHeight));
                //显示广告.并记录时间
                final Dialog dialog = new Dialog(HomeMainAct.this, R.style.dialog);
                dialog.setContentView(viewWindow, new LinearLayout.LayoutParams(containerWidth, containerHeight));
                //
                Window window = dialog.getWindow();
                window.setGravity(Gravity.CENTER);
                window.setWindowAnimations(R.style.home_add_dialog_animation);
                if (isActRunning)
                    dialog.show();//is your activity running?
                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ad.getAdlinkurl() == null || ad.getAdlinkurl().equals("")) {
                            dialog.dismiss();
                            return;
                        }
                        Intent intent = new Intent(HomeMainAct.this, CommonWeb.class);
                        intent.putExtra("url", ad.getAdlinkurl());
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        isShowAd = false;
                    }
                });
                return false;
            }
        });
    }

    public void onEvent(EventTypeCommon type) {
        if (type.getType() == EventTypeCommon.typeRefreshHomePage) {
            mRefreshListView.setSelection(0);
            getHomeData(false);
        }
    }

    //是否临时账户。是则并加入处理。
    public boolean isDealTempAccount() {
        boolean temp = false;
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag(this));
        if (UserRole.isTemp(roleFlag)) {
            temp = true;
            TipsDialog.popup(this, getString(R.string.temp_account_msg), getString(R.string.temp_account_cancel), getString(R.string.temp_account_ok), new TipsDialog.OnTipsListener() {
                @Override
                public void onConfirm() {
                    startActivity(new Intent(HomeMainAct.this, GuideActivity.class));
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });
        }
        return temp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
        isActRunning = false;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.SHOW_AD:
                    //首次不提示广告
                    LogUtil.logE(" case ACTION.SHOW_AD", "" + GuideUtil.guidePage1(HomeMainAct.this));
                    if (GuideUtil.guidePage1(HomeMainAct.this)) {
                        showAd();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.getContext().getNeedShowAd()) {
            //加一个判断 new
            if (GuideUtil.isGuidePage1Finished(this)) {
                MyApplication.getContext().setNeedShowAd(false);
                CommonTask.getLoginAd();
            }
        }
        mAdAdapter.closeFloatLayout();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TabHome home = (TabHome) getParent();
        return home.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        Intent it = null;
        switch (v.getId()) {
            case R.id.imgScan:
                if (isDealTempAccount()) {
                    return;
                }
                it = new Intent(this, CaptureActivity.class);
                startActivity(it);
                break;
            case R.id.iv_search:
                if (isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(this,UmengClickEventHelper.SYCZYH);
                Intent intent = new Intent(this, SearchUserAct.class);
                startActivity(intent);
                break;
        }
    }
}
