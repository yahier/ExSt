package yahier.exst.act.home.help;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.dialog.SystemTipDialog;
import com.stbl.stbl.model.Industry;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.home.HelpTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;

public class FindSomeoneActivity extends ThemeActivity {

    private EditText mTitleEt;
    private EditText mContentEt;
    private TextView mIndustryTv;
    private TextView mBountyTv;
    private TextView mTitleCountTv;
    private TextView mContentCountTv;

    private ArrayList<Industry> mIndustryList;
    private StringWheelDialog mIndustryDialog;

    private ArrayList<Integer> mGoldAmountList;
    private StringWheelDialog mGoldAmountDialog;

    private Industry mIndustry;
    private int mAmount;

    private boolean mIsDestroy;
    private LoadingDialog mLoadingDialog;
    private SystemTipDialog mTipDialog;

    private long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_someone);

        initView();

        getIndustryList();

        getBountyList();
    }

    private void initView() {
        setLabel(getString(R.string.me_i_want_to_find_people));

        mTitleEt = (EditText) findViewById(R.id.et_title);
        mTitleEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    ToastUtil.showToast(R.string.me_title_cannot_line_break);
                    return true;
                }
                return false;
            }
        });
        mContentEt = (EditText) findViewById(R.id.et_content);
        mIndustryTv = (TextView) findViewById(R.id.tv_industry);
        mBountyTv = (TextView) findViewById(R.id.tv_bounty);

        mTitleCountTv = (TextView) findViewById(R.id.tv_title_count);
        mContentCountTv = (TextView) findViewById(R.id.tv_content_count);

        mLoadingDialog = new LoadingDialog(this);
        mTipDialog = new SystemTipDialog(this);
        mTipDialog.setTitle(getString(R.string.me_tip));
        mTipDialog.setContent(getString(R.string.me_is_give_up_this_edit));
        mTipDialog.setInterface(new SystemTipDialog.ISystemTipDialog() {
            @Override
            public void onConfirm() {
                finish();
            }
        });

        mIndustryList = new ArrayList<>();
        mIndustryDialog = new StringWheelDialog(this);
        mIndustryDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mIndustry = mIndustryList.get(position);
                mIndustryTv.setText(mIndustry.getTitle());
            }

            @Override
            public void onRetry() {
                getIndustryList();
            }
        });

        mGoldAmountList = new ArrayList<>();
        mGoldAmountDialog = new StringWheelDialog(this);
        mGoldAmountDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mAmount = mGoldAmountList.get(position);
                mBountyTv.setText(mAmount + "");
            }

            @Override
            public void onRetry() {
                getBountyList();
            }
        });

        findViewById(R.id.layout_industry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndustryDialog.show();
            }
        });

        findViewById(R.id.layout_bounty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoldAmountDialog.show();
            }
        });

        findViewById(R.id.btn_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastClick()) {
                    LogUtil.logE("fast click");
                    return;
                }
                beforePublish();
            }
        });

        mTitleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTitleCountTv.setText(s.toString().length() + "/25");
            }
        });

        mContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContentCountTv.setText(s.toString().length() + "/150");
            }
        });

        findViewById(R.id.layout_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodUtils.hideInputMethod(mTitleEt);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
    }

    public boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - mLastClickTime < 1000) {
            return true;
        }
        mLastClickTime = time;
        return false;
    }

    private void beforePublish() {
        String title = mTitleEt.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.showToast(R.string.please_input_title);
            return;
        }
        if (title.length() < 4) {
            ToastUtil.showToast(R.string.me_help_title_length_limit_tip);
            return;
        }
        if (title.length() > 25) {
            ToastUtil.showToast(R.string.me_help_title_length_limit_tip);
            return;
        }
        String description = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(description.trim())) {
            ToastUtil.showToast(R.string.me_please_input_desc);
            return;
        }
        if (description.length() > 150) {
            ToastUtil.showToast(R.string.me_help_desc_length_limit_tip);
            return;
        }
        if (mIndustry == null) {
            ToastUtil.showToast(R.string.me_please_choose_industry);
            return;
        }
//        if (mAmount == 0) {
//            ToastUtil.showToast("请设置赏金");
//            return;
//        }
        publish(title, description);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void getIndustryList() {
        HelpTask.getIndustryList().setCallback(this, mGetIndustryListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Industry>> mGetIndustryListCallback = new SimpleTask.Callback<ArrayList<Industry>>() {


        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mIndustryDialog.showRetry();
        }

        @Override
        public void onCompleted(ArrayList<Industry> result) {
            if (result.size() == 0) {
                mIndustryDialog.showEmpty();
                return;
            }
            mIndustryList.clear();
            mIndustryList.addAll(result);

            ArrayList<String> stringList = new ArrayList<>();
            for (Industry industry : mIndustryList) {
                stringList.add(industry.getTitle());
            }
            mIndustryDialog.setData(stringList);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void getBountyList() {
        HelpTask.getBountyList().setCallback(this, mGetBountyListCallback).start();
    }

    private SimpleTask.Callback<ArrayList<Integer>> mGetBountyListCallback = new SimpleTask.Callback<ArrayList<Integer>>() {


        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mGoldAmountDialog.showRetry();
        }

        @Override
        public void onCompleted(ArrayList<Integer> result) {
            if (result.size() == 0) {
                mGoldAmountDialog.showEmpty();
                return;
            }
            mGoldAmountList.clear();
            mGoldAmountList.addAll(result);

            ArrayList<String> stringList = new ArrayList<>();
            for (Integer i : mGoldAmountList) {
                stringList.add(i + "");
            }
            mGoldAmountDialog.setData(stringList);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


    private void publish(final String title, final String description) {
//        Payment.getPassword(this, mAmount, new PayingPwdDialog.OnInputListener() {
//
//            @Override
//            public void onInputFinished(String pwd) {
                if (!mLoadingDialog.isShowing()) {
                    mLoadingDialog.show();
                }
                HelpTask.publish(title, description, mIndustry.getId(), mAmount, null).setCallback(FindSomeoneActivity.this, mPublishCallback).start();
//            }
//        });
    }

    private SimpleTask.Callback<Long> mPublishCallback = new SimpleTask.Callback<Long>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Long result) {
            mLoadingDialog.dismiss();
            long issueid = result;
            ToastUtil.showToast(R.string.me_publish_success);
            Intent intent = new Intent(ACTION.HELP_PUBLISH_SUCCESS);
            LocalBroadcastHelper.getInstance().send(intent);
            finish();
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mTitleEt.getText().toString())) {
            mTipDialog.show();
            return;
        }
        if (!TextUtils.isEmpty(mContentEt.getText().toString())) {
            mTipDialog.show();
            return;
        }
        super.onBackPressed();
    }


}
