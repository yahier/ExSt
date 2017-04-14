package yahier.exst.adapter;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.ui.DirectScreen.dialog.InRoomPasswordDialog;
import com.stbl.stbl.ui.DirectScreen.util.LiveRoomHelper;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.utils.NumUtils;
import com.stbl.stbl.widget.ImageGridView;
import com.stbl.stbl.widget.avsdk.QavsdkManger;
import com.stbl.stbl.widget.dialog.STProgressDialog;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DongtaiSquareAdapter extends CommonAdapter implements OnClickListener, QavsdkManger.OnQavsdkUpdateListener{

    Activity mContext;
    MyApplication app;
    List<Statuses> list;
    //List<Boolean> isShowInput;
    List<Boolean> isPraised;
    public final static String key = "item";
    public final int requestDetailDongtai = 100;
    public final static int requestTribe = 105;
    private STProgressDialog pressageDialog;
    private String flag = DongtaiSquareAdapter.this.getClass().getSimpleName();
    private  LinearLayout.LayoutParams paramsLong,paramsShort;
    /**
     * 动态主页grid一栏的数目
     */

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public DongtaiSquareAdapter(Activity mContext) {
        this.mContext = mContext;
        app = (MyApplication) mContext.getApplication();
        list = new ArrayList<Statuses>();
        //isShowInput = new ArrayList<Boolean>();
        isPraised = new ArrayList<Boolean>();
        paramsLong = new LinearLayout.LayoutParams(GVImgAdapter.gridWidth(mContext), ActionBar.LayoutParams.WRAP_CONTENT);
        paramsShort = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void setData(List<Statuses> list) {
        //isShowInput.clear();
        isPraised.clear();
        for (int i = 0; i < list.size(); i++) {
            //isShowInput.add(false);
            isPraised.add(false);
        }
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * 更改关注状态
     *
     * @param userId
     * @param followState
     */
    public void setFollowStateChanged(long userId, boolean followState) {
        for (int i = 0; i < list.size(); i++) {
            Statuses statuses = list.get(i);
            if (statuses.getUser().getUserid() == userId) {
                LogUtil.logE("改变状态");
                if (followState) {
                    statuses.setIsattention(Statuses.isattention_yes);
                } else {
                    statuses.setIsattention(Statuses.isattention_no);
                }
                list.set(i, statuses);
            }
        }
        notifyDataSetChanged();
    }

    public void deleteStatusesId(long statusesId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatusesid() == statusesId) {
                list.remove(i);
                //isShowInput.remove(i);
                isPraised.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 发布微博之后调用
     *
     * @param statuses
     */
    public void addItem(Statuses statuses) {
        list.add(0, statuses);
        //isShowInput.add(0, false);
        isPraised.add(0, false);
        notifyDataSetChanged();
    }

    public void addData(List<Statuses> list) {
        for (int i = 0; i < list.size(); i++) {
            //isShowInput.add(false);
            isPraised.add(false);
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    class CityHolder {
        LinearLayout mainZone;
        GridView itemGV;

        TextView userName, userGenderAge;
        ImageView userImg;

        TextView  relation,tvCity, time;
        TextView item1, item2, item3, item4;
        TextView tvFollowState;
        // 链接
        LinearLayout link1Lin, link2Lin, link3Lin, link4Lin, link5Lin;
        ImageView link3Img;
        TextView link3Tv,lin3UserName;
        TextView tvShortTitle,tvLongTitle,tvLongContent;
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
        View linLongSpe;
        View linLink;
        View imgAuthorized;
    }

    // 直接notifyDataSetChanged
    public void updateCollectText(ListView listView, int position,int collectcount) {
        Statuses statuses = list.get(position);
        statuses.setIsfavorited(Statuses.isfavoritedYes);
        statuses.setFavorcount(collectcount);
        list.set(position, statuses);
        notifyDataSetChanged();
    }

    public void updateCancelCollectText(ListView listView, int position,int collectcount) {
        Statuses statuses = list.get(position);
        statuses.setIsfavorited(Statuses.isfavoritedNo);
        statuses.setFavorcount(collectcount);
        list.set(position, statuses);
        notifyDataSetChanged();
    }

    // 更新转发数.
    public void updateForwardText(ListView listView, int position) {
        Statuses statuses = list.get(position);
        list.set(position, statuses);
        notifyDataSetChanged();
    }

    // 更新整个item
    public void updateItem(Statuses statuses, int position) {
        if (position < list.size()){
            list.set(position, statuses);
            notifyDataSetChanged();
        }
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

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.dongtai_square_list_item, null);
            ho.mainZone = (LinearLayout) con.findViewById(R.id.main_zone);
            ho.userName = (TextView) con.findViewById(R.id.name);
            ho.tvShortTitle = (TextView) con.findViewById(R.id.tvShortTitle);
            ho.tvLongTitle = (TextView) con.findViewById(R.id.tvLongTitle);
            ho.tvLongContent = (TextView) con.findViewById(R.id.tvLongContent);
            ho.relation = (TextView) con.findViewById(R.id.relation);
            ho.tvCity = (TextView) con.findViewById(R.id.tvCity);
            ho.time = (TextView) con.findViewById(R.id.time);
            ho.userGenderAge = (TextView) con.findViewById(R.id.gender_age);
            ho.userImg = (ImageView) con.findViewById(R.id.item_iv);
            ho.itemGV = (GridView) con.findViewById(R.id.item_gv);

            ho.item1 = (TextView) con.findViewById(R.id.item_text1);
            ho.item2 = (TextView) con.findViewById(R.id.item_text2);
            ho.item3 = (TextView) con.findViewById(R.id.item_text3);
            ho.item4 = (TextView) con.findViewById(R.id.item_text4);

            ho.link3Lin = (LinearLayout) con.findViewById(R.id.link3);
            ho.link3Img = (ImageView) con.findViewById(R.id.link3_img);
            ho.link3Tv = (TextView) con.findViewById(R.id.link3_content);
            ho.lin3UserName = (TextView)con.findViewById(R.id.link3_userName);
            ho.link1Lin = (LinearLayout) con.findViewById(R.id.link1);
            ho.link2Lin = (LinearLayout) con.findViewById(R.id.link2);
            ho.link4Lin = (LinearLayout) con.findViewById(R.id.link4);

            //	ho.inputLin = (FaceRelativeLayout) con.findViewById(R.id.input_lin);
            //	ho.input = (EditText) con.findViewById(R.id.et_sendmessage);
            //	ho.sendRemark = (ImageView) con.findViewById(R.id.btn_send);
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

            ho.tvFollowState = (TextView) con.findViewById(R.id.tvFollowState);
            ho.link5Lin = (LinearLayout) con.findViewById(R.id.link5);
            ho.link5img = (ImageView) con.findViewById(R.id.link5_img);
            ho.link5tvTitle = (TextView) con.findViewById(R.id.tv_user_live);
            ho.link5tvContent = (TextView) con.findViewById(R.id.link5_content);
            ho.linLongSpe = con.findViewById(R.id.linLongSpe);
            ho.linLink = con.findViewById(R.id.linLink);
            ho.imgAuthorized = con.findViewById(R.id.imgAuthorized);
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
        int numClumn;
        // 如果只有一张图，或者是长微博。就需要放大
        if (pics != null && statuses.getStatusestype() == Statuses.type_long) {
            numClumn = 1;
        } else {
            numClumn = GVImgAdapter.gridColumn;
        }
        GVImgAdapter gridAdapter = new GVImgAdapter(mContext, pics, numClumn);
        gridAdapter.setStatusesId(statuses.getStatusesid());
        // 识别长动态与短动态
        switch (statuses.getStatusestype()) {
            case Statuses.type_short:
                String content = statuses.getContent();
                content = content.replace("\n", "");
                if (content == null || content.equals("")) {
                    ho.tvShortTitle.setVisibility(View.GONE);
                } else {
                    ho.tvShortTitle.setVisibility(View.VISIBLE);
                    content = Util.halfToFull(content);// 转换为全角
                    ho.tvShortTitle.setText(content);
                }
                ho.tvLongTitle.setVisibility(View.GONE);
                ho.tvLongContent.setVisibility(View.GONE);
                ho.itemGV.setPadding(0,0,(int)mContext.getResources().getDimension(R.dimen.statuses_right_padding),0);
                ho.linLongSpe.setBackgroundResource(R.color.transparent);
                ho.linLongSpe.setLayoutParams(paramsShort);
                if(pics!=null&&pics.getPics().size()==4){
                    gridAdapter.setType(GVImgAdapter.typeShort4);
                }else{
                    gridAdapter.setType(GVImgAdapter.typeShort);
                }
                break;
            case Statuses.type_long:
                gridAdapter.setType(GVImgAdapter.typeLong);
                ho.tvShortTitle.setVisibility(View.GONE);
                if (statuses.getContent() == null)
                    break;
                String pureContent = statuses.getContent().replace(Config.longWeiboFillMark, "");
                String title = statuses.getTitle();
                LogUtil.logE(pureContent);
                pureContent = pureContent.replace("\n", "");
                LogUtil.logE(pureContent);
                if (title == null || title.equals("")) {
                    ho.tvLongTitle.setVisibility(View.GONE);
                } else {
                    ho.tvLongTitle.setVisibility(View.VISIBLE);
                    ho.tvLongTitle.setText(title);
                }

                if (pureContent.equals("")) {
                    ho.tvLongContent.setVisibility(View.GONE);
                } else {
                    ho.tvLongContent.setVisibility(View.VISIBLE);
                    pureContent = Util.halfToFull(pureContent);// 转换为全角
                    ho.tvLongContent.setText(pureContent);
                }
                ho.itemGV.setPadding(0,0,0,0);
                ho.linLongSpe.setBackgroundResource(R.color.theme_bg);
                ho.linLongSpe.setLayoutParams(paramsLong);
                break;
        }
        ho.linLink.setLayoutParams(paramsLong);
        // 图片

        ho.itemGV.setNumColumns(numClumn);
        ho.itemGV.setAdapter(gridAdapter);
        ViewUtils.setStatusesAdapterViewHeight(ho.itemGV, GVImgAdapter.gridColumn);
        // 最上一层
        ho.time.setText(DateUtil.getTimeOff(statuses.getCreatetime()));
        ho.item1.setText(statuses.getForwardcount() + "");
        ho.item2.setText(statuses.getCommentcount() + "");
        ho.item3.setText(statuses.getPraisecount() + "");
        ho.item4.setText(statuses.getFavorcount() + "");
        // 显示四个item是否操作
        if (statuses.getIspraised() == Statuses.ispraisedYes) {
            ho.item3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_praise_presed, 0, 0, 0);
            isPraised.set(i, true);
        } else {
            ho.item3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_item3, 0, 0, 0);

        }

        if (statuses.getIsattention() == Statuses.isattention_yes) {
            ho.tvFollowState.setText("已关注");
            ho.tvFollowState.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dongtai_follow_yes, 0, 0);
        } else {
            ho.tvFollowState.setText("关注");
            ho.tvFollowState.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dongtai_follow_no, 0, 0);
        }

        if (statuses.getIsfavorited() == Statuses.isfavoritedYes) {
            ho.item4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_collect_pressed, 0, 0, 0);
        } else {
            ho.item4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_item4, 0, 0, 0);
        }
        ho.item1.setOnClickListener(this);
        ho.item2.setOnClickListener(this);
        ho.item3.setOnClickListener(this);
        ho.item4.setOnClickListener(this);
        ho.tvFollowState.setOnClickListener(this);

        setTag(statuses, ho.userImg, ho.item1, ho.item2, ho.item3, ho.item4, ho.tvFollowState);
        setPositionTag(i, ho.item1, ho.item2, ho.item3, ho.item4, ho.tvFollowState);
        // 图片列表图像
        // 得到用户列表
        UserItem user = statuses.getUser();
        ho.userName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//        ho.userName.setText(user.getNickname());
        ho.tvCity.setText(user.getCityname());
        if(user.getCertification()==UserItem.certificationYes){
            ho.imgAuthorized.setVisibility(View.VISIBLE);
        }else{
            ho.imgAuthorized.setVisibility(View.GONE);
        }
        if(Relation.isMaster(user.getRelationflag())){
            ho.relation.setText(UserItem.relationTypeMaster);
            ho.relation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_master, 0, 0, 0);
        }else  if(Relation.isStu(user.getRelationflag())){
            ho.relation.setText(UserItem.relationTypeStudent);
            ho.relation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_student, 0, 0, 0);
        }else{
            ho.relation.setText("");
            ho.relation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }


        ho.userGenderAge.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_boy_bg);
        } else if (user.getGender() == UserItem.gender_girl){
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_girl_bg);
        }else {
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
        }
        PicassoUtil.load(mContext, user.getImgurl(), ho.userImg);
        ho.userImg.setOnClickListener(this);
        // 进入详细
        con.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onStatesesListener.enterDongtaiDetail(i, statuses);

            }
        });
        //
