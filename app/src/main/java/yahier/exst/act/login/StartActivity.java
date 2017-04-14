package yahier.exst.act.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.ImageView;

import com.stbl.base.library.task.ThreadPool;
import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.DnsResult;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HandleAsync;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpGetConnection;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedVersion;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.widget.avsdk.QavsdkManger;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

public class StartActivity extends BaseActivity implements FinalHttpCallback {
    final String tag = getClass().getSimpleName();
    boolean isRefreshedToken = false;
    long maxMills = 5000;//5000
    long minMills = 1000;//1000
    /**
     * 是否打开动态提醒
     */
    private boolean isOpenDongtaiNewMsg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView imageView = (ImageView) findViewById(R.id.imgAd);
        imageView.setImageBitmap(ImageUtils.decodeBitmapSafety(getResources(), R.drawable.bg_splash, null));
        initFileDir();
        QavsdkManger.getInstance().setOpenQavsdkService(false);
        //LogUtil.logE("JPushInterface clearAlias");
        //gwtIps();
        //refreshToken();
        isOpenDongtaiNewMsg = getIntent().getBooleanExtra(KEY.StatusesGetNotification, false);
        LogUtil.logE("LogUtil", "StartActivity isOpenDongtaiNewMsg -- " + isOpenDongtaiNewMsg);
        dealDirect();
        CommonTask.getCommonDicBackground();
        SharedVersion.checkFirstNewVersion();
    }

    @Override
    protected void setStatusBar() {
    }

    private void initFileDir(){
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.initDirs();
            }
        });
    }

    boolean timerActive = true;
    //最多5秒钟，一定要离开此页面
    CountDownTimer timer = new CountDownTimer(maxMills, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (!timerActive) return;
            LogUtil.logE(millisUntilFinished + "");
            if (millisUntilFinished < (maxMills - minMills) && isRefreshedToken) {
                //LogUtil.logE("onTick  gotoTabHomeAct");
                gotoTabHomeAct();
                timerActive = false;
            }
        }

        @Override
        public void onFinish() {
            if (timerActive) {
                //LogUtil.logE("onFinish  gotoTabHomeAct");
                gotoTabHomeAct();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerActive = false;
    }

    /**
     * 处理跳转.判断是否刷新token
     */
    void dealDirect() {
        if (SharedToken.getToken().equals("")) {
            isJumpToIntro();
        } else {
            if (SharedToken.isTokenValid(this)) {
                //是否24小时内刷新过
                if (SharedToken.isFreshedIn24Hours(this)) {
                    // pass.继续等待 直到时间来到。自己跳
                    isRefreshedToken = true;
                    timer.start();
                } else {
                    refreshToken();
                }
            } else {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
            }
        }
    }

    //清除掉token也会到这里来
    private void isJumpToIntro() {
        if (AppUtils.isFinishIntroduce()) {
            startActivity(new Intent(this, GuideActivity.class));
        } else {
            startActivity(new Intent(this, IntroduceActivity.class));
        }
        finish();
    }

    //判断用户角色。
    void judgeUserRole() {
        int roleFlag;
        try {
            roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
            if (UserRole.isNotMaster(roleFlag)) {
                startActivity(new Intent(this, GuideActivity.class));
                finish();
            } else {
                //新改
                //isRefreshedToken = true;
                Intent intent = new Intent(StartActivity.this, TabHome.class);
                if (isOpenDongtaiNewMsg) {
                    intent.putExtra("open_dongtai_new_msg", true);
                }
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {

        }


    }

    //5月11号加入修改，没有拜师进入app会进入到登录注册页。
    void gotoTabHomeAct() {
        int roleFlag;
        try {
            roleFlag = Integer.valueOf(SharedToken.getRoleFlag());
            if (UserRole.isNotMaster(roleFlag)) {
                Intent intent = new Intent(StartActivity.this, GuideActivity.class);//从跳转拜师到跳转引导页。5月11号修改
                startActivity(intent);
            } else {
                Intent intent = new Intent(StartActivity.this, TabHome.class);
                if (isOpenDongtaiNewMsg) intent.putExtra("open_dongtai_new_msg", true);
                startActivity(intent);
            }
        } catch (Exception e) {

        }

        finish();
    }

    //获取数据之后，会判断角色
    private void refreshToken() {
        Params params = new Params();
        params.put("refreshtoken", SharedToken.getRefreshToken());
        new HttpEntity(this).commonPostData(Method.refreshToken, params, this);
    }

    //获取dns解析后的ips
    public void gwtIps() {
        //final String url = "http://203.107.1.1/181759/d?host=dev-api.stbl.cc";
        final String url = Config.aliyunHost + "?host=" + Config.reserveHostIp;
        // LogUtil.logE(tag,"url:"+url);
        HandleAsync hand = new HandleAsync();
        hand.excute(new HandleAsync.Listener() {
            @Override
            public String getResult() throws ClientProtocolException, IOException {
                //LogUtil.logE(tag, "getIps");
                String result = HttpGetConnection.getConnectUrl(url);
                LogUtil.logE(tag, result);
                DnsResult item = JSONHelper.getObject(result, DnsResult.class);
                //LogUtil.logE(tag, item.getIps().size() + "");
                if (item != null && item.getIps().size() > 0) {
                    //LogUtil.logE(tag, item.getIps().get(0));
                    SharedCommon.putIp(StartActivity.this, item.getIps().get(0));
                }
                return null;
            }

            @Override
            public void parse(String result) {
                //LogUtil.logE(tag, "parse");
                dealDirect();
            }
        });
    }


    //处理https
    public void dealHttps() {
//        try {
//            String url = SharedCommon.getIp(this);
//            final String host = Config.reserveHostIp;//"dev-api.stbl.cc";// dev-api.stbl.cc;test2-api.stbl.cc;这两个host都verify true
//            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
//            connection.setRequestProperty("Host", host);
//            connection.setHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    boolean isPassed = HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
//                    LogUtil.logE(tag, isPassed + "");
//                    return isPassed;
//                }
//            });
//
//            LogUtil.logE(tag, "hello");
//            // connection.connect();
//            String result = HttpGetConnection.getResult(connection);
//            LogUtil.logE(tag, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.logE(tag, "eecep:" + e.getLocalizedMessage());
//        }
    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        //如果更新失败的话，去哪里好呢
        if (item.getIssuccess() != BaseItem.successTag) {
            //ToastUtil.showToast(item.getErr().getMsg());
            switch (methodName) {
                //如果更新失败的话，去哪里好呢
                case Method.refreshToken:
                    if (item.getIssuccess() == BaseItem.errorNoTaostTag) {
                        TipsDialog dialog = TipsDialog.popup(StartActivity.this, getString(R.string.tip), getString(R.string.common_netword_not_available_tips), getString(R.string.im_confirm));
                        dialog.setCancelable(false);
                        dialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
                            @Override
                            public void onConfirm() {
                                dealDirect();
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                    }
            }

            return;
        }
        String con = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.refreshToken:
                LogUtil.logE(tag, "refreshToken:" + con);
                //保存
                AuthToken token = JSONHelper.getObject(con, AuthToken.class);
                //SharedToken.putValue(StartActivity.this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRoleflag(), token.getExpiriestime());
                SharedToken.putTokenValue(token);
                judgeUserRole();
                break;
        }

    }

}
