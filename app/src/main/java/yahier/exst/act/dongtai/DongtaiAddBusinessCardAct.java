package yahier.exst.act.dongtai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DongtaiAddBusinessCardAdapter;
import com.stbl.stbl.adapter.DongtaiAddBusinessCardAdapter.OnAdapterListener;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupMemberList;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;


/**
 * @author yahier 添加名片
 */
public class DongtaiAddBusinessCardAct extends ThemeActivity implements FinalHttpCallback, OnAdapterListener {
    XListView listView;
    EditText input;
    DongtaiAddBusinessCardAdapter adapter;
    //final int requestCount = 15;
    //int loadType = 0;// 加载模式
    //final int loadTypeBottom = 0;// 底部加载
    View btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_add_business_card);
        setLabel(R.string.add_card);
        listView = (XListView) findViewById(R.id.list);
        input = (EditText) findViewById(R.id.input);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        //listView.setOnXListViewListener(this);
        adapter = new DongtaiAddBusinessCardAdapter(this);
        adapter.setOnAdapterListener(this);
        listView.setAdapter(adapter);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getData();
            }
        });
        btnSearch.performClick();

        input.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable edit) {
                String value = edit.toString();
                filter(value);
            }
        });
    }

    void getData() {
        Params params = new Params();
        params.put("keyword", input.getText().toString());
        params.put("grouptype", GroupMemberList.typeRequestNoneGroup);
        params.put("hasself", GroupMemberList.hasselfYes);
        new HttpEntity(this).commonPostData(Method.getAppContacts, params, this);
    }

    //过滤
    void filter(String value) {
        if (list == null) return;
        List<UserItem> newList = new ArrayList<UserItem>();
        for (int i = 0; i < list.size(); i++) {
            UserItem user = list.get(i);
            if (user.getNickname().contains(value)) {
                newList.add(user);
            }
        }

        adapter.setData(newList);
    }

    List<UserItem> list;

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        if (methodName.equals(Method.getAppContacts)) {
            list = JSONHelper.getList(obj, UserItem.class);
            if (list != null && list.size() > 0) {
                InputMethodUtils.hideInputMethod(input);
                adapter.setData(list);
            } else {
                showToast(getString(R.string.me_no_data));
            }
        }
    }

    @Override
    public void onChoosedFinish(UserItem user) {
        EventBus.getDefault().post(new EventStatusesType(user));
        //动态用上面,群里点击用下面
        Intent intent = new Intent();
        intent.putExtra("data", user);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

}
