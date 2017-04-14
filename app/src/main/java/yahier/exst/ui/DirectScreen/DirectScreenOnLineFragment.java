package yahier.exst.ui.DirectScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ReportStatusesOrUserAct;
import com.stbl.stbl.api.data.GiftAnimInfo;
import com.stbl.stbl.api.data.LiveRoomCreateInfo;
import com.stbl.stbl.api.data.MemberInfo;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.ui.BaseClass.fragment.STBLBaseFragment;
import com.stbl.stbl.ui.DirectScreen.widget.AssembleAudioDataApi;
import com.stbl.stbl.ui.ItemAdapter.DirectScreenOnLineAdapter;
import com.stbl.stbl.ui.DirectScreen.dialog.GiftAnimDialog;
import com.stbl.stbl.ui.DirectScreen.dialog.InviteTipsDialog;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SwipeXListView;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.stbl.stbl.widget.swipe.SwipeMenu;
import com.stbl.stbl.widget.swipe.SwipeMenuCreator;
import com.stbl.stbl.widget.swipe.SwipeMenuItem;
import com.stbl.stbl.widget.swipe.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/3/7.
 *
 * 直播在线用户
 */
public class DirectScreenOnLineFragment extends STBLBaseFragment implements SwipeXListView.OnXListViewListener{
    private SwipeXListView listView;
//    private SwipeMenuListView listView;
    private Activity mActivity;
    private DirectScreenOnLineAdapter adapter;
    private static final int PAGE_COUNT = 15;
    private int pageCount = 1;
    private long lastPostTime;
    private List<MemberInfo> memberList = new ArrayList<>();
    private int freshMode = 0;
    private static final int ON_REFRESH = 1; //下拉
    private int roomId;
    protected STProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direct_screen_on_line, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        listView = (SwipeXListView) mActivity.findViewById(R.id.lv_smlv);
//        listView = (SwipeMenuListView) mActivity.findViewById(R.id.lv_smlv);
        TextView textView = (TextView) mActivity.findViewById(R.id.tv_empty);
        listView.setEmptyView(textView);
        Bundle bundle = getArguments();
        roomId = bundle.getInt("roomId", 0);
        adapter = new DirectScreenOnLineAdapter(mActivity);
        listView.setAdapter(adapter);
        listView.setOnXListViewListener(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomId != 0 && lastPostTime < System.currentTimeMillis() - (5*1000)) {
                    showDialog("正在加载成员列表...");
                    freshMode = ON_REFRESH;
                    getRoomMember(roomId);
                }else{
                    ToastUtil.showToast(mActivity,"请稍后...");
                }
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                MemberInfo memberInfo = (MemberInfo) adapter.getItem(position);
                String userid = SharedToken.getUserId(mActivity);
                if (memberInfo == null) return;
                if (userid != null && userid.equals(String.valueOf(memberInfo.getMemberid()))) {
                    ToastUtil.showToast(mActivity, "亲，这是你自己哦");
                    return;
                }
                if (1 == menu.getMenuItem(index).getId()) { //举报
                    if (memberInfo != null) {
                        jumpToAct(memberInfo.getMemberid());
                    }
                } else if (2 == menu.getMenuItem(index).getId()) { //踢出
                    if (userid != null && userid.equals(String.valueOf(adapter.getHomeOwner()))) {
                        kickMember(memberInfo);
                    } else {
                        ToastUtil.showToast(mActivity, "你不是房主，不能踢出");
                    }
                }
            }
        });
    }

    private void createMenu(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
//                if (menu.getViewType() == 1) return;
                SwipeMenuItem reportItem = new SwipeMenuItem(mActivity);
                reportItem.setId(1);
                reportItem.setBackground(R.color.gray2);
                reportItem.setWidth((int) mActivity.getResources().getDimension(R.dimen.cart_delete_width));
                reportItem.setTitle("举报");
                reportItem.setTitleSize(16);
                reportItem.setTitleColor(mActivity.getResources().getColor(R.color.white));
                menu.addMenuItem(reportItem);

                String userid = ((MyApplication)mActivity.getApplication()).getUserId();
                if (userid != null && userid.equals(String.valueOf(adapter.getHomeOwner()))) {
                    SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
                    deleteItem.setId(2);
                    deleteItem.setBackground(R.color.theme_red_bg);
                    deleteItem.setWidth((int) mActivity.getResources().getDimension(R.dimen.cart_delete_width));
                    deleteItem.setTitle("踢出");
                    deleteItem.setTitleSize(16);
                    deleteItem.setTitleColor(mActivity.getResources().getColor(R.color.white));
                    menu.addMenuItem(deleteItem);
                }

            }
        };
        listView.setMenuCreator(creator);
    }
    private void showDialog(String message){
        if(dialog == null)
            dialog = new STProgressDialog(mActivity);

        if(dialog.isShowing())
            dialog.dismiss();
        else {
            dialog.setMsgText(message);
            dialog.show();
        }
    }

    private void dissmissDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    //踢出某个用户
    private void kickMember(final MemberInfo info){
        showDialog("正在踢出...");
        Params params = new Params();
        params.put("roomid",roomId);
        params.put("touserid", info.getMemberid());
        new HttpEntity(mActivity).commonPostData(Method.imLiveOwnerKick, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {
                dissmissDialog();
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() != BaseItem.successTag) {
                    ToastUtil.showToast(getActivity(), item.getErr().getMsg());
                    return;
                }

                AssembleAudioDataApi.getInstance().sendShotRoomMsg(info.getMemberid(), info.getNickname());
                if (adapter != null) adapter.deleteItem(info.getMemberid());
            }
        });
    }

    private void jumpToAct(long userid){
        Intent intent = new Intent(mActivity, ReportStatusesOrUserAct.class);
        intent.putExtra("type",4);
        intent.putExtra("referenceid",userid);
        mActivity.startActivity(intent);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (listView != null)
            listView.getParent().requestDisallowInterceptTouchEvent(isVisibleToUser);
        //30秒内只请求一次，防止重复切屏
        if(lastPostTime < System.currentTimeMillis() - (10*1000) && isVisibleToUser){
            if(roomId != 0) {
                freshMode = ON_REFRESH;
                getRoomMember(roomId);
            }
        }
    }

    private void getRoomMember(long roomId){
        lastPostTime = System.currentTimeMillis();
        Params params = new Params();
        params.put("roomid",roomId);
        params.put("searchtype", 0);//在线直播
        params.put("page", pageCount);
        params.put("count", PAGE_COUNT);
        LogUtil.logD("LogUtil", "roomId:" + roomId );
        new HttpEntity(mActivity).commonPostData(Method.liveRoomGetMember,params,this);
    }
    @Override
    public int getNavigationResId() {
        return 0;
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {

    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        LogUtil.logD("LogUtil","methodName:"+methodName+"--result:"+result);
        listView.onLoadMoreComplete();
        listView.onRefreshComplete();
        dissmissDialog();
        switch (methodName){
            case Method.liveRoomGetMember:
                if (valueObj != null){
                  List<MemberInfo>  dataList =  JSONHelper.getList(valueObj, MemberInfo.class);
                    if (dataList == null || dataList.size() <= 0){
                        if (freshMode == ON_REFRESH){
                            memberList.clear();
                            adapter.setData(memberList);
                        }
                    }
                    if (freshMode == ON_REFRESH){
                        memberList.clear();
                    }
                    memberList.addAll(dataList);
                    adapter.setData(memberList);
                }
                createMenu();
                break;
        }
    }

    @Override
    public void onRefresh(SwipeXListView v) {
        freshMode = ON_REFRESH;
        if(roomId != 0) {
            pageCount = 1;
            getRoomMember(roomId);
        }
    }

    @Override
    public void onLoadMore(SwipeXListView v) {
        freshMode = 0;
        if (roomId != 0){
            pageCount += 1;
            getRoomMember(roomId);
        }
    }
}
