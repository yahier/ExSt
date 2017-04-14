package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.mine.WalletTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;

public class ExchangeBeanActivity extends ThemeActivity {

    private TextView mExchangeCategoryTv;
    private TextView mPayCategoryTv;

    private ImageView mBeanIv;
    private TextView mBeanHintTv;
    private TextView mBeanAmountTv;

    private TextView mExchangeTipTv;
    private TextView mPayAmountTv;

    private ArrayList<String> mExchangeCategoryList;
    private StringWheelDialog mExchangeCategoryDialog;

    private ArrayList<String> mGoldPayCategoryList;
    private ArrayList<String> mGreenPayCategoryList;
    private StringWheelDialog mPayCategoryDialog;

    private ArrayList<String> mGreenAmountList;
    private ArrayList<String> mGoldAmountList;
    private StringWheelDialog mBeanAmountDialog;

    private int mExchangeCategory;
    private int mPayCategory;
    private int mAmount;
    private int mPayType = 3; //账户余额兑换金豆

    private boolean mIsDestroy;
    private LoadingDialog mLoadingDialog;

    private float amounts;
    private int jindou;
    private float jindou2yue;
    private float lvdou2yue;
    private float lvdou2jindou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_bean);

        initView();
    }

    private void initView() {
        setLabel("兑换师徒豆");
        mExchangeCategoryTv = (TextView) findViewById(R.id.tv_exchange_category);
        mPayCategoryTv = (TextView) findViewById(R.id.tv_pay_category);

        mBeanIv = (ImageView) findViewById(R.id.iv_bean);
        mBeanHintTv = (TextView) findViewById(R.id.tv_bean_hint);
        mBeanAmountTv = (TextView) findViewById(R.id.tv_bean_amount);
        mExchangeTipTv = (TextView) findViewById(R.id.tv_exchange_tip);
        mPayAmountTv = (TextView) findViewById(R.id.tv_pay_amount);

        amounts = (float) SharedPrefUtils.getFromUserFile(KEY.amounts, 0f);
        jindou = (int) SharedPrefUtils.getFromUserFile(KEY.jindou, 0);
        jindou2yue = (float) SharedPrefUtils.getFromPublicFile(KEY.jindou2yue, 0f);
        lvdou2yue = (float) SharedPrefUtils.getFromPublicFile(KEY.lvdou2yue, 0f);
        lvdou2jindou = (float) SharedPrefUtils.getFromPublicFile(KEY.lvdou2jindou, 0f);

        int balance2Gold = (int) (amounts / jindou2yue);
        mExchangeTipTv.setText("钱包余额最多可兑换:" + balance2Gold + "个金豆");

        mLoadingDialog = new LoadingDialog(this);

        mExchangeCategoryList = new ArrayList<>();
        mExchangeCategoryList.add("兑换金豆");
        mExchangeCategoryList.add("兑换绿豆");
        mExchangeCategoryDialog = new StringWheelDialog(this);
        mExchangeCategoryDialog.setData(mExchangeCategoryList);

        mExchangeCategoryDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mExchangeCategory = position;
                setState();
            }

            @Override
            public void onRetry() {

            }
        });

        mGoldPayCategoryList = new ArrayList<>();
        mGoldPayCategoryList.add("余额支付");

        mGreenPayCategoryList = new ArrayList<>();
        mGreenPayCategoryList.add("余额支付");
        mGreenPayCategoryList.add("金豆支付");
        mPayCategoryDialog = new StringWheelDialog(this);
        mPayCategoryDialog.setData(mGoldPayCategoryList);
        mPayCategoryDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mPayCategory = position;
                setState();
            }

            @Override
            public void onRetry() {

            }
        });

        mGreenAmountList = new ArrayList<>();
        mGoldAmountList = new ArrayList<>();
        mBeanAmountDialog = new StringWheelDialog(this);
        mBeanAmountDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                if (mExchangeCategory == 0) {
                    mAmount = Integer.parseInt(mGoldAmountList.get(position));
                } else if (mExchangeCategory == 1) {
                    mAmount = Integer.parseInt(mGreenAmountList.get(position));
                }
                mBeanAmountTv.setText(mAmount + "个");
                mBeanHintTv.setVisibility(View.GONE);
                setState();
            }

            @Override
            public void onRetry() {

            }
        });

        findViewById(R.id.layout_exchange_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExchangeCategoryDialog.show();
            }
        });

        findViewById(R.id.layout_pay_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayCategoryDialog.show();
            }
        });

        findViewById(R.id.layout_exchange_amount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeanAmountDialog.show();
            }
        });

        findViewById(R.id.btn_exchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeExchange();
            }
        });

    }

    private void setState() {
        if (mExchangeCategory == 0) { //兑换金豆
            mPayCategory = 0;        //只有余额支付
            mPayType = 3;
            mExchangeCategoryTv.setText("兑换金豆");
            mPayCategoryDialog.setData(mGoldPayCategoryList);
            mBeanAmountDialog.setData(mGoldAmountList);
            if (mGoldAmountList.size() > 0) {
                if (!mGoldAmountList.contains(mAmount + "")) {
                    mAmount = Integer.parseInt(mGoldAmountList.get(0));
                    mBeanAmountTv.setText(mAmount + "个");
                    mBeanHintTv.setVisibility(View.GONE);
                }
            }
            mPayCategoryTv.setText("余额支付");
            mBeanIv.setImageResource(R.drawable.ic_gold_bean);
            int balance2Gold = (int) (amounts / jindou2yue);
            mExchangeTipTv.setText("钱包余额最多可兑换:" + balance2Gold + "个金豆");
            if (mAmount != 0) {
                float payAmount = jindou2yue * mAmount;
                String payAmountText = StringUtil.get2ScaleString(payAmount);
                payAmountText = payAmountText.equals("") ? String.valueOf(payAmount) : payAmountText;
                String html = "应付：<font color='#F25B62'>¥ " + payAmountText + "</font>";
                mPayAmountTv.setText(Html.fromHtml(html));
            }
        } else {                      //兑换绿豆
            mExchangeCategoryTv.setText("兑换绿豆");
            mPayCategoryDialog.setData(mGreenPayCategoryList);
            mBeanAmountDialog.setData(mGreenAmountList);
            if (mGreenAmountList.size() > 0) {
                if (!mGreenAmountList.contains(mAmount + "")) {
                    mAmount = Integer.parseInt(mGreenAmountList.get(0));
                    mBeanAmountTv.setText(mAmount + "个");
                    mBeanHintTv.setVisibility(View.GONE);
                }
            }
            mBeanIv.setImageResource(R.drawable.ic_green_bean);
            if (mPayCategory == 0) {  //余额支付
                mPayType = 3;
                mPayCategoryTv.setText("余额支付");
                int balance2Green = (int) (amounts / lvdou2yue);
                mExchangeTipTv.setText("钱包余额最多可兑换:" + balance2Green + "个绿豆");
                if (mAmount != 0) {
                    float payAmount = lvdou2yue * mAmount;
                    String payAmountText = StringUtil.get2ScaleString(payAmount);
                    payAmountText = payAmountText.equals("") ? String.valueOf(payAmount) : payAmountText;
                    String html = "应付：<font color='#F25B62'>¥ " + payAmountText + "</font>";
                    mPayAmountTv.setText(Html.fromHtml(html));
                }
            } else {                  //金豆支付
                mPayType = 4;
                mPayCategoryTv.setText("金豆支付");
                int gold2Green = (int) (jindou / lvdou2jindou);
                mExchangeTipTv.setText("拥有的金豆最多可兑换:" + gold2Green + "个绿豆");
                if (mAmount != 0) {
                    int payAmount = (int) (lvdou2jindou * mAmount);
                    String html = "应付：<font color='#F25B62'>" + payAmount + "个金豆</font>";
                    mPayAmountTv.setText(Html.fromHtml(html));
                }
            }
        }
    }

    private void beforeExchange() {
        if (mAmount == 0) {
            ToastUtil.showToast("请选择兑换数量");
            return;
        }
        if (mExchangeCategory == 0) {
            exchangeGoldBean();
        } else {
            exchangeGreenBean();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;

    }

    private void exchangeGoldBean() {
        Payment.getPassword(this,0, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                if (!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }
                WalletTask.exchangeGoldBean(mPayType, mAmount, pwd).setCallback(ExchangeBeanActivity.this, mExchangeBeanCallback).start();
            }
        });
    }

    private SimpleTask.Callback<Integer> mExchangeBeanCallback = new SimpleTask.Callback<Integer>() {
        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast("兑换成功");
            LocalBroadcastHelper.getInstance().send(new Intent(ACTION.GET_WALLET_BALANCE));
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void exchangeGreenBean() {
        Payment.getPassword(this, 0,new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                if (!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }
                WalletTask.exchangeGreenBean(mPayType, mAmount, pwd).setCallback(ExchangeBeanActivity.this, mExchangeBeanCallback).start();
            }
        });
    }
}
