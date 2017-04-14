package yahier.exst.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.BrandPlusActivity;
import com.stbl.stbl.act.ad.TribeTudiAdActivity;
import com.stbl.stbl.act.dongtai.DongtaiRankAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.mine.MineAttenListAct;
import com.stbl.stbl.act.mine.MineDongtai;
import com.stbl.stbl.act.mine.MineFansListAct;
import com.stbl.stbl.act.mine.MineSignAct;
import com.stbl.stbl.act.mine.MineTudiListAct;
import com.stbl.stbl.act.mine.MyDongtaiActivity;
import com.stbl.stbl.act.mine.MyGiftActivity;
import com.stbl.stbl.act.mine.MyLinkActivity;
import com.stbl.stbl.act.mine.NiceLinkActivity;
import com.stbl.stbl.act.mine.OtherAlbumActivity;
import com.stbl.stbl.act.mine.OtherGiftActivity;
import com.stbl.stbl.act.mine.PhotoImagePagerAct;
import com.stbl.stbl.act.mine.SignAct;
import com.stbl.stbl.act.mine.SignHelpMeAct;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BigChief;
import com.stbl.stbl.item.DynamicNew;
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
import com.stbl.stbl.item.UserRole;
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
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.VoicePlayer;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */

public class TribeHeaderView extends LinearLayout implements View.OnClickListener {

    long userId;

    ImageView imgUser;
    //ImageView mBlurBgIv;
    TextView tv_count_tudi, tv_count_attention, tv_count_fans;
    TextView tvSignature;
    TextView tvStatusesCount, tvAlbumCount, tvGiftCount;

    TextView btnSignHelp;
    LinearLayout linGift;

    //TextView tvLevelWealthTitle, tvLevelWealthName;
    //TextView tvLevelPeopleTitle, tvLevelPeopleName;
    //ProgressBar progressBar1, progressBar2;
    //Level level;
    //ImageView mPlayIv;

    ImageView imgZongshi;
    ImageView imgZongshiMaster;
    //TextView tvRelation;

    TextView tvTipGift, tvTipAlbum;

    View linConnectLink;

    private TribeMainAct mActivity;

    private VoicePlayer mPlayer;

    final int maxPhotoSize = 5;

    TextView tvname;
    //private TextView tvAge;
    //private TextView tvLocation;
    //private ImageView ivGender;//性别

    private TextView tvUserInfo;

    TribeUserItem userItem;
    Masters elders;

    private ImageView mMainAdIv;
    private TextView mMainAdTitle, tvAdBusinessType;
    private ImageView ivAdLogo;//广告logo

    private LinearLayout layoutHelpSignUser;
    private View linTop;
    private ImageView imgQiuzhang, imgZhanglao, imgShifu;
    private View linQiuzhang, linZhanglao, linShifu;
    private View linGiftAll, linPhotoAll, linStatuses, linLinks;
    private ImageView imgVoice;
    private View linSign;
    private TextView tvLinkCount;
    View linHeader;
    View viewDividerSign;
    TextView tvShifuLabel, tvTudiLabel;

    //点击动态跳转的类型
    private int mDynamicType = DynamicNew.TYPE_DYNAMIC;

    public TribeHeaderView(Context context, long userId) {
        this(context, null);
        this.userId = userId;
    }

