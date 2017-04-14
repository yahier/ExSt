package yahier.exst.adapter;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import com.stbl.stbl.dialog.InputDialog;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.widget.ImageGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 此页面 暂时完全复制DongtaiMainAdapter
 *
 * @author lenovo
 */
public class MineDongtaiMainAdapter extends CommonAdapter implements OnClickListener, FinalHttpCallback {
    Activity mContext;
    MyApplication app;
    List<Statuses> list;
    public final static int SCROLL_STATE_IDLE = 0;
    public final static int SCROLL_STATE_TOUCH_SCROLL = 1;
    private int defaultScrollState = SCROLL_STATE_IDLE;
    public final static String key = "item";
    private int invisibleWidth;// 被隐藏区域的宽度
    boolean isMine = false;
    // List<Boolean> isPraised;
    private LinearLayout.LayoutParams paramsLong, paramsShort;

    /**
     * 动态主页grid一栏的数目
     */

    public void setState(int scrollState) {
        this.defaultScrollState = scrollState;
        notifyDataSetChanged();
    }

    public MineDongtaiMainAdapter(Activity mContext, boolean isMine) {
        this.mContext = mContext;
        this.isMine = isMine;
        //isPraised = new ArrayList<Boolean>();
        app = (MyApplication) mContext.getApplication();
        list = new ArrayList<Statuses>();
        invisibleWidth = (int) Device.getDensity(mContext) * 100;
        paramsLong = new LinearLayout.LayoutParams(GVImgAdapter.gridWidth(mContext), ActionBar.LayoutParams.WRAP_CONTENT);
        paramsShort = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public MineDongtaiMainAdapter(Activity mContext, List<Statuses> list) {
        this.mContext = mContext;
        this.list = list;
        app = (MyApplication) mContext.getApplication();
        invisibleWidth = (int) Device.getDensity(mContext) * 100;
    }

    public void setData(List<Statuses> list) {
        //isPraised.clear();
        for (int i = 0; i < list.size(); i++) {
            // isPraised.add(false);
        }
        this.list = list;
        notifyDataSetChanged();
    }

    public void delete(long statusesId){
        Statuses target = null;
        for (Statuses statuses : list){
            if (statuses.getStatusesid() == statusesId){
                target = statuses;
                break;
            }
        }
        if (target != null){
            list.remove(target);
            notifyDataSetChanged();
        }
    }

    public void delete(int position) {
        this.list.remove(position);
        defaultScrollState = SCROLL_STATE_TOUCH_SCROLL;
        notifyDataSetChanged();
    }

    public void addData(List<Statuses> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
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
        // String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.weiboRemark:
                ToastUtil.showToast("评论成功");
                mInputDialog.dismiss();
                int commentCount = cStatuses.getCommentcount() + 1;
                cStatuses.setCommentcount(commentCount);
                list.set(cPosition, cStatuses);
                notifyDataSetChanged();
                break;

        }
    }

    class CityHolder {
        LinearLayout mainZone;
        GridView itemGV;
        TextView userName, tvCity, userGenderAge;
        ImageView userImg;
        TextView time;
        TextView item1, item2, item3, item4;
        ImageView itemMore;
        TextView more_item1, more_item2;

        LinearLayout link1Lin, link2Lin, link3Lin, link4Lin, link5Lin;
        ImageView link3Img;
        TextView link3Tv;

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
        //直播
        ImageView link5img;
        TextView link5tvTitle;
        TextView link5tvContent;

        TextView tvShortTitle, tvLongTitle, tvLongContent;
        View linLongSpe;
        View linLink;
        View imgAuthorized;

        ImageView imgItem3, imgItem4;
        View linItem1, linItem2, linItem3, linItem4;
    }


    //更新对应position的mStatuses
    public void update(int position, Statuses mStatuses) {
        //list.set(position, mStatuses);
    }

