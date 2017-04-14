package yahier.exst.act.dongtai;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.PublishShoppingActivity;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.act.redpacket.RedPacketDetailAct;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ImagePagerAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.dialog.ShoppingCircleTipsDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.LinkInStatuses;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.ResultItem;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdUserItem2;
import com.stbl.stbl.item.ad.PreOpenHongbaoCheck;
import com.stbl.stbl.item.ad.RPImgInfo;
import com.stbl.stbl.item.ad.RedPacketDict;
import com.stbl.stbl.item.ad.ShoppingCircleComment;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.item.ad.YunHongbaoDetail;
import com.stbl.stbl.item.ad.YunHongbaoInfo;
import com.stbl.stbl.item.ad.YunHongbaoPicker;
import com.stbl.stbl.item.ad.YunHongbaoStatistics;
import com.stbl.stbl.item.redpacket.RedpacketDetail;
import com.stbl.stbl.item.redpacket.RedpacketPickResult;
import com.stbl.stbl.item.redpacket.RedpacketSetting;
import com.stbl.stbl.item.redpacket.RedpacketSettingAll;
import com.stbl.stbl.item.redpacket.RpPickersAll;
import com.stbl.stbl.item.redpacket.RpReceiveDetailItem;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.model.UserCard;
import com.stbl.stbl.task.AdTask;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.task.shoppingCircleTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EmojiParseThread;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ShareUtils;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.CircleProgressView;
import com.stbl.stbl.widget.DongtaiDetailRewardLayout;
import com.stbl.stbl.widget.DongtaiDetailScrollView;
import com.stbl.stbl.widget.EmojiKeyboard;
import com.stbl.stbl.widget.EmojiTextView;
import com.stbl.stbl.widget.NoScrollViewPager;
import com.stbl.stbl.widget.RealtimeBlurView;
import com.stbl.stbl.widget.TitleBar;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.yunzhanghu.redpacketsdk.RPTokenCallback;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.ui.activity.RPDetailActivity;
import com.yunzhanghu.redpacketui.utils.RPRedPacketUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.rong.eventbus.EventBus;


/**
 * Created by tnitf on 2016/6/12.modified by yahier.
 * 购物圈详情页面。该页面抢红包逻辑略复杂。
 */
public class AdDongtaiDetailAct extends BaseActivity implements View.OnClickListener, FinalHttpCallback {

    private static final String TAG = "DongtaiDetailActivity";

    private DongtaiDetailRewardLayout mRewardLayout;
    LinearLayout linContainer;
    AdDongtaiDetailAct mContext;

    TextView txtRewardCount;

    // 用户信息
    ImageView userImg, imgAdLevel;
    TextView tvName;
    List<StatusesComment> listRemark;

    String statusesid;

    ShoppingCircleDetail statuses;
    View imgMore;
    final int resultDeleteCode = 104;// 删除动态
    final int requestVideoFull = 105;// 转发M
    int commentCount;
    int awardCount = 0;// 打赏人数
    View linLink;
    final String tag = getClass().getSimpleName();

    private TextView mCommentTabTv;
    private View mCommentTabLine;

    private ShoppingCircleCommentFragment mCommentFragment;

    private TitleBar mBar;

    private List<StatusesReward> mRewardUserList;

    private SwipeToLoadLayout mSwipeToLoadLayout;

    private DongtaiDetailScrollView mScrollView;
    private NoScrollViewPager mViewPager;
    private RelativeLayout mTabLayout;

    private boolean mIsRemoveGlobalListener;
    CircleProgressView circleProgressbar;
    TextView tvHongbaoAmount;
    TextView tvTime;
    EmojiTextView tvContent;
    View linHongbaoPick;
    TextView tvSquareAttend;
    RedpacketDetail redpacketDetail;

    //已经抢的红包次数
    int RPtakencount;
    //自由抢红包的次数
    private int freeTimesOfPickHongbao = 0;
    private int reveivetimes;
    //判断是否显示圆红包图标

