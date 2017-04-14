package yahier.exst.act.ad;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.KEY;

/**
 * Created by Administrator on 2016/10/14.
 */

public class PublishShoppingLinkActivity extends ThemeActivity {

    public static final int TYPE_ADD_LINK = 0;
    public static final int TYPE_NICE_LINK = 1;
    public static final int TYPE_CARD = 2;
    public static final int TYPE_GOODS = 3;
    private int mType;

    private AddLinkFragment mAddLinkFragment;
    private NiceLinkFragment mNiceLinkFragment;
    private AddCardFragment mCardFragment;
    private AddGoodsFragment mGoodsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_shopping_link);

        mType = getIntent().getIntExtra(KEY.TYPE, TYPE_ADD_LINK);
        addFragment();
    }

    private void addFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (mType) {
            case TYPE_ADD_LINK:
                setLabel(R.string.me_add_link);
                setTopGone();
                mAddLinkFragment = new AddLinkFragment();
                transaction.add(R.id.layout_container, mAddLinkFragment);
                break;
            case TYPE_NICE_LINK:
                setLabel(R.string.me_nice_link);
                mNiceLinkFragment = new NiceLinkFragment();
                transaction.add(R.id.layout_container, mNiceLinkFragment);
                break;
            case TYPE_CARD:
                setLabel(R.string.me_card);
                mCardFragment = new AddCardFragment();
                transaction.add(R.id.layout_container, mCardFragment);
                break;
            case TYPE_GOODS:
                setLabel(R.string.me_goods);
                mGoodsFragment = new AddGoodsFragment();
                transaction.add(R.id.layout_container, mGoodsFragment);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mAddLinkFragment != null) {
            mAddLinkFragment.saveInputData();
        }
    }

}
