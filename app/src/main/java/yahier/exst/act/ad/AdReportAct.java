package yahier.exst.act.ad;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.ad.AdReportAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.CommonDict;
import com.stbl.stbl.item.ad.AdBusinessType;
import com.stbl.stbl.model.Ad;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.util.database.DataCacheDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/3.
 */

public class AdReportAct extends ThemeActivity {
    GridView listView;
    TextView tvCommit;
    EditText inputContent;
    Ad ad;
    AdReportAdapter adapter;
    List<AdBusinessType> adreporttype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_report_act);
        setLabel("举报");

        ad = (Ad) getIntent().getSerializableExtra("ad");


        initViews();
        initData();
    }

    void initData(){
        final String cache = (String) SharedPrefUtils.getFromPublicFile(KEY.adreporttype,"");
        if(cache.equals("")){
            CommonTask.getCommonDicBackground();
            ToastUtil.showToast("数据有误，请稍候重试");
            return;
        }
        adreporttype = JSONHelper.getList(cache,AdBusinessType.class);
        adapter = new AdReportAdapter(adreporttype);
        listView.setAdapter(adapter);

    }
    void initViews() {
        inputContent = (EditText) findViewById(R.id.inputContent);
         listView = (GridView) findViewById(R.id.list);
        tvCommit = (TextView) findViewById(R.id.tvCommit);
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
    }


    void report() {
        AdBusinessType type = adapter.getSelected();
        if (type == null) {
            ToastUtil.showToast("原因必选");
            return;
        }
        WaitingDialog.show(this, R.string.waiting);
        Params params = new Params();
        params.put("adid", ad.adid);
        params.put("reportingtype", type.getValue());
        params.put("content", inputContent.getText().toString());
        new HttpEntity(mActivity).commonPostData(Method.reportAd, params, new FinalHttpCallback() {
            @Override
            public void parse(String methodName, String result) {

                WaitingDialog.dismiss();
                BaseItem item = JSONHelper.getObject(result, BaseItem.class);
                if (item.getIssuccess() == BaseItem.successTag) {
                    ToastUtil.showToast("举报成功");
                }else{
                    ToastUtil.showToast(item.getErr().getMsg());
                }
                finish();
            }
        });

    }


}
