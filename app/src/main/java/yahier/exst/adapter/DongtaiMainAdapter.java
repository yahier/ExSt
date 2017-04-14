package yahier.exst.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.AdDongtaiDetailAct;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.act.dongtai.VideoPlayAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.dialog.LoadingTipsDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.LinkInStatuses;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.ui.DirectScreen.dialog.InRoomPasswordDialog;
import com.stbl.stbl.ui.DirectScreen.util.LiveRoomHelper;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.DongtaiRemarkDB;
import com.stbl.stbl.util.EmojiParseThread;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.widget.EmojiTextView;
import com.stbl.stbl.widget.ImageGridView;
import com.stbl.stbl.widget.VideoPlayView;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态主列表，动态广场，动态搜索共用的adapter
 */
public class DongtaiMainAdapter extends CommonAdapter implements OnClickListener, QavsdkManger.OnQavsdkUpdateListener, FinalHttpCallback, VideoPlayView.OnVideoListener {

    private String flag = DongtaiMainAdapter.this.getClass().getSimpleName();
    Activity mContext;
    MyApplication app;
    List<Statuses> list;
    List<Boolean> isPraised;
    List<Boolean> isPlayVideo;
    List<GVImgAdapter> listGVIAdapter;
    public final static String key = "item";
    public final int requestDetailDongtai = 100;

    private STProgressDialog pressageDialog;
    private LinearLayout.LayoutParams paramsLong, paramsShort;
    private int type;
    public final static int typeStatuses = 0;
    public final static int typeStatusesSquare = 1;
    public final static int typeStatusesSearch = 2;
    //VideoPlayAct.VideoPlayView videoItemView;
    private boolean isFirstAddVideo = true; //是否第一次添加视频
    boolean isShowImg = true;
    LoadingTipsDialog loadingTipsDialog; //评论状态弹框

    public DongtaiMainAdapter(Activity mContext) {
        this.mContext = mContext;
        loadingTipsDialog = new LoadingTipsDialog(mContext);

        this.type = typeStatuses;
        app = (MyApplication) mContext.getApplication();
        list = new ArrayList<Statuses>();
        //isShowInput = new ArrayList<Boolean>();
        isPraised = new ArrayList<Boolean>();
        paramsLong = new LinearLayout.LayoutParams(GVImgAdapter.gridWidth(mContext), LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsShort = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        QavsdkManger.getInstance().registerCreateRoom(mContext, flag);
        listGVIAdapter = new ArrayList<>();
    }


    public void checkIsPauseVideo(int firstVisibleItem, int lastVisibleItem) {
        int currentPlayIndex = -1;
        for (int i = 0; i < isPlayVideo.size(); i++) {
            if (isPlayVideo.get(i)) {
                currentPlayIndex = i;
            }
        }

        if (currentPlayIndex == -1) {
            return;
        }
        //LogUtil.logE("scroll",currentPlayIndex);

        //如果当前显示 3-6项，如果当前播放的序号小于3或者大于6，就要暂停
        if (currentPlayIndex < firstVisibleItem || currentPlayIndex > lastVisibleItem) {
            pauseInVisibleVideo();
        }
    }

    /**
     * 动态主页grid一栏的数目
     */
    public DongtaiMainAdapter(Activity mContext, int type) {
        this.mContext = mContext;
        loadingTipsDialog = new LoadingTipsDialog(mContext);
        this.type = type;
        app = (MyApplication) mContext.getApplication();
        list = new ArrayList<Statuses>();
        //isShowInput = new ArrayList<Boolean>();
        isPraised = new ArrayList<Boolean>();
        isPlayVideo = new ArrayList<Boolean>();
        paramsLong = new LinearLayout.LayoutParams(GVImgAdapter.gridWidth(mContext), LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsShort = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        QavsdkManger.getInstance().registerCreateRoom(mContext, flag);
        // videoItemView = new VideoPlayView(mContext);
        if (VideoPlayAct.videoItemView == null) {
            VideoPlayAct.videoItemView = new VideoPlayView(mContext);
        }
        listGVIAdapter = new ArrayList<>();
    }

    public void setData(List<Statuses> list) {
        stopVideo();
        isPraised.clear();
        isPlayVideo.clear();
        listGVIAdapter.clear();
        for (int i = 0; i < list.size(); i++) {
            //isShowInput.add(false);
            isPraised.add(false);
            isPlayVideo.add(false);
            listGVIAdapter.add(null);
        }
        this.list = list;
        notifyDataSetChanged();
    }


    public void deleteStatusesId(long statusesId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatusesid() == statusesId) {
                //LogUtil.logE("被删除的索引:  " + i);
                list.remove(i);
                isPraised.remove(i);
                listGVIAdapter.remove(i);
                //删除正在播放的视频，也要加停止播放处理
                if (isPlayVideo.get(i)) {
                    pauseInVisibleVideo();
                }
                isPlayVideo.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 发布微博之后调用.加载置顶的后面
     *
     * @param statuses
     */
    public void addItemAfterPulish(Statuses statuses) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIstop() == Statuses.istopYes) {
                continue;
            }
            list.add(i, statuses);
            isPraised.add(i, false);
            isPlayVideo.add(i, false);
            listGVIAdapter.add(i, null);
            notifyDataSetChanged();
            return;
        }

    }

    public void addData(List<Statuses> list) {
        for (int i = 0; i < list.size(); i++) {
            isPraised.add(false);
            isPlayVideo.add(false);
            listGVIAdapter.add(null);
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 :list.size();
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (Method.weiboRemark.equals(methodName)){
                loadingTipsDialog.showFaild(mContext.getString(R.string.statuses_reply_faild));
                loadingTipsDialog.postDissmiss();
                return;
            }
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.weiboDel:
                deleteStatusesId(cStatuses.getStatusesid());
                break;
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                cStatuses.setIsattention(Statuses.isattention_yes);
                list.set(cPosition, cStatuses);
                for (Statuses statuses : list) {
                    if (statuses.getUser().getUserid() == cStatuses.getUser().getUserid()) {
                        statuses.setIsattention(Statuses.isattention_yes);
                        notifyDataSetChanged();
                    }
                }
                break;
            case Method.userCancelFollow:
                ToastUtil.showToast(R.string.unfollow_success);
                cStatuses.setIsattention(Statuses.isattention_no);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                cStatuses.setIsshield(Statuses.isshield_yes);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;
            case Method.userCancelgnore:
                ToastUtil.showToast(R.string.unshield_success);
                cStatuses.setIsshield(Statuses.isshield_no);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;
            case Method.weiboRemark:
//                ToastUtil.showToast(R.string.remark_success);
                loadingTipsDialog.showSuccess(mContext.getString(R.string.statuses_reply_success));
                loadingTipsDialog.postDissmiss();
                mInputDialog.dismiss();
                int commentCount = cStatuses.getCommentcount() + 1;
                cStatuses.setCommentcount(commentCount);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                //发起获取最新评论的接口
                getRemarkList();
                break;
            case Method.weiboRemarkList:
                List<StatusesComment> listRemark = JSONHelper.getList(obj, StatusesComment.class);
                new DongtaiRemarkDB(mContext).insertToDB(listRemark);
                notifyDataSetChanged();
                break;

        }
    }


