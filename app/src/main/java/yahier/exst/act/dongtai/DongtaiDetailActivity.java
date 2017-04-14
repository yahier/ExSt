package yahier.exst.act.dongtai;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.adapter.CommonFragmentPagerAdapter;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.adapter.GVImgAdapter;
import com.stbl.stbl.adapter.GiftAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.ImagePagerAct;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.LinkInStatuses;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.PraiseItem;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.ServerError;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.item.UserInfo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.task.dongtai.DongtaiDetailTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.ShareUtils;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.UIUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.DongtaiDetailRewardLayout;
import com.stbl.stbl.widget.DongtaiDetailScrollView;
import com.stbl.stbl.widget.NoScrollViewPager;
import com.stbl.stbl.widget.TitleBar;
import com.stbl.stbl.widget.VideoPlayView;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiDetailActivity extends BaseActivity implements View.OnClickListener, FinalHttpCallback, VideoPlayView.OnVideoListener {

    private static final String TAG = "DongtaiDetailActivity";

    private DongtaiDetailRewardLayout mRewardLayout;

    GridView gridPic;
    LinearLayout linContainer;
    DongtaiDetailActivity mContext;

    TextView txtRewardCount;
    TextView item1, item2, item3, item4;// // 四个item
    ImageView img3, img4;
    // 用户信息
    ImageView userImg;
    TextView tvName, tvTime, tvShortContent, tvContent;
    ImageView imgRelation;
    List<StatusesComment> listRemark;

    long statusesid;

    Statuses statuses;

    View imgMore;
    final int requestForward = 101;// 转发
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;// 删除动态
    final int requestVideoFull = 105;// 转发
    RelativeLayout ll_facechoose;
    int commentCount;
    List<Gift> listGift;
    int awardCount = 0;// 打赏人数
    ImageView imgCover;

    View linLongStatusesContent;
    View linShortStatusesContent;
    View linLink;
    public final static String keyType = "statusesType";
    int statusesTypePre;//
    final String tag = getClass().getSimpleName();
    View imgAuthorized;

    private TextView mCommentTabTv;
    private TextView mPraiseTabTv;
    private View mCommentTabLine;
    private View mPraiseTabLine;

    private DongtaiCommentFragment mCommentFragment;
    private DongtaiPraiseFragment mPraiseFragment;

    private TitleBar mBar;
    private View linLongBack;

    private InputDialog mInputDialog;

    private List<StatusesReward> mRewardUserList;

    private SwipeToLoadLayout mSwipeToLoadLayout;

    private DongtaiDetailScrollView mScrollView;
    private NoScrollViewPager mViewPager;
    private RelativeLayout mTabLayout;

    private boolean mIsRemoveGlobalListener;
    private boolean mScrollToPosition;
    //VideoPlayView videoItemView;//封装了视频播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai_detail);
        mContext = this;

        statuses = (Statuses) getIntent().getSerializableExtra(DongtaiMainAdapter.key);
        mScrollToPosition = getIntent().getBooleanExtra(KEY.SCROLL_TO_POSITION, false);
        if (statuses == null) {
            statusesid = getIntent().getLongExtra("statusesId", 0);
            statusesTypePre = getIntent().getIntExtra(keyType, Statuses.type_short);
        } else {
            statusesTypePre = statuses.getStatusestype();
            statusesid = statuses.getStatusesid();
        }
        initViews();
        if (statusesTypePre == Statuses.type_long) {
            mBar.setVisibility(View.GONE);
            imgCover.setVisibility(View.VISIBLE);
            linLongBack.setVisibility(View.VISIBLE);
            int containerWidth = Device.getWidth(mContext);
            int bitmapHeight = containerWidth * Config.statusesCoverHeightScale / Config.statusesCoverWidthScale;
            imgCover.setLayoutParams(new LinearLayout.LayoutParams(containerWidth, bitmapHeight));
        } else {
            mBar.setVisibility(View.VISIBLE);
            linLongBack.setVisibility(View.GONE);
            imgCover.setVisibility(View.GONE);
        }

        initGiftWindow();
        getDongtaiDetail();
    }

    View backLong;
    View imgLongShare;
    int bannerHeight = 0;
    View tvLongTitle;

    void initViews() {
        //videoItemView = new VideoPlayView(mContext);
        linLongBack = findViewById(R.id.linLongBack);
        backLong = findViewById(R.id.backLong);
        imgLongShare = findViewById(R.id.imgLongShare);
        tvLongTitle = findViewById(R.id.tvLongTitle);
        mBar = (TitleBar) findViewById(R.id.bar);
        mBar.setTitle(R.string.statuses_detail);
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResulFinishtUpdate();
            }
        });
        mBar.setActionIcon(R.drawable.share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statuses == null) return;
                if (statuses.getLinks() != null && statuses.getLinks().getLinktype() == LinkStatuses.linkTypeVideo){
                    MobclickAgent.onEvent(DongtaiDetailActivity.this, UmengClickEventHelper.SHPFX);
                }
                ShareItem shareItem = ShareUtils.createDongtaiShare(statuses);
                LogUtil.logE("shareItem:" + shareItem.toString());
                //new CommonShare().showShareWindow(DongtaiDetailActivity.this, String.valueOf(CommonShare.sharedMiStatuses), String.valueOf(statuses.getStatusesid()), null, shareItem);
                new ShareDialog(DongtaiDetailActivity.this).shareStatus(statuses.getStatusesid(), shareItem);
            }
        });
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDongtaiDetail();
                mCommentFragment.refreshCommentList();
                mPraiseFragment.refreshPraiseList();
            }
        });
        mScrollView = (DongtaiDetailScrollView) findViewById(R.id.swipe_target);
        mViewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        initViewPagerData();
        mTabLayout = (RelativeLayout) findViewById(R.id.layBar);

        if (mScrollToPosition) {
            mTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }

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


                if (bannerHeight == 0) {
                    bannerHeight = imgCover.getHeight();
                }
                if (scrollY > bannerHeight) {
                    linLongBack.setBackgroundColor(UIUtils.getResColor(R.color.theme_yellow));
                    tvLongTitle.setVisibility(View.VISIBLE);
                } else {
                    linLongBack.setBackgroundColor(UIUtils.getResColor(R.color.transparent));
                    tvLongTitle.setVisibility(View.GONE);
                }

            }
        });

        mInputDialog = new InputDialog(this);
        mInputDialog.setOnSendListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statuses == null) {
                    return;
                }
                addComment();
            }
        });

        mCommentTabLine = findViewById(R.id.tab_line_comment);
        mPraiseTabLine = findViewById(R.id.tab_line_praise);
        mCommentTabTv = (TextView) findViewById(R.id.tv_comment);
        mPraiseTabTv = (TextView) findViewById(R.id.tv_praise);

        mCommentTabTv.setSelected(true);
        mPraiseTabTv.setSelected(false);
        mCommentTabLine.setVisibility(View.VISIBLE);
        mPraiseTabLine.setVisibility(View.GONE);

        mCommentTabTv.setOnClickListener(this);
        mPraiseTabTv.setOnClickListener(this);

        imgAuthorized = findViewById(R.id.imgAuthorized);
        imgCover = (ImageView) findViewById(R.id.imgCover);
        linLink = findViewById(R.id.linLink);
        linLongStatusesContent = findViewById(R.id.linLongStatusesContent);
        linShortStatusesContent = findViewById(R.id.linShortStatusesContent);

        imgMore = (View) findViewById(R.id.imgMore);
        imgMore.setOnClickListener(this);
        findViewById(R.id.btn_award).setOnClickListener(this);
        userImg = (ImageView) findViewById(R.id.item_iv);
        userImg.setOnClickListener(this);
        tvName = (TextView) findViewById(R.id.name);
        imgRelation = (ImageView) findViewById(R.id.relation);
        tvTime = (TextView) findViewById(R.id.time);
        linContainer = (LinearLayout) findViewById(R.id.dongtai_detail_container);
        tvShortContent = (TextView) findViewById(R.id.main_short_content);
        tvContent = (TextView) findViewById(R.id.main_long_content);
        txtRewardCount = (TextView) findViewById(R.id.tv_reward_count);
        tvShortContent.setTextIsSelectable(true);
        tvContent.setTextIsSelectable(true);

        item1 = (TextView) findViewById(R.id.item_text1);
        item2 = (TextView) findViewById(R.id.item_text2);
        item3 = (TextView) findViewById(R.id.item_text3);
        item4 = (TextView) findViewById(R.id.item_text4);
        findViewById(R.id.item_layout1).setOnClickListener(this);
        findViewById(R.id.item_layout2).setOnClickListener(this);
        findViewById(R.id.item_layout3).setOnClickListener(this);
        findViewById(R.id.item_layout4).setOnClickListener(this);

        img3 = (ImageView) findViewById(R.id.item_img3);
        img4 = (ImageView) findViewById(R.id.item_img4);

        mRewardLayout = (DongtaiDetailRewardLayout) findViewById(R.id.layout_reward);

        txtRewardCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DongtaiDetailActivity.this, DongtaiRewardActivity.class);
                intent.putExtra("statusesid", statusesid);
                startActivity(intent);
            }
        });

        gridPic = (GridView) findViewById(R.id.item_gv);
        ll_facechoose = (RelativeLayout) findViewById(R.id.ll_facechoose);
        mRewardUserList = new ArrayList<>();
    }

    private void initViewPagerData() {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        mCommentFragment = new DongtaiCommentFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putLong("statusesId", statusesid);
        mCommentFragment.setArguments(bundle1);
        fragmentList.add(mCommentFragment);

        mPraiseFragment = new DongtaiPraiseFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putLong("statusesId", statusesid);
        mPraiseFragment.setArguments(bundle2);
        fragmentList.add(mPraiseFragment);

        String[] titles = new String[]{"评论", "已赞"};
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
                    case 1:
                        mScrollView.setRefreshView(mPraiseFragment.getRefreshView());
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
     *
     * @param user
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
     * 显示链接中的动态
     *
     * @param statuses
     */
    void showStatusesLink(Statuses statuses) {
        if (statuses == null)
            return;
        View view = findViewById(R.id.link3);
        view.setVisibility(View.VISIBLE);
        linLink.setVisibility(View.VISIBLE);
        view.setOnClickListener(this);
        view.setTag(statuses);
        ImageView link3Img = (ImageView) view.findViewById(R.id.link3_img);
        TextView link3Tv = (TextView) view.findViewById(R.id.link3_content);
        TextView lin3User = (TextView) view.findViewById(R.id.link3_userName);
        if (statuses.getUser() != null) {
            LogUtil.logE("nickName:" + statuses.getUser().getNickname());
//            lin3User.setText(statuses.getUser().getNickname());
            lin3User.setText(statuses.getUser().getAlias() == null || statuses.getUser().getAlias().equals("") ? statuses.getUser().getNickname() :statuses.getUser().getAlias());
        } else {
            LogUtil.logE("nickName222:" + statuses.getUser().getNickname());
        }
        String title = statuses.getTitle();
        if (title != null && !title.equals("")) {
            LogUtil.logE("title:" + title);
            link3Tv.setText(title);
        } else {
            LogUtil.logE("content:" + statuses.getContent());
            String content = statuses.getContent().replace(Config.longWeiboFillMark, "");
            link3Tv.setText(content);
            LogUtil.logE("content:" + content);
        }
        StatusesPic link3Pics = statuses.getStatusespic();
        if (link3Pics != null && link3Pics.getPics().size() > 0) {
            String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
            PicassoUtil.loadStatuses(mContext, imgUrl, link3Img);
        } else {
            PicassoUtil.loadStatuses(mContext, link3Pics.getEx(), link3Img);
        }

    }


    void showHongbaoLink(LinkStatuses link) {
        linLink.setVisibility(View.VISIBLE);
        final View view = findViewById(R.id.link9Hongbao);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(this);
        view.setTag(link);
        TextView tvHongbaoContent = (TextView) view.findViewById(R.id.tvHongbaoContent);
        tvHongbaoContent.setText(link.getLinkex2());

        TextView tvHongbaoBrand = (TextView) view.findViewById(R.id.tvHongbaoBrand);
        tvHongbaoBrand.setText(link.getLinktitle());


        ImageView imgHongbaoImg = (ImageView) view.findViewById(R.id.link9Img);
        PicassoUtil.load(this, link.getLinkex(), imgHongbaoImg);


    }

    /**
     * 显示视频.
     */
    void showVideo(final LinkStatuses link) {
        if (link == null) return;
        linLink.setVisibility(View.VISIBLE);
        final View view = findViewById(R.id.statusesVideo);
        view.setVisibility(View.VISIBLE);
        TextView linVideoTitle = (TextView) view.findViewById(R.id.linVideoTitle);
        ImageView linVideoCover = (ImageView) view.findViewById(R.id.linVideoCover);
        final View linViewRreview = view.findViewById(R.id.linViewRreview);

        if (TextUtils.isEmpty(link.getLinktitle())) {
            linVideoTitle.setVisibility(View.GONE);
        } else {
            linVideoTitle.setText(link.getLinktitle());
        }

        PicassoUtil.load(mContext, link.getLinkex(), linVideoCover, R.drawable.dongtai_default);
        view.setOnClickListener(null);
        linViewRreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logE("video", link.getLinkurl());
                linViewRreview.setVisibility(View.GONE);
                startVideo(link.getLinkurl(), view, R.id.layout_video);
            }
        });


        if (VideoPlayAct.videoItemView == null) {
            VideoPlayAct.videoItemView = new VideoPlayView(this);
        }
        VideoPlayAct.videoItemView.setOnListener(this);
    }


    void startVideo(String url, View view, int frameLayoutId) {
        ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();//找到videoitemview的父类，然后remove
        if (last != null) {
            last.removeAllViews();
        }

        FrameLayout frameLayout = (FrameLayout) view.findViewById(frameLayoutId);
        frameLayout.removeAllViews();
        frameLayout.addView(VideoPlayAct.videoItemView);
        VideoPlayAct.videoItemView.start(url);//(listData.getList().get(position).getMp4_url());
    }

    /**
     * 显示链接中的视频
     */
    void showStatusesVideo(final LinkStatuses link) {
        if (link == null) return;
        linLink.setVisibility(View.VISIBLE);
        final View view = findViewById(R.id.link7Video);
        view.findViewById(R.id.link_layout_video).setOnClickListener(null);
        Statuses linkStatuses = link.getStatusesinfo();
        view.setVisibility(View.VISIBLE);
        TextView linkVideoTitle = (TextView) view.findViewById(R.id.linkVideoTitle);
        TextView tvLinkVideoNick = (TextView) view.findViewById(R.id.tvLinkVideoNick);
        TextView tvLinkVideoContent = (TextView) view.findViewById(R.id.tvLinkVideoContent);
        ImageView linkVideoCover = (ImageView) view.findViewById(R.id.linkVideoCover);
        final View linkViewRreview = view.findViewById(R.id.linkViewRreview);
        //显示
        if (TextUtils.isEmpty(link.getLinktitle())) {
            linkVideoTitle.setVisibility(View.GONE);
        } else {
            linkVideoTitle.setText(link.getLinktitle());
        }

//        tvLinkVideoNick.setText("@" + linkStatuses.getUser().getNickname());
        tvLinkVideoNick.setText("@" + (linkStatuses.getUser().getAlias() == null || linkStatuses.getUser().getAlias().equals("")
                ? linkStatuses.getUser().getNickname() :linkStatuses.getUser().getAlias()));
        tvLinkVideoContent.setText(linkStatuses.getContent());
        PicassoUtil.load(mContext, link.getLinkex(), linkVideoCover);

        view.setTag(linkStatuses);
        view.setOnClickListener(this);
        //最大的视频item
        //ho.link7Video.setOnClickListener(null);
        linkViewRreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ho2.linkViewRreview.setVisibility(View.GONE);
                startVideo(link.getLinkurl(), view, R.id.link_layout_video);
                linkViewRreview.setVisibility(View.GONE);
                //cPosition = i;
            }
        });

        if (VideoPlayAct.videoItemView == null) {
            VideoPlayAct.videoItemView = new VideoPlayView(this);
        }
        VideoPlayAct.videoItemView.setOnListener(this);


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

    /**
     * 得到statuses数据之后的操作
     */

    void setValueFromStatuses() {
        //新加。得到动态信息后，再加载评论和点赞
        mCommentFragment.refreshCommentList();
        mPraiseFragment.refreshPraiseList();
        StatusesPic pics = statuses.getStatusespic();
        // 判断长短微博
        int statusesType = statuses.getStatusestype();
        statusesid = statuses.getStatusesid();
        switch(statusesType) {
            case Statuses.type_long:
                imgCover.setVisibility(View.VISIBLE);
                linLongBack.setVisibility(View.VISIBLE);
                mBar.setVisibility(View.GONE);
                backLong.setOnClickListener(this);
                imgLongShare.setOnClickListener(this);

                linLongStatusesContent.setVisibility(View.VISIBLE);
                linShortStatusesContent.setVisibility(View.GONE);
                imgCover.setVisibility(View.VISIBLE);
                gridPic.setVisibility(View.GONE);
                String title = statuses.getTitle();
                if (title.equals("")) {
                    tvContent.setVisibility(View.GONE);
                } else {
                    tvContent.setVisibility(View.VISIBLE);// visible
                    tvContent.setText(title);
                    checkAutoLink(tvContent);
                }
                linLink.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                parseLongContent();
                break;
            case Statuses.type_short:
                imgCover.setVisibility(View.GONE);
                linLongBack.setVisibility(View.GONE);
                mBar.setVisibility(View.VISIBLE);
                linLongStatusesContent.setVisibility(View.GONE);
                linShortStatusesContent.setVisibility(View.VISIBLE);
                //gridPic.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                String content = statuses.getContent();
                if (content == null || content.equals("")) {
                    tvShortContent.setVisibility(View.GONE);
                } else {
                    tvShortContent.setVisibility(View.VISIBLE);
                    if (content != null && !content.equals("")) {
                        content = Util.halfToFull(content);
                    }
                    tvShortContent.setText(content);
                    checkAutoLink(tvShortContent);
                }

                linContainer.setVisibility(View.GONE);
                int columnNum = GVImgAdapter.gridColumn;
                int picSize = pics.getPics().size();
                GVImgAdapter gridAdapter = new GVImgAdapter(mContext, pics, columnNum);
                if (pics == null || picSize == 0) {
                    gridPic.setVisibility(View.GONE);
                } else if (picSize == 1) {
                    gridAdapter.setType(GVImgAdapter.typeShort1);
                    columnNum = picSize;
                } else if (picSize < 4) {
                    columnNum = picSize;
                    gridAdapter = new GVImgAdapter(mContext, pics, columnNum);
                    gridAdapter.setType(GVImgAdapter.typeShort);
                } else {
                    gridAdapter.setType(GVImgAdapter.typeShort);
                }
                gridPic.setNumColumns(columnNum);
                gridPic.setAdapter(gridAdapter);
                ViewUtils.setAdapterViewHeight(gridPic, columnNum);
                gridPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent(mContext, ImagePagerAct.class);
                        intent.putExtra("index", arg2);
                        intent.putExtra("pics", statuses.getStatusespic());
                        startActivity(intent);

                    }
                });
                //gridPic.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                //linLink.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                break;
        }

        awardCount = statuses.getRewardcount();
        setRewardCountTextView();
        UserItem user = statuses.getUser();
        int accountType = user.getAccounttype();
        if (accountType == UserItem.accountTypeSystem) {
            if (String.valueOf(statuses.getPublisheruserid()).equals(SharedToken.getUserId())) {
                imgMore.setVisibility(View.VISIBLE);
            } else {
                imgMore.setVisibility(View.GONE);
            }

        }
        ImageUtils.loadHead(user.getImgurl(), userImg);
        if (user.getCertification() == UserItem.certificationYes) {
            imgAuthorized.setVisibility(View.VISIBLE);
        } else {
            imgAuthorized.setVisibility(View.GONE);
        }
