package yahier.exst.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * 友盟自定义事件统计
 * Created by Administrator on 2016/10/10 0010.
 */

public class UmengClickEventHelper {
    //首页banner点击 sybndj
    public static final String SYBNDJ = "sybndj";
    //首页广告位点击 syggwdj
    public static final String SYGGWDJ = "syggwdj";
    //我也要开通点击次数   wyyktcs
    public static final String WYYKTCS = "wyyktcs";
    //“品牌+”icon点击次数 ppjdjcs
    public static final String PPJDJCS = "ppjdjcs";
    //部落我的广告位点击次数 blwdggcs
    public static final String BLWDGGCS = "blwdggcs";
    //部落徒弟广告位点击次数 bltdggcs
    public static final String BLTDGGCS = "bltdggcs";
    //广告位介绍分享次数   fxggjscs
    public static final String FXGGJSCS = "fxggjscs";
    //商圈点击抢红包的次数 qhbcs
    public static final String QHBCS = "qhbcs";
    //商圈详情分享的点击次数 fxgwqcs
    public static final String FXGWQCS = "fxgwqcs";

    /**16.12.19*/ // MobclickAgent.onEvent(this,UmengClickEventHelper.SYCZYH);
    /**首页查找用户点击*/
    public static final String SYCZYH = "syczyh";
    /**首页每周排行榜点击*/
    public static final String SYMZPHB = "symzphb";
    /**首页兑换商城点击*/
    public static final String SYDHSC = "sydhsc";
    /**首页师徒商城点击*/
    public static final String SYSTSC = "systsc";
    /**首页热门大酋长推荐点击*/
    public static final String SYRMDQZTJ = "syrmdqztj";
    /**首页热门大酋长换一换点击*/
    public static final String SYRMDQZHYH = "syrmdqzhyh";
    /**动态搜索点击*/
    public static final String DTSS = "dtss";
    /**动态关注tab点击*/
    public static final String DTGZTAB = "dtgztab";
    /**动态广场tab点击*/
    public static final String DTGCTAB = "dtgctab";
    /**动态商圈tab点击*/
    public static final String DTSQTAB = "dtsqtab";
    /**动态消息点击*/
    public static final String DTXX = "dtxx";
    /**动态发布按钮点击*/
    public static final String DTFBAN = "dtfban";
    /**动态精彩视频入口点击*/
    public static final String DTJCSPRK = "dtjcsprk";
    /**动态品牌+入口点击*/
    public static final String DTPPRK = "dtpprk";
    /**发布红包点击*/
    public static final String FBHB = "fbhb";
    /**发布长动态点击*/
    public static final String FBCDT = "fbcdt";
    /**发布短动态点击*/
    public static final String FBDDT = "fbddt";
    /**视频分享点击*/
    public static final String SHPFX = "shpfx";
    /**视频转发点击*/
    public static final String SPZF = "spzf";
    /**消息创建讨论组点击*/
    public static final String XXCJTLZ = "xxcjtlz";
    /**消息添加好友点击*/
    public static final String XXTJHY = "xxtjhy";
    /**我的人脉点击*/
    public static final String WDRM = "wdrm";
    /**我的钱包点击*/
    public static final String WDQB = "wdqb";
    /**我的主页点击*/
    public static final String WDZY = "wdzy";
    /**我的订单点击*/
    public static final String WDDD = "wddd";
    /**我的动态点击*/
    public static final String WDDT = "wddt";
    /**帮一帮点击*/
    public static final String BYB = "byb";
    /**我的精彩链接点击*/
    public static final String WDJCLJ = "wdjclj";
    /**我的礼盒点击*/
    public static final String WDLH = "wdlh";
    /**我的相册点击*/
    public static final String WDXC = "wdxc";
    /**我的收藏点击*/
    public static final String WDSC = "wdsc";
    /**金豆充值点击*/
    public static final String JDCZ = "jdcz";
    /**师徒票赚取点击*/
    public static final String STPZQ = "stpzq";
    /**师徒票消费点击*/
    public static final String STPXF = "stpxf";
    /**帮一帮搜索点击*/
    public static final String BYBSS = "bybss";
    /**帮Ta点击*/
    public static final String BTA = "bta";
    /**收到的邀请点击*/
    public static final String SDDYQ = "sddyq";
    /**我要找人点击*/
    public static final String WYZR = "wyzr";
    /**签到按钮点击*/
    public static final String QDZN = "qdzn";
    /**兑换商品分享点击*/
    public static final String DHSPFX = "dhspfx";
    /**立即兑换按钮点击*/
    public static final String LJDHAN = "ljdhan";
    /**商城banner点击*/
    public static final String SCBN = "scbn";
    /**商品类目点击*/
    public static final String SPLM = "splm";
    /**商城搜索点击*/
    public static final String SCSS = "scss";
    /**推荐商品点击*/
    public static final String TJSP = "tjsp";
    /**猜你喜欢点击*/
    public static final String CNXH = "cnxh";
    /**商品按最新查看*/
    public static final String SPAZXCK = "spazxck";
    /**商品按销量查看*/
    public static final String SPAXLCK = "spaxlck";
    /**商品按价格查看*/
    public static final String SPAJGCK = "spajgck";
    /**商品分享点击*/
    public static final String SPFX = "spfx";
    /**收藏商品点击*/
    public static final String SCSP = "scsp";
    /**加入购物车点击*/
    public static final String JRGWC = "jrgwc";
    /**立即购买点击*/
    public static final String LJGM = "ljgm";
    /**部落分享点击*/
    public static final String BLFX = "blfx";
    /**部落帮Ta签到点击*/
    public static final String BLBTQD = "blbtqd";
    /**部落查看动态点击*/
    public static final String BLCKDT = "blckdt";
    /**部落精彩链接点击*/
    public static final String BLJCLJ = "bljclj";
    /**部落送礼物点击*/
    public static final String BLSLW = "blslw";
    /**部落加关注点击*/
    public static final String BLJGZ = "bljgz";
    /**部落加好友*/
    public static final String BLJHY = "bljhy";
    /**部落发消息点击*/
    public static final String BLFXX = "blfxx";
    /**我要收徒icon点击*/
    public static final String WYSTI = "wysti";
    /**我要逛逛icon点击*/
    public static final String WYGGI = "wyggi";
    /**升级账户icon点击*/
    public static final String SJZHI = "sjzhi";
    /**品牌+列表商务合作点击*/
    public static final String PPJLBSWHZ = "ppjlbswhz";
    /**品牌+列表投放广告点击*/
    public static final String PPJLBTFGG = "ppjlbtfgg";
    /**品牌+列表我的返利点击*/
    public static final String PPJLBWDFL = "ppjlbwdfl";
    /**品牌+列表购物圈管理点击*/
    public static final String PPJLBGWQGL = "ppjlbgwqgl";
    /**品牌+列表我的权益点击*/
    public static final String PPJLBWDQY = "ppjlbwdqy";
    /**品牌+列表引流神器点击*/
    public static final String PPJJLBYLSQ = "ppjjlbylsq";
    /**首页banner分享点击*/
    public static final String SYBFX = "sybfx";
    /**首页广告位分享点击*/
    public static final String SYGGWFX = "syggwfx";
    /**解锁抢红包权限任务*/
    public static final String JSQHBQXRW = "jsqhbqxrw";
    /**解锁抢红包次数任务*/
    public static final String JSQHBCSRW = "jsqhbcsrw";
    /**解锁抢红包权限任务_完善个人信息*/
    public static final String JSQHBQXRW_WSGRXX = "jsqhbqxrw_wsgrxx";
    /**解锁抢红包权限任务_发布一条动态*/
    public static final String JSQHBQXRW_FBYTDT = "jsqhbqxrw_fbytdt";
    /**HTML5内部分享*/
    public static final String HTML5_INNER_SHARE = "html5_inner_share";