    final int requestRemarkCount = 4;

    void getRemarkList() {
        Params params = new Params();
        params.put("statusesid", cStatuses.getStatusesid());
        params.put("count", requestRemarkCount);
        //params.put("lastid", Statuses.type_public);
        new HttpEntity(mContext).commonPostData(Method.weiboRemarkList, params, this);
    }

    public final static int requestFullVideo = 1005;

    @Override
    public void onFullCilck() {
        //Intent intent = new Intent(mContext, VideoPlayAct.class);
        //mContext.startActivity(intent);
        //startActivityForResult(intent, requestVideoFull);
        onStatesesListener.doFullClick(cPosition);
    }


    class CityHolder {
        //	LinearLayout mainZone;
        GridView itemGV;

        TextView userName;
        ImageView userImg;
        TextView time;
        ImageView relation;
        TextView tvitem1, tvitem2, tvitem3, tvitem4;

        // 链接
        LinearLayout link1User, link2Wish, link3Statuses, link4Goods, link5Live;
        //动态链接
        ImageView link3Img;
        TextView link3Tv, lin3UserName;


        // 名片链接
        ImageView link1Img;
        TextView lin1Name;
        TextView link1AgeGender;
        TextView link1City;
        TextView link1Signature;
        // 物品链接
        ImageView link4imgLink;
        TextView link4tvGoodsTitle;
        TextView link4tvGoodsPrice;
        TextView link4tvGoodsSale;
        // 链接5 直播
        ImageView link5img;
        TextView link5tvTitle;
        TextView link5tvContent;

        EmojiTextView tvShortTitle, tvLongTitle;
        View linLongSpe;
        View linLink;
        View imgAuthorized;
        ImageView imgMore;
        View tvSquareAtten;
        ImageView imgItem1, imgItem2, imgItem3, imgItem4;
        View linItem1, linItem2, linItem3, linItem4;
        ListView remarkList;
        View linTop, linHot;

        //视频
        View statusesVideo;
        TextView linVideoTitle, linkVideoTitle;
        ImageView linVideoCover, linkVideoCover;
        ImageView linVideoStart;
        View linViewRreview, linkViewRreview;

        //视频链接
        View link7Video;
        TextView tvLinkVideoNick, tvLinkVideoContent;

        View link8Link;
        ImageView link8Img;
        TextView link8Content;
        View linRemark, tvMoreRemark;

        View link9Hongbao, linHongbaoBg;
        TextView tvLink9HongbaoContent, tvLink9HongbaoBrand;
        ImageView imgLink9HongbaoImg;

    }

    // 直接notifyDataSetChanged
    public void updateCollectText(ListView listView, int position, int collectcount) {
        Statuses statuses = list.get(position);
        statuses.setIsfavorited(Statuses.isfavoritedYes);
        statuses.setFavorcount(collectcount);
        list.set(position, statuses);
        notifyDataSetChanged();
    }

    public void updateCancelCollectText(ListView listView, int position, int collectcount) {
        Statuses statuses = list.get(position);
        statuses.setIsfavorited(Statuses.isfavoritedNo);
        statuses.setFavorcount(collectcount);
        list.set(position, statuses);
        notifyDataSetChanged();
    }


    // 更新整个item
    public void updateItem(Statuses statuses, int position) {
        if (list.size() > position) {
            list.set(position, statuses);
            notifyDataSetChanged();
        }
    }


    //更新对应position的mStatuses
    public void update(int position, Statuses mStatuses) {
        //list.set(position, mStatuses);
    }


    // 刷新点赞
    public void updatePraiseText(ListView listView, int position, int praiseCount, int type) {
        Statuses statuses = list.get(position);
        if (type == PraiseResult.type_add) {
            statuses.setIspraised(Statuses.ispraisedYes);
        } else if (type == PraiseResult.type_cancel) {
            statuses.setIspraised(Statuses.ispraisedNo);
        }

        statuses.setPraisecount(praiseCount);
        list.set(position, statuses);
        notifyDataSetChanged();
    }