    View linRpPickers;
    /*
     * 红包传入状态
     */
    int redpacketstatusTransfered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_dongtai_detail_act);
        mContext = this;
        statusesid = getIntent().getStringExtra("statusesId");   //statusesId
        redpacketstatusTransfered = getIntent().getIntExtra("redpacketstatus", RedpacketDetail.status_pickOver);

        //LogUtil.logE("AdDongtaiDetailAct", "redpacketstatus:" + redpacketstatusTransfered);
        initViews();
        getSquareDetail();
        EventBus.getDefault().register(this);
        initHongbaoTimes();
    }


    void initHongbaoTimes() {
        final RedpacketSettingAll redpacketSettingAll = SharedPrefUtils.getRedpacketSettingAll();
        if (redpacketSettingAll == null || redpacketSettingAll.getAdconfig() == null || redpacketSettingAll.getUnadconfig() == null) {
            CommonTask.getRedpacketSetting();
            ToastUtil.showToast("数据有误，请稍候重试");
            return;
        }
        if ((int) SharedPrefUtils.getFromPublicFile(KEY.ISADVERTISER + SharedToken.getUserId(), 0) == 0) {
            reveivetimes = redpacketSettingAll.getUnadconfig().getRecelimitcount();
            freeTimesOfPickHongbao = redpacketSettingAll.getUnadconfig().getRecefreecount();
        } else {
            reveivetimes = redpacketSettingAll.getAdconfig().getRecelimitcount();
            freeTimesOfPickHongbao = redpacketSettingAll.getAdconfig().getRecefreecount();
        }

        LogUtil.logE("免费抢红包次数", freeTimesOfPickHongbao);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isActDestroyed = true;
    }


    //刷新所有数据 包括评论
    void refresh() {
        getSquareDetail();
    }


    EmojiKeyboard mEmojiKeyboard;
    ImageView mEmojiIv, mSendIv;
    EditText mContentEt;

    void initViews() {
        linRpPickers = findViewById(R.id.linRpPickers);
        imgAdLevel = (ImageView) findViewById(R.id.imgAdLever);
        tvSquareAttend = (TextView) findViewById(R.id.tvSquareAttend);
        linHongbaoPick = findViewById(R.id.linHongbaoPick);
        tvContent = (EmojiTextView) findViewById(R.id.tvContent);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvHongbaoAmount = (TextView) findViewById(R.id.tvHongbaoAmount);
        circleProgressbar = (CircleProgressView) findViewById(R.id.circleProgressbar);
        circleProgressbar.setStrokeWidth((int) mContext.getResources().getDimension(R.dimen.dp_4));
        mContentEt = (EditText) findViewById(R.id.et_content);
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

        mEmojiIv = (ImageView) findViewById(R.id.iv_emoji);
        mSendIv = (ImageView) findViewById(R.id.iv_send);
        mEmojiIv.setOnClickListener(this);
        mSendIv.setOnClickListener(this);

        mBar = (TitleBar) findViewById(R.id.bar);
        mBar.setTitle("商圈详情");
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResulFinishtUpdate();
            }
        });
        mBar.setActionIcon(R.drawable.share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mScrollView = (DongtaiDetailScrollView) findViewById(R.id.swipe_target);
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        initViewPagerData();
        mTabLayout = (RelativeLayout) findViewById(R.id.layBar);

        mScrollView.setOnScrollChangeListener(new DongtaiDetailScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int scrollY, boolean isTouch) {
                if (isTouch) {
                    if (!mIsRemoveGlobalListener) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mTabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
                        } else {
                            mTabLayout.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
                        }
                        mIsRemoveGlobalListener = true;
                    }
                }

            }
        });

        mCommentTabLine = findViewById(R.id.tab_line_comment);
        mCommentTabTv = (TextView) findViewById(R.id.tv_comment);
        mCommentTabTv.setSelected(true);
        mCommentTabLine.setVisibility(View.VISIBLE);
        mCommentTabTv.setOnClickListener(this);
        linLink = findViewById(R.id.linLink);
        imgMore = findViewById(R.id.imgMore);
        imgMore.setOnClickListener(this);
        userImg = (ImageView) findViewById(R.id.item_iv);
        userImg.setOnClickListener(this);
        tvName = (TextView) findViewById(R.id.name);
        linContainer = (LinearLayout) findViewById(R.id.dongtai_detail_container);
        txtRewardCount = (TextView) findViewById(R.id.txtRewardCount);

        mRewardLayout = (DongtaiDetailRewardLayout) findViewById(R.id.layout_reward);

        mRewardUserList = new ArrayList<>();
    }


    //接收点击了分享
    public void onEvent(EventTypeCommon event) {
        switch(event.getType()) {
            case EventTypeCommon.typeClickedShare:
                break;
            case EventTypeCommon.typeShoppingCircleReplyCommentSuccess:
                getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodUtils.hideInputMethod(mContentEt);
                    }
                }, 300);


                break;
        }
    }

    /**
     * 显示红包分享的提示
     */
    void showRPSharetip() {
        int AllSharePickTime = reveivetimes - freeTimesOfPickHongbao;
        int leftSharePickTimes = reveivetimes - RPtakencount;//总次数-已经抢的次数
        TipsDialog dialog = TipsDialog.popup(this, "免费抢红包次数已用完，分享此商圈红包动态可赢取抢红包机会1次。你今天还有" + leftSharePickTimes + "次(共" + AllSharePickTime + "次)抢红包机会", "再看看", "立即分享", new TipsDialog.OnTipsListener() {
            @Override
            public void onConfirm() {
                share();
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.setTitleGone();
    }


    void share() {
        if (statuses == null) return;
        UmengClickEventHelper.onShoppingCShareClickEvent(this);
        ShareItem shareItem = ShareUtils.createShoppingCircle(statuses);
        new ShareDialog(mContext).shareShoppingCircle(statuses.getSquareid(), hongbaoId, shareItem);
    }


    private void initViewPagerData() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        mCommentFragment = new ShoppingCircleCommentFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("statusesId", statusesid);
        mCommentFragment.setArguments(bundle1);
        fragmentList.add(mCommentFragment);


        String[] titles = new String[]{"评论"};
        CommonFragmentPagerAdapter adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch(i) {
                    case 0:
                        mScrollView.setRefreshView(mCommentFragment.getRefreshView());
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.setRefreshView(mCommentFragment.getRefreshView());
            }
        }, 100);
    }

    /**
     * 链接开始
     * 显示链接中的商品
     *
     * @param goods
     */
    void showGoodsLink(Goods goods) {
        if (goods == null)
            return;
        View view = findViewById(R.id.link4);
        view.setVisibility(View.VISIBLE);
        linLink.setVisibility(View.VISIBLE);
        view.setTag(goods);
        view.setOnClickListener(this);
        ImageView imgLink = (ImageView) findViewById(R.id.link4imgLink);
        TextView tvGoodsTitle = (TextView) findViewById(R.id.link4tvGoodsTitle);
        TextView tvGoodsPrice = (TextView) findViewById(R.id.link4tvGoodsPrice);
        TextView tvGoodsSale = (TextView) findViewById(R.id.link4tvGoodsSale);
        PicassoUtil.load(mContext, goods.getImgurl(), imgLink);
        tvGoodsTitle.setText(goods.getGoodsname());
        tvGoodsPrice.setText("￥" + goods.getMinprice());
        tvGoodsSale.setText(getString(R.string.mall_sales) + goods.getSalecount());
    }

    /**
     * 显示链接中的名片
     */
    void showCardLink(UserItem user) {
        if (user == null)
            return;
        View view = findViewById(R.id.link1);
        view.setVisibility(View.VISIBLE);
        linLink.setVisibility(View.VISIBLE);
        view.setTag(user);
        view.setOnClickListener(this);
        ImageView imgLink = (ImageView) view.findViewById(R.id.link1imgUser);
        TextView name = (TextView) view.findViewById(R.id.link1name);
        TextView user_gender_age = (TextView) view.findViewById(R.id.link1user_gender_age);
        TextView user_city = (TextView) view.findViewById(R.id.link1user_city);
        TextView user_signature = (TextView) view.findViewById(R.id.link1user_signature);

        PicassoUtil.load(mContext, user.getImgurl(), imgLink);
//        name.setText(user.getNickname());
        name.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias());
        user_city.setText(user.getCityname());
        user_signature.setText(user.getSignature());

        user_gender_age.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            user_gender_age.setTextAppearance(this, R.style.dongtai_list_gender_boy);
        } else if (user.getGender() == UserItem.gender_girl) {
            user_gender_age.setTextAppearance(this, R.style.dongtai_list_gender_girl);
        } else {
            user_gender_age.setTextAppearance(this, R.style.dongtai_list_gender_unknow);
        }
    }


    /**
     * 显示链接广告
     *
     * @param link
     */
    void showAdLink(LinkStatuses link) {
        final Ad ad = link.getAdinfo();
        if (ad == null) return;
        int width = linContainer.getWidth();
        int height = Config.AD_DESIGN_HEIGHT * width / Config.AD_DESIGN_WIDTH;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height + DimenUtils.dp2px(48));
        params.setMargins(0, Util.dip2px(MyApplication.getContext(), 5), 0, 0);

        RelativeLayout.LayoutParams mCoverParams = new RelativeLayout.LayoutParams(width, height);
        RelativeLayout.LayoutParams mFloatParams = new RelativeLayout.LayoutParams(width, height + DimenUtils.dp2px(48));

        linLink.setVisibility(View.VISIBLE);
        final View view = findViewById(R.id.link10Ad);
        view.setVisibility(View.VISIBLE);
        view.setLayoutParams(params);
        TextView tvAdTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvAdBusinessType = (TextView) view.findViewById(R.id.tvAdBusinessType);

        final RealtimeBlurView mBlurView = (RealtimeBlurView) findViewById(R.id.blur_view);
        final RelativeLayout mFloatLayout = (RelativeLayout) findViewById(R.id.layout_float);
        final ImageView mCrownIv = (ImageView) view.findViewById(R.id.iv_crown);
        final ImageView mHeadIv = (ImageView) view.findViewById(R.id.iv_head);
        final TextView mNickTv = (TextView) view.findViewById(R.id.tv_nick);
        final ImageView mOwnerLogoIv = (ImageView) view.findViewById(R.id.iv_ad_chief_logo);
        final TextView mFamilyTv = (TextView) view.findViewById(R.id.tv_family_amount);
        final TextView mStudentTv = (TextView) view.findViewById(R.id.tv_student_amount);
        final TextView mNearestChiefTv = (TextView) view.findViewById(R.id.tv_nearest_bigchief);
        final ImageView mNearestLogoIv = (ImageView) view.findViewById(R.id.iv_nearest_chief_logo);
        final TextView mViewHomepageTv = (TextView) view.findViewById(R.id.tv_view_homepage);
        final ImageView mCloseIv = (ImageView) view.findViewById(R.id.iv_close);
        final ImageView mLogoIv = (ImageView) view.findViewById(R.id.iv_logo);
        ImageView imgAdImg = (ImageView) view.findViewById(R.id.iv_cover);

        imgAdImg.setLayoutParams(mCoverParams);
        mFloatLayout.setLayoutParams(mFloatParams);

        ImageUtils.loadCircleHead(ad.user.getImgurl(), mLogoIv);
        ImageUtils.loadImage(ad.adimglarurl, imgAdImg);
        tvAdTitle.setText(ad.adtitle);

        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatLayout.setVisibility(View.GONE);
            }
        });
        mFloatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgAdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CommonWeb.class);
                intent.putExtra("ad", ad);
                intent.putExtra("title", getString(R.string.me_ad_detail));
                intent.putExtra("type", CommonWeb.typeAd);
                mContext.startActivity(intent);
            }
        });

        View tvIcon = view.findViewById(R.id.tv_ad_icon);
        if (ad.issys == Ad.issysYes) {
            tvIcon.setVisibility(View.GONE);
        } else {
            tvIcon.setVisibility(View.VISIBLE);
            mLogoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFloatLayout.setVisibility(View.VISIBLE);
                    mBlurView.forceBlur();
                    mActivity.getTaskManager().start(AdTask.getUserCard(ad.user.getUserid())
                            .setCallback(new HttpTaskCallback<UserCard>(mActivity) {

                                @Override
                                public void onError(TaskError e) {
                                    super.onError(e);
                                    ToastUtil.showToast(e.msg);
                                }

                                @Override
                                public void onSuccess(UserCard result) {
                                    ad.usercard = result;
                                    final UserCard.UserBaseInfoView info = ad.usercard.userbaseinfoview;
                                    ImageUtils.loadCircleHead(info.imgurl, mHeadIv);
                                    mNickTv.setText(info.nickname);
                                    mFamilyTv.setText(String.format(mActivity.getString(R.string.me_family_amount_d), ad.usercard.userexview.familycount));
                                    mStudentTv.setText(String.format(mActivity.getString(R.string.me_student_amount_d), ad.usercard.userexview.tudicount));
                                    if (ad.usercard.bigchiefinfo == null) {
                                        mNearestChiefTv.setText("");
                                        ImageUtils.loadImage("", mNearestLogoIv);
                                    } else {
                                        mNearestChiefTv.setText(Html.fromHtml(String.format(mActivity.getString(R.string.me_nearest_bigchief_s), ad.usercard.bigchiefinfo.bigchiefusername)));
                                        ImageUtils.loadImage(ad.usercard.bigchiefinfo.imgurl, mNearestLogoIv);
                                    }

                                    if ((info.roleflag & 4) == 4) {
                                        mCrownIv.setVisibility(View.VISIBLE);
                                        mOwnerLogoIv.setVisibility(View.VISIBLE);
                                        ImageUtils.loadImage(ad.usercard.cbigchiefinfo.imgurl, mOwnerLogoIv);
                                    } else {
                                        mCrownIv.setVisibility(View.GONE);
                                        mOwnerLogoIv.setVisibility(View.GONE);
                                    }
                                    mViewHomepageTv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(mActivity, TribeMainAct.class);
                                            intent.putExtra("userId", info.userid);
                                            mActivity.startActivity(intent);
                                        }
                                    });
                                }
                            }));
                }
            });
        }
        if (ad.businessclass == Ad.businessclassNone) {
            tvAdBusinessType.setVisibility(View.GONE);
        } else {
            tvAdBusinessType.setVisibility(View.VISIBLE);
            tvAdBusinessType.setText(ad.businessclassname);
            tvAdBusinessType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdHelper.toApplyAdCooperateAct(ad, mActivity);
                }
            });
        }
    }


    /**
     * 显示精彩链接
     *
     * @param userlinksinfo
     */
    void showLink(LinkInStatuses userlinksinfo) {
        if (userlinksinfo == null) return;
        View view = findViewById(R.id.link8Link);
        view.setVisibility(View.VISIBLE);
        linLink.setVisibility(View.VISIBLE);
        view.setTag(userlinksinfo);
        view.setOnClickListener(this);

        ImageView imgLink = (ImageView) view.findViewById(R.id.link8Img);
        TextView name = (TextView) view.findViewById(R.id.link8Content);
        name.setText(userlinksinfo.getTitle());
        ImageUtils.loadSquareImage(userlinksinfo.getImgurl(), imgLink);
    }


    //显示红包icon.
    void showHongbaoIcon() {
        //如果列表状态不是可抢，或者已经抢过，就不下雨
        if (redpacketstatusTransfered != RedpacketDetail.status_pickabel || (redpacketDetail.getTakenamount() != 0)) {
            return;
        }
        //如果下红包雨,则下面的红包信息一块不能点击
        linRpPickers.setOnClickListener(null);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                int imgWidth = linHongbaoPick.getMeasuredWidth();
                int imgHeight = linHongbaoPick.getMeasuredHeight();
                int width = linContainer.getWidth();
                int height = linContainer.getHeight();
                if (height == 0) return;
                linHongbaoPick.setVisibility(View.VISIBLE);
                int randomHeight = new Random().nextInt(height);
                int randomWidth = new Random().nextInt(width);

                //不要右偏
                if (randomWidth > width - imgWidth) {
                    randomWidth = width - imgWidth - 20;
                }

                //不要下偏
                if (randomHeight > height - imgHeight) {
                    randomHeight = height - imgHeight - 20;
                }

                int widthHeight = Util.dip2px(mContext, 70);//70也是布局中的宽高
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthHeight, widthHeight);
                params.setMargins(randomWidth, randomHeight, 0, 0);
                linHongbaoPick.setLayoutParams(params);

                linHongbaoPick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (circleProgressbar.getProgress() < 100) {
                            return;
                        }

                        v.setEnabled(false);
                        v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v.setEnabled(true);
                            }
                        }, 1500);

                        checkPickRp();

                    }
                });
                hongbaoRainStep1();
                progress = 0;

                handler.sendEmptyMessage(whatValue);

            }
        }, 1000);
        handler = new ProgressHandler(mContext);
    }


    void checkPickRp() {
        if (TextUtils.isEmpty(hongbaoId) || !hongbaoId.contains("RP")) {
            return;
        }
        //如果是普通红包,当前用户是发红包的人。直接进入详情
        if (redpacketDetail.getType() == RedpacketDetail.type_normal && String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
            gotoRedDetailAct();
            return;
        }
        Params params = new Params();
        params.put("redpacketid", hongbaoId);
        new HttpEntity(mActivity).commonRedpacketPostData(Method.checkPacketReceive, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                        ToastUtil.showToast(item.getErr().getMsg());
                    }
                    return;
                }
                String obj = JSONHelper.getStringFromObject(item.getResult());
                PreOpenHongbaoCheck hongbaoCheckItem = JSONHelper.getObject(obj, PreOpenHongbaoCheck.class);
                RPtakencount = hongbaoCheckItem.getTakencount();
                Intent intent;
                String url;
                switch(hongbaoCheckItem.getDenyreason()) {
                    case PreOpenHongbaoCheck.denyreasonNo:
                        startPickRp();
                        break;
                    case PreOpenHongbaoCheck.denyreasonNoFreeTime:
                        showRPSharetip();
                        break;
                    case PreOpenHongbaoCheck.denyreasonPicked:
                        startPickRp();
                        break;
                    case PreOpenHongbaoCheck.denyreasonRpOver:
                        redpacketDetail.setStatus(RedpacketDetail.status_pickOver);
                        startPickRp();
                        break;
                    case PreOpenHongbaoCheck.denyreasonRpExpire:
                        redpacketDetail.setStatus(RedpacketDetail.status_expire);
                        startPickRp();
                        break;
                    case PreOpenHongbaoCheck.denyreasonNoPermission: //去做任务解锁抢红包权限
                        MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JSQHBQXRW);
                        intent = new Intent(AdDongtaiDetailAct.this, CommonWeb.class);
                        url = (String) SharedPrefUtils.getFromPublicFile(KEY.MISSION_ITEM, "");
                        if (TextUtils.isEmpty(url)) {
                            return;
                        }
                        intent.putExtra("url", url);
                        startActivity(intent);
                        break;
                    case PreOpenHongbaoCheck.denyreasonUnlockMax://解锁最大抢红包次数
                        MobclickAgent.onEvent(mActivity, UmengClickEventHelper.JSQHBCSRW);
                        intent = new Intent(AdDongtaiDetailAct.this, CommonWeb.class);
                        url = (String) SharedPrefUtils.getFromPublicFile(KEY.MISSION_INVITE, "");
                        if (TextUtils.isEmpty(url)) {
                            return;
                        }
                        intent.putExtra("url", url);
                        startActivity(intent);
                        break;
                    default:
                        ToastUtil.showToast(hongbaoCheckItem.getMessage());
                }


            }
        });
    }

    void startPickRp() {
        boolean isSender;
        if (redpacketDetail != null) {
            if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                isSender = true;
            } else {
                isSender = false;
            }
            //没有抢
            if (redpacketDetail.getTakenamount() == 0) {
                if (isSender) {
                    switch(redpacketDetail.getStatus()) {
                        case RedpacketDetail.status_pickabel:
                            switch(redpacketDetail.getType()) {
                                case RedpacketDetail.type_normal:
                                    gotoRedDetailAct();
                                    break;
                                case RedpacketDetail.type_random:
                                    showPickDialog();
                                    break;
                            }
                            break;
                        //红包已经过期
                        case RedpacketDetail.status_expire:
                            switch(redpacketDetail.getType()) {
                                case RedpacketDetail.type_normal:
                                    gotoRedDetailAct();
                                    break;
                                case RedpacketDetail.type_random:
                                    showPickDialog();
                                    break;
                            }
                            break;
                        //红包抢完了
                        case RedpacketDetail.status_pickOver:
                            switch(redpacketDetail.getType()) {
                                case RedpacketDetail.type_normal:
                                    gotoRedDetailAct();
                                    break;
                                case RedpacketDetail.type_random:
                                    showPickDialog();
                                    break;
                            }
                            break;
                    }
                    //领红包者 没有抢过
                } else {
                    switch(redpacketDetail.getStatus()) {
                        case RedpacketDetail.status_pickabel:
                            showPickDialog();
                            break;
                        //红包已经过期
                        case RedpacketDetail.status_expire:
                            showPickDialog();//提示红包已经过期
                            break;
                        //红包抢完了
                        case RedpacketDetail.status_pickOver:
                            showPickDialog();//提示红包已被领完
                            switch(redpacketDetail.getType()) {
                                case RedpacketDetail.type_random:
                                    //随机红包可以查看人家的手气
                                    break;
                            }
                            break;
                    }
                }
            } else {
                //如果抢过了。全部进入详情
                gotoRedDetailAct();
            }
        }
    }

    final static int whatValue = 1;
    ProgressHandler handler;
    static int progress = 0;

    private static class ProgressHandler extends Handler {
        private WeakReference<AdDongtaiDetailAct> mActivity = null;

        public ProgressHandler(AdDongtaiDetailAct activity) {
            mActivity = new WeakReference<AdDongtaiDetailAct>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AdDongtaiDetailAct activity = mActivity.get();
            if (activity == null) {
                return;
            }
            if (progress > 100) return;
            progress++;
            activity.circleProgressbar.setProgress(progress);
            sendEmptyMessageDelayed(whatValue, progress);//速度越来越慢
        }
    }

    //红包雨的两个步骤
    private void hongbaoRainStep1() {
        ViewGroup group = (ViewGroup) findViewById(R.id.linFrameBig);
        for (int i = 0; i < 15; i++) {
            int randomTimeOff = new Random().nextInt(4000);
            group.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hongbaoRainStep2();

                }
            }, randomTimeOff);
        }
    }

    private void hongbaoRainStep2() {
        long duration = 3000;
        int width = Device.getWidth();
        int height = Device.getHeight();
        ViewGroup group = (ViewGroup) findViewById(R.id.linFrameBig);
        int demension = Util.dip2px(this, 40);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(demension, demension);

        for (int i = 0; i < 2; i++) {
            int randomWidthOff = new Random().nextInt(width);
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.icon_gouwuquan_hongbao);
            group.addView(img, params);
            ObjectAnimator.ofFloat(img, "translationX", randomWidthOff, randomWidthOff).setDuration(duration).start();
            ObjectAnimator.ofFloat(img, "translationY", -demension, height + 200).setDuration(duration).start();

        }

    }


    /**
     * 获取红包详情 new
     */
    void getRedpacketDetail() {
        if (TextUtils.isEmpty(hongbaoId) || !hongbaoId.contains("RP")) {
            return;
        }
        Params params = new Params();
        params.put("redpacketid", hongbaoId);
        new HttpEntity(mContext).commonRedpacketPostData(Method.getRedPacketDetail, params, this);
    }

    /**
     * 领取红包.
     */
    void pickRedpacket() {
        if (TextUtils.isEmpty(hongbaoId) || !hongbaoId.contains("RP")) {
            return;
        }
        Params params = new Params();
        params.put("redpacketid", hongbaoId);
        new HttpEntity(mContext).commonRedpacketPostData(Method.redpacketPick, params, this);
    }

    /**
     * 获取红包领取列表.
     */
    void getPickersList() {
        if (TextUtils.isEmpty(hongbaoId) || !hongbaoId.contains("RP")) {
            return;
        }
        Params params = new Params();
        params.put("redpacketid", hongbaoId);
        new HttpEntity(mContext).commonRedpacketPostData(Method.getRedpacketPickersV1, params, this);
    }


    /**
     * 得到statuses数据之后的操作
     */
    //默认为不可抢的状态
    int rpStatus = com.stbl.stbl.item.ad.RedPacketInfo.statusPickOver;
    String hongbaoId;

    void setValueFromStatuses() {
        com.stbl.stbl.item.ad.RedPacketInfo rpInfo = statuses.getRpinfo();
        if (rpInfo != null) {
            hongbaoId = rpInfo.getRpid();
            rpStatus = rpInfo.getStatus();
            //改变了可抢的判断，进入判断。
            getRedpacketDetail();
            getPickersList();
        } else {

        }
        String content = statuses.getContent();
        if (content == null || content.equals("")) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            content = Util.halfToFull(content);// 转换为全角
            EmojiParseThread.getInstance().parse(content, tvContent);
        }

        tvTime.setText("发布于" + DateUtil.getYMD(String.valueOf(statuses.getCreatetime())));

        if (linContainer.getChildCount() != 0) {
            linContainer.removeAllViews();
        }

        RPImgInfo bigPic = statuses.getImginfo2();

        if (bigPic != null) {
            ArrayList<String> pics = bigPic.getMiddlepic();
            for (int i = 0; i < pics.size(); i++) {
                addImageView(pics, i);
            }
        }

        //awardCount = statuses.getRewardcount();
        AdUserItem2 user = statuses.getUserinfo();
        getFollowState(user.getAduserid());
        String mIconTitle = user.getIcontitle();
        if (mIconTitle == null) {
            imgAdLevel.setVisibility(View.GONE);
            userImg.setBackgroundResource(R.color.transparent);
        } else {
            switch(user.getIcontitle()) {
                case AdUserItem2.icontitleGold:
                    imgAdLevel.setVisibility(View.VISIBLE);
                    imgAdLevel.setImageResource(R.drawable.ad_level_gold);
                    userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_gold);
                    break;
                case AdUserItem2.icontitleSilver:
                    imgAdLevel.setVisibility(View.VISIBLE);
                    imgAdLevel.setImageResource(R.drawable.ad_level_silver);
                    userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_silver);
                    break;
                case AdUserItem2.icontitleCopper:
                    imgAdLevel.setVisibility(View.VISIBLE);
                    imgAdLevel.setImageResource(R.drawable.ad_level_cooper);
                    userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_cooper);
                    break;
                default:
                    imgAdLevel.setVisibility(View.GONE);
                    userImg.setBackgroundResource(R.color.transparent);
                    break;

            }
        }
        ImageUtils.loadIcon(user.getAdimgurl(), userImg);
        tvName.setText(user.getAdnickname());


        LinkStatuses link = statuses.getLinkinfo();
        if (link != null) {
            int linkType = link.getLinktype();
            switch(linkType) {
                case LinkStatuses.linkTypeCrad:
                    UserItem userLink = link.getUserinfo();
                    showCardLink(userLink);
                    break;
                case LinkStatuses.linkTypeProduct:
                    Goods goods = link.getProductinfo();
                    showGoodsLink(goods);
                    break;
                case LinkStatuses.linkTypeNiceLink:
                    showLink(link.getUserlinksinfo());
                    break;
                case LinkStatuses.linkTypeAd:
                    showAdLink(link);
                    break;

            }

        }
        //获取详情之后，获取评论
        mCommentFragment.refreshCommentList();

    }

    void getFollowState(long userId) {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.userAttentionInfo, params, this);
    }


    void addImageView(ArrayList<String> pics, final int index) {
        try {
            final ImageView img = new ImageView(this);
            linContainer.addView(img);
            String url = pics.get(index);
            ImageUtils.loadBitmap(url, img, new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                    ToastUtil.showToast(R.string.failed_load_img);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_square_image);
                    int containerWidth = linContainer.getWidth();// - paddingAmount;// -20;
                    int bitmapHeight = containerWidth * bitmap.getHeight() / bitmap.getWidth();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(containerWidth, bitmapHeight);
                    params.setMargins(0, Util.dip2px(mContext, 5), 0, 0);
                    img.setLayoutParams(params);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                    int containerWidth = linContainer.getWidth();// - paddingAmount;// -20;
                    int bitmapHeight = containerWidth * bitmap.getHeight() / bitmap.getWidth();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(containerWidth, bitmapHeight);
                    params.setMargins(0, Util.dip2px(mContext, 5), 0, 0);
                    img.setLayoutParams(params);
                    //img.setPadding(0, 20, 0, 0);//竟然影响了左右。于是改用了margin
                    return false;
                }
            });
            img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(mContext, ImagePagerAct.class);
                    intent.putExtra("index", index);
                    intent.putExtra("list", statuses.getImginfo2().getOriginalpic());
                    startActivity(intent);

                }
            });
        } catch(Exception e) {
            //ToastUtil.showToast(R.string.failed_to_parse_long_statuses);
        }

    }


    @TargetApi (Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        if (statuses == null)
            return;
        Intent intent;
        //int a = 9/0;
        switch(view.getId()) {
            case R.id.tv_comment: //评论tab
                mCommentTabTv.setSelected(true);
                mCommentTabLine.setVisibility(View.VISIBLE);
                //切换tab
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.item_iv:
                if (statuses == null) {
                    return;
                }
                if (statuses.getUserinfo().getClicktype() == AdUserItem2.typeClickAd && !TextUtils.isEmpty(statuses.getUserinfo().getAdurl())) {
                    intent = new Intent(mContext, CommonWeb.class);
                    // intent.putExtra("url", statuses.getUserinfo().getAdurl());
                    Ad ad = new Ad();
                    ad.businessclass = Ad.businessclassNone;
                    ad.businessclassname = "";
                    ad.adurl = statuses.getUserinfo().getAdurl();
                    ad.adid = statuses.getUserinfo().getAdid();
                    intent.putExtra("ad", ad);
                    intent.putExtra("type", CommonWeb.typeAd);
                    mContext.startActivity(intent);
                } else {
                    intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", statuses.getUserid());
                    intent.putExtra("typeEntry", TribeMainAct.typeEntryOther);
                    mContext.startActivity(intent);
                }
                break;
            case R.id.link1:
                UserItem user = (UserItem) view.getTag();
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", user.getUserid());
                startActivity(intent);
                break;
            case R.id.link4:
                Goods goods = (Goods) view.getTag();
                intent = new Intent(mContext, MallGoodsDetailAct.class);
                intent.putExtra("goodsid", goods.getGoodsid());
                mContext.startActivity(intent);
                break;
            case R.id.link8Link:
                LinkInStatuses userlinksinfo = (LinkInStatuses) view.getTag();
                intent = new Intent(mContext, CommonWeb.class);
                intent.putExtra("url", userlinksinfo.getLinksurl());
                mContext.startActivity(intent);
                break;
            case R.id.imgMore:
                if (String.valueOf(statuses.getUserid()).equals(SharedToken.getUserId())) {
                    showDeletewWindow(view, cPosition);
                } else {
                    showMoreWindow(view);
                }
                break;
            case R.id.iv_emoji:
                if (mEmojiKeyboard.getVisibility() == View.GONE) {
                    InputMethodUtils.hideInputMethod(mContentEt);
                    mEmojiIv.setImageResource(R.drawable.input_dialog_keyboard);
                    mEmojiKeyboard.setVisibility(View.VISIBLE);
                } else {
                    mEmojiKeyboard.setVisibility(View.GONE);
                    mEmojiIv.setImageResource(R.drawable.icon_emoji);
                    InputMethodUtils.showInputMethod(mContentEt);
                }
                break;
            case R.id.iv_send:
                addComment();
                break;
            case R.id.tvSquareAttend:
                Params params = new Params();
                params.put("target_userid", statuses.getUserid());
                new HttpEntity(mContext).commonPostData(Method.userFollow, params, this);
                break;
            case R.id.linRpPickers:
                //checkPickRp();if(true)return;
                //需要修改此处。需要加进入的条件判断
                boolean isSender;
                if (redpacketDetail != null) {
                    if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                        isSender = true;
                    } else {
                        isSender = false;
                    }
                    //没有抢
                    if (redpacketDetail.getTakenamount() == 0) {
                        if (isSender) {
                            gotoRedDetailAct();
                            //领红包者 没有抢过
                        } else {
                            if (redpacketDetail.getType() == RedpacketDetail.type_random)
                                gotoRedDetailAct();
                            else {
                                //普通红包。不是发红包的人,也没有被领取。
                                switch(redpacketDetail.getStatus()) {
                                    case RedpacketDetail.status_expire:
                                    case RedpacketDetail.status_pickOver:
                                        showPickDialog();
                                        break;
                                }

                            }

                        }
                    } else {
                        //如果抢过了。全部进入详情
                        gotoRedDetailAct();
                    }
                }
                break;
        }

    }


    //是否抢过红包
    boolean isPickRp = false;

    void gotoRedDetailAct() {
        if (dialogPick != null) dialogPick.dismiss();
        Intent intent = new Intent(this, RedPacketDetailAct.class);
        intent.putExtra("redpacketid", hongbaoId);
        startActivity(intent);
    }

    /**
     * 显示抢红包的dialog。根据sender和状态
     */
    Dialog dialogPick;
    boolean isActDestroyed = false;

    void showPickDialog() {
        if (isActDestroyed) return;
        //如果打开过红包，就直接进入详情
        if (isPickRp) {
            gotoRedDetailAct();
            return;
        }
        if (dialogPick != null && dialogPick.isShowing()) {
            dialogPick.dismiss();
        }
        View view = LayoutInflater.from(this).inflate(R.layout.rp_open_dialog_design, null);
        dialogPick = new Dialog(this, R.style.dialog);
        int width = getResources().getDimensionPixelSize(R.dimen.dialogWidth);
        int height = getResources().getDimensionPixelSize(R.dimen.dialogHeight);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        dialogPick.setContentView(view, params);
        dialogPick.show();
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 800);
                switch(v.getId()) {
                    case R.id.layout_closed:
                        dialogPick.dismiss();
                        break;
                    case R.id.btn_open_money:
                        pickRedpacket();
                        break;
                    case R.id.tv_check_lucky:
                        gotoRedDetailAct();
                        break;
                }
            }
        };


        ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tv_greeting = (TextView) view.findViewById(R.id.tv_greeting);
        TextView tv_username = (TextView) view.findViewById(R.id.tv_username);

        view.findViewById(R.id.layout_closed).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_open_money).setOnClickListener(clickListener);

        View tv_check_lucky = view.findViewById(R.id.tv_check_lucky);
        TextView tv_open_title = (TextView) view.findViewById(R.id.tv_open_title);

        tv_check_lucky.setOnClickListener(clickListener);
        tv_open_title.setOnClickListener(clickListener);

        if (redpacketDetail != null) {
            tv_username.setText(redpacketDetail.getUsername());
            ImageUtils.loadCircleHead(redpacketDetail.getUsericon(), iv_avatar);
            switch(redpacketDetail.getStatus()) {
                case RedpacketDetail.status_pickabel:
                    tv_greeting.setText(redpacketDetail.getMessage());
                    switch(redpacketDetail.getType()) {
                        case RedpacketDetail.type_normal:
                            tv_open_title.setText("给你发了一个红包");
                            tv_check_lucky.setVisibility(View.INVISIBLE);
                            break;
                        case RedpacketDetail.type_random:
                            tv_open_title.setText("发了一个红包，金额随机");
                            if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                                tv_check_lucky.setVisibility(View.VISIBLE);
                            } else {
                                tv_check_lucky.setVisibility(View.INVISIBLE);
                            }
                            break;
                    }
                    break;
                //红包已经过期
                case RedpacketDetail.status_expire:
                    tv_greeting.setText("红包已过期");
                    tv_open_title.setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.linOpen).setVisibility(View.INVISIBLE);
                    if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                        tv_check_lucky.setVisibility(View.VISIBLE);
                    } else {
                        tv_check_lucky.setVisibility(View.INVISIBLE);
                    }
                    break;
                //红包抢完了
                case RedpacketDetail.status_pickOver:
                    tv_greeting.setText("手慢无，红包派完了");
                    view.findViewById(R.id.linOpen).setVisibility(View.INVISIBLE);
                    tv_open_title.setVisibility(View.INVISIBLE);

                    switch(redpacketDetail.getType()) {
                        case RedpacketDetail.type_normal:
                            tv_check_lucky.setVisibility(View.INVISIBLE);
                            break;
                        case RedpacketDetail.type_random:
                            tv_check_lucky.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
            }
        }
    }

    PopupWindow window;
    int cPosition;

    //展示删除window
    void showDeletewWindow(View targetView, int position) {
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                if (view.getId() == R.id.statuses_delete) {
                    TipsDialog.popup(mContext, "是否确定删除", "取消", "确定", new TipsDialog.OnTipsListener() {
                        @Override
                        public void onConfirm() {
                            Params params = new Params();
                            params.put("squareid", statuses.getSquareid());
                            new HttpEntity(mContext).commonPostData(Method.deleteShoppingCircle, params, mContext);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });


                }

            }
        };

        if (window != null && window.isShowing()) {
            window.dismiss();
            return;
        }
        int[] locations = new int[2];
        targetView.getLocationOnScreen(locations);
        int y = locations[1];

        int bottomHeight = targetView.getHeight() - 30;
        TextView windowView = new TextView(mContext);
        windowView.setId(R.id.statuses_delete);
        windowView.setGravity(Gravity.CENTER);
        windowView.setText(R.string.delete);
        windowView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        windowView.setTextColor(mContext.getResources().getColor(R.color.black7));
        windowView.setBackgroundResource(R.drawable.bg_statuses_main_window);
        windowView.setOnClickListener(listener);
        windowView.measure(0, 0);
        int height = windowView.getMeasuredHeight();
        int width = windowView.getMeasuredWidth();
        window = new PopupWindow(windowView, width, height);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setFocusable(true);
        window.setTouchable(true);


