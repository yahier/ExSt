package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallOrderAdapter1.OnOrderItemClickListener;
import com.stbl.stbl.act.home.seller.OrderDetailAct;
import com.stbl.stbl.common.RCommonAdapter;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import io.rong.eventbus.EventBus;

public class MallOrderFrag extends Fragment implements OnItemClickListener, OnXListViewListener, FinalHttpCallback {

    private XListView mListView;
    private RCommonAdapter<MallOrder> mAdapter;
    private ArrayList<MallOrder> listData;
    int pageIndex;
    private Context mContext;
    final int requestCount = 15;
    long lastid;
    int loadType = 0;// 加载模式
    final int loadTypeBottom = 0;// 底部加载
    final int loadTypeTop = 1;// 顶部加载。清除以前数据
    int queryType;
    //填写订单号requestcode
    public static final int REQUEST_EXPRESS = 0x1211;
    private TextView emptyView;

    @Override
    public void onAttach(Activity context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // EventBus的回调
    public void onEvent(EventType type) {
        switch (type.getType()) {
            // 刷新售后订单列表
            case EventType.TYPE_REFRESH_ORDER_5:
                getData();// 有点困惑参数值
                break;
            case EventType.TYPE_REFRESH_ORDER_LIST://状态有更改，刷新订单列表
                LogUtil.logE("LogUtil","orderfrag onEvent TYPE_REFRESH_ORDER_LIST");
                update();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_EXPRESS) {
                String expressno = data.getStringExtra("KEY_NUM");
                int expresscomtype = data.getIntExtra("KEY_NAME", 0);
                long orderid = 0;
                if (mAdapter instanceof MallOrderAdapter5) {
                    orderid = ((MallOrderAdapter5) mAdapter).getRefundOrderId();
                }else if (mAdapter instanceof MallOrderAdapter1){
                    orderid = ((MallOrderAdapter1) mAdapter).getRefundOrderId();
                }
                refundDeliver(orderid, expressno, expresscomtype);
            }
        }
    }

    //买家填写订单号退货
    private void refundDeliver(long orderid, String expressno, int expresscomtype) {
        JSONObject json = new JSONObject();
        try {
            json.put("orderid", orderid);
            json.put("expressno", expressno);
            json.put("expresscomtype", expresscomtype);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.logD("LogUtil", "-----" + json.toString());
        new HttpEntity(mContext).commonPostJson(Method.replyRefundDeliver, json.toString(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.seller_order_list, null);
        mListView = (XListView) layout.findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        mListView.setOnXListViewListener(this);
        listData = new ArrayList<>();

        emptyView = (TextView)layout.findViewById(R.id.empty_tv);

        Bundle bundle = getArguments();
        pageIndex = bundle.getInt("index");
        LogUtil.logE("index:" + pageIndex);

        switch (pageIndex) {
            case 0:
                mAdapter = new MallOrderAdapter1(this, listData);
                queryType = MallOrder.querytypeAll;
                getData();
                break;
            case 1:
                mAdapter = new MallOrderAdapter2(getActivity(), listData);
                queryType = MallOrder.querytypeToBePaid;
                getData();
                break;
            case 2:
//                mAdapter = new MallOrderAdapter3(getActivity(), listData);
                mAdapter = new MallOrderAdapter3s(getActivity(), listData);
                queryType = MallOrder.querytypeToSend;
                getData();
                break;
            case 3:
                mAdapter = new MallOrderAdapter4(getActivity(), listData);
                queryType = MallOrder.querytypeToReceive;
                getData();
                break;
            case 4:
                mAdapter = new MallOrderAdapter5(this, listData);
                queryType = MallOrder.STATE_EVALUATE;
                getData();
                break;
            default:
                mAdapter = new MallOrderAdapter1(this, listData);
                break;

        }
        mListView.setAdapter(mAdapter);
        return layout;
    }

    void getData() {
        Params params = new Params();
        params.put("querytype", queryType);
        params.put("count", requestCount);
        params.put("lastid", lastid);
        LogUtil.logE("lastid:" + lastid);
        new HttpEntity(mContext, false).commonPostData(Method.showOrderList, params, this);
    }

    public void update() {
        loadType = loadTypeTop;
        lastid = 0;
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Activity act = getActivity();
        Intent it = new Intent(act, OrderDetailAct.class);
        act.startActivity(it);
    }

    @Override
    public void onRefresh(XListView v) {
        loadType = loadTypeTop;
        lastid = 0;
        getData();
        ((MallOrderAct) getActivity()).getCountData(null);
    }

    @Override
    public void onLoadMore(XListView v) {
        loadType = loadTypeBottom;
        getData();
    }

//    @Override
//    public void OnItemButtonClick(int position, int id) {
//        switch (id) {
//            case R.id.btn_pingjia:
//                TipsDialog tips = new TipsDialog(getActivity(), "取消订单", "取消", "确定");
//                tips.show();
//                break;
//            case R.id.btn_tuihuo:
//                Intent it = new Intent(getActivity(), MallReturnApplyAct.class);
//                startActivity(it);
//                break;
//        }
//    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mContext, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.showOrderList:
                    mListView.onLoadMoreComplete();
                    mListView.onRefreshComplete();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.showOrderList:
                List<MallOrder> list = JSONHelper.getList(obj, MallOrder.class);
                mListView.onLoadMoreComplete();
                mListView.onRefreshComplete();
                if (list != null && list.size() >= 0) {
                    if (list.size() > 0) {
                        lastid = list.get(list.size() - 1).getId();
                    }
                    if (loadType == loadTypeBottom) {
                        mAdapter.addData(list);
                        if (list.size() < Statuses.requestCount) {
                            mListView.EndLoad();
                        }
                    } else {
//                        LogUtil.logE("else..set");
                        mAdapter.setData(list);
                    }
                }
                emptyView.setVisibility(mAdapter.getCount() > 0 ? View.GONE : View.VISIBLE);
                break;
            case Method.replyRefundDeliver:
                getData();
                ToastUtil.showToast(mContext,mContext.getString(R.string.mall_goods_mail_return));
                break;
        }

    }

}
