package yahier.exst.act.home.mall.address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.AreaItem;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.ProvinceItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.Address;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.ui.BaseClass.STBLBaseActivity;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.NumberUtil;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedCommon;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WheelCity2;
import com.stbl.stbl.util.WheelCity2.OnCityWheelListener;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.utils.StringUtils;

/**
 * 增加和修改收获地址
 *
 * @author lenovo
 */
public class MallAddressEditAct extends STBLBaseActivity implements OnClickListener, OnCityWheelListener {

    private EditText edtName, edtPhone, edtDetails;
    private CheckBox checkIsDefaultAddress;
    private WheelCity2 wheelCity = new WheelCity2();

    private Button btnChangeArea, btnChangeCountry;
    private TextView tvTextCount;
    private List<ProvinceItem> listProvinceCity;
    private String provinceid, cityid, districtid;
    private int type;
    private final int typeAdd = 0;
    private final int typeEdit = 1;
    private int addressid;
    private String countryId = "9001";//默认中国

    private ArrayList<CountryPhoneCode> mCountryCodeList;
    private ArrayList<String> mStringList;
    private StringWheelDialog mWheelDialog;
    private CountryPhoneCode mCurrentCode;
    private boolean mIsDestroy;
    private int mMaxLength = 200;
    private boolean isNull;//是否第一个地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_addr_edit);

        type = getIntent().getIntExtra("type", typeAdd);
        isNull = getIntent().getBooleanExtra("isNull", false);

        edtName = (EditText) findViewById(R.id.inputName);
        edtPhone = (EditText) findViewById(R.id.et_phone);

        btnChangeCountry = (Button) findViewById(R.id.change_country_code_btn);
        btnChangeCountry.setOnClickListener(this);
        btnChangeArea = (Button) findViewById(R.id.change_area_btn);
        btnChangeArea.setOnClickListener(this);

        edtDetails = (EditText) findViewById(R.id.inputDetailsAddress);
        checkIsDefaultAddress = (CheckBox) findViewById(R.id.checkIsDefaultAddress);
        tvTextCount = (TextView) findViewById(R.id.tv_text_count);

        edtDetails.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int editStart;
            private int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = edtDetails.getSelectionStart();
                editEnd = edtDetails.getSelectionEnd();
                if (temp.length() > mMaxLength) {
                    ToastUtil.showToast(MallAddressEditAct.this, getString(R.string.mall_address_can_not_exceed) + mMaxLength + getString(R.string.mall_a_word));
                    try {
                        s.delete(editStart - 1, editEnd);
                    } catch(StackOverflowError e) {
                        e.printStackTrace();
                        System.gc();
                    }
                    int tempSelection = editStart;
                    edtDetails.setText(s);
                    edtDetails.setSelection(tempSelection);
                }
                tvTextCount.setText(s.toString().length() + "/" + mMaxLength + getString(R.string.mall_word));
            }
        });
        getProvinceCityList();

        switch(type) {
            case typeAdd:
                btnChangeCountry.setText(R.string.mall_country_default);
                navigationView.setTitleBar(getString(R.string.mall_create_address));
                break;
            case typeEdit:
                navigationView.setTitleBar(getString(R.string.mall_update_address));
                Address address = (Address) getIntent().getSerializableExtra("item");
                addressid = address.getAddressid();
                edtName.setText(address.getUsername());
                edtPhone.setText(address.getPhone());

                countryId = address.getCountryid();
                btnChangeCountry.setText(getString(R.string.mall_country_area) + address.getCountryname());
                btnChangeArea.setText(getString(R.string.mall_choise_area) + address.getProvincename() + address.getCityname() + address.getDistrictname());
                edtDetails.setText(address.getAddress());
                if (address.getIsdefault() == Address.isdefaultYes) {
                    checkIsDefaultAddress.setChecked(true);
                } else {
                    checkIsDefaultAddress.setChecked(false);
                }
                provinceid = address.getProvinceid();
                cityid = address.getCityid();
                districtid = address.getDistrictid();
                break;
        }

        navigationView.setClickLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        navigationView.setTextClickRight(getString(R.string.mall_done), new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddress();
            }
        });

        mWheelDialog = new StringWheelDialog(this);

        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();

        wheelCity.setOnCityWheelListener(this);

        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mCurrentCode = mCountryCodeList.get(position);
                countryId = String.valueOf(mCurrentCode.getId());
                btnChangeCountry.setText(getString(R.string.mall_country_area) + mCurrentCode.getCountry() + mCurrentCode.getPrefix());
                setVisibChangeArea();
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });

        setVisibChangeArea();
        getCountryCodeList();
    }

    private void setVisibChangeArea() {
        if (!StringUtils.isEmpty(countryId) && countryId.equals("9001")) {
            btnChangeArea.setVisibility(View.VISIBLE);
            findViewById(R.id.controls3).setVisibility(View.VISIBLE);
        } else {
            btnChangeArea.setVisibility(View.GONE);
            findViewById(R.id.controls3).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.change_country_code_btn:
                mWheelDialog.show();
                break;
            case R.id.change_area_btn:
                hideInputSoft();
                if (listProvinceCity == null)
                    return;
                wheelCity.show(this, listProvinceCity);
                break;
        }

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

    private void addAddress() {

        if (StringUtils.isEmpty(countryId)) {
            TipsDialog.popup(this, getString(R.string.mall_please_choise_country), getString(R.string.mall_confirm2));
            return;
        }

        String phoneNo = edtPhone.getText().toString();
        if (StringUtils.isEmpty(phoneNo)) {
            TipsDialog.popup(this, getString(R.string.mall_please_input_phone), getString(R.string.mall_confirm2));
            return;
        }

        if (countryId.equals("9001")) {
            boolean isVerified = NumberUtil.isPhoneNo(phoneNo);
            if (!isVerified) {
                TipsDialog.popup(this, getString(R.string.mall_correct_input_phone), getString(R.string.mall_confirm2));
                return;
            }
        }

        dialog.setMsgText(getString(R.string.mall_is_to_save_adress));
        dialog.show();

        Params params = new Params();
        params.put("addressid", addressid);
        params.put("countryid", countryId);
        params.put("provinceid", provinceid);
        params.put("cityid", cityid);
        params.put("districtid", districtid);
        params.put("username", edtName.getText().toString());
        params.put("phone", phoneNo);
        params.put("address", edtDetails.getText().toString());
//		params.put("isdefault", checkIsDefaultAddress.isChecked() ? Address.isdefaultYes :Address.isdefaultNo );
        params.put("isdefault", isNull ? Address.isdefaultYes : Address.isdefaultNo);
        new HttpEntity(this).commonPostData(Method.addressAddOrEdit, params, this);
    }

    private void hideInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        dissmissDialog();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
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
                wheelCity.showArea(listArea);
                break;
            case Method.addressAddOrEdit:
                dialog.dismiss();
                ToastUtil.showToast(this, getString(R.string.mall_save_success));
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onChoosedOk(String value, String code1, String code2, String code3) {
        provinceid = String.valueOf(code1);
        cityid = code2;
        districtid = String.valueOf(code3);
        btnChangeArea.setText(getString(R.string.mall_choise_area) + value);
    }

    // 选择好了城市，获取区域列表
    @Override
    public void onCityChoosed(String cityCode) {
        Params params = new Params();
        params.put("code", cityCode);
        new HttpEntity(this).commonPostData(Method.getAreaData, params, this);
    }

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
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }
    }
}
