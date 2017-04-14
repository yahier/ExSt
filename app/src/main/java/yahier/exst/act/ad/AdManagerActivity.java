package yahier.exst.act.ad;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MineWalletAct;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.ad.PublishAdStatusItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * 开通广告位后的管理页面
 * Created by Administrator on 2016/9/25 0025.
 */

public class AdManagerActivity extends BaseActivity implements View.OnClickListener{
    private ImageView topLeft; //返回
    private ImageView topRight; //分享
    private TextView tvAdStatus; //广告投放状态
    private ImageView ivRight2; //广告投放右侧箭头
    private RelativeLayout rlPublishAd; //广告投放
    private RelativeLayout rlMyPrerogative; //我的权益
    private RelativeLayout rlShoppingCircleManager; //购物圈管理
    private RelativeLayout rlBusinessCooperation; //商务合作
    private View ivHaveCooperater; //商务合作红点提示
    private RelativeLayout rlMyRebate; //我的佣金
    private RelativeLayout rlYlshenqi; //引流神器
    private View ivHaveRebate; //我的返利红点提示
    private TextView tvService; //咨询客服
    private TextView tvAdDescription; //广告位介绍

    private PublishAdStatusItem mPublishAdStatusItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_manager_layout);

        topLeft = (ImageView) findViewById(R.id.top_left);
        topRight = (ImageView) findViewById(R.id.top_right);
        rlPublishAd = (RelativeLayout) findViewById(R.id.rl_publish_ad);
        tvAdStatus = (TextView) findViewById(R.id.tv_ad_status);
        ivRight2 = (ImageView) findViewById(R.id.iv_right2);
        rlMyPrerogative = (RelativeLayout) findViewById(R.id.rl_my_prerogative);
        rlShoppingCircleManager = (RelativeLayout) findViewById(R.id.rl_shopping_circle_manager);
        rlBusinessCooperation = (RelativeLayout) findViewById(R.id.rl_business_cooperation);
        ivHaveCooperater = findViewById(R.id.iv_have_cooperater);
        rlMyRebate = (RelativeLayout) findViewById(R.id.rl_my_rebate);
        ivHaveRebate = findViewById(R.id.iv_have_rebate);
        tvService = (TextView) findViewById(R.id.tv_service);
        tvAdDescription  = (TextView) findViewById(R.id.tv_ad_description);
        rlYlshenqi = (RelativeLayout) findViewById(R.id.rl_ylshenqi);

        topLeft.setOnClickListener(this);
        topRight.setOnClickListener(this);
        rlPublishAd.setOnClickListener(this);
        rlMyPrerogative.setOnClickListener(this);
        rlShoppingCircleManager.setOnClickListener(this);
        rlBusinessCooperation.setOnClickListener(this);
        rlMyRebate.setOnClickListener(this);
        tvService.setOnClickListener(this);
        tvAdDescription.setOnClickListener(this);
        rlYlshenqi.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAdStatus();
        //判断返利红点提示是否需要显示
        ivHaveRebate.setVisibility(AdHelper.isShowRebate() ? View.VISIBLE : View.GONE);
        //判断商务合作红点提示是否需要显示
        ivHaveCooperater.setVisibility(AdHelper.isShowCooperater() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.top_left: //返回
                finish();
                break;
            case R.id.top_right: //分享
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_main_open,"");
                if ("".equals(url)) return;
                ShareItem shareItem = new ShareItem();
                shareItem.title = getString(R.string.ad_share_adurl_title);
                shareItem.content = String.format(getString(R.string.ad_share_adurl_content), SharedUser.getUserNick());
                shareItem.link = url;
                new ShareDialog(this).shareWebpage(shareItem);
                break;
            case R.id.rl_my_prerogative: //我的权益
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJLBWDQY);
                intent = new Intent(this,AdMyPrerogativeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_publish_ad:  //投放广告
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJLBTFGG);
                toPublishAd();
                break;
            case R.id.rl_shopping_circle_manager: //购物圈管理
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJLBGWQGL);
                intent = new Intent(this,SetBrandAct.class);
                startActivity(intent);
                break;
            case R.id.rl_business_cooperation: //商务合作
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJLBSWHZ);
                /**记录是否有新商务合作信息*/
                AdHelper.setShowCooperater(false);
                intent = new Intent(this,AdBusinessCooperationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_rebate: //我的返利
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJLBWDFL);
                /**记录是否有新返利信息*/
                AdHelper.setShowRebate(false);
                intent = new Intent(this,MineWalletAct.class);
//                intent = new Intent(this,MyFanliActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_service: //咨询客服
                TipsDialog2.showHotLine(this);
                break;
            case R.id.tv_ad_description: //了解更多
                url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_brandplus_introd,"");
                if ("".equals(url)) return;
                intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;
            case R.id.rl_ylshenqi: //引流神器
                MobclickAgent.onEvent(this, UmengClickEventHelper.PPJJLBYLSQ);
                url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_drains_introd,"");
                if ("".equals(url)) return;
                intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;
        }
    }
    /**去投放广告*/
    private void toPublishAd(){
        if (mPublishAdStatusItem == null){
            WaitingDialog.show(this,true);
            getAdStatus();
        }else{
            if (mPublishAdStatusItem.getCanapply() == 0){ //不能投放
                TipsDialog2.popup(this,mPublishAdStatusItem.getMsg(),getString(R.string.queding));
            }else{
                Intent intent = new Intent(this,PublishAdAct.class);
                startActivity(intent);
            }
        }
    }
    /**获取广告投放状态*/
    private void getAdStatus(){
        Params params = new Params();
        new HttpEntity(this).commonPostData(Method.adsysApplyStatusGet, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                WaitingDialog.dismiss();
                LogUtil.logE("LogUtil",methodName +"---"+result);
                BaseItem item = JSONHelper.getObject(result,BaseItem.class);
                if (item == null) return;
                if (item.getIssuccess() != BaseItem.successTag){
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null){
                        ToastUtil.showToast(mActivity, item.getErr().getMsg());
                    }
                }

                String json = JSONHelper.getStringFromObject(item.getResult());
                switch (methodName){
                    case Method.adsysApplyStatusGet:
                        mPublishAdStatusItem = JSONHelper.getObject(json,PublishAdStatusItem.class);
                        if (mPublishAdStatusItem != null){
                            if (mPublishAdStatusItem.getStatus() == 0){
                                tvAdStatus.setVisibility(View.VISIBLE);
                                ivRight2.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
            }
        });
    }
}
