package yahier.exst.act.dongtai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.adapter.DongtaiSquareAdapter;
import com.stbl.stbl.adapter.DongtaiSquareAdapter.OnStatesesListener;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Collect;
import com.stbl.stbl.item.LinkStatuses;
import com.stbl.stbl.item.Message;
import com.stbl.stbl.item.PraiseResult;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.List;

/**
 * @author lenovo
 */
@Deprecated
public class DongtaiSquare extends ThemeActivity implements OnClickListener, FinalHttpCallback, OnXListViewListener, OnStatesesListener {

    XListView listView;
    DongtaiSquareAdapter adapter;
    PopupWindow window;
    int page = 1;
    long lastid = 0;
    int samecity = 0;// 0为全部，1是全程
    final int field_all = 0;// 全部
    final int field_city = 1;// 同城
    int gender = 2;// 0全部，1女，2男
    final int gender_all = 2;
    final int gender_girl = 1;
    final int gender_boy = 0;
    int loadType = 0;// 加载模式
    final int loadTypeBottom = 0;// 底部加载
    final int loadTypeTop = 1;// 顶部加载。清除以前数据
    int position;
    final int requestDetailDongtai = 100;
    final int requestForward = 101;// 转发
    final int resultUpdateCode = 102;// 更新数据
    final int resultDeleteCode = 104;
    Context mContext;
    RadioGroup genderGroup, fieldGroup;
    //GetNewStatusesReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_square);
        setLabel("动态广场");
        mContext = this;

       // receiver = new GetNewStatusesReceiver();
       // registerReceiver(receiver, new IntentFilter(DongtaiAct.actionGetOneNewStatuses));
        initViews();
        getMainList();
    }

    void initViews() {
        setRightImage(R.drawable.dongtai_square_more, this);
        listView = (XListView) findViewById(R.id.list);
        listView.setOnXListViewListener(this);
        adapter = new DongtaiSquareAdapter(this);
        adapter.setOnStatesesListener(this);
        listView.setAdapter(adapter);
    }

    /**
     * 获取主列表数据
     */
    void getMainList() {
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("page", page);
        params.put("lastid", lastid);
        params.put("count", Statuses.requestCount);
        params.put("samecity", samecity);
        params.put("gender", gender);
        new HttpEntity(this).commonPostData(Method.weiboSquare, params, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.theme_top_banner_right:
                showWindow();
                break;
            case R.id.dialog_ok:
                window.dismiss();
                adapter.deleteAll();
                page = 1;
                getMainList();
                break;
            case R.id.dialog_cancel:
                window.dismiss();
                break;
            case R.id.linAll:
                LogUtil.logE("linAll click");
                window.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果从动态详细中返回
        if (requestCode == requestDetailDongtai) {
            switch (resultCode) {
                case resultDeleteCode:
                    long statusesId = data.getLongExtra("statusesId", 0);
                    adapter.deleteStatusesId(statusesId);
                    break;
                case resultUpdateCode:
                    Statuses statuses = (Statuses) data.getSerializableExtra("item");
                    LogUtil.logE("onActivityResult " + this.position);
                    adapter.updateItem(statuses, position);
                    break;
            }
            // 如果进入转发页面再返回
        } else if (requestCode == requestForward) {
            if (resultCode == requestForward) {
                position++;// 之所以要++,是因为转发后，会加进来一条数据
                adapter.updateForwardText(listView, position);
            }
        } else if (requestCode == DongtaiSquareAdapter.requestTribe) {
            if (resultCode == DongtaiSquareAdapter.requestTribe) {
                boolean isFollowed = data.getBooleanExtra("isFollowed", false);
                adapter.setFollowStateChanged(statuses.getUser().getUserid(), isFollowed);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
    }

    TextView tvWindowSelectedValue;

    void showWindow() {
        if (window != null) {
            LogUtil.logE("window != null");
        }

        if (window != null && window.isShowing()) {
            LogUtil.logE("window is show");
            window.dismiss();
            return;
        }
        samecity = field_all;
        gender = gender_all;
        View windowView = getLayoutInflater().inflate(R.layout.dongtai_square_window, null);
        windowView.findViewById(R.id.dialog_ok).setOnClickListener(this);
        windowView.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        windowView.findViewById(R.id.linAll).setOnClickListener(this);
        windowView.findViewById(R.id.linContent).setOnClickListener(this);
        genderGroup = (RadioGroup) windowView.findViewById(R.id.gender_group);
        fieldGroup = (RadioGroup) windowView.findViewById(R.id.field_group);
        // windowView.measure(0, 0);
        // int height = windowView.getMeasuredHeight();
        window = new PopupWindow(windowView, Device.getWidth(this), Device.getHeight(this) - Device.getStatusBasrHeight(this) - topBanner.getHeight());
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        // window.setAnimationStyle(R.style.top_popupAnimation);
        // window.setBackgroundDrawable(new ColorDrawable(-00000));
        LogUtil.logE("topBanner height:" + topBanner.getHeight());
        window.showAtLocation(windowView, Gravity.NO_GRAVITY, 0, topBanner.getHeight() + Device.getStatusBasrHeight(this));
        tvWindowSelectedValue = (TextView) windowView.findViewById(R.id.tvWindowSelectedValue);// 筛选字段

		/*
         * pop.setBackgroundDrawable(new ColorDrawable(-00000));
		 * pop.setOutsideTouchable(true); pop.setFocusable(true);
		 */

        genderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                LogUtil.logE("checkedId:" + checkedId);
                switch (checkedId) {
                    case R.id.gender_all:
                        gender = gender_all;
                        setSelextedValue();
                        break;
                    case R.id.gender_boy:
                        gender = gender_boy;
                        setSelextedValue();
                        break;
                    case R.id.gender_girl:
                        gender = gender_girl;
                        setSelextedValue();
                        break;

                }

            }
        });
        fieldGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                switch (checkedId) {
                    case R.id.field_all:
                        samecity = field_all;
                        setSelextedValue();
                        break;
                    case R.id.field_city:
                        samecity = field_city;
                        setSelextedValue();
                        break;
                }

            }
        });

    }

    //
    String selectedGenderValue = "";
    String selectedAreaValue = "";

    void setSelextedValue() {
        LogUtil.logE(gender + ":" + samecity);
        switch (gender) {
            case gender_all:
                selectedGenderValue = "全部";
                break;
            case gender_boy:
                selectedGenderValue = "男";
                break;
            case gender_girl:
                selectedGenderValue = "女";
                break;

        }

        switch (samecity) {
            case field_all:
                selectedAreaValue = "全部";
                break;
            case field_city:
                selectedAreaValue = "同城";
                break;
        }

        tvWindowSelectedValue.setText(selectedGenderValue + " " + selectedAreaValue);
    }

    @Override
    public void onRefresh(XListView v) {
        listView.setPullLoadEnable(true);
        loadType = loadTypeTop;
        page = 1;
        lastid=0;
        getMainList();
    }

    @Override
    public void onLoadMore(XListView v) {
        loadType = loadTypeBottom;
        listView.setPullLoadEnable(false);
        getMainList();

    }

