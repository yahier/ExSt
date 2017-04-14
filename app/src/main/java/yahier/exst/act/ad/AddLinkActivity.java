package yahier.exst.act.ad;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.SelectLinkActivity;
import com.stbl.stbl.adapter.OneTypeAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.util.KEY;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/26.
 */

public class AddLinkActivity extends BaseActivity {

    private static final int REQUEST_CODE_SELECT_LINK = 10001;
    public static final int TYPE_LINK = 0;
    public static final int TYPE_CARD = 1;
    public static final int TYPE_GOODS = 2;

    private TextView mTypeTv;
    private TextView mSelectExistLinkTv;

    private ListPopupWindow mPopupWindow;
    private ArrayList<Item> mAllTypeList;
    private ArrayList<Item> mTypeList;
    private Item mCurrentType;

    private AddLinkFragment mLinkFragment;
    private AddCardFragment mCardFragment;
    private AddGoodsFragment mGoodsFragment;

    private View mCoverView;

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_link);

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCoverView = findViewById(R.id.v_cover);
        mTypeTv = (TextView) findViewById(R.id.tv_link_type);
        mTypeTv.setSelected(false);
        mSelectExistLinkTv = (TextView) findViewById(R.id.tv_select_exist_link);
        mSelectExistLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SelectLinkActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_LINK);
            }
        });
        mTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupWindow();
            }
        });
        mAllTypeList = new ArrayList<>();
        mAllTypeList.add(new Item(TYPE_LINK, getString(R.string.me_link)));
        mAllTypeList.add(new Item(TYPE_CARD, getString(R.string.me_card)));
        mAllTypeList.add(new Item(TYPE_GOODS, getString(R.string.me_goods)));

        mTypeList = new ArrayList<>();
        mPopupWindow = new ListPopupWindow(mActivity);
        mAdapter = new MyAdapter(mTypeList);
        mPopupWindow.setAdapter(mAdapter);
        mPopupWindow.setAnchorView(findViewById(R.id.tv_cancel));
        mPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        mPopupWindow.setModal(true);
        mPopupWindow.setListSelector(null);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mTypeTv.setSelected(false);
                animDismiss();
            }
        });
        mPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindow.dismiss();
                Item item = mTypeList.get(position);
                setCurrentType(item);
            }
        });
        setCurrentType(mAllTypeList.get(0));
    }

    private void showListPopupWindow() {
        mTypeTv.setSelected(true);
        animShow();
        mPopupWindow.show();
    }

    private void animShow() {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mCoverView, "backgroundColor", 0x00000000, 0x7f000000);
        colorAnim.setDuration(50);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }

    private void animDismiss() {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mCoverView, "backgroundColor", 0x7f000000, 0x00000000);
        colorAnim.setDuration(50);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }

    private void setCurrentType(Item item) {
        mCurrentType = item;
        mTypeList.clear();
        mTypeList.addAll(mAllTypeList);
        mTypeList.remove(mCurrentType);
        mAdapter.notifyDataSetChanged();
        mTypeTv.setText(mCurrentType.type);
        selectTab(mCurrentType.index);
        mSelectExistLinkTv.setVisibility(item.index == 0 ? View.VISIBLE : View.GONE);
    }

    private void selectTab(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (position) {
            case TYPE_LINK:
                if (mLinkFragment == null) {
                    mLinkFragment = new AddLinkFragment();
                    transaction.add(R.id.layout_container, mLinkFragment);
                } else {
                    transaction.show(mLinkFragment);
                }
                break;
            case TYPE_CARD:
                if (mCardFragment == null) {
                    mCardFragment = new AddCardFragment();
                    transaction.add(R.id.layout_container, mCardFragment);
                } else {
                    transaction.show(mCardFragment);
                }
                break;
            case TYPE_GOODS:
                if (mGoodsFragment == null) {
                    mGoodsFragment = new AddGoodsFragment();
                    transaction.add(R.id.layout_container, mGoodsFragment);
                } else {
                    transaction.show(mGoodsFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (mLinkFragment != null) {
            transaction.hide(mLinkFragment);
        }
        if (mCardFragment != null) {
            transaction.hide(mCardFragment);
        }
        if (mGoodsFragment != null) {
            transaction.hide(mGoodsFragment);
        }
    }


    private static class Item {
        int index;
        String type;

        public Item(int index, String type) {
            this.index = index;
            this.type = type;
        }
    }

    private class MyAdapter extends OneTypeAdapter<Item> {

        public MyAdapter(ArrayList<Item> list) {
            super(list);
        }

        @Override
        protected int getLayoutId() {
            return R.layout.item_add_link;
        }

        @Override
        protected ViewHolder getViewHolder() {
            return new ViewHolder() {

                TextView mTv;

                @Override
                public void init(View v) {
                    mTv = (TextView) v;
                }

                @Override
                public void bind(View v, int position) {
                    mTv.setText(getList().get(position).type);
                }
            };
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SELECT_LINK:
                LinkBean link = (LinkBean) data.getSerializableExtra(KEY.LINK_BEAN);
                if (link != null) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY.TYPE, TYPE_LINK);
                    intent.putExtra(KEY.LINK_BEAN, link);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }
}
