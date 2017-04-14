package yahier.exst.act.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.preferences.STBLWession;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.AuthToken;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CountryPhoneCode;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.UserRole;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.LoginTask;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.DesUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2016/3/12.
 */
public class RegisterStep1ActNew extends ThemeActivity implements View.OnClickListener, FinalHttpCallback {
    TextView tvZone;
    TextView tvUserProtocal;
    EditText inputNick;
    EditText inputPhone;
    ImageView imgUser;

    TextView btnOk;
    //final String UserProtocal = "<font color='#999999'>注册即表示同意</font><font color='#e7be09'><b>《用户协议》</b></font>";


    Activity mContext;
    UserItem user;
    RegisterStep1ActNew act;
    private CountryPhoneCode mCurrentCode;

    private boolean mIsDestroy;
    private StringWheelDialog mWheelDialog;
    private ArrayList<CountryPhoneCode> mCountryCodeList;
    private ArrayList<String> mStringList;
    private Dialog mActionSheet;
    File file;
    int maxWidth = 400, maxHeight = 400;

    View linLogin3;
    WxInfo mWxInfo;
    View imgDeleteNick, imgDeletePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_step1_new);
        mContext = this;
        setLabel(R.string.register);

        act = this;
        imgDeleteNick = findViewById(R.id.imgDeleteNick);
        imgDeletePhone = findViewById(R.id.imgDeletePhone);
        imgDeleteNick.setOnClickListener(this);
        imgDeletePhone.setOnClickListener(this);
        linLogin3 = findViewById(R.id.linLogin3);
        btnOk = (TextView) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        inputNick = (EditText) findViewById(R.id.inputNick);
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        tvZone = (TextView) findViewById(R.id.tvZone);
        findViewById(R.id.linZone).setOnClickListener(this);
        findViewById(R.id.imgLoginWechat).setOnClickListener(this);
        tvUserProtocal = (TextView) findViewById(R.id.tvUserProtocal);
        tvUserProtocal.setText(Html.fromHtml(getString(R.string.user_protical_tip)));
        tvUserProtocal.setOnClickListener(this);
        RegisterActList.add(this);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        imgUser.setOnClickListener(this);
        inputNick.addTextChangedListener(new TextsListener());
        tvZone.addTextChangedListener(new TextsListener());
        inputPhone.addTextChangedListener(new TextsListener());
        mCountryCodeList = new ArrayList<>();
        mStringList = new ArrayList<>();
        mWheelDialog = new StringWheelDialog(this);
        getCountryCodeList();
        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                if (mCountryCodeList.size() == 0) return;
                mCurrentCode = mCountryCodeList.get(position);
                //SharedUser.putCountryCode(act, mCurrentCode.getCountry(), mCurrentCode.getPrefix());
                tvZone.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
            }

            @Override
            public void onRetry() {
                getCountryCodeList();
            }
        });

        //
        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.choose_from_album));
        actionList.add(getString(R.string.take_photo));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(act, position);
            }
        });
        if (!CommonShare.isWechatInstalled(act)) {
            linLogin3.setVisibility(View.GONE);
        }
        setWechatValue();
    }


    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            setBtnCheck();
        }
    }

    void setBtnCheck() {
        String nick = inputNick.getText().toString().trim();
        String zone = tvZone.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            imgDeleteNick.setVisibility(View.GONE);
        } else {
            imgDeleteNick.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(phone)) {
            imgDeletePhone.setVisibility(View.GONE);
        } else {
            imgDeletePhone.setVisibility(View.VISIBLE);
        }


        if (nick.equals("") || zone.equals("") || phone.equals("")) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }

    }


    List<CountryPhoneCode> listCode;

    @Override
    public void onClick(final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        },500);
        view.setEnabled(false);
        switch(view.getId()) {
            case R.id.tvUserProtocal:
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.regis_instrod, "");
                Intent intent = new Intent(this, CommonWeb.class);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case R.id.btnOk:
                checkPhoneNick();
                break;
            case R.id.linZone:
                mWheelDialog.show();
                break;
            case R.id.imgUser:
                mActionSheet.show();
                break;
            case R.id.imgLoginWechat:

                break;
            case R.id.imgDeleteNick:
                inputNick.setText("");
                break;
            case R.id.imgDeletePhone:
                inputPhone.setText("");
                break;
        }
    }



    //如果是微信授权，则自动填写一些数据
    void setWechatValue() {
        LogUtil.logE("setWechatValue", "setWechatValue");
        MyApplication app = (MyApplication) getApplication();
        user = app.getUserItem();
        //if (user == null) return;
        inputNick.setText(user.getNickname());
        LogUtil.logE("setWechatValue", user.getImgurl());
        //如果有微信后的图片
        if (!TextUtils.isEmpty(user.getOpenid()) && !TextUtils.isEmpty(user.getImgurl())) {
            PicassoUtil.load(this, user.getImgurl(), imgUser);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
        WaitingDialog.dismiss();
        if (mWheelDialog.isShowing()) {
            mWheelDialog.dismiss();
        }

        RegisterActList.remove(this);
    }

    void saveUserInfo() {
        if (user == null) user = new UserItem();
        String nickName = inputNick.getText().toString();
        String phone = inputPhone.getText().toString();
        user.setNickname(nickName);
        user.setTelphone(phone);
        user.setPhonePrex(mCurrentCode.getPrefix());
        //保存头像
        SharedUser.putUserValue(user);

    }


    /**
     * 检查昵称和手机号码
     */
    void checkPhoneNick() {
        String nickName = inputNick.getText().toString();
        if (nickName.equals("")) {
            ToastUtil.showToast(R.string.nick_canot_be_null);
            return;
        }
        String phone = inputPhone.getText().toString();
        Params params = new Params();
        params.put("nickname", nickName);
        params.put("phone", phone);
        params.put("areacode", mCurrentCode.getPrefix());
        new HttpEntity(this).commonPostData(Method.checkPhoneNick, params, this);
    }

    /**
     * 获取省份 城市
     */
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
                //初始选择
                mCurrentCode = mCountryCodeList.get(0);
                tvZone.setText(mCurrentCode.getCountry() + "+" + mCurrentCode.getPrefix());
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
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.checkPhoneNick:
                saveUserInfo();
                Intent intent = new Intent(this, RegisterStep2ActNew.class);
                intent.putExtra("imgUserPath", imgUserPath);
                startActivity(intent);
                break;


        }
    }

    String imgUserPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            file = CropPhotoUtils.onActivityResult(this, requestCode, resultCode, data);
            if (file != null) {
                imgUserPath = file.getAbsolutePath();
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                opts.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(imgUserPath, opts);
//                opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, maxWidth, maxHeight);
//                opts.inJustDecodeBounds = false;
//                Bitmap imgBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
//                imgUser.setImageBitmap(imgBitmap);
                ImageUtils.loadCircleHead(file, imgUser);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



}
