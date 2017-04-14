package yahier.exst.act.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.ViewPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.util.AppUtils;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/5/4.
 */
public class IntroduceActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ArrayList<View> mViewList;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();

    }

    /**
     * 隐藏底部虚拟按键。比如华为手机
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initView() {
        mViewList = new ArrayList<>();
        mViewPager = (ViewPager) findViewById(R.id.vp);
        CirclePageIndicator pageIndicator = (CirclePageIndicator) findViewById(R.id.vpi);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        int[] resIds = new int[]{R.drawable.bg_intro_1, R.drawable.bg_intro_2, R.drawable.bg_intro_3};
        for (int i = 0; i < resIds.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageUtils.loadImage(resIds[i], iv);
            mViewList.add(iv);
        }
        View v = layoutInflater.inflate(R.layout.intro_last_pager, null);
        ImageView iv = (ImageView) v.findViewById(R.id.iv);
        iv.setImageBitmap(ImageUtils.decodeBitmapSafety(getResources(), R.drawable.bg_intro_4, null));
        v.findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealDirect();
            }
        });
        mViewList.add(v);
        mAdapter = new ViewPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);
        pageIndicator.setViewPager(mViewPager);
    }

    //处理跳转
    private void dealDirect() {
        AppUtils.setFinishIntroduce();
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }
}