//        tvName.setText(user.getNickname());
        tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias());

        if (Relation.isMaster(user.getRelationflag())) {
            imgRelation.setVisibility(View.VISIBLE);
            imgRelation.setImageResource(R.drawable.icon_master);//R.drawable.relation_master_bg);
        } else if (Relation.isStu(user.getRelationflag())) {
            imgRelation.setVisibility(View.VISIBLE);
            imgRelation.setImageResource(R.drawable.icon_student);
        } else {
            imgRelation.setVisibility(View.GONE);
        }

        tvTime.setText(DateUtil.getTimeOff(statuses.getCreatetime()));
        item1.setText(statuses.getForwardcount() + "");
        commentCount = statuses.getCommentcount();
        item2.setText(String.valueOf(commentCount));
        item3.setText(statuses.getPraisecount() + "");
        item4.setText(statuses.getFavorcount() + "");

        if (statuses.getIspraised() == Statuses.ispraisedYes) {
            img3.setImageResource(R.drawable.dongtai_praise_presed);
        } else {
            img3.setImageResource(R.drawable.dongtai_item3);
        }

        if (statuses.getIsfavorited() == Statuses.isfavoritedYes) {
            img4.setImageResource(R.drawable.dongtai_collect_pressed);
        } else {
            img4.setImageResource(R.drawable.dongtai_item4);
        }

        LinkStatuses link = statuses.getLinks();
        if (link != null) {
            int linkType = link.getLinktype();
            switch(linkType) {
                case LinkStatuses.linkTypeCrad:
                    UserItem userLink = link.getUserinfo();
                    showCardLink(userLink);
                    break;
                case LinkStatuses.linkTypeWish:
                    break;
                case LinkStatuses.linkTypeStateses:
                    Statuses linkStatuses = link.getStatusesinfo();
                    if (link.getLinkex2() != null && link.getLinkex2().equals(LinkStatuses.linkex2VideoTag)) {
                        showStatusesVideo(link);
                        break;
                    }
                    showStatusesLink(linkStatuses);
                    break;
                case LinkStatuses.linkTypeProduct:
                    Goods goods = link.getProductinfo();
                    showGoodsLink(goods);
                    break;
                case LinkStatuses.linkTypeLive:
                    showLiveLink(statuses);
                    break;
                case LinkStatuses.linkTypeNiceLink:
                    showLink(link.getUserlinksinfo());
                    break;
                case LinkStatuses.linkTypeVideo:
                    showVideo(link);
                    break;
                case LinkStatuses.linkTypeHongbao:
                    showHongbaoLink(link);
                    break;

            }

        }

        List<StatusesReward> rewardList = statuses.getRewarduserlst();
        if (rewardList != null && rewardList.size() > 0) {
            findViewById(R.id.layout_reward_list).setVisibility(View.VISIBLE);
            mRewardUserList.clear();
            mRewardUserList.addAll(rewardList);
            mRewardLayout.setData(mRewardUserList);
        } else {
            findViewById(R.id.layout_reward_list).setVisibility(View.GONE);
        }
    }


    void checkAutoLink(TextView tv){
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable)text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setText(style);
        }
    }

    private class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(mContext, CommonWeb.class);
            intent.putExtra("url", mUrl);
            startActivity(intent);
        }
    }

    private void setRewardCountTextView() {
        String awardCountStr = String.valueOf(awardCount);
        SpannableString span = new SpannableString(awardCountStr);
        span.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_orange)), 0, awardCountStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtRewardCount.setText("");
        txtRewardCount.append(span);
        txtRewardCount.append(getString(R.string.people_award));
    }

    /**
     * 显示直播链接
     */
    void showLiveLink(Statuses statuses) {
        if (statuses == null) return;
        LinkStatuses link = statuses.getLinks();
        UserItem user = statuses.getUser();
        if (link == null || user == null) return;
        link.setUserhead(user.getImgurl());
//        link.setUsername(user.getNickname());
        link.setUsername(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias());

        View view = findViewById(R.id.link5);
        view.setVisibility(View.VISIBLE);
        linLink.setVisibility(View.VISIBLE);
        view.setOnClickListener(this);
        view.setTag(link);
        TextView tvUserlive = (TextView) view.findViewById(R.id.tv_user_live);
        tvUserlive.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias() + "开启了直播");
