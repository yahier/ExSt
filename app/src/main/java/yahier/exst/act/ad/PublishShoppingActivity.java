package yahier.exst.act.ad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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

import com.alibaba.fastjson.JSONArray;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.redpacket.SendRedPacketActivity;
import com.stbl.stbl.adapter.dongtai.PublishShortStatusImageAdapter;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.dialog.AlertDialog;
import com.stbl.stbl.dialog.HandlingDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.ShoppingStatus;
import com.stbl.stbl.model.UserAd;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.task.UploadRedPacketImageTask;
import com.stbl.stbl.task.dongtai.StatusTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.widget.EmojiKeyboard;
import com.stbl.stbl.widget.NestedGridView;
import com.stbl.stbl.widget.PublishShoppingLinkPanel;
import com.stbl.stbl.widget.RoundImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/26.
 */

public class PublishShoppingActivity extends BaseActivity {

    private static final int REQUEST_ALBUM = 0;
    private static final int REQUEST_TAKE_PICTURE = 1;
    public static final int REQUEST_CODE_ADD_LINK = 2;
    private static final int REQUEST_CODE_POST_AD = 3;
    private static final int REQUEST_CODE_SEND_REDPACKET = 4;

    private EmojiconEditText mContentEt;
    private CheckBox mShareCircleCb;
    private CheckBox mShareQzoneCb;
    private NestedGridView mGridView;
    private PublishShortStatusImageAdapter mAdapter;

    private File mPhotoFile;

    private LinkViewHolder mLinkHolder;
    private GoodsViewHolder mGoodsHolder;
    private CardViewHolder mCardHolder;
    private AdViewHolder mAdHolder;

    private ImageView mDeleteIv;

    private int linktype;
    private String linkid;

    private TextView mNextTv;

    private ImageView mEmojiIv;
    private EmojiKeyboard mEmojiKeyboard;

    private ImageView mLinkIv;
    private PublishShoppingLinkPanel mLinkPanel;

    private UserAd mUserAd;
    private String mContent;

    private ArrayList<ImageItem> mImageList;

    private UploadRedPacketImageTask mUploadTask;

    private LoadingTipsDialog mTipDialog;

    private HashMap<String, String> mDoneMap;

