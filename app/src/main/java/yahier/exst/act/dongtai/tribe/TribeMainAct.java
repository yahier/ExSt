package yahier.exst.act.dongtai.tribe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cunoraz.tagview.Utils;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.act.ad.TribeTudiAdActivity;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.adapter.DongtaiSquareAdapter;
import com.stbl.stbl.adapter.GiftAdapter;
import com.stbl.stbl.adapter.mine.TribeListAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.ShareDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.DynamicNew;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.item.Masters;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.ShareItem;
import com.stbl.stbl.item.SignListResult;
import com.stbl.stbl.item.SignTribe;
import com.stbl.stbl.item.TribeAllItem;
import com.stbl.stbl.item.TribeUserItem;
import com.stbl.stbl.item.TribeUserView;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.model.TribeAd;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.task.mine.TribeTask;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.AdHelper;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.utils.UIUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.DialogFactory;
import com.stbl.stbl.widget.TribeHeaderView;
import com.stbl.stbl.widget.refresh.OnLoadMoreListener;
import com.stbl.stbl.widget.refresh.OnRefreshListener;
import com.stbl.stbl.widget.refresh.SwipeToLoadLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 部落主页
 *
 * @author lenovo
 */
public class TribeMainAct extends BaseActivity implements OnClickListener, FinalHttpCallback {

    public final static int typeEntryOther = 0;
    public final static int typeEntrySelf = 1;
    int typeEntry = typeEntryOther;

    private static final int REQUEST_SETTING = 0x1000;
    private static final int COUNT = 4;

    Context mContext;
    long userId;
    TribeUserItem userItem;

    TextView btnFriend;
    TextView btnFollow;// 关注，加好友，已是好友

    public Relation relation;
    boolean isFollowed = false;

    ImageView expandedImageView;
    /**
     * 头像缩放动画
     */
    private Animator mCurrentAnimator;
    /**
     * 头像缩放动画持续时间
     */
    private int mShortAnimationDuration = 300;
    /**
     * 做头像动画需要的bitmap
     */
    public Bitmap mAminBitmp;

    private Dialog mSaveImgDialog;

    public ImageView imgMore;
    ImageView imgShare;

    List<Gift> listGift;
    Dialog dialogGift;

    boolean isActDestroyed = false;

    PopupWindow window;

    Masters elders;

    Dialog dialog;
    TextView tvMiddle;
    TribeAllItem tribe;

    private RecyclerView mRecyclerView;
    private TribeListAdapter mRecyclerAdapter;
    private ArrayList<Ad> mAdList;
    private TribeHeaderView mHeaderView;
    private SwipeToLoadLayout mSwipeToLoadLayout;

    private int mPage = 1;
    View theme_top_banner;