//		final CityHolder newHo = ho;
//		ho.sendRemark.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				String con = newHo.input.getText().toString();
//				onStatesesListener.doRemark(i, statuses, con);
//			}
//		});

        // 这里来处理链接的内容啦
        LinkStatuses link = statuses.getLinks();
        if (link != null) {
            int linkType = link.getLinktype();
            ho.linLink.setVisibility(View.VISIBLE);
            switch (linkType) {
                case LinkStatuses.linkTypeCrad:
                    ho.link1Lin.setVisibility(View.VISIBLE);
                    ho.link2Lin.setVisibility(View.GONE);
                    ho.link3Lin.setVisibility(View.GONE);
                    ho.link4Lin.setVisibility(View.GONE);
                    UserItem linkUser = link.getUserinfo();
                    if (linkUser == null)
                        break;
                    ho.lin1Name.setText(linkUser.getAlias() == null || linkUser.getAlias().equals("") ? linkUser.getNickname() : linkUser.getAlias());
//                    ho.lin1Name.setText(linkUser.getNickname());
                    PicassoUtil.load(mContext, linkUser.getImgurl(), ho.link1Img);
                    ho.link1City.setText(linkUser.getCityname());
                    ho.link1Signature.setText(linkUser.getSignature());
                    ho.link1Lin.setTag(linkUser);
                    ho.link1Lin.setOnClickListener(linkListener);

                    ho.link1AgeGender.setText(linkUser.getAge() + "");
                    if (linkUser.getGender() == UserItem.gender_boy) {
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_boy_bg);
                    } else if (linkUser.getGender() == UserItem.gender_girl){
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_girl_bg);
                    } else {
                        ho.link1AgeGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        ho.link1AgeGender.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
                    }

                    break;
                case LinkStatuses.linkTypeWish:
                    ho.link2Lin.setVisibility(View.VISIBLE);
                    ho.link2Lin.setOnClickListener(linkListener);

                    ho.link1Lin.setVisibility(View.GONE);
                    ho.link3Lin.setVisibility(View.GONE);
                    ho.link4Lin.setVisibility(View.GONE);
                    break;
                case LinkStatuses.linkTypeStateses:
                    ho.link3Lin.setVisibility(View.VISIBLE);
                    ho.link1Lin.setVisibility(View.GONE);
                    ho.link2Lin.setVisibility(View.GONE);
                    ho.link4Lin.setVisibility(View.GONE);
                    Statuses linkStatuses = link.getStatusesinfo();
                    if (linkStatuses == null) {
                        LogUtil.logE("linkStatuses is null");
                        break;
                    }
                    if(linkStatuses.getUser()!=null){
                        ho.lin3UserName.setText(linkStatuses.getUser().getAlias() == null || linkStatuses.getUser().getAlias().equals("")
                                ? linkStatuses.getUser().getNickname() : linkStatuses.getUser().getAlias());
//                        ho.lin3UserName.setText(linkStatuses.getUser().getNickname());
                    }
                    String title = linkStatuses.getTitle();
                    if (title != null && !title.equals("")) {
                        ho.link3Tv.setText(title);
                    } else {
                        String content = linkStatuses.getContent().replace(Config.longWeiboFillMark, "");
                        ho.link3Tv.setText(content);
                    }
                    // 好奇怪啊 。怎么没显示动态的值呢。打印都有显示呢

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
                        LogUtil.logE("link3Pics imgUrl is " + imgUrl);
                        PicassoUtil.loadStatuses(mContext, imgUrl, ho.link3Img);
                    } else {
                        LogUtil.logE("link3Pics is null");
                        PicassoUtil.load(mContext, R.drawable.dongtai_default, ho.link3Img);
                    }
                    ho.link3Lin.setTag(linkStatuses);
                    ho.link3Lin.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeProduct:
                    ho.link4Lin.setVisibility(View.VISIBLE);
                    ho.link1Lin.setVisibility(View.GONE);
                    ho.link2Lin.setVisibility(View.GONE);
                    ho.link3Lin.setVisibility(View.GONE);
                    Goods goods = link.getProductinfo();
                    if (goods == null)
                        break;
                    PicassoUtil.load(mContext, goods.getImgurl(), ho.link4imgLink);
                    ho.link4tvGoodsTitle.setText(goods.getGoodsname());
                    ho.link4tvGoodsPrice.setText("￥" + goods.getMinprice());
                    ho.link4tvGoodsSale.setText("销量:" + goods.getSalecount());
                    ho.link4Lin.setTag(goods);
                    ho.link4Lin.setOnClickListener(linkListener);
                    break;
                case LinkStatuses.linkTypeLive:
                    ho.link1Lin.setVisibility(View.GONE);
                    ho.link2Lin.setVisibility(View.GONE);
                    ho.link3Lin.setVisibility(View.GONE);
                    ho.link4Lin.setVisibility(View.GONE);
                    ho.link5Lin.setVisibility(View.VISIBLE);
                    ho.link5tvTitle.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias() + "开启了直播");
