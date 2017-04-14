package yahier.exst.act.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.wxapi.MD5Util;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class WeixinBindAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
    private ImageView ivUserImg;
    private Button btnOk;
    private UMShareAPI mShareAPI;
    private String phone;
    private boolean isBind = false; //当前是否属于绑定状态
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_weixin_bind_layout);
        ivUserImg = (ImageView) findViewById(R.id.iv_user_img);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        mShareAPI = UMShareAPI.get(this);
        mLoadingDialog = new LoadingDialog(this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        isBind = intent.getBooleanExtra("isBind", false);
        if (isBind) {
            setLabel("解除绑定");
            btnOk.setText("解除绑定");
            getWxHeaderImg();
        } else {
            setLabel("绑定微信");
            btnOk.setText("绑定微信");
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_ok:
                if (!isBind) {
                    weixinAuth();
                } else {
                    bindOrUnBindWx(null);
                }
                break;

            default:
                break;
        }
    }

    private void getWxHeaderImg() {
        mLoadingDialog.show();
        Params params = new Params();
        params.put("opentype", UserItem.otherauthtypeWeixin);
        new HttpEntity(this).commonPostData(Method.mine_get_bind_info, params, this);
    }

    private void bindOrUnBindWx(final WxInfo wxInfo) {
        mLoadingDialog.show();
        getPassword(this, new OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                mLoadingDialog.dismiss();

                if (pwd == null || "".equals(pwd)) return;
                Params params = new Params();
                params.put("opentype", 1); //1-微信，2-QQ
                if (pwd != null || !pwd.equals("")) params.put("paypwd", pwd);
                if (wxInfo != null) {
                    String resultOpenId = "";
                    String resultUnionId = "";
                    try {
                        resultOpenId = DesUtils.EncryptAsDoNet(wxInfo.openid);
                        resultUnionId = DesUtils.EncryptAsDoNet(wxInfo.unionid);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    params.put("openid", resultOpenId);
                    params.put("unionid", resultUnionId);
                    params.put("nickname", wxInfo.nickname);
                    params.put("sex", wxInfo.sex);
                    params.put("headimgurl", wxInfo.headimgurl);
                    params.put("country", wxInfo.country);
                    params.put("city", wxInfo.city);
                    params.put("province", wxInfo.province);
                    params.put("language", wxInfo.language);
                    //LogUtil.logE("openid:"+resultOpenId+"--opentype:"+1+"--paypwd:"+pwd + "--headimgurl:" + wxInfo.headimgurl);
                }
                mLoadingDialog.show();
                new HttpEntity(WeixinBindAct.this).commonPostData(Method.wxBindOrUnBind, params, WeixinBindAct.this);
            }
        });
    }

    //获取用户是否有设置支付密码，没有则跳转到设置支付密码界面
    public void getPassword(final Context ctx, final OnInputListener pwdListener) {
        new HttpUtil(ctx, null).post(Method.userPasswordCheck, new OnFinalHttpCallback() {
            @Override
            public void onHttpResponse(String methodName, String json, Object handle) {
                mLoadingDialog.dismiss();
                switch (methodName) {
                    case Method.userPasswordCheck:
                        boolean needPwd = JSONHelper.getObject(json, Boolean.class);
                        if (needPwd) {
                            PayingPwdDialog dialog = new PayingPwdDialog();
                            dialog.setOnInputListener(new OnInputListener() {

                                @Override
                                public void onInputFinished(String pwd) {
                                    if (pwd != null) {
                                        String pwdMd5 = MD5Util.MD5Encode(pwd, null);
                                        pwdListener.onInputFinished(pwdMd5);
                                    }
                                }
                            });
//						dialog.setOnInputListener(pwdListener);
                            dialog.show(ctx);
                        } else {
//						pwdListener.onInputFinished("");
                            ToastUtil.showToast(WeixinBindAct.this, "请先设置支付密码");
                            Intent intent = new Intent(WeixinBindAct.this, PayPasswordSettingAct.class);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }
                        break;
                }
            }

            @Override
            public void onHttpError(String methodName, String msg, Object handle) {
                mLoadingDialog.dismiss();
                pwdListener.onInputFinished(null);
            }
        });
    }

    @Override
    public void parse(String methodName, String result) {
        mLoadingDialog.dismiss();
        LogUtil.logE("methodName:" + methodName + "--result:" + result);
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        try {
            JSONObject json = new JSONObject(result);
            int issuccess = json.optInt("issuccess");
            if (issuccess != BaseItem.successTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Method.wxBindOrUnBind.equals(methodName)) {
            isBind = !isBind;
            if (isBind) {
                setLabel("解除绑定");
                ToastUtil.showToast(this, "绑定成功");
                btnOk.setText("解除绑定");
                //SharedUser.putAuthType(this, UserItem.otherauthtypeWeixin);
                SharedUser.putAuthType(UserItem.otherauthtypeWeixin);
            } else {
                setLabel("绑定微信");
                ToastUtil.showToast(this, "解除绑定成功");
                btnOk.setText("绑定微信");
                PicassoUtil.load(this, R.drawable.def_head, ivUserImg);
                //SharedUser.putAuthType(this, UserItem.otherauthtypeNone);
                SharedUser.putAuthType(UserItem.otherauthtypeNone);
            }
            Intent intent = new Intent();
            intent.putExtra("bindWx", isBind);
            setResult(Activity.RESULT_OK, intent);
        } else if (Method.mine_get_bind_info.equals(methodName)) {
            try {
                JSONObject json = new JSONObject(result);
                JSONObject jsonInfo = json.optJSONObject("result");
                String headerimg = jsonInfo.optString("headimgurl");
                ImageUtils.loadCircleHead(headerimg, ivUserImg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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

    private void getWeixinInfo(){
        mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                LogUtil.logE("user info: " + data.toString());

                Set<String> keys = data.keySet();
                WxInfo wxInfo = new WxInfo();
                for (String key : keys) {
                    if (key.equals("sex")) {
                        wxInfo.sex = data.get(key).toString();
                    } else if (key.equals("nickname")) {
                        wxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("unionid")) {
                        wxInfo.unionid = data.get(key).toString();
                    } else if (key.equals("province")) {
                        wxInfo.province = data.get(key).toString();
                    } else if (key.equals("openid")) {
                        wxInfo.openid = data.get(key).toString();
                    } else if (key.equals("language")) {
                        wxInfo.language = data.get(key).toString();
                    } else if (key.equals("headimgurl")) {
                        wxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("country")) {
                        wxInfo.country = data.get(key).toString();
                    } else if (key.equals("city")) {
                        wxInfo.city = data.get(key).toString();
                    } else if (key.equals("screen_name")) {
                        wxInfo.nickname = data.get(key).toString();
                    } else if (key.equals("profile_image_url")) {
                        wxInfo.headimgurl = data.get(key).toString();
                    } else if (key.equals("gender")) {
                        wxInfo.sex = data.get(key).toString();
                    }
                }
                bindOrUnBindWx(wxInfo);
                if (wxInfo.headimgurl != null)
                    ImageUtils.loadCircleHead(wxInfo.headimgurl, ivUserImg);
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

    class WxInfo {
        String sex;
        String nickname;
        String unionid;
        String province;
        String openid = "";
        String language;
        String headimgurl;
        String country;
        String city;
    }

}
