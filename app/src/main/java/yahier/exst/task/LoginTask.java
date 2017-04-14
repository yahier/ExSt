package yahier.exst.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tnitf on 2016/3/13.
 */
public class LoginTask {

    /**
     * 获取国家区号
     */
    public static SimpleTask<HashMap<String, Object>> getCountryCodeList() {
        return new SimpleTask<HashMap<String, Object>>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getContriesPhoneCode, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    ArrayList<CountryPhoneCode> codeList = (ArrayList<CountryPhoneCode>) JSONArray.parseArray(response.result, CountryPhoneCode.class);
                    if (codeList == null) {
                        onError("数据返回错误");
                    } else {
                        map.put("CountryCodeList", codeList);
                        ArrayList<String> stringList = new ArrayList<>();
                        for (CountryPhoneCode code : codeList) {
                            stringList.add(code.getCountry() + "+" + code.getPrefix());
                        }
                        map.put("StringList", stringList);
                        onCompleted(map);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    /**
     * 密码登录
     */
    public static SimpleTask<AuthToken> pwdLogin(final String areacode, final String phone, final String pwd) {
        return new SimpleTask<AuthToken>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("areacode", areacode);
                json.put("phone", phone);
                json.put("pwd", pwd);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.authLogin, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    //AuthToken token = obj.getObject("result", AuthToken.class);    //fastjson的bug
                    AuthToken token = JSONHelper.getObject(response.result, AuthToken.class);
                    if (token != null) {
                        onCompleted(token);
                    } else {
                        onError("数据返回错误");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    /**
     * 带openid，unionid的登录
     *
     * @param openid
     * @param unionid
     * @return
     */
    public static SimpleTask<AuthToken> weixinLogin(final String openid, final String unionid) {
        return new SimpleTask<AuthToken>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("openid", openid);
                json.put("unionid", unionid);
                json.put("opentype", 1);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.wxLogin, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    //AuthToken token = obj.getObject("result", AuthToken.class);  //fastjson bug
                    AuthToken token = JSONHelper.getObject(response.result, AuthToken.class);
                    LogUtil.logE("LoginTask weixinLogin token:" + token);
                    if (token != null) {
                        onCompleted(token);
                    } else {
                        onError("数据返回错误");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    /**
     * 获取短信验证码
     */
    public static SimpleTask<Integer> getSmsCode(final String areacode, final String phone) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("areacode", areacode);
                json.put("phone", phone);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getJustmsCode, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    /**
     * 用图形验证码 验证码标识 获取短信验证码
     */
    public static SimpleTask<Integer> getSmsCode(final String vertifycode, final String randomid, final String areacode, final String phone) {
        return new SimpleTask<Integer>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("vertifycode", vertifycode);
                json.put("randomid", randomid);
                json.put("areacode", areacode);
                json.put("phone", phone);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getJustmsCode, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    onCompleted(1);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    /**
     * 短信验证码登录
     */
    public static SimpleTask<AuthToken> smsLogin(final String areacode, final String phone, final String smscode) {
        return new SimpleTask<AuthToken>() {
            @Override
            protected void call() {
                JSONObject json = new JSONObject();
                json.put("areacode", areacode);
                json.put("phone", phone);
                json.put("smscode", smscode);
                try {
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.smsLogin, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    //AuthToken token = obj.getObject("result", AuthToken.class);  //fastjson bug
                    AuthToken token = JSONHelper.getObject(response.result, AuthToken.class);
                    if (token != null) {
                        onCompleted(token);
                    } else {
                        onError("数据返回错误");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

}
