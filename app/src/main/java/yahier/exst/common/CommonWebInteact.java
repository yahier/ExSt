package yahier.exst.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.PublishShoppingActivity;
import com.stbl.stbl.act.ad.PurchaseAdAualifyAct;
import com.stbl.stbl.act.dongtai.AdDongtaiDetailAct;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.DongtaiPulishLongAct;
import com.stbl.stbl.act.dongtai.PublishShortStatusActivity;
import com.stbl.stbl.act.dongtai.VideoListAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.help.GiveHelpActivity;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.home.mall.OrderDetailBuyerAct;
import com.stbl.stbl.act.home.mall.integral.MallIntegralAct;
import com.stbl.stbl.act.home.mall.integral.MallIntegralGoodsDetailAct;
import com.stbl.stbl.act.home.seller.OrderDetailAct;
import com.stbl.stbl.act.login.StartActivity;
import com.stbl.stbl.act.mine.MinePeopleResourceAct;
import com.stbl.stbl.act.mine.MineWalletAct;
import com.stbl.stbl.act.mine.MyQrcodeActivity;
import com.stbl.stbl.act.mine.SettingMainAct;
import com.stbl.stbl.act.mine.UserInfoMain;
import com.stbl.stbl.barcoe.CaptureActivity;
import com.stbl.stbl.item.redpacket.RedpacketDetail;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * 与网页的交互页面，获取网页的参数，跳转到app的对应页面。
 *
 * @author lenovo
 */
