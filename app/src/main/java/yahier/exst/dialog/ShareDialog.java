package yahier.exst.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.base.library.task.TaskManager;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.AdManagerActivity;
import com.stbl.stbl.act.ad.AdReportAct;
import com.stbl.stbl.act.ad.IntroduceAdAct;
import com.stbl.stbl.adapter.ShareGridAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.ad.PublishAdStatusItem;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.FileTask;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import io.rong.eventbus.EventBus;

public class ShareDialog extends Dialog {

    public final static int sharedMiRegister = 10000;
    public final static int sharedMiApp = 10010;
    public final static int sharedMiGoods = 10020;
    public final static int sharedMiIntegralGoods = 10021;
    public final static int sharedMiStatuses = 10030;
    public final static int sharedMiTribe = 10040;
    public final static int sharedShoutu = 10060;//收徒分享
    public final static int sharedAd = 10086;//代付订单分享
    public final static int sharedShoppingCircle = 10090;//购物圈分享
    public final static int sharedBrandPlus = 10091;//品牌+分享


    private GridView mGridView;
    private ShareGridAdapter mAdapter;
    private TaskManager mTaskManager;

    private LoadingDialog mLoadingDialog;

    private Activity mActivity;

    private int mShareType;
    private static final int SHARE_TYPE_STATUS = 1;//分享动态
    private static final int SHARE_TYPE_GOODS = 2; //分享商品
    private static final int SHARE_TYPE_INTEGRAL_GOODS = 3; //分享兑换商品
    private static final int SHARE_TYPE_TRIBE = 4; //分享主页
    private static final int SHARE_TYPE_AD = 5; //分享广告
    private static final int SHARE_TYPE_SHOPPINGCIRCLE = 6; //分享购物圈
    private static final int SHARE_TYPE_APP = 7; //分享app
    private static final int SHARE_TYPE_TUDI = 8; //分享我要收徒
    private static final int SHARE_TYPE_LONG_STATUS = 9; //分享长动态

    private ShareItem mShareItem;

    View tvShareShoppingCircleTip;
    View viewShareLinWeb;

    private View llAdSwitch;//分享插入广告卡片开关控件
    private ImageView ivAdSwitch;//分享插入广告卡片开关
    private int isAdSwitch = 0; //默认不插入广告 ；1为插入广告 ; 2为主动点击关闭(针对广告主第一次是默认开启的，关闭后要手动开启；2表示手动关闭过)
    private PublishAdStatusItem mPublishAdStatusItem;//广告投放状态

    public ShareDialog(Context context) {
        super(context, R.style.Common_Dialog);
        mActivity = (Activity) context;
        setContentView(R.layout.dialog_share);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        if (context instanceof BaseActivity) {
            mTaskManager = ((BaseActivity) context).getTaskManager();
        }
        if (mTaskManager == null) {
            mTaskManager = new TaskManager();
        }
        initView();
    }