//    class GetNewStatusesReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context arg0, Intent intent) {
//            if (intent.getAction().equals(DongtaiAct.actionGetOneNewStatuses)) {
//                statuses = (Statuses) intent.getSerializableExtra("statuses");
//                adapter.addItem(statuses);
//            }
//
//        }
//
//    }

    @Override
    public void doPraise(long statusesid, int position) {
        this.position = position;
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("statusesid", statusesid + "");
        new HttpEntity(this).commonPostData(Method.weiboPraise, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }

            switch (methodName) {
                case Method.weiboSquare:
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName){
            case Method.weiboSquare:
                listView.onLoadMoreComplete();
                listView.onRefreshComplete();
                listView.setPullLoadEnable(true);
                List<Statuses> list = JSONHelper.getList(obj, Statuses.class);
                LogUtil.logE(methodName, list.size() + "");
                if (list.size() > 0) {
                    page++;
                    lastid = list.get(list.size()-1).getStatusesid();
                    if (loadType == loadTypeBottom) {
                        adapter.addData(list);
                    } else {
                        adapter.setData(list);
                    }

                    if (list.size() < Statuses.requestCount) {
                        listView.EndLoad();
                    }
                } else {
                    listView.EndLoad();
                }
                break;
            case Method.weiboRemark:
                ToastUtil.showToast(this, "添加评论成功");
                Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra(DongtaiMainAdapter.key, statuses);
                startActivityForResult(intent, requestDetailDongtai);
                break;
            case Method.weiboCollect:
                ToastUtil.showToast(this, "收藏成功");
                Collect mCollect = JSONHelper.getObject(obj, Collect.class);
                adapter.updateCollectText(listView, position,mCollect.getCollectcount());
                break;
            case Method.weiboCancelCollect:
                ToastUtil.showToast(this, "取消收藏成功");
                Collect mCollect2 = JSONHelper.getObject(obj, Collect.class);
                adapter.updateCancelCollectText(listView, position,mCollect2.getCollectcount());
                break;
            case Method.userFollow:
                ToastUtil.showToast(this, "关注成功");
                break;
            case Method.userIgnore:
                ToastUtil.showToast(this, "屏蔽成功");
                break;
            case Method.weiboPraise:
                PraiseResult praiseItem = JSONHelper.getObject(obj, PraiseResult.class);
                adapter.updatePraiseText(listView, position, praiseItem.getCount(), praiseItem.getType());
                break;
        }
//        if (methodName.equals(Method.weiboSquare)) {
//            listView.onLoadMoreComplete();
//            listView.onRefreshComplete();
//            List<Statuses> list = JSONHelper.getList(obj, Statuses.class);
//            LogUtil.logE(methodName, list.size() + "");
//            if (list.size() > 0) {
//                page++;
//                if (loadType == loadTypeBottom) {
//                    adapter.addData(list);
//                } else {
//                    adapter.setData(list);
//                }
//
//                if (list.size() < Statuses.requestCount) {
//                    listView.EndLoad();
//                }
//            } else {
//                listView.EndLoad();
//            }
//        } else if (methodName.equals(Method.weiboRemark)) {
//            ToastUtil.showToast(this, "添加评论成功");
//            Intent intent = new Intent(mContext, DongtaiDetail.class);
//            intent.putExtra(DongtaiMainAdapter.key, statuses);
//            startActivityForResult(intent, requestDetailDongtai);
//        } else if (methodName.equals(Method.weiboCollect)) {
//            ToastUtil.showToast(this, "收藏成功");
//            Collect mCollect = JSONHelper.getObject(obj, Collect.class);
//            adapter.updateCollectText(listView, position,mCollect.getCollectcount());
//        } else if (methodName.equals(Method.weiboCancelCollect)) {
//            ToastUtil.showToast(this, "取消收藏成功");
//            Collect mCollect = JSONHelper.getObject(obj, Collect.class);
//            adapter.updateCancelCollectText(listView, position,mCollect.getCollectcount());
//        } else if (methodName.equals(Method.userFollow)) {
//            ToastUtil.showToast(this, "关注成功");
//        } else if (methodName.equals(Method.userIgnore)) {
//            ToastUtil.showToast(this, "屏蔽成功");
//        } else if (methodName.equals(Method.weiboPraise)) {
//            PraiseResult praiseItem = JSONHelper.getObject(obj, PraiseResult.class);
//            adapter.updatePraiseText(listView, position, praiseItem.getCount(), praiseItem.getType());
//            if (praiseItem.getType() == PraiseResult.type_add) {
//               // ToastUtil.showToast(this, "点赞成功");
//            } else {
//               // ToastUtil.showToast(this, "取消点赞成功");
//            }
//
//          // adapter.setPraise(position, true);
//        }

    }

    Statuses statuses;
    Message message;

    @Override
    public void doForward(Statuses statuses, int position) {
        if (String.valueOf(statuses.getUser().getUserid()).equals(SharedToken.getUserId())) {
            ToastUtil.showToast(this, "不允许转发自己的微博");
            return;
        }
        this.position = position;

        Intent intent = new Intent(this, DongtaiPulish.class);
        intent.putExtra("linkType", LinkStatuses.linkTypeStateses);
        intent.putExtra("typeSource", DongtaiPulish.typeForward);
        intent.putExtra("data", statuses);
        // startActivity(intent);
        startActivityForResult(intent, requestForward);

    }

    @Override
    public void doRemark(int position, Statuses statuses, String content) {
        message = new Message();
        message.setContent(content);
        message.setName(SharedUser.getUserNick());
        // 以上构造评论item
        this.position = position;
        this.statuses = statuses;

        Params params = new Params();
        params.put("statusesid", String.valueOf(statuses.getStatusesid()));
        params.put("content", content);
        new HttpEntity(this).commonPostData(Method.weiboRemark, params, this);

    }

    @Override
    public void doCollect(long statusesid, int position, int isCollected) {
        this.position = position;
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("statusesid", statusesid + "");
        if (isCollected == Statuses.isfavoritedYes) {
            new HttpEntity(this).commonPostData(Method.weiboCancelCollect, params, this);
        } else {
            new HttpEntity(this).commonPostData(Method.weiboCollect, params, this);
        }

    }

    @Override
    public void enterDongtaiDetail(int position, Statuses statuses) {
        this.position = position;
        LogUtil.logE("enterDongtaiDetail " + this.position);
        Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
        intent.putExtra("statusesId", statuses.getStatusesid());
        intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
        startActivityForResult(intent, requestDetailDongtai);

    }

    @Override
    public void enterTribe(Statuses statuses) {
        this.statuses = statuses;
        Intent intent = new Intent(mContext, TribeMainAct.class);
        intent.putExtra("userId", statuses.getUser().getUserid());
        intent.putExtra("typeEntry", TribeMainAct.typeEntryOther);
        startActivityForResult(intent, DongtaiSquareAdapter.requestTribe);

    }

}
