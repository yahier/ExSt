package yahier.exst.act.ad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.WaitingDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/2.
 * 申请广告商业合作页面
 */

public class ApplyAdCooperateAct extends ThemeActivity implements FinalHttpCallback,View.OnClickListener {
    EditText inputName, inputPhone, inputDes;
    TextView tvTip, tvCommit,inputArea;
    ImageView imgAd;
    Ad ad;
    boolean isAbleApply = false;

    //国家 区号选择
    private boolean mIsDestroy;
    private ArrayList<CountryPhoneCode> mCountryCodeList;
    private ArrayList<String> mStringList;
    private StringWheelDialog mWheelDialog;
    private CountryPhoneCode mCurrentCode;
    private String checkResult; //校验申请接口数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_apply_cooperate_act);
        setLabel("商务合作");
        checkResult = getIntent().getStringExtra("checkJson");
        ad = (Ad) getIntent().getSerializableExtra("ad");
        if (ad == null) {
            ToastUtil.showToast("需要传递ad进来");
            return;
        }
        initViews();
        if (checkResult != null){
            parseApplyCheck(checkResult);
        }else {
            getLeftApplyTimes();
        }
        getCountryCodeList();
    }


    void checkIsInputOk() {
        if (isAbleApply&&!inputName.getText().toString().equals("") && !inputPhone.getText().toString().equals("") && !inputDes.getText().toString().equals("")&&!inputArea.getText().toString().equals("")) {
            tvCommit.setEnabled(true);
        } else {
            tvCommit.setEnabled(false);
        }
    }


    void initViews() {
        mWheelDialog = new StringWheelDialog(this);
        imgAd = (ImageView)findViewById(R.id.imgAd);
        inputArea = (TextView)findViewById(R.id.inputArea);
        inputArea.setOnClickListener(this);
        inputName = (EditText) findViewById(R.id.inputName);
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        inputDes = (EditText) findViewById(R.id.inputDes);

        tvTip = (TextView) findViewById(R.id.tvTip);
        tvCommit = (TextView) findViewById(R.id.tvCommit);

        inputName.addTextChangedListener(new TextsListener());
        inputPhone.addTextChangedListener(new TextsListener());
        inputDes.addTextChangedListener(new TextsListener());
        inputArea.addTextChangedListener(new TextsListener());
        tvCommit.setOnClickListener(this);

        ImageUtils.loadBitmap(ad.adimglarurl, imgAd, new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                ToastUtil.showToast(R.string.failed_load_img);
                imgAd.setImageResource(R.drawable.img_banner_default);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                int containerWidth = Device.getWidth() - Util.dip2px(ApplyAdCooperateAct.this, 12) * 2;
                LogUtil.logE("ad", "containerWidth:" + containerWidth + "  url:" + ad.adimgminurl);
                int bitmapHeight = containerWidth * Config.uploadAdHeightScale / Config.uploadAdWidthScale;
                imgAd.setLayoutParams(new LinearLayout.LayoutParams(containerWidth, bitmapHeight));
                return false;
            }
        });

        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                if (mCountryCodeList.size() == 0) return;
                mCurrentCode = mCountryCodeList.get(position);
                //SharedUser.putCountryCode(mCurrentCode.getCountry(), mCurrentCode.getPrefix());
                inputArea.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });
        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();
        //初始化中国
        mCurrentCode = new CountryPhoneCode();
        mCurrentCode.setCountry("中国");
        mCurrentCode.setPrefix("86");
        inputArea.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.inputArea:
                mWheelDialog.show();
                break;
            case R.id.tvCommit:
                doCommit();
                break;
        }
    }


    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            checkIsInputOk();
        }
    }

    void doCommit() {
        WaitingDialog.show(this, R.string.waiting);
        Params params = new Params();
        params.put("adid", ad.adid);//Statuses.requestCount
        params.put("name", inputName.getText().toString());
        params.put("areacode", mCurrentCode.getPrefix());
        params.put("phone", inputPhone.getText().toString());
        params.put("content", inputDes.getText().toString());
        new HttpEntity(mActivity).commonPostData(Method.adApplyBusinessCooperate, params, this);
    }

    void getLeftApplyTimes() {
        WaitingDialog.show(this, R.string.waiting);
        Params params = new Params();
        params.put("adid", ad.adid);//Statuses.requestCount
        new HttpEntity(mActivity).commonPostData(Method.adBusinessApplyLeftTimes, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {

            switch(methodName){
                case Method.adBusinessApplyLeftTimes:
                    isAbleApply = false;
                    if (item.getIssuccess() == BaseItem.errorNoTaostTag) {
                        break;
                    }

                    TipsDialog.popup(this, item.getErr().getMsg(), "确定", new TipsDialog.OnTipsListener() {
                        @Override
                        public void onConfirm() {
                            finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    break;
                default:
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                        ToastUtil.showToast(mActivity, item.getErr().getMsg());
                    }
                    break;
            }
            return;
        }
       // String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.adApplyBusinessCooperate:
                //ToastUtil.showToast("提交成功，请耐心等待");
                Intent intent = new Intent(this, CommonResultAct.class);
                intent.putExtra("type", CommonResultAct.typeApplyAdBusinessCoop);
                startActivity(intent);
                finish();
                break;
            case Method.adBusinessApplyLeftTimes:
               parseApplyCheck(result);
                break;
        }
    }
    private void parseApplyCheck(String result){
        if (result == null) return;
        isAbleApply = true;
        JSONObject json = JSONObject.parseObject(result);
        String leftTimes = json.getString("result");
        String value = String.format(this.getString(R.string.ad_business_apply_left_times), leftTimes);
        tvTip.setText(Html.fromHtml(value));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        WaitingDialog.dismiss();
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }
    }

    private void getCountryCodeList() {
        LoginTask.getCountryCodeList().setCallback(this, mGetCountryCodeCallback).start();
    }

    private SimpleTask.Callback<HashMap<String, Object>> mGetCountryCodeCallback = new SimpleTask.Callback<HashMap<String, Object>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mWheelDialog.showRetry();
        }

        @Override
        public void onCompleted(HashMap<String, Object> result) {
            if (result.containsKey("CountryCodeList")) {
                ArrayList<CountryPhoneCode> codeList = (ArrayList<CountryPhoneCode>) result.get("CountryCodeList");
                if (codeList.size() == 0) {
                    mWheelDialog.showEmpty();
                    return;
                }
                mCountryCodeList.clear();
                mCountryCodeList.addAll(codeList);
            }

            if (result.containsKey("StringList")) {
                ArrayList<String> stringList = (ArrayList<String>) result.get("StringList");
                mStringList.clear();
                mStringList.addAll(stringList);
                mWheelDialog.setData(mStringList);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };
}
