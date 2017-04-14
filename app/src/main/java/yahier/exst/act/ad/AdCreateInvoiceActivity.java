package yahier.exst.act.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.AreaItem;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.ProvinceItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelCity2;
import com.stbl.stbl.util.database.DataCacheDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 补开发票
 * Created by Administrator on 2016/9/30 0030.
 */

public class AdCreateInvoiceActivity extends ThemeActivity implements View.OnClickListener,FinalHttpCallback, WheelCity2.OnCityWheelListener {
    private LinearLayout llPersonal; //选择个人
    private LinearLayout llUnit; //选择单位
    private ImageView ivPersonal;//个人
    private ImageView ivUnit; //单位
    private EditText etUnitName; //单位名称
    private EditText etName; //姓名
    private EditText etPhone; //联系电话
    private TextView tvSelectCountry;//选择国家
    private TextView tvSelectPC;//选择省份/城市/区县
    private EditText etAddressDetail; //详细地址
    private TextView tvSubmit;//选择省份/城市/区县

    private int invoiceType = INVOICE_TYPE_PERSONAL; //发票类型，0-没有，1-个人，2-公司
    private static final int INVOICE_TYPE_PERSONAL = 1; //1-个人
    private static final int INVOICE_TYPE_UNIT = 2; //2-公司

    private String orderno;//订单号
    private boolean mIsDestroy;
    private StringWheelDialog mWheelDialog;
    private String countryId = "9001";//默认中国
    private String areacode = "86";//中国
    private CountryPhoneCode mCurrentCode;
    private ArrayList<CountryPhoneCode> mCountryCodeList; //国家码
    private ArrayList<String> mStringList; //国家列表
    private WheelCity2 wheelCity = new WheelCity2();//省市区选择弹框
    private List<ProvinceItem> listProvinceCity;//省市区信息
    private String provinceid, cityid, districtid;//省市区id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_create_invoice_layout);

        setLabel(getString(R.string.ad_invoice_create));
        orderno = getIntent().getStringExtra("orderno");
        llPersonal = (LinearLayout) findViewById(R.id.ll_personal);
        llUnit = (LinearLayout) findViewById(R.id.ll_unit);
        ivPersonal = (ImageView) findViewById(R.id.iv_personal);
        ivUnit = (ImageView) findViewById(R.id.iv_unit);
        etUnitName = (EditText) findViewById(R.id.et_unit_name);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        tvSelectCountry = (TextView) findViewById(R.id.tv_select_country);
        tvSelectPC = (TextView) findViewById(R.id.tv_select_p_c);
        etAddressDetail = (EditText) findViewById(R.id.et_address_detail);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);

        llPersonal.setOnClickListener(this);
        llUnit.setOnClickListener(this);
        tvSelectCountry.setOnClickListener(this);
        tvSelectPC.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);

        mWheelDialog = new StringWheelDialog(this);
        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mCurrentCode = mCountryCodeList.get(position);
                countryId = String.valueOf(mCurrentCode.getId());
                areacode = mCurrentCode.getPrefix();
                tvSelectCountry.setText(mCurrentCode.getCountry() + mCurrentCode.getPrefix());
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });
        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();
        wheelCity.setOnCityWheelListener(this);
        getCountryCodeList();
        getProvinceCityList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_personal:
                invoiceType = INVOICE_TYPE_PERSONAL;
                ivPersonal.setImageResource(R.drawable.button_ad_sel);
                ivUnit.setImageResource(R.drawable.button_ad_nor);
                break;
            case R.id.ll_unit:
                invoiceType = INVOICE_TYPE_UNIT;
                ivPersonal.setImageResource(R.drawable.button_ad_nor);
                ivUnit.setImageResource(R.drawable.button_ad_sel);
                break;
            case R.id.tv_select_country:
                //国家不支持选择，先保留代码
