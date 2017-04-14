package yahier.exst.act.home.mall.integral;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mall.integral.MallExchangeHistoryAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.model.MallIntegralProduct;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.TitleBar;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 积分兑换商品记录
 * Created by Administrator on 2016/7/25 0025.
 */
public class MallExchangeHistoryAct extends BaseActivity implements FinalHttpCallback,XListView.OnXListViewListener{
    private TitleBar mBar; //标题栏
    private XListView xListView;//兑换记录列表
    private MallExchangeHistoryAdapter mAdapter;//兑换记录列表适配器
    private List<MallOrder> mData;//商品数据

    private int lastid = 0;//最后一条id
    private int page = 1;//当前页面
    private int count = 15; //每页商品数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_exchange_history_layout);
        mBar = (TitleBar) findViewById(R.id.bar);
        xListView = (XListView) findViewById(R.id.xlv_Exchange_history);

        mData = new ArrayList<>();
        mBar.setCenterTitle(R.string.integral_exchange_history);
        mBar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new MallExchangeHistoryAdapter(this,mData);
        xListView.setAdapter(mAdapter);
        xListView.setOnXListViewListener(this);

        getHistoryList();
    }

    @Override
    public void onRefresh(XListView v) {
        page = 1;
        lastid = 0;
        getHistoryList();
    }

    @Override
    public void onLoadMore(XListView v) {
        page ++;
        getHistoryList();
    }

    /**
     * 请求列表数据
     */
    private void getHistoryList(){
        Params params = new Params();
        params.put("lastid", lastid);
        params.put("count", count);
        new HttpEntity(this).commonPostData(Method.orderPointsmallOrderShow, params, this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void parse(String methodName, String result) {
        if (xListView != null){
            xListView.onLoadMoreComplete();
            xListView.onRefreshComplete();
        }
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.orderPointsmallOrderShow: //兑换历史记录
                List<MallOrder> list = JSONHelper.getList(obj, MallOrder.class);
                xListView.onLoadMoreComplete();
                xListView.onRefreshComplete();
                if (list != null && list.size() >= 0) {
                    if (list.size() > 0) {
                        lastid = list.get(list.size() - 1).getId();
                    }
                    if (page == 1) {
                        mData.clear();
                    }
                    mData.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }
//                if (list.size() < count) {
//                    xListView.EndLoad();
//                }
                if (mData.size() == 0 || list.size() < count){
                    xListView.EndLoad();
                    xListView.setFooterViewText(getString(R.string.no_more));
                }
                break;
        }
    }
}
