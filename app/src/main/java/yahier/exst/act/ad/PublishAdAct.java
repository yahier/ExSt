package yahier.exst.act.ad;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.ad.AdCooperateTypeAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.StringWheelDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CommonDict;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.ad.AdBusinessType;
import com.stbl.stbl.item.ad.AdType;
import com.stbl.stbl.item.ad.AdTypeParent;
import com.stbl.stbl.item.ad.BrandInfo;
import com.stbl.stbl.item.ad.RedPacketDict;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.util.WebDialogUtil;
import com.stbl.stbl.util.WheelAd;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2016/9/21.
 */

public class PublishAdAct extends ThemeActivity implements View.OnClickListener, FinalHttpCallback, WheelAd.OnCityWheelListener {
    private ImageView img;
    private TextView tvCommit;
    private EditText inputLink, inputTitle;

    private Dialog mActionSheet;
    private PublishAdAct mContext;
    private View linImg;
    private TextView tvAdType;
    private TextView tvBusinessCoop;
    StringWheelDialog mWheelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_publish_act);
        mContext = this;
        setLabel("投放广告");
        initView();
        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.choose_from_album));
        actionList.add(getString(R.string.take_photo));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(mContext, position, CropPhotoUtils.typeUploadAd);
            }
        });

        EventBus.getDefault().register(this);
        //getBusinessTypes();
        getAdTypes();
        initBusinessData();


    }

    public void onEvent(EventTypeCommon event) {
        switch(event.getType()) {
            case EventTypeCommon.typeClosePublishAd:
                finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initView() {
        tvAdType = (TextView) findViewById(R.id.tvAdType);
        linImg = findViewById(R.id.linImg);
        int requireWidth = Device.getWidth() - Util.dip2px(this, 30);
        int requireHeight = Config.uploadAdHeightScale * requireWidth / Config.uploadAdWidthScale;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(requireWidth, requireHeight);
        params.setMargins(Util.dip2px(this, 15), Util.dip2px(this, 15), Util.dip2px(this, 15), 0);
        linImg.setLayoutParams(params);

        tvCommit = (TextView) findViewById(R.id.tvCommit);
        tvCommit.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.img);
        inputLink = (EditText) findViewById(R.id.inputLink);
        inputTitle = (EditText) findViewById(R.id.inputTitle);

        img.setOnClickListener(this);
        inputLink.addTextChangedListener(new TextsListener());
        inputTitle.addTextChangedListener(new TextsListener());
        tvAdType.addTextChangedListener(new TextsListener());
        tvAdType.setOnClickListener(this);
        tvBusinessCoop = (TextView) findViewById(R.id.tvBusinessCoop);
        tvBusinessCoop.setOnClickListener(this);
        findViewById(R.id.imgHelp).setOnClickListener(this);
        TextView tvTipUpload = (TextView)findViewById(R.id.tvTipUpload);
        String value = String.format(mContext.getString(R.string.ad_uploadimg_tip));
        tvTipUpload.setText(Html.fromHtml(value));

    }

    AdBusinessType mSelectedBusinessType;

    void initBusinessData() {
        final String cache = (String) SharedPrefUtils.getFromPublicFile(KEY.adbusinessestype, "");
        if (cache.equals("")) {
            CommonTask.getCommonDicBackground();
            ToastUtil.showToast("数据有误，请稍候重试");
            return;
        }
        final List<AdBusinessType> adreporttype = JSONHelper.getList(cache, AdBusinessType.class);

        final ArrayList<String> mStringList = new ArrayList<String>();
        for (int i = 0; i < adreporttype.size(); i++) {
            mStringList.add(adreporttype.get(i).getName());
        }
        mWheelDialog = new StringWheelDialog(this);
        mWheelDialog.setData(mStringList);
        mWheelDialog.setInterface(new StringWheelDialog.IStringWheelDialog() {
            @Override
            public void onConfirm(int position) {
                mSelectedBusinessType = adreporttype.get(position);
                tvBusinessCoop.setText(mSelectedBusinessType.getName());
            }

            @Override
            public void onRetry() {
                ToastUtil.showToast("数据有误，请稍候重试");
            }
        });
    }


    public void postImg() {
        WaitingDialog.show(this, false);
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Params params = new Params();
        params.put("pic", bitmap);
        params.put("type", BrandInfo.typeUploadImgAd);
        new HttpEntity(this).commonPostImg(Method.uploadAdImg, params, this);

    }

    void doCommit(String imgUrl) {
        Params params = new Params();
        params.put("imgurl", imgUrl);//Statuses.requestCount
        params.put("title", inputTitle.getText().toString());
        params.put("linkurl", inputLink.getText().toString());
        if (mSelectedBusinessType != null) {
            params.put("businessclass", mSelectedBusinessType.getValue());
        }
        params.put("tradeclassid", adtype.getId());
        new HttpEntity(mActivity).commonPostData(Method.commitAdApply, params, this);
    }


    //获取广告合作分类
    void getBusinessTypes() {
        Params params = new Params();
        new HttpEntity(mActivity).commonPostData(Method.adGetBusinessTypes, params, this);
    }

    //获取广告分类
    void getAdTypes() {
        Params params = new Params();
        new HttpEntity(mActivity).commonPostData(Method.adGetTypes, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mActivity, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.uploadAdImg:
                ImgUrl imgUrl = JSONHelper.getObject(obj, ImgUrl.class);
                doCommit(imgUrl.getFilename());
                break;
            case Method.commitAdApply:
                setResult(RESULT_OK);
                Intent intent = new Intent(this, CommonResultAct.class);
                intent.putExtra("type", CommonResultAct.typeCommitAdApply);
                startActivity(intent);
                break;
            case Method.adGetBusinessTypes:
                //List<AdBusinessType> list = JSONHelper.getList(obj, AdBusinessType.class);
                break;
            case Method.adGetTypes:
                listType = JSONHelper.getList(obj, AdTypeParent.class);
                //参考 UserInfoMain


                break;


        }
    }

    List<AdTypeParent> listType;
    AdType adtype;
    int currItem1 = 0;//广告大类
    int currItem2 = 0;//广告小类

    @Override
    public void onChoosed(String title, AdType type, int currItem1, int currItem2) {
        this.adtype = type;
        this.currItem1 = currItem1;
        this.currItem2 = currItem2;
        tvAdType.setText(title + " " + type.getTitle());
    }

    class TextsListener extends TextListener {
        @Override
        public void afterTextChanged(Editable arg0) {
            checkIsInputOk();
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.img:
                mActionSheet.show();
                break;
            case R.id.tvCommit:
                postImg();
                break;
            case R.id.tvAdType:
                if (listType == null) return;
                WheelAd wheelCity = new WheelAd();
                wheelCity.setOnCityWheelListener(this);
                wheelCity.setCurrItem(currItem1, currItem2);
                wheelCity.show(this, listType);
                break;
            case R.id.tvBusinessCoop:
                mWheelDialog.show();
                break;
            case R.id.imgHelp:
                String url = (String) SharedPrefUtils.getFromPublicFile(KEY.adsys_business_introd,"");
                new WebDialogUtil().setUrl(this,url);
                if (true) return;
                TipsDialog.popup(this, "你可以为广告加上\n诚招代理或诚招加盟的入口", "确定", new TipsDialog.OnTipsListener() {
                    @Override
                    public void onConfirm() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });

                break;
        }
    }


    void checkIsInputOk() {
        if (img.getDrawable() != null && !inputLink.getText().toString().equals("") && !inputTitle.getText().toString().equals("") && !tvAdType.getText().toString().equals("")) {
            tvCommit.setEnabled(true);
        } else {
            tvCommit.setEnabled(false);
        }
    }

    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //剪裁的封面图
        File fileCover = CropPhotoUtils.onActivityResult(this, requestCode, resultCode, data);
        if (fileCover != null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileCover.getAbsolutePath(), opts);

            if (opts.outWidth < Config.minUploadAdImgWidth) {
                TipsDialog dialog = new TipsDialog(this, "提示", "图片像素过低，请重新上传", "确定");

                dialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
                    @Override
                    public void onConfirm() {
                        //img.setImageDrawable(null);
                        //checkIsInputOk();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                dialog.show();

            } else {
                opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, Config.maxUploadAdImgWidth, Config.maxUploadAdImgHeight);
                opts.inJustDecodeBounds = false;
                Bitmap mbitmap = BitmapFactory.decodeFile(fileCover.getAbsolutePath(), opts);
                //LogUtil.logE("封面宽高:" + mbitmap.getWidth() + ":" + mbitmap.getWidth());

                LogUtil.logE("PublishAdAct 输出的原图:" + opts.outWidth + ":" + opts.outHeight);
                LogUtil.logE("PublishAdAct 压缩后的图:" + mbitmap.getWidth() + ":" + mbitmap.getHeight());
                img.setImageBitmap(mbitmap);
                img.setVisibility(View.VISIBLE);
                int containerWidth = Device.getWidth(mContext);
                int bitmapHeight = containerWidth * mbitmap.getHeight() / mbitmap.getWidth();
                img.setLayoutParams(new FrameLayout.LayoutParams(containerWidth, bitmapHeight));
                //String path = fileCover.getAbsolutePath();
                checkIsInputOk();
            }


        }


    }


}
