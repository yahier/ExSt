package yahier.exst.act.dongtai;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.adapter.GVImgAdapter;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.PublicWay;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.WaitingDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import io.rong.eventbus.EventBus;


/**
 * @author lenovo
 * 由DongtaiRepost替换
 */
@Deprecated
public class DongtaiPulish extends BaseActivity implements OnClickListener, FinalHttpCallback {
    Context mContext;
    private GridView gridview;
    private GridAdapter adapter;
    private EditText inputContent;
    private TextView btnPulish;
    private Dialog pop = null;
    TextView tvLink;
    String fileName;
    long statusesid;// 发布的微博id
    int uploadImgSize = 0;// 上传成功的图片数目
    // int uploadImgRespondeSize = 0;//上传得到反馈结果的数目，无论成功还是失败的结果
    // Dialog dialog;
    int type_leave = 0;
    final int type_leave_back = 0;// 后退 回到主页
    final int type_leave_forward = 1;// 前进
    // 链接相关
    int linkType;
    Statuses linkStatuses;

    String linkId;
    int typeSource = 0;
    public final static int typeOrigin = 0;// 原创的
    public final static int typeForward = 1;// 转发的

    private final int REQUEST_TAKE_PICTURE = 0x000001;
    private final int resultRorwardCode = 101;// 与DongtaiAct中的request一致
    TextView tvTitle;
    // 发布生成的动态
    Statuses newStatuses;
    View linkGroup;
    TextView tvLeftCount;
    final String tag = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_pulish_act);
        EventBus.getDefault().register(this);
        mContext = DongtaiPulish.this;
        inits();
        getIntentData();
        ToastUtil.showToast("请用新的转发页面");
    }

    //
    public void onEvent(EventStatusesType type) {
        LogUtil.logE("onEvent type:" + type.getType());
        switch(type.getType()) {
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
                linkStatuses = type.getStatuses().getStatuses();
                showStatusesLink();
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
        tvGoodsSale.setText(getString(R.string.sale_count_) + goods.getSalecount());
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

        PicassoUtil.load(mContext, user.getImgurl(), imgLink);
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

    /**
     * 将整个动态都传进来，而不仅仅是链接的动态
     *
     * @param
     */
    void showStatusesLink() {
        LogUtil.logE("showStatusesLink:" + linkStatuses.getContent());
        //如果是转发.显示内容在编辑框，并取得下一层的动态
        if (linkStatuses.getIsforward() == Statuses.isforwardYes) {
            String alias = linkStatuses.getUser().getAlias();
            String nickName = alias == null || alias.equals("") ? linkStatuses.getUser().getNickname() : alias;
//            String nickName = linkStatuses.getUser().getNickname();
            String content = linkStatuses.getContent();
            content = content.replace(Config.longWeiboFillMark, "");
            inputContent.setText("//@" + nickName + ":" + content);
            linkStatuses = linkStatuses.getLinks().getStatusesinfo();
        }
        linkType = Statuses.linkTypeStatuses;
        linkId = String.valueOf(linkStatuses.getStatusesid());//
        setLinkVisibility();

        TextView link3Tv = (TextView) findViewById(R.id.link3_content);

        if (linkStatuses.getTitle() != null && !linkStatuses.getTitle().equals("")) {
            link3Tv.setText(linkStatuses.getTitle());
        } else {
            link3Tv.setText(linkStatuses.getContent().replace(Config.longWeiboFillMark, " "));
        }
        TextView lin3UserName = (TextView) findViewById(R.id.link3_userName);
        String alias = linkStatuses.getUser().getAlias();
        lin3UserName.setText(alias == null || alias.equals("") ? linkStatuses.getUser().getNickname() : alias);
//        lin3UserName.setText(linkStatuses.getUser().getNickname());

        ImageView link3Img = (ImageView) findViewById(R.id.link3_img);
        StatusesPic link3Pics = linkStatuses.getStatusespic();
        if (link3Pics != null) {
            String imgUrl;
            if (link3Pics.getDefaultpic().equals(GVImgAdapter.longCover)) {
                imgUrl = link3Pics.getMiddlepic() + link3Pics.getDefaultpic();
            } else if (link3Pics.getPics().size() > 0) {
                imgUrl = link3Pics.getMiddlepic() + link3Pics.getPics().get(0);
            } else {
                imgUrl = link3Pics.getEx();
            }
            PicassoUtil.loadStatuses(mContext, imgUrl, link3Img);
        } else {
            PicassoUtil.load(mContext, R.drawable.dongtai_default, link3Img);
        }


    }

    void setLinkVisibility() {
        View[] linkViews = {findViewById(R.id.linkCard), findViewById(R.id.linkWish), findViewById(R.id.linkStatuses), findViewById(R.id.linkGoods)};
        for (int i = 0; i < linkViews.length; i++) {
            linkViews[i].setVisibility(View.GONE);
        }
        //	linkDelete.setVisibility(View.GONE);
        if (linkType == 0) {
            return;
        }
        linkViews[linkType - 1].setVisibility(View.VISIBLE);
        if (typeSource != typeForward) {
            //linkDelete.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 转发时，进入则弹出键盘
     */
    private void showInputSoft() {
        inputContent.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(inputContent, InputMethodManager.SHOW_FORCED);// SHOW_FORCED

            }
        }, 100);


    }

    /**
     * 获取进入activity时 带入的数据
     */
    void getIntentData() {
        Intent intent = getIntent();
        typeSource = intent.getIntExtra("typeSource", 0);
        // 如果是转发的.不能修改，不能删除
        if (typeSource == typeForward) {
            //linkDelete.setVisibility(View.GONE);
            tvTitle.setText(R.string.repost_statuses);
            tvLink.setOnClickListener(null);
            gridview.setVisibility(View.GONE);
            showInputSoft();
        } else {
            tvLink.setOnClickListener(this);
            // linkDelete.setVisibility(View.VISIBLE);
        }
        linkType = intent.getIntExtra("linkType", 0);
        if (linkType == 0) {
            return;
        }
        checkPulishBtn();
        switch(linkType) {
            case LinkStatuses.linkTypeStateses:
                findViewById(R.id.tvLinkDelete1).setVisibility(View.GONE);
                linkStatuses = (Statuses) intent.getSerializableExtra("data");
                findViewById(R.id.link_Lin).setVisibility(View.GONE);
                findViewById(R.id.btnPulishLong).setVisibility(View.GONE);
                showStatusesLink();
                break;
        }

    }

    public void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    @Override
    public void onClick(final View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.theme_top_banner_left:
                type_leave = type_leave_back;
                showDeleteTipDialog();
                break;
            case R.id.btnPulishLong:
                type_leave = type_leave_forward;
                showDeleteTipDialog();
                break;
            case R.id.link_card:
                pop.dismiss();
                enterAct(DongtaiAddBusinessCardAct.class);
                break;
            case R.id.link_wish:
                pop.dismiss();
                enterAct(AddLinkWishAct.class);
                break;
            case R.id.link_collect:
                pop.dismiss();
                //enterAct(MyCollectionActivity.class);
                intent = new Intent(this, MyCollectionActivity.class);
                intent.putExtra("mode", MyCollectionActivity.mode_statuses_choose);
                startActivity(intent);

                break;
            case R.id.weibo_pulish:
                InputMethodUtils.hideInputMethod(inputContent);
                WaitingDialog.show(mContext, R.string.waiting);
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, Config.interClickTime);
                if (typeSource == typeForward) {
                    forward();
                } else {
                    pulish();
                }
                break;
            case R.id.tvLink:
                hideInputSoft();
                showLinkWindow();
                break;
            case R.id.window_close:
                // dialog.dismiss();
                break;
            case R.id.window_ok:
                // dialog.dismiss();
                if (type_leave == type_leave_forward) {
                    intent = new Intent(this, DongtaiPulishLongAct.class);
                    startActivity(intent);
                    finish();
                } else if (type_leave == type_leave_back) {
                    finish();
                }
                break;
//            case R.id.window_no:
            // dialog.dismiss();
//                break;
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

        }
    }

    private void hideInputSoft() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // showToast("onDestroy");
        clearUiData();
        EventBus.getDefault().unregister(this);
        Bimp.tempSelectBitmap.clear();
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

    void inits() {
        tvLeftCount = (TextView) findViewById(R.id.tvLeftCount);
        tvLink = (TextView) findViewById(R.id.tvLink);
        linkGroup = findViewById(R.id.link_Lin);
        String value = getString(R.string.add_link_card_collection);
        tvLink.setText(Html.fromHtml(value));
        tvTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
        tvLink.setOnClickListener(this);
        findViewById(R.id.tvLinkDelete1).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete2).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete3).setOnClickListener(this);
        findViewById(R.id.btnPulishLong).setOnClickListener(this);
        btnPulish = (TextView) findViewById(R.id.weibo_pulish);
        btnPulish.setOnClickListener(this);
        inputContent = (EditText) findViewById(R.id.dongtai_content_input);

        final int maxWordSize = Config.remarkContentLength;
        inputContent.addTextChangedListener(new TextListener() {

            @Override
            public void afterTextChanged(Editable edit) {
                String content = edit.toString();
                int size = content.length();
                if (size > maxWordSize) {
                    inputContent.setText(content.substring(0, maxWordSize));
                    inputContent.setSelection(maxWordSize);
                    size = maxWordSize;
                }
                tvLeftCount.setText(size + "/" + maxWordSize);
                checkPulishBtn();
            }
        });
        findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
        // setRightImage(R.drawable.icon_dynamic_long, this);

        gridview = (GridView) findViewById(R.id.noScrollgridview);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    showGetPicWindow();
                } else {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    void checkPulishBtn() {
        String content = inputContent.getText().toString().trim();
        LogUtil.logE(tag, "checkPulishBtn " + content.length());
        if (Bimp.tempSelectBitmap.size() > 0 || !content.equals("") || linkType != 0) {
            ablePulishBtn();
        } else {
            disablePulishBtn();
        }
    }

    void ablePulishBtn() {
        btnPulish.setEnabled(true);
        // btnPulish.setBackgroundResource(R.drawable.common_btn_red);
    }

    void disablePulishBtn() {
        btnPulish.setEnabled(false);
        // btnPulish.setBackgroundColor(getResources().getColor(R.color.theme_milk));
    }

    /**
     * 显示拍照 取图 window
     */
    private void showGetPicWindow() {
        hideInputSoft();
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
                pop.dismiss();
                photo();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                Intent intent = new Intent(mContext, AlbumActivity.class);
                startActivity(intent);
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

    public void photo() {
        fileName = String.valueOf(System.currentTimeMillis());
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.getFile(fileName)));
        startActivityForResult(openCameraIntent, REQUEST_TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    // Bitmap bm = (Bitmap) data.getExtras().get("data");// 得到压缩图
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);
                    opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, Device.getWidth(this), 600);
                    opts.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);
                    bm = BitmapUtil.rotateUpright(bm, FileUtils.getFile(fileName).getAbsolutePath());
                    String path = FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.file = FileUtils.getFile(fileName);
                    takePhoto.setImagePath(path);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    protected void onResume() {
        super.onResume();
        checkPulishBtn();
        adapter.update();
        ViewUtils.setAdapterViewHeight(gridview, 4, 10);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (int i = 0; i < PublicWay.activityList.size(); i++) {
            LogUtil.logE("finish." + i);
            PublicWay.activityList.get(i).finish();
        }
    }

    // 清除ui的数据
    void clearUiData() {
        inputContent.setText("");
        Bimp.tempSelectBitmap.clear();
        Bimp.bitmapSize = 0;
        adapter.notifyDataSetChanged();
    }

    void showDeleteTipDialog() {
        // 判断有没有内容
        if (inputContent.getText().toString().trim().equals("") && Bimp.tempSelectBitmap.size() == 0 && linkType == 0) {
            if (type_leave == type_leave_forward) {
                Intent intent = new Intent(this, DongtaiPulishLongAct.class);
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
                InputMethodUtils.hideInputMethod(inputContent);
                if (type_leave == type_leave_forward) {
                    Intent intent = new Intent(DongtaiPulish.this, DongtaiPulishLongAct.class);
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

    @SuppressLint ("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dongtai_pulish_img_icon));
                holder.image.setScaleType(ScaleType.FIT_XY);
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setScaleType(ScaleType.CENTER_CROP);
                ImageUtils.loadFile(Bimp.tempSelectBitmap.get(position).file, holder.image);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        // 屏蔽以下方法后，没有显示出选中的图
        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.bitmapSize == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.bitmapSize += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }


    //发布按钮的调用
    void pulish() {
        LogUtil.logE("DongtaiPulish", "pulish");
        uploadImgSize = 0;
        attachid = System.currentTimeMillis();
        WaitingDialog.show(this, getString(R.string.waiting), false);
        disablePulishBtn();
        if (Bimp.tempSelectBitmap.size() == 0) {
            pulishContent();
        } else {
            String value = String.format(getString(R.string.sending_d_img), String.valueOf(1));
            WaitingDialog.setContent(value);
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                postImgStream(i);
            }
        }

    }

    /**
     * 发布文字
     */
    private void pulishContent() {
        // WaitingDialog.show(this, getString(R.string.waiting), false);
        disablePulishBtn();
        JSONObject json = new JSONObject();
        try {
            String content = inputContent.getText().toString();
            content = content.trim();
            json.put("content", content);
            json.put("statusestype", Statuses.type_short);
            json.put("imgcount", Bimp.tempSelectBitmap.size());
            json.put("attachid", attachid);
            if (linkType != 0 && linkId != null && !linkId.equals("")) {
                json.put("linktype", linkType);
                json.put("linkid", linkId);
                LogUtil.logE("id:" + linkId + "   type:" + linkType);
            }

        } catch(org.json.JSONException t) {
            // 没有进入到异常
            StackTraceElement[] tracks = t.getStackTrace();
            for (StackTraceElement el : tracks) {
                LogUtil.logE("error:", el.getClassName());
                LogUtil.logE("error:", el.getLineNumber() + "");
            }

        }
        new HttpEntity(this).commonPostJson(Method.weiboPush, json.toString(), this);
    }

    // 转发
    private void forward() {
        disablePulishBtn();
        Params params = new Params();
        params.put("content", inputContent.getText().toString());
        params.put("imgcount", Bimp.tempSelectBitmap.size());
        params.put("statusesid", linkStatuses.getStatusesid());
        new HttpEntity(this).commonPostData(Method.weiboForward, params, this);
    }

    // 流的形式发送 成功
    public void postImgStream(int index) {
        Params params = new Params();
        params.put("attachid", attachid);
        params.put("index", index);
        File temp = null;
        try {
            temp = BitmapUtil.createUploadTempFile(Bimp.tempSelectBitmap.get(index).file, "publish_old_dongtai_temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        params.put("pic", temp);//new
        new HttpEntity(this).commonPostImg(Method.weiboUploadImg, params, this);
    }


    long attachid;

    @Override
    public void onBackPressed() {
        showDeleteTipDialog();
    }

    // 发布成功后续
    public void pulishPostResult() {
        WaitingDialog.dismiss();
        ToastUtil.showToast(R.string.post_success);
        sendBroadCast();
        clearUiData();
        if (typeSource == typeForward) {
            setResult(resultRorwardCode);
        }
        finish();
    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch(methodName) {
                case Method.weiboUploadImg:
                    //uploadImgRespondeSize++;
                    //if (uploadImgRespondeSize == Bimp.tempSelectBitmap.size()) {
                    // showDeleteTip();
                    //}
            }
            WaitingDialog.dismiss();
            ablePulishBtn();
            return;
        }
        hideInputSoft();
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.weiboPush:
                newStatuses = JSONHelper.getObject(obj, Statuses.class);
                statusesid = newStatuses.getStatusesid();
                pulishPostResult();
                break;
            case Method.weiboForward://转发已经不能带图
                newStatuses = JSONHelper.getObject(obj, Statuses.class);
                statusesid = newStatuses.getStatusesid();
                if (Bimp.tempSelectBitmap.size() == 0) {
                    pulishPostResult();
                } else {
                    String value = String.format(getString(R.string.sending_d_img), "1");
                    WaitingDialog.setContent(value);
                    for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                        postImgStream(i);
                        // postImg(i);
                    }
                }

                break;
            case Method.weiboUploadImg:
                uploadImgSize++;
                //uploadImgRespondeSize++;
                if (uploadImgSize == Bimp.tempSelectBitmap.size()) {
                    pulishContent();
                } else {
                    String value = String.format(getString(R.string.sending_d_img), String.valueOf(uploadImgSize + 1));
                    WaitingDialog.setContent(value);
                }
                break;
        }

    }

    //现在不调用。提示动态发布失败，删除微博，数目归0
//    void showDeleteTip() {
//        TipsDialog.popup(this, "微博发布失败", "确定");
//        Params params = new Params();
//        params.put("statusesid", statusesid);
//        new HttpEntity(this).commonPostData(Method.weiboDel, params, this);
//        //uploadImgRespondeSize = 0;
//        uploadImgSize = 0;
//    }

    void sendBroadCast() {
        Intent intent = new Intent("getOneNewStatuses");
        intent.putExtra("statuses", newStatuses);
        sendBroadcast(intent);
    }


}