    private ImageView mBackView;
    private View mDividerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tribe_main_act);
        mContext = this;
        userId = mActivity.getIntent().getLongExtra("userId", 0);
        if (userId == 0) {
            finish();
            return;
        }

        initViews();
        setTextByUserType();
        initGiftWindow();

        EventBus.getDefault().register(this);
        getTribeAllData();
        getMasters();
        LocalBroadcastHelper.getInstance().register(mReceiver, ACTION.UPDATE_SIGN_IN_LIST);
    }

    float scrolledVerticalPix = 0;

    void initViews() {
        theme_top_banner = findViewById(R.id.theme_top_banner);
        mBackView = (ImageView) findViewById(R.id.theme_top_banner_left);
        mBackView.setOnClickListener(this);

        mDividerView = findViewById(R.id.toolbar_divider);

        tvMiddle = (TextView) findViewById(R.id.theme_top_banner_middle);
        imgMore = (ImageView) findViewById(R.id.imgMore);
        imgShare = (ImageView) findViewById(R.id.imgShare);

        btnFriend = (TextView) findViewById(R.id.btnFriend);
        btnFollow = (TextView) findViewById(R.id.btnFollow);

        ArrayList<String> actionList = new ArrayList<>();
        actionList.add(getString(R.string.save_to_phone));
        mSaveImgDialog = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSaveImgDialog.dismiss();
                FileUtils.saveImage(userItem.getImgmiddleurl(), userItem.getUserid() + "_" + userItem.getNickname());
            }
        });
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);

        mAdList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.swipe_target);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else if (position == 1 && mAdList.size() == 1) {
                    return 2;
                } else if (position == mAdList.size() + 1) {//加载更多，单行
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        final int margin = DimenUtils.dp2px(12);
        final int padding = DimenUtils.dp2px(8);
        final int half = DimenUtils.dp2px(4);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                if (itemPosition == 0) {
                    outRect.set(0, 0, 0, 0);
                } else if (itemPosition == mAdList.size() + 1) {//加载更多，单行
                    outRect.set(0, 0, 0, 0);
                } else if (itemPosition == 1 && mAdList.size() == 1) {
                    //outRect.set(margin*3, 0, margin, 0);
                    outRect.set(0, 0, 0, 0);
                } else if (itemPosition == 1) {
                    outRect.set(margin, 0, half, 0);
                    //LogUtil.logE("margin", "1");
                } else if (itemPosition == 2) {
                    outRect.set(half, 0, margin, 0);
                    //LogUtil.logE("margin", "2");
                } else if (itemPosition % 2 == 1) {
                    outRect.set(margin, padding, half, 0);
                    //LogUtil.logE("margin", "3");
                } else if (itemPosition % 2 == 0) {
                    outRect.set(half, padding, margin, 0);
                    // LogUtil.logE("margin", "4");
                }
            }
        });
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipe_to_load_layout);
        mSwipeToLoadLayout.setRefreshEnabled(false);
        mSwipeToLoadLayout.setLoadMoreEnabled(false);
        mSwipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerAdapter.setLoadingMore();
                getTribeAd();
            }
        });


        final int topBannerHeight = Utils.dipToPx(this, 180);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledVerticalPix = scrolledVerticalPix + dy;
                if (scrolledVerticalPix < topBannerHeight) {
                    float centage = scrolledVerticalPix / topBannerHeight;
                    //LogUtil.logE("onScrolled", scrolledVerticalPix + "       " + centage);
                    float alpha = (255 * centage);
                    // 只是layout背景透明
                    theme_top_banner.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                    //theme_top_banner.setAlpha(centage);
                    if(alpha > 127){
                        mBackView.setImageResource(R.drawable.back);
                        imgShare.setImageResource(R.drawable.share);
                        imgMore.setImageResource(R.drawable.tribe_tiny_info_more);
                        mDividerView.setVisibility(View.VISIBLE);
                    } else {
                        mBackView.setImageResource(R.drawable.back_white);
                        imgShare.setImageResource(R.drawable.share_white);
                        imgMore.setImageResource(R.drawable.tribe_more_icon);
                        mDividerView.setVisibility(View.GONE);
                    }
                } else {
                    theme_top_banner.setBackgroundColor(getResources().getColor(R.color.title_color));
                }
                //判断是否显示banner标题
                if (mHeaderView.getViewDividerSignY() > 0 && scrolledVerticalPix >= mHeaderView.getViewDividerSignY()) {
                    tvMiddle.setVisibility(View.VISIBLE);
                } else {
                    tvMiddle.setVisibility(View.INVISIBLE);
                }

            }
        });
        mHeaderView = new TribeHeaderView(this, userId);
        mRecyclerAdapter = new TribeListAdapter(mAdList, mHeaderView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setItemHeight();
        mRecyclerAdapter.setInterface(new TribeListAdapter.AdapterInterface() {
            @Override
            public void onItemClick(Ad ad) {
                UmengClickEventHelper.onTribeTudiClickEvent(TribeMainAct.this);

                Intent intent = new Intent(mActivity, CommonWeb.class);
                intent.putExtra("ad", ad);
                intent.putExtra("title", getString(R.string.me_ad_detail));
                intent.putExtra("type", CommonWeb.typeAd);
                startActivity(intent);
            }

            @Override
            public void onLoadMore() {
                getTribeAd();
            }

            @Override
            public void onAdBusinessClick(Ad ad) {
                AdHelper.toApplyAdCooperateAct(ad, TribeMainAct.this);
            }

            @Override
            public void toAll() {
                Intent intent = new Intent(TribeMainAct.this, TribeTudiAdActivity.class);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });
    }

    private void setTextByUserType() {
        typeEntry = mActivity.getIntent().getIntExtra("typeEntry", typeEntryOther);
        switch (typeEntry) {
            case typeEntryOther:
                // 如果不是自己
                if (!SharedToken.getUserId().equals(String.valueOf(userId))) {
                    //findViewById(R.id.linBottom).setVisibility(View.VISIBLE);
                    //  mHeaderView.setOtherText();
                } else {
                    findViewById(R.id.imgMore).setVisibility(View.GONE);
                    // mHeaderView.setSeftText();
                }
                break;
            case typeEntrySelf:
                // mHeaderView.setSignText();
                break;
        }
    }

    public void onEvent(EventUpdateAlias event) {
        if (event != null && event.getUserid() == userId && userItem != null) {
            mHeaderView.updateNick(event.getAlias());
            userItem.setAlias(event.getAlias());
            tvMiddle.setText((TextUtils.isEmpty(userItem.getAlias()) ? userItem.getNickname() : userItem.getAlias()) + "的主页");
            refreshRongUserDate();
        }
    }

    public void onEvent(EventTypeCommon type) {
        if (type.getType() == EventTypeCommon.typeChangeRelation) {
            getTribeAllData();
        } else if (type.getType() == EventTypeCommon.typeSignIn) {
            if (type.getUserId() == userId) {
                if (tribe != null && tribe.getIssignin() != SignListResult.issignYes) {
                    mHeaderView.updateSignStatus();
                    getSignList();
                }


            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_SETTING) {
                Relation relation = (Relation) data.getSerializableExtra("relation");
                if (relation != null) {
                    this.relation = relation;
                }
            }
        }
    }

    /**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a
     * hidden "zoomed-in" image view and animating its bounds to fit the entire
     * activity content area. More specifically:
     * <p/>
     * <ol>
     * <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image
     * view.</li>
     * <li>Calculate the starting and ending bounds for the expanded view.</li>
     * <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X,
     * SCALE_Y) simultaneously, from the starting bounds to the ending bounds.</li>
     * <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     *
     * @param thumbView    The thumbnail view to zoom in.
     * @param //imageResId
     * @param bmp          The high-resolution version of the image represented by the
     *                     thumbnail.
     */
    public void zoomImageFromThumb(final View thumbView, Bitmap bmp) {
        if (bmp == null) return;
        // If there's an animation in progress, cancel it immediately and
        // proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.

        expandedImageView.setBackgroundColor(0xee333333);
        expandedImageView.setImageBitmap(bmp);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the
        // final bounds are the global visible rectangle of the container view.
        // Also
        // set the container view's offset as the origin for the bounds, since
        // that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the
        // "center crop" technique. This prevents undesirable stretching during
        // the animation.
        // Also calculate the start scaling factor (the end scaling factor is
        // always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
        // top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the
        // original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedImageView.setBackgroundDrawable(null);
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                // Animate the four positioning/sizing properties in parallel,
                // back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
        expandedImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mSaveImgDialog.show();
                return true;
            }
        });
    }

    /**
     * 获取师傅 长老 酋长
     */
    void getMasters() {
        Params params = new Params();
        params.put("objuserid", userId);
        new HttpEntity(this).commonPostData(Method.getElders, params, this);
    }


    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, Config.interClickTime);
        Intent intent;
        switch (view.getId()) {
            case R.id.theme_top_banner_left:
                backAction();
                break;
            // 底部三个
            case R.id.btnSendGift:
                MobclickAgent.onEvent(this, UmengClickEventHelper.BLSLW);
                getGiftList();
                break;
            case R.id.btnFriend:
                if (Relation.isFriend(relation.getRelationflag())) {
                    MobclickAgent.onEvent(this, UmengClickEventHelper.BLFXX);
                    ThemeActivity.isMerchant(userId);
                    String nickName = userItem.getAlias() == null || userItem.getAlias().equals("") ? userItem.getNickname() : userItem.getAlias();
                    Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                            .appendQueryParameter("targetId", String.valueOf(userId)).appendQueryParameter("title", nickName).build();
                    mContext.startActivity(new Intent("android.intent.action.VIEW", uri));
                } else {
                    MobclickAgent.onEvent(this, UmengClickEventHelper.BLJHY);
                    new AddFriendUtil(mContext, TribeMainAct.this).addFriendDirect(userItem);
                }
                break;
            case R.id.imgMore:
                intent = new Intent(this, RelationSettingAct.class);
                intent.putExtra("userItem", userItem);
                intent.putExtra("relation", relation);
                startActivityForResult(intent, REQUEST_SETTING);
                break;
            case R.id.imgShare:
                if (userItem != null) {
                    MobclickAgent.onEvent(this, UmengClickEventHelper.BLFX);
                    ShareItem shareItem = new ShareItem();
                    shareItem.setTitle(userItem.getNickname() + getString(R.string.someone_statuses_homepage));
                    if (userItem.getSignature() == null || TextUtils.isEmpty(userItem.getSignature().trim())) {
                        shareItem.setContent(getString(R.string.hi_welcome_to_my_tribe));
                    } else {
                        shareItem.setContent(userItem.getSignature());
                    }
                    shareItem.setImgUrl(userItem.getImgurl());
                    shareItem.nickname = userItem.getNickname();
                    new ShareDialog(this).shareTribe(userItem.getUserid(), shareItem);
                }
                break;
        }
    }

    /**
     * 设置点击监听，有数据之后调用
     */
    private void setOnClickListener() {
        imgMore.setOnClickListener(this);
        imgShare.setOnClickListener(this);
        // 底部三个按钮
        btnFriend.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        findViewById(R.id.btnSendGift).setOnClickListener(this);
        mHeaderView.setOnClick();
    }


    //这个判断 在TribeHeaderView类也有、、bad
    void checkAccount() {
        if (userItem == null) return;
        int accountType = userItem.getAccounttype();
        switch (accountType) {
            //系统账号
            case UserItem.accountTypeSystem:
                findViewById(R.id.linBottom).setVisibility(View.GONE);
                findViewById(R.id.linCount).setVisibility(View.GONE);
                findViewById(R.id.linHelpSign).setVisibility(View.GONE);
                imgMore.setVisibility(View.GONE);
                break;
            case UserItem.accountTypeTeam:
                //tvAge.setVisibility(View.GONE);
                //tvLocation.setVisibility(View.GONE);
                //ivGender.setVisibility(View.GONE);
                break;
            default:
                if (!SharedToken.getUserId().equals(String.valueOf(userId))) {
                    findViewById(R.id.linBottom).setVisibility(View.VISIBLE);
                    mHeaderView.setBottomVisible();
                    //  mHeaderView.setOtherText();
                }
                break;
        }
    }

    int sortno;

    private void getTribeAllData() {
        mPage = 1;
        mTaskManager.start(TribeTask.getTribeAllData(userId, mPage, COUNT)
                .setCallback(new HttpTaskCallback<HashMap<String, Object>>(mActivity) {
                    @Override
                    public void onSuccess(HashMap<String, Object> result) {
                        tribe = (TribeAllItem) result.get("tribe");
                        TribeAd ad = (TribeAd) result.get("ad");
                        if (ad != null) {
                            if (ad.main != null) {
                                mHeaderView.updateMainAd(ad.main);
                            }
                            sortno = ad.sub.getSortno();
                            if (ad.sub.getAdview() != null && ad.sub.getAdview().size() > 0) {
                                mHeaderView.updateTudiAd();
                                mAdList.clear();
                                mAdList.addAll(ad.sub.getAdview());
                                mRecyclerAdapter.notifyDataSetChanged();
                                if (ad.sub.getAdview().size() >= COUNT) {
                                    mPage++;
                                    mRecyclerAdapter.setHasMore();
                                } else {
                                    mRecyclerAdapter.allLoadFinish();
                                }
                            } else {
                                mRecyclerAdapter.hideLoadMore();
                            }
                        }

                        if (tribe != null) {
                            setOnClickListener();
                            TribeUserView userView = tribe.getUserview();
                            // 个人基本信息
                            userItem = userView.getUser();
                            refreshRongUserDate();
                            checkAccount();

                            if (!SharedToken.getUserId().equals(String.valueOf(userId))) {
                                tvMiddle.setText((TextUtils.isEmpty(userItem.getAlias()) ? userItem.getNickname() : (userItem.getAlias())));
                            } else {
                                tvMiddle.setText(userItem.getNickname());
                            }
                            // 关系
                            relation = userView.getRelation();
                            int relationFlag = relation.getRelationflag();
                            //判断好友关系
                            if (Relation.isFriend(relationFlag)) {
                                btnFriend.setText(R.string.send_message);
                                //btnFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_card_message, 0, 0, 0);
                            } else {
                                //btnFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_card_friends, 0, 0, 0);
                                btnFriend.setText(R.string.plus_friend);
                            }
                            //判断关注关系
                            checkFollowState();
                            mHeaderView.updateTribeData(tribe, userView, userItem, relationFlag);
                            mHeaderView.updateEldersInfo(elders);
                        }


                    }
                }));
    }

    private void getTribeAd() {
        mTaskManager.start(TribeTask.getTribeAd(userId, mPage, COUNT, sortno)
                .setCallback(new HttpTaskCallback<TribeAd>(mActivity) {

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                        mRecyclerAdapter.setHasMore();
                        mSwipeToLoadLayout.setLoadingMore(false);
                    }

                    @Override
                    public void onSuccess(TribeAd result) {
                        mSwipeToLoadLayout.setLoadingMore(false);
                        if (result.sub != null && result.sub.getAdview() != null && result.sub.getAdview().size() > 0) {
                            mPage++;
                            mAdList.addAll(result.sub.getAdview());
                            mRecyclerAdapter.notifyDataSetChanged();
                            sortno = result.sub.getSortno();
                            if (result.sub.getAdview().size() >= COUNT) {
                                if (mAdList.size() >= 16 && result.sub.getCount() > 16) {
                                    mRecyclerAdapter.setToAll();
                                    mSwipeToLoadLayout.setLoadMoreEnabled(false);
                                } else {
                                    mRecyclerAdapter.setLoadMore();
                                    mSwipeToLoadLayout.setLoadMoreEnabled(true);
                                }
                            } else {
                                mRecyclerAdapter.allLoadFinish();
                            }
                        } else {
                            mRecyclerAdapter.allLoadFinish();
                        }
                    }
                }));
    }

    /**
     * 获取礼物列表
     */
    void getGiftList() {
        if (listGift == null) {
            new HttpEntity(this).commonPostData(Method.userGetGiftList, null, this);
        } else {
            showDaShangDialog(listGift);
        }
    }

    /**
     * 获取签到列表
     */
    void getSignList() {
        Params params = new Params();
        params.put("target_userid", userId);
        new HttpEntity(this).commonPostData(Method.tribeSignList, params, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActDestroyed = true;
        LogUtil.logE("onDestroy");
        mAminBitmp = null;
        EventBus.getDefault().unregister(this);
        LocalBroadcastHelper.getInstance().unregister(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHeaderView.onPlayerStop();
    }

    /**
     * 给礼物打赏.需要修改接口
     *
     * @param giftid
     */
    public void giveGift(int giftid, int goldValue) {
        final Params params = new Params();
        params.put("businessid", userId);// 业务id，这里是用户id么
        params.put("giftid", giftid);

        Payment.getPassword(this, goldValue, new OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    showSendGiftTipDialog(params, pwd);
                } else {
                    WaitingDialog.show(mContext, R.string.waiting, false);
                    params.put("paypwd", pwd);
                    new HttpEntity(mContext).commonPostData(Method.imSendGift, params, TribeMainAct.this);
                }
            }
        });
    }

    // 初始化礼物window
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
                giveGift(adapter.getItem(arg2).getGiftid(), adapter.getItem(arg2).getValue());
            }
        });
    }

    private void showSendGiftTipDialog(final Params params, final String pwd) {
        TipsDialog.popup(this, getString(R.string.is_sure_to_give_gift), getString(R.string.me_cancle), getString(R.string.me_confirm), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                WaitingDialog.show(mContext, R.string.waiting, false);
                params.put("paypwd", pwd);
                new HttpEntity(mContext).commonPostData(Method.imSendGift, params, TribeMainAct.this);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    OnClickListener doFollowListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            Params params = new Params();
            params.put("target_userid", userId);
            new HttpEntity(mContext).commonPostData(Method.userFollow, params, TribeMainAct.this);
        }
    };

    /**
     * 显示礼物
     *
     * @param listGift
     */
    void showDaShangDialog(List<Gift> listGift) {
        if (dialogGift.isShowing() || isFinishing()) {
            return;
        }
        if (listGift == null || listGift.size() == 0)
            return;
        GiftAdapter adapter = new GiftAdapter(this, listGift);
        gridGift.setAdapter(adapter);
        dialogGift.show();
    }


    @Override
    public void parse(String methodName, String result) {
        LogUtil.logE("tribe", "parse");
        if (!isActDestroyed)
            WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (!methodName.equals(Method.imAddFriendDirect) && item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.userGetGiftList:
                // 礼物列表
                listGift = JSONHelper.getList(obj, Gift.class);
                showDaShangDialog(listGift);
                break;
            case Method.imSendGift:
                ToastUtil.showToast(R.string.give_gift_success);
                break;
            case Method.imAddRelation:
                ToastUtil.showToast(R.string.sent_friend_apply);
                break;
            case Method.userFollow: {
                ToastUtil.showToast(R.string.follow_success);
                relation.setIsattention(Relation.isattention_yes);
                checkFollowState();
                Intent intent = new Intent(ACTION.FOCUS_UNFOCUS_USER);
                intent.putExtra(KEY.USER_ID, userId);
                intent.putExtra(KEY.IS_FOCUS, Relation.isattention_yes);
                LocalBroadcastHelper.getInstance().send(intent);
            }
            break;
            case Method.userCancelFollow: {
                ToastUtil.showToast(R.string.unfollow_success);
                relation.setIsattention(Relation.isattention_no);
                checkFollowState();
                Intent intent = new Intent(ACTION.FOCUS_UNFOCUS_USER);
                intent.putExtra(KEY.USER_ID, userId);
                intent.putExtra(KEY.IS_FOCUS, Relation.isattention_no);
                LocalBroadcastHelper.getInstance().send(intent);
            }
            break;
            case Method.getElders: {
                elders = JSONHelper.getObject(obj, Masters.class);
                mHeaderView.updateEldersInfo(elders);
            }
            break;
            case Method.imAddFriendDirect:
                btnFriend.setText(R.string.send_message);
                //btnFriend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_card_message, 0, 0, 0);
                relation.setRelationflag(Relation.relation_type_friend);
                checkFollowState();
                break;
            case Method.tribeSignList:
                SignTribe signTribe = JSONHelper.getObject(obj, SignTribe.class);
                if (signTribe != null && signTribe.getProxysigninview() != null) {
                    mHeaderView.updateSignInList(signTribe.getProxysigninview());
                }
                break;

        }
    }

    //刷新保存的用户信息
    private void refreshRongUserDate() {
        if (userItem == null) return;
        String name = userItem.getAlias() == null || userItem.getAlias().equals("") ? userItem.getNickname() : userItem.getAlias();
        //更新融云信息
        RongDB rong = new RongDB(MyApplication.getContext());
        rong.updateOrNone(new IMAccount(RongDB.typeUser, String.valueOf(userItem.getUserid()), name, userItem.getImgurl(), userItem.getCertification(), userItem.getAlias()));
        //并马上强制刷新 历史记录
        Uri uri = Uri.parse(userItem.getImgurl());
        UserInfo user = new UserInfo(String.valueOf(userItem.getUserid()), name, uri);
        RongIM.getInstance().refreshUserInfoCache(user);
    }

    //判断关注状态
    void checkFollowState() {
        if (relation.getIsattention() == Relation.isattention_no) {
            btnFollow.setText(R.string.add_follow);
            //btnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_attention_follow, 0, 0, 0);
            btnFollow.setOnClickListener(doFollowListener);
            int relationFlag = relation.getRelationflag();
            if (!Relation.isFriend(relationFlag)) {
                imgMore.setVisibility(View.GONE);
            }
        } else if (relation.getIsfans() == Relation.isbeattention_yes) {
            btnFollow.setText(R.string.followed_each_other);
            btnFollow.setOnClickListener(doUnfollowListener);
            //btnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_attention_follow_each_other, 0, 0, 0);
            setMoreVisible();
        } else {
            btnFollow.setText(R.string.followed);
            btnFollow.setOnClickListener(doUnfollowListener);
            //btnFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_attention_followed, 0, 0, 0);
            setMoreVisible();
        }
        if (relation.getIsfans() == Relation.isbeattention_yes) {
            setMoreVisible();
        }
    }

    void setMoreVisible() {
        if (!SharedToken.getUserId().equals(String.valueOf(userId))) {
            imgMore.setVisibility(View.VISIBLE);
        }

        checkAccount();
    }

    OnClickListener doUnfollowListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            MobclickAgent.onEvent(TribeMainAct.this, UmengClickEventHelper.BLJGZ);
            showUnFollowWindow();

        }
    };

    void showUnFollowWindow() {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                Params params = new Params();
                params.put("target_userid", userId);
                new HttpEntity(TribeMainAct.this).commonPostData(Method.userCancelFollow, params, TribeMainAct.this);

            }
        };

        if (window != null && window.isShowing()) {
            window.dismiss();
            return;
        }
        View bottomView = findViewById(R.id.linBottom);
        int bottomHeight = bottomView.getHeight();

        TextView windowView = new TextView(this);
        windowView.setGravity(Gravity.CENTER);
        windowView.setText(R.string.unfollow);
        windowView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        windowView.setTextColor(getResources().getColor(R.color.black7));
        windowView.setBackgroundResource(R.drawable.icon_popup_down_bg);
        windowView.setOnClickListener(listener);
        windowView.measure(0, 0);
        int height = windowView.getMeasuredHeight();
        int width = windowView.getMeasuredWidth();
        window = new PopupWindow(windowView, width, height);
        window.setFocusable(true);
        window.setTouchable(true);
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        int widthOff = Device.getWidth(this) / 2 - windowView.getMeasuredWidth() / 2;

        window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, Device.getHeight() - bottomHeight - height);
    }

    @Override
    public void onBackPressed() {
        backAction();
    }

    void backAction() {
        if (expandedImageView.getVisibility() == View.VISIBLE) {
            expandedImageView.performClick();
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("isFollowed", isFollowed);
        setResult(DongtaiSquareAdapter.requestTribe, intent);
        finish();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION.UPDATE_SIGN_IN_LIST:
                    long uid = intent.getLongExtra(EXTRA.USER_ID, 0);
                    if (uid == TribeMainAct.this.userId) {
                        ArrayList<UserItem> userList = (ArrayList<UserItem>) intent.getSerializableExtra(EXTRA.USER_ITEM_LIST);
                        mHeaderView.updateSignInList(userList);
                    }
                    break;
            }
        }
    };


}
