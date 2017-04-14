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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.act.dongtai.StatusesFragmentShoppingCircle;
import com.stbl.stbl.act.dongtai.VideoPlayAct;
import com.stbl.stbl.act.dongtai.AdDongtaiDetailAct;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.adapter.msg.RPGVImgAdapter;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.CommonWeb;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.LinkInStatuses;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.ad.AdUserItem2;
import com.stbl.stbl.item.ad.RPImgInfo;
import com.stbl.stbl.item.ad.RedPacketInfo;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.item.redpacket.RedpacketDetail;
import com.stbl.stbl.model.Ad;
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
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.utils.StringUtils;
import com.stbl.stbl.utils.UmengClickEventHelper;
import com.stbl.stbl.widget.EmojiTextView;
import com.stbl.stbl.widget.ImageGridView;
import com.stbl.stbl.widget.VideoPlayView;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 商圈adapter
 */
public class ShoppingCircleAdapter extends CommonAdapter implements OnClickListener, FinalHttpCallback {

    private String flag = ShoppingCircleAdapter.this.getClass().getSimpleName();
    Activity mContext;
    MyApplication app;
    List<ShoppingCircleDetail> list;
    List<RPGVImgAdapter> listGVIAdapter;
    public final static String key = "item";
    public final int requestDetailDongtai = 100;

    private STProgressDialog pressageDialog;
    private LinearLayout.LayoutParams paramsLong, paramsShort;
    private int type;


