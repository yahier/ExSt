package yahier.exst.act.ad;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.PayPasswordSettingAct;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.CommonWebInteact;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.model.WithdrawAccount;
import com.stbl.stbl.model.WxInfo;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.FanliTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.CustomVerticalCenterSpan;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.widget.StblWebView;

import java.io.File;
import java.util.ArrayList;

/**
 * 绑定帐号
 * Created by Administrator on 2016/9/23.
 */

public class BindAccountActivity extends ThemeActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ADD_IMAGE = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;

    private RoundImageView mHeadIv;
    private TextView mNickTv;
    private EditText mNameEt;
    private EditText mIdCardEt;
    private ImageView mFrontIv;
    private ImageView mBackIv;
    private Button mBindBtn;

    private ImageView mDeleteFrontIv;
    private ImageView mDeleteBackIv;

    private WxInfo mWxInfo;

    private ImageItem mFrontImg;
    private ImageItem mBackImg;

    private Dialog mAddImgPopWindow;

    private File mPhotoFile;

    private int mCurrentImg;

    private StblWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        setLabel(getString(R.string.ad_bind_wechat));
        mWxInfo = (WxInfo) getIntent().getSerializableExtra(KEY.WX_INFO);
        if (mWxInfo == null) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        mHeadIv = (RoundImageView) findViewById(R.id.iv_head);
        mNickTv = (TextView) findViewById(R.id.tv_nick);
        mNameEt = (EditText) findViewById(R.id.et_real_name);
        mIdCardEt = (EditText) findViewById(R.id.et_idcard);
        mFrontIv = (ImageView) findViewById(R.id.iv_idcard_front);
        mBackIv = (ImageView) findViewById(R.id.iv_idcard_back);
        mBindBtn = (Button) findViewById(R.id.btn_bind);

        mDeleteFrontIv = (ImageView) findViewById(R.id.iv_delete_front);
        mDeleteBackIv = (ImageView) findViewById(R.id.iv_delete_back);

        mWebView = (StblWebView) findViewById(R.id.wv);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.logE("shouldOverrideUrlLoading" + "url:--" + url);
                if (url.startsWith("tel:")) {//支持拨打电话
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("stbl")) {
                    Intent intent = new Intent(mActivity, CommonWebInteact.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                return false;//原本false
            }
        });
        String url = (String) SharedPrefUtils.getFromPublicFile(KEY.withdraw_bind_instructions, "");
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        } else {
            CommonTask.getCommonDicBackground();
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(getString(R.string.me_please_input_real_name));
        int length = ssb.length();
        ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(getString(R.string.me_please_input_real_name_tip));
        ssb.setSpan(new CustomVerticalCenterSpan(9), length, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNameEt.setHint(ssb);

        SpannableStringBuilder ssb2 = new SpannableStringBuilder();
        ssb2.append(getString(R.string.me_please_input_real_id_card));
        int length2 = ssb2.length();
        ssb2.setSpan(new AbsoluteSizeSpan(15, true), 0, length2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb2.append(getString(R.string.me_please_input_real_id_card_tip));
        ssb2.setSpan(new CustomVerticalCenterSpan(9), length2, ssb2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mIdCardEt.setHint(ssb2);

        ImageUtils.loadHead(mWxInfo.getHeadimgurl(), mHeadIv);
        mNickTv.setText(mWxInfo.getNickname());
        findViewById(R.id.tv_upload_front).setOnClickListener(this);
        mFrontIv.setOnClickListener(this);
        findViewById(R.id.tv_upload_back).setOnClickListener(this);
        mBackIv.setOnClickListener(this);

        mDeleteFrontIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrontImg = null;
                mFrontIv.setImageDrawable(null);
                mDeleteFrontIv.setVisibility(View.GONE);
            }
        });

        mDeleteBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackImg = null;
                mBackIv.setImageDrawable(null);
                mDeleteBackIv.setVisibility(View.GONE);
            }
        });

        mBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeBind();
            }
        });

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.me_choose_from_phone));
        actionList.add(getString(R.string.me_photograph));
        mAddImgPopWindow = DialogFactory.createActionSheet(mActivity, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddImgPopWindow.dismiss();
                switch (position) {
                    case 0: {
                        Intent intent = new Intent(mActivity, AlbumActivity.class);
                        intent.putExtra("MAX_NUM", 1);
                        startActivityForResult(intent, REQUEST_CODE_ADD_IMAGE);
                    }
                    break;
                    case 1: {
                        mPhotoFile = new File(FileUtils.getAppDir(), System.currentTimeMillis() + ".jpg");
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
                    }
                    break;
                }
            }
        });
    }

    private void beforeBind() {
        final String name = mNameEt.getText().toString().trim();
        final String idcard = mIdCardEt.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idcard) || mFrontImg == null || mBackImg == null) {
            ToastUtil.showToast(R.string.me_please_complete_info);
            return;
        }
        Payment.getPassword(this, 0, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                if (pwd == null) {
                    ToastUtil.showToast(R.string.me_request_fail);
                    return;
                }
                if (pwd.equals("")) {
                    showSetPayPwdAlertDialog();
                } else {
                    bind(name, idcard, pwd);
                }
            }
        });
    }

    private void bind(String name, String idcard, String pwd) {
        mBindBtn.setEnabled(false);
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getString(R.string.me_binding));
        dialog.show();
        mTaskManager.start(FanliTask.bindWithdrawAccount(WithdrawAccount.ACCOUNT_TYPE_WECHAT, mWxInfo.nickname, mWxInfo.headimgurl,
                name, idcard, mWxInfo.openid, mWxInfo.unionid, pwd, mFrontImg, mBackImg)
                .setCallback(new HttpTaskCallback<String>(mActivity) {

                    @Override
                    public void onFinish() {
                        mBindBtn.setEnabled(true);
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onMessage(int arg1, int arg2, Object obj) {
                        dialog.setMessage(obj.toString());
                    }

                    @Override
                    public void onSuccess(String result) {
                        ToastUtil.showToast(R.string.me_bind_done);
                        setResult(RESULT_OK);
                        finish();
                    }
                }));
    }

    private void showSetPayPwdAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_please_set_pay_pwd_first),
                getString(R.string.cancel),
                getString(R.string.me_go_setting),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        Intent intent = new Intent(mActivity, PayPasswordSettingAct.class);
                        startActivity(intent);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_IMAGE:
                if (Bimp.tempSelectBitmap.size() > 0) {
                    setImageItem(Bimp.tempSelectBitmap.get(0));
                    Bimp.tempSelectBitmap.clear();
                }
                break;
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    FileUtils.scanFile(mPhotoFile);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.file = mPhotoFile;
                    takePhoto.setImagePath(mPhotoFile.getAbsolutePath());
                    setImageItem(takePhoto);
                }
                break;
        }
    }

    private void setImageItem(ImageItem img) {
        if (mCurrentImg == 0) {
            mFrontImg = img;
            ImageUtils.loadFile(mFrontImg.file, mFrontIv);
            mDeleteFrontIv.setVisibility(View.VISIBLE);
        } else {
            mBackImg = img;
            ImageUtils.loadFile(mBackImg.file, mBackIv);
            mDeleteBackIv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload_front:
            case R.id.iv_idcard_front:
                mCurrentImg = 0;
                mAddImgPopWindow.show();
                break;
            case R.id.tv_upload_back:
            case R.id.iv_idcard_back:
                mCurrentImg = 1;
                mAddImgPopWindow.show();
                break;
        }
    }
}