    Dialog dialog;

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.main_dongtai_list_item, null);
            ho.userName = (TextView) con.findViewById(R.id.name);

            ho.relation = (ImageView) con.findViewById(R.id.relation);
            ho.time = (TextView) con.findViewById(R.id.time);
            ho.userImg = (ImageView) con.findViewById(R.id.item_iv);
            ho.itemGV = (GridView) con.findViewById(R.id.item_gv);
            ho.tvitem1 = (TextView) con.findViewById(R.id.item_text1);
            ho.tvitem2 = (TextView) con.findViewById(R.id.item_text2);
            ho.tvitem3 = (TextView) con.findViewById(R.id.item_text3);
            ho.tvitem4 = (TextView) con.findViewById(R.id.item_text4);

            ho.link3Statuses = (LinearLayout) con.findViewById(R.id.link3Statuses);
            ho.link3Img = (ImageView) con.findViewById(R.id.link3_img);
            ho.link3Tv = (TextView) con.findViewById(R.id.link3_content);
            ho.lin3UserName = (TextView) con.findViewById(R.id.link3_userName);
            ho.link1User = (LinearLayout) con.findViewById(R.id.link1User);
            ho.link2Wish = (LinearLayout) con.findViewById(R.id.link2Wish);
            ho.link4Goods = (LinearLayout) con.findViewById(R.id.link4Goods);
            ho.link5Live = (LinearLayout) con.findViewById(R.id.link5Live);
            // 名片链接
            ho.link1Img = (ImageView) con.findViewById(R.id.link1imgUser);
            ho.lin1Name = (TextView) con.findViewById(R.id.link1name);
            ho.link1AgeGender = (TextView) con.findViewById(R.id.link1user_gender_age);
            ho.link1City = (TextView) con.findViewById(R.id.link1user_city);
            ho.link1Signature = (TextView) con.findViewById(R.id.link1user_signature);
            // 产品链接
            ho.link4imgLink = (ImageView) con.findViewById(R.id.link4imgLink);
            ho.link4tvGoodsTitle = (TextView) con.findViewById(R.id.link4tvGoodsTitle);
            ho.link4tvGoodsPrice = (TextView) con.findViewById(R.id.link4tvGoodsPrice);
            ho.link4tvGoodsSale = (TextView) con.findViewById(R.id.link4tvGoodsSale);

            ho.link5img = (ImageView) con.findViewById(R.id.link5_img);
            ho.link5tvTitle = (TextView) con.findViewById(R.id.tv_user_live);
            ho.link5tvContent = (TextView) con.findViewById(R.id.link5_content);

            ho.tvShortTitle = (EmojiTextView) con.findViewById(R.id.tvShortTitle);
            ho.tvLongTitle = (EmojiTextView) con.findViewById(R.id.tvLongTitle);
            ho.linLongSpe = con.findViewById(R.id.linLongSpe);
            ho.linLink = con.findViewById(R.id.linLink);
            ho.imgAuthorized = con.findViewById(R.id.imgAuthorized);
            ho.imgMore = (ImageView) con.findViewById(R.id.imgMore);
            ho.tvSquareAtten = con.findViewById(R.id.tvSquareAtten);

            ho.linItem1 = con.findViewById(R.id.linItem1);
            ho.linItem2 = con.findViewById(R.id.linItem2);
            ho.linItem3 = con.findViewById(R.id.linItem3);
            ho.linItem4 = con.findViewById(R.id.linItem4);

            ho.imgItem1 = (ImageView) con.findViewById(R.id.imgItem1);
            ho.imgItem2 = (ImageView) con.findViewById(R.id.imgItem2);
            ho.imgItem3 = (ImageView) con.findViewById(R.id.imgItem3);
            ho.imgItem4 = (ImageView) con.findViewById(R.id.imgItem4);

            ho.remarkList = (ListView) con.findViewById(R.id.remarkList);
            ho.linTop = con.findViewById(R.id.linTop);
            ho.linHot = con.findViewById(R.id.linHot);
            ho.statusesVideo = con.findViewById(R.id.statusesVideo);

            ho.link7Video = con.findViewById(R.id.link7Video);

            ho.linVideoTitle = (TextView) con.findViewById(R.id.linVideoTitle);
            ho.linVideoCover = (ImageView) con.findViewById(R.id.linVideoCover);
            ho.linVideoStart = (ImageView) con.findViewById(R.id.linVideoStart);
            ho.linViewRreview = con.findViewById(R.id.linViewRreview);
            ho.linkViewRreview = con.findViewById(R.id.linkViewRreview);


            ho.linkVideoTitle = (TextView) con.findViewById(R.id.linkVideoTitle);
            ho.tvLinkVideoNick = (TextView) con.findViewById(R.id.tvLinkVideoNick);
            ho.tvLinkVideoContent = (TextView) con.findViewById(R.id.tvLinkVideoContent);
            ho.linkVideoCover = (ImageView) con.findViewById(R.id.linkVideoCover);

            //经常链接
            ho.link8Link = con.findViewById(R.id.link8Link);
            ho.link8Img = (ImageView) con.findViewById(R.id.link8Img);
            ho.link8Content = (TextView) con.findViewById(R.id.link8Content);
            ho.tvMoreRemark = con.findViewById(R.id.tvMoreRemark);
            ho.linRemark = con.findViewById(R.id.linRemark);

            ho.link9Hongbao = con.findViewById(R.id.link9Hongbao);
            ho.linHongbaoBg = con.findViewById(R.id.linHongbaoBg);

            ho.tvLink9HongbaoContent = (TextView) con.findViewById(R.id.tvHongbaoContent);
            ho.tvLink9HongbaoBrand = (TextView) con.findViewById(R.id.tvHongbaoBrand);
            ho.imgLink9HongbaoImg = (ImageView) con.findViewById(R.id.link9Img);

            con.setTag(ho);
        } else
            ho = (CityHolder) con.getTag();
        ImageGridView igv = (ImageGridView) ho.itemGV;
        igv.setOnTouchInvalidPositionListener(new ImageGridView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                return false; //不终止路由事件让父级控件处理事件
            }
        });
        final Statuses statuses = list.get(i);
        final StatusesPic pics = statuses.getStatusespic();
        int picsSize = pics.getPics().size();
        int numClumn;
        // 如果是长微博。就需要放大
        if (pics != null && statuses.getStatusestype() == Statuses.type_long) {
            numClumn = 1;
        } else {
            if (picsSize > 3) {
                numClumn = GVImgAdapter.gridColumn;
            } else {
                numClumn = picsSize == 0 ? GVImgAdapter.gridColumn :picsSize;
            }

        }
        if (statuses.getIstop() == Statuses.istopYes) {
            ho.linTop.setVisibility(View.VISIBLE);
        } else {
            ho.linTop.setVisibility(View.GONE);
        }

        GVImgAdapter gridAdapter = null;
        if (listGVIAdapter.get(i) == null) {
            gridAdapter = new GVImgAdapter(mContext, pics, numClumn);
            listGVIAdapter.set(i, gridAdapter);
        } else {
            gridAdapter = listGVIAdapter.get(i);
        }

        //GVImgAdapter gridAdapter = new GVImgAdapter(mContext, pics, numClumn);
        gridAdapter.setStatusesId(statuses.getStatusesid());
        // 识别长动态与短动态
        switch(statuses.getStatusestype()) {
            case Statuses.type_short:
                String content = statuses.getContent();
                //content = content.replace("\n", "");
                if (content == null || content.equals("")) {
                    ho.tvShortTitle.setVisibility(View.GONE);
                } else {
                    ho.tvShortTitle.setVisibility(View.VISIBLE);
                    content = Util.halfToFull(content);// 转换为全角
                    //ho.tvShortTitle.setText(content);
                    EmojiParseThread.getInstance().parse(content, ho.tvShortTitle);
                }
                ho.tvLongTitle.setVisibility(View.GONE);
                //ho.itemGV.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                if (pics == null || pics.getPics().size() == 0) {
                    ho.itemGV.setVisibility(View.GONE);
                } else if (pics.getPics().size() == 1) {
                    gridAdapter.setType(GVImgAdapter.typeShort1);
                } else {
                    gridAdapter.setType(GVImgAdapter.typeShort);
                }
                ho.linLongSpe.setBackgroundResource(R.color.transparent);
                ho.linLongSpe.setLayoutParams(paramsShort);
                break;
            case Statuses.type_long:
                gridAdapter.setType(GVImgAdapter.typeLong);
                ho.tvShortTitle.setVisibility(View.GONE);
                if (statuses.getContent() == null)
                    break;
                // String pureContent = statuses.getContent().replace(Config.longWeiboFillMark, "");
                String title = statuses.getTitle();
                if (title == null || title.equals("")) {
                    ho.tvLongTitle.setVisibility(View.GONE);
                } else {
                    ho.tvLongTitle.setVisibility(View.VISIBLE);
                    //ho.tvLongTitle.setText(title);
                    EmojiParseThread.getInstance().parse(title, ho.tvLongTitle);
                }

                // ho.itemGV.setPadding(0, 0, 0, 0);
                ho.linLongSpe.setBackgroundResource(R.color.theme_bg);//去边距 可以去掉此设置
                ho.linLongSpe.setLayoutParams(paramsLong);
                break;
        }
        UserItem user = statuses.getUser();
        int accountType = user.getAccounttype();
        //LogUtil.logE(user.getNickname() + " accountType:" + accountType);
        //系统账号，特别处理。以后要复制到详情
        if (accountType == UserItem.accountTypeSystem) {
            if (String.valueOf(statuses.getPublisheruserid()).equals(SharedToken.getUserId())) {
                ho.imgMore.setVisibility(View.VISIBLE);
            } else {
                ho.imgMore.setVisibility(View.GONE);
            }
            ho.tvSquareAtten.setVisibility(View.GONE);
        } else {
            switch(type) {
                case typeStatuses:
                    ho.imgMore.setVisibility(View.VISIBLE);
                    ho.tvSquareAtten.setVisibility(View.GONE);
                    break;
                case typeStatusesSquare:
                    ho.imgMore.setVisibility(View.GONE);
                    if (statuses.getIshot() == Statuses.ishot_yes) {
                        ho.linHot.setVisibility(View.VISIBLE);
                    } else {
                        ho.linHot.setVisibility(View.GONE);
                    }
                    if (statuses.getIsattention() == Relation.isattention_yes) {
                        ho.tvSquareAtten.setVisibility(View.GONE);
                    } else {
                        ho.tvSquareAtten.setVisibility(View.VISIBLE);
                    }
                    break;
                case typeStatusesSearch:
                    ho.imgMore.setVisibility(View.GONE);
                    if (statuses.getIsattention() == Relation.isattention_yes) {
                        ho.tvSquareAtten.setVisibility(View.GONE);
                    } else {
                        ho.tvSquareAtten.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }


        //显示评论
        DongtaiRemarkDB db = new DongtaiRemarkDB(mContext);
        List<StatusesComment> listComment = db.query(statuses.getStatusesid());
        if (listComment.size() == 0) {
            ho.linRemark.setVisibility(View.GONE);

        } else {
            ho.linRemark.setVisibility(View.VISIBLE);
            if (listComment.size() > 3) {
                ho.tvMoreRemark.setVisibility(View.VISIBLE);
            } else {
                ho.tvMoreRemark.setVisibility(View.GONE);
            }
            //ho.remarkList.setVisibility(View.VISIBLE);
            RemarkAdapter adapter = new RemarkAdapter(mContext, listComment);
            ho.remarkList.setAdapter(adapter);
            final CityHolder ho2 = ho;
            ho.remarkList.post(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.setListViewHeightBasedOnChildren(ho2.remarkList);//setAdapterViewHeight不可靠  getListHeight不可靠 setListViewHeightBasedOnChildren

                }
            });
        }

        //切换封面图和视频
        if (isPlayVideo.get(i)) {
            ho.linViewRreview.setVisibility(View.GONE);
            ho.linkViewRreview.setVisibility(View.GONE);
        } else {
            ho.linViewRreview.setVisibility(View.VISIBLE);
            ho.linkViewRreview.setVisibility(View.VISIBLE);
        }
        //这段代码是为了解决在某些机型上surfaceview来不及初始化，导致第一次不能播放的问题
        if (isFirstAddVideo) {
            isFirstAddVideo = false;
            VideoPlayAct.videoItemView.setOnListener(this);
            ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();//找到videoitemview的父类，然后remove
            if (last != null) {
                last.removeAllViews();
            }

            FrameLayout frameLayout = (FrameLayout) ho.link7Video.findViewById(R.id.link_layout_video);
            frameLayout.removeAllViews();
            frameLayout.addView(VideoPlayAct.videoItemView);
        }

        //ho.linLink.setLayoutParams(paramsLong);
        // 图片
        ho.itemGV.setNumColumns(numClumn);
        ho.itemGV.setAdapter(gridAdapter);
        ViewUtils.setStatusesAdapterViewHeight(ho.itemGV, numClumn);//GVImgAdapter.gridColumn   numClumn
        // 判断是否显示
        if (pics == null || pics.getPics().size() == 0) {
            ho.itemGV.setVisibility(View.GONE);
        } else {
            ho.itemGV.setVisibility(View.VISIBLE);
        }
        // 最上一层
        ho.time.setText(DateUtil.getTimeOff(statuses.getCreatetime()));

        ho.tvitem1.setText(statuses.getForwardcount() + "");
        ho.tvitem2.setText(statuses.getCommentcount() + "");
        ho.tvitem3.setText(statuses.getPraisecount() + "");
        ho.tvitem4.setText(statuses.getFavorcount() + "");

        if (statuses.getIspraised() == Statuses.ispraisedYes) {
            isPraised.set(i, true);
            ho.imgItem3.setImageResource(R.drawable.dongtai_praise_presed);
            ho.tvitem3.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
        } else {
            ho.imgItem3.setImageResource(R.drawable.dongtai_item3);
            ho.tvitem3.setTextColor(mContext.getResources().getColor(R.color.black7));
        }

        if (statuses.getIsfavorited() == Statuses.isfavoritedYes) {
            ho.imgItem4.setImageResource(R.drawable.dongtai_collect_pressed);
            ho.tvitem4.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
        } else {
            ho.imgItem4.setImageResource(R.drawable.dongtai_item4);
            ho.tvitem4.setTextColor(mContext.getResources().getColor(R.color.black7));
        }
        ho.linItem1.setOnClickListener(this);
        ho.linItem2.setOnClickListener(this);
        ho.linItem3.setOnClickListener(this);
        ho.linItem4.setOnClickListener(this);
        ho.imgMore.setOnClickListener(this);
        ho.tvMoreRemark.setOnClickListener(this);
        ho.tvSquareAtten.setOnClickListener(this);
        setTag(statuses, ho.userImg, ho.tvitem1, ho.tvitem2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4, ho.imgMore, ho.tvSquareAtten, ho.tvMoreRemark);
        setPositionTag(i, ho.tvitem1, ho.tvitem2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4, ho.imgMore, ho.tvSquareAtten, ho.tvMoreRemark);
        // 图片列表图像
        // 得到用户列表


        ho.userName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias());
