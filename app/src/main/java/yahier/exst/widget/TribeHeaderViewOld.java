package yahier.exst.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.BrandPlusActivity;
import com.stbl.stbl.act.dongtai.DongtaiRankAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.mine.MineAttenListAct;
import com.stbl.stbl.act.mine.MineDongtai;
import com.stbl.stbl.act.mine.MineFansListAct;
import com.stbl.stbl.act.mine.MineSignAct;
import com.stbl.stbl.act.mine.MineTudiListAct;
import com.stbl.stbl.act.mine.MyGiftActivity;
import com.stbl.stbl.act.mine.MyLinkActivity;
import com.stbl.stbl.act.mine.NiceLinkActivity;
import com.stbl.stbl.act.mine.OtherGiftActivity;
import com.stbl.stbl.act.mine.PhotoImagePagerAct;
import com.stbl.stbl.act.mine.SignHelpMeAct;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BigChief;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.Level;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.item.Masters;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.SignListResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.TribeAllItem;
import com.stbl.stbl.item.TribeGift;
import com.stbl.stbl.item.TribeUserItem;
import com.stbl.stbl.item.TribeUserView;
import com.stbl.stbl.item.UserCount;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.VoicePlayer;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */

public class TribeHeaderViewOld extends LinearLayout implements View.OnClickListener {

    long userId;

    TextView tvMainItem1, tvMainItem2, tvMainItem3;
    ImageView imgUser;
    ImageView mBlurBgIv;
    TextView tv_count_tudi, tv_count_attention, tv_count_fans;
    TextView tvSignature;
    TextView tvLinkCount, tvStatusesCount;

    Button btnSignHelp;
    LinearLayout linGift;

    TextView tvLevelWealthTitle, tvLevelWealthName;
    TextView tvLevelPeopleTitle, tvLevelPeopleName;
    ProgressBar progressBar1, progressBar2;
    Level level;
    ImageView mPlayIv;

    ImageView imgZongshi;
    ImageView imgZongshiMaster;
    TextView tvRelation;

    TextView tvTipGift, tvTipSign, tvTipAlbum;

    View imgZongshiTagFill;

    private TribeMainAct mActivity;

    private VoicePlayer mPlayer;

    final int maxPhotoSize = 5;

    TextView tvname;
    private View imgAuthorized;
    private TextView tvAge;
    private TextView tvLocation;
    private ImageView ivGender;//性别

    TribeUserItem userItem;
    Masters elders;

    private ImageView mMainAdIv;
    private TextView mMainAdTitle, tvAdBusinessType;

    private LinearLayout layoutHelpSignUser;

    public TribeHeaderViewOld(Context context, long userId) {
        this(context, null);
        this.userId = userId;
    }

    public TribeHeaderViewOld(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TribeHeaderViewOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (TribeMainAct) context;
        LayoutInflater.from(context).inflate(R.layout.header_tribe_old, this);
        initView();
    }