    /**
     * 动态主页grid一栏的数目
     */
    public ShoppingCircleAdapter(Activity mContext, int type) {
        this.mContext = mContext;
        this.type = type;
        app = (MyApplication) mContext.getApplication();
        list = new ArrayList<ShoppingCircleDetail>();
        //isShowInput = new ArrayList<Boolean>();
        paramsLong = new LinearLayout.LayoutParams(RPGVImgAdapter.gridWidth(mContext), LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsShort = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        QavsdkManger.getInstance().registerCreateRoom(mContext, flag);
        listGVIAdapter = new ArrayList<>();
    }

    public void setData(List<ShoppingCircleDetail> list) {
        listGVIAdapter.clear();
        for (int i = 0; i < list.size(); i++) {
            listGVIAdapter.add(null);
        }
        this.list = list;
        notifyDataSetChanged();
    }


    public void deleteStatusesId(String statusesId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSquareid().equals(statusesId)) {
                list.remove(i);
                listGVIAdapter.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public void deleteItem() {
        list.remove(cStatuses);
        notifyDataSetChanged();
    }


    /**
     * 发布微博之后调用.加载置顶的后面
     *
     * @param statuses
     */
    public void addItemAfterPulish(ShoppingCircleDetail statuses) {
        list.add(0, statuses);
        listGVIAdapter.add(0, null);
        notifyDataSetChanged();
    }

    public void addData(List<ShoppingCircleDetail> list) {
        checkDataRepeat(list);
        for (int i = 0; i < list.size(); i++) {
            listGVIAdapter.add(null);
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    //检查数据重复问题
    void checkDataRepeat(List<ShoppingCircleDetail> list) {
        if (list == null) return;
        boolean isRepeat = false;
        for (int i = 0; i < this.list.size(); i++) {
            if (isRepeat) break;
            ShoppingCircleDetail item1 = this.list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (item1.getSquareid().equals(list.get(j).getSquareid())) {
                    isRepeat = true;
                    //ToastUtil.showToast("警告 数据重复索引是"+i+" "+j);
                    break;
                }
            }
        }
//        if (isRepeat) {
//            LogUtil.logE("购物圈列表", "数据重复");
//        } else {
//            LogUtil.logE("购物圈列表", "数据不重复");
//        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

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
        switch (methodName) {
            case Method.weiboDel:
                //deleteStatusesId(cStatuses.getStatusesid());
                break;
            case Method.userFollow:
                ToastUtil.showToast(R.string.follow_success);
                AdUserItem2 user = cStatuses.getUserinfo();
                //关注所有
                for (ShoppingCircleDetail itemData : list) {
                    if (itemData.getUserid() == user.getAduserid()) {
                        AdUserItem2 user2 = itemData.getUserinfo();
                        user2.setIsattention(Relation.isattention_yes);
                        itemData.setUserinfo(user2);
                        notifyDataSetChanged();
                    }
                }
                notifyDataSetChanged();
                break;
            case Method.userCancelFollow:
                ToastUtil.showToast(R.string.unfollow_success);
                //cStatuses.setIsattention(Statuses.isattention_no);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;
            case Method.userIgnore:
                ToastUtil.showToast(R.string.shield_success);
                //cStatuses.setIsshield(Statuses.isshield_yes);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;
            case Method.deleteShoppingCircle:
                ToastUtil.showToast("删除成功");
                deleteItem();
                break;


        }
    }


    class CityHolder {
        GridView itemGV;
        TextView userName;
        ImageView userImg;
        EmojiTextView tvShortTitle;
        View linLongSpe;
        //TextView tvSquareAtten;
        View tvDelete;
        TextView tvHongbaoOperate;
        View linPickHongbao;
        ImageView imgAdLevel;

    }


    // 更新整个item
    public void updateItem(ShoppingCircleDetail statuses, int position) {
        if (list.size() > position) {
            list.set(position, statuses);
            notifyDataSetChanged();
        }
    }


    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.shopping_circle_list_item, null);
            ho.userName = (TextView) con.findViewById(R.id.name);

            ho.userImg = (ImageView) con.findViewById(R.id.item_iv);
            ho.itemGV = (GridView) con.findViewById(R.id.item_gv);
            ho.tvShortTitle = (EmojiTextView) con.findViewById(R.id.tvShortTitle);
            ho.linLongSpe = con.findViewById(R.id.linLongSpe);
            //ho.tvSquareAtten = (TextView) con.findViewById(R.id.tvSquareAtten);
            ho.tvHongbaoOperate = (TextView) con.findViewById(R.id.tvHongbaoOperate);
            ho.linPickHongbao = con.findViewById(R.id.linPickHongbao);
            ho.imgAdLevel = (ImageView) con.findViewById(R.id.imgAdLever);
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
        final ShoppingCircleDetail statuses = list.get(i);
        final RPImgInfo pics = statuses.getImginfo2();
        //LogUtil.logE("adapter imgSize", i + ":" + pics.getPics().size());
        int imgSize = pics.getMiddlepic().size();
        int numClumn = 0;
        // 如果是长微博。就需要放大
        if (pics != null) {
            if (imgSize > 3) {
                numClumn = GVImgAdapter.gridColumn;
            } else {
                numClumn = imgSize == 0 ? GVImgAdapter.gridColumn : imgSize;
            }
        }
        RPGVImgAdapter gridAdapter = null;
        if (listGVIAdapter.get(i) == null) {
//            gridAdapter = new GVImgAdapter(mContext, pics, numClumn);
            gridAdapter = new RPGVImgAdapter(mContext, pics, numClumn, true);//true表示是购物圈
            gridAdapter.setStatusesId(statuses.getSquareid());
            listGVIAdapter.set(i, gridAdapter);
        } else {
            gridAdapter = listGVIAdapter.get(i);
        }
        gridAdapter.setImgExpand(false);
        AdUserItem2 user = statuses.getUserinfo();
        String mIconTitle = user.getIcontitle();
        if (mIconTitle == null) {
            ho.imgAdLevel.setVisibility(View.GONE);
            ho.userImg.setBackgroundResource(R.color.transparent);
        } else {
            switch (user.getIcontitle()) {
                case AdUserItem2.icontitleGold:
                    ho.imgAdLevel.setVisibility(View.VISIBLE);
                    ho.imgAdLevel.setImageResource(R.drawable.ad_level_gold);
                    ho.userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_gold);
                    break;
                case AdUserItem2.icontitleSilver:
                    ho.imgAdLevel.setVisibility(View.VISIBLE);
                    ho.imgAdLevel.setImageResource(R.drawable.ad_level_silver);
                    ho.userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_silver);
                    break;
                case AdUserItem2.icontitleCopper:
                    ho.imgAdLevel.setVisibility(View.VISIBLE);
                    ho.imgAdLevel.setImageResource(R.drawable.ad_level_cooper);
                    ho.userImg.setBackgroundResource(R.drawable.shape_circle_img_bg_cooper);
                    break;
                default:
                    ho.imgAdLevel.setVisibility(View.GONE);
                    ho.userImg.setBackgroundResource(R.color.transparent);
                    break;

            }
        }


