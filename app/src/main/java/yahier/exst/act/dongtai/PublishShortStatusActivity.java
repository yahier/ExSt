package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.im.rong.CollectProvider;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.act.mine.NiceLinkActivity;
import com.stbl.stbl.adapter.dongtai.PublishShortStatusImageAdapter;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.dialog.TipsDialog2;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.dongtai.StatusTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.EmojiKeyboard;
import com.stbl.stbl.widget.NestedGridView;
import com.stbl.stbl.widget.RoundImageView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * Created by tnitf on 2016/7/24.
 */
public class PublishShortStatusActivity extends BaseActivity {

    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_CODE_COLLECTION_GOODS = 2;
    private static final int REQUEST_CODE_CARD = 3;
    private static final int REQUEST_CODE_LINK = 4;

    private EmojiconEditText mContentEt;
    private CheckBox mShareCircleCb;
    private CheckBox mShareQzoneCb;
    private NestedGridView mGridView;
    private PublishShortStatusImageAdapter mAdapter;

    private File mPhotoFile;

    private LinkViewHolder mLinkHolder;
    private GoodsViewHolder mGoodsHolder;
    private CardViewHolder mCardHolder;

    private ImageView mDeleteIv;

//    private LoadingDialog mLoadingDialog;
    private LoadingTipsDialog mLoadingDialog;

    private int linktype;
    private String linkid;

    private TextView mPublishTv;

    private ImageView mEmojiIv;
    private EmojiKeyboard mEmojiKeyboard;

    private ArrayList<ImageItem> mTempImageList;

    private int jump_type = 0;
    public final static int JUMP_TYPE_TASK = 1; //解锁抢红包的任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_short_status);
        jump_type = getIntent().getIntExtra("jumptype",0);
        initView();
        ImageUtils.clearMemoryCache();
        if (jump_type == JUMP_TYPE_TASK){
            getDFcontent();
        }
    }

    private void initView() {
        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodUtils.hideInputMethod(mContentEt);
                if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                    mEmojiKeyboard.setVisibility(View.GONE);
                }
                return false;
            }
        });
        mContentEt = (EmojiconEditText) findViewById(R.id.et_content);
        Bimp.tempSelectBitmap.clear();
        mGridView = (NestedGridView) findViewById(R.id.gv_image);
        mTempImageList = new ArrayList<>();
        mAdapter = new PublishShortStatusImageAdapter(Bimp.tempSelectBitmap);
        mGridView.setAdapter(mAdapter);

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mGridView.getWidth();
                int height = width / 4;
                mAdapter.setItemHeight(height);
                mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mAdapter.setInterface(new PublishShortStatusImageAdapter.IAdapter() {
            @Override
            public void onAdd() {
                Intent intent = new Intent(mActivity, AlbumActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDelete(int position) {
                Bimp.tempSelectBitmap.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPreview(int position) {
                Intent intent = new Intent(mActivity, GalleryActivity.class);
                intent.putExtra("ID", position);
                startActivity(intent);
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPublishTv = (TextView) findViewById(R.id.tv_publish);
        mPublishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });

        mShareCircleCb = (CheckBox) findViewById(R.id.iv_share_circle);
        mShareQzoneCb = (CheckBox) findViewById(R.id.iv_share_qzone);

        boolean shareCircle = (Boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_SHARE_TO_CIRCLE, true);//默认分享朋友圈
        boolean shareQzone = (Boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_SHARE_TO_QZONE, false);
        mShareCircleCb.setChecked(shareCircle);
        mShareQzoneCb.setChecked(shareQzone);

        if (jump_type == JUMP_TYPE_TASK && !shareCircle && !shareQzone) {
            if (CommonShare.isWechatInstalled(mActivity)) {
                mShareCircleCb.setChecked(true);
            } else if (CommonShare.isQQInstalled(mActivity)) {
                mShareQzoneCb.setChecked(true);
            }
        }

        mShareCircleCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (jump_type == JUMP_TYPE_TASK && !isShare()){
                    mShareCircleCb.setChecked(true);
                    ToastUtil.showToast("至少选择一个分享");
                    return;
                }
                SharedPrefUtils.putToPublicFile(KEY.IS_SHARE_TO_CIRCLE, isChecked);
            }
        });

        mShareQzoneCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (jump_type == JUMP_TYPE_TASK && !isShare()){
                    mShareQzoneCb.setChecked(true);
                    ToastUtil.showToast("至少选择一个分享");
                    return;
                }
                SharedPrefUtils.putToPublicFile(KEY.IS_SHARE_TO_QZONE, isChecked);
            }
        });

        findViewById(R.id.iv_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AlbumActivity.class);
                startActivity(intent);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        findViewById(R.id.iv_add_photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Bimp.tempSelectBitmap.size() >= 9) {
                    ToastUtil.showToast(R.string.me_most_add_9_photo);
                    return;
                }
                mPhotoFile = new File(FileUtils.getAppDir(), System.currentTimeMillis() + ".jpg");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        findViewById(R.id.iv_add_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, DongtaiAddBusinessCardAct.class);
                startActivityForResult(intent, REQUEST_CODE_CARD);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        findViewById(R.id.iv_add_collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MyCollectionActivity.class);
                intent.putExtra("mode", MyCollectionActivity.mode_statuses_choose);
                startActivityForResult(intent, REQUEST_CODE_COLLECTION_GOODS);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        findViewById(R.id.iv_add_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeAddLink();
                Intent intent = new Intent(mActivity, NiceLinkActivity.class);
                intent.putExtra("userItem", MyApplication.getContext().getUserItem());
                intent.putExtra(KEY.MODE_NICE_LINK, NiceLinkActivity.MODE_CHOOSE);
                startActivityForResult(intent, REQUEST_CODE_LINK);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        mEmojiIv = (ImageView) findViewById(R.id.iv_add_emoji);
        mEmojiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiKeyboard.getVisibility() == View.GONE) {
                    InputMethodUtils.hideInputMethod(mContentEt);
                    mEmojiIv.setImageResource(R.drawable.dongtai_publish_keyboard);
                    mEmojiKeyboard.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmojiKeyboard.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.dongtai_publish_add_emoji);
                    InputMethodUtils.showInputMethod(mContentEt);
                }
            }
        });

        mLinkHolder = new LinkViewHolder();
        mGoodsHolder = new GoodsViewHolder();
        mCardHolder = new CardViewHolder();

        mDeleteIv = (ImageView) findViewById(R.id.iv_delete);
        mDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linktype = 0;
                linkid = null;
                mLinkHolder.hide();
                mGoodsHolder.hide();
                mCardHolder.hide();
                mDeleteIv.setVisibility(View.GONE);
            }
        });