    private void initView() {
        imgZongshiTagFill = findViewById(R.id.imgZongshiTagFill);
        mPlayIv = (ImageView) findViewById(R.id.iv_play);
        mPlayIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean playing = mPlayer.pauseOrResume();
                mPlayIv.setImageResource(playing ? R.drawable.ic_pause_voice : R.drawable.ic_play_voice);
            }
        });
        mPlayer = new VoicePlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayIv.setVisibility(View.VISIBLE);
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayIv.setImageResource(R.drawable.ic_play_voice);
            }
        });

        imgUser = (ImageView) findViewById(R.id.imgUser);
        mBlurBgIv = (ImageView) findViewById(R.id.iv_blur_bg);

        tvRelation = (TextView) findViewById(R.id.tvRelation);
        imgZongshiMaster = (ImageView) findViewById(R.id.imgZongshiMaster);
        imgZongshi = (ImageView) findViewById(R.id.imgZongshi);

        tvname = (TextView) findViewById(R.id.tvName);

        tvAge = (TextView) findViewById(R.id.tv_age);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        ivGender = (ImageView) findViewById(R.id.iv_gender);

        tvTipGift = (TextView) findViewById(R.id.tvTipGift);
        tvTipAlbum = (TextView) findViewById(R.id.tvTipAlbum);
        tvTipSign = (TextView) findViewById(R.id.tvTipSign);
        tvLinkCount = (TextView) findViewById(R.id.tvLinkCount);
        tvStatusesCount = (TextView) findViewById(R.id.tvStatusesCount);
        tvSignature = (TextView) findViewById(R.id.tvSignature);
        // 等级
        tvLevelPeopleTitle = (TextView) findViewById(R.id.tvLevelPeopleTitle);
        tvLevelWealthName = (TextView) findViewById(R.id.tvLevelWealthName);
        tvLevelWealthTitle = (TextView) findViewById(R.id.tvLevelWealthTitle);
        tvLevelPeopleName = (TextView) findViewById(R.id.tvLevelPeopleName);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        // 徒弟 关注 粉丝数
        tv_count_tudi = (TextView) findViewById(R.id.tv_count_tudi);
        tv_count_attention = (TextView) findViewById(R.id.tv_count_attention);
        tv_count_fans = (TextView) findViewById(R.id.tv_count_fans);
        // 上面五个item
        btnSignHelp = (Button) findViewById(R.id.btnSignHelp);

        tvMainItem1 = (TextView) findViewById(R.id.dongtai_main_item1);
        tvMainItem2 = (TextView) findViewById(R.id.dongtai_main_item2);
        tvMainItem3 = (TextView) findViewById(R.id.dongtai_main_item3);
        linGift = (LinearLayout) findViewById(R.id.linGift);

        findViewById(R.id.lin_tudi).setOnClickListener(this);
        findViewById(R.id.lin_attend).setOnClickListener(this);
        findViewById(R.id.lin_fans).setOnClickListener(this);
        imgUser.setOnClickListener(this);

        layoutHelpSignUser = (LinearLayout) findViewById(R.id.layoutHelpSignUser);

        mMainAdIv = (ImageView) findViewById(R.id.iv_main_ad_cover);
        mMainAdTitle = (TextView) findViewById(R.id.tv_main_ad_title);
        tvAdBusinessType = (TextView) findViewById(R.id.tvAdBusinessType);

    }

    void setUserValue(final UserItem user) {
        tvname.setText(user.getAlias() != null && !user.getAlias().equals("") ? user.getAlias() : user.getNickname());

        if (user.getAge() == 0) {
            tvAge.setVisibility(View.GONE);
        } else {
            tvAge.setVisibility(View.VISIBLE);
            tvAge.setText(String.format(MyApplication.getContext().getString(R.string.im_age), user.getAge()));
        }
        ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
            ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
            ivGender.setImageResource(R.drawable.icon_female);
        } else {
            ivGender.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(user.getCityname())) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(user.getCityname());
        }

        getUserImgOriginal(user.getImgmiddleurl());
    }

    /**
     * 获取用户头像原图
     */
    private void getUserImgOriginal(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(MyApplication.getContext()).load(url)
                    .asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (bitmap == null) {
                        return;
                    }
                    mActivity.mAminBitmp = bitmap;
                    ImageUtils.fastBlur(userItem.getImgmiddleurl(), mBlurBgIv);
                }
            });
        }
    }


    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, Config.interClickTime);
        Intent intent;
        switch (v.getId()) {
            // 顶部5个
            case R.id.dongtai_main_item1:
                intent = new Intent(mActivity, TribeMainAct.class);
                intent.putExtra("userId", elders.getHeadmenview().getUserid());
                mActivity.startActivity(intent);
                mActivity.finish();
                break;
            case R.id.dongtai_main_item2:
                intent = new Intent(mActivity, TribeMainAct.class);
                intent.putExtra("userId", elders.getElderview().getUserid());
                mActivity.startActivity(intent);
                mActivity.finish();
                break;
            case R.id.dongtai_main_item3:
                int tag = (int) v.getTag();
                if (tag == Masters.qubaishiNo) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", elders.getMasterview().getUserid());
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
                break;
            case R.id.dongtai_main_item4:
                intent = new Intent(mActivity, DongtaiRankAct.class);
                mActivity.startActivity(intent);
                break;
            case R.id.dongtai_main_item5:
                intent = new Intent(mActivity, BrandPlusActivity.class);
                mActivity.startActivity(intent);
                break;
            case R.id.linGift:// 进入礼盒页面
                if (SharedToken.getUserId().equals(String.valueOf(userItem.getUserid()))) {
                    intent = new Intent(mActivity, MyGiftActivity.class);
                    mActivity.startActivity(intent);
                } else {
                    intent = new Intent(mActivity, OtherGiftActivity.class);
                    intent.putExtra(EXTRA.USER_ITEM, userItem);
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.layoutHelpSignUser: {
                intent = new Intent(mActivity, SignHelpMeAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
            }
            break;
            case R.id.btnSignHelp:// 帮助签到
                intent = new Intent(mActivity, MineSignAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.linStatuses:
                intent = new Intent(mActivity, MineDongtai.class);
                intent.putExtra("userItem", userItem);
                mActivity.startActivity(intent);
                break;
            case R.id.linLinks:
                if (SharedToken.getUserId().equals(String.valueOf(userItem.getUserid()))) {
                    intent = new Intent(mActivity, MyLinkActivity.class);
                    mActivity.startActivity(intent);
                } else {
                    intent = new Intent(mActivity, NiceLinkActivity.class);
                    intent.putExtra("userItem", userItem);
                    mActivity.startActivity(intent);
                }
                break;

            case R.id.imgZongshiMaster:
                if (userItem.getBigchiefuserid() != 0) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", userItem.getBigchiefuserid());
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }
                break;
            case R.id.lin_tudi:
                intent = new Intent(mActivity, MineTudiListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.lin_attend:
                intent = new Intent(mActivity, MineAttenListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.lin_fans:
                intent = new Intent(mActivity, MineFansListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.imgUser:
                if (mActivity.mAminBitmp == null) {
                    if (userItem != null)
                        getUserImgOriginal(userItem.getImgurloriginal());
                } else {
                    mActivity.zoomImageFromThumb(imgUser, mActivity.mAminBitmp);
                }
                break;
            case R.id.iv_main_ad_cover:
                if (ad == null) return;
                UmengClickEventHelper.onTribeMeClickEvent(mActivity);

                intent = new Intent(mActivity, CommonWeb.class);
                intent.putExtra("ad", ad);
                intent.putExtra("type", CommonWeb.typeAd);
                mActivity.startActivity(intent);
                break;
            case R.id.tvAdBusinessType:
                if (ad == null) return;
                AdHelper.toApplyAdCooperateAct(ad, mActivity);
                break;
        }
    }

    public void setOnClick() {
        imgZongshiMaster.setOnClickListener(this);
        findViewById(R.id.dongtai_main_item4).setOnClickListener(this);
        findViewById(R.id.dongtai_main_item5).setOnClickListener(this);
        linGift.setOnClickListener(this);
        layoutHelpSignUser.setOnClickListener(this);
        btnSignHelp.setOnClickListener(this);

        findViewById(R.id.linLinks).setOnClickListener(this);
    }

    public void updateTribeData(final TribeAllItem tribe, TribeUserView userView, TribeUserItem user, int relationFlag) {
        userItem = user;
        //判断是否认证
        if (userItem.getCertification() == UserItem.certificationYes) {
            findViewById(R.id.imgAuthorized).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.imgAuthorized).setVisibility(View.GONE);
        }
        //父类大酋长信息
        BigChief bigChief = userItem.getBigchiefinfo();
        if (bigChief != null) {
            imgZongshiMaster.setVisibility(View.VISIBLE);
            PicassoUtil.load(mActivity, bigChief.getImgurl(), imgZongshiMaster, R.drawable.icon_zongshi);
        }

        //当前用户的大酋长信息
        BigChief cbigChief = userItem.getCbigchiefinfo();
        if (cbigChief != null) {
            findViewById(R.id.imgZongShiBg).setVisibility(View.VISIBLE);
            imgZongshi.setVisibility(View.VISIBLE);
            imgZongshiTagFill.setVisibility(View.VISIBLE);
            PicassoUtil.load(mActivity, cbigChief.getImgurl(), imgZongshi, R.drawable.icon_zongshi);
        }

        int accountType = userItem.getAccounttype();
        switch (accountType) {
            //系统账号
            case UserItem.accountTypeSystem:
                findViewById(R.id.linCount).setVisibility(View.GONE);
                findViewById(R.id.linLevel).setVisibility(View.GONE);
                findViewById(R.id.linHelpSign).setVisibility(View.GONE);
                findViewById(R.id.lineBelowLevel).setVisibility(View.GONE);
                break;
            case UserItem.accountTypeTeam:
                tvAge.setVisibility(View.GONE);
                tvLocation.setVisibility(View.GONE);
                ivGender.setVisibility(View.GONE);
                break;
        }

        setUserValue(userItem);
        tvSignature.setText(userItem.getSignature());
        ImageUtils.loadCircleHead(userItem.getImgurl(), imgUser);
        downloadRecordVoice(userItem.getPhonetic());

        // 数据和等级
        level = userView.getLevel();
        if (level != null) {
            tvLevelWealthTitle.setText(level.getLevelrichtitle());
            tvLevelWealthName.setText(Res.getString(R.string.wealth_level) + ":" + level.getLevelrichname());
            tvLevelPeopleTitle.setText(level.getLevelcontacttitle());
            tvLevelPeopleName.setText(Res.getString(R.string.pelple_resource_level) + ":" + level.getLevelcontactname());
            int progressWealth = level.getLevelrichcur() * 100 / (level.getLevelrichcur() + level.getLevelrichnext());
            progressBar1.setProgress(progressWealth);
            int progressPeople = level.getLevelcontactcur() * 100 / (level.getLevelcontactnext() + level.getLevelcontactcur());
            progressBar2.setProgress(progressPeople);
        }

        UserCount count = tribe.getCountview();
        tv_count_attention.setText(String.valueOf(count.getAttention_count()));
        tv_count_tudi.setText(String.valueOf(count.getTudi_count()));
        tv_count_fans.setText(String.valueOf(count.getFans_count()));

        //判断师傅关系
        if (Relation.isMaster(relationFlag)) {
            tvRelation.setText(UserItem.relationTypeMaster);
            tvRelation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_master, 0, 0, 0);
        } else if (Relation.isStu(relationFlag)) {
            tvRelation.setText(UserItem.relationTypeStudent);
            tvRelation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_student, 0, 0, 0);
        }

        // 礼物
        List<TribeGift> tribeListGift = tribe.getGiftsview();
        if (tribeListGift != null && tribeListGift.size() > 0) {
            int margin = DimenUtils.dp2px(16);
            int width = (Device.getWidth() - DimenUtils.dp2px(120)) / 5;
            int maxWidth = DimenUtils.dp2px(45);
            if (width > maxWidth) {
                width = maxWidth;
            }
            findViewById(R.id.linGiftAll).setVisibility(View.VISIBLE);
            int size = Math.min(5, tribeListGift.size());
            for (int i = 0; i < size; i++) {
                LinearLayout giftView = new LinearLayout(mActivity);
                giftView.setOrientation(LinearLayout.VERTICAL);
                giftView.setGravity(Gravity.CENTER_HORIZONTAL);
                LayoutParams giftParams = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
                giftParams.setMargins(0, 0, margin, 0);
                linGift.addView(giftView, i, giftParams);

                Gift gift = tribeListGift.get(i).getGiftinfo();
                ImageView iv = new ImageView(mActivity);
                LayoutParams params = new LayoutParams(width, width);
                giftView.addView(iv, params);
                ImageUtils.loadSquareImage(gift.getGiftimg(), iv);

                TextView tv = new TextView(mActivity);
                tv.setTextColor(getResources().getColor(R.color.f_gray));
                tv.setTextSize(10);
                tv.setMaxLines(1);
                giftView.addView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                tv.setText(gift.getGiftname());
            }
        }

        // 签到
        ArrayList<UserItem> listUser = (ArrayList<UserItem>) tribe.getProxysigninview();
        if (tribe.getIssignin() == SignListResult.issignYes) {
            btnSignHelp.setText(R.string.signed);
        }
        updateSignInList(listUser);

        // 图片
        // 如果不是自己
        if (!SharedToken.getUserId().equals(String.valueOf(userId))) {
            tvTipAlbum.setText(Res.getString(R.string.its_ablum) + "(" + tribe.getUserphotocount() + ")");
        } else {
            tvTipAlbum.setText(Res.getString(R.string.my_album) + "(" + tribe.getUserphotocount() + ")");
        }
        final List<Photo> tribeListPhoto = tribe.getUserphotoview();
        if (tribeListPhoto != null && tribeListPhoto.size() > 0) {
            findViewById(R.id.linPhotoAll).setVisibility(View.VISIBLE);
            LinearLayout linPhoto = (LinearLayout) findViewById(R.id.photo_lin);
            int margin = DimenUtils.dp2px(10);
            int width = (Device.getWidth() - DimenUtils.dp2px(100)) / 5;
            int maxWidth = DimenUtils.dp2px(60);
            if (width > maxWidth) {
                width = maxWidth;
            }
            int size = Math.min(tribeListPhoto.size(), 5);
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mActivity);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LayoutParams params = new LayoutParams(width, width);
                params.setMargins(0, 0, margin, 0);
                linPhoto.addView(iv, params);
                final int j = i;
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mActivity, PhotoImagePagerAct.class);
                        intent.putExtra("index", j);
                        intent.putExtra("userItem", userItem);
                        intent.putExtra("isShowGridIcon", true);
                        intent.putExtra("totalcount", tribe.getUserphotocount());
                        intent.putParcelableArrayListExtra("photo", (ArrayList<? extends Parcelable>) tribeListPhoto);
                        mActivity.startActivity(intent);
                    }
                });
                ImageUtils.loadSquareImage(tribeListPhoto.get(i).getMiddleurl(), iv);
            }
        }
        tvLinkCount.setText(String.valueOf(tribe.getUserlinksnum()));
        tvStatusesCount.setText(String.valueOf(tribe.getUserstatusesnum()));
        // 链接
        List<LinkBean> listLink = tribe.getLinksview();
        if (listLink != null && listLink.size() > 0) {
            findViewById(R.id.linkRight).setVisibility(View.VISIBLE);
            findViewById(R.id.tvLinksNone).setVisibility(View.GONE);
            findViewById(R.id.linLinks).setVisibility(View.VISIBLE);
            findViewById(R.id.linLinksPhoto).setVisibility(View.VISIBLE);

            LinearLayout linLinksPhoto = (LinearLayout) findViewById(R.id.linLinksPhoto);
            int margin = DimenUtils.dp2px(8);
            int width = Device.getWidth() / 8;
            int maxWidth = DimenUtils.dp2px(50);
            if (width > maxWidth) {
                width = maxWidth;
            }
            int size = Math.min(listLink.size(), 4);
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mActivity);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LayoutParams params = new LayoutParams(width, width);
                params.setMargins(0, 0, margin, 0);
                linLinksPhoto.addView(iv, params);
                ImageUtils.loadSquareImage(listLink.get(i).getPicminurl(), iv);
            }
        } else {
            findViewById(R.id.linLinks).setVisibility(View.GONE);
            findViewById(R.id.linLinks).setOnClickListener(null);
            findViewById(R.id.tvLinksNone).setVisibility(View.VISIBLE);
            findViewById(R.id.linLinksPhoto).setVisibility(View.GONE);
        }
        // 动态
        Statuses statuses = tribe.getLateststatusesview();
        if (statuses != null) {
            findViewById(R.id.linStatusesYes).setVisibility(View.VISIBLE);
            findViewById(R.id.linStatuses).setVisibility(View.VISIBLE);
            findViewById(R.id.linStatuses).setOnClickListener(this);
            EmojiconTextView tvStatusesContent = (EmojiconTextView) findViewById(R.id.tvStatusesContent);
            TextView tvStatusesTime = (TextView) findViewById(R.id.tvStatusesTime);
            ImageView imgStatuses = (ImageView) findViewById(R.id.imgStatusesImg);
            String content = statuses.getTitle();
            if (content == null || content.equals("")) {
                content = statuses.getContent();
            }
            tvStatusesContent.setText(content);
            tvStatusesTime.setText(DateUtil.getTimeOff(statuses.getCreatetime()));
            StatusesPic link3Pics = statuses.getStatusespic();
            if (link3Pics.getPics().size() > 0) {
                String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
                //如果是长动态，就取封面图
                if (statuses.getStatusestype() == Statuses.type_long) {
                    imgUrl = link3Pics.getMiddlepic() + link3Pics.getDefaultpic();
                }
                int widthHeight = (int) getResources().getDimension(R.dimen.list_head_img_width_height);
                PicassoUtil.loadStatusesCropCenter(mActivity, imgUrl, widthHeight, widthHeight, imgStatuses);
            } else {

                PicassoUtil.load(mActivity, link3Pics.getEx(), imgStatuses);
            }
        } else {
            findViewById(R.id.linStatuses).setVisibility(View.GONE);
            findViewById(R.id.linStatusesYes).setVisibility(View.GONE);
            findViewById(R.id.tvStatusesNone).setVisibility(View.VISIBLE);
        }
    }

    public void updateEldersInfo(Masters e) {
        elders = e;
        if (elders != null) {
            if (elders.getHeadmenview() != null) {
                tvMainItem1.setOnClickListener(this);
                tvMainItem1.setBackgroundResource(R.drawable.dongtai_main_top_itembg);
            } else {
                tvMainItem1.setOnClickListener(null);
                tvMainItem1.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
            }
            if (elders.getElderview() != null) {
                tvMainItem2.setOnClickListener(this);
                tvMainItem2.setBackgroundResource(R.drawable.dongtai_main_top_itembg);
            } else {
                tvMainItem2.setOnClickListener(null);
                tvMainItem2.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
            }
            if (elders.getMasterview() != null) {
                tvMainItem3.setOnClickListener(this);
                tvMainItem3.setBackgroundResource(R.drawable.dongtai_main_top_itembg);
                tvMainItem3.setTag(Masters.qubaishiNo);
            } else {
                tvMainItem3.setOnClickListener(null);
                // 如果没有师傅，也不是酋长就可以点击
                if (elders.getIsheadmen() == Masters.isheadmenNo) {
                    tvMainItem3.setOnClickListener(null);//先不拜师
                    tvMainItem3.setTag(Masters.qubaishiYes);
                }
                tvMainItem3.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
            }
        } else {
            tvMainItem1.setOnClickListener(null);
            tvMainItem2.setOnClickListener(null);
            tvMainItem3.setOnClickListener(null);
            tvMainItem1.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
            tvMainItem2.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
            tvMainItem3.setBackgroundResource(R.drawable.dongtai_main_top_itembg_unclickable);
        }
    }

    public void updateNick(String alias) {
        tvname.setText(TextUtils.isEmpty(alias) ? userItem.getNickname() : alias);
    }

    public void updateSignStatus() {
        btnSignHelp.setText(Res.getString(R.string.me_already_sign_in));
    }

    public void onPlayerStop() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mPlayIv.setImageResource(R.drawable.ic_play_voice);
        }
    }

    public void updateSignInList(ArrayList<UserItem> userList) {
        layoutHelpSignUser.removeAllViews();
        if (userList != null && userList.size() > 0) {
            int margin = DimenUtils.dp2px(16);
            int width = (layoutHelpSignUser.getWidth() - margin * 5) / 5;
            int maxWidth = DimenUtils.dp2px(36);
            if (width > maxWidth) {
                width = maxWidth;
            }
            int size = Math.min(userList.size(), 5);
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mActivity);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LayoutParams params = new LayoutParams(width, width);
                params.setMargins(0, 0, margin, 0);
                layoutHelpSignUser.addView(iv, params);
                ImageUtils.loadCircleHead(userList.get(i).getImgurl(), iv);
            }
        } else {
            TextView tv = new TextView(mActivity);
            tv.setText(getResources().getString(R.string.me_no_people_help_sign_in));
            tv.setTextColor(getResources().getColor(R.color.f_gray));
            tv.setTextSize(12);
            layoutHelpSignUser.addView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void downloadRecordVoice(String voiceUrl) {
        LogUtil.logE("voiceUrl:" + voiceUrl);
        if (!TextUtils.isEmpty(voiceUrl)) {
            mPlayer.downloadOnlineVoice(voiceUrl, new VoicePlayer.OnDownloadSuccessListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    setPlayerDataSource(path);
                }
            });
        }
    }

    private void setPlayerDataSource(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Ad ad;

    public void updateMainAd(Ad ad) {
        this.ad = ad;
        mMainAdIv.getLayoutParams().height = (Device.getWidth() - DimenUtils.dp2px(12)) / 2;
        findViewById(R.id.layout_main_ad).setVisibility(VISIBLE);
        ImageUtils.loadImage(ad.adimgminurl, mMainAdIv);
        mMainAdIv.setOnClickListener(this);
        mMainAdTitle.setText(ad.adtitle);

        if (ad.businessclass == Ad.businessclassNone) {
            tvAdBusinessType.setVisibility(View.GONE);
        } else {
            tvAdBusinessType.setVisibility(View.VISIBLE);
            tvAdBusinessType.setText(ad.businessclassname);
            tvAdBusinessType.setOnClickListener(this);
        }

    }

    public void updateTudiAd() {
        findViewById(R.id.layout_tudi_ad).setVisibility(VISIBLE);
    }

    public void setSeftText() {
        btnSignHelp.setText(R.string.sign);
        tvTipGift.setText(R.string.my_gift_given);
        tvTipSign.setText(R.string.people_recent_help_me_signing);
        tvTipAlbum.setText(R.string.my_album);
    }

    public void setOtherText() {
        tvTipGift.setText(R.string.gift_given);
        tvTipSign.setText(R.string.people_recent_help_signing);
        tvTipAlbum.setText(R.string.its_ablum);
    }

    public void setSignText() {
        btnSignHelp.setText(R.string.sign);
    }
}
