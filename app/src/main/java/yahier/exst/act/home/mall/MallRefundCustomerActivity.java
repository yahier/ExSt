package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.MallOrder;
import com.stbl.stbl.ui.BaseClass.STBLBaseTableActivity;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.HttpUtil;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by meteorshower on 16/3/28.
 * 退货/售后
 */
public class MallRefundCustomerActivity extends STBLBaseTableActivity<MallOrder> {

    private boolean loadMoreFlag = false;
    private TextView emptyView;
    //填写订单号requestcode
    public static final int REQUEST_EXPRESS = 0x1211;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_refresh_navigation_list);

        navigationView.setTitleBar(getString(R.string.mall_return_after_sale));
        navigationView.setClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bindRefreshList(R.id.lv_content, new MallOrderAdapter1(this, arrayList));

        emptyView = (TextView)findViewById(R.id.empty_tv);
        emptyView.setText(R.string.mall_no_data);

        startRefresh();
        EventBus.getDefault().register(this);
    }

    public void onEvent(EventType type){
        if (type != null && type.getType() == EventType.TYPE_REFRESH_ORDER_LIST){
            onReload();
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
                if (tableAdapter instanceof MallOrderAdapter1){
                    orderid = ((MallOrderAdapter1) tableAdapter).getRefundOrderId();
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
        new HttpEntity(this).commonPostJson(Method.replyRefundDeliver, json.toString(),this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void getData(int lastId) {
        Params params = new Params();
        params.put("querytype", 4);
        params.put("count", 15);
        params.put("lastid", lastId);
        LogUtil.logE("lastid:" + lastId);
        new HttpEntity(this, false).commonPostData(Method.showOrderList, params, this);
    }

    @Override
    public void onReload() {
        loadMoreFlag = false;
        getData(0);
    }

    @Override
    public void loadMore() {
        if(arrayList.size() > 0) {
            loadMoreFlag = true;
            getData(arrayList.get(arrayList.size() - 1).getId());
        }
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        stopRefresh();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        stopRefresh();
        switch(methodName){
            case Method.showOrderList:
                List<MallOrder> list = JSONHelper.getList(valueObj, MallOrder.class);
                if(list != null){
                    if(!loadMoreFlag)
                        arrayList.clear();

                    arrayList.addAll(list);
                    tableAdapter.notifyDataSetChanged();
                }

                emptyView.setVisibility(arrayList.size() == 0 ? View.VISIBLE : View.GONE);
                break;
            case Method.replyRefundDeliver:
                getData(0);
                ToastUtil.showToast(this,getString(R.string.mall_goods_mail_return));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
