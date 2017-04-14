package yahier.exst.act.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.RedPacket;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PayingPwdDialog.OnInputListener;
import com.stbl.stbl.util.Payment;
import com.stbl.stbl.util.ToastUtil;

/**
 * 填写红包页面，并发送
 *
 * @author lenovo
 */
public class SendRedPackectAct extends ThemeActivity {
    EditText inputDouCount;
    Button btnCastBean;
    Context mContext;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_send_redpacket_act);
        mContext = this;
        setLabel(R.string.red_packet);
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            ToastUtil.showToast(R.string.has_not_choose_receiver);
            return;
        }
        btnCastBean = (Button) findViewById(R.id.btnCastBean);
        inputDouCount = (EditText) findViewById(R.id.inputDouCount);
        btnCastBean.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                String strDouCount = inputDouCount.getText().toString();

                if (strDouCount.equals("")) {
                    ToastUtil.showToast(R.string.please_input_greenbean_count);
                    return;
                }
                int douCount = 0;
                try {
                    douCount = Integer.valueOf(strDouCount);
                } catch (Exception e) {
                    ToastUtil.showToast(R.string.count_must_be_integer);
                }

                createHongbao(douCount);
            }
        });
    }

    void createHongbao(int amount) {
        final Params params = new Params();
        params.put("amount", amount);
        params.put("hongbaotype", RedPacket.typeRedPacket);//
        params.put("touserid", userId);
        Payment.getPassword(this,amount, new OnInputListener() {

            @Override
            public void onInputFinished(String pwd) {
                params.put("paypwd", pwd);
                new HttpEntity(SendRedPackectAct.this).commonPostData(Method.imCreateRedPacket, params, new FinalHttpCallback() {

                    @Override
                    public void parse(String methodName, String result) {
                        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                        if (item.getIssuccess() != BaseItem.successTag) {
                            ToastUtil.showToast(mContext, item.getErr().getMsg());
                            return;
                        }
                        String obj = JSONHelper.getStringFromObject(item.getResult());
                        RedPacket redPacket = JSONHelper.getObject(obj, RedPacket.class);
                        // EventBus.getDefault().post(new
                        // EventTypeWallet(EventTypeWallet.typeBance));// 刷新余额
                        // EventBus.getDefault().post(new
                        // EventTypeWallet(EventTypeWallet.typeShitudou));//
                        // 刷新师徒豆
                        LogUtil.logE("hongbaoId:" + redPacket.getHongbaoid());
                        ToastUtil.showToast(R.string.send_red_packet_success);
                        Intent intent = getIntent();
                        intent.putExtra("item", redPacket);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                });
            }
        });
    }

}
