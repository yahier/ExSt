package yahier.exst.act.redpacket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.redpacket.RpSendRecordAdapter;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.common.TabHome;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.redpacket.RpReceiveLogItem;
import com.stbl.stbl.item.redpacket.RpSendLogItem;
import com.stbl.stbl.item.redpacket.RpSummaryItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 发出的红包记录
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RedPacketSendFragment extends BaseFragment implements FinalHttpCallback,XListView.OnXListViewListener,View.OnClickListener{

    private XListView xlvSendRecord;
    private RpSendRecordAdapter mAdapter;
    private List<RpSendLogItem> rpSendList;
    private int page = 1; //页码
    private int year = 0; //年份

    private ImageView iv_avatar; //头像
    private TextView tv_year; //年份
    private LinearLayout ll_year_select; //年份选择
    private TextView tv_username; //用户名
    private TextView tv_received_money_amount;//总金额
    private TextView tv_sended_count; //个数

    private TextView tv_nodata_tips; //没数据提示
    private TextView tv_to_shoppingcircle; //去商圈

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_redpacket_send_layout,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initView(){
        View view = getView();
        xlvSendRecord = (XListView) view.findViewById(R.id.xlv_send_record);
        xlvSendRecord.setPullLoadEnable(true);

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_rp_sended_header,null);
        iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
        tv_year = (TextView) headView.findViewById(R.id.tv_year);
        ll_year_select = (LinearLayout) headView.findViewById(R.id.ll_year_select);
        tv_username = (TextView) headView.findViewById(R.id.tv_username);
        tv_received_money_amount = (TextView) headView.findViewById(R.id.tv_received_money_amount);
        tv_sended_count = (TextView) headView.findViewById(R.id.tv_sended_count);
        tv_nodata_tips = (TextView) headView.findViewById(R.id.tv_nodata_tips);
        tv_to_shoppingcircle = (TextView) headView.findViewById(R.id.tv_to_shoppingcircle);

        tv_to_shoppingcircle.setOnClickListener(this);
        xlvSendRecord.addHeaderView(headView);
        mAdapter = new RpSendRecordAdapter(getActivity(),rpSendList);
        xlvSendRecord.setAdapter(mAdapter);
        xlvSendRecord.setOnXListViewListener(this);
        xlvSendRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RpSendLogItem item = (RpSendLogItem) parent.getAdapter().getItem(position);
                if (item == null) return;
                String rpId = item.getRedpacketid();
                Intent intent = new Intent(getActivity(),RedPacketDetailAct.class);
                intent.putExtra("redpacketid",rpId);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_shoppingcircle:
                Intent intent  = new Intent(getActivity(), TabHome.class);
                intent.setAction(ACTION.GO_TO_STATUS_PAGE);
                intent.putExtra(TabHome.Index_index,2);
                startActivity(intent);
                break;
        }
    }

    private void initData(){
        rpSendList = new ArrayList<>();
        year = Calendar.getInstance().get(Calendar.YEAR);
        tv_year.setText(String.valueOf(year));
        WaitingDialog.show(getActivity(),true);
        getHeadData(year);
        getSendedList(year,page);
    }

    private void showSummaryInfo(RpSummaryItem item){
        ImageUtils.loadCircleHead(item.getUsericon(),iv_avatar);
        tv_username.setText(item.getUsername());
        tv_received_money_amount.setText(item.getTotalamount());

        String totalcount = String.valueOf(item.getTotalcount());
        if(getActivity()==null)return;
        String countText = String.format(getActivity().getString(R.string.rp_send_count),totalcount);
        SpannableString spannableString = new SpannableString(countText);
        spannableString.setSpan(new ForegroundColorSpan
                (getResources().getColor(R.color.yellow_ffb)),countText.indexOf(totalcount),
                countText.indexOf(totalcount)+totalcount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_sended_count.setText(spannableString);
    }

    @Override
    public void onRefresh(XListView v) {
        page = 1;
        xlvSendRecord.setPullLoadEnable(true);
        getHeadData(year);
        getSendedList(year,page);
    }

    @Override
    public void onLoadMore(XListView v) {
        getSendedList(year,page);
    }

    //领取红包的摘要
    private void getHeadData(int year){
        Params params = new Params();
        params.put("year",year);//年份 (2000-2100)
        params.put("querytype",2);//查询类型，1-收到，2-发出
        new HttpEntity(getActivity()).commonRedpacketPostData(Method.userGetRedpacketSummary,params,this);
    }
    //领取的记录列表
    private void getSendedList(int year,int pager){
        Params params = new Params();
        params.put("year",year);//年份 (2000-2100)
        params.put("page",pager);//页码
        new HttpEntity(getActivity()).commonRedpacketPostData(Method.userGetSentRedpackets,params,this);
    }

    @Override
    public void parse(String methodName, String result) {
        LogUtil.logE("LogUtil",methodName+"----result-->"+result);
        WaitingDialog.dismiss();
        if (xlvSendRecord != null){
            xlvSendRecord.onRefreshComplete();
            xlvSendRecord.onLoadMoreComplete();
        }
        BaseItem item = JSONHelper.getObject(result,BaseItem.class);
        if (item != null && item.getIssuccess() != BaseItem.successTag){
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null)
                ToastUtil.showToast(item.getErr().getMsg());
            return;
        }
        String json = JSONHelper.getStringFromObject(item.getResult());

        switch (methodName){
            case Method.userGetRedpacketSummary: //接收红包总的信息
                RpSummaryItem summaryItem = JSONHelper.getObject(json,RpSummaryItem.class);
                if (summaryItem != null) showSummaryInfo(summaryItem);
                break;
            case Method.userGetSentRedpackets: //个人领取红包列表
                JSONObject jsonObject = JSON.parseObject(json);
                List<RpSendLogItem> list = null;
                if (jsonObject != null && jsonObject.containsKey("datalist")){
                    list = JSONHelper.getList(jsonObject.get("datalist").toString(),RpSendLogItem.class);
                }
                if (list == null || (list != null && list.size() == 0)){
                    xlvSendRecord.EndLoad();
                    if (rpSendList.size() == 0 ){ //没数据，去商圈按钮
                        tv_nodata_tips.setVisibility(View.VISIBLE);
                        tv_to_shoppingcircle.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                if (rpSendList.size() > 0) {
                    tv_nodata_tips.setVisibility(View.GONE);
                    tv_to_shoppingcircle.setVisibility(View.GONE);
                }

                if (page == 1){
                    rpSendList.clear();
                }
                //防止多次失败，其中对应页码的数据刷不出来了
                page ++;
                rpSendList.addAll(list);
                mAdapter.setDataChange(rpSendList);
                break;
        }

    }
}