//        mLoadingDialog = new LoadingDialog(this);
//        mLoadingDialog.setCancelable(false);
//        mLoadingDialog.setCanceledOnTouchOutside(false);
//        mLoadingDialog.setMessage(getString(R.string.me_publishing));

        mLoadingDialog = new LoadingTipsDialog(this);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);


        LinearLayout shareLayout = (LinearLayout) findViewById(R.id.layout_share);
        if (CommonShare.isWechatInstalled(this)) {
            shareLayout.setVisibility(View.VISIBLE);
            mShareCircleCb.setVisibility(View.VISIBLE);
        }
        if (CommonShare.isQQInstalled(this)) {
            shareLayout.setVisibility(View.VISIBLE);
            mShareQzoneCb.setVisibility(View.VISIBLE);
        }

        if (shareLayout.getVisibility() == View.VISIBLE) {
            final ImageView shareTipIv = (ImageView) findViewById(R.id.iv_share_tip);
            boolean isFirst = (boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_FIRST_PUBLISH_SHORT_STATUS, true);
            if (isFirst) {
                shareTipIv.setVisibility(View.VISIBLE);
                SharedPrefUtils.putToPublicFile(KEY.IS_FIRST_PUBLISH_SHORT_STATUS, false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing() || shareTipIv == null) {
                            return;
                        }
                        shareTipIv.setVisibility(View.GONE);
                    }
                }, 5000);
            }
        }

        mEmojiKeyboard = (EmojiKeyboard) findViewById(R.id.emoji_keyboard);
        mEmojiKeyboard.setOnEmojiconBackspaceClickedListener(new EmojiKeyboard.OnEmojiconBackspaceClickedListener() {
            @Override
            public void onEmojiconBackspaceClicked(View v) {
                EmojiKeyboard.backspace(mContentEt);
            }
        });

        mEmojiKeyboard.setOnEmojiconClickedListener(new EmojiKeyboard.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                EmojiKeyboard.input(mContentEt, emojicon);
            }
        });

        mContentEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.dongtai_publish_add_emoji);
                }
                return false;
            }
        });
    }
    //获取做任务时的默认动态文本
    private void getDFcontent(){
        mTaskManager.start(StatusTask.getDynamicDFcontent().setCallback(new TaskCallback<String>() {
            @Override
            public void onError(TaskError e) {
            }

            @Override
            public void onSuccess(String result) {
                if (mContentEt != null && mContentEt.getText().toString().equals("")){
                    mContentEt.setText(result);
                }
            }
        }));
    }
    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mContentEt.getText().toString().trim()) && Bimp.tempSelectBitmap.size() == 0 && linktype == 0) {
            super.onBackPressed();
            return;
        }

        TipsDialog.popup(this, R.string.is_giveup_editing, R.string.me_cancel, R.string.me_confirm, new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                finish();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void publish() {
        String content = mContentEt.getText().toString().trim();
        if (TextUtils.isEmpty(content) && Bimp.tempSelectBitmap.size() == 0 && linktype == 0) {
            ToastUtil.showToast(R.string.me_write_something);
            return;
        }
        if (jump_type == JUMP_TYPE_TASK && !isShare()){
            ToastUtil.showToast("至少选择一个分享");
            return;
        }
        mPublishTv.setEnabled(false);
//        mLoadingDialog.show();
        if (Bimp.tempSelectBitmap.size() > 0){
            mLoadingDialog.showLoading("正在上传第1张图");
        }else{
            mLoadingDialog.showLoading(getString(R.string.me_publishing));
        }

        mTaskManager.start(StatusTask.publishShortStatus(Statuses.type_short, content, Bimp.tempSelectBitmap.size(), System.currentTimeMillis(), linktype, linkid)
                .setCallback(new HttpTaskCallback<Statuses>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        mLoadingDialog.dismiss();
                        showErrorTips();
                    }

                    @Override
                    public void onSuccess(Statuses result) {
                        Statuses statuses = result;
//                        ToastUtil.showToast(R.string.post_success);
                        mLoadingDialog.showSuccess(getString(R.string.post_success));
                        if (!isInstalled(SHARE_MEDIA.QQ) && !isInstalled(SHARE_MEDIA.WEIXIN_CIRCLE)){
                            CommonTask.completeMissionCallback(CommonTask.MISSION_TYPE_PUBLISH_STATUS);
                            EventBus.getDefault().post(new EventTypeCommon(EventTypeCommon.typeRefreshWeb));
                        }
                        Intent intent = new Intent("getOneNewStatuses");
                        intent.putExtra("statuses", statuses);
                        intent.putExtra(KEY.IS_SHARE_TO_CIRCLE, mShareCircleCb.isChecked());
                        intent.putExtra(KEY.IS_SHARE_TO_QZONE, mShareQzoneCb.isChecked());
                        if (jump_type == JUMP_TYPE_TASK)
                            intent.putExtra("type_task", true);
                        sendBroadcast(intent);
                        InputMethodUtils.hideInputMethod(mContentEt);
//                        finish();
                        mPublishTv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingDialog.dismiss();
                                finish();
                            }
                        },1500);
                    }

                    @Override
                    public void onMessage(int arg1, int arg2, Object obj) {
                        mLoadingDialog.setMessage(obj.toString());
                    }

                    @Override
                    public void onFinish() {
                        mLoadingDialog.dismiss();
                        mPublishTv.setEnabled(true);
                    }
                }));
    }
    //是否已选择分享，是否可以分享
    private boolean isShare(){
        boolean flag = false;
        if (mShareCircleCb.isChecked() || mShareQzoneCb.isChecked()){
            flag = true;
        }else{
            if (isInstalled(SHARE_MEDIA.QQ) || isInstalled(SHARE_MEDIA.WEIXIN_CIRCLE)){
                flag = false;
            }else{
                flag = true;//没安装微信、QQ，不用分享
            }
        }
        return flag;
    }
    public boolean isInstalled(SHARE_MEDIA media) {
        return UMShareAPI.get(mActivity).isInstall(mActivity, media);
    }
    //发布失败提示框
    private void showErrorTips(){
        TipsDialog2 tipsDialog2 = TipsDialog2.popup(this, getString(R.string.statuses_pulish_faild), getString(R.string.cancel), getString(R.string.statuses_reset_pulish), new TipsDialog2.OnTipsListener() {
            @Override
            public void onConfirm() {
                publish();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Bimp.tempSelectBitmap.size() == 0 && mTempImageList.size() > 0) {
            Bimp.tempSelectBitmap.addAll(mTempImageList);
            mTempImageList.clear();
        }
        mAdapter.notifyDataSetChanged();
    }

    public void beforeAddLink() {
        mTempImageList.clear();
        if (Bimp.tempSelectBitmap.size() > 0) {
            mTempImageList.addAll(Bimp.tempSelectBitmap);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    FileUtils.scanFile(mPhotoFile);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.file = mPhotoFile;
                    takePhoto.setImagePath(mPhotoFile.getAbsolutePath());
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
            case REQUEST_CODE_COLLECTION_GOODS:
                if (resultCode == CollectProvider.resultGoodsOk) {
                    Goods goods = (Goods) data.getSerializableExtra("data");
                    mGoodsHolder.setData(goods);
                }
                break;
            case REQUEST_CODE_CARD:
                if (resultCode == Activity.RESULT_OK) {
                    UserItem user = (UserItem) data.getSerializableExtra("data");
                    mCardHolder.setData(user);
                }
                break;
            case REQUEST_CODE_LINK:
                if (resultCode == Activity.RESULT_OK) {
                    LinkBean link = (LinkBean) data.getSerializableExtra("data");
                    mLinkHolder.setData(link);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
    }

    private class LinkViewHolder {
        RelativeLayout mLayout;
        ImageView mLinkIv;
        TextView mNameTv;
        LinkBean mLink;

        public LinkViewHolder() {
            mLayout = (RelativeLayout) findViewById(R.id.layout_link);
            mLinkIv = (ImageView) findViewById(R.id.iv_link);
            mNameTv = (TextView) findViewById(R.id.tv_link_name);
        }

        public void hide() {
            mLayout.setVisibility(View.GONE);
        }

        public void show() {
            mLayout.setVisibility(View.VISIBLE);
        }

        public void setData(LinkBean link) {
            mLink = link;
            linktype = Statuses.linkTypeNiceLink;
            linkid = String.valueOf(mLink.getLinkid());
            ImageUtils.loadIcon(link.getPiclarurl(), mLinkIv);
            mNameTv.setText(link.getLinktitle());
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, CommonWeb.class);
                    intent.putExtra("url", mLink.getLinkurl());
                    intent.putExtra("title", getString(R.string.me_wonderful_link));
                    startActivity(intent);
                }
            });

            mGoodsHolder.hide();
            mCardHolder.hide();
            show();
            mDeleteIv.setVisibility(View.VISIBLE);
        }
    }

    private class GoodsViewHolder {
        RelativeLayout mLayout;
        ImageView mGoodsIv;
        TextView mNameTv;
        TextView mPriceTv;
        Goods mGoods;

        public GoodsViewHolder() {
            mLayout = (RelativeLayout) findViewById(R.id.layout_goods);
            mGoodsIv = (ImageView) findViewById(R.id.iv_goods);
            mNameTv = (TextView) findViewById(R.id.tv_goods_name);
            mPriceTv = (TextView) findViewById(R.id.tv_goods_price);
        }

        public void setData(Goods goods) {
            mGoods = goods;
            linktype = Statuses.linkTypeGoods;
            linkid = String.valueOf(goods.getGoodsid());
            ImageUtils.loadIcon(goods.getImgurl(), mGoodsIv);
            mNameTv.setText(goods.getGoodsname());
            mPriceTv.setText("¥" + goods.getMinprice());
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, MallGoodsDetailAct.class);
                    intent.putExtra("goodsid", mGoods.getGoodsid());
                    mActivity.startActivity(intent);
                }
            });

            mLinkHolder.hide();
            mCardHolder.hide();
            show();
            mDeleteIv.setVisibility(View.VISIBLE);
        }

        public void show() {
            mLayout.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mLayout.setVisibility(View.GONE);
        }
    }

    private class CardViewHolder {
        LinearLayout mLayout;
        RoundImageView mCardIv;
        TextView mNameTv;
        UserItem mUser;

        public CardViewHolder() {
            mLayout = (LinearLayout) findViewById(R.id.layout_card);
            mCardIv = (RoundImageView) findViewById(R.id.iv_card);
            mNameTv = (TextView) findViewById(R.id.tv_card_name);
        }

        public void show() {
            mLayout.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mLayout.setVisibility(View.GONE);
        }

        public void setData(UserItem user) {
            mUser = user;
            linktype = Statuses.linkTypeCard;
            linkid = String.valueOf(user.getUserid());

            ImageUtils.loadHead(user.getImgmiddleurl(), mCardIv);
            mNameTv.setText(Html.fromHtml(String.format(getString(R.string.me_s_card),
                    user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias())));
//            mNameTv.setText(Html.fromHtml(String.format(getString(R.string.me_s_card), user.getNickname())));
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", mUser.getUserid());
                    startActivity(intent);
                }
            });

            mLinkHolder.hide();
            mGoodsHolder.hide();
            show();
            mDeleteIv.setVisibility(View.VISIBLE);
        }
    }

}