//                    ho.link5tvTitle.setText(user.getNickname() + "开启了直播");
                    ho.link5tvContent.setText("话题：" + (link.getLinktitle() == null ? "--" : link.getLinktitle()));
//				String linkId = link.getLinkid();
                    link.setUsername(user.getNickname());
                    link.setUserhead(user.getImgurl());
                    ho.link5Lin.setTag(link);
                    ho.link5Lin.setOnClickListener(linkListener);
            }

        } else {
            ho.linLink.setVisibility(View.GONE);
        }
        //用于进入房间防止重复点击的，防止其他情况导致状态不对，adapter刷新就重置状态
        isCanInRoom = true;
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
    public void onClick(View view) {
        Statuses statuses = (Statuses) view.getTag();
        Intent intent;
        switch (view.getId()) {
            case R.id.item_text1:
                int position1 = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doForward(statuses, position1);
                break;
            case R.id.item_text2:
                int position2 = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.enterDongtaiDetail(position2, statuses);
                break;
            case R.id.item_text3:
                int position3 = (int) view.getTag(R.id.tag_dongtai_item);
                // notifyDataSetChanged();
                onStatesesListener.doPraise(statuses.getStatusesid(), position3);
                break;
            case R.id.item_text4:
                int position4 = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doCollect(statuses.getStatusesid(), position4, statuses.getIsfavorited());
                break;
            case R.id.item_iv:
                Intent intent2 = new Intent(mContext, TribeMainAct.class);
                intent2.putExtra("userId", statuses.getUser().getUserid());
                mContext.startActivity(intent2);
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
            case R.id.tvFollowState:
                int position6 = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.enterTribe(statuses);
                break;
        }
    }
    private boolean isCanInRoom = true;//不允许重复点击
    OnClickListener linkListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.link1:
                    UserItem user = (UserItem) view.getTag();
                    intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", user.getUserid());
                    mContext.startActivity(intent);
                    break;
                case R.id.link2:
                    break;
                case R.id.link3:
                    Statuses statuses = (Statuses) view.getTag();
                    intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", statuses.getStatusesid());
                    intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
                    mContext.startActivity(intent);
                    break;
                case R.id.link4:
                    Goods goods = (Goods) view.getTag();
                    intent = new Intent(mContext, MallGoodsDetailAct.class);
                    intent.putExtra("goodsid", goods.getGoodsid());
                    mContext.startActivity(intent);
                    break;
                case R.id.link5://直播间
                    if (!isCanInRoom) {
                        ToastUtil.showToast(mContext, "请稍后");
                        return;
                    }
                    isCanInRoom = false;

                    final LinkStatuses linkStatuses = (LinkStatuses) view.getTag();
                    if (linkStatuses == null) {
                        LogUtil.logE("LogUtil", "linkStatuses = null");
                        break;
                    }
                    final int roomId = NumUtils.getObjToInt(linkStatuses.getLinkid());
                    showDialog("请稍后...");
