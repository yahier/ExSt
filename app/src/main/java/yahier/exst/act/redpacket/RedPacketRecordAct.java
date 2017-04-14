package yahier.exst.act.redpacket;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.utils.StatusBarUtil;

import java.util.ArrayList;

/**
 * 红包领取、发出记录
 * Created by Alan Chueng on 2017/1/3 0003.
 */

public class RedPacketRecordAct extends BaseActivity implements View.OnClickListener{
    private TextView tv_title_receive; //我收到的
    private TextView tv_title_send; //我发出的
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_redpacket_record);
        initView();
    }

    @Override
    protected void setStatusBar(){
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
        //StatusBarUtil.StatusBarLightMode(this);
    }

    public void initView(){
        findViewById(R.id.tv_top_left).setOnClickListener(this);
        tv_title_receive = (TextView) findViewById(R.id.tv_title_receive);
        tv_title_send = (TextView) findViewById(R.id.tv_title_send);
        mViewPager = (ViewPager) findViewById(R.id.vp_redpacket);

        tv_title_receive.setOnClickListener(this);
        tv_title_send.setOnClickListener(this);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new RedPacketReceiveFragment());
        fragmentList.add(new RedPacketSendFragment());

        CommonFragmentPagerAdapter adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(),fragmentList,null);
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    tv_title_receive.setBackgroundResource(R.drawable.bg_top_left_white);
                    tv_title_send.setBackgroundResource(R.drawable.bg_top_right_alpha);
                    tv_title_receive.setTextColor(ContextCompat.getColor(RedPacketRecordAct.this,R.color.theme_red_ff6));
                    tv_title_send.setTextColor(ContextCompat.getColor(RedPacketRecordAct.this,R.color.white));
                }else{
                    tv_title_receive.setBackgroundResource(R.drawable.bg_top_left_alpha);
                    tv_title_send.setBackgroundResource(R.drawable.bg_top_right_white);
                    tv_title_receive.setTextColor(ContextCompat.getColor(RedPacketRecordAct.this,R.color.white));
                    tv_title_send.setTextColor(ContextCompat.getColor(RedPacketRecordAct.this,R.color.theme_red_ff6));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_top_left:
                finish();
                break;
            case R.id.tv_title_receive: //我收到的
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_title_send: //我发出的
                mViewPager.setCurrentItem(1);
                break;
        }
    }
}
