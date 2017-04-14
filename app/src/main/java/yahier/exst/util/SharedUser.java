package yahier.exst.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;

public class SharedUser {
    static final String nameUser = "user";

    public final static String keyareacode = "areacode";
    public final static String keyPhonePrex = "phonePrex";
    public final static String keyPhone = "phone";
    public final static String keyPwd = "pwd";
    public final static String keyNick = "name";
    public final static String keyphotoImg = "photo";
    public final static String COUNTRY = "country";
    public final static String COUNTRY_CODE = "country_code";
    public final static String INVITE_CODE = "invite_code";
    public final static String keyotherauthtype = "otherauthtype";
    public final static String keyhaspaypassword = "haspaypassword";
    public final static String keySignature = "signature";
    public final static String keyAge = "age";
    public final static String keyBirthday = "birthday";
    public final static String keyGender = "gender";
    public final static String keyLocation = "location";
    public final static String keyOpenId = "openid";
    public final static String keyUnionId = "unionid";

    public static void putUserValue(UserItem userItem) {
        Editor editor = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE).edit();
        if (userItem.getAreacode() != null)
            editor.putString(keyareacode, userItem.getAreacode());
        if (userItem.getTelphone() != null)
            editor.putString(keyPhone, userItem.getTelphone());
        if (userItem.getPhonePrex() != null)
            editor.putString(keyPhonePrex, userItem.getPhonePrex());
        if (userItem.getLoginPwd() != null)
            editor.putString(keyPwd, userItem.getLoginPwd());
        if (userItem.getOtherauthtype() != 0)
            editor.putInt(keyotherauthtype, userItem.getOtherauthtype());
        if (userItem.getHaspaypassword() != 0)
            editor.putInt(keyhaspaypassword, userItem.getHaspaypassword());
        if (userItem.getNickname() != null)
            editor.putString(keyNick, userItem.getNickname());
        if (userItem.getImgmiddleurl() != null)
            editor.putString(keyphotoImg, userItem.getImgmiddleurl());
        if (userItem.getImgurl() != null)
            editor.putString(keyphotoImg, userItem.getImgurl());

        if (userItem.getInvitecode() != null)
            editor.putString(INVITE_CODE, userItem.getInvitecode());
        if (userItem.getSignature() != null)
            editor.putString(keySignature, userItem.getSignature());
        if (userItem.getAge() != 0)
            editor.putInt(keyAge, userItem.getAge());
        //if (userItem.getGender() != UserItem.gender_unknow)
        editor.putInt(keyGender, userItem.getGender());
        if (userItem.getBirthday() != 0)
            editor.putLong(keyBirthday, userItem.getBirthday());
        if (userItem.getCityname() != null)
            editor.putString(keyLocation, userItem.getCityname());


        if (userItem.getOpenid() != null)
            editor.putString(keyOpenId, userItem.getOpenid());

        if (userItem.getUnionid() != null)
            editor.putString(keyUnionId, userItem.getUnionid());

        LogUtil.logE("share openid put", userItem.getOpenid());
        editor.apply();
    }


    public static void putCountryCode(String country, String countryCode) {
        Editor editor = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE).edit();
        editor.putString(COUNTRY, country);
        editor.putString(COUNTRY_CODE, countryCode);
        editor.apply();
    }

    public static void putUserPhone(String phone) {
        Editor editor = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE).edit();
        editor.putString(keyPhone, phone);
        editor.apply();
    }

    public static void putAuthType(int type) {
        Editor editor = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE).edit();
        editor.putInt(keyotherauthtype, type);
        editor.apply();
    }


    public static String getUserNick() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE);
        return shared.getString(keyNick, "");
    }

    public static String getPhone() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE);
        return shared.getString(keyPhone, "");
    }


    public static UserItem getUserItem() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE);
        UserItem user = new UserItem();
        user.setNickname(shared.getString(keyNick, ""));
        user.setInvitecode(shared.getString(INVITE_CODE, ""));
        user.setAreacode(shared.getString(keyareacode, ""));
        user.setImgmiddleurl(shared.getString(keyphotoImg, ""));
        user.setImgurl(shared.getString(keyphotoImg, ""));
        user.setLoginPwd(shared.getString(keyPwd, ""));
        user.setTelphone(shared.getString(keyPhone, ""));
        user.setPhonePrex(shared.getString(keyPhonePrex, ""));
        user.setUserid(Long.valueOf(SharedToken.getUserId()));
        user.setHaspaypassword(shared.getInt(keyhaspaypassword, UserItem.haspaypasswordYes));
        user.setOtherauthtype(shared.getInt(keyotherauthtype, UserItem.otherauthtypeNone));
        user.setSignature(shared.getString(keySignature, ""));
        user.setAge(shared.getInt(keyAge, 0));
        user.setGender(shared.getInt(keyGender, UserItem.gender_unknow));
        user.setBirthday(shared.getLong(keyBirthday, 0));
        user.setCityname(shared.getString(keyLocation, ""));
        user.setOpenid(shared.getString(keyOpenId, ""));
        user.setUnionid(shared.getString(keyUnionId, ""));

        LogUtil.logE("share gender get", user.getGender());

        return user;
    }


    public static String getCountry() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE);
        return shared.getString(COUNTRY, "");
    }

    public static String getCountryCode() {
        SharedPreferences shared = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE);
        return shared.getString(COUNTRY_CODE, "");
    }


    //只在我的-设置-退出事件时 被调用
    public static void clearData() {
        String phone = getPhone();
        Editor editor = MyApplication.getContext().getSharedPreferences(nameUser, Context.MODE_PRIVATE).edit();
        editor.clear().apply();
        putUserPhone(phone);
//        editor.putString(keyNick, "");
//        editor.putString(keyphotoImg, "");
//        editor.putString(keyPwd, "");
//        editor.putString(INVITE_CODE, "");
//        editor.putInt(keyotherauthtype, 0);
//        editor.putInt(keyhaspaypassword, -1);
//        editor.apply();
    }


}