    public void updatePraiseText(ListView listView, int position) {
        int visibleStart = listView.getFirstVisiblePosition();// 开始显露的位置
        int po = visibleStart - position;
        if (visibleStart == 0) {
            po = po + 1;
        }
        View view = listView.getChildAt(po);
        TextView text = (TextView) view.findViewById(R.id.item_text3);
        int count = Integer.valueOf(text.getText().toString());
        text.setText(String.valueOf(count + 1));
        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_praise_presed, 0, 0, 0);
        // 更新列表对应的数据
        Statuses statuses = list.get(position);
        statuses.setIspraised(Statuses.ispraisedYes);
        statuses.setPraisecount(statuses.getPraisecount() + 1);
        list.set(position, statuses);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.mine_dongtai_list_item, null);
            ho.mainZone = (LinearLayout) con.findViewById(R.id.main_zone);
            ho.userName = (TextView) con.findViewById(R.id.name);
            ho.time = (TextView) con.findViewById(R.id.time);
            ho.tvCity = (TextView) con.findViewById(R.id.tvCity);
            ho.userGenderAge = (TextView) con.findViewById(R.id.gender_age);
            ho.userImg = (ImageView) con.findViewById(R.id.item_iv);
            ho.itemGV = (GridView) con.findViewById(R.id.item_gv);
            ho.itemMore = (ImageView) con.findViewById(R.id.dongtai_more);

            ho.more_item1 = (TextView) con.findViewById(R.id.more_item1);
            ho.more_item2 = (TextView) con.findViewById(R.id.more_item2);

            ho.item1 = (TextView) con.findViewById(R.id.item_text1);
            ho.item2 = (TextView) con.findViewById(R.id.item_text2);
            ho.item3 = (TextView) con.findViewById(R.id.item_text3);
            ho.item4 = (TextView) con.findViewById(R.id.item_text4);


            ho.link3Lin = (LinearLayout) con.findViewById(R.id.link3);
            ho.link3Img = (ImageView) con.findViewById(R.id.link3_img);
            ho.link3Tv = (TextView) con.findViewById(R.id.link3_content);
            ho.link1Lin = (LinearLayout) con.findViewById(R.id.link1);
            ho.link2Lin = (LinearLayout) con.findViewById(R.id.link2);
            ho.link4Lin = (LinearLayout) con.findViewById(R.id.link4);
            ho.link5Lin = (LinearLayout) con.findViewById(R.id.link5);
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
            //直播
            ho.link5img = (ImageView) con.findViewById(R.id.link5_img);
            ho.link5tvTitle = (TextView) con.findViewById(R.id.tv_user_live);
            ho.link5tvContent = (TextView) con.findViewById(R.id.link5_content);

            ho.tvShortTitle = (TextView) con.findViewById(R.id.tvShortTitle);
            ho.tvLongTitle = (TextView) con.findViewById(R.id.tvLongTitle);
            ho.tvLongContent = (TextView) con.findViewById(R.id.tvLongContent);
            ho.linLongSpe = con.findViewById(R.id.linLongSpe);
            ho.linLink = con.findViewById(R.id.linLink);
            ho.imgAuthorized = con.findViewById(R.id.imgAuthorized);

            ho.linItem1 = con.findViewById(R.id.linItem1);
            ho.linItem2 = con.findViewById(R.id.linItem2);

            ho.imgItem3 = (ImageView) con.findViewById(R.id.imgItem3);
            ho.linItem3 = con.findViewById(R.id.linItem3);

            ho.imgItem4 = (ImageView) con.findViewById(R.id.imgItem4);
            ho.linItem4 = con.findViewById(R.id.linItem4);
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
        // 如果是长微博。就需要放大
        if (pics != null && statuses.getStatusestype() == Statuses.type_long) {
            numClumn = 1;
        } else {
            numClumn = GVImgAdapter.gridColumn;
        }
        ho.itemGV.setNumColumns(numClumn);
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
                if (pics == null || pics.getPics().size() == 0) {
                    ho.itemGV.setVisibility(View.GONE);
                } else if (pics.getPics().size() == 4) {
                    gridAdapter.setType(GVImgAdapter.typeShort4);
                } else {
                    gridAdapter.setType(GVImgAdapter.typeShort);
                }
                ho.itemGV.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.statuses_right_padding), 0);
                ho.linLongSpe.setBackgroundResource(R.color.transparent);
                ho.linLongSpe.setLayoutParams(paramsShort);
                break;
            case Statuses.type_long:
                ho.tvShortTitle.setVisibility(View.GONE);
                if (statuses.getContent() == null)
                    break;
                String pureContent = statuses.getContent().replace(Config.longWeiboFillMark, "");
                String title = statuses.getTitle();
                pureContent = pureContent.replace("\n", "");
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
                gridAdapter.setType(GVImgAdapter.typeLong);
                ho.itemGV.setVisibility(View.VISIBLE);
                ho.itemGV.setPadding(0, 0, 0, 0);
                ho.linLongSpe.setBackgroundResource(R.color.theme_bg);
                ho.linLongSpe.setLayoutParams(paramsLong);
                break;
        }
        //不再显示长动态的内容
        ho.tvLongContent.setVisibility(View.GONE);
        // 图片
        ho.linLink.setLayoutParams(paramsLong);
        ho.itemGV.setAdapter(gridAdapter);
        ViewUtils.setAdapterViewHeight(ho.itemGV, GVImgAdapter.gridColumn);
        // 判断是否显示
        if (pics == null || pics.getPics().size() == 0) {
            ho.itemGV.setVisibility(View.GONE);
        } else {
            ho.itemGV.setVisibility(View.VISIBLE);
        }
        // 最上一层
        ho.time.setText(DateUtil.getTimeOff(statuses.getCreatetime()));
        ho.item1.setText(statuses.getForwardcount() + "");
        ho.item2.setText(statuses.getCommentcount() + "");
        ho.item3.setText(statuses.getPraisecount() + "");
        ho.item4.setText(statuses.getFavorcount() + "");
        if (statuses.isIstop() == Statuses.istopYes) {
            ho.more_item1.setText("取消置顶");
        } else {
            ho.more_item1.setText("置顶");
        }

        if (statuses.getIspraised() == Statuses.ispraisedYes) {
            //ho.item3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_praise_presed, 0, 0, 0);
            ho.imgItem3.setImageResource(R.drawable.dongtai_praise_presed);
            ho.item3.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
            // isPraised.set(i, true);
        } else {
            // ho.item3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_item3, 0, 0, 0);
            ho.imgItem3.setImageResource(R.drawable.dongtai_item3);
            ho.item3.setTextColor(mContext.getResources().getColor(R.color.black7));
        }

        if (statuses.getIsfavorited() == Statuses.isfavoritedYes) {
            //ho.item4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_collect_pressed, 0, 0, 0);
            ho.imgItem4.setImageResource(R.drawable.dongtai_collect_pressed);
            ho.item4.setTextColor(mContext.getResources().getColor(R.color.theme_brown));
        } else {
            //ho.item4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_item4, 0, 0, 0);
            ho.imgItem4.setImageResource(R.drawable.dongtai_item4);
            ho.item4.setTextColor(mContext.getResources().getColor(R.color.black7));
        }

        setTag(statuses, ho.itemMore, ho.more_item1, ho.more_item2, ho.item1, ho.item2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4, ho.link3Lin);
        setPositionTag(i, ho.itemMore, ho.more_item2, ho.item1, ho.item2, ho.linItem1, ho.linItem2, ho.linItem3, ho.linItem4);
        ho.more_item1.setOnClickListener(this);
        ho.more_item2.setOnClickListener(this);
        ho.linItem1.setOnClickListener(this);
        ho.linItem2.setOnClickListener(this);
        ho.linItem3.setOnClickListener(this);
        ho.linItem4.setOnClickListener(this);
        // 图片列表图像
        // 得到用户列表
        UserItem user = statuses.getUser();
        ho.userName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//        ho.userName.setText(user.getNickname());
        ho.tvCity.setText(user.getCityname());
        if (user.getCertification() == UserItem.certificationYes) {
            ho.imgAuthorized.setVisibility(View.VISIBLE);
        } else {
            ho.imgAuthorized.setVisibility(View.GONE);
        }
        ho.userGenderAge.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_boy_bg);
        } else if (user.getGender() == UserItem.gender_girl) {
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_girl_bg);
        } else {
            ho.userGenderAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ho.userGenderAge.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
        }
        PicassoUtil.load(mContext, user.getImgurl(), ho.userImg);