//        ho.userName.setText(user.getNickname());
        if (user.getCertification() == UserItem.certificationYes) {
            ho.imgAuthorized.setVisibility(View.VISIBLE);
        } else {
            ho.imgAuthorized.setVisibility(View.GONE);
        }

        if (Relation.isMaster(user.getRelationflag())) {
            ho.relation.setVisibility(View.VISIBLE);
            ho.relation.setImageResource(R.drawable.icon_master);//R.drawable.relation_master_bg);
        } else if (Relation.isStu(user.getRelationflag())) {
            ho.relation.setVisibility(View.VISIBLE);
            ho.relation.setImageResource(R.drawable.icon_student);
        } else {
            ho.relation.setVisibility(View.GONE);
        }

        ImageUtils.loadHead(user.getImgurl(), ho.userImg);
        ho.userImg.setOnClickListener(this);

        // 进入详细
        con.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onStatesesListener.enterDongtaiDetail(i, statuses, false);
                pauseInVisibleVideo();

            }
        });

        View[] linkViews = {ho.link1User, ho.link2Wish, ho.link3Statuses, ho.link4Goods, ho.link5Live, ho.statusesVideo, ho.link7Video, ho.link8Link, ho.link9Hongbao};
        // 这里来处理链接的内容啦
        final LinkStatuses link = statuses.getLinks();
        if (link != null) {
            ho.linLink.setVisibility(View.VISIBLE);
            int linkType = link.getLinktype();
            switch(linkType) {
                case LinkStatuses.linkTypeHongbao:
                    setLinkVisibility(linkViews, 8);
                    //LogUtil.logE("width22222222",paramsLong.width);
                    int edge = (int) mContext.getResources().getDimension(R.dimen.edge_gap) * 2;
                    LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(paramsLong.width - edge, paramsLong.width - edge);//1440  完好显示大概1330
                    ho.link9Hongbao.setLayoutParams(pa);
                    ho.tvLink9HongbaoContent.setText(link.getLinkex2());
                    ho.tvLink9HongbaoBrand.setText(link.getLinktitle());
                    PicassoUtil.load(mContext, link.getLinkex(), ho.imgLink9HongbaoImg);
                    ho.link9Hongbao.setTag(link);
                    ho.link9Hongbao.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeCrad:
                    setLinkVisibility(linkViews, 0);
                    UserItem linkUser = link.getUserinfo();
                    if (linkUser == null)
                        break;
                    ho.lin1Name.setText(linkUser.getAlias() == null || linkUser.getAlias().equals("") ? linkUser.getNickname() :linkUser.getAlias());
//                    ho.lin1Name.setText(linkUser.getNickname());
                    if (isShowImg)
                        PicassoUtil.load(mContext, linkUser.getImgurl(), ho.link1Img);
                    ho.link1City.setText(linkUser.getCityname());
                    ho.link1Signature.setText(linkUser.getSignature());
                    ho.link1User.setTag(linkUser);
                    ho.link1User.setOnClickListener(linkListener);

                    ho.link1AgeGender.setText(linkUser.getAge() + "");
                    if (linkUser.getGender() == UserItem.gender_boy) {
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_boy_bg);
                    } else if (linkUser.getGender() == UserItem.gender_girl) {
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_girl_bg);
                    } else {
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
                    }

                    break;
                case LinkStatuses.linkTypeWish:
                    setLinkVisibility(linkViews, 1);
                    break;
                case LinkStatuses.linkTypeStateses:
                    Statuses linkStatuses = link.getStatusesinfo();
                    //还是视频
                    if (link.getLinkex2() != null && link.getLinkex2().equals(LinkStatuses.linkex2VideoTag)) {
                        setLinkVisibility(linkViews, 6);

                        if(TextUtils.isEmpty(link.getLinktitle())){
                            ho.linVideoTitle.setVisibility(View.GONE);
                        }else{
                            ho.linkVideoTitle.setText(link.getLinktitle());
                        }
                        String name = linkStatuses.getUser().getAlias() == null || linkStatuses.getUser().equals("") ? linkStatuses.getUser().getNickname() :linkStatuses.getUser().getAlias();
                        ho.tvLinkVideoNick.setText("@" + name);
//                        ho.tvLinkVideoNick.setText("@" + linkStatuses.getUser().getNickname());
                        ho.tvLinkVideoContent.setText(linkStatuses.getContent());
                        if (isShowImg)
                            PicassoUtil.load(mContext, link.getLinkex(), ho.linkVideoCover, R.drawable.dongtai_default);
                        final CityHolder ho2 = ho;
                        //最大的视频item
                        ho.link7Video.setOnClickListener(linkListener);
                        ho.link7Video.setTag(linkStatuses);
                        ho.link7Video.findViewById(R.id.link_layout_video).setOnClickListener(null);
                        ho.linkViewRreview.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //ho2.linkViewRreview.setVisibility(View.GONE);
                                startVideo(link.getLinkurl(), ho2.link7Video, R.id.link_layout_video, i);
                                //cPosition = i;
                            }
                        });
                        break;
                    }
                    setLinkVisibility(linkViews, 2);
                    if (linkStatuses == null) {
                        LogUtil.logE("linkStatuses is null");
                        break;
                    }
                    ho.lin3UserName.setText(linkStatuses.getUser().getAlias() == null || linkStatuses.getUser().getAlias().equals("")
                            ? linkStatuses.getUser().getNickname() :linkStatuses.getUser().getAlias());