//                mWheelDialog.show();
                break;
            case R.id.tv_select_p_c:
                hideInputSoft();
                if (listProvinceCity == null || !countryId.equals("9001"))
                    return;
                wheelCity.show(this, listProvinceCity);
                break;
            case R.id.tv_submit:
                checkInvoiceInfo();
                break;
        }
    }

    private void checkInvoiceInfo(){
        if (invoiceType == INVOICE_TYPE_UNIT) {
            if (TextUtils.isEmpty(etUnitName.getText().toString())) {
                ToastUtil.showToast(getString(R.string.ad_input_unit_name));
                return;
            }
        }
        if (TextUtils.isEmpty(etName.getText().toString())){
            ToastUtil.showToast(getString(R.string.ad_input_contact_name));
            return;
        }
        if (TextUtils.isEmpty(etAddressDetail.getText().toString())){
            ToastUtil.showToast(getString(R.string.ad_input_addr_detail));
            return;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString())){
            ToastUtil.showToast(getString(R.string.ad_input_phoneno));
            return;
        }
        if (TextUtils.isEmpty(provinceid)) {
            ToastUtil.showToast(getString(R.string.ad_select_province));
            return;
        }
        submitInvoiceInfo();
    }

    private void submitInvoiceInfo(){
        Params params = new Params();
        params.put("orderno",orderno);
        params.put("orderinvoicetitle",etUnitName.getText().toString());
        params.put("orderinvoicecontent",getString(R.string.ad_service3));
        params.put("orderinvoicetype",invoiceType);
        params.put("contactname",etName.getText().toString());
        params.put("provinceid",provinceid);
        params.put("cityid",cityid);
        params.put("districtid",districtid);
        params.put("contactaddr",etAddressDetail.getText().toString());
        params.put("areacode",areacode);
        params.put("contactphone",etPhone.getText().toString());
        new HttpEntity(this).commonPostData(Method.adsysOrderInvoiceCreate,params,this);
    }

    @Override
    public void onChoosedOk(String value, String code1, String code2, String code3) {
        provinceid = String.valueOf(code1);
        cityid = code2;
        districtid = String.valueOf(code3);
        tvSelectPC.setText(getString(R.string.mall_choise_area) + value);
    }

    // 选择好了城市，获取区域列表
    @Override
    public void onCityChoosed(String cityCode) {
        Params params = new Params();
        params.put("code", cityCode);
        new HttpEntity(this).commonPostData(Method.getAreaData, params, this);
    }

    private void hideInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    void getProvinceCityList() {
        DataCacheDB cacheDB = new DataCacheDB(this);
        String obj = cacheDB.getCityTreeCacheJson();
        Params params = new Params();
        if (obj != null) {
            listProvinceCity = JSONHelper.getList(obj, ProvinceItem.class);
            params.put("updatetime", SharedCommon.getCityTreeUpdateTime());//上次的更新时间
        }else{
            params.put("updatetime", 0);//上次的更新时间
        }
        new HttpEntity(this).commonPostData(Method.getProvinceCityData, params, this);
    }
    /**
     * 获取国家区号
     */
    private void getCountryCodeList() {
        LoginTask.getCountryCodeList().setCallback(this, mGetCountryCodeCallback).start();
    }

    private SimpleTask.Callback<HashMap<String, Object>> mGetCountryCodeCallback = new SimpleTask.Callback<HashMap<String, Object>>() {
        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(HashMap<String, Object> result) {
            if (result.containsKey("CountryCodeList")) {
                ArrayList<CountryPhoneCode> codeList = (ArrayList<CountryPhoneCode>) result.get("CountryCodeList");
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

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item == null) return;
        if (item.getIssuccess() != BaseItem.successTag){
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null){
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;
        }
        String valueObj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.getProvinceCityData:
                List<ProvinceItem> listProvinceCity2  = JSONHelper.getList(valueObj, ProvinceItem.class);
                //缓存到数据库并记录下更新时间
                if (listProvinceCity2 != null) {
                    listProvinceCity = listProvinceCity2;
                    SharedCommon.putCityTreeUpdateTime();
                    DataCacheDB cacheDB = new DataCacheDB(this);
                    cacheDB.saveCityTreeCacheJson(valueObj);
                }
                break;
            case Method.getAreaData:
                List<AreaItem> listArea = JSONHelper.getList(valueObj, AreaItem.class);
                if (listArea != null)
                    wheelCity.showArea(listArea);
                break;
            case Method.adsysOrderInvoiceCreate:
                setResult(Activity.RESULT_OK);
                ToastUtil.showToast(getString(R.string.ad_submit_success));
                finish();
                break;
        }
    }
}