//        tvUserlive.setText(user.getNickname() + "开启了直播");
        TextView tvTitle = (TextView) view.findViewById(R.id.link5_content);
        tvTitle.setText("话题：" + link.getLinktitle());
    }


    void addTextView(String content) {
        if (content != null && !content.equals("")) {
            content = Util.halfToFull(content);
        }
        EmojiconTextView text = new EmojiconTextView(this);
        text.setTextAppearance(this, R.style.statuses_long_content);
        text.setTextSize(15);//sp
        text.setEmojiconSize(32);//px
        text.setTextIsSelectable(true);
        text.setTextColor(getResources().getColor(R.color.font_black_gray));
        text.setPadding(0, 5, 0, 5);
        text.setLineSpacing(6, 1);
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setText(content);
        linContainer.addView(text, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 解析长微博内容
     */
    void parseLongContent() {
        if (linContainer.getChildCount() != 0) {
            linContainer.removeAllViews();
        }
        String contentSource = statuses.getContent();
        if (contentSource == null) {
            //ToastUtil.showToast(this, "长微博的内容为空 所以没有解析了");
            return;
        }
        int imageIndex = 0;
        StatusesPic bigPic = statuses.getStatusespic();
        // 展示封面
        String coverUrl = bigPic.getDefaultpic();
        if (coverUrl != null && !coverUrl.equals("")) {
            coverUrl = bigPic.getOriginalpic() + coverUrl;
            ImageUtils.loadBitmap(coverUrl, imgCover, new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                    ToastUtil.showToast(R.string.failed_to_load_cover_img);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                    int containerWidth = Device.getWidth(mContext);
                    int bitmapHeight = containerWidth * bitmap.getHeight() / bitmap.getWidth();
                    imgCover.setLayoutParams(new LinearLayout.LayoutParams(containerWidth, bitmapHeight));
                    return false;
                }
            });
        }

        // 解析图文
        while (contentSource.length() > 0) {
            int index = contentSource.indexOf(Config.longWeiboFillMark);
            if (index == 0) {
                // 显示图片
                addImageView(bigPic, imageIndex);
                imageIndex++;
                contentSource = contentSource.substring(Config.longWeiboFillMark.length());
            } else if (index > 0) {
                // 显示文字
                addTextView(contentSource.substring(0, index));
                contentSource = contentSource.substring(index);
            } else {
                // 显示最后的文字
                addTextView(contentSource);
                break;
            }

        }

    }

    void addImageView(StatusesPic bigPic, final int index) {
        try {
            final ImageView img = new ImageView(this);
            img.setPadding(0, 5, 0, 5);
            linContainer.addView(img);
            img.getLayoutParams().width = linContainer.getWidth();
            String url = bigPic.getOriginalpic() + bigPic.getPics().get(index);
            ImageUtils.loadBitmap(url, img, new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String s, Target<Bitmap> target, boolean b) {
                    ToastUtil.showToast(R.string.failed_load_img);
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap bitmap, String s, Target<Bitmap> target, boolean b, boolean b1) {
                    int containerWidth = linContainer.getWidth();// - paddingAmount;// -20;
                    int bitmapHeight = containerWidth * bitmap.getHeight() / bitmap.getWidth();
                    img.setLayoutParams(new LinearLayout.LayoutParams(containerWidth, bitmapHeight));
                    return false;
                }
            });

            img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(mContext, ImagePagerAct.class);
                    intent.putExtra("index", index);
                    intent.putExtra("pics", statuses.getStatusespic());
                    startActivity(intent);

                }
            });
        } catch(Exception e) {
            ToastUtil.showToast(R.string.failed_to_parse_long_statuses);
        }

    }


    @TargetApi (Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        if (statuses == null)
            return;
        Intent intent;
        switch(view.getId()) {
            case R.id.tv_comment: //评论tab
                mCommentTabTv.setSelected(true);
                mPraiseTabTv.setSelected(false);
                mCommentTabLine.setVisibility(View.VISIBLE);
                mPraiseTabLine.setVisibility(View.GONE);
                //切换tab
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.tv_praise: //已赞tab
                mCommentTabTv.setSelected(false);
                mPraiseTabTv.setSelected(true);
                mCommentTabLine.setVisibility(View.GONE);
                mPraiseTabLine.setVisibility(View.VISIBLE);
                //切换tab
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.item_iv:
                if (statuses == null) {
                    return;
                }
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", statuses.getUser().getUserid());
                intent.putExtra("typeEntry", TribeMainAct.typeEntryOther);
                mContext.startActivity(intent);
                break;
            case R.id.btn_award: {
                if (statuses == null) {
                    return;
                }
                UserItem user = statuses.getUser();
                if (Long.valueOf(SharedToken.getUserId()) == user.getUserid()) {
                    ToastUtil.showToast(R.string.can_not_award_self);
                    return;
                }
                beforeShowGiftDialog();
            }

            break;
//            case R.id.praise_more:
//                if (statuses == null)
//                    return;
//                Intent intent3 = new Intent(mContext, DongtaiPraisedAct.class);
//                intent3.putExtra("item", statuses);
//                mContext.startActivity(intent3);
//                break;
            case R.id.item_layout1: //转发
                if (statuses == null || statuses.getUser() == null) {
                    return;
                }
                if (statuses.getLinks() != null && statuses.getLinks().getLinktype() == LinkStatuses.linkTypeVideo){
                    MobclickAgent.onEvent(DongtaiDetailActivity.this, UmengClickEventHelper.SPZF);
                }
                Intent intent1 = new Intent(this, DongtaiRepost.class);
                intent1.putExtra("linkType", LinkStatuses.linkTypeStateses);
                intent1.putExtra("typeSource", DongtaiPulish.typeForward);
                intent1.putExtra("data", statuses);
                startActivityForResult(intent1, requestForward);
                break;
            case R.id.item_layout2: //评论
                mInputDialog.show();
                break;
            case R.id.item_layout3: //点赞
                if (statuses == null)
                    return;
                praiseOrUnpraiseDongtai();
                break;
            case R.id.item_layout4: //收藏
                if (statuses == null)
                    return;
                collectOrUncollectDongtai();
                break;
            case R.id.window_close:
                dialogGift.dismiss();
                break;
            case R.id.link1:
                UserItem user = (UserItem) view.getTag();
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", user.getUserid());
                startActivity(intent);
                break;
            case R.id.link2:
                break;
            case R.id.link3:
                Statuses statusesTag = (Statuses) view.getTag();
                Intent i = new Intent(mContext, DongtaiDetailActivity.class);
                i.putExtra("statusesId", statusesTag.getStatusesid());
                startActivity(i);
                break;
            case R.id.link4:
                Goods goods = (Goods) view.getTag();
                intent = new Intent(mContext, MallGoodsDetailAct.class);
                intent.putExtra("goodsid", goods.getGoodsid());
                mContext.startActivity(intent);
                break;
            case R.id.link7Video:
                Statuses statuses2 = (Statuses) view.getTag();
                intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", statuses2.getStatusesid());
                startActivity(intent);
                break;
            case R.id.link8Link:
                LinkInStatuses userlinksinfo = (LinkInStatuses) view.getTag();
                intent = new Intent(mContext, CommonWeb.class);
                intent.putExtra("url", userlinksinfo.getLinksurl());
                mContext.startActivity(intent);
                break;
            case R.id.link9Hongbao:
                LinkStatuses link9 = (LinkStatuses) view.getTag();
                intent = new Intent(mContext, AdDongtaiDetailAct.class);
                intent.putExtra("statusesId", link9.getLinkid());
                mContext.startActivity(intent);
                break;
            case R.id.imgMore:
                if (String.valueOf(statuses.getPublisheruserid()).equals(SharedToken.getUserId())) {
                    showDeletewWindow(view);
                } else {
                    showMoreWindow(view);
                }
                break;
            case R.id.backLong:
                setResulFinishtUpdate();
                break;
            case R.id.imgLongShare:
                if (statuses == null) return;
                ShareItem shareItem = ShareUtils.createDongtaiShare(statuses);
                LogUtil.logE("shareItem:" + shareItem.toString());
                //new CommonShare().showShareWindow(DongtaiDetailActivity.this, String.valueOf(CommonShare.sharedMiStatuses), String.valueOf(statuses.getStatusesid()), null, shareItem);
                new ShareDialog(this).shareLongStatus(statuses.getStatusesid(), shareItem);
                break;
        }

    }


    PopupWindow window;

    //展示删除window
    void showDeletewWindow(View targetView) {
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                if (view.getId() == R.id.statuses_delete) {
                    TipsDialog.popup(mContext, "是否确定删除", "取消", "确定", new TipsDialog.OnTipsListener() {
                        @Override
                        public void onConfirm() {
                            Params params = new Params();
                            params.put("statusesid", statuses.getStatusesid());
                            new HttpEntity(mContext).commonPostData(Method.weiboDel, params, mContext);
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
        windowView.setOnClickListener(listener);
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
                    case R.id.tvItem1:
                        String meId = SharedToken.getUserId();
                        Params params = new Params();
                        params.put("userid", meId);
                        params.put("target_userid", statuses.getPublisheruserid());
                        if (statuses.getIsshield() == Relation.isshield_yes) {
                            //tvItem1.setText("看Ta的动态");
                            new HttpEntity(mContext).commonPostData(Method.userCancelgnore, params, mContext);
                        } else {
                            // tvItem1.setText("不看Ta的动态");
                            new HttpEntity(mContext).commonPostData(Method.userIgnore, params, mContext);
                        }

                        break;
                    case R.id.tvItem2:
                        if (statuses.getIsattention() == Relation.isattention_yes) {
                            Params params1 = new Params();
                            params1.put("target_userid", statuses.getPublisheruserid());
                            new HttpEntity(mContext).commonPostData(Method.userCancelFollow, params1, mContext);
                        } else {
                            Params params2 = new Params();
                            params2.put("target_userid", statuses.getPublisheruserid());
                            new HttpEntity(mContext).commonPostData(Method.userFollow, params2, mContext);
                        }

                        break;
                    case R.id.tvItem3:
                        Intent intent = new Intent(mContext, ReportStatusesOrUserAct.class);
                        intent.putExtra("type", ReportStatusesOrUserAct.typeStatuses);
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


        if (statuses.getIsshield() == Relation.isshield_yes) {
            tvItem1.setText(R.string.see_his_statuses);
        } else {
            tvItem1.setText(R.string.not_see_his_statuses);
        }

        if (statuses.getIsattention() == Relation.isattention_yes) {
            tvItem1.setVisibility(View.VISIBLE);
            windowView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
            tvItem2.setText(R.string.unfollow);
        } else {
            tvItem1.setVisibility(View.GONE);
            windowView.findViewById(R.id.line1).setVisibility(View.GONE);
            tvItem2.setText(R.string.follow);

        }
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
        // 从转发页面回来
        if (requestCode == requestForward) {
            if (resultCode == requestForward) {
                int count = Integer.valueOf(item1.getText().toString());
                item1.setText(String.valueOf(count + 1));
            }
        }

        switch(requestCode) {
            case requestForward:
                if (resultCode == requestForward) {
                    int count = Integer.valueOf(item1.getText().toString());
                    item1.setText(String.valueOf(count + 1));
                }
                break;
            case requestVideoFull://从全屏页面回来
                ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();
                if (last != null) {
                    last.removeAllViews();
                }
                VideoPlayAct.videoItemView.setOnListener(this);
                LinkStatuses link = statuses.getLinks();
                View view;
                FrameLayout frameLayout;
                int linkType = link.getLinktype();
                switch(linkType) {
                    case LinkStatuses.linkTypeStateses:
                        view = findViewById(R.id.link7Video);
                        frameLayout = (FrameLayout) view.findViewById(R.id.link_layout_video);
                        frameLayout.removeAllViews();
                        frameLayout.addView(VideoPlayAct.videoItemView);
                        // VideoPlayAct.videoItemView.restart();//
                        break;
                    case LinkStatuses.linkTypeVideo:
                        view = findViewById(R.id.statusesVideo);
                        frameLayout = (FrameLayout) view.findViewById(R.id.layout_video);
                        frameLayout.removeAllViews();
                        frameLayout.addView(VideoPlayAct.videoItemView);
                        //  VideoPlayAct.videoItemView.restart();//
                        break;

                }


                break;
        }


    }

    public void setResulFinishtUpdate() {
        int count1 = Integer.valueOf(item1.getText().toString());
        int count2 = Integer.valueOf(item2.getText().toString());
        int count3 = Integer.valueOf(item3.getText().toString());
        int count4 = Integer.valueOf(item4.getText().toString());
        if (statuses == null) {
            finish();
            return;
        }
        statuses.setForwardcount(count1);
        statuses.setCommentcount(count2);
        statuses.setPraisecount(count3);
        statuses.setFavorcount(count4);
        Intent intent = new Intent();
        intent.putExtra("item", statuses);
        setResult(resultUpdateCode, intent);
        finish();
    }


    Dialog dialogGift;
    GridView gridGift;

    void initGiftWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.award_window, null);
        gridGift = (GridView) view.findViewById(R.id.grid);
        dialogGift = new Dialog(this, R.style.dialog);
        dialogGift.setContentView(view);
        gridGift.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dialogGift.dismiss();
                GiftAdapter adapter = (GiftAdapter) gridGift.getAdapter();
                sendGift(adapter.getItem(arg2).getGiftid(), adapter.getItem(arg2).getValue());
            }
        });
    }

    void showGiftWindow(List<Gift> listGift) {
        if (listGift == null || listGift.size() == 0)
            return;
        GiftAdapter adapter = new GiftAdapter(this, listGift);
        gridGift.setAdapter(adapter);
        dialogGift.show();
    }


    /**
     * 获取微博详细
     */
    void getDongtaiDetail() {
        mTaskManager.start(DongtaiDetailTask.getDongtaiDetail(statusesid)
                .setCallback(new HttpTaskCallback<Statuses>(mActivity) {
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
                    public void onSuccess(Statuses result) {
                        statuses = result;
                        setValueFromStatuses();
                    }

                    @Override
                    public void onFinish() {
                        mSwipeToLoadLayout.setRefreshing(false);
                    }
                }));
    }

    // 获取礼物列表
    void beforeShowGiftDialog() {
        if (listGift == null) {
            getGiftList();
        } else {
            showGiftWindow(listGift);
        }
    }

    private void getGiftList() {
        mTaskManager.start(DongtaiDetailTask.getGiftList()
                .setCallback(new HttpTaskCallback<ArrayList<Gift>>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<Gift> result) {
                        listGift = result;
                        showGiftWindow(listGift);
                    }
                }));
    }

    boolean isActDestroyed = false;

    // 给礼物. 打赏
    void sendGift(final int giftid, int goldValue) {
        Payment.getPassword(this, goldValue, new PayingPwdDialog.OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                dialogGift.dismiss();
                if (!isActDestroyed)
                    WaitingDialog.show(mContext, R.string.waiting, false);
                mTaskManager.start(DongtaiDetailTask.sendGift(statusesid, giftid, pwd)
                        .setCallback(new HttpTaskCallback<Long>(mActivity) {

                            @Override
                            public void onError(TaskError e) {
                                super.onError(e);
                                ToastUtil.showToast(e.msg);
                            }

                            @Override
                            public void onSuccess(Long result) {
                                ToastUtil.showToast(R.string.award_success);
                                if (awardCount == 0) {
                                    findViewById(R.id.layout_reward_list).setVisibility(View.VISIBLE);
                                }
                                StatusesReward reward = new StatusesReward();
                                UserItem user = SharedUser.getUserItem();
                                // UserItem user = new UserItem();
                                //user.setImgurl(SharedUser.getPhotoUrl(MyApplication.getContext()));
                                //user.setUserid(Long.valueOf(SharedToken.getUserId(MyApplication.getContext())));
                                reward.setUser(user);
                                mRewardUserList.add(0, reward);
                                mRewardLayout.setData(mRewardUserList);
                                awardCount++;
                                setRewardCountTextView();
                            }

                            @Override
                            public void onFinish() {
                                WaitingDialog.dismiss();
                            }
                        }));
            }
        });
    }

    // 评论
    void addComment() {
        String content = mInputDialog.getContent();
        if (content.equals("")) {
            ToastUtil.showToast(R.string.please_input_first);
            return;
        }

        if (content.length() > Config.remarkContentLength) {
            ToastUtil.showToast("内容过长，最多" + Config.remarkContentLength + "个文字");
            return;
        }
        mInputDialog.setSendButtonEnable(false);
        final LoadingTipsDialog loadingTipsDialog = new LoadingTipsDialog(this);
        loadingTipsDialog.showLoading(getString(R.string.statuses_replying));
        mTaskManager.start(DongtaiDetailTask.addComment(statusesid, content, 0)
                .setCallback(new HttpTaskCallback<StatusesComment>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
//                        ToastUtil.showToast(e.msg);
                        loadingTipsDialog.showFaild(getString(R.string.statuses_reply_faild));
                        loadingTipsDialog.postDissmiss();
                    }

                    @Override
                    public void onSuccess(StatusesComment result) {
                        loadingTipsDialog.showSuccess(getString(R.string.statuses_reply_success));
                        loadingTipsDialog.postDissmiss();
                        mInputDialog.hideFaceView();
                        mCommentFragment.afterAddComment(result);
                        mInputDialog.clearContent();
                        afterAddComment();
                        //新加
                        new DongtaiRemarkDB(mContext).insertToDB(mCommentFragment.getData());
                    }

                    @Override
                    public void onFinish() {
                        mInputDialog.setSendButtonEnable(true);
                    }
                }));
    }

    public void afterAddComment() {
        commentCount++;
        item2.setText(commentCount + "");
    }

    public void afterDeleteComment() {
        commentCount--;
        if (commentCount < 0) {
            commentCount = 0;
        }
        item2.setText(commentCount + "");
    }


    void praiseOrUnpraiseDongtai() {
        mTaskManager.start(DongtaiDetailTask.praiseOrUnpraiseDongtai(statusesid)
                .setCallback(new HttpTaskCallback<PraiseResult>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(PraiseResult result) {
                        PraiseResult praiseResult = result;
                        if (praiseResult.getType() == PraiseResult.type_add) {
//                    ToastUtil.showToast("点赞成功");
                            statuses.setIspraised(Statuses.ispraisedYes);
                            PraiseItem praiseItem = new PraiseItem();
                            UserInfo user = new UserInfo();
                            UserItem userItem = SharedUser.getUserItem();
                            user.setUserid(userItem.getUserid());
                            user.setNickname(userItem.getNickname());
                            user.setAlias(userItem.getAlias());
                            user.setImgurl(userItem.getImgmiddleurl());
                            praiseItem.setUser(user);
                            praiseItem.setStatusesId(statusesid);
                            praiseItem.setPraisetime(System.currentTimeMillis() / 1000);
                            mPraiseFragment.afterPraiseDongtai(praiseItem);
                        } else if (praiseResult.getType() == PraiseResult.type_cancel) {
//                    ToastUtil.showToast("取消点赞成功");
                            statuses.setIspraised(Statuses.ispraisedNo);
                            mPraiseFragment.afterUnpraiseDongtai();
                        }
                        StatusesAnimUtil.showAnimPraiseAnim(DongtaiDetailActivity.this, item3, img3, result, statuses);
                    }
                }));
    }

    // 收藏
    void collectOrUncollectDongtai() {
        if (statuses == null)
            return;
        int isCollected = statuses.getIsfavorited();
        Params params = new Params();
        params.put("statusesid", statusesid + "");
        if (isCollected == Statuses.ispraisedYes) {
            mTaskManager.start(DongtaiDetailTask.uncollectDongtai(statusesid)
                    .setCallback(new HttpTaskCallback<Collect>(mActivity) {

                        @Override
                        public void onError(TaskError e) {
                            super.onError(e);
                            ToastUtil.showToast(e.msg);
                        }

                        @Override
                        public void onSuccess(Collect result) {
                            statuses.setIsfavorited(Statuses.isfavoritedNo);
                            StatusesAnimUtil.showAnimCancelCollect(DongtaiDetailActivity.this, item4, img4, result.getCollectcount());
                        }
                    }));
        } else {
            mTaskManager.start(DongtaiDetailTask.collectDongtai(statusesid)
                    .setCallback(new HttpTaskCallback<Collect>(mActivity) {

                        @Override
                        public void onError(TaskError e) {
                            super.onError(e);
                            ToastUtil.showToast(e.msg);
                        }

                        @Override
                        public void onSuccess(Collect result) {
                            statuses.setIsfavorited(Statuses.isfavoritedYes);
                            StatusesAnimUtil.showAnimCollect(DongtaiDetailActivity.this, item4, img4, result.getCollectcount());
                        }
                    }));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActDestroyed = true;
        if (VideoPlayAct.videoItemView != null) {
            VideoPlayAct.videoItemView.stop();
        }

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

                int width2 = mPraiseTabTv.getWidth();
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width2, 4);
                params2.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_praise);
                params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
                mPraiseTabLine.setLayoutParams(params2);
                LogUtil.logE("onresume", width + "" + width2);
            }
        });

    }

    //    public void pauseVideo() {