//        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - 40;//30  //50在s6合适
        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - DimenUtils.dp2px(4);
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, y + bottomHeight);
    }


    //展示更多操作的window
    void showMoreWindow(View targetView) {
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                switch(view.getId()) {
                    case R.id.tvItem3:
                        Intent intent = new Intent(mContext, ReportStatusesOrUserAct.class);
                        intent.putExtra("type", ReportStatusesOrUserAct.typeShoppingCircle);
                        intent.putExtra("statuses", statuses);
                        mContext.startActivity(intent);
                        break;
                }
            }
        };

        if (window != null && window.isShowing()) {
            window.dismiss();
            return;
        }
        int[] locations = new int[2];
        targetView.getLocationOnScreen(locations);
        int y = locations[1];
        int bottomHeight = targetView.getHeight() - 30;


        View windowView = LayoutInflater.from(mContext).inflate(R.layout.statuses_main_pulish_window, null);
        TextView tvItem1 = (TextView) windowView.findViewById(R.id.tvItem1);
        tvItem1.setOnClickListener(listener);
        TextView tvItem2 = (TextView) windowView.findViewById(R.id.tvItem2);
        tvItem2.setOnClickListener(listener);
        windowView.findViewById(R.id.tvItem3).setOnClickListener(listener);

        tvItem1.setVisibility(View.GONE);
        tvItem2.setVisibility(View.GONE);
        windowView.findViewById(R.id.line1).setVisibility(View.GONE);
        windowView.findViewById(R.id.line2).setVisibility(View.GONE);

        windowView.setBackgroundResource(R.drawable.bg_statuses_main_window);
        windowView.setOnClickListener(listener);
        windowView.measure(0, 0);
        int height = windowView.getMeasuredHeight();
        int width = windowView.getMeasuredWidth();
        window = new PopupWindow(windowView, width, height);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setFocusable(true);
        window.setTouchable(true);
