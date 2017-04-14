package yahier.exst.act.dongtai;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.CropPhotoUtils;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.DialogFactory;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

public class DongtaiPulishLongAct extends BaseActivity implements OnClickListener, FinalHttpCallback {
    DongtaiPulishLongAct mContext;
    EditText input_title;
    LinearLayout group;
    TextView pulishBtn;
    LinearLayout.LayoutParams layoutParams;
    int childSize = 0;
    String fileName;
    long statusesid;// 发布的微博id
    List<Bitmap> listBitmap = new ArrayList<Bitmap>();// 要上传的图片列表
    int uploadImgSize = 0;// 已经上传的图片数目


    Dialog dialog;
    int type_leave = 0;
    final int type_leave_back = 0;// 后退 回到主页
    final int type_leave_forward = 1;// 前进
    int linkType;
    String linkId;
    Statuses linkStatuses;
    TextView tvLink;
    View linkGroup;
    private final int requestLink = 110;
    private final int CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE = 200;
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Statuses newStatuses;
    ImageView imgCover;
    final int indexCover = 99;
    TextView tvLeftCount;
    final int titleMaxLength = 30;

    private LoadingTipsDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_pulish_long);
        ImageUtils.clearMemoryCache();
        mContext = this;
        EventBus.getDefault().register(this);
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        // setRightImage(R.drawable.icon_dynamic_short, this);
        input_title = (EditText) findViewById(R.id.input_title);
        group = (LinearLayout) findViewById(R.id.group);
        tvLink = (TextView) findViewById(R.id.tvLink);
        linkGroup = findViewById(R.id.link_Lin);

        String value = getString(R.string.add_link_card_collection);
        tvLink.setText(Html.fromHtml(value));
        imgCover = (ImageView) findViewById(R.id.imgCover);
        tvLeftCount = (TextView) findViewById(R.id.tvLeftCount);
        findViewById(R.id.linCover).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete1).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete2).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete3).setOnClickListener(this);
        findViewById(R.id.btnPulishShort).setOnClickListener(this);
        tvLink.setOnClickListener(this);
        findViewById(R.id.pulish_picture).setOnClickListener(this);
        findViewById(R.id.pulish_text).setOnClickListener(this);
        pulishBtn = (TextView) findViewById(R.id.pulish);
        pulishBtn.setOnClickListener(this);
        layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 10, 0, 10);

        input_title.addTextChangedListener(new TextListener() {

            @Override
            public void afterTextChanged(Editable edit) {
                int size = edit.toString().length();
                tvLeftCount.setText(size + "/" + titleMaxLength);
                if (edit.toString().trim().equals("")) {
                    disablePulishBtn();
                } else {
                    ablePulishBtn();
                }

            }
        });

        String sharedContent = SharedStatusesLong.getContent(mContext);
        addText(sharedContent);
        SharedStatusesLong.clear(mContext);

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.choose_from_album));
        actionList.add(getString(R.string.take_photo));
        mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActionSheet.dismiss();
                CropPhotoUtils.select(mContext, position, false);
            }
        });

        mLoadingDialog = new LoadingTipsDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    Dialog mActionSheet;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Bimp.tempSelectBitmap.clear();
    }

    private void hideInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    void disablePulishBtn() {
        pulishBtn.setEnabled(false);
    }

    void ablePulishBtn() {
        pulishBtn.setEnabled(true);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.theme_top_banner_left:
                type_leave = type_leave_back;
                showDeleteTipDialog();
                break;
            case R.id.btnPulishShort:
                type_leave = type_leave_forward;
                showDeleteTipDialog();
                break;
            case R.id.tvLink:
                hideInputSoft();
                showLinkWindow();
                break;
            case R.id.pulish_picture:
                // 隐藏默认输入法
                hideInputSoft();
                childSize++;
                showGetPicWindow();
                break;
            case R.id.pulish_text:
                int size = group.getChildCount();
                // 检查最后一个EditText有没有输入
                if (size > 0) {
                    View item = group.getChildAt(size - 1);
                    if (item instanceof EditText) {
                        String str = ((EditText) item).getText().toString();
                        if (str.equals("")) {
                            ToastUtil.showToast(R.string.please_input_first);
                            return;
                        }
                    }
                }

                childSize++;
                break;
            case R.id.pulish:
                String title = input_title.getText().toString();
                if (title.trim().equals("")) {
                    ToastUtil.showToast(R.string.please_input_title);
                    return;
                }
                if (title.length() > titleMaxLength) {
                    ToastUtil.showToast(R.string.title_is_too_long);
                    return;
                }
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                pulish();
                break;
            case R.id.window_close:
                dialog.dismiss();
                break;
            case R.id.window_ok:
                dialog.dismiss();
                if (type_leave == type_leave_forward) {
                    Intent intent = new Intent(this, DongtaiPulishLongAct.class);
                    startActivity(intent);
                    finish();
                } else if (type_leave == type_leave_back) {
                    finish();
                }
                break;
//            case R.id.window_no:
//                dialog.dismiss();
//                break;
            case R.id.link_card:
                pop.dismiss();
                startLinkActivity(DongtaiAddBusinessCardAct.class);
                break;
            case R.id.link_wish:
                pop.dismiss();
                startLinkActivity(AddLinkWishAct.class);
                break;
            case R.id.link_collect:
                pop.dismiss();
                startLinkActivity(MyCollectionActivity.class);
                break;
            case R.id.tvLinkDelete1:
                linkGroup.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
            case R.id.tvLinkDelete2:
                linkGroup.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
            case R.id.tvLinkDelete3:
                linkGroup.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
            case R.id.linCover:
//                String fileName = String.valueOf(System.currentTimeMillis());
//                file = FileUtils.getFile(fileName);
//                LogUtil.logE("file path:" + file.getAbsolutePath());
//                mapUtil = new BitmapUtil(this);
//                mapUtil.showGetPicWindow(this, file.getAbsolutePath());
                // testCoverImg();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                mActionSheet.show();
                break;
        }

    }


    public void onEvent(EventStatusesType type) {
        LogUtil.logE("onEvent type:" + type.getType());
        switch (type.getType()) {
            case EventStatusesType.typeGoods:
                linkGroup.setVisibility(View.GONE);
                showGoodsLink(type.getGoods());
                break;
            case EventStatusesType.typeCard:
                linkGroup.setVisibility(View.GONE);
                showCardLink(type.getUser());
                break;
            case EventStatusesType.typeStatuses:
                linkGroup.setVisibility(View.GONE);
                showStatusesLink(type.getStatuses().getStatuses());
                break;
            default:
                // showToast("onEvent:default");
                break;
        }

    }

    void showGoodsLink(Goods goods) {
        linkType = Statuses.linkTypeGoods;
        linkId = String.valueOf(goods.getGoodsid());
        setLinkVisibility();

        findViewById(R.id.linkGoods).setVisibility(View.VISIBLE);
        ImageView imgLink = (ImageView) findViewById(R.id.link4imgLink);
        TextView tvGoodsTitle = (TextView) findViewById(R.id.link4tvGoodsTitle);
        TextView tvGoodsPrice = (TextView) findViewById(R.id.link4tvGoodsPrice);
        TextView tvGoodsSale = (TextView) findViewById(R.id.link4tvGoodsSale);
        PicassoUtil.load(mContext, goods.getImgurl(), imgLink);
        tvGoodsTitle.setText(goods.getGoodsname());
        tvGoodsPrice.setText("￥" + goods.getMinprice());
        tvGoodsSale.setText("销量:" + goods.getSalecount());
    }

    void showCardLink(UserItem user) {
        linkType = Statuses.linkTypeCard;
        linkId = String.valueOf(user.getUserid());
        setLinkVisibility();
        ImageView imgLink = (ImageView) findViewById(R.id.link1imgUser);
        TextView name = (TextView) findViewById(R.id.link1name);
        TextView user_gender_age = (TextView) findViewById(R.id.link1user_gender_age);
        TextView user_city = (TextView) findViewById(R.id.link1user_city);
        TextView user_signature = (TextView) findViewById(R.id.link1user_signature);

        PicassoUtil.load(mContext, user.getImgmiddleurl(), imgLink);
        name.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
//        name.setText(user.getNickname());
        user_city.setText(user.getCityname());
        user_signature.setText(user.getSignature());
        user_gender_age.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
        } else if (user.getGender() == UserItem.gender_girl) {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
        } else {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
        }
    }

    void showStatusesLink(Statuses statuses) {
        linkType = Statuses.linkTypeStatuses;
        linkId = String.valueOf(statuses.getStatusesid());
        setLinkVisibility();
        ImageView link3Img = (ImageView) findViewById(R.id.link3_img);
        TextView link3Tv = (TextView) findViewById(R.id.link3_content);
        if (statuses.getTitle() != null && !statuses.getTitle().equals("")) {
            link3Tv.setText(statuses.getTitle());
        } else {
            link3Tv.setText(statuses.getContent().replace(Config.longWeiboFillMark, " "));
        }
        TextView lin3UserName = (TextView) findViewById(R.id.link3_userName);
        if (statuses.getUser() != null) {
//            lin3UserName.setText(statuses.getUser().getNickname());
            String alias = statuses.getUser().getAlias();
            lin3UserName.setText(alias == null || alias.equals("") ? statuses.getUser().getNickname() : alias);
        }
        StatusesPic link3Pics = statuses.getStatusespic();
        if (link3Pics != null && link3Pics.getPics().size() > 0) {
            String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
            PicassoUtil.load(mContext, imgUrl, link3Img);
        }

    }

    void setLinkVisibility() {
        View[] linkViews = {findViewById(R.id.linkCard), findViewById(R.id.linkWish), findViewById(R.id.linkStatuses), findViewById(R.id.linkGoods)};
        for (int i = 0; i < linkViews.length; i++) {
            linkViews[i].setVisibility(View.GONE);
        }
        // linkDelete.setVisibility(View.GONE);
        if (linkType == 0)
            return;
        linkViews[linkType - 1].setVisibility(View.VISIBLE);
        // linkDelete.setVisibility(View.VISIBLE);
    }

    void startLinkActivity(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        intent.putExtra("mode", MyCollectionActivity.mode_statuses_choose);
        startActivityForResult(intent, requestLink);
    }

    void showDeleteTipDialog() {
        // 判断有没有内容
        if (input_title.getText().toString().trim().equals("") && combineStr().equals("") && listBitmap.size() == 0 && linkType == 0) {
            if (type_leave == type_leave_forward) {
                Intent intent = new Intent(this, PublishShortStatusActivity.class);
                startActivity(intent);
                finish();
            } else if (type_leave == type_leave_back) {
                finish();
            }
            return;
        }

        TipsDialog.popup(this, R.string.is_giveup_editing, R.string.cancel, R.string.queding, new OnTipsListener() {

            @Override
            public void onConfirm() {
                if (type_leave == type_leave_forward) {
                    Intent intent = new Intent(DongtaiPulishLongAct.this, DongtaiPulish.class);
                    startActivity(intent);
                    finish();
                } else if (type_leave == type_leave_back) {
                    finish();
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    Dialog pop;

    private void showGetPicWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            return;
        }
        pop = new Dialog(this, R.style.Common_Dialog);
        View view = getLayoutInflater().inflate(R.layout.window_select_image, null);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pop.dismiss();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE);
                int currentSize = listBitmap.size();
                if (currentSize >= 15) {
                    ToastUtil.showToast(R.string.already_add_15_img);
                    return;
                }
                Intent intent = new Intent(DongtaiPulishLongAct.this, AlbumActivity.class);
                intent.putExtra("MAX_NUM", 15 - currentSize);
                startActivityForResult(intent, CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE);
                //下面这种一样。
//                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//                getAlbum.setType("image/*");
//                startActivityForResult(getAlbum, CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE);
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
            }
        });
        pop.setContentView(view);
        Window window = pop.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        pop.show();
    }

    private void showLinkWindow() {
        hideInputSoft();
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            return;
        }
        pop = new Dialog(this, R.style.Common_Dialog);
        View view = getLayoutInflater().inflate(R.layout.window_show_link, null);
        view.findViewById(R.id.link_card).setOnClickListener(this);
        view.findViewById(R.id.link_wish).setOnClickListener(this);
        view.findViewById(R.id.link_collect).setOnClickListener(this);
        view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pop.dismiss();
            }
        });
        view.findViewById(R.id.parent).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pop.dismiss();
            }
        });
        pop.setContentView(view);
        Window window = pop.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        pop.show();
    }


    public void photo() {
        fileName = String.valueOf(System.currentTimeMillis());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.getFile(fileName)));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    void addText(String... strs) {
        EmojiconEditText edit = new EmojiconEditText(this);
        edit.setBackgroundColor(getResources().getColor(R.color.white));
        edit.setPadding(5, 15, 5, 15);
        edit.setTextSize(18);
        edit.setEmojiconSize(38);
        edit.setGravity(Gravity.CENTER_VERTICAL);
        if (strs != null) {
            // edit.setText(strs[0]);

        }
        group.addView(edit, layoutParams);
    }

    @Override
    public void onBackPressed() {
        showDeleteTipDialog();
    }

    void addImage(final Bitmap bitmap) {
        if (bitmap == null) return;
        final View view = LayoutInflater.from(this).inflate(R.layout.dongtai_pulish_long_img, null);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        int height = Device.getWidth(this) * bitmap.getHeight() / bitmap.getWidth();
        img.setLayoutParams(new FrameLayout.LayoutParams(Device.getWidth(this), height));
        img.setImageBitmap(bitmap);
        group.addView(view);
        listBitmap.add(bitmap);
        // 删除图片功能
        view.findViewById(R.id.img_delete).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                group.removeView(view);
                listBitmap.remove(bitmap);
            }
        });
        addText();
    }

    /**
     * 将图片文字全部整合成大一个字符串
     */

    String combineStr() {
        LogUtil.logE("整合", "combineStr");
        String valueStr = "";
        int size = group.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                valueStr += ((EditText) view).getText().toString().trim();
            } else if (view instanceof FrameLayout) {
                valueStr += Config.longWeiboFillMark;
            }
        }
        return valueStr;

    }

    // 以下是拍照取图相关的

    Bitmap mbitmap;

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
            imgCover.setImageBitmap(mbitmap);
            findViewById(R.id.tvCoverTip).setVisibility(View.GONE);
            imgCover.setVisibility(View.VISIBLE);
            int containerWidth = Device.getWidth(mContext);
            int bitmapHeight = containerWidth * mbitmap.getHeight() / mbitmap.getWidth();
            imgCover.setLayoutParams(new FrameLayout.LayoutParams(containerWidth, bitmapHeight));
            String path = fileCover.getAbsolutePath();
            LogUtil.logE("path:" + path);
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + path)));

            //fileCover.delete();
        }

        ContentResolver resolver = getContentResolver();
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
            case 300:
                System.out.println("resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    FileUtils.scanFile(FileUtils.getFile(fileName));
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);
                    opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, Config.maxUploadImgWidth, Config.maxUploadImgHeight);
                    opts.inJustDecodeBounds = false;
                    mbitmap = BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);
                    mbitmap = BitmapUtil.rotateUpright(mbitmap, FileUtils.getFile(fileName).getAbsolutePath());
                    if (mbitmap != null) {
                        Log.e("Main", mbitmap.getWidth() + ":" + mbitmap.getHeight());
                        addImage(mbitmap);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }

                break;
            case CAPTURE_GALLERY_ACTIVITY_REQUEST_CODE:
            case 400:
                //if (resultCode == RESULT_OK) {
                if (Bimp.tempSelectBitmap.size() > 0) {
                    for (ImageItem item : Bimp.tempSelectBitmap) {
                        //设置。修改颠倒问题。
                        Bitmap bitmap = BitmapUtil.rotateUpright(item.getBitmap(), item.getImagePath(), false);
                        addImage(bitmap);
                    }
                    Bimp.tempSelectBitmap.clear();
                }

                    /*
                    try {
                        Uri originalUri = data.getData();
                        LogUtil.logE("originasmlUri:"+originalUri);
                        byte[] mContext = BitmapUtil.readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        // BitmapFactory.Options opts2 = new
                        // BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        // opts2.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(mContext, 0, mContext.length, opts);
                        opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, maxWidth, maxHeight);
                        opts.inJustDecodeBounds = false;
                        mbitmap = BitmapUtil.getPicFromBytes(mContext, opts);
                        // img_test.setImageBitmap(mbitmap);
                        addImage(mbitmap);
//                        File file = new File(Environment.getExternalStorageDirectory(), "/YlseImgchooseTemp" + ".jpg");
//                        FileOutputStream outputStream = null;
//                        try {
//                            outputStream = new FileOutputStream(file);
//                            mbitmap.compress(CompressFormat.JPEG, 100, outputStream);
//                            outputStream.close();
//                            // mbitmap.recycle();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    */
                //}
                break;


        }

    }


    // 发布文字 invoke by 发布按钮
    private void pulish() {
        uploadImgSize = 0;
        if (listBitmap.size() == 0) {
            ToastUtil.showToast(R.string.please_upload_one_picture_atleast);
            return;
        }

        BitmapDrawable drawable = (BitmapDrawable) imgCover.getDrawable();
        if (drawable == null) {
            ToastUtil.showToast(R.string.please_add_cover_img);
            return;
        }

        if (listBitmap.size() > 15) {
            ToastUtil.showToast(R.string.already_add_15_img);
            return;
        }
        attachid = System.currentTimeMillis();
        String con = combineStr();
        // LogUtil.logE("整合之后  con:" + con);
        if (con.length() > 10000) {
            ToastUtil.showToast(R.string.content_exceed_10000_please_modify);
            return;
        }

//        WaitingDialog.show(this, getString(R.string.waiting), false);
        mLoadingDialog.showLoading(getString(R.string.me_publishing));
        disablePulishBtn();
        postImgCover();
        postImgStrea();
    }

    long attachid;

    public void postText() {
        String con = combineStr();
        JSONObject json = new JSONObject();
        try {
            json.put("attachid", attachid);
            json.put("title", input_title.getText().toString().trim());
            json.put("content", con);
            json.put("statusestype", Statuses.type_long);
            json.put("imgcount", String.valueOf(listBitmap.size()));
            if (linkType != 0 && linkId != null && !linkId.equals("")) {
                json.put("linktype", linkType);
                json.put("linkid", linkId);
            }

        } catch (org.json.JSONException t) {
            // 没有进入到异常
            StackTraceElement[] tracks = t.getStackTrace();
            for (StackTraceElement el : tracks) {
                LogUtil.logE("error:", el.getClassName());
                LogUtil.logE("error:", el.getLineNumber() + "");
            }

        }
        new HttpEntity(this).commonPostJson(Method.weiboPush, json.toString(), this);
    }

    // 流的形式发送 成功
    public void postImgStrea() {
        String value  = String.format(getString(R.string.sending_d_img),String.valueOf(1));
//        WaitingDialog.setContent(value);
        mLoadingDialog.setMessage(value);
        for (int i = 0; i < listBitmap.size(); i++) {
            Params params = new Params();
            params.put("attachid", attachid);
            params.put("statusesid", statusesid + "");
            params.put("index", String.valueOf(i));
            params.put("pic", listBitmap.get(i));
            new HttpEntity(this).commonPostImg(Method.weiboUploadImg, params, this);
        }

    }

    // 上传封面
    public void postImgCover() {
//        WaitingDialog.setContent(R.string.uploading_cover_img);
        mLoadingDialog.setMessage(getString(R.string.uploading_cover_img));
        // 获取封面Bitmap
        BitmapDrawable drawable = (BitmapDrawable) imgCover.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Params params = new Params();
        params.put("attachid", attachid);
        params.put("statusesid", statusesid + "");
        params.put("index", indexCover);
        params.put("pic", bitmap);
        new HttpEntity(this).commonPostImg(Method.weiboUploadImg, params, this);

    }
    //发布失败提示框
    private void showErrorTips(){
        TipsDialog2 tipsDialog2 = TipsDialog2.popup(this, getString(R.string.statuses_pulish_faild), getString(R.string.cancel), getString(R.string.statuses_reset_pulish), new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
                pulish();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item == null) {
            ablePulishBtn();
//            WaitingDialog.dismiss();
            mLoadingDialog.dismiss();
            return;
        }
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            String content = combineStr();
            SharedStatusesLong.putContent(mContext, content);
            ablePulishBtn();
//            WaitingDialog.dismiss();
            mLoadingDialog.dismiss();
            showErrorTips();
            return;
        }


        // 发布微博正文
        if (methodName.equals(Method.weiboPush)) {
            if (item.getIssuccess() == BaseItem.successTag) {
                String obj = JSONHelper.getStringFromObject(item.getResult());
                newStatuses = JSONHelper.getObject(obj, Statuses.class);
                statusesid = newStatuses.getStatusesid();
//                ToastUtil.showToast(R.string.post_success);
                mLoadingDialog.showSuccess(getString(R.string.post_success));
                SharedStatusesLong.clear(mContext);
                sendBroadCast();
                pulishBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                        finish();
                    }
                },1500);

            }
        } else if (methodName.equals(Method.weiboUploadImg)) {
            if (item.getIssuccess() == BaseItem.successTag) {
                uploadImgSize++;
                LogUtil.logE("uploadImgSize", uploadImgSize);
                if (uploadImgSize == listBitmap.size() + 1) {// 这个+1是因为封面
                    postText();
                } else {
                    String value  = String.format(getString(R.string.sending_d_img),String.valueOf(uploadImgSize + 1));
//                    WaitingDialog.setContent(value);
                    mLoadingDialog.setMessage(value);
                }
            }
        }

    }

    void sendBroadCast() {
        WaitingDialog.dismiss();
        Intent intent = new Intent("getOneNewStatuses");
        intent.putExtra("statuses", newStatuses);
        sendBroadcast(intent);
    }

}