//        if (VideoPlayAct.videoItemView != null && VideoPlayAct.videoItemView.isPlay()) {
//            VideoPlayAct.videoItemView.pause();
//            LogUtil.logE("pauseVideo");
//        }
//    }

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
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.weiboDel:
                //删除了
                ToastUtil.showToast(R.string.delete_success);
                Intent intent = getIntent();
                intent.putExtra(KEY.STATUSES_ID, statusesid);
                setResult(resultDeleteCode, intent);
                Intent intent2 = new Intent(ACTION.DONGTAI_DELETED);
                intent2.putExtra(KEY.STATUSES_ID, statusesid);
                LocalBroadcastHelper.getInstance().send(intent2);
                finish();
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                statuses.setIsshield(Statuses.isshield_yes);
                break;
            case Method.userCancelgnore:
                ToastUtil.showToast(R.string.unshield_success);
                statuses.setIsshield(Statuses.isshield_no);
                break;
            case Method.userCancelFollow:
                statuses.setIsattention(Statuses.isattention_no);
                ToastUtil.showToast(R.string.unfollow_success);
                break;
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                statuses.setIsattention(Statuses.isattention_yes);
                break;
        }
    }


    @Override
    public void onFullCilck() {
        isPauseVideo = false;
        Intent intent = new Intent(this, VideoPlayAct.class);
        startActivityForResult(intent, requestVideoFull);
    }
}