//        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - 30;
        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - DimenUtils.dp2px(4);
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, y + bottomHeight);//Device.getHeight() - bottomHeight - height
    }

    @Override
    public void onBackPressed() {
        setResulFinishtUpdate();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

        }


    }

    public void setResulFinishtUpdate() {
        //如果表情出来了 也是先隐藏表情
        if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
            mEmojiKeyboard.setVisibility(View.GONE);
            return;
        }
        //重新设置红包状态
        if (statuses != null) {
            com.stbl.stbl.item.ad.RedPacketInfo redPacketInfo = statuses.getRpinfo();
            if (redPacketInfo != null && redpacketDetail != null) {
                redPacketInfo.setStatus(redpacketDetail.getStatus());
                statuses.setRpinfo(redPacketInfo);
            }
        }
        Intent intent = getIntent();
        intent.putExtra("item", statuses);
        setResult(StatusesFragmentShoppingCircle.requestDetailDongtai, intent);
        finish();
    }


    /**
     * 获取微博详细
     */
    void getSquareDetail() {
        mTaskManager.start(shoppingCircleTask.getDetail(statusesid)
                .setCallback(new HttpTaskCallback<ShoppingCircleDetail>(mActivity) {
                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        if (e.code == ServerError.codeStatusesNotExist) {
                            TipsDialog dialog = TipsDialog.popup(mContext, getString(R.string.tip), e.msg, getString(R.string.queding));
                            dialog.setCancelable(false);
                            dialog.setOnTipsListener(new TipsDialog.OnTipsListener() {
                                @Override
                                public void onConfirm() {
                                    finish();
                                }

                                @Override
                                public void onCancel() {
                                }
                            });
                        } else {
                            ToastUtil.showToast(e.msg);
                        }
                    }

                    @Override
                    public void onSuccess(ShoppingCircleDetail result) {
                        statuses = result;
                        setValueFromStatuses();
                    }

                    @Override
                    public void onFinish() {
                        mSwipeToLoadLayout.setRefreshing(false);
                    }
                }));
    }


    // 评论
    void addComment() {
        String content = mContentEt.getText().toString();
        if (content.equals("")) {
            ToastUtil.showToast(R.string.please_input_first);
            return;
        }

        if (content.length() > Config.remarkContentLength) {
            ToastUtil.showToast("内容过长，最多" + Config.remarkContentLength + "个文字");
            return;
        }
        mSendIv.setEnabled(false);
        final LoadingTipsDialog tipsDialog = new LoadingTipsDialog(this);
        tipsDialog.showLoading(getString(R.string.statuses_replying));
        mTaskManager.start(shoppingCircleTask.addComment(statusesid, content, 0)
                .setCallback(new HttpTaskCallback<ShoppingCircleComment>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
//                        ToastUtil.showToast(e.msg);
                        tipsDialog.showFaild(getString(R.string.statuses_reply_faild));
                        tipsDialog.postDissmiss();
                    }

                    @Override
                    public void onSuccess(ShoppingCircleComment result) {
                        tipsDialog.showSuccess(getString(R.string.statuses_reply_success));
                        tipsDialog.postDissmiss();
                        mCommentFragment.afterAddComment(result);
                        mContentEt.setText("");
                        //接着 隐藏表情栏。隐藏键盘
                        if (mEmojiKeyboard.getVisibility() == View.VISIBLE) {
                            mEmojiKeyboard.setVisibility(View.GONE);
                            return;
                        }
                        InputMethodUtils.hideInputMethod(mContentEt);

                    }

                    @Override
                    public void onFinish() {
                        mSendIv.setEnabled(true);
                    }
                }));
    }


    boolean isPauseVideo = true;

    @Override
    protected void onPause() {
        super.onPause();
        if (isPauseVideo) {
            if (VideoPlayAct.videoItemView != null && VideoPlayAct.videoItemView.isPlay()) {
                VideoPlayAct.videoItemView.pause();
                LogUtil.logE("pauseVideo");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPauseVideo = true;

        mCommentTabTv.post(new Runnable() {
            @Override
            public void run() {
                int width = mCommentTabTv.getWidth();
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(width, 4);
                params1.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_comment);
                params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
                mCommentTabLine.setLayoutParams(params1);
            }
        });

    }


    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mScrollView.scrollBy(0, mTabLayout.getBottom());
        }
    };

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;

        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.deleteShoppingCircle:
                ResultItem deleteItem = JSONHelper.getObject(result, ResultItem.class);
                if (deleteItem.getResult() == 1) {
                    ToastUtil.showToast(R.string.delete_success);
                    Intent intent2 = new Intent(ACTION.DELETE_SHOPPING_CIRCLE);
                    intent2.putExtra(KEY.SQUARE_ID, statusesid);
                    LocalBroadcastHelper.getInstance().send(intent2);
                    finish();
                }
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                // statuses.setIsshield(Statuses.isshield_yes);
                break;
            case Method.userCancelgnore:
                ToastUtil.showToast(R.string.unshield_success);
                // statuses.setIsshield(Statuses.isshield_no);
                break;
            case Method.userCancelFollow:
                //statuses.setIsattention(Statuses.isattention_no);
                ToastUtil.showToast(R.string.unfollow_success);
                break;
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                AdUserItem2 user = statuses.getUserinfo();
                user.setIsattention(Relation.isattention_yes);
                statuses.setUserinfo(user);
                tvSquareAttend.setVisibility(View.GONE);
                break;
            case Method.getRedPacketDetail:
                redpacketDetail = JSONHelper.getObject(obj, RedpacketDetail.class);
                tvHongbaoAmount.setText("¥" + StringUtil.get2ScaleString(redpacketDetail.getAmount()));
                findViewById(R.id.linHongbaoAmount).setVisibility(View.VISIBLE);
                //默认可以点击底部,如果下红包雨就不能点击。如果刚刚领取完红包,从红包详情回来,暂时也不能点
                linRpPickers.setOnClickListener(this);
                showHongbaoIcon();
                if (isPickRp) {
                    linRpPickers.setOnClickListener(null);
                }
                break;
            //抢了红包之后
            case Method.redpacketPick:
                RedpacketPickResult pickResult = JSONHelper.getObject(obj, RedpacketPickResult.class);
                PreOpenHongbaoCheck hongbaoCheckItem = pickResult.getCheckresult();
                RPtakencount = hongbaoCheckItem.getTakencount();
                switch(hongbaoCheckItem.getDenyreason()) {
                    case PreOpenHongbaoCheck.denyreasonNo:
                        isPickRp = true;
                        gotoRedDetailAct();
                        getPickersList();
                        getRedpacketDetail();
                        break;
                    case PreOpenHongbaoCheck.denyreasonNoFreeTime:
                        showRPSharetip();
                        break;
                    case PreOpenHongbaoCheck.denyreasonPicked:
                        showPickDialog();
                        break;
                    case PreOpenHongbaoCheck.denyreasonRpOver:
                        redpacketDetail.setStatus(RedpacketDetail.status_pickOver);
                        showPickDialog();
                        break;
                    case PreOpenHongbaoCheck.denyreasonRpExpire:
                        redpacketDetail.setStatus(RedpacketDetail.status_expire);
                        showPickDialog();
                        break;
                    case PreOpenHongbaoCheck.denyreasonOthers:
                    default:
                        ToastUtil.showToast(hongbaoCheckItem.getMessage());
                }
                break;
            case Method.getRedpacketPickersV1:
                RpPickersAll rpPickers = JSONHelper.getObject(obj, RpPickersAll.class);
                if (rpPickers == null) return;
                List<RpReceiveDetailItem> listPickers = rpPickers.getDatalist();
                List<StatusesReward> rewardList = new ArrayList<>(listPickers.size());
                String pickedMoney = null;

                for (int i = 0; i < listPickers.size(); i++) {
                    StatusesReward reward = new StatusesReward();
                    UserItem userItem = new UserItem();
                    userItem.setUserid(Long.valueOf(listPickers.get(i).getUserid()));
                    userItem.setNickname(listPickers.get(i).getUsername());
                    userItem.setImgurl(listPickers.get(i).getUsericon());
                    reward.setUser(userItem);
                    rewardList.add(reward);
                    if (String.valueOf(listPickers.get(i).getUserid()).equals(SharedToken.getUserId())) {
                        pickedMoney = listPickers.get(i).getAmount();
                    }
                }


                if (listPickers != null && listPickers.size() > 0) {
                    findViewById(R.id.layout_reward_list).setVisibility(View.VISIBLE);
                    mRewardUserList.clear();
                    mRewardUserList.addAll(rewardList);
                    mRewardLayout.setDataForShoppingCircle(mRewardUserList);
                    mRewardLayout.setVisibility(View.VISIBLE);
                } else {
                    mRewardLayout.setVisibility(View.GONE);
                }


                int pickPeopleNum = rpPickers.getTotalcount();
                if (pickedMoney == null || pickedMoney.equals("") || pickedMoney.equals("0") || pickedMoney.equals("0.0") || pickedMoney.equals("0.00")) {
                    String value = String.format(mContext.getString(R.string.status_pick_luckypacket_info), String.valueOf(pickPeopleNum));
                    txtRewardCount.setText(Html.fromHtml(value));
                } else {
                    String value = String.format(mContext.getString(R.string.status_pick_luckypacket_info2), String.valueOf(pickPeopleNum), pickedMoney);
                    txtRewardCount.setText(Html.fromHtml(value));
                }

                break;
            case Method.userAttentionInfo:
                Relation relation = JSONHelper.getObject(obj, Relation.class);
                if (relation == null) return;
                //如果已经关注,或者是本人
                if (relation.getIsattention() == Relation.isattention_yes || statuses.getUserinfo().getAduserid() == Long.valueOf(SharedToken.getUserId())) {
                    tvSquareAttend.setVisibility(View.GONE);
                } else {
                    tvSquareAttend.setVisibility(View.VISIBLE);
                    tvSquareAttend.setText(R.string.plus_follow);
                    tvSquareAttend.setTextColor(mContext.getResources().getColor(R.color.theme_yellow_e38));
                    tvSquareAttend.setBackgroundResource(R.drawable.shape_dongtai_item_focused);
                    tvSquareAttend.setOnClickListener(this);
                }
                break;
        }
    }

}