    private void initView() {
        llAdSwitch = findViewById(R.id.ll_ad_switch);
        ivAdSwitch = (ImageView) findViewById(R.id.iv_ad_switch);
        viewShareLinWeb = findViewById(R.id.linWeb);
        tvShareShoppingCircleTip = findViewById(R.id.tvShareShoppingCircleTip);
        findViewById(R.id.linRepot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(mActivity, AdReportAct.class);
                intent.putExtra("ad", ad);
                mActivity.startActivity(intent);
            }
        });
        mGridView = (GridView) findViewById(R.id.gv);
        mAdapter = new ShareGridAdapter();
        mGridView.setAdapter(mAdapter);

        mLoadingDialog = new LoadingDialog(getContext());

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if (mShareItem == null) {
                    return;
                }
                SHARE_MEDIA platform = null;
                String title = mShareItem.title;
                String content = mShareItem.content;


                switch(position) {
                    case 0:
                        platform = SHARE_MEDIA.WEIXIN;
                        if (!isInstalled(platform)) {
                            ToastUtil.showToast("安装微信才能分享");
                            return;
                        }
                        break;
                    case 1: //朋友圈
                        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                        if (mShareType != SHARE_TYPE_STATUS
                                && mShareType != SHARE_TYPE_GOODS
                                && mShareType != SHARE_TYPE_TUDI
                                && mShareType != SHARE_TYPE_TRIBE) {
                            title = content;
                        }
                        if (mShareType == SHARE_TYPE_TRIBE) {
                            title = title + "," + content;
                        }
                        if (!isInstalled(platform)) {
                            ToastUtil.showToast("安装微信才能分享");
                            return;
                        }
                        break;
                    case 2:
                        platform = SHARE_MEDIA.QQ;
                        if (!isInstalled(platform)) {
                            ToastUtil.showToast("安装QQ才能分享");
                            return;
                        }
                        break;
                    case 3:
                        platform = SHARE_MEDIA.QZONE;
                        if (!isInstalled(SHARE_MEDIA.QQ)) {
                            ToastUtil.showToast("安装QQ才能分享");
                            return;
                        }
                        break;
                    case 4: { //微博的分享样式是内容拼接标题，所以会把标题设置为""
                        platform = SHARE_MEDIA.SINA;
                        if (!isInstalled(platform)) {
                            ToastUtil.showToast("安装新浪微博才能分享");
                            return;
                        }
                        switch(mShareType) {
                            case SHARE_TYPE_STATUS: {
//                                title = title.replace("动态", "师徒部落动态");
//                                content = Res.getString(R.string.me_share) + title + ": " + content + Res.getString(R.string.me_from_at_stbl);
                                content = "#师徒部落动态#+" + content + Res.getString(R.string.me_from_at_stbl);
                            }
                            break;
                            case SHARE_TYPE_GOODS:
                                content = "#师徒商城#" + title + "（分享自@师徒部落）";
//                                content = Res.getString(R.string.me_at_stbl_find_a_good_thing) + title + Res.getString(R.string.me_view_click_here);
                                break;
                            case SHARE_TYPE_INTEGRAL_GOODS:
                                content = "#师徒部落免费兑换# 分享一个免费商品给你，动动手就能兑换（分享自@师徒部落）";
//                                content = Res.getString(R.string.me_at_stbl_find_a_nice_gift) + title + Res.getString(R.string.me_click_here_exchange);
                                break;
                            case SHARE_TYPE_TRIBE: {
//                                title = title.replace("部落", "师徒部落");
//                                content = title + Res.getString(R.string.me_strong_people_business_social) + Res.getString(R.string.me_from_at_stbl);
                                content = "#师徒部落主页# " + content + "（分享自@师徒部落）";
                            }
                            break;
                            case SHARE_TYPE_AD:
                                content = title + content;
                                break;
                            case SHARE_TYPE_SHOPPINGCIRCLE:
                                content = "购物圈内容";
                                break;
                            case SHARE_TYPE_APP:
                                content = "#师徒部落#" + title + "（分享自@师徒部落）";
                                break;
                            case SHARE_TYPE_TUDI:
                                content = "#师徒部落，人脉商务社交神器#" + title + "（分享自@师徒部落）";
                                break;
                        }
                        if (mShareType > 0) {
                            title = "";
                        } else {
                            title = " " + title;
                        }
                    }
                    break;
                    case 5:
                        platform = SHARE_MEDIA.FACEBOOK;
                        if (!isInstalled(platform)) {
                            ToastUtil.showToast("安装Facebook才能分享");
                            return;
                        }
                        break;
                    case 6://复制链接
                        StringUtil.copyToClipboard(mShareItem.link);
                        break;
                    case 7://更多分享（系统分享）先下载图片
                        if (mShareType == SHARE_TYPE_STATUS) {
                            downloadImage(mShareItem.nickname + "发布了一条有趣的动态，快来看看：" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_LONG_STATUS) {
                            downloadImage(mShareItem.nickname + "分享了一篇美文，快来看看：" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_GOODS) {
                            downloadImage("我在师徒部落发现了一个很赞的商品，赶快来看看吧：" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_INTEGRAL_GOODS) {
                            downloadImage("分享一个免费商品给你，动动手就能兑换：" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_APP) {
                            downloadImage("来这里，你比别人更强大，跟我一起创造强人脉商业帝国：" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_TUDI) {
                            downloadImage(title + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else if (mShareType == SHARE_TYPE_TRIBE) {
                            downloadImage("HI，这是" + mShareItem.nickname + "的个人主页，赶紧和他建立强人脉商业帝国吧！" + mShareItem.link + "[师徒部落]", mShareItem.imgUrl);
                        } else {
                            downloadImage(content + " " + mShareItem.link + Res.getString(R.string.me_from_at_stbl), mShareItem.imgUrl);
                        }
                        break;
                }
                if (isAdSwitch == 1) { //拼接插入广告的参数
                    mShareItem.link = mShareItem.link + "&adswitch=1";
                }

                if (platform != null) {
                    new ShareAction(mActivity)
                            .setPlatform(platform)
                            .setCallback(mShareListener)
                            .withTitle(title)
                            .withText(content)
                            .withMedia(mShareItem.umImage)
                            .withTargetUrl(mShareItem.link)
                            .share();
                    //定义为点击了分享。在购物圈可以给他抢红包了。

                    LogUtil.logE("share click", title + "   " + content + "   " + mShareItem.link);
                    EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeClickedShare));
                }
            }
        });
    }

    public boolean isInstalled(SHARE_MEDIA media) {
        return UMShareAPI.get(mActivity).isInstall(mActivity, media);
    }

    /**
     * 获取分享链接后分享
     */
    public void getLinkAndShare(String mi, final String bi, final String ex, final ShareItem shareItem) {
        mShareItem = shareItem;
        mLoadingDialog.show();
        mTaskManager.start(CommonTask.getShareLink(mi, bi, null)
                .setCallback(new HttpTaskCallback<String>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(mShareItem.title)) {
                            mShareItem.title = Res.getString(R.string.me_stbl_share);
                        }
                        if (TextUtils.isEmpty(mShareItem.content)) {
                            mShareItem.content = Res.getString(R.string.me_stbl_share);
                        }
                        if (mShareItem.umImage == null) {
                            if (TextUtils.isEmpty(mShareItem.imgUrl)) {
                                mShareItem.umImage = new UMImage(mActivity, R.drawable.icon_share_logo);
                            } else {
                                mShareItem.umImage = new UMImage(mActivity, shareItem.imgUrl);
                            }
                        }
                        if (shareItem.defaultIcon != 0) { //默认图
                            mShareItem.umImage = new UMImage(mActivity, shareItem.defaultIcon);
                        }
                        mShareItem.link = result;
//去掉次分享
//                        if (mShareType == SHARE_TYPE_SHOPPINGCIRCLE) {
//                            tvShareShoppingCircleTip.setVisibility(View.VISIBLE);
//                        }

                        show();
                    }

                    @Override
                    public void onFinish() {
                        mLoadingDialog.dismiss();
                    }
                }));
    }

    /**
     * 分享插入广告开关监听
     */
    public void adSwitchListener() {
        llAdSwitch.setVisibility(View.VISIBLE);
        //不是广告主
        final boolean isAdvertiser = (int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0;
        isAdSwitch = (int) SharedPrefUtils.getFromPublicFile(KEY.SHARE_AD_SWITCH + SharedToken.getUserId(), 0);
        if (isAdSwitch == 0 && !isAdvertiser) isAdSwitch = 1;//广告主第一次默认开启
        adSwitchIcon();
        ivAdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不是广告主直接弹框提示
                if (isAdvertiser) {
                    isAdSwitch = 2;
                    SharedPrefUtils.putToPublicFile(KEY.SHARE_AD_SWITCH + SharedToken.getUserId(), isAdSwitch);
                    adSwitchIcon();
                    notAdvertiser();
                    return;
                }
                if (isAdSwitch == 0 || isAdSwitch == 2) { //选择插入广告
                    if (mPublishAdStatusItem == null) {
                        getAdStatus();
                    } else {
                        adStatusManager();
                    }
                } else {
//                    isAdSwitch = 0;
                    isAdSwitch = 2;
                    adSwitchIcon();
                    SharedPrefUtils.putToPublicFile(KEY.SHARE_AD_SWITCH + SharedToken.getUserId(), isAdSwitch);
                }
            }
        });
    }

    /**
     * 切换插入广告开关图标
     */
    private void adSwitchIcon() {
        if (isAdSwitch == 0 || isAdSwitch == 2) {
            ivAdSwitch.setImageResource(R.drawable.icon_button_off);
        } else {
            ivAdSwitch.setImageResource(R.drawable.icon_button_on);
        }
    }

    /**
     * 获取广告投放状态
     */
    private void getAdStatus() {
        Params params = new Params();
        new HttpEntity(mActivity).commonPostData(Method.adsysApplyStatusGet, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                WaitingDialog.dismiss();
                LogUtil.logE("LogUtil", methodName + "---" + result);
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item == null) return;
                if (item.getIssuccess() != BaseItem.successTag) {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                        ToastUtil.showToast(mActivity, item.getErr().getMsg());
                    }
                }

                String json = JSONHelper.getStringFromObject(item.getResult());
                switch(methodName) {
                    case Method.adsysApplyStatusGet:
                        mPublishAdStatusItem = JSONHelper.getObject(json, PublishAdStatusItem.class);
                        if (mPublishAdStatusItem != null) {
                            adStatusManager();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 根据广告投放状态处理
     */
    private void adStatusManager() {
        //1 ;投放申请单状态，-1 无，0-待审核，1-同意，2-不同意
        isAdSwitch = 2;
        switch(mPublishAdStatusItem.getStatus()) {
            case -1:
                noAd();
                break;
            case 0:
                adWaitCheck();
                break;
            case 1:
                isAdSwitch = 1;
                break;
            case 2:
                adWaitCheck();
                break;
        }
        adSwitchIcon();
        SharedPrefUtils.putToPublicFile(KEY.SHARE_AD_SWITCH + SharedToken.getUserId(), isAdSwitch);
    }

    /**
     * 不是广告主弹框
     */
    public void notAdvertiser() {
        TipsDialog2.popup(mActivity, "您还不是广告主\n开通【品牌+】，就可以使用此服务", "不了，谢谢", "去开通", new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
                mActivity.startActivity(new Intent(mActivity, IntroduceAdAct.class));
                dismiss();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    /**
     * 广告在审核弹框
     */
    public void adWaitCheck() {
        TipsDialog2.popup(mActivity, "您的广告还在审核中", "知道了", new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    /**
     * 未上传广告弹框
     */
    public void noAd() {
        TipsDialog2.popup(mActivity, "您还未上传广告", "不了，谢谢", "去上传", new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
                mActivity.startActivity(new Intent(mActivity, AdManagerActivity.class));
                dismiss();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    /**
     * 分享动态
     */
    public void shareStatus(long statusesid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_STATUS;
        adSwitchListener();
        getLinkAndShare(String.valueOf(sharedMiStatuses), String.valueOf(statusesid), null, shareItem);
    }

    /**
     * 分享动态
     */
    public void shareLongStatus(long statusesid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_LONG_STATUS;
        adSwitchListener();
        getLinkAndShare(String.valueOf(sharedMiStatuses), String.valueOf(statusesid), null, shareItem);
    }

    /**
     * 分享商品
     */
    public void shareGoods(long goodsid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_GOODS;
        getLinkAndShare(String.valueOf(sharedMiGoods), String.valueOf(goodsid), null, shareItem);
    }

    /**
     * 分享代付订单
     */
    public void shareAdOtherPay(String adOrderno, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_AD;
        getLinkAndShare(String.valueOf(sharedAd), adOrderno, null, shareItem);
    }

    /**
     * 分享积分商品
     */
    public void shareIntegralGoods(long goodsid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_INTEGRAL_GOODS;
        getLinkAndShare(String.valueOf(sharedMiIntegralGoods), String.valueOf(goodsid), null, shareItem);
    }

    /**
     * 分享部落
     */
    public void shareTribe(long userid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_TRIBE;
        getLinkAndShare(String.valueOf(sharedMiTribe), String.valueOf(userid), null, shareItem);
    }

    /**
     * 我要收徒邀请
     */
    public void shareInvite(long userid, final ShareItem shareItem) {
        mShareType = SHARE_TYPE_TUDI;
        getLinkAndShare(String.valueOf(sharedMiTribe), String.valueOf(userid), null, shareItem);
    }

    String redpacketid;

    /**
     * 分享购物圈
     */
    public void shareShoppingCircle(String squareId, final String redpacketid, final ShareItem shareItem) {
        this.redpacketid = redpacketid;
        mShareType = SHARE_TYPE_SHOPPINGCIRCLE;
        adSwitchListener();
        getLinkAndShare(String.valueOf(sharedShoppingCircle), squareId, null, shareItem);
    }

    /**
     * 分享App
     */
    public void shareApp(final ShareItem shareItem) {
        mShareType = SHARE_TYPE_APP;
        getLinkAndShare(String.valueOf(sharedMiRegister), SharedToken.getUserId(MyApplication.getContext()), null, shareItem);
    }

    /**
     * 分享网页
     */
    public void shareWebpage(ShareItem shareItem) {
        mShareItem = shareItem;
        if (TextUtils.isEmpty(mShareItem.title)) {
            mShareItem.title = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(mShareItem.content)) {
            mShareItem.content = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(mShareItem.imgUrl)) {
            mShareItem.umImage = new UMImage(mActivity, R.drawable.icon_share_logo);
        } else {
            mShareItem.umImage = new UMImage(mActivity, shareItem.imgUrl);
        }
        show();
    }


    Ad ad;

    /**
     * 分享广告网页
     */
    public void shareAdWebpage(ShareItem shareItem, Ad ad) {
        this.ad = ad;
        mShareItem = shareItem;
        if (TextUtils.isEmpty(mShareItem.title)) {
            mShareItem.title = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(mShareItem.content)) {
            mShareItem.content = Res.getString(R.string.me_stbl_share);
        }

        if (TextUtils.isEmpty(mShareItem.imgUrl)) {
            mShareItem.umImage = new UMImage(mActivity, R.drawable.icon_share_logo);
        } else {
            mShareItem.umImage = new UMImage(mActivity, shareItem.imgUrl);
        }

        findViewById(R.id.line1).setVisibility(View.VISIBLE);
        viewShareLinWeb.setVisibility(View.VISIBLE);

        LogUtil.logE("share shareAdWebpage", mShareItem.title + "   " + mShareItem.content + "   " + mShareItem.link);
        show();
    }


    private UMShareListener mShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (mShareType == SHARE_TYPE_SHOPPINGCIRCLE) {
                Params params = new Params();
                params.put("redpacketid", redpacketid);
                new HttpEntity(mActivity).commonRedpacketPostData(Method.redpacketShareCallback, params, null);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            ToastUtil.showToast(R.string.me_share_fail);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            //分享取消
        }
    };

    private void downloadImage(final String content, final String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            mLoadingDialog.show();
            mTaskManager.start(FileTask.download(imgUrl, System.currentTimeMillis() + ".jpg")
                    .setCallback(new TaskCallback<File>() {
                        @Override
                        public void onError(TaskError e) {
                            ToastUtil.showToast(e.msg);
                        }

                        @Override
                        public void onSuccess(File result) {
                            moreShare(content, Uri.fromFile(result));
                        }

                        @Override
                        public void onFinish() {
                            mLoadingDialog.dismiss();
                        }
                    }));
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.icon_share_logo);
            File file = FileUtils.saveTempBitmap(bitmap);
            if (file != null) {
                moreShare(content, Uri.fromFile(file));
            }
        }
    }

    private void moreShare(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra("Kdescription", content);//给朋友圈的默认文本
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定义选择框的标题
        mActivity.startActivity(Intent.createChooser(shareIntent, Res.getString(R.string.me_share_to)));
    }

}
