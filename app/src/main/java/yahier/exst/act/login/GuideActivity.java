package yahier.exst.act.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.AutoScrollViewPager;
import com.stbl.stbl.widget.CirclePageIndicator;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GuideActivity extends BaseActivity implements View.OnClickListener {

    private AutoScrollViewPager mViewPager;
    private CirclePageIndicator mPagerIndicator;
    private ArrayList<View> mViewList;
    private BannerPagerAdapter mAdapter;
    private View linLoginWechat;
    View layout_login, btn_register;

    private UMShareAPI mShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        mShareAPI = UMShareAPI.get(this);
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.FINISH_GUIDE_ACTIVITY);
        SharedUser.clearData();
    }


    private void initView() {
        mViewList = new ArrayList<>();
        mViewPager = (AutoScrollViewPager) findViewById(R.id.vp);
        mPagerIndicator = (CirclePageIndicator) findViewById(R.id.vpi);

        int[] resIds = new int[]{R.drawable.bg_guide_1, R.drawable.bg_guide_2, R.drawable.bg_guide_3, R.drawable.bg_guide_4};
        mAdapter = new BannerPagerAdapter(resIds);
        mViewPager.setAdapter(mAdapter);
        mPagerIndicator.setViewPager(mViewPager);
        mViewPager.startAutoScroll();

        layout_login = findViewById(R.id.layout_login);
        layout_login.setOnClickListener(this);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        findViewById(R.id.layout_tourist).setOnClickListener(this);
        linLoginWechat = findViewById(R.id.linLoginWechat);
        if (!CommonShare.isWechatInstalled(this)) {
            linLoginWechat.setVisibility(View.GONE);
            btn_register.setBackgroundResource(R.drawable.selector_white_btn);
            layout_login.setBackgroundResource(R.drawable.selector_white_btn);
        }
        linLoginWechat.setOnClickListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(GuideActivity.this, RegisterStep1ActNew.class));
                break;
            case R.id.layout_login:
                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                intent.putExtra(EXTRA.FROM_GUIDE_ACTIVITY, true);
                startActivity(intent);
                break;
            case R.id.linLoginWechat:
                weixinAuth();
                break;
            case R.id.layout_tourist:
                loginTemp();
                break;

        }
    }

    void loginTemp() {
        new HttpEntity(this).commonPostData(Method.loginTemp, null, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                LogUtil.logE(methodName, result);
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    String obj = JSONHelper.getStringFromObject(item.getResult());
                    AuthToken token = JSONHelper.getObject(obj, AuthToken.class);
                    //SharedToken.putValue(GuideActivity.this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRoleflag(), token.getExpiriestime());
                    SharedToken.putTokenValue(token);
                    startActivity(new Intent(GuideActivity.this, TabHome.class));
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.FINISH_GUIDE_ACTIVITY:
                    finish();
                    break;
            }
        }
    };

    boolean mIsDestroy = false;
    WxInfo mWxInfo;

    //以下是微信数据
    //微信授权得到信息
    private void weixinAuth() {
        mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());
                getWeixinInfo();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
            }
        });
    }

    private void getWeixinInfo() {
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());

                Set<String> keys = data.keySet();
                mWxInfo = new WxInfo();
                for (String key : keys) {
                    if (key.equals("sex")) {
                        mWxInfo.sex = data.get(key).toString();
                    } else if (key.equals("nickname")) {
                        mWxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("unionid")) {
                        mWxInfo.unionid = data.get(key).toString();
                    } else if (key.equals("province")) {
                        mWxInfo.province = data.get(key).toString();
                    } else if (key.equals("openid")) {
                        mWxInfo.openid = data.get(key).toString();
                    } else if (key.equals("language")) {
                        mWxInfo.language = data.get(key).toString();
                    } else if (key.equals("headimgurl")) {
                        mWxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("country")) {
                        mWxInfo.country = data.get(key).toString();
                    } else if (key.equals("city")) {
                        mWxInfo.city = data.get(key).toString();
                    } else if (key.equals("screen_name")) {
                        mWxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("profile_image_url")) {
                        mWxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("gender")) {
                        mWxInfo.sex = data.get(key).toString();
                    }
                }
                wxLogin(mWxInfo);
                deleteOauth();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastUtil.showToast(R.string.me_auth_fail);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                //授权取消
            }
        });
    }

    private void deleteOauth() {
        mShareAPI.deleteOauth(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    //微信登录.能登录则登录。不能登录就显示出微信账号的信息
    private void wxLogin(WxInfo wx) {
        LogUtil.logE("step1:wxLogin");
        try {
            String openidEncrypt = DesUtils.EncryptAsDoNet(wx.openid);
            String openUnionEncrypt = DesUtils.EncryptAsDoNet(wx.unionid);
            LoginTask.weixinLogin(openidEncrypt, openUnionEncrypt).setCallback(this, mWxLoginCallback).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SimpleTask.Callback<AuthToken> mWxLoginCallback = new SimpleTask.Callback<AuthToken>() {
        @Override
        public void onError(TaskError e) {
            WaitingDialog.dismiss();
            LogUtil.logE("step1 onError");
            //mLoadingDialog.dismiss();
            if (e == null || e.getMessage() == null) {
                ToastUtil.showToast(R.string.data_error);
                return;
            }
            if (e.code == ServerError.ERR_LOGIN_WX_NO_RECORD && mWxInfo != null) {
                //如果不能直接登录，保存微信授权得到的信息
                UserItem user = new UserItem();
                user.setImgurl(mWxInfo.headimgurl);
                user.setNickname(mWxInfo.nickname);
                user.setOpenid(mWxInfo.openid);
                user.setUnionid(mWxInfo.unionid);
                if (mWxInfo.sex.equals("1")) {
                    //user.setGender(UserItem.gender_boy);
                } else {
                    //user.setGender(UserItem.gender_girl);
                }


                SharedUser.putUserValue(user);
                LogUtil.logE("mWxLoginCallback onError");
                Intent intent = new Intent(GuideActivity.this, RegisterStep1ActNew.class);
                //intent.putExtra(KEY.USER_ITEM, user);
                startActivity(intent);
            } else {
                ToastUtil.showToast(e.getMessage());
            }
        }

        @Override
        public void onCompleted(AuthToken token) {
            WaitingDialog.dismiss();
            SharedToken.putTokenValue(token);
            //SharedToken.putValue(GuideActivity.this, token.getAccesstoken(), token.getRefreshtoken(), token.getUserid(), token.getRongyuntoken(),
            //        token.getUserinfo() != null ? token.getUserinfo().getRoleflag() : null, "" + token.getUserinfo().getMasterid(), token.getExpiriestime());
            STBLWession.getInstance().writeLiveRoomToken(token.getLiveRoomToken());
            //MyApplication.getContext().setUserItem(token.getUserinfo());
            LogUtil.logE("mWxLoginCallback onCompleted");
            int roleFlag;
            try {
                roleFlag = Integer.valueOf(token.getUserinfo().getRoleflag());
                if (UserRole.isNotMaster(roleFlag)) {
                    Intent intent = new Intent(GuideActivity.this, ChooseMasterAct.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(GuideActivity.this, TabHome.class));
                }
            } catch (Exception e) {

            }
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    class BannerPagerAdapter extends PagerAdapter {

        private int[] mArray;

        public BannerPagerAdapter(int[] array) {
            mArray = array;
        }

        @Override
        public int getCount() {
            return mArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageUtils.loadImage(mArray[position], view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}