    private ShoppingCircleDetail adsquareinfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_shopping);
        initView();
        mDoneMap = new HashMap<>();
        mUploadTask = new UploadRedPacketImageTask();
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.ONE_IMAGE_UPLOAD_SUCCESS, ACTION.ALL_IMAGE_UPLOAD_FINISH);
        ImageUtils.clearMemoryCache();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
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
        mImageList = new ArrayList<>();
        mAdapter = new PublishShortStatusImageAdapter(mImageList);
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
                Bimp.tempSelectBitmap.clear();
                Bimp.tempSelectBitmap.addAll(mImageList);
                Intent intent = new Intent(mActivity, AlbumActivity.class);
                startActivityForResult(intent, REQUEST_ALBUM);
                InputMethodUtils.hideInputMethod(mContentEt);
            }

            @Override
            public void onDelete(int position) {
                mImageList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPreview(int position) {
                Bimp.tempSelectBitmap.clear();
                Bimp.tempSelectBitmap.addAll(mImageList);
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

        mNextTv = (TextView) findViewById(R.id.tv_next);
        mNextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep();
            }
        });

        mShareCircleCb = (CheckBox) findViewById(R.id.iv_share_circle);
        mShareQzoneCb = (CheckBox) findViewById(R.id.iv_share_qzone);

        mShareCircleCb.setChecked((Boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_SHARE_TO_CIRCLE, true));//默认分享朋友圈
        mShareQzoneCb.setChecked((Boolean) SharedPrefUtils.getFromPublicFile(KEY.IS_SHARE_TO_QZONE, false));

        mShareCircleCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefUtils.putToPublicFile(KEY.IS_SHARE_TO_CIRCLE, isChecked);
            }
        });

        mShareQzoneCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefUtils.putToPublicFile(KEY.IS_SHARE_TO_QZONE, isChecked);
            }
        });

        findViewById(R.id.iv_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bimp.tempSelectBitmap.clear();
                Bimp.tempSelectBitmap.addAll(mImageList);
                Intent intent = new Intent(mActivity, AlbumActivity.class);
                startActivityForResult(intent, REQUEST_ALBUM);
                InputMethodUtils.hideInputMethod(mContentEt);
            }
        });
        findViewById(R.id.iv_add_photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageList.size() >= 9) {
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
        findViewById(R.id.iv_add_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0) {
                    showAddAdAlertDialog();
                } else {
                    getAdInfo();
                    InputMethodUtils.hideInputMethod(mContentEt);
                }
            }
        });
        mLinkIv = (ImageView) findViewById(R.id.iv_add_link);
        mLinkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.dongtai_publish_add_emoji);
                }
                if (mLinkPanel.getVisibility() == View.GONE) {
                    InputMethodUtils.hideInputMethod(mContentEt);
                    mLinkIv.setImageResource(R.drawable.dongtai_publish_keyboard);
                    mLinkPanel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLinkPanel.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    mLinkPanel.setVisibility(View.GONE);
                    mLinkIv.setImageResource(R.drawable.dongtai_publish_add_link);
                    InputMethodUtils.showInputMethod(mContentEt);
                }
            }
        });
        mEmojiIv = (ImageView) findViewById(R.id.iv_add_emoji);
        mEmojiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLinkPanel.getVisibility() == View.VISIBLE) {
                    mLinkPanel.setVisibility(View.GONE);
                    mLinkIv.setImageResource(R.drawable.dongtai_publish_add_link);
                }
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
        mAdHolder = new AdViewHolder();

        mDeleteIv = (ImageView) findViewById(R.id.iv_delete);
        mDeleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linktype = 0;
                linkid = null;
                mLinkHolder.hide();
                mGoodsHolder.hide();
                mCardHolder.hide();
                mAdHolder.hide();
                mDeleteIv.setVisibility(View.GONE);
            }
        });

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

        mLinkPanel = (PublishShoppingLinkPanel) findViewById(R.id.layout_link_panel);

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
                if (mLinkPanel.getVisibility() == View.VISIBLE) {
                    mLinkPanel.setVisibility(View.GONE);
                    mLinkIv.setImageResource(R.drawable.dongtai_publish_add_link);
                }
                if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.dongtai_publish_add_emoji);
                }
                return false;
            }
        });
        mTipDialog = new LoadingTipsDialog(this);
        mTipDialog.setCanceledOnTouchOutside(false);
    }

    private void showNeedPostAdAlertDialog() {
        AlertDialog.create(mActivity, getString(R.string.me_you_not_yet_post_ad_now_go_post),
                getString(R.string.cancel),
                getString(R.string.me_go_post),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        Intent intent = new Intent(mActivity, PublishAdAct.class);
                        startActivityForResult(intent, REQUEST_CODE_POST_AD);
                    }
                }).show();
    }

    private void showAdReviewingAlertDialog() {
//        AlertDialog.create(mActivity, getString(R.string.me_your_ad_can_be_added_after_review),
//                new AlertDialog.AlertDialogInterface() {
//                    @Override
//                    public void onNegative() {
//
//                    }
//
//                    @Override
//                    public void onPositive() {
//
//                    }
//                }, true).show();
        ToastUtil.showToast(R.string.me_your_ad_can_be_added_after_review);
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mContentEt.getText().toString().trim()) && mImageList.size() == 0 && linktype == 0) {
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

    private void getAdInfo() {
        if (mUserAd != null) {
            addAd();
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        mTaskManager.start(AdTask.getAdInfo()
                .setCallback(new HttpTaskCallback<UserAd>(mActivity) {

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(UserAd result) {
                        mUserAd = result;
                        addAd();
                    }
                }));
    }

    private void addAd() {
        switch (mUserAd.state) {
            case UserAd.STATE_NONE:
                showNeedPostAdAlertDialog();
                break;
            case UserAd.STATE_REVIEWING:
                showAdReviewingAlertDialog();
                break;
            case UserAd.STATE_NORMAL:
                mAdHolder.setData(mUserAd.adview);
                break;
        }
    }

    private void nextStep() {
        InputMethodUtils.hideInputMethod(mContentEt);
        String content = mContentEt.getText().toString().trim();
        if (mImageList.size() == 0) {
            ToastUtil.showToast(R.string.me_add_one_image_at_least);
            return;
        }
        mContent = content;
        ViewUtils.setEnabled(mNextTv);
        ArrayList<String> failList = new ArrayList<>();
        for (ImageItem item : mImageList) {
            String path = item.file.getAbsolutePath();
            if (!mDoneMap.containsKey(path)) {
                failList.add(path);
            }
        }
        if (failList.size() > 0) {
            mTipDialog.showLoading(getString(R.string.me_image_uploading));
            mUploadTask.startTask(failList);
            mUploadTask.allImageUploadFinish();
        } else {
            //所有图片上传成功
            JSONArray imgarr = new JSONArray();
            for (ImageItem item : mImageList) {
                String path = item.file.getAbsolutePath();
                imgarr.add(mDoneMap.get(path));
            }
            publishStatus(imgarr);
        }
    }

    private void showPublishFailAlertDialog(final JSONArray imgarr) {
        AlertDialog dialog = AlertDialog.create(mActivity, getString(R.string.me_status_create_fail),
                getString(R.string.cancel),
                getString(R.string.me_continue_create),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        publishStatus(imgarr);
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showImageUploadFailAlertDialog(final ArrayList<String> failList) {
        AlertDialog dialog = AlertDialog.create(mActivity, getString(R.string.me_image_upload_fail),
                getString(R.string.cancel),
                getString(R.string.me_continue_upload),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        mTipDialog.showLoading(getString(R.string.me_image_uploading));
                        mUploadTask.startTask(failList);
                        mUploadTask.allImageUploadFinish();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ALBUM:
                if (resultCode != RESULT_OK) {
                    return;
                }
                mImageList.clear();
                mImageList.addAll(Bimp.tempSelectBitmap);
                notifyAdapterChange();
                break;
            case REQUEST_TAKE_PICTURE:
                if (mImageList.size() < 9 && resultCode == RESULT_OK) {
                    FileUtils.scanFile(mPhotoFile);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.file = mPhotoFile;
                    takePhoto.setImagePath(mPhotoFile.getAbsolutePath());
                    mImageList.add(takePhoto);
                    notifyAdapterChange();
                }
                break;
            case REQUEST_CODE_ADD_LINK:
                if (resultCode != RESULT_OK) {
                    return;
                }
                switch (data.getIntExtra(KEY.TYPE, -1)) {
                    case PublishShoppingLinkActivity.TYPE_ADD_LINK: {
                        LinkBean link = (LinkBean) data.getSerializableExtra(KEY.LINK_BEAN);
                        mLinkHolder.setData(link);
                    }
                    break;
                    case PublishShoppingLinkActivity.TYPE_NICE_LINK: {
                        LinkBean link = (LinkBean) data.getSerializableExtra(KEY.LINK_BEAN);
                        mLinkHolder.setData(link);
                    }
                    break;
                    case PublishShoppingLinkActivity.TYPE_CARD:
                        UserItem user = (UserItem) data.getSerializableExtra(KEY.USER_ITEM);
                        mCardHolder.setData(user);
                        break;
                    case PublishShoppingLinkActivity.TYPE_GOODS:
                        Goods goods = (Goods) data.getSerializableExtra(KEY.GOODS);
                        mGoodsHolder.setData(goods);
                        break;
                    default:
                        break;
                }
                break;
            case REQUEST_CODE_POST_AD:
                if (resultCode != RESULT_OK) {
                    return;
                }
                mUserAd = null;
                break;
            case REQUEST_CODE_SEND_REDPACKET: {
                if (resultCode != RESULT_OK) {
                    return;
                }
                Intent intent2 = new Intent(ACTION.ADD_SHOPPING_CIRCLE);
                intent2.putExtra("item", adsquareinfo);
                intent2.putExtra(KEY.IS_SHARE_TO_CIRCLE, mShareCircleCb.isChecked());
                intent2.putExtra(KEY.IS_SHARE_TO_QZONE, mShareQzoneCb.isChecked());
                sendBroadcast(intent2);
                finish();
            }
            break;
        }
    }

    private void notifyAdapterChange() {
        mAdapter.notifyDataSetChanged();
        if (mImageList.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (ImageItem item : mImageList) {
                list.add(item.file.getAbsolutePath());
            }
            mUploadTask.startTask(list);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUploadTask.quit();
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
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
            mAdHolder.hide();
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
            mAdHolder.hide();
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
                    TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias())));
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
            mAdHolder.hide();
            show();
            mDeleteIv.setVisibility(View.VISIBLE);
        }
    }

    private class AdViewHolder {
        RelativeLayout mLayout;
        ImageView mAdIv;
        TextView mNameTv;
        Ad mAd;

        public AdViewHolder() {
            mLayout = (RelativeLayout) findViewById(R.id.layout_ad);
            mAdIv = (ImageView) findViewById(R.id.iv_ad);
            mNameTv = (TextView) findViewById(R.id.tv_ad_name);
        }

        public void show() {
            mLayout.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mLayout.setVisibility(View.GONE);
        }

        public void setData(Ad ad) {
            mAd = ad;
            linktype = Statuses.linkTypeAd;
            linkid = String.valueOf(ad.adid);

            ImageUtils.loadBanner(ad.adimgminurl, mAdIv);
            mNameTv.setText(ad.adtitle);
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, CommonWeb.class);
                    intent.putExtra("url", mAd.adurl);
                    intent.putExtra("title", getString(R.string.me_ad_detail));
                    startActivity(intent);
                }
            });

            mLinkHolder.hide();
            mGoodsHolder.hide();
            mCardHolder.hide();
            show();
            mDeleteIv.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION.ONE_IMAGE_UPLOAD_SUCCESS: {
                    String path = intent.getStringExtra(KEY.SDCARD_FILE_PATH);
                    String fileName = intent.getStringExtra(KEY.SERVER_FILE_PATH);
                    mDoneMap.put(path, fileName);
                }
                break;
                case ACTION.ALL_IMAGE_UPLOAD_FINISH: {
                    if (!mTipDialog.isShowing()) {
                        return;
                    }
                    mTipDialog.dismiss();
                    ArrayList<String> failList = new ArrayList<>();
                    for (ImageItem item : mImageList) {
                        String path = item.file.getAbsolutePath();
                        if (!mDoneMap.containsKey(path)) {
                            failList.add(path);
                        }
                    }
                    if (failList.size() > 0) {
                        showImageUploadFailAlertDialog(failList);
                    } else {
                        //所有图片上传成功
                        JSONArray imgarr = new JSONArray();
                        for (ImageItem item : mImageList) {
                            String path = item.file.getAbsolutePath();
                            imgarr.add(mDoneMap.get(path));
                        }
                        publishStatus(imgarr);
                    }
                }
                break;
            }
        }
    };

    private void publishStatus(final JSONArray imgarr) {
        final HandlingDialog dialog = new HandlingDialog(mActivity);
        dialog.show();
        mTaskManager.start(StatusTask.publishRedPacketStatus(mContent, linktype, linkid, imgarr)
                .setCallback(new HttpTaskCallback<ShoppingStatus>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        showPublishFailAlertDialog(imgarr);
                    }

                    @Override
                    public void onSuccess(ShoppingStatus result) {
                        adsquareinfo = result.adsquareinfo;
                        Intent i = new Intent(mActivity, SendRedPacketActivity.class);
                        i.putExtra(KEY.SQUARE_ID, adsquareinfo.getSquareid());
                        startActivityForResult(i, REQUEST_CODE_SEND_REDPACKET);
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                }));
    }

    private void showAddAdAlertDialog() {
        AlertDialog dialog = AlertDialog.create(mActivity,
                getString(R.string.me_publish_redpacket_add_ad_tip),
                getString(R.string.cancel),
                getString(R.string.me_go_open),
                new AlertDialog.AlertDialogInterface() {
                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onPositive() {
                        mActivity.startActivity(new Intent(mActivity, IntroduceAdAct.class));
                        finish();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