    /**首页banner点击*/
    public static void onBannerClickEvent(Context context){
        MobclickAgent.onEvent(context,SYBNDJ);
    }

    /**首页广告位点击*/
    public static void onAdClickEvent(Context context){
        MobclickAgent.onEvent(context,SYGGWDJ);
    }

    /**我也要开通点击次数*/
    public static void onOpenMtClickEvent(Context context){
        MobclickAgent.onEvent(context,WYYKTCS);
    }
    /**“品牌+”icon点击次数*/
    public static void onPPJClickEvent(Context context){
        MobclickAgent.onEvent(context,PPJDJCS);
    }
    /**部落我的广告*/
    public static void onTribeMeClickEvent(Context context){
        MobclickAgent.onEvent(context,BLWDGGCS);
    }
    /**部落徒弟的广告*/
    public static void onTribeTudiClickEvent(Context context){
        MobclickAgent.onEvent(context,BLTDGGCS);
    }

    /**广告位介绍页分享次数*/
    public static void onAdShareClickEvent(Context context){
        MobclickAgent.onEvent(context,FXGGJSCS);
    }
    /**商圈点击抢红包的次数*/
    public static void onRedpackageClickEvent(Context context){
        MobclickAgent.onEvent(context,QHBCS);
    }
    /**商圈详情分享的点击次数*/
    public static void onShoppingCShareClickEvent(Context context){
        MobclickAgent.onEvent(context,FXGWQCS);
    }
}
