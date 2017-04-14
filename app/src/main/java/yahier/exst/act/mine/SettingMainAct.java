package yahier.exst.act.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stbl.base.library.task.Task;
import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.act.im.SharedGroups;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.util.ConfigControl;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.DongtaiSearchRecordDB;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.GoodsiSearchRecordDB;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.jpush.JPushManager;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONObject;

import java.io.File;

import io.rong.imkit.RongIM;

public class SettingMainAct extends ThemeActivity implements OnClickListener {
    Context mContext;
    private LinearLayout settingItem1;
    private LinearLayout settingItem4;
    private Button settingItem6;
    private LinearLayout mLangSettingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        mContext = this;
        setLabel(getString(R.string.me_setting));
        initViews();
        try {
            if (UserRole.isTemp(Integer.parseInt(SharedToken.getRoleFlag(this)))) {
                settingItem1.setVisibility(View.GONE);
                settingItem6.setVisibility(View.GONE);
                findViewById(R.id.v_line2).setVisibility(View.GONE);
                settingItem4.setVisibility(View.GONE);
                findViewById(R.id.v_line4).setVisibility(View.GONE);
                findViewById(R.id.v_line5).setVisibility(View.GONE);
                findViewById(R.id.v_line6).setVisibility(View.GONE);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        settingItem1 = (LinearLayout) findViewById(R.id.setting_item1);
        settingItem1.setOnClickListener(this);
        findViewById(R.id.setting_item2).setOnClickListener(this);
        findViewById(R.id.setting_item3).setOnClickListener(this);
        settingItem4 = (LinearLayout) findViewById(R.id.setting_item4);
        settingItem4.setOnClickListener(this);
        findViewById(R.id.setting_item5).setOnClickListener(this);
        settingItem6 = (Button) findViewById(R.id.setting_item6);
        settingItem6.setOnClickListener(this);
        mLangSettingLayout = (LinearLayout) findViewById(R.id.setting_item7);
        mLangSettingLayout.setOnClickListener(this);
        if (ConfigControl.switchLang) {
            findViewById(R.id.divider_top_lang).setVisibility(View.VISIBLE);
            mLangSettingLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.divider_bottom_lang).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        final Context mContext = view.getContext();
        switch (view.getId()) {
            case R.id.setting_item1:
                enterAct(SettingAccountMainAct.class);
                break;
            case R.id.setting_item2:
                TipsDialog.popup(this, getString(R.string.me_clear_cache_tip), getString(R.string.me_cancle), getString(R.string.me_confirm), new OnTipsListener() {

                    @Override
                    public void onConfirm() {
                        clearCache();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                break;
            case R.id.setting_item3:
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.problem_instrod, "");
                Intent intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url", url);
                intent.putExtra("title", getString(R.string.me_faq));
                startActivity(intent);
                break;
            case R.id.setting_item4:
                ShareItem shareItem = new ShareItem();
                shareItem.setContent(getString(R.string.me_share_app_content));
                shareItem.setTitle(getString(R.string.me_share_app_title));
                //new CommonShare().showShareWindow(this, String.valueOf(CommonShare.sharedMiRegister), SharedToken.getUserId(this), null, shareItem);
                new ShareDialog(this).shareApp(shareItem);
                break;
            case R.id.setting_item5:
                enterAct(AboutUsAct.class);
                break;
            case R.id.setting_item6:
                TipsDialog.popup(this, getString(R.string.me_is_confirm_logout_current_account), getString(R.string.me_cancle), getString(R.string.me_confirm), new OnTipsListener() {

                    @Override
                    public void onConfirm() {
                        if (SharedToken.clearToken()) {
                            // 关掉我的主页
                            sendBroadcast(new Intent("exit"));
                            mContext.startActivity(new Intent(mContext, GuideActivity.class));
                            QavsdkManger.getInstance().editQavsdk();
                            RongIM.getInstance().getRongIMClient().logout();//不再接收融云Push消息
                            JPushManager.getInstance().clearAlias(SettingMainAct.this);
                            //清除当前用户信息
                            SharedUser.clearData();
                            SharedToken.clearToken();
                            SharedGroups.clearGroups();
                            //帐号被抢登，清除缓存
                            new DataCacheDB(mContext).deleteAllData();
                            finish();
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                break;
            case R.id.setting_item7:
                startActivity(new Intent(this, MultiLangSetingActivity.class));
                break;
        }
    }

    private void clearCache() {
        ImageUtils.clearMemoryCache();
        mTaskManager.start(clearCacheTask().setCallback(new TaskCallback<String>() {
            @Override
            public void onError(TaskError e) {
                ToastUtil.showToast(mContext, getString(R.string.me_data_error));
            }

            @Override
            public void onSuccess(String result) {
                ToastUtil.showToast(getString(R.string.me_clear_done));
            }
        }));
    }

    private static Task<String> clearCacheTask() {
        return new Task<String>() {
            @Override
            protected void call() {
                Glide.get(MyApplication.getContext()).clearDiskCache();

                File picassoCacheFile = MyApplication.getContext().getCacheDir();
                FileUtils.deleteDir(picassoCacheFile);
                //FileUtils.deleteDir(new File(Config.localFilePath));
                new RongDB(MyApplication.getContext()).deleteAllData();//新加
                new GoodsiSearchRecordDB(MyApplication.getContext()).deleteAllData();//商品搜索
                new DongtaiSearchRecordDB(MyApplication.getContext()).deleteAllData();
                new DongtaiRemarkDB(MyApplication.getContext()).deleteAllData();//动态搜索
                new DataCacheDB(MyApplication.getContext()).deleteAllData();//json缓存
                onSuccess("");
            }
        };
    }

    // 测试微信支付
    void testWXPay() {
        final IWXAPI api = WXAPIFactory.createWXAPI(this, "wx2933b730046aeea9");
        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
        Toast.makeText(this, "获取订单中...", Toast.LENGTH_SHORT).show();
        try {
            byte[] buf = Util.httpGet(url);
            if (buf != null && buf.length > 0) {
                String content = new String(buf);
                Log.e("get server pay params:", content);
                JSONObject json = new JSONObject(content);
                if (null != json && !json.has("retcode")) {
                    PayReq req = new PayReq();
                    // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
                    req.extData = "app data"; // optional
                    Toast.makeText(this, "正常调起支付", Toast.LENGTH_SHORT).show();
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                    Toast.makeText(this, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PAY_GET", "服务器请求错误");
                Toast.makeText(this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("PAY_GET", "异常：" + e.getMessage());
            Toast.makeText(this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
