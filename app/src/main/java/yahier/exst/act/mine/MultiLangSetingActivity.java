package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;

/**
 * Created by Administrator on 2016/8/18.
 */

public class MultiLangSetingActivity extends ThemeActivity {

    private ImageView mFollowSystemIv;
    private ImageView mSimpleChineseIv;
    private ImageView mEnglishIv;

    private int mPosition;
    private int mOldPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_lang_setting);

        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_multi_lang_setting));
        setRightText(getString(R.string.me_save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition != mOldPosition) {
                    SharedPrefUtils.putToPublicFile(KEY.MULTI_LANG_SETTING, mPosition);
                    LocalBroadcastHelper.getInstance().send(new Intent(ACTION.SWITCH_LANG));
                }
                ToastUtil.showToast(R.string.me_save_success);
                finish();
            }
        });

        mFollowSystemIv = (ImageView) findViewById(R.id.iv_follow_system);
        mSimpleChineseIv = (ImageView) findViewById(R.id.iv_simple_chinese);
        mEnglishIv = (ImageView) findViewById(R.id.iv_english);

        findViewById(R.id.layout_follow_system).setOnClickListener(mClickListener);
        findViewById(R.id.layout_simple_chinese).setOnClickListener(mClickListener);
        findViewById(R.id.layout_english).setOnClickListener(mClickListener);

        mOldPosition = (int) SharedPrefUtils.getFromPublicFile(KEY.MULTI_LANG_SETTING, 0);
        mPosition = mOldPosition;
        selectLang(mPosition);
    }

    private void selectLang(int position) {
        mFollowSystemIv.setVisibility(View.INVISIBLE);
        mSimpleChineseIv.setVisibility(View.INVISIBLE);
        mEnglishIv.setVisibility(View.INVISIBLE);
        switch (position) {
            case 0:
                mFollowSystemIv.setVisibility(View.VISIBLE);
                break;
            case 1:
                mSimpleChineseIv.setVisibility(View.VISIBLE);
                break;
            case 2:
                mEnglishIv.setVisibility(View.VISIBLE);
                break;
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_follow_system:
                    mPosition = 0;
                    break;
                case R.id.layout_simple_chinese:
                    mPosition = 1;
                    break;
                case R.id.layout_english:
                    mPosition = 2;
                    break;
            }
            selectLang(mPosition);
        }
    };

    @Override
    protected void receiveSwitchLang() {
        //因为要结束页面，所以这个页面不切换语言
    }
}