        final RedPacketInfo redPacketInfo = statuses.getRpinfo();
        if (redPacketInfo != null) {
            String moneyAccount = StringUtil.get2ScaleString(Float.valueOf(redPacketInfo.getAmount()));
            switch (type) {
                case StatusesFragmentShoppingCircle.typeBrowse:
                    switch (redPacketInfo.getStatus()) {
                        case RedpacketDetail.status_pickabel:
                            ho.tvHongbaoOperate.setText("抢红包 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_red);
                            break;
                        case RedpacketDetail.status_expire:
                            ho.tvHongbaoOperate.setText("红包已过期 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_gray_ccc);
                            break;
                        case RedpacketDetail.status_pickOver:
                            ho.tvHongbaoOperate.setText(redPacketInfo.getEndtime() + "抢完 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_gray_ccc);
                            break;
                    }


                    break;
                case StatusesFragmentShoppingCircle.typeManage:
                    switch (redPacketInfo.getStatus()) {
                        case RedpacketDetail.status_pickabel:
                            ho.tvHongbaoOperate.setText("抢红包 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_red);
                            break;
                        case RedpacketDetail.status_expire:
                            ho.tvHongbaoOperate.setText("红包已过期 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_gray_ccc);
                            break;
                        case RedpacketDetail.status_pickOver:
                            ho.tvHongbaoOperate.setText(redPacketInfo.getEndtime() + "抢完 ￥" + moneyAccount);
                            ho.linPickHongbao.setBackgroundResource(R.drawable.common_btn_gray_ccc);
                            break;
                    }
                    break;
            }
        }


        String content = statuses.getContent();
        if (content == null || content.equals("")) {
            ho.tvShortTitle.setVisibility(View.GONE);
        } else {
            ho.tvShortTitle.setVisibility(View.VISIBLE);
            content = Util.halfToFull(content);// 转换为全角
            EmojiParseThread.getInstance().parse(content, ho.tvShortTitle);
        }
        //ho.itemGV.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);

        if (pics == null || imgSize == 0) {
            ho.itemGV.setVisibility(View.GONE);
        } else if (imgSize == 1) {
            gridAdapter.setType(RPGVImgAdapter.typeShort1);
        } else {
            gridAdapter.setType(RPGVImgAdapter.typeShort);
        }
        ho.linLongSpe.setBackgroundResource(R.color.transparent);

        ho.linLongSpe.setLayoutParams(paramsShort);
        ho.linLongSpe.setPadding(0, (int) mContext.getResources().getDimension(R.dimen.dp_12), 0, 0);
        // 图片
        ho.itemGV.setNumColumns(numClumn);
        ho.itemGV.setAdapter(gridAdapter);
        ViewUtils.setStatusesAdapterViewHeight(ho.itemGV, numClumn);//GVImgAdapter.gridColumn   numClumn
        // 判断是否显示

        if (pics == null || imgSize == 0) {
            ho.itemGV.setVisibility(View.GONE);
        } else {
            ho.itemGV.setVisibility(View.VISIBLE);
        }
        setTag(statuses, ho.userImg);

        ho.userName.setText(user.getAdnickname());
        ImageUtils.loadIcon(user.getAdimgurl(), ho.userImg);
        ho.userImg.setOnClickListener(this);
        ho.itemGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {//购物圈点图片都跳详情
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onStatesesListener.enterDongtaiDetail(i, statuses, false);
            }
        });
        // 进入详细
        con.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onStatesesListener.enterDongtaiDetail(i, statuses, false);
            }
        });
        ho.tvHongbaoOperate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, AdDongtaiDetailAct.class);
//                if (redPacketInfo != null) {
//                    intent.putExtra("redpacketstatus", redPacketInfo.getStatus());
//                }
//                intent.putExtra("statusesId", statuses.getSquareid());
//                mContext.startActivity(intent);
                onStatesesListener.enterDongtaiDetail(i, statuses, false);
            }
        });
        return con;
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
        cStatuses = (ShoppingCircleDetail) view.getTag();
        Intent intent;
        switch (view.getId()) {
            case R.id.item_iv:
                //如果点击类型是广告，并且广告url不为空，则跳转广告;否则跳转部落

                if (cStatuses.getUserinfo().getClicktype() == AdUserItem2.typeClickAd && !TextUtils.isEmpty(cStatuses.getUserinfo().getAdurl())) {
                    intent = new Intent(mContext, CommonWeb.class);
                    //intent.putExtra("url", cStatuses.getUserinfo().getAdurl());
                    Ad ad = new Ad();
                    ad.businessclass = Ad.businessclassNone;
                    ad.businessclassname = "";
                    ad.adurl = cStatuses.getUserinfo().getAdurl();
                    ad.adid = cStatuses.getUserinfo().getAdid();
                    intent.putExtra("ad", ad);
                    intent.putExtra("type", CommonWeb.typeAd);

                    mContext.startActivity(intent);
                } else {
                    intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", cStatuses.getUserid());
                    mContext.startActivity(intent);
                }
                break;
            case R.id.imgMore:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
//                if (String.valueOf(cStatuses.getPublisheruserid()).equals(SharedToken.getUserId())) {
//                    showDeletewWindow(view, cPosition);
//                } else {
//                    showMoreWindow(view);
//                }
                break;
//            case R.id.tvSquareAtten:
//                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
//                Params params = new Params();
//                params.put("target_userid", cStatuses.getUserid());
//                new HttpEntity(mContext).commonPostData(Method.userFollow, params, this);
//                break;
        }
    }


    PopupWindow window;
    ShoppingCircleDetail cStatuses;
    int cPosition;

    //展示删除window
    void showDeletewWindow(View targetView, int position) {
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                window.dismiss();
                if (view.getId() == R.id.statuses_delete) {
                    Params params = new Params();
                    params.put("statusesid", cStatuses.getSquareid());
                    new HttpEntity(mContext).commonPostData(Method.weiboDel, params, ShoppingCircleAdapter.this);
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


    OnStatesesListener onStatesesListener;

    public void setOnStatesesListener(OnStatesesListener onStatesesListener) {
        this.onStatesesListener = onStatesesListener;
    }

    public interface OnStatesesListener {
        void enterDongtaiDetail(int position, ShoppingCircleDetail statuses, boolean scrollToPosition);
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


}
