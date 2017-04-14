package yahier.exst.act.ad;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.item.ad.BrandInfo;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.DialogFactory;

import java.io.File;
import java.util.ArrayList;
import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2016/9/23.
 */

public class SetBrandAct extends ThemeActivity implements View.OnClickListener, FinalHttpCallback {
    TextView tvLinkTribe, tvLinkAD, tvTipUpload;
    ImageView imgLogo;
    EditText inputBrand;
    SetBrandAct mContext;
    Dialog mActionSheet;
    boolean isNeedUploadImg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_set_brand_act);
        setLabel("设置品牌信息");
        mContext = this;
        initViews();
        setRightText("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.choose_from_album));
        actionList.add(getString(R.string.take_photo));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(mContext, position, true);
            }
        });
        getInfo();
    }

    void initViews() {
        tvTipUpload = (TextView) findViewById(R.id.tvTipUpload);
        tvLinkTribe = (TextView) findViewById(R.id.tvLinkTribe);
        tvLinkAD = (TextView) findViewById(R.id.tvLinkAD);
        tvLinkTribe.setOnClickListener(this);
        tvLinkAD.setOnClickListener(this);
        findViewById(R.id.linImg).setOnClickListener(this);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        inputBrand = (EditText) findViewById(R.id.inputBrand);
        imgLogo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tvLinkTribe:
                tvLinkTribe.setSelected(true);
                tvLinkAD.setSelected(false);
                break;
            case R.id.tvLinkAD:
                tvLinkAD.setSelected(true);
                tvLinkTribe.setSelected(false);
                break;
            case R.id.imgLogo:
            case R.id.linImg:
                mActionSheet.show();
                break;
            case R.id.tvDeleteLogo:
                tvTipUpload.setVisibility(View.VISIBLE);
                imgLogo.setVisibility(View.GONE);
                //imgLogo.setImageBitmap(null);
                isNeedUploadImg = false;
                break;
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
            opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, Config.maxUploadImgWidth, Config.maxUploadImgHeight);
            opts.inJustDecodeBounds = false;
            Bitmap mbitmap = BitmapFactory.decodeFile(fileCover.getAbsolutePath(), opts);
            //LogUtil.logE("封面宽高:" + mbitmap.getWidth() + ":" + mbitmap.getWidth());
            imgLogo.setImageBitmap(mbitmap);
            imgLogo.setVisibility(View.VISIBLE);
            tvTipUpload.setVisibility(View.GONE);
            //int containerWidth = Device.getWidth(mContext);
            //int bitmapHeight = containerWidth * mbitmap.getHeight() / mbitmap.getWidth();
            //imgLogo.setLayoutParams(new FrameLayout.LayoutParams(containerWidth, bitmapHeight));
            //String path = fileCover.getAbsolutePath();
            isNeedUploadImg = true;
        }


    }


    void getInfo() {
        new HttpEntity(mActivity).commonPostData(Method.getAdBrandOnlySettingPage, null, this);
    }

    void uploadImg() {
        if (imgLogo.getVisibility() == View.GONE) {
            ToastUtil.showToast("请上传品牌LOGO");
            return;
        }

        if (inputBrand.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请填写品牌名称");
            return;
        }

        if (tvLinkTribe.isSelected() == false && tvLinkAD.isSelected() == false) {
            ToastUtil.showToast("请设置跳转类型");
            return;
        }


        WaitingDialog.show(this, false);
        //没有显示logo的，或者显示了但没修改过的。
        if (isNeedUploadImg == false) {
            doCommit(null);
        } else {
            //上传图片
            BitmapDrawable drawable = (BitmapDrawable) imgLogo.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Params params = new Params();
            params.put("pic", bitmap);
            params.put("type", BrandInfo.typeUploadImgBrand);
            new HttpEntity(this).commonPostImg(Method.uploadAdImg, params, this);
        }
    }

    void doCommit(String imgUrl) {
        Params params = new Params();
        if (imgUrl != null)
            params.put("adimgurl", imgUrl);//Statuses.requestCount
        params.put("adnickname", inputBrand.getText().toString());
        //跳转链接
        if (tvLinkAD.isSelected()) {
            params.put("clicktype", BrandInfo.typeClickAd);
        } else {
            params.put("clicktype", BrandInfo.typeClickTribe);
        }

        new HttpEntity(mActivity).commonPostData(Method.setAdBrand, params, this);
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
            case Method.setAdBrand:
                TipsDialog.popup(this, "修改保存成功", "确定", new TipsDialog.OnTipsListener() {
                    @Override
                    public void onConfirm() {
                        finish();

                    }

                    @Override
                    public void onCancel() {

                    }
                });
                EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeUpdateBrand));
//                Intent intent = new Intent(this, CommonResultAct.class);
//                intent.putExtra("type", CommonResultAct.typeCommitAdApply);
//                startActivity(intent);
                break;
            case Method.getAdBrandOnlySettingPage:
                BrandInfo brand = JSONHelper.getObject(obj, BrandInfo.class);
                if (brand == null) {
                    tvLinkTribe.setSelected(true);
                    return;
                }
                if (!TextUtils.isEmpty(brand.getAdnickname())) {
                    inputBrand.setText(brand.getAdnickname());
                }
                if (TextUtils.isEmpty(brand.getAdimgurl())) {
                    imgLogo.setVisibility(View.GONE);
                    tvTipUpload.setVisibility(View.VISIBLE);
                } else {
                    PicassoUtil.load(this, brand.getAdimgurl(), imgLogo);
                    imgLogo.setVisibility(View.VISIBLE);
                    tvTipUpload.setVisibility(View.GONE);
                }

                if (brand.getClicktype() == BrandInfo.typeClickAd) {
                    tvLinkAD.setSelected(true);
                } else if (brand.getClicktype() == BrandInfo.typeClickTribe) {
                    tvLinkTribe.setSelected(true);
                } else {
                    //如果没有选择过，则默认选择跳转部落
                    tvLinkTribe.setSelected(true);
                }
                break;


        }
    }
}