//                    ho.lin3UserName.setText(linkStatuses.getUser().getNickname());
                    // 重新设置新的链接动态
                    // setTag(linkStatuses, ho.item1);

                    String content = linkStatuses.getContent().replace(Config.longWeiboFillMark, "");
                    ho.link3Tv.setText(content);
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
                        //LogUtil.logE("link3Pics imgUrl is " + imgUrl);
                        if (isShowImg)
                            PicassoUtil.loadStatuses(mContext, imgUrl, ho.link3Img);
                    } else {
                        //LogUtil.logE("link3Pics is null");
                        if (isShowImg)
                            PicassoUtil.load(mContext, R.drawable.dongtai_default, ho.link3Img);
                    }

//                    if (link3Pics.getPics().size() > 0) {
//                        String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
//                        PicassoUtil.loadStatuses(mContext, imgUrl, ho.link3Img);
//                    } else {
//                        ho.link3Img.setImageResource(R.drawable.dongtai_default);
//                    }
                    ho.link3Statuses.setTag(linkStatuses);
                    ho.link3Statuses.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeProduct:
                    setLinkVisibility(linkViews, 3);
                    Goods goods = link.getProductinfo();
                    if (goods == null)
                        break;

                    PicassoUtil.load(mContext, goods.getImgurl(), ho.link4imgLink);
                    ho.link4tvGoodsTitle.setText(goods.getGoodsname());
                    ho.link4tvGoodsPrice.setText("￥" + goods.getMinprice());
                    ho.link4tvGoodsSale.setText(mContext.getString(R.string.mall_sales) + goods.getSalecount());
                    ho.link4Goods.setTag(goods);
                    ho.link4Goods.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeLive:
                    setLinkVisibility(linkViews, 4);
                    ho.link5tvTitle.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias() + mContext.getString(R.string.start_live));
