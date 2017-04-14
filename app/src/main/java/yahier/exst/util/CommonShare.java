package yahier.exst.util;

import android.app.Activity;
import android.text.TextUtils;

import com.stbl.stbl.R;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.task.CommonTask;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import io.rong.eventbus.EventBus;

public class CommonShare {
//    final static String wxAppId = "wx2933b730046aeea9";
//    final static String wxSecret = "6d2f70dca202d6b4ba176affba3d1194";
//
//    final static String qqAppId = "1105104399";//1105026416
//    final static String qqSecret = "KEYU9WHaQt2RJfyOQzx";//9NDdVIxzssCMThVw

    public final static int sharedMiRegister = 10000;
    public final static int sharedMiApp = 10010;
    public final static int sharedMiGoods = 10020;
    public final static int sharedMiIntegralGoods = 10021;
    public final static int sharedMiStatuses = 10030;
    public final static int sharedMiTribe = 10040;
    public final static int sharedShoutu = 10060;//收徒分享
    static final String TAG = CommonShare.class.getSimpleName();
//
//    public enum ShareWay {
//        WX, WX_CIRCLE, QQ, QQ_ZONE, SINA, MORE,
//    }

//    public void share(Activity act, ShareWay shareWay, String url) {
//        UMImage img = new UMImage(act, mShareItem.getImgUrl());
//        String title = mShareItem.getTitle();
//        String content = mShareItem.getContent();
//
//        if (TextUtils.isEmpty(title)) {
//            title = Res.getString(R.string.me_stbl_share);
//        }
//        if (TextUtils.isEmpty(content)) {
//            content = Res.getString(R.string.me_stbl_share);
//        }
//        if (TextUtils.isEmpty(mShareItem.getImgUrl())) {
//            img = new UMImage(act, R.drawable.icon_share_logo);
//        }
//
//        SHARE_MEDIA platform = null;
//        switch (shareWay) {
//            case WX:
//                platform = SHARE_MEDIA.WEIXIN;
//                break;
//            case WX_CIRCLE:
//                platform = SHARE_MEDIA.WEIXIN_CIRCLE;
//                title = content;//朋友圈只用到title，我们要显示内容
//                break;
//            case QQ:
//                platform = SHARE_MEDIA.QQ;
//                break;
//            case QQ_ZONE:
//                platform = SHARE_MEDIA.QZONE;
//                break;
//            case SINA:
//                platform = SHARE_MEDIA.SINA;
//
//                break;
//            case MORE:
//                break;
//        }
//        if (platform != null) {
//            new ShareAction(act)
//                    .setPlatform(platform)
//                    .setCallback(mShareListener)
//                    .withTitle(title)
//                    .withText(content)
//                    .withMedia(img)
//                    .withTargetUrl(url)
//                    .share();
//        }
//    }

//    private UMShareListener mShareListener = new UMShareListener() {
//        @Override
//        public void onResult(SHARE_MEDIA share_media) {
//            ToastUtil.showToast(R.string.me_share_success);
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//            ToastUtil.showToast(R.string.me_share_fail);
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA share_media) {
//            //分享取消
//        }
//    };

    public static boolean isWechatInstalled(Activity act) {
        return UMShareAPI.get(act).isInstall(act, SHARE_MEDIA.WEIXIN);
    }

    public static boolean isQQInstalled(Activity act) {
        return UMShareAPI.get(act).isInstall(act, SHARE_MEDIA.QQ);
    }

//    Dialog pop;
//
//    ShareItem mShareItem;

    //新的分享
//    public void showShareWindow(final Activity mAct, String mi, String bi, String ex, ShareItem shareItem) {
//        mShareItem = shareItem;
//        final LoadingDialog dialog = new LoadingDialog(mAct);
//        dialog.show();
//        JSONObject json = new JSONObject();
//        if (mi != null)
//            json.put("mi", mi);
//        if (mi != null)
//            json.put("bi", bi);
//        if (mi != null)
//            json.put("ex", ex);
//        new HttpUtil(mAct, null).postJson(Method.common_share_url, json.toJSONString(), new OnFinalHttpCallback() {
//
//            @Override
//            public void onHttpResponse(String methodName, String json, Object handle) {
//                dialog.dismiss();
//                if (methodName.equals(Method.common_share_url)) {
//                    String shareUrl = JSONHelper.getObject(json, String.class);
//
//                    popupMenu(mAct, shareUrl);
//                }
//            }
//
//            @Override
//            public void onHttpError(String methodName, String msg, Object handle) {
//                dialog.dismiss();
//            }
//        });
//    }

//    public void showShareWindow(final Activity act, ShareItem shareItem, String shareUrl) {
//        mShareItem = shareItem;
//        popupMenu(act, shareUrl);
//    }

    //新的分享,不弹框
//    public void showShareWindowToType(final Activity act, String mi, String bi, String ex, ShareItem shareItem, final ShareWay way) {
//        mShareItem = shareItem;
//        JSONObject json = new JSONObject();
//        if (mi != null)
//            json.put("mi", mi);
//        if (mi != null)
//            json.put("bi", bi);
//        if (mi != null)
//            json.put("ex", ex);
//        new HttpUtil(act, null).postJson(Method.common_share_url, json.toJSONString(), new OnFinalHttpCallback() {
//
//            @Override
//            public void onHttpResponse(String methodName, String json, Object handle) {
//                if (methodName.equals(Method.common_share_url)) {
//                    String shareUrl = JSONHelper.getObject(json, String.class);
//
//                    share(act, way, shareUrl);
//                }
//            }
//
//            @Override
//            public void onHttpError(String methodName, String msg, Object handle) {
//            }
//        });
//    }

//    private void popupMenu(final Activity act, final String shareUrl) {
//        LogUtil.logE("shareUrl:" + shareUrl);
//        if (act == null || act.isFinishing()) return;
//        OnClickListener listener = new OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                pop.dismiss();
//                switch (view.getId()) {
//                    case R.id.linShareQQfeiend:
//                        share(act, ShareWay.QQ, shareUrl);
//                        break;
//                    case R.id.linShareQzone:
//                        share(act, ShareWay.QQ_ZONE, shareUrl);
//                        break;
//                    case R.id.linShareWechatFriend:
//                        share(act, ShareWay.WX, shareUrl);
//                        break;
//                    case R.id.linShareWechatMoments:
//                        share(act, ShareWay.WX_CIRCLE, shareUrl);
//                        break;
//                    case R.id.linShareWeibo:
//                        share(act, ShareWay.SINA, shareUrl);
//                        break;
//                    case R.id.linShareMore:
//                        shareSingleImg(act, mShareItem.getImgUrl());
//                        break;
//                    case R.id.viewShare:
//                        break;
//                }
//            }
//        };
//
//        pop = new Dialog(act, R.style.Common_Dialog);
//        View view = act.getLayoutInflater().inflate(R.layout.common_shatre_window, null);
//        view.findViewById(R.id.viewShare).setOnClickListener(listener);
//        view.findViewById(R.id.linShareQQfeiend).setOnClickListener(listener);
//        view.findViewById(R.id.linShareQzone).setOnClickListener(listener);
//        view.findViewById(R.id.linShareWechatFriend).setOnClickListener(listener);
//        view.findViewById(R.id.linShareWechatMoments).setOnClickListener(listener);
//        view.findViewById(R.id.linShareWeibo).setOnClickListener(listener);
//        view.findViewById(R.id.linShareMore).setOnClickListener(listener);
//        pop.setContentView(view);
//        Window window = pop.getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setWindowAnimations(R.style.dialog_animation);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(params);
//        pop.show();
//    }

//    File file;
//
//    public void shareSingleImg(final Activity act, final String imgUrl) {
//        HandleAsync hand = new HandleAsync();
//        hand.excute(new HandleAsync.Listener() {
//            @Override
//            public String getResult() throws ClientProtocolException, IOException {
//                file = FileUtils.getFileAfterSvae(imgUrl);
//                return null;
//            }
//
//            @Override
//            public void parse(String result) {
//                if (file != null) {
//                    if (file == null) {
//                        ToastUtil.showToast("请待会儿再试");
//                        return;
//                    }
//                    Uri imageUri = Uri.fromFile(file);
//                    Intent shareIntent = new Intent();
//                    //shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, mShareItem.getContent());
//                    // shareIntent.putExtra(Intent.EXTRA_TITLE, "我是标题");
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                    shareIntent.setType("image/*");
//                    act.startActivity(Intent.createChooser(shareIntent, "分享到"));
//                }
//            }
//        });
//    }

    public static void shareShortStatusAfterPublish(Activity activity, ShareItem shareItem, String url, boolean isShareCircle, boolean isShareQzone,boolean isShareTask) {
        if (isShareCircle) {
            shareStatusToCircle(activity, shareItem, url, isShareQzone,isShareTask);
        } else if (isShareQzone) {
            shareStatusToQzone(activity, shareItem, url,isShareTask);
        }
    }

    public static void shareStatusToCircle(final Activity act, final ShareItem shareItem, final String url, final boolean isShareQzone, final boolean isShareTask) {
        UMImage img = new UMImage(act, shareItem.getImgUrl());
        String title = shareItem.getTitle();
        String content = shareItem.getContent();

        if (TextUtils.isEmpty(title)) {
            title = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(content)) {
            content = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(shareItem.getImgUrl())) {
            img = new UMImage(act, R.drawable.icon_share_logo);
        }
        new ShareAction(act)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .withTitle(content)
                .withText(content)
                .withMedia(img)
                .withTargetUrl(url)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastUtil.showToast(R.string.me_share_success);
                        if (isShareQzone) {
                            shareStatusToQzone(act, shareItem, url,isShareTask);
                        }
                        CommonTask.completeMissionCallback(CommonTask.MISSION_TYPE_PUBLISH_STATUS);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtil.showToast(R.string.me_share_fail);
                        if (isShareQzone) {
                            shareStatusToQzone(act, shareItem, url,isShareTask);
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        if (isShareQzone) {
                            shareStatusToQzone(act, shareItem, url,isShareTask);
                        }
                    }
                }).share();
    }

    public static void shareStatusToQzone(Activity act, ShareItem shareItem, String url, final boolean isShareTask) {
        UMImage img = new UMImage(act, shareItem.getImgUrl());
        String title = shareItem.getTitle();
        String content = shareItem.getContent();

        if (TextUtils.isEmpty(title)) {
            title = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(content)) {
            content = Res.getString(R.string.me_stbl_share);
        }
        if (TextUtils.isEmpty(shareItem.getImgUrl())) {
            img = new UMImage(act, R.drawable.icon_share_logo);
        }
        new ShareAction(act)
                .withTitle(title)
                .withText(content)
                .withMedia(img)
                .withTargetUrl(url)
                .setPlatform(SHARE_MEDIA.QZONE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        ToastUtil.showToast(R.string.me_share_success);
                        CommonTask.completeMissionCallback(CommonTask.MISSION_TYPE_PUBLISH_STATUS);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtil.showToast(R.string.me_share_fail);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                    }
                }).share();
    }

}
