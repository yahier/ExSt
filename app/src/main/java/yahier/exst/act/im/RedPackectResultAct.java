package yahier.exst.act.im;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.rong.MyNotiMessage;
import com.stbl.stbl.act.im.rong.NotificationType;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.PickResultBig;
import com.stbl.stbl.item.im.RedPacketOpenResult;
import com.stbl.stbl.item.im.RedPacketPickResult;
import com.stbl.stbl.item.im.RedPacktPicker;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * 红包结果页面
 *
 * @author lenovo
 */
public class RedPackectResultAct extends ThemeActivity implements FinalHttpCallback {
    Button btnOpen;
    TextView tvTip;
    Context mContext;
    long hongbaoId;
    TextView tvCount;
    String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_redpacket_result_act);

        hongbaoId = getIntent().getLongExtra("hongbaoId", 0);
        targetId = getIntent().getStringExtra("targetId");
        if (hongbaoId == 0||targetId==null) {
            ToastUtil.showToast(R.string.data_error);
            return;
        }
        mContext = this;
        setLabel(R.string.red_packet);
        tvCount = (TextView) findViewById(R.id.tvCount);
        tvTip = (TextView) findViewById(R.id.tvTip);
        btnOpen = (Button) findViewById(R.id.btnOpen);
        openRedPacket(hongbaoId);
        btnOpen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                pickRedPacket(hongbaoId);
            }
        });
    }

    void openRedPacket(long hongbaoid) {
        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(mContext).commonPostData(Method.imOpenRedPacket, params, this);
    }

    void pickRedPacket(long hongbaoid) {
        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(mContext).commonPostData(Method.imPickRedPacket, params, this);
    }


    //需要判断是不是
    void getResult(long hongbaoid) {

        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(this).commonPostData(Method.imGetRedPacket, params, this);
    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.imOpenRedPacket:
                RedPacketOpenResult openResult = JSONHelper.getObject(obj, RedPacketOpenResult.class);
                LogUtil.logE("imOpenRedPacket states:" + openResult.getStatus());
                switch (openResult.getStatus()) {
                    case RedPacketOpenResult.status_avilable:
                        btnOpen.setVisibility(View.VISIBLE);
                        break;
                    case RedPacketOpenResult.status_expire:
                        ToastUtil.showToast(R.string.time_expired);
                        break;
                    case RedPacketOpenResult.status_finished:
                        getResult(openResult.getHongbaoid());
                        break;
                    case RedPacketOpenResult.status_hasGot:
                        getResult(openResult.getHongbaoid());
                        break;
                }
                break;
            case Method.imPickRedPacket:
                PickResultBig pcikResult = JSONHelper.getObject(obj, PickResultBig.class);
                LogUtil.logE("imPickRedPacket states:" + pcikResult.getStatus());
                switch (pcikResult.getStatus()) {
                    case RedPacketPickResult.status_avilable:
                        //ToastUtil.showToast(mContext, "status_avilable");
                        break;
                    case RedPacketPickResult.status_expire:
                        ToastUtil.showToast(R.string.time_expired);
                        break;
                    case RedPacketPickResult.status_finished:
                        getResult(pcikResult.getHongbaoid());
                        break;
                    case RedPacketPickResult.status_hasGot:
                        btnOpen.setVisibility(View.GONE);
                        findViewById(R.id.linResult).setVisibility(View.VISIBLE);
                        tvCount.setText("" + pcikResult.getPickedresult().getAmount());

                        //发送通知
                        MyNotiMessage mesage = MyNotiMessage.obtain(String.valueOf(NotificationType.typeOpenRedPackect));
                        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, targetId, mesage, null, null,null);
                        break;
                }
                break;
            case Method.imGetRedPacket:
                RedPacketPickResult redResult = JSONHelper.getObject(obj, RedPacketPickResult.class);
                if (redResult == null) return;

                if (redResult.getPickusers() != null && redResult.getPickusers().size() > 0) {
                    RedPacktPicker picker = redResult.getPickusers().get(0);
                    btnOpen.setVisibility(View.GONE);
                    findViewById(R.id.linResult).setVisibility(View.VISIBLE);
                    tvCount.setText(picker.getPickamount());
                    if (String.valueOf(picker.getPickuserid()).equals(SharedToken.getUserId())) {
                        tvTip.setText(R.string.goldBeanHasStoreInYourAccount);
                    } else {
                        tvTip.setText(R.string.goldBeanHasBeenGot);
                    }


                }

                break;
        }
    }




}
