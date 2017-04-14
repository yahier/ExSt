package yahier.exst.act.redpacket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.mine.MineWalletAct;
import com.stbl.stbl.adapter.redpacket.RpReceiveDetailAdpater;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.redpacket.RedpacketDetail;
import com.stbl.stbl.item.redpacket.RpPickersAll;
import com.stbl.stbl.item.redpacket.RpReceiveDetailItem;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;
import com.stbl.stbl.utils.StatusBarUtil;
import com.stbl.stbl.widget.XListView;
import com.umeng.socialize.utils.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 */

public class RedPacketDetailAct extends BaseActivity implements View.OnClickListener, FinalHttpCallback {
    ImageView imgSender;
    TextView tvSenderName;
    TextView tvGreetings;
    TextView tvTipInfo;//谨慎，需要根据多种不同的状态显示不同
    String redpacketid;
    int page = 1;
    XListView listView;
    RpReceiveDetailAdpater adapter;
    TextView tvRecordsBelowList, tvRecordsAlignBottom;
    View tvSenderTipBottom, tvSenderTip;
    View tvPickedMoneyTip;
    final String TAG = "RedPacketDetailAct";
    long lastid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet_detail_act);
        initViews();
        redpacketid = getIntent().getStringExtra("redpacketid");
        getDetail();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, R.color.theme_red_ff6);
    }

    void initViews() {
        listView = (XListView) findViewById(R.id.list);
        listView.setHeaderViewIsPull(false);
        listView.setPullLoadEnable(false);
        listView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
            }

            @Override
            public void onLoadMore(XListView v) {
                page++;
                getListData();
            }
        });
        View headerView = LayoutInflater.from(this).inflate(R.layout.red_packet_list_header_act, null);
        listView.addHeaderView(headerView);


        View footerView = LayoutInflater.from(this).inflate(R.layout.red_packet_list_footer_act, null);
        listView.addFooterView(footerView);
        tvGreetings = (TextView) findViewById(R.id.tvGreetings);
        imgSender = (ImageView) findViewById(R.id.imgSender);
        imgSender.setOnClickListener(this);
        tvSenderName = (TextView) findViewById(R.id.tvSenderName);
        tvTipInfo = (TextView) findViewById(R.id.tvTipInfo);
        tvRecordsBelowList = (TextView) findViewById(R.id.tvRecordsBelowList);
        tvRecordsAlignBottom = (TextView) findViewById(R.id.tvRecordsAlignBottom);
        tvRecordsBelowList.setOnClickListener(this);
        tvRecordsAlignBottom.setOnClickListener(this);
        tvPickedMoneyTip = findViewById(R.id.tvPickedMoneyTip);
        tvPickedMoneyTip.setOnClickListener(this);
        tvSenderTip = findViewById(R.id.tvSenderTip);
        tvSenderTipBottom = findViewById(R.id.tvSenderTipBottom);

        findViewById(R.id.imgBack).setOnClickListener(this);
        List<RpReceiveDetailItem> rpReceiveList = new ArrayList<>();
        adapter = new RpReceiveDetailAdpater(this, rpReceiveList);
        listView.setAdapter(adapter);

    }


    void getDetail() {
        Params params = new Params();
        params.put("redpacketid", redpacketid);
        new HttpEntity(this).commonRedpacketPostData(Method.getRedPacketDetail, params, this);
    }

    void getListData() {
        Log.e("getListData lastid",lastid+"");
        Params params = new Params();
        params.put("redpacketid", redpacketid);
        params.put("lastid",lastid);
        params.put("page", page);
        new HttpEntity(this).commonRedpacketPostData(Method.getRedpacketPickersV1, params, this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.tvRecordsBelowList:
                intent = new Intent(RedPacketDetailAct.this, RedPacketRecordAct.class);
                startActivity(intent);
                break;
            case R.id.tvRecordsAlignBottom:
                intent = new Intent(RedPacketDetailAct.this, RedPacketRecordAct.class);
                startActivity(intent);
                break;
            case R.id.imgSender:
                if (redpacketDetail == null) return;
                intent = new Intent(RedPacketDetailAct.this, TribeMainAct.class);
                intent.putExtra("userId", redpacketDetail.getUserid());
                startActivity(intent);
                break;
            case R.id.tvPickedMoneyTip:
                intent = new Intent(RedPacketDetailAct.this, MineWalletAct.class);
                startActivity(intent);
                break;

        }
    }


    RedpacketDetail redpacketDetail;

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag && item.getErr() != null) {
                ToastUtil.showToast(item.getErr().getMsg());
            }

            switch (methodName) {
                case Method.getRedpacketPickers:
                    listView.onLoadMoreComplete();
                    break;
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getRedPacketDetail:
                redpacketDetail = JSONHelper.getObject(obj, RedpacketDetail.class);
                ImageUtils.loadCircleHead(redpacketDetail.getUsericon(), imgSender);
                tvSenderName.setText(redpacketDetail.getUsername() + "的红包");
                tvGreetings.setText(redpacketDetail.getMessage());
                //判断红包类型
                if (redpacketDetail.getType() == RedpacketDetail.type_random) {
                    findViewById(R.id.tvPin).setVisibility(View.VISIBLE);
                    getListData();
                } else {
                    findViewById(R.id.tvPin).setVisibility(View.GONE);
                    //如果是普通红包.并且不是发布人，就不显示列表
                    if (!String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                        tvTipInfo.setVisibility(View.GONE);
                        tvRecordsAlignBottom.setVisibility(View.VISIBLE);
                    } else {
                        getListData();
                    }
                }
                //如果没有抢到红包，就不显示抢到的红包金额栏
                if (redpacketDetail.getTakenamount() != 0) {
                    findViewById(R.id.linPickedMoney).setVisibility(View.VISIBLE);
                    TextView tvPickedMoney = (TextView) findViewById(R.id.tvPickedMoney);
                    tvPickedMoney.setText(StringUtil.get2ScaleString(redpacketDetail.getTakenamount()));
                } else {
                    findViewById(R.id.linPickedMoney).setVisibility(View.GONE);
                }


                BigDecimal b1 = new BigDecimal(redpacketDetail.getAmount());
                BigDecimal b2 = new BigDecimal(redpacketDetail.getBalance());

                String moneyPicked2Scale = StringUtil.get2ScaleString(b1.subtract(b2).floatValue());
                String moneyAll2Scale = StringUtil.get2ScaleString(redpacketDetail.getAmount());
                String moneyTip = moneyPicked2Scale + "/" + moneyAll2Scale;
                switch (redpacketDetail.getStatus()) {
                    case RedpacketDetail.status_expire:
                        String numTip = (redpacketDetail.getTotalqty() - redpacketDetail.getRestqty()) + "/" + redpacketDetail.getTotalqty() + "个";
                        if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                            tvTipInfo.setText("该红包已过期,已领取" + numTip + ",共" + moneyTip + "元");
                        } else {
                            tvTipInfo.setText("该红包已过期,已领取" + numTip);
                        }
                        break;
                    case RedpacketDetail.status_pickabel:
                        numTip = (redpacketDetail.getTotalqty() - redpacketDetail.getRestqty()) + "/" + redpacketDetail.getTotalqty() + "个";
                        if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                            tvTipInfo.setText("已领取" + numTip + ",共" + moneyTip + "元");
                        } else {
                            tvTipInfo.setText("已领取" + numTip);
                        }
                        break;
                    case RedpacketDetail.status_pickOver:
                        String valueTip;
                        if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                            valueTip = redpacketDetail.getTotalqty() + "个红包" + moneyAll2Scale + "元," + redpacketDetail.getReceiveTimelongStr() + "被抢完";
                        } else {
                            valueTip = redpacketDetail.getTotalqty() + "个红包," + redpacketDetail.getReceiveTimelongStr() + "被抢完";
                        }

                        tvTipInfo.setText(valueTip);
                        break;
                }
                break;
            case Method.getRedpacketPickersV1:
                RpPickersAll rpPickers = JSONHelper.getObject(obj, RpPickersAll.class);
                if (rpPickers != null) {
                    lastid = rpPickers.getLastid();
                    if (rpPickers.getDatalist().size() > 0) {
                        listView.setPullLoadEnable(true);
                    } else {
                        listView.setPullLoadEnable(false);
                    }
                    adapter.addData(rpPickers.getDatalist());

                }

                //如果没有抢到红包,也不是发红包的人 就什么提示都不显示了
                if (redpacketDetail.getTakenamount() == 0 && !String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                    tvRecordsBelowList.setVisibility(View.GONE);
                    tvRecordsAlignBottom.setVisibility(View.GONE);
                    // TODO: 17/1/5
                } else {
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int screenHeight = Device.getHeight() - Device.getStatusBasrHeight(RedPacketDetailAct.this);

                            LogUtil.logE(TAG + "height", screenHeight + ":" + listView.getHeight());

                            int tvHeight = Util.dip2px(RedPacketDetailAct.this, 20 + 20 + 15);
                            if (screenHeight > (listView.getHeight() + tvHeight)) {
                                LogUtil.logE(TAG, "if");
                                if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                                    tvSenderTipBottom.setVisibility(View.VISIBLE);
                                    tvSenderTip.setVisibility(View.GONE);
                                } else {
                                    tvRecordsAlignBottom.setVisibility(View.VISIBLE);
                                    tvRecordsBelowList.setVisibility(View.GONE);
                                }

                            } else {
                                LogUtil.logE(TAG, "else");
                                if (String.valueOf(redpacketDetail.getUserid()).equals(SharedToken.getUserId())) {
                                    tvSenderTip.setVisibility(View.VISIBLE);
                                    tvSenderTipBottom.setVisibility(View.GONE);
                                } else {
                                    tvRecordsBelowList.setVisibility(View.VISIBLE);
                                    tvRecordsAlignBottom.setVisibility(View.GONE);
                                }

                            }


                        }
                    }, 200);

                }


                break;

        }

    }
}
