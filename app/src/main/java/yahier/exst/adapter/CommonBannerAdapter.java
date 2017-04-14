package yahier.exst.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.login.GuideActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

public class CommonBannerAdapter extends PagerAdapter {
    String tag = "CommonBannerAdapter";
    private LayoutInflater inflater;
    private Activity mContext;
    HashMap<Integer, View> views;
    int size;
    public final static float rateOfWH = 2.0f / 5;
    List<Banner> list;

    public CommonBannerAdapter(Activity mContext, List<Banner> list) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        views = new HashMap<Integer, View>();
        this.list = list;
        size = list.size();
        if (size == 2) {
            size = 4;
            list.add(list.get(0));
            list.add(list.get(1));
        }
    }

    @Override
    public int getCount() {
        if (size > 1)
            return size * 10000;
        else
            return size;

    }

    @Override
    public Object instantiateItem(ViewGroup group, int position) {
        while (position >= size) {
            position = position - size;
        }
        final Banner banner = list.get(position);
        View view = views.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.common_pager_item, group, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            PicassoUtil.loadBanner(mContext, banner.getImgurl(), imageView);
            views.put(position, view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        group.addView(view);

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(mContext, UmengClickEventHelper.SCBN);
                dealBannerJump(mContext, banner);
            }
        });

        return view;
    }

    public static void dealBannerJump(Context mContext, Banner banner){
        String parame = banner.getParame();
        //parame ="http://test2-wap.stbl.cc/wx/Pinball.html?m=99&v=1";
        if (parame == null || parame.equals(""))
            return;

        //如果是http或者https开头，则直接进入网页
        if (parame.startsWith("http") || parame.startsWith("https")) {
            Intent intent = new Intent(mContext, CommonWeb.class);
            intent.putExtra("url", parame);
            intent.putExtra("title", banner.getTitle());
            mContext.startActivity(intent);
            return;
        }

        int mIndex1 = parame.indexOf("m=") + 2;
        int mIndex2 = parame.indexOf("&");
        int vIndex1 = parame.indexOf("v=") + 2;
        if (mIndex1 < 0 || mIndex2 < 0 || vIndex1 < 0)
            return;
        String m = parame.substring(mIndex1, mIndex2);
        String v = parame.substring(vIndex1, parame.length());

        LogUtil.logE("标值是:" + m + "——————————" + v);

        long businessId = 0;
        int mInt = -1;
        try {
            mInt = Integer.valueOf(m);
            businessId = Long.valueOf(v);
        } catch (Exception e) {

        }

        Intent intent;
        switch (mInt) {
            case Banner.typeWeb:
                intent = new Intent(mContext, CommonWeb.class);
                intent.putExtra("url", parame);
                intent.putExtra("title", banner.getTitle());
                intent.putExtra("isBanner",true);
                mContext.startActivity(intent);
                break;
            case Banner.typeGoods:
                if (businessId == 0) {
                    intent = new Intent(mContext, MallAct.class);
                } else {
                    intent = new Intent(mContext, MallGoodsDetailAct.class);
                    intent.putExtra("goodsid", businessId);
                }
                mContext.startActivity(intent);
                break;
            case Banner.typeCard:
                if (businessId == 0)
                    return;
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", businessId);
                mContext.startActivity(intent);
                break;
            case Banner.typeTopic:

                break;
            case Banner.typeStatuses:
                if (businessId == 0)
                    return;
                intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", businessId);
                mContext.startActivity(intent);
                break;
            case Banner.typeOrder:
                //
                break;
            case Banner.typeNoAction:
                // pass
                break;
            case Banner.typeModel:// 模块跳转
                // switch (valueInt) {
                // case Banner.typeModelHome:
                // break;
                // case Banner.typeModelStatuses:
                // break;
                // case Banner.typeModelMessage:
                // break;
                // case Banner.typeModelCommunity:
                // break;
                // case Banner.typeModelMall:
                // break;
                // case Banner.typeModelMaotai:
                // break;
                // }
                break;
        }
    }

    boolean isDealTempAccount() {
        boolean temp = false;
        int roleFlag = Integer.valueOf(SharedToken.getRoleFlag(mContext));
        if (UserRole.isTemp(roleFlag)) {
            temp = true;
            TipsDialog.popup(mContext, mContext.getString(R.string.temp_account_msg), mContext.getString(R.string.temp_account_cancel), mContext.getString(R.string.temp_account_ok), new TipsDialog.OnTipsListener() {
                @Override
                public void onConfirm() {
                    mContext.startActivity(new Intent(mContext, GuideActivity.class));
                    mContext.finish();
                }

                @Override
                public void onCancel() {

                }
            });

        }
        return temp;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        while (position >= list.size()) {
            position = position - list.size();
        }
        // container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    OnPagerItemClickListener onPagerItemClick;

    public void setPagerItemOnClickListener(OnPagerItemClickListener onPagerItemClick) {
        this.onPagerItemClick = onPagerItemClick;
    }

    public interface OnPagerItemClickListener {
        void onPagerItemClick();
    }
}