public class CommonWebInteact extends BaseActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_interact);
        tv = (TextView) findViewById(R.id.tv);
        dealParame();
        finish();
    }

    void dealParame() {
        Uri uri = getIntent().getData();
        if (uri == null)
            return;
        String type = uri.getQueryParameter("m");
        String value = uri.getQueryParameter("v");
        String value2 = uri.getQueryParameter("v2");
        LogUtil.logE("CommonWebInteact：" + type + "——" + value + "——" + value2);
        int typeInt = 0;
        long valueLong = 0;
        try {
            typeInt = Integer.valueOf(type);
            valueLong = Long.valueOf(value);
            //
        } catch (Exception e) {
            // return;
        }
        //tv.setText("传递来的值是 " + type + "——" + value);
        Intent intent = null;
        switch (typeInt) {
            case Banner.typeOrder:
                if (valueLong == 0)
                    finish();
                // 如果用户id和卖家id相同，那就是卖家
                if (value2 != null && SharedToken.getUserId(this).equals(value2)) {
                    intent = new Intent(this, OrderDetailAct.class);
                    intent.putExtra("orderid", valueLong);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, OrderDetailBuyerAct.class);
                    intent.putExtra("orderid", valueLong);
                    startActivity(intent);
                    LogUtil.logE("finish");
                }

                break;
            case Banner.typeGoods:
                if (valueLong == 0)
                    finish();
                intent = new Intent(this, MallGoodsDetailAct.class);
                intent.putExtra("goodsid", valueLong);
                startActivity(intent);
                break;
            case Banner.typeStatuses:
                if (valueLong == 0)
                    finish();
                intent = new Intent(this, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", valueLong);
                startActivity(intent);
                break;
            case Banner.typeTopic:

                break;
            case Banner.typeCard:
                intent = new Intent(this, TribeMainAct.class);
                intent.putExtra("userId", Long.valueOf(value));
                startActivity(intent);
                break;
            case Banner.typeExchangeMall:
                if (valueLong == 0){
                    intent = new Intent(this, MallIntegralAct.class);}
                else{
                    intent = new Intent(this, MallIntegralGoodsDetailAct.class);
                    intent.putExtra("goodsid", valueLong);
                   // it.putExtra("integralBalance",integralBalance);
                }
                startActivity(intent);
                setResult(Activity.RESULT_OK);
                break;
            case Banner.typeShoppingCirclePulish: //开通广告页
            case Banner.typePurchaseAd: //开通广告页
                intent = new Intent(this,PurchaseAdAualifyAct.class);
                startActivity(intent);
                break;
            case Banner.typeModel:
                int valueInt = Integer.valueOf(value);
                switch (valueInt) {
                    case Banner.typeModelHome:
                        break;
                    case Banner.typeModelStatuses:
                        break;
                    case Banner.typeModelMessage:
                        break;
                    case Banner.typeModelCommunity:
                        break;
                    case Banner.typeModelMall:
                        break;
                    case Banner.typeModelMaotai:
                        break;
                }

                break;
            case Banner.typeWeb:
                // intent = new Intent(this,OrderDetailAct.class);
                intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url", value);
                startActivity(intent);
                break;

            case Banner.typeUserInfoSetting://跳转至资料设置页面
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JSQHBQXRW_WSGRXX);
                intent = new Intent(this, UserInfoMain.class);
                intent.putExtra(EXTRA.USER_ITEM, SharedUser.getUserItem());
                if (valueLong == 1) intent.putExtra("jumptype", 1);
                startActivity(intent);
                break;
            case Banner.typePulishDynamic://跳转至短动态发布页面
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JSQHBQXRW_FBYTDT);
                intent = new Intent(this,PublishShortStatusActivity.class);
                if (valueLong == 1) intent.putExtra("jumptype", PublishShortStatusActivity.JUMP_TYPE_TASK);
                startActivity(intent);
                break;
            case Banner.typeRichScan://扫一扫
                intent = new Intent(this, CaptureActivity.class);
                startActivity(intent);
                break;
            case Banner.typeShouTu://我要收徒
                intent = new Intent(mActivity, MyQrcodeActivity.class);
                intent.putExtra("isFromMine", false);
                mActivity.startActivity(intent);
                break;
            case Banner.typeAccountUpgrade://账号升级
                intent = new Intent(this, CommonWeb.class);
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.account_update, "");
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case Banner.typePulishRedPacket:// 红包发布
                intent = new Intent(this,PublishShoppingActivity.class);
                startActivity(intent);
                break;
            case Banner.typePulishLongDynamic:// 长动态发布
                intent = new Intent(this,DongtaiPulishLongAct.class);
                startActivity(intent);
                break;
            case Banner.typeWallet: // 我的钱包
                intent = new Intent(this,MineWalletAct.class);
                startActivity(intent);
                break;
            case Banner.typeMyContacts:// 我的人脉
                intent = new Intent(this,MinePeopleResourceAct.class);
                startActivity(intent);
                break;
            case Banner.typeAppSetting:// App设置
                intent = new Intent(this,SettingMainAct.class);
                startActivity(intent);
                break;
            case Banner.typeWonderfulVideo:// 精彩视频
                startActivity(new Intent(mActivity, VideoListAct.class));
                break;
            case Banner.typeMine:// 我的模块
                intent = new Intent(mActivity, TabHome.class);
                intent.putExtra("tabIndex", 3);
                startActivity(intent);
                break;
            case Banner.typeShoppingCircle: //商圈列表
                intent  = new Intent(mActivity, TabHome.class);
                intent.setAction(ACTION.GO_TO_STATUS_PAGE);
                intent.putExtra(TabHome.Index_index,2);
                startActivity(intent);
                break;
            case Banner.getTypeShoppingCircleDetail: //商圈详情
                intent = new Intent(mActivity, AdDongtaiDetailAct.class);
                intent.putExtra("statusesId",value);
                intent.putExtra("redpacketstatus", RedpacketDetail.status_pickabel);
                startActivity(intent);
                break;
            case Banner.typeBangyibang: //帮一帮
                startActivity(new Intent(mActivity, GiveHelpActivity.class));
                break;
            case Banner.typeExchangeDetail: //兑换商品列表
                startActivity(new Intent(this, MallIntegralAct.class));
                break;
            default:
                if (!isTopActivity()) {
                    intent = new Intent(this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
                break;
        }

    }

    /**
     * 判断程序是否在运行
     */
    private boolean isTopActivity() {
        String packageName = getPackageName();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            //应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName()) &&
                    !this.getComponentName().getClassName().equals(tasksInfo.get(0).baseActivity.getClassName())) {
                am.moveTaskToFront(getTaskId(), 0);
                return true;
            }
        }
        return false;
    }

}