//        if (isMine) {
//            ho.itemMore.setVisibility(View.VISIBLE);
//        } else {
//            ho.itemMore.setVisibility(View.GONE);
//        }
        final CityHolder newHo = ho;
        // 点击右边，向左滑动此item
        ho.itemMore.setOnClickListener(this);
        // 进入详细
        con.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.logE("触动点击");
                Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra(key, statuses);
                mContext.startActivity(intent);
            }
        });
        // 如果在滑动。将向左滑动的还原
        if (defaultScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            doSmoomthToDefault(ho.mainZone);
        }

        // 监听con的左右滑动
        con.setOnTouchListener(new OnTouchListener() {
            float down1 = 0;
            float down2 = 0;
            final int leastmoving = 20;
            boolean isHandled = false;
            int oriatation;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (true)
                    return false;

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        down1 = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        down2 = event.getX();
                        if (down1 != 0) {
                            if (down1 > down2 + leastmoving) {
                                // LogUtil.logE("向左滑动");
                                oriatation = 1;
                                isHandled = true;
                            } else if (down1 < down2 - leastmoving) {
                                // LogUtil.logE("向右滑动");
                                oriatation = 2;
                                isHandled = true;
                            } else {
                                isHandled = false;
                            }
                        } else {
                            down1 = event.getY();
                        }
                        if (isHandled == true) {
                            if (oriatation == 1) {
                                doSmoomth(newHo.mainZone, 1, down2);
                            } else {
                                // doSmoomthToDefault(newHo.mainZone);
                                doSmoomth(newHo.mainZone, 2, down2);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        down1 = down2 = 0;
                        // LogUtil.logE("up:" + isHandled);
                        // if (isHandled == true) {
                        // if (oriatation == 1) {
                        // doSmoomth(newHo.mainZone, 1);
                        // } else {
                        // // doSmoomthToDefault(newHo.mainZone);
                        // doSmoomth(newHo.mainZone, 2);
                        // }
                        // }
                        break;
                }

                return isHandled;
            }
        });

        // 这里来处理链接的内容啦
        LinkStatuses link = statuses.getLinks();
        if (link != null) {
            ho.linLink.setVisibility(View.VISIBLE);
            int linkType = link.getLinktype();
            ho.link5Lin.setVisibility(View.GONE);
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
                    } else if (linkUser.getGender() == UserItem.gender_girl) {
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
                    String content = linkStatuses.getContent().replace(Config.longWeiboFillMark, "");
                    // 好奇怪啊 。怎么没显示动态的值呢。打印都有显示呢
                    ho.link3Tv.setText(content);
                    StatusesPic link3Pics = linkStatuses.getStatusespic();
                    if (link3Pics.getPics().size() > 0) {
                        String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
                        PicassoUtil.loadStatuses(mContext, imgUrl, ho.link3Img);
                    } else {
                        //ho.link3Img.setImageResource(R.drawable.dongtai_default);
                        PicassoUtil.loadStatuses(mContext, link3Pics.getEx(), ho.link3Img);
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
                    break;
            }

        } else {
            ho.linLink.setVisibility(View.GONE);
        }
        return con;
    }

    public void doSmoomth(View view, int oriatation, float distance) {
        float translation = view.getTranslationX();
        // 如果向左
        if (oriatation == 1) {
            distance = -distance;
            //LogUtil.logE("doSmooth 向左滑动:" + translation + ":" + distance);
            ObjectAnimator.ofFloat(view, "translationX", 0, distance).setDuration(50).start();
            // 向右
        } else if (oriatation == 2) {
            //LogUtil.logE("doSmooth 向右滑动");
            ObjectAnimator.ofFloat(view, "translationX", translation, distance).setDuration(200).start();
        }
    }


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
                    cStatuses = (Statuses) view.getTag();
                    intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", cStatuses.getStatusesid());
                    mContext.startActivity(intent);
                    break;
                case R.id.link4:
                    Goods goods = (Goods) view.getTag();
                    intent = new Intent(mContext, MallGoodsDetailAct.class);
                    intent.putExtra("goodsid", goods.getGoodsid());
                    mContext.startActivity(intent);
                    break;

            }
        }
    };

    /**
     * 1是向左，2是向右
     *
     * @param view
     * @param oriatation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void doSmoomth(View view, int oriatation) {
        System.out.println("x:" + view.getTranslationX());
        if (view.getTranslationX() == 0 && oriatation == 1) {
            LogUtil.logE("if");
            ObjectAnimator.ofFloat(view, "translationX", 0, -invisibleWidth).setDuration(200).start();
        } else if (view.getTranslationX() < 0 && oriatation == 2) {
            LogUtil.logE("else");
            ObjectAnimator.ofFloat(view, "translationX", -invisibleWidth, 0).setDuration(200).start();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void doSmoomth(View view) {
        System.out.println("x:" + view.getTranslationX());
        if (view.getTranslationX() < 0)
            ObjectAnimator.ofFloat(view, "translationX", -invisibleWidth, 0).setDuration(200).start();
        else
            ObjectAnimator.ofFloat(view, "translationX", 0, -invisibleWidth).setDuration(200).start();
    }

    public void doSmoomthToDefault(View view) {
        ObjectAnimator.ofFloat(view, "translationX", 0, 0).setDuration(200).start();
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

    Statuses cStatuses;
    int cPosition;

    @Override
    public void onClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        },Config.interClickTime);
        cStatuses = (Statuses) view.getTag();
        switch (view.getId()) {
            case R.id.more_item1:
                onStatesesListener.doSetTop(cStatuses.getStatusesid(), cStatuses.isIstop());
                break;
            case R.id.more_item2:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doDelete(cStatuses.getStatusesid(), cPosition);
                break;
            case R.id.linItem1:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doForward(cStatuses, cPosition);
                break;
            case R.id.linItem2:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                if (cStatuses.getCommentcount() == 0) {
                    showInputDialog();
                } else {
                    onStatesesListener.enterDongtaiDetail(cPosition, cStatuses, true);
                }
                break;
            case R.id.linItem3:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doPraise(cStatuses, cPosition);
                break;
            case R.id.linItem4:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doCollect(cStatuses, cPosition, cStatuses.getIsfavorited());

                break;
            case R.id.dongtai_more:
                cPosition = (int) view.getTag(R.id.tag_dongtai_item);
                onStatesesListener.doDelete(cStatuses.getStatusesid(), cPosition);
                break;
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
                    ToastUtil.showToast(mContext, "请先输入");
                    return;
                }

                if (content.length() > Config.remarkContentLength) {
                    ToastUtil.showToast(mContext, "内容过长，最多" + Config.remarkContentLength + "个文字");
                    return;
                }
                mInputDialog.setSendButtonEnable(false);
                Params params = new Params();
                params.put("statusesid", cStatuses.getStatusesid());
                params.put("content", content);
                LogUtil.logE("content:" + content);
                new HttpEntity(mContext).commonPostData(Method.weiboRemark, params, MineDongtaiMainAdapter.this);
            }
        });
    }

    OnStatesesListener onStatesesListener;

    public void setOnStatesesListener(OnStatesesListener onStatesesListener) {
        this.onStatesesListener = onStatesesListener;
    }

    public interface OnStatesesListener {
        // 置顶
        void doSetTop(long statusesid, int isTop);

        // 删除
        void doDelete(long statusesid, int position);

        void doForward(Statuses statuses, int position);// 转发

        void doRemark(int position, Statuses statuses, String content);// 评论

        void doPraise(Statuses statuses, int position);

        void doCollect(Statuses statuses, int position, int isCollected);// 收藏

        void enterDongtaiDetail(int position, Statuses statuses, boolean scrollToPosition);
    }

}
