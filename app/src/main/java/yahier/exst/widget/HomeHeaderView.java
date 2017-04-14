package yahier.exst.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiRankAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.HomeMainAct;
import com.stbl.stbl.act.home.mall.MallAct;
import com.stbl.stbl.act.home.mall.SharedMallType;
import com.stbl.stbl.act.home.mall.integral.MallIntegralAct;
import com.stbl.stbl.adapter.BannerPagerAdapter;
import com.stbl.stbl.adapter.CommonBannerAdapter;
import com.stbl.stbl.adapter.home.BigChiefGridAdapter;
import com.stbl.stbl.model.Banner;
import com.stbl.stbl.model.Headmen;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/26.
 */

public class HomeHeaderView extends LinearLayout implements View.OnClickListener {

    private static final int BIG_CHIEF_COUNT = 12;

    private HomeMainAct mActivity;

    private BannerView mBannerView;
    private ArrayList<Banner> mBannerList;
    private BannerPagerAdapter mBannerAdapter;

    private NestedGridView mBigChiefGv;
    private ArrayList<Headmen> mBigChiefList;
    private BigChiefGridAdapter mBigChiefAdapter;

    private TextView mChangeTv;
    private ArrayList<Headmen> mAllBigChiefList;
    private int mStart;
    private int mEnd;

    public HomeHeaderView(Context context) {
        this(context, null);
    }

    public HomeHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (HomeMainAct) context;
        if (!SharedPrefUtils.isNewYear()) {
            LayoutInflater.from(context).inflate(R.layout.header_home_list, this);
        }else{
            LayoutInflater.from(context).inflate(R.layout.header_home_list_skin, this);
        }
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_paihangbang).setOnClickListener(this);
        findViewById(R.id.btn_integral).setOnClickListener(this);
        findViewById(R.id.btn_mall).setOnClickListener(this);
        mChangeTv = (TextView) findViewById(R.id.tv_change);
        mChangeTv.setOnClickListener(this);
        //banner
        mBannerList = new ArrayList<>();
        mBannerView = (BannerView) findViewById(R.id.banner);
        mBannerAdapter = new BannerPagerAdapter(mBannerList);
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager pager, int position) {
                if (mActivity.isDealTempAccount()) {
                    return;
                }
                UmengClickEventHelper.onBannerClickEvent(mActivity);
                Banner banner = mBannerList.get(position);
                CommonBannerAdapter.dealBannerJump(mActivity, banner);
            }
        });

        mAllBigChiefList = new ArrayList<>();
        //热门大酋长
        mBigChiefGv = (NestedGridView) findViewById(R.id.gv_big_chief);
        mBigChiefList = new ArrayList<>();
        mBigChiefAdapter = new BigChiefGridAdapter(mBigChiefList);
        mBigChiefGv.setAdapter(mBigChiefAdapter);

        mBigChiefGv.post(new Runnable() {
            @Override
            public void run() {
                int width = Device.getWidth() - DimenUtils.dp2px(24) - DimenUtils.dp2px(18f) * 3;
                int height = width / 4 + DimenUtils.dp2px(38);
                mBigChiefAdapter.setItemHeight(height);
            }
        });

        mBigChiefGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActivity.isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(getContext(),UmengClickEventHelper.SYRMDQZTJ);
                Intent intent = new Intent(mActivity, TribeMainAct.class);
                intent.putExtra("userId", mBigChiefList.get(position).getUserview().getUserid());
                mActivity.startActivity(intent);
            }
        });
        post(new Runnable() {
            @Override
            public void run() {
                mBannerView.getLayoutParams().height = (Device.getWidth() - DimenUtils.dp2px(80)) / 2;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_paihangbang:
                if (mActivity.isDealTempAccount()) {
                    return;
                }
                MobclickAgent.onEvent(getContext(),UmengClickEventHelper.SYMZPHB);
                mActivity.startActivity(new Intent(getContext(), DongtaiRankAct.class));
                break;
            case R.id.btn_integral://积分商场
                MobclickAgent.onEvent(getContext(),UmengClickEventHelper.SYDHSC);
                mActivity.startActivity(new Intent(getContext(), MallIntegralAct.class));
                break;
            case R.id.btn_mall:
                MobclickAgent.onEvent(getContext(),UmengClickEventHelper.SYSTSC);
                Intent it = new Intent(getContext(), MallAct.class);
                SharedMallType.putType(getContext(), SharedMallType.typeSourceDefault);
                mActivity.startActivity(it);
                break;
            case R.id.tv_change:
//                changeBigChiefGrid();
                MobclickAgent.onEvent(getContext(),UmengClickEventHelper.SYRMDQZHYH);
                if (changeListener != null) {
                    changeListener.change();
                }
                break;
        }
    }

    public void setBannerView(ArrayList<Banner> bannerList) {
        mBannerList.clear();
        mBannerList.addAll(bannerList);
        mBannerView.notifyDataSetChanged();
    }

    public void setBigChiefGridView(ArrayList<Headmen> menList) {
        mChangeTv.setEnabled(true);
        mBigChiefList.clear();
        mBigChiefList.addAll(menList.subList(0, Math.min(BIG_CHIEF_COUNT, menList.size())));
        mBigChiefAdapter.notifyDataSetChanged();
    }

//    public void setBigChiefGridView(ArrayList<Headmen> menList) {
//        mAllBigChiefList.clear();
//        mAllBigChiefList.addAll(menList);
//        mChangeTv.setEnabled(true);
//        mStart = 0;
//        mEnd = Math.min(BIG_CHIEF_COUNT, mAllBigChiefList.size());
//        mBigChiefList.clear();
//        mBigChiefList.addAll(mAllBigChiefList.subList(mStart, mEnd));
//        mBigChiefAdapter.notifyDataSetChanged();
//    }

    //    private void changeBigChiefGrid() {
//        if (mAllBigChiefList.size() <= BIG_CHIEF_COUNT) {
//            return;
//        }
//        //多于8个
//        mBigChiefList.clear();
//        int nextStart = mEnd; //第一次从8开始
//        int nextEnd = Math.min(nextStart + BIG_CHIEF_COUNT, mAllBigChiefList.size());
//        mBigChiefList.addAll(mAllBigChiefList.subList(nextStart, nextEnd));
//        if (nextEnd == mAllBigChiefList.size()) {
//            nextStart = 0;
//            nextEnd = BIG_CHIEF_COUNT - mBigChiefList.size();
//            mBigChiefList.addAll(mAllBigChiefList.subList(nextStart, nextEnd));
//        }
//        mStart = nextStart;
//        mEnd = nextEnd;
//        mBigChiefAdapter.notifyDataSetChanged();
//    }
    private ChangeBigChiefDataListener changeListener;

    public void setChangeBitChiefListener(ChangeBigChiefDataListener changeListener) {
        this.changeListener = changeListener;
    }

    public interface ChangeBigChiefDataListener {
        void change();
    }
}