//                    ho.link5tvTitle.setText(user.getNickname() + mContext.getString(R.string.start_live));
                    ho.link5tvContent.setText(mContext.getString(R.string.topic) + ":" + (link.getLinktitle() == null ? "--" :link.getLinktitle()));
//				String linkId = link.getLinkid();
                    link.setUsername(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() :user.getAlias());
//                    link.setUsername(user.getNickname());
                    link.setUserhead(user.getImgurl());
                    ho.link5Live.setTag(link);
                    ho.link5Live.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeVideo:
                    setLinkVisibility(linkViews, 5);
                    if(TextUtils.isEmpty(link.getLinktitle())){
                        ho.linVideoTitle.setVisibility(View.GONE);
                    }else{
                        ho.linVideoTitle.setText(link.getLinktitle());
                    }
                    PicassoUtil.load(mContext, link.getLinkex(), ho.linVideoCover, R.drawable.dongtai_default);
                    //ho.linkVideo.setTag(link);
                    // ho.linkVideo.setOnClickListener(linkListener);
                    final CityHolder ho2 = ho;
                    //最大的视频item
                    ho.statusesVideo.setOnClickListener(null);
                    ho.linViewRreview.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ho2.linViewRreview.setVisibility(View.GONE);
                            //LinkStatuses link = (LinkStatuses)view.getTag();
                            //LogUtil.logE("url:"+link.getLinkurl());
                            startVideo(link.getLinkurl(), ho2.statusesVideo, R.id.layout_video, i);
                        }
                    });
                    break;
                case LinkStatuses.linkTypeNiceLink:
                    setLinkVisibility(linkViews, 7);
                    LinkInStatuses userlinksinfo = link.getUserlinksinfo();
                    if (userlinksinfo == null) {
                        break;
                    }
                    ho.link8Content.setText(userlinksinfo.getTitle());
                    ImageUtils.loadSquareImage(userlinksinfo.getImgurl(), ho.link8Img);
                    ho.link8Link.setTag(userlinksinfo);
                    ho.link8Link.setOnClickListener(linkListener);
                    break;
            }

        } else {
            ho.linLink.setVisibility(View.GONE);
        }
        //用于进入房间防止重复点击的，防止其他情况导致状态不对，adapter刷新就重置状态
        isCanInRoom = true;
        return con;
    }


    void setLinkVisibility(View[] linkViews, int indexVisible) {
        for (int i = 0; i < linkViews.length; i++) {
            if (i == indexVisible) {
                linkViews[i].setVisibility(View.VISIBLE);
            } else {
                linkViews[i].setVisibility(View.GONE);
            }
        }
    }

    void setTag(Object tag, View... views) {
        for (View view : views) {
            view.setTag(tag);
        }
    }

    void setPositionTag(Object position, View... views) {
        for (View view : views) {
            view.setTag(R.id.tag_dongtai_item, position);
        }
    }

    InputDialog mInputDialog;

    void showInputDialog() {
        mInputDialog = new InputDialog(mContext);
        mInputDialog.show();
        mInputDialog.setOnSendListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mInputDialog.getContent();
                if (content.equals("")) {
                    ToastUtil.showToast(R.string.please_input_first);
                    return;
                }

                if (content.length() > Config.remarkContentLength) {
                    String toastTip = String.format(mContext.getString(R.string.statuses_content_exceed_length), String.valueOf(Config.remarkContentLength));
                    ToastUtil.showToast(toastTip);
                    return;
                }
                mInputDialog.setSendButtonEnable(false);

                loadingTipsDialog.showLoading(mContext.getString(R.string.statuses_replying));
                Params params = new Params();
                params.put("statusesid", cStatuses.getStatusesid());
                params.put("content", content);
                new HttpEntity(mContext).commonPostData(Method.weiboRemark, params, DongtaiMainAdapter.this);
            }
        });
    }

    @Override
    public void onClick(final View view) {
        //设置点击间隔
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, Config.interClickTime);
        cStatuses = (Statuses) view.getTag();
        Intent intent;
        switch(view.getId()) {
            case R.id.linItem1:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doForward(cStatuses, cPosition);
                pauseInVisibleVideo();
                break;
            case R.id.linItem2:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                showInputDialog();
                break;
            case R.id.linItem3:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doPraise(cStatuses, cPosition);
                break;
            case R.id.linItem4:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                //LogUtil.logE("cPosition",cPosition);
                onStatesesListener.doCollect(cStatuses, cPosition, cStatuses.getIsfavorited());
                break;
            case R.id.item_iv:
                intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", cStatuses.getUser().getUserid());
                startAct(intent);
                break;
//		case R.id.praise_more:
//			Intent intent3 = new Intent(mContext, DongtaiPraisedAct.class);
//			intent3.putExtra(key, statuses);
//			mContext.startActivity(intent3);
//			break;
//		case R.id.praise_img:
//			Intent intent5 = new Intent(mContext, TribeMainAct.class);
//			intent5.putExtra("userId", statuses.getUser().getUserid());
//			mContext.startActivity(intent5);
//			break;
            case R.id.imgMore:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                if (String.valueOf(cStatuses.getPublisheruserid()).equals(SharedToken.getUserId())) {
                    showDeletewWindow(view, cPosition);
                } else {
                    showMoreWindow(view);
                }
                break;
            case R.id.tvSquareAtten:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                Params params = new Params();
                params.put("target_userid", cStatuses.getPublisheruserid());
                new HttpEntity(mContext).commonPostData(Method.userFollow, params, this);
                break;
            case R.id.tvMoreRemark:
                intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", cStatuses.getStatusesid());
                intent.putExtra(KEY.SCROLL_TO_POSITION, true);
                intent.putExtra(DongtaiDetailActivity.keyType, cStatuses.getStatusestype());
                startAct(intent);
                break;
        }
    }


    PopupWindow window;
    Statuses cStatuses;
    int cPosition;

    //展示删除window
    void showDeletewWindow(View targetView, int position) {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                if (view.getId() == R.id.statuses_delete) {
                    TipsDialog.popup(mContext, "是否确定删除", "取消", "确定", new TipsDialog.OnTipsListener() {
                        @Override
                        public void onConfirm() {
                            Params params = new Params();
                            params.put("statusesid", cStatuses.getStatusesid());
                            new HttpEntity(mContext).commonPostData(Method.weiboDel, params, DongtaiMainAdapter.this);
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
        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - DimenUtils.dp2px(7);
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, y + bottomHeight);
    }


    //展示更多操作的window
    void showMoreWindow(View targetView) {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                switch(view.getId()) {
                    case R.id.tvItem1:
                        String meId = SharedToken.getUserId();
                        Params params = new Params();
                        params.put("userid", meId);
                        params.put("target_userid", cStatuses.getPublisheruserid());
                        if (cStatuses.getIsshield() == Relation.isshield_yes) {
                            //tvItem1.setText("看Ta的动态");
                            new HttpEntity(mContext).commonPostData(Method.userCancelgnore, params, DongtaiMainAdapter.this);
                        } else {
                            // tvItem1.setText("不看Ta的动态");
                            new HttpEntity(mContext).commonPostData(Method.userIgnore, params, DongtaiMainAdapter.this);
                        }

                        break;
                    case R.id.tvItem2:
                        if (cStatuses.getIsattention() == Relation.isattention_yes) {
                            Params params1 = new Params();
                            params1.put("target_userid", cStatuses.getPublisheruserid());
                            new HttpEntity(mContext).commonPostData(Method.userCancelFollow, params1, DongtaiMainAdapter.this);
                        } else {
                            Params params2 = new Params();
                            params2.put("target_userid", cStatuses.getPublisheruserid());
                            new HttpEntity(mContext).commonPostData(Method.userFollow, params2, DongtaiMainAdapter.this);
                        }

                        break;
                    case R.id.tvItem3:
                        Intent intent = new Intent(mContext, ReportStatusesOrUserAct.class);
                        intent.putExtra("type", ReportStatusesOrUserAct.typeStatuses);
                        intent.putExtra("statuses", cStatuses);
                        startAct(intent);
                        pauseInVisibleVideo();
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


        if (cStatuses.getIsshield() == Relation.isshield_yes) {
            tvItem1.setText(R.string.see_his_statuses);
        } else {
            tvItem1.setText(R.string.not_see_his_statuses);
        }

        if (cStatuses.getIsattention() == Relation.isattention_yes) {
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
        int widthOff = Device.getWidth(mContext) - windowView.getMeasuredWidth() - DimenUtils.dp2px(7);
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, widthOff, y + bottomHeight);//Device.getHeight() - bottomHeight - height
    }


    void startAct(Intent intent) {
        mContext.startActivity(intent);
        //pauseInVisibleVideo();
    }

    private boolean isCanInRoom = true;//不允许重复点击
    OnClickListener linkListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent;
            Statuses statuses;
            switch(view.getId()) {
                case R.id.link1User:
                    UserItem user = (UserItem) view.getTag();
                    intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", user.getUserid());
                    startAct(intent);
                    break;
                case R.id.link2Wish:
                    break;
                case R.id.link3Statuses:
                    statuses = (Statuses) view.getTag();
                    intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", statuses.getStatusesid());
                    intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
                    startAct(intent);
                    break;
                case R.id.link4Goods:
                    Goods goods = (Goods) view.getTag();
                    intent = new Intent(mContext, MallGoodsDetailAct.class);
                    intent.putExtra("goodsid", goods.getGoodsid());
                    startAct(intent);
                    break;
                case R.id.link8Link:
                    LinkInStatuses userlinksinfo = (LinkInStatuses) view.getTag();
                    intent = new Intent(mContext, CommonWeb.class);
                    intent.putExtra("url", userlinksinfo.getLinksurl());
                    startAct(intent);
                    break;
                case R.id.link7Video:
                    statuses = (Statuses) view.getTag();
                    intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", statuses.getStatusesid());
                    intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
                    startAct(intent);
                    break;
                case R.id.link9Hongbao:
                    LinkStatuses link9 = (LinkStatuses) view.getTag();
                    intent = new Intent(mContext, AdDongtaiDetailAct.class);
                    intent.putExtra("statusesId", link9.getLinkid());
                    startAct(intent);
                    break;
                case R.id.link5Live://直播间
                    if (!isCanInRoom) {
                        ToastUtil.showToast(R.string.please_wait);
                        return;
                    }
                    isCanInRoom = false;
                    final int roomId;
                    final LinkStatuses linkStatuses = (LinkStatuses) view.getTag();
                    if (linkStatuses == null) {
                        LogUtil.logE("LogUtil", "linkStatuses = null");
                        break;
                    }
                    roomId = NumUtils.getObjToInt(linkStatuses.getLinkid());
                    showDialog(mContext.getString(R.string.please_wait));

                    String urlLink = linkStatuses.getLinkurl();
                    if (StringUtils.isEmpty(urlLink)) {
                        ToastUtil.showToast(R.string.room_data_error);
                        return;
                    }

                    String roomGroupId = StringUtils.analysisUrlParam(urlLink, "roomgroupid");
                    if (StringUtils.isEmpty(roomGroupId)) {
                        ToastUtil.showToast(R.string.room_data_error);
                        return;
                    }

                    final LiveRoomHelper roomHelper = new LiveRoomHelper(mContext, roomGroupId);

                    roomHelper.setOnQavsdkRoomCloseListener(new QavsdkManger.OnQavsdkRoomCloseListener() {
                        @Override
                        public void roomStatus(int status) {
                            dialogDismiss();
                            isCanInRoom = true;
                            switch(status) {
                                case 1: // 1:房间存在并进入；
                                    break;
                                case 2: // 2：房间存在需关闭回调；
//								break;
                                case 3: //  3：房间不存在，可以创建新房间
                                    //LogUtil.logE("LogUtil", "Add Room Id: " + roomId);
                                    String url = linkStatuses.getLinkurl();
                                    if (url != null) {
                                        Uri uri = Uri.parse(url);
                                        String isneedpwd = uri.getQueryParameter("isneedpwd");
                                        String roomGroupId = uri.getQueryParameter("roomgroupid");
                                        LogUtil.logE("LogUtil", "isneedpwd: " + isneedpwd);
                                        if (isneedpwd.equals("1")) {
                                            String title = String.format(mContext.getString(R.string.dialog_title_in_room), linkStatuses.getUsername());
                                            InRoomPasswordDialog inRoomPasswordDialog = new InRoomPasswordDialog(mContext, title, linkStatuses.getUserhead(), mContext.getString(R.string.ensure_enter), new InRoomPasswordDialog.OkClickListener() {
                                                @Override
                                                public void onClick(String password) {
                                                    if (password == null || password.equals("")) {
                                                        ToastUtil.showToast(R.string.please_input_pwd);
                                                        return;
                                                    }
                                                    roomHelper.inRoom(roomId, password);
                                                }
                                            });
                                            inRoomPasswordDialog.show();
                                        } else {
                                            roomHelper.inRoom(roomId, null);
                                        }
                                    } else {
                                        roomHelper.inRoom(roomId, null);
                                    }
                                    break;
                                case 4: //4:不关闭之前房间
                                    break;
                            }
                        }
                    });
                    roomHelper.isStartServiceAndRoomIdEquals(roomId);
                    break;

            }
        }
    };


    public void stopVideo() {
        VideoPlayAct.videoItemView.stop();
        VideoPlayAct.videoItemView.release();
    }

    public void pauseVideo() {
//        if (VideoPlayAct.videoItemView.isPlay()) {
//            VideoPlayAct.videoItemView.pause();
//        }

    }

    public void pauseInVisibleVideo() {
        if (VideoPlayAct.videoItemView.isPlay()) {
            LogUtil.logE("adapter", "pauseInVisibleVideo");
            VideoPlayAct.videoItemView.pause();
        }

    }


    View viewContainer;
    int videoGroupId;

    //开始播放，并要让其它还原
    void startVideo(String url, View view, int frameLayoutId, int position) {
        viewContainer = view;
        videoGroupId = frameLayoutId;
        cPosition = position;
        for (int i = 0; i < isPlayVideo.size(); i++) {
            if (i == position) {
                isPlayVideo.set(i, true);
            } else {
                isPlayVideo.set(i, false);
            }
        }
        notifyDataSetChanged();

        VideoPlayAct.videoItemView.setOnListener(this);
        ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();//找到videoitemview的父类，然后remove
        if (last != null) {
            last.removeAllViews();
        }

        FrameLayout frameLayout = (FrameLayout) view.findViewById(frameLayoutId);
        frameLayout.removeAllViews();
        frameLayout.addView(VideoPlayAct.videoItemView);

        VideoPlayAct.videoItemView.start(url);//(listData.getList().get(position).getMp4_url());
        //lastPostion=position;
    }


    //从全屏回来
    public void testContinueVideo() {
        if (viewContainer == null || videoGroupId == 0) return;
        ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();
        if (last != null) {
            last.removeAllViews();
        }
        VideoPlayAct.videoItemView.setOnListener(this);
        FrameLayout frameLayout = (FrameLayout) viewContainer.findViewById(videoGroupId);
        frameLayout.removeAllViews();
        frameLayout.addView(VideoPlayAct.videoItemView);
    }


    //从详情回来
    public void resetVideo() {
        for (int i = 0; i < isPlayVideo.size(); i++) {
            isPlayVideo.set(i, false);
        }
        notifyDataSetChanged();
//
//
//        if(viewContainer==null||videoGroupId==0)return;
////        ViewGroup last = (ViewGroup) VideoPlayAct.videoItemView.getParent();
////        if (last != null) {
////            last.removeAllViews();
////        }
//        VideoPlayAct.videoItemView = new VideoPlayView(mContext);
//        VideoPlayAct.videoItemView.setOnListener(this);
//        FrameLayout frameLayout = (FrameLayout) viewContainer.findViewById(videoGroupId);
//        frameLayout.removeAllViews();
//        frameLayout.addView(VideoPlayAct.videoItemView);
    }


    public void setPraise(int position, boolean isPraise) {
        isPraised.set(position, isPraise);
        if (isPraised.get(position)) {
            isPraised.set(position, false);
        } else {
            isPraised.set(position, true);
        }
        notifyDataSetChanged();
    }

    OnStatesesListener onStatesesListener;

    public void setOnStatesesListener(OnStatesesListener onStatesesListener) {
        this.onStatesesListener = onStatesesListener;
    }

    public interface OnStatesesListener {
        void doForward(Statuses statuses, int position);// 转发

        void doRemark(int position, Statuses statuses, String content);// 评论

        void doPraise(Statuses statuses, int position);

        void doCollect(Statuses statuses, int position, int isCollected);// 收藏

        void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition);

        void doFullClick(int position);
    }

    private void showDialog(String message) {
        if (pressageDialog == null)
            pressageDialog = new STProgressDialog(mContext);

        if (pressageDialog.isShowing())
            pressageDialog.dismiss();
        else {
            pressageDialog.setMsgText(message);
            pressageDialog.show();
        }
    }

    private void dialogDismiss() {
        if (pressageDialog != null)
            pressageDialog.dismiss();
    }

    @Override
    public void closeProgress() {
        dialogDismiss();
    }

    @Override
    public void startSdkSuccess() {

    }

    public void onDestory() {
        QavsdkManger.getInstance().unregisterCreateRoom(mContext, flag);
    }
}
