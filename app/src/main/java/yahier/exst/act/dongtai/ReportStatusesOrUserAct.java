package yahier.exst.act.dongtai;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.ad.ShoppingCircleDetail;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

/**
 * 举报动态(从动态详细页面进入)或者举报用户(从群聊中进入)
 *
 * @author lenovo
 */
public class ReportStatusesOrUserAct extends ThemeActivity implements FinalHttpCallback {
    Button okBtn;
    TextView tvLeftCount;
    int type;
    EditText input;
    private long referenceid;
    private String referenceidStr;
    public final static int typeUser = 1;
    public final static int typeStatuses = 2;
    public final static int typeThread = 3;// 跟帖id
    public final static int typeLive = 4; //直播
    public final static int typeGoods = 5; //商品
    public final static int typeShoppingCircle = 6; //商品

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_report);
        setLabel("举报");
        input = (EditText) findViewById(R.id.input_report);
        okBtn = (Button) findViewById(R.id.ok_report);
        tvLeftCount = (TextView) findViewById(R.id.tvLeftCount);

        type = getIntent().getIntExtra("type", 0);

        switch (type) {
            case 0:
                finish();
                break;
            case typeStatuses:
                final Statuses statuses = (Statuses) getIntent().getSerializableExtra("statuses");
                referenceid = statuses.getStatusesid();
                break;
            case typeUser:
                final String userId = getIntent().getStringExtra("userId");
                try {
                    referenceid = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case typeThread: //跟帖
                referenceid = getIntent().getLongExtra("referenceid", 0);
                break;
            case typeLive: //直播间
                referenceid = getIntent().getLongExtra("referenceid", 0);
                break;
            case typeGoods: //商品
                referenceid = getIntent().getLongExtra("referenceid", 0); //商品id
                break;
            case typeShoppingCircle: //商品
                final ShoppingCircleDetail shoppingCircleDetail = (ShoppingCircleDetail) getIntent().getSerializableExtra("statuses");
                referenceidStr = shoppingCircleDetail.getSquareid();
                break;
        }
        if (referenceid != 0)
            referenceidStr = String.valueOf(referenceid);

        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String content = input.getText().toString().trim();
                if (content.equals("")) {
                    ToastUtil.showToast(ReportStatusesOrUserAct.this, "你还没有输入内容");
                }
                try {
                    doReport(type, referenceidStr, content);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.linAll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodUtils.hideInputMethod(input);
            }
        });


        final int maxWordSize = Config.remarkContentLength;
        input.addTextChangedListener(new TextListener() {

            @Override
            public void afterTextChanged(Editable edit) {
                String content = edit.toString();
                int size = content.length();
                if (size > maxWordSize) {
                    input.setText(content.substring(0, maxWordSize));
                    input.setSelection(maxWordSize);
                    okBtn.setBackgroundColor(getResources().getColor(R.color.theme_yellow));
                } else if (edit.toString().trim().equals("")) {
                    tvLeftCount.setText(size + "/" + maxWordSize);
                    okBtn.setEnabled(false);
                    okBtn.setBackgroundColor(getResources().getColor(R.color.theme_yellow));
                } else {
                    tvLeftCount.setText(size + "/" + maxWordSize);
                    okBtn.setEnabled(true);
                    okBtn.setBackgroundResource(R.drawable.common_btn_red);
                }
            }
        });

        findViewById(R.id.theme_top_banner_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTip();
            }
        });

    }

    /**
     * @param type           举报类型：1-用户 2-动态贴 3-话题跟帖 4-直播
     * @param referenceidStr 举报对象id（type=1，那就是用户id，2就是动态id，3就是跟帖id）
     * @param reason         举报原因
     */
    private void doReport(int type, String referenceidStr, String reason) {
        Params params = new Params();
        params.put("type", type);
        params.put("referenceid", referenceidStr);
        params.put("reason", reason);
        new HttpEntity(this).commonPostData(Method.report, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        LogUtil.logE("LogUtil", "methodName-:" + methodName + "-result-:" + result);
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            return;
        }
        if (methodName.equals(Method.report)) {
            ToastUtil.showToast(this, "举报成功");
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        checkTip();
    }


    void checkTip() {
        String value = input.getText().toString();
        if (value.length() > 0) {

            TipsDialog.popup(this, "是否放弃本次编辑？", "取消", "确定", new TipsDialog.OnTipsListener() {

                @Override
                public void onConfirm() {
                    finish();
                }

                @Override
                public void onCancel() {
                }
            });
        } else {
            finish();
        }
    }
}
