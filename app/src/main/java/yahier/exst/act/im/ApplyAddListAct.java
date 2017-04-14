package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.rong.DemoCommandNotificationMessage;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.ApplyAgreeUser;
import com.stbl.stbl.item.im.ApplyGotItem;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.utils.SpannableUtils;
import com.stbl.stbl.widget.EmptyView;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.InformationNotificationMessage;

/**
 * 收到的申请列表
 *
 * @author lenovo
 */
public class ApplyAddListAct extends ThemeActivity implements FinalHttpCallback {

    XListView listView;
    AppAdapter adapter;
    EmptyView emptyView;
    int page = 1;
    final int count = 15;
    boolean isRefresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_apply_list);
        setLabel(R.string.im_new_friend);
        listView = (XListView) findViewById(R.id.list);
        emptyView = (EmptyView) findViewById(R.id.empty_view);
        adapter = new AppAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                isRefresh = true;
                listView.setPullLoadEnable(true);
                page = 1;
                getData();
            }

            @Override
            public void onLoadMore(XListView v) {
                page += 1;
                isRefresh = false;
                getData();
            }
        });
        EventBus.getDefault().post(new IMEventType(IMEventType.typeIMViewedApply));
        getData();
    }

    int currentchildPosition = 0;

    private void deleteApplyAdd(int position) {
        currentchildPosition = position;
        ApplyGotItem apply = adapter.get(position);
        deleteRecord(apply.getApplyid());
    }

    // 删除申请记录
    void deleteRecord(long applyid) {
        Params params = new Params();
        params.put("applyid", applyid);
        new HttpEntity(this).commonPostData(Method.imDeleteapply, params, this);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    void getData() {
        Params params = new Params();
        params.put("count", count);
        params.put("page", page);
        new HttpEntity(this).commonPostData(Method.applyListV1, params, this);
    }

    class AppAdapter extends BaseSwipeAdapter {
        private Context mContext;
        List<ApplyGotItem> list;
        LayoutInflater mInflater;

        public AppAdapter(Context mContext) {
            this.mContext = mContext;
            mInflater = LayoutInflater.from(mContext);
            this.list = new ArrayList<ApplyGotItem>();
        }

        public void setData(List<ApplyGotItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public ApplyGotItem get(int position) {
            return list.get(position);
        }

        public void addData(List<ApplyGotItem> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void delete(int position) {
            list.remove(position);
            notifyDataSetChanged();
        }


        public void handleReceive(int position) {
            ApplyGotItem child = list.get(position);
            child.setApplystate(ApplyGotItem.applystateReceived);
            notifyDataSetChanged();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe_layout;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            View convertView = mInflater.inflate(R.layout.apply_got_list_item, null);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void fillValues(final int position, View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.swipeLayout.close(false);
            final ApplyGotItem item = list.get(position);
            final ApplyGotItem.FromUser user = item.getFromuser();
            if (user == null) return;
            ImageUtils.loadHead(user.getImgmiddleurl(), holder.iv_icon);
            holder.tv_name.setText(user.getNickname());

            if (user.getCbigchiefinfo() != null) {
                holder.ivUserBigChiefIcon.setVisibility(View.VISIBLE);
                PicassoUtil.load(mContext, user.getCbigchiefinfo().getImgurl(), holder.ivUserBigChiefIcon);
            } else {
                holder.ivUserBigChiefIcon.setVisibility(View.GONE);
            }

            holder.tvFamilyCount.setText(String.format(mContext.getString(R.string.im_family_count), user.getFamilycount()));
            holder.tvTudiCount.setText(String.format(mContext.getString(R.string.im_tudi_count), user.getTudicount()));

            if (user.getBigchiefinfo() != null) {
                holder.tvLastBigChiefName.setTextColor(getResources().getColor(R.color.theme_yellow));
                holder.ivLastBigChiefIcon.setVisibility(View.VISIBLE);
                holder.tvLastBigChiefName.setText(user.getBigchiefinfo().getNickname());
                PicassoUtil.load(mContext, user.getBigchiefinfo().getImgurl(), holder.ivLastBigChiefIcon);
            } else {
                holder.tvLastBigChiefName.setTextColor(getResources().getColor(R.color.gray_a5));
                holder.ivLastBigChiefIcon.setVisibility(View.GONE);
                holder.tvLastBigChiefName.setText(R.string.im_no_have);
            }

            switch(item.getApplystate()) {
                case ApplyGotItem.applystateDefault:
                    holder.tvReceived.setVisibility(View.GONE);
                    holder.tvReceive.setVisibility(View.VISIBLE);
                    holder.tvReceive.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            currentchildPosition = position;
                            doReceive(item.getApplyid());
                        }
                    });
                    break;
                case ApplyGotItem.applystateReceived:
                    holder.tvReceived.setVisibility(View.VISIBLE);
                    holder.tvReceive.setVisibility(View.GONE);
                    holder.tvReceived.setText(R.string.accepted);
                    break;
                case ApplyGotItem.applystateRejected:
                    holder.tvReceived.setVisibility(View.VISIBLE);
                    holder.tvReceive.setVisibility(View.GONE);
                    holder.tvReceived.setText(R.string.denied);
                    break;
            }

            holder.itemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", user.getUserid());
                    mContext.startActivity(intent);
                }
            });
            String checkStr = getString(R.string.im_check_info);
            SpannableString ss = SpannableUtils.formatSpannable(checkStr + item.getApplymsg(), checkStr.length(), (checkStr + item.getApplymsg()).length(), getResources().getColor(R.color.black), 0);
            holder.tvApplyMsg.setText(ss);

            holder.llDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteApplyAdd(position);
                }
            });
        }

        class ViewHolder {
            ImageView iv_icon;//申请人头像
            TextView tv_name;//申请人昵称
            ImageView ivUserBigChiefIcon;//申请人大酋长图标
            TextView tvFamilyCount;//申请人家族人数
            TextView tvTudiCount; //申请人徒弟数
            TextView tvLastBigChiefName;//最近大酋长昵称
            ImageView ivLastBigChiefIcon; //最近大酋长图标
            TextView tvApplyMsg;//申请信息
            TextView tvReceived;//已接受
            TextView tvReceive;//接受
            LinearLayout llDelete;//删除申请
            RelativeLayout itemLayout;
            SwipeLayout swipeLayout;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.img);
                tv_name = (TextView) view.findViewById(R.id.tvName);
                ivUserBigChiefIcon = (ImageView) view.findViewById(R.id.iv_user_bigchief_icon);
                tvFamilyCount = (TextView) view.findViewById(R.id.tv_family_count);
                tvTudiCount = (TextView) view.findViewById(R.id.tv_tudi_count);
                tvLastBigChiefName = (TextView) view.findViewById(R.id.tv_bigchief_name);
                ivLastBigChiefIcon = (ImageView) view.findViewById(R.id.iv_bigchief_icon);
                tvApplyMsg = (TextView) view.findViewById(R.id.tvApplyMsg);
                tvReceived = (TextView) view.findViewById(R.id.tvReceived);
                tvReceive = (TextView) view.findViewById(R.id.tvReceive);
                llDelete = (LinearLayout) view.findViewById(R.id.ll_delete);
                itemLayout = (RelativeLayout) view.findViewById(R.id.layout_item);
                swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_layout);
                swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom2"));
            }
        }

    }

    final int handleReceive = 1;

    /**
     * 处理请求 handleresult 1同意，2拒绝.当前直接同意
     *
     * @param appyid
     * @param
     */
    void doReceive(long appyid) {
        WaitingDialog.showTouchNotCancel(this, getString(R.string.please_wait));
        Params params = new Params();
        params.put("applyid", appyid);
        params.put("handleresult", handleReceive);
        new HttpEntity(this).commonPostData(Method.handleApply, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch(methodName) {
                case Method.handleApply:
                    WaitingDialog.dismiss();
                    break;
                case Method.applyList:
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    emptyView.hide();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.applyListV1:
                List<ApplyGotItem> list = JSONHelper.getList(obj, ApplyGotItem.class);
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();

                listView.setVisibility(View.VISIBLE);
                emptyView.hide();
                LogUtil.logE("size5", list.size());
                if (isRefresh) {
                    if (list == null || list.size() == 0) {
                        listView.setVisibility(View.GONE);
                        emptyView.showEmpty();
                        return;
                    }
                    adapter.setData(list);
                } else {
                    adapter.addData(list);
                }

                if (list.size() < count) {
                    listView.EndLoad();
                }

                break;
            case Method.handleApply:
                WaitingDialog.dismiss();
                EventBus.getDefault().post(new IMEventType(IMEventType.typeUpdateContactList));
                EventBus.getDefault().post(new IMEventType(IMEventType.typeChangedContact));
                adapter.handleReceive(currentchildPosition);
                ToastUtil.showToast(R.string.deal_success);
                ApplyGotItem item1 = adapter.get(currentchildPosition);
                ApplyGotItem.FromUser user = item1.getFromuser();
                //FriendsDB.getInstance(this).insert(user);
                //发送命令消息，通知对方
                sendNoti(user);
                break;
            case Method.imDeleteapply:
                ToastUtil.showToast(R.string.delete_success);
                adapter.delete(currentchildPosition);
                if (adapter.getCount() == 0) {
                    emptyView.showEmpty();
                }
                break;
        }

    }


    void sendNoti(ApplyGotItem.FromUser item) {
        test1(item);
        ApplyAgreeUser applyUser = new ApplyAgreeUser();
        applyUser.setTargetid(item.getUserid());
        applyUser.setTargetname(item.getNickname());
        applyUser.setUserid(Long.valueOf(SharedToken.getUserId()));
        UserItem user = SharedUser.getUserItem();
        applyUser.setNickname(user.getNickname());
        applyUser.setImgurl(user.getImgmiddleurl());
        String data = JSON.toJSONString(applyUser);//对象转成String
        LogUtil.logE("ApplyAddListAct data", data);
        DemoCommandNotificationMessage me = DemoCommandNotificationMessage.obtain("add", data);
        RongIM.getInstance()
                .getRongIMClient()
                .sendMessage(Conversation.ConversationType.PRIVATE,
                        "" + item.getUserid(), me, null, null,
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

    //测试不打开窗口，但是给窗口发送一条通知信息
    void test1(ApplyGotItem.FromUser item) {
        String tipContent = String.format(getString(R.string.you_have_benn_friends_with_someone_chat_now), item.getNickname());
        RongIM.getInstance().getRongIMClient().insertMessage(Conversation.ConversationType.PRIVATE, item.getUserid() + "", SharedToken.getUserId(), InformationNotificationMessage.obtain(tipContent), null);
    }
}