//                    QavsdkManger.getInstance().setOnQavsdkRoomCloseListener(new QavsdkManger.OnQavsdkRoomCloseListener() {
//                        @Override
//                        public void roomStatus(int status) {
//                            dialogDismiss();
//                            isCanInRoom = true;
//                            switch (status) {
//                                case 1: // 1:房间存在并进入；
//                                    break;
//                                case 2: // 2：房间存在需关闭回调；
////								break;
//                                case 3: //  3：房间不存在，可以创建新房间
//                                    LogUtil.logE("LogUtil", "Add Room Id: " + roomId);
//                                    String url = linkStatuses.getLinkurl();
//                                    if (url != null) {
//                                        Uri uri = Uri.parse(url);
//                                        String isneedpwd = uri.getQueryParameter("isneedpwd");
//                                        LogUtil.logE("LogUtil", "isneedpwd: " + isneedpwd);
//                                        if (isneedpwd.equals("1")) {
//                                            String title = String.format(mContext.getString(R.string.dialog_title_in_room), linkStatuses.getUsername());
//                                            InRoomPasswordDialog inRoomPasswordDialog = new InRoomPasswordDialog(mContext, title, linkStatuses.getUserhead(), "确认进入", new InRoomPasswordDialog.OkClickListener() {
//                                                @Override
//                                                public void onClick(String password) {
//                                                    if (password == null || password.equals("")) {
//                                                        ToastUtil.showToast(mContext, "请输入密码");
//                                                        return;
//                                                    }
//                                                    inRoom(roomId, password);
//                                                }
//                                            });
//                                            inRoomPasswordDialog.show();
//                                        } else {
//                                            inRoom(roomId, null);
//                                        }
//                                    } else {
//                                        inRoom(roomId, null);
//                                    }
//                                    break;
//                                case 4: //4:不关闭之前房间
//                                    break;
//                            }
//                        }
//                    });
//                    QavsdkManger.getInstance().isStartServiceAndRoomIdEquals(mContext, roomId);
                    final LiveRoomHelper roomHelper = new LiveRoomHelper(mContext,flag);
                    roomHelper.setOnQavsdkRoomCloseListener(new QavsdkManger.OnQavsdkRoomCloseListener() {
                        @Override
                        public void roomStatus(int status) {
                            dialogDismiss();
                            isCanInRoom = true;
                            switch (status) {
                                case 1: // 1:房间存在并进入；
                                    break;
                                case 2: // 2：房间存在需关闭回调；
//								break;
                                case 3: //  3：房间不存在，可以创建新房间
                                    LogUtil.logE("LogUtil", "Add Room Id: " + roomId);
                                    String url = linkStatuses.getLinkurl();
                                    if (url != null) {
                                        Uri uri = Uri.parse(url);
                                        String isneedpwd = uri.getQueryParameter("isneedpwd");
                                        LogUtil.logE("LogUtil", "isneedpwd: " + isneedpwd);
                                        if (isneedpwd.equals("1")) {
                                            String title = String.format(mContext.getString(R.string.dialog_title_in_room), linkStatuses.getUsername());
                                            InRoomPasswordDialog inRoomPasswordDialog = new InRoomPasswordDialog(mContext, title, linkStatuses.getUserhead(), "确认进入", new InRoomPasswordDialog.OkClickListener() {
                                                @Override
                                                public void onClick(String password) {
                                                    if (password == null || password.equals("")) {
                                                        ToastUtil.showToast(mContext, "请输入密码");
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


    /**
     *进入房间请求
//     * @param roomId 房间id
//     * @param pwd 密码
     */
//    private void inRoom(int roomId, String pwd){
//        try {
//            showDialog("正在加入房间,请稍候...");
//            JSONObject json = new JSONObject();
//            json.put("roomid", roomId);
//            if (pwd != null) json.put("pwd",pwd);
//            new HttpEntity(mContext).commonPostJson(Method.imLiveroomIn, json.toJSONString(), new FinalHttpCallback() {
//                @Override
//                public void parse(String methodName, String result) {
//                    Log.e(methodName, result);
//                    BaseItem item = JSONHelper.getObject(result, BaseItem.class);
//                    if (item.getIssuccess() != BaseItem.successTag) {
//                        dialogDismiss();
//                        ToastUtil.showToast(mContext, item.getErr().getMsg());
//                        return;
//                    }
//                    String valueObj = JSONHelper.getStringFromObject(item.getResult());
//
//                    RoomInfo info = JSONHelper.getObject(valueObj, RoomInfo.class);
//
//                    if(info == null){
//                        dialogDismiss();
//                        ToastUtil.showToast(mContext, "房间数据错误!!");
//                        return;
//                    }
//
//                    QavsdkManger.getInstance().createRoomEnter(info.getRoomid(), DongtaiSquareAdapter.this);
//                }
//            });
//        } catch (Exception e) {
//            Log.w("DongTaiMainAdapter", e);
//            ToastUtil.showToast(mContext, "直播链接类型发生变化了");
//        }
//    }

    private void showDialog(String message){
        if(pressageDialog == null)
            pressageDialog = new STProgressDialog(mContext);

        if(pressageDialog.isShowing())
            pressageDialog.dismiss();
        else {
            pressageDialog.setMsgText(message);
            pressageDialog.show();
        }
    }

    private void dialogDismiss(){
        if(pressageDialog != null)
            pressageDialog.dismiss();
    }

//    public void setPraise(int position, boolean isPraise) {
//        isPraised.set(position, isPraise);
//        if (isPraised.get(position)) {
//            isPraised.set(position, false);
//        } else {
//            isPraised.set(position, true);
//        }
//        notifyDataSetChanged();
//    }

    OnStatesesListener onStatesesListener;

    public void setOnStatesesListener(OnStatesesListener onStatesesListener) {
        this.onStatesesListener = onStatesesListener;
    }

    public interface OnStatesesListener {
        void doForward(Statuses statuses, int position);// 转发

        void doRemark(int position, Statuses statuses, String content);// 评论

        void doPraise(long id, int position);

        void doCollect(long id, int position, int isCollected);// 收藏

        void enterDongtaiDetail(int position, Statuses statuses);

        void enterTribe(Statuses statuses);
    }
    @Override
    public void closeProgress() {
        dialogDismiss();
    }

    @Override
    public void startSdkSuccess() {

    }

    public void onDestory(){
        QavsdkManger.getInstance().unregisterCreateRoom(mContext,flag);
    }
}