    public TribeHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TribeHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (TribeMainAct) context;
        LayoutInflater.from(context).inflate(R.layout.header_tribe, this);
        initView();
    }

    private void initView() {
        tvShifuLabel = (TextView) findViewById(R.id.tvShifuLabel);
        tvTudiLabel = (TextView) findViewById(R.id.tvTudiLabel);
        viewDividerSign = findViewById(R.id.viewDividerSign);
        linHeader = findViewById(R.id.linHeader);
        linConnectLink = findViewById(R.id.linConnectLink);
        tvGiftCount = (TextView) findViewById(R.id.tvGiftCount);
        tvLinkCount = (TextView) findViewById(R.id.tvLinkCount);
        linSign = findViewById(R.id.linSign);
        imgVoice = (ImageView) findViewById(R.id.imgVoice);
        linPhotoAll = findViewById(R.id.linPhotoAll);
        linStatuses = findViewById(R.id.linStatuses);
        linLinks = findViewById(R.id.linLinks);


        linGiftAll = findViewById(R.id.linGiftAll);
        linTop = findViewById(R.id.linTop);


        //imgZongshiTagFill = findViewById(R.id.imgZongshiTagFill);
        imgVoice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean playing = mPlayer.pauseOrResume();
                imgVoice.setImageResource(playing ? R.drawable.tribe_voice_start : R.drawable.tribe_voice_pause);
            }
        });
        mPlayer = new VoicePlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                imgVoice.setVisibility(View.VISIBLE);
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgVoice.setImageResource(R.drawable.tribe_voice_start);
            }
        });

        imgUser = (ImageView) findViewById(R.id.imgUser);
        //mBlurBgIv = (ImageView) findViewById(R.id.iv_blur_bg);

        //tvRelation = (TextView) findViewById(R.id.tvRelation);
        imgZongshiMaster = (ImageView) findViewById(R.id.imgZongshiMaster);
        imgZongshi = (ImageView) findViewById(R.id.imgZongshi);

        tvname = (TextView) findViewById(R.id.tvName);


        tvUserInfo = (TextView) findViewById(R.id.tvUserInfo);
        //tvAge = (TextView) findViewById(R.id.tv_age);
        //tvLocation = (TextView) findViewById(R.id.tv_location);
        //ivGender = (ImageView) findViewById(R.id.iv_gender);

        linQiuzhang = findViewById(R.id.linQiuzhang);
        linZhanglao = findViewById(R.id.linZhanglao);
        linShifu = findViewById(R.id.linShifu);

        imgQiuzhang = (ImageView) findViewById(R.id.imgQiuzhang);
        imgZhanglao = (ImageView) findViewById(R.id.imgZhanglao);
        imgShifu = (ImageView) findViewById(R.id.imgShifu);


        tvTipGift = (TextView) findViewById(R.id.tvTipGift);
        tvTipAlbum = (TextView) findViewById(R.id.tvTipAlbum);
        tvStatusesCount = (TextView) findViewById(R.id.tvStatusesCount);
        tvAlbumCount = (TextView) findViewById(R.id.tvAlbumCount);
        tvSignature = (TextView) findViewById(R.id.tvSignature);
        // 徒弟 关注 粉丝数
        tv_count_tudi = (TextView) findViewById(R.id.tv_count_tudi);
        tv_count_attention = (TextView) findViewById(R.id.tv_count_attention);
        tv_count_fans = (TextView) findViewById(R.id.tv_count_fans);
        // 上面五个item
        btnSignHelp = (TextView) findViewById(R.id.btnSignHelp);
        linGift = (LinearLayout) findViewById(R.id.linGift);

        findViewById(R.id.lin_tudi).setOnClickListener(this);
        findViewById(R.id.lin_attend).setOnClickListener(this);
        findViewById(R.id.lin_fans).setOnClickListener(this);
        imgUser.setOnClickListener(this);

        layoutHelpSignUser = (LinearLayout) findViewById(R.id.layoutHelpSignUser);

        mMainAdIv = (ImageView) findViewById(R.id.iv_main_ad_cover);
        mMainAdTitle = (TextView) findViewById(R.id.tv_main_ad_title);
        tvAdBusinessType = (TextView) findViewById(R.id.tvAdBusinessType);
        ivAdLogo = (ImageView) findViewById(R.id.iv_logo);

    }

    void setUserValue(final UserItem user, int relationFlag) {
        tvname.setText(user.getAlias() != null && !user.getAlias().equals("") ? user.getAlias() : user.getNickname());

        boolean isShowLabel = false;
        //显示师傅或者徒弟标记
        if (Relation.isMaster(relationFlag)) {
            tvShifuLabel.setVisibility(View.VISIBLE);
            isShowLabel = true;
        } else if (Relation.isStu(relationFlag)) {
            tvTudiLabel.setVisibility(View.VISIBLE);
            isShowLabel = true;
        } else {
        }

        StringBuffer userInfo = new StringBuffer();
        if (user.getGender() == UserItem.gender_boy) {
            userInfo.append("男").append("   ");
        } else if (user.getGender() == UserItem.gender_girl) {
            userInfo.append("女").append("   ");
        } else {
        }

        if (user.getAge() == 0) {
        } else {
            userInfo.append(user.getAge() + "岁").append("   ");
        }

        if (TextUtils.isEmpty(user.getCityname())) {
        } else {
            userInfo.append(user.getCityname());
        }

        if (isShowLabel && !userInfo.toString().equals("")) {
            findViewById(R.id.viewLableShifuTudi).setVisibility(View.VISIBLE);
        }
        tvUserInfo.setText(userInfo.toString());
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
                    //ImageUtils.fastBlur(userItem.getImgmiddleurl(), mBlurBgIv);
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
            case R.id.linGiftAll:// 进入礼盒页面
                intent = new Intent(mActivity, OtherGiftActivity.class);
                intent.putExtra(EXTRA.USER_ITEM, userItem);
                mActivity.startActivity(intent);
                break;
            case R.id.layoutHelpSignUser:
                intent = new Intent(mActivity, SignHelpMeAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.btnSignHelp:// 帮助签到
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.BLBTQD);
                intent = new Intent(mActivity, SignAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                mActivity.startActivity(intent);
                break;
            case R.id.linStatuses:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.BLCKDT);
                intent = new Intent(mActivity, MyDongtaiActivity.class);
                intent.putExtra("userItem", userItem);
                intent.putExtra("dynamicType", mDynamicType-1);
                mActivity.startActivity(intent);
                break;
            case R.id.linLinks:
                MobclickAgent.onEvent(mActivity, UmengClickEventHelper.BLJCLJ);
                intent = new Intent(mActivity, NiceLinkActivity.class);
                intent.putExtra("userItem", userItem);
                intent.putExtra("isFromTribe", true);
                mActivity.startActivity(intent);
                break;
            case R.id.imgZongshiMaster:
                if (userItem.getBigchiefuserid() != 0) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", userItem.getBigchiefuserid());
                    mActivity.startActivity(intent);
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
            case R.id.linQiuzhang:
                if (masters != null && masters.getHeadmenview() != null) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", masters.getHeadmenview().getUserid());
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.linZhanglao:
                if (masters != null && masters.getElderview() != null) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", masters.getElderview().getUserid());
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.linShifu:
                if (masters != null && masters.getMasterview() != null) {
                    intent = new Intent(mActivity, TribeMainAct.class);
                    intent.putExtra("userId", masters.getMasterview().getUserid());
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.linPhotoAll:
                intent = new Intent(mActivity, OtherAlbumActivity.class);
                intent.putExtra(EXTRA.USER_ITEM, userItem);
                mActivity.startActivity(intent);
                break;
        }
    }

    public void setOnClick() {
        imgZongshiMaster.setOnClickListener(this);
        findViewById(R.id.linGiftAll).setOnClickListener(this);
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
            //PicassoUtil.load(mActivity, bigChief.getImgurl(), imgZongshiMaster, R.drawable.icon_zongshi);
            ImageUtils.loadCircleHead(bigChief.getImgurl(), imgZongshiMaster);

        }

        //masterid = 0 ,并且角色是大酋长,那么 就是顶级大酋长
        if (userItem.getMasterid() == 0 && UserRole.isMaster(Integer.parseInt(userItem.getRoleflag()))) {
            linConnectLink.setVisibility(View.GONE);
            linTop.setBackgroundResource(R.drawable.tribe_top_chief_bg);
            imgUser.setBackgroundResource(R.drawable.shape_tribe_chief_photo_bg);
        } else {
            linConnectLink.setVisibility(View.VISIBLE);
            linTop.setBackgroundResource(R.drawable.tribe_top_normal_bg);
            imgUser.setBackgroundResource(R.drawable.shape_tribe_photo_bg);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tribe_top_normal_bg);
        int widthTop = Device.getWidth() * bitmap.getHeight() / bitmap.getWidth();
        LayoutParams paramsTop = new LayoutParams(Device.getWidth(), widthTop);
        paramsTop.setMargins(0, 0, 0, Util.dpToPx(getResources(), -50));
        linTop.setLayoutParams(paramsTop);

        //当前用户的大酋长信息
        BigChief cbigChief = userItem.getCbigchiefinfo();
        if (cbigChief != null) {
            //findViewById(R.id.imgZongShiBg).setVisibility(View.VISIBLE);
            imgZongshi.setVisibility(View.VISIBLE);
            //imgZongshiTagFill.setVisibility(View.VISIBLE);
            PicassoUtil.load(mActivity, cbigChief.getImgurl(), imgZongshi, R.drawable.icon_zongshi);
        }

        int accountType = userItem.getAccounttype();
        switch (accountType) {
            //系统账号
            case UserItem.accountTypeSystem:
                findViewById(R.id.linCount).setVisibility(View.GONE);
                findViewById(R.id.linHelpSign).setVisibility(View.GONE);
                linConnectLink.setVisibility(View.GONE);
                // findViewById(R.id.viewDividerSign).setBackgroundColor(getContext().getColor(R.color.transparent));
                break;
            case UserItem.accountTypeTeam:
                //tvAge.setVisibility(View.GONE);
                //tvLocation.setVisibility(View.GONE);
                //ivGender.setVisibility(View.GONE);
                break;
        }

        setUserValue(userItem, relationFlag);


        if (!TextUtils.isEmpty(userItem.getSignature()) || imgZongshi.getVisibility() != View.GONE) {
            if (!TextUtils.isEmpty(userItem.getSignature())) {
                String signature = userItem.getSignature().replace("\n", "");
                tvSignature.setVisibility(View.VISIBLE);
                tvSignature.setText(signature);
            }
        } else {
            findViewById(R.id.linSignature).setVisibility(View.GONE);
        }

        ImageUtils.loadCircleHead(userItem.getImgurl(), imgUser);
        downloadRecordVoice(userItem.getPhonetic());


        UserCount count = tribe.getCountview();

        tv_count_attention.setText(String.valueOf(count.getAttention_count()));
        tv_count_tudi.setText(String.valueOf(count.getTudi_count()));
        tv_count_fans.setText(String.valueOf(count.getFans_count()));

        // 礼物
        final List<TribeGift> tribeListGift = tribe.getGiftsview();
        if (tribeListGift != null && tribeListGift.size() > 0) {

            tvGiftCount.setText(getCount(tribeListGift.size()));
            final int margin = DimenUtils.dp2px(15);
            final int width = DimenUtils.dp2px(40);
            linGiftAll.setVisibility(View.VISIBLE);
            linGift.post(new Runnable() {
                @Override
                public void run() {
                    int linWidth = linGift.getWidth();
                    int size = Math.min(6, tribeListGift.size());//最多显示5个
                    int sizeCompute = (linWidth + margin/2) / (width + margin);

                    size = Math.min(size, sizeCompute);
                    LogUtil.logE("gift", size + ":" + sizeCompute + ":" + linWidth);
                    for (int i = 0; i < size; i++) {
                        Gift gift = tribeListGift.get(i).getGiftinfo();

                        ImageView iv = new ImageView(mActivity);
                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                        LayoutParams params = new LayoutParams(width, width);
                        params.setMargins(0, 0, margin, 0);
                        linGift.addView(iv, params);
                        ImageUtils.loadSquareImage(gift.getGiftimg(), iv);
                    }
                }
            });


        }


        // 签到
        final ArrayList<UserItem> listUser = (ArrayList<UserItem>) tribe.getProxysigninview();
        if (tribe.getIssignin() == SignListResult.issignYes) {
            btnSignHelp.setText(Res.getString(R.string.me_already_sign_in));
            linSign.setBackgroundResource(R.drawable.tribe_signed_bg);
            btnSignHelp.setTextColor(Color.parseColor("#a8a8a8"));
        } else {
            linSign.setBackgroundResource(R.drawable.tribe_sign_bg);
            btnSignHelp.setTextColor(mActivity.getResources().getColor(R.color.white));
            if (String.valueOf(tribe.getUserview().getUser().getUserid()).equals(SharedToken.getUserId(mActivity))) {
                btnSignHelp.setText(Res.getString(R.string.sign));
            } else {
                btnSignHelp.setText(Res.getString(R.string.help_signing));
            }

        }

        //稍微延迟绘制签到人员列表
        mActivity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                updateSignInList(listUser);
            }
        });


        // 图片
        final List<Photo> tribeListPhoto = tribe.getUserphotoview();
        if (tribeListPhoto != null && tribeListPhoto.size() > 0) {
            linPhotoAll.setVisibility(View.VISIBLE);
            linPhotoAll.setOnClickListener(this);
            tvAlbumCount.setText(getCount(tribe.getUserphotocount()));
            final LinearLayout linPhoto = (LinearLayout) findViewById(R.id.photo_lin);
            linPhoto.post(new Runnable() {
                @Override
                public void run() {
                    final int maxSizeItem = 5;
                    int margin = DimenUtils.dp2px(3);
                    int widthItem = (linPhoto.getWidth() - margin * (maxSizeItem - 1)) / maxSizeItem;


                    int size = Math.min(tribeListPhoto.size(), maxSizeItem);
                    for (int i = 0; i < size; i++) {
                        ImageView iv = new ImageView(mActivity);
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        LayoutParams params = new LayoutParams(widthItem, widthItem);
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
            });


        }
//        tvStatusesCount.setText(getCount(tribe.getUserstatusesnum()));
        // 链接
        List<LinkBean> listLink = tribe.getLinksview();
        if (listLink != null && listLink.size() > 0) {
            linLinks.setVisibility(View.VISIBLE);
            findViewById(R.id.linLink1).setVisibility(View.VISIBLE);
            tvLinkCount.setText(getCount(listLink.size()));
            LinkBean link1 = listLink.get(0);
            ImageView imgLink1 = (ImageView) findViewById(R.id.imgLink1);
            EmojiconTextView tvLink1 = (EmojiconTextView) findViewById(R.id.tvLink1);

            int widthHeight = (int) getResources().getDimension(R.dimen.list_head_img_width_height);
            PicassoUtil.loadStatusesCropCenter(mActivity, link1.getPicminurl(), widthHeight, widthHeight, imgLink1);
            tvLink1.setText(link1.getLinktitle());

        } else {
            linLinks.setVisibility(View.GONE);
            linLinks.setOnClickListener(null);
        }

        //如果关系链和礼物栏都不显示。那么久不显示divider2


        // 动态
        DynamicNew dynamicNew = tribe.getTribelatestview();
        if (dynamicNew != null){
            mDynamicType = dynamicNew.getType();
            tvStatusesCount.setText(getCount(dynamicNew.getCount()));
            linStatuses.setVisibility(View.VISIBLE);
            linStatuses.setOnClickListener(this);
            EmojiconTextView tvStatusesContent = (EmojiconTextView) findViewById(R.id.tvStatusesContent);
            TextView tvStatusesTime = (TextView) findViewById(R.id.tvStatusesTime);
            ImageView imgStatuses = (ImageView) findViewById(R.id.imgStatusesImg);
            String content = dynamicNew.getContent();


            String nameValue = userItem.getNickname();
            if (!TextUtils.isEmpty(userItem.getAlias())) {
                nameValue = userItem.getAlias();
            }
            tvStatusesContent.setText(content);
            if (dynamicNew.getType() == DynamicNew.TYPE_DYNAMIC) {
                tvStatusesTime.setText(nameValue + "的动态");
            }else{
                tvStatusesTime.setText(nameValue + "的红包");
            }
            String imgUrl = dynamicNew.getImgurl();
            int widthHeight = (int) getResources().getDimension(R.dimen.list_head_img_width_height);
            PicassoUtil.loadStatusesCropCenter(mActivity, imgUrl, widthHeight, widthHeight, imgStatuses);
        }else{
            linStatuses.setVisibility(View.GONE);
        }

        //如果相册 动态 精彩li链接都没有。就不显示linDivider3
        if (linPhotoAll.getVisibility() == View.GONE && linStatuses.getVisibility() == View.GONE && linLinks.getVisibility() == View.GONE) {
            //findViewById(R.id.linDivider3).setVisibility(View.GONE);
        } else {
            //findViewById(R.id.linDivider3).setVisibility(View.VISIBLE);
        }


        final View imgPlaceHolder = findViewById(R.id.imgPlaceHolder);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int screenHeight = Device.getHeight();
                int headerHeight = linHeader.getHeight();
                imgPlaceHolder.measure(0, 0);
                int placeHight = imgPlaceHolder.getMeasuredHeight();
                int bottomHeight = 0;
                if (isBottomShow) {
                    bottomHeight = Util.dip2px(mActivity, 49);
                }

                if ((screenHeight - headerHeight - Device.getStatusBasrHeight(mActivity) - bottomHeight) > (placeHight / 2)) {
                    imgPlaceHolder.setVisibility(View.VISIBLE);
                } else {
                    imgPlaceHolder.setVisibility(View.GONE);
                }
                //上面的高度计算,新加一个判断,如果显示徒弟广告,就不显示占位图。因为显示徒弟广告后,绝对没有多余的高度了。
                if (isShowTudiAd) {
                    imgPlaceHolder.setVisibility(View.GONE);
                }

                LogUtil.logE("headerHeight ", headerHeight + ":" + screenHeight + ":" + bottomHeight + ":" + placeHight + "——" + (screenHeight - headerHeight - Device.getStatusBasrHeight(mActivity)));
                //获取签到下面线的间距
                int[] locations = new int[2];
                viewDividerSign.getLocationOnScreen(locations);
                viewDividerSignY = locations[1] - Util.dip2px(getContext(), 50) - Device.getStatusBasrHeight(mActivity);
            }
        }, 200);

    }


    public int getViewDividerSignY() {
        return viewDividerSignY;
    }

    int viewDividerSignY;

    String getCount(int count) {
        return "( " + count + " )";
    }

    Masters masters;

    //需要加判断   && (UserRole.isMaster(Integer.parseInt(SharedUser.getUserItem().getRoleflag())))
    public void updateEldersInfo(Masters e) {
        if (e == null || userItem == null) return;
        masters = e;
        if (e.getHeadmenview() == null) {
            linQiuzhang.setVisibility(View.GONE);
        } else {
            linQiuzhang.setVisibility(View.VISIBLE);
            linQiuzhang.setOnClickListener(this);
            ImageUtils.loadCircleHead(e.getHeadmenview().getImgurl(), imgQiuzhang);
        }

        if (e.getElderview() == null) {
            linZhanglao.setVisibility(View.GONE);
        } else {
            linZhanglao.setOnClickListener(this);
            linZhanglao.setVisibility(View.VISIBLE);
            ImageUtils.loadCircleHead(e.getElderview().getImgurl(), imgZhanglao);
        }

        if (e.getMasterview() == null) {
            linShifu.setVisibility(View.GONE);
        } else {
            linShifu.setOnClickListener(this);
            linShifu.setVisibility(View.VISIBLE);
            ImageUtils.loadCircleHead(e.getMasterview().getImgurl(), imgShifu);
        }

    }

    public void updateNick(String alias) {
        tvname.setText(TextUtils.isEmpty(alias) ? userItem.getNickname() : alias);
    }

    public void updateSignStatus() {
        btnSignHelp.setText(Res.getString(R.string.me_already_sign_in));
        linSign.setBackgroundResource(R.drawable.tribe_signed_bg);
        btnSignHelp.setTextColor(Color.parseColor("#a8a8a8"));
    }

    public void onPlayerStop() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            imgVoice.setImageResource(R.drawable.tribe_voice_pause);
        }
    }


    boolean isBottomShow = false;

    public void setBottomVisible() {
        isBottomShow = true;
    }

    public void updateSignInList(ArrayList<UserItem> userList) {
        layoutHelpSignUser.removeAllViews();
        if (userList != null && userList.size() > 0) {
            //最好计算一个最大数
            final int width = DimenUtils.dp2px(34);
            final int margin = DimenUtils.dp2px(8);
            int widthRightBtn = btnSignHelp.getWidth();
            //计算linWidth的宽度
            final int linWidth = Device.getWidth() - DimenUtils.dp2px(12 + 12) - widthRightBtn;
            //5是设计上的最大数量。60是 '已帮签到' 四个字的预留宽度.
            final int extraWidth = DimenUtils.dp2px(0);//预防出错的预留宽度
            final int textMarginLeft = 9;
            final int maxItemSize = Math.min(5, (linWidth - DimenUtils.dp2px(60) - textMarginLeft - extraWidth) / (width + margin));
            LogUtil.logE("value", linWidth + "——" + maxItemSize);
            final int size = Math.min(userList.size(), maxItemSize);

            RelativeLayout.LayoutParams linParams = (RelativeLayout.LayoutParams) layoutHelpSignUser.getLayoutParams();
            linParams.width = linWidth;
            layoutHelpSignUser.setLayoutParams(linParams);
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mActivity);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LayoutParams params = new LayoutParams(width, width);
                params.setMargins(0, 0, margin, 0);
                layoutHelpSignUser.addView(iv, params);
                ImageUtils.loadCircleHead(userList.get(i).getImgurl(), iv);
            }

            TextView tv = new TextView(mActivity);
            tv.setText("已帮签到");
            tv.setTextColor(getResources().getColor(R.color.f_gray));
            tv.setTextSize(13);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.setMargins(textMarginLeft, 0, 0, 0);
            layoutHelpSignUser.addView(tv, params);

        } else {
            TextView tv = new TextView(mActivity);
            tv.setText(getResources().getString(R.string.me_no_people_help_sign_in));
            tv.setTextColor(getResources().getColor(R.color.f_gray));
            tv.setTextSize(13);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            layoutHelpSignUser.addView(tv, params);
        }
    }

    private void downloadRecordVoice(String voiceUrl) {
        LogUtil.logE("voiceUrl:" + voiceUrl);
        if (!TextUtils.isEmpty(voiceUrl)) {
            imgVoice.setVisibility(View.VISIBLE);
            mPlayer.downloadOnlineVoice(voiceUrl, new VoicePlayer.OnDownloadSuccessListener() {
                @Override
                public void onDownloadSuccess(String path) {
                    setPlayerDataSource(path);
                }
            });
        } else {
            imgVoice.setVisibility(View.GONE);
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
        ImageUtils.loadImage(ad.adimglarurl, mMainAdIv);
        mMainAdIv.setOnClickListener(this);
        mMainAdTitle.setText(ad.adtitle);
        if (ad.user != null)
            ImageUtils.loadCircleHead(ad.user.getImgurl(), ivAdLogo);
        if (ad.businessclass == Ad.businessclassNone) {
            tvAdBusinessType.setVisibility(View.GONE);
        } else {
            tvAdBusinessType.setVisibility(View.VISIBLE);
            tvAdBusinessType.setText(ad.businessclassname);
            tvAdBusinessType.setOnClickListener(this);
        }

    }

    boolean isShowTudiAd = false;

    public void updateTudiAd() {
        isShowTudiAd = true;
        findViewById(R.id.layout_tudi_ad).setVisibility(VISIBLE);
        findViewById(R.id.tv_tudi_ad_all).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TribeTudiAdActivity.class);
                intent.putExtra("userid", userId);
                getContext().startActivity(intent);
            }
        });
    }

//    public void setSeftText() {
//        btnSignHelp.setText(R.string.sign);
//        //tvTipAlbum.setText(R.string.my_album);
//    }
//
//    public void setOtherText() {
//        btnSignHelp.setText(R.string.help_signing);
//        //tvTipAlbum.setText(R.string.its_ablum);
//    }
//
//    public void setSignText() {
//        btnSignHelp.setText(R.string.sign);
//    }
}
