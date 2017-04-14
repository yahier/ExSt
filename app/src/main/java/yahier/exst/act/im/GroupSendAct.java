package yahier.exst.act.im;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.AddLinkWishAct;
import com.stbl.stbl.act.dongtai.DongtaiAddBusinessCardAct;
import com.stbl.stbl.act.dongtai.EventStatusesType;
import com.stbl.stbl.act.im.rong.BusinessCardMessage;
import com.stbl.stbl.act.im.rong.GoodsMessage;
import com.stbl.stbl.act.im.rong.StatusesMessage;
import com.stbl.stbl.act.mine.MyCollectionActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.NetUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ReportUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

public class GroupSendAct extends ThemeActivity implements OnClickListener {

    Context mContext;
    int maxUserSize = 4;
    Dialog pop;
    int linkType;
    String linkId;
    EditText inputMessage;
    List<UserItem> listUser = new ArrayList<UserItem>();
    String content;
    final int requestChooseFriendCode = 101;

    private LinearLayout mFriendLayout;
    private TextView mCountTv;
    private TextView mUnselectTv;

    private LinearLayout mAddLinkLayout;
    final int maxSelected = 200;
    int sizeTypeSent;
    int intevalTime;
    final int maxWordSize = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_send_act);

        setLabel(R.string.send_mass_message);
        EventBus.getDefault().register(this);
        mContext = this;

        initView();
        setListener();
    }

    private void initView() {
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        if (content != null) {
            inputMessage.setText(content);
        }

        mFriendLayout = (LinearLayout) findViewById(R.id.layout_selected_friend);
        mCountTv = (TextView) findViewById(R.id.tv_selected_count);
        mUnselectTv = (TextView) findViewById(R.id.tv_unselect);

        mAddLinkLayout = (LinearLayout) findViewById(R.id.layout_add_link);

        findViewById(R.id.tvLinkDelete1).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete2).setOnClickListener(this);
        findViewById(R.id.tvLinkDelete3).setOnClickListener(this);

    }

    private void setListener() {
        setRightText(R.string.send, new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        findViewById(R.id.layout_friend_group).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupSendAct.this,
                                SelectFriendActivity.class);
                        UserList userList = new UserList();
                        userList.setList(listUser);
                        intent.putExtra(
                                SelectFriendActivity.EXTRA_SELECTED_FRIEND_LIST,
                                userList);
                        intent.putExtra("maxSelected",maxSelected);
                        intent.putExtra(KEY.SHOW_INDEX_SELECT, true);
                        startActivityForResult(intent, requestChooseFriendCode);
                    }
                });
        mAddLinkLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showLinkWindow();
            }
        });

    }

    private void sendMessage() {
        sizeTypeSent = 0;
        String text = inputMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sizeTypeSent = sizeTypeSent + 1;
        }

        if (goodsLink != null) {
            sizeTypeSent = sizeTypeSent + 1;
        }
        if (userLink != null) {
            sizeTypeSent = sizeTypeSent + 1;
        }
        if (statusesCo != null) {
            sizeTypeSent = sizeTypeSent + 1;
        }

        if (sizeTypeSent == 0) {
            ToastUtil.showToast(R.string.please_type_or_chooseLnik);
            return;
        } else if (sizeTypeSent == 1) {
            intevalTime = 300;
        } else if (sizeTypeSent == 2) {
            intevalTime = 500;
        }


        if (listUser.size() == 0) {
            ToastUtil.showToast(R.string.has_no_choose_friend);
            return;
        }
        if (listUser.size() > maxSelected) {
            ToastUtil.showToast(getString(R.string.most_send_everyTime) + maxSelected + getString(R.string.person));
            return;
        }
        if (!NetUtil.isNetworkAvailable(MyApplication.getContext())) {
            ToastUtil.showToast(R.string.me_network_unavailable);
            return;
        }

        if(inputMessage.getText().toString().toString().length()>maxWordSize){
            ToastUtil.showToast(R.string.im_group_send_text_limit);
            return;
        }
        WaitingDialog.show(this, getString(R.string.please_wait_sending), false);
        Message message = Message.obtain();
        handler.sendMessage(message);
    }


    int index = 0;
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            LogUtil.logE("handler " + index);
            String value = String.format(getString(R.string.sending_to_d_person), String.valueOf(index + 1));
            WaitingDialog.setContent(value);
            if (index == listUser.size()) {
                ReportUtils.reportQFXX(mActivity, mUseSelectAll);//上报群发消息成功
                WaitingDialog.dismiss();
                ToastUtil.showToast(R.string.sent_completed);
                finish();
//                TipsDialog.popup(GroupSendAct.this, getString(R.string.sent_completed), getString(R.string.queding), new TipsDialog.OnTipsListener() {
//                    @Override
//                    public void onConfirm() {
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
                return true;
            }
            UserItem user = listUser.get(index);
            send(user);
            index++;
            handler.sendEmptyMessageDelayed(1, intevalTime);
            return false;
        }
    });


    /**
     * 0511修改此方法。选择某种链接后，将其它链接数据设置为null
     *
     * @param type
     */
    public void onEvent(EventStatusesType type) {
        LogUtil.logE("onEvent type:" + type.getType());
        switch (type.getType()) {
            case EventStatusesType.typeGoods:
                userLink = null;
                statusesLink = null;
                mAddLinkLayout.setVisibility(View.GONE);
                showGoodsLink(type.getGoods());
                break;
            case EventStatusesType.typeCard:
                statusesLink = null;
                goodsLink = null;
                mAddLinkLayout.setVisibility(View.GONE);
                showCardLink(type.getUser());
                break;
            case EventStatusesType.typeStatuses:
                userLink = null;
                goodsLink = null;
                mAddLinkLayout.setVisibility(View.GONE);
                showStatusesLink(type.getStatuses());
                break;
            default:
                // showToast("onEvent:default");
                break;
        }

    }

    Goods goodsLink;

    void showGoodsLink(Goods goods) {
        goodsLink = goods;
        linkType = Statuses.linkTypeGoods;
        linkId = String.valueOf(goods.getGoodsid());
        setLinkVisibility();

        findViewById(R.id.linkGoods).setVisibility(View.VISIBLE);
        ImageView imgLink = (ImageView) findViewById(R.id.link4imgLink);
        TextView tvGoodsTitle = (TextView) findViewById(R.id.link4tvGoodsTitle);
        TextView tvGoodsPrice = (TextView) findViewById(R.id.link4tvGoodsPrice);
        TextView tvGoodsSale = (TextView) findViewById(R.id.link4tvGoodsSale);
        PicassoUtil.load(mContext, goods.getImgurl(), imgLink);
        tvGoodsTitle.setText(goods.getGoodsname());
        tvGoodsPrice.setText("￥" + goods.getMinprice());
        tvGoodsSale.setText(getString(R.string.sale_count_) + goods.getSalecount());
    }

    UserItem userLink;

    void showCardLink(UserItem user) {
        userLink = user;
        linkType = Statuses.linkTypeCard;
        linkId = String.valueOf(user.getUserid());
        setLinkVisibility();
        RoundImageView imgLink = (RoundImageView) findViewById(R.id.link1imgUser);
        TextView name = (TextView) findViewById(R.id.link1name);
        TextView user_gender_age = (TextView) findViewById(R.id.link1user_gender_age);
        TextView user_city = (TextView) findViewById(R.id.link1user_city);
        TextView user_signature = (TextView) findViewById(R.id.link1user_signature);

        name.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//        name.setText(user.getNickname());
        user_city.setText(user.getCityname());
        user_signature.setText(user.getSignature());
        user_gender_age.setText(user.getAge() + "");
        if (user.getGender() == UserItem.gender_boy) {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.dongtai_gender_boy, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
        } else if (user.getGender() == UserItem.gender_girl) {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.dongtai_gender_girl, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
        } else {
            user_gender_age.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0);
            user_gender_age.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
        }
        ImageUtils.loadCircleHead(user.getImgmiddleurl(), imgLink);
    }

    Statuses statusesLink;
    StatusesCollect statusesCo;

    void showStatusesLink(StatusesCollect statusesCo) {
        this.statusesCo = statusesCo;
        statusesLink = statusesCo.getStatuses();
        linkType = Statuses.linkTypeStatuses;
        linkId = String.valueOf(statusesLink.getStatusesid());
        setLinkVisibility();
        ImageView link3Img = (ImageView) findViewById(R.id.link3_img);
        TextView link3Content = (TextView) findViewById(R.id.link3_content);
        String contentStr = statusesLink.getTitle();
        if (contentStr == null || contentStr.equals("")) {
            contentStr = statusesLink.getContent();
        }
        link3Content.setText(contentStr);
        //显示名字
        TextView tvName = (TextView) findViewById(R.id.link3_userName);
        String name = statusesLink.getUser().getAlias() == null || statusesLink.getUser().getAlias().equals("") ? statusesLink.getUser().getNickname() : statusesLink.getUser().getAlias();
        String publisher = "<font color='#F4B10B'>" + name + "</font>"+getString(R.string.posted_statuses);
//        String publisher = "<font color='#F4B10B'>" + statusesLink.getUser().getNickname() + "</font>"+getString(R.string.posted_statuses);
        tvName.setText(Html.fromHtml(publisher));

        StatusesPic link3Pics = statusesLink.getStatusespic();
        if (link3Pics != null && link3Pics.getPics().size() > 0) {
            String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
            PicassoUtil.load(mContext, imgUrl, link3Img);
        } else {
            PicassoUtil.load(mContext, link3Pics.getEx(), link3Img);
        }

    }

    void setLinkVisibility() {
        View[] linkViews = {findViewById(R.id.linkCard),
                findViewById(R.id.linkWish), findViewById(R.id.linkStatuses),
                findViewById(R.id.linkGoods)};
        for (int i = 0; i < linkViews.length; i++) {
            linkViews[i].setVisibility(View.GONE);
        }
        if (linkType == 0)
            return;
        linkViews[linkType - 1].setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void showUsers() {
        if (listUser != null && listUser.size() > 0) {
            mFriendLayout.removeAllViews();

            mUnselectTv.setVisibility(View.GONE);
            mCountTv.setVisibility(View.VISIBLE);
            mCountTv.setText(listUser.size() + "");

            int size = Math.min(maxUserSize, listUser.size());

            int widthHeight = getResources().getDimensionPixelSize(
                    R.dimen.list_statuses_remark_head_img_width_height);
            for (int i = 0; i < size; i++) {
                RoundImageView roungImg = new RoundImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        widthHeight, widthHeight);
                params.setMargins(0, 0, 16, 0);
                PicassoUtil.load(mContext, listUser.get(i).getImgurl(),
                        roungImg);
                mFriendLayout.addView(roungImg, params);
            }
        } else {
            mUnselectTv.setVisibility(View.VISIBLE);
            mCountTv.setVisibility(View.GONE);
            mCountTv.setText("0");
            mFriendLayout.removeAllViews();
        }

    }

    public void showLinkWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            return;
        }
        pop = new Dialog(this, R.style.Common_Dialog);
        View view = getLayoutInflater()
                .inflate(R.layout.window_show_link, null);
        view.findViewById(R.id.link_card).setOnClickListener(this);
        view.findViewById(R.id.link_wish).setOnClickListener(this);
        view.findViewById(R.id.link_collect).setOnClickListener(this);
        view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        pop.dismiss();
                    }
                });
        pop.setContentView(view);
        Window window = pop.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        pop.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.link_card:
                pop.dismiss();
                enterAct(DongtaiAddBusinessCardAct.class);
                break;
            case R.id.link_wish:
                pop.dismiss();
                enterAct(AddLinkWishAct.class);
                break;
            case R.id.link_collect:
                pop.dismiss();
                Intent intent = new Intent(this,MyCollectionActivity.class);
                intent.putExtra("mode",MyCollectionActivity.mode_im_choose);
                startActivity(intent);
                break;
            case R.id.tvLinkDelete1:
                mAddLinkLayout.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
            case R.id.tvLinkDelete2:
                mAddLinkLayout.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
            case R.id.tvLinkDelete3:
                mAddLinkLayout.setVisibility(View.VISIBLE);
                linkType = 0;
                linkId = null;
                setLinkVisibility();
                break;
        }
    }



    void send(UserItem user) {
        //文字
        String text = inputMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            TextMessage message = TextMessage.obtain(text);
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, "" + user.getUserid(), message, null, null,
                    new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                            Log.e("RongRedPacketProvider", "-----onError--" + errorCode);
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Log.e("RongRedPacketProvider", "-----onSuccess--" + integer);
                        }
                    });
        }

        //名片.
        if (userLink != null) {
            BusinessCardMessage rongRedPacketMessage = BusinessCardMessage.obtain(userLink);
            RongIM.getInstance()
                    .getRongIMClient()
                    .sendMessage(Conversation.ConversationType.PRIVATE,
                            "" + user.getUserid(), rongRedPacketMessage, null,
                            null, new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer integer,
                                                    RongIMClient.ErrorCode errorCode) {
                                    Log.e("RongRedPacketProvider",
                                            "-----onError--" + errorCode);
                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Log.e("RongRedPacketProvider",
                                            "-----onSuccess--" + integer);
                                }
                            });

        }


        //动态
        if (statusesCo != null) {
            StatusesMessage messageStatuses = StatusesMessage.obtain(statusesCo);
            RongIM.getInstance().getRongIMClient()
                    .sendMessage(Conversation.ConversationType.PRIVATE,
                            "" + user.getUserid(), messageStatuses, null, null,
                            new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer integer,
                                                    RongIMClient.ErrorCode errorCode) {
                                    Log.e("RongRedPacketProvider",
                                            "-----onError--" + errorCode);
                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    Log.e("RongRedPacketProvider",
                                            "-----onSuccess--" + integer);
                                }
                            });
        }


        //商品
        if (goodsLink != null) {
            GoodsMessage messageGoods = GoodsMessage.obtain(goodsLink);
            RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE,
                    "" + user.getUserid(), messageGoods, null, null,
                    new RongIMClient.SendMessageCallback() {
                        @Override
                        public void onError(Integer integer,
                                            RongIMClient.ErrorCode errorCode) {
                            Log.e("RongRedPacketProvider",
                                    "-----onError--" + errorCode);
                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            Log.e("RongRedPacketProvider",
                                    "-----onSuccess--" + integer);
                        }
                    });
        }
    }




    /**
     * 返回好友的id数组
     *
     * @return
     */
    private long[] getUserIds() {
        int size = listUser.size();
        long[] userIds = new long[size];
        for (int i = 0; i < size; i++) {
            userIds[i] = listUser.get(i).getUserid();
        }
        return userIds;
    }

    public void enterAct(Class<?> mClass) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
    }

    private boolean mUseSelectAll;//是否使用全选功能

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestChooseFriendCode) {
            switch (resultCode) {
                case RESULT_OK:
                    if (!mUseSelectAll) {
                        mUseSelectAll = data.getBooleanExtra(KEY.USE_SELECT_ALL, false);//是否使用全选功能
                    }
                    UserList users = (UserList) data.getSerializableExtra("users");
                    listUser.clear();
                    listUser.addAll(users.getList());
                    showUsers();
                    break;

            }
            // 如果进入转发页面再返回
        }
    }
}
