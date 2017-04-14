package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.AddFriendFromPhoneAct;
import com.stbl.stbl.adapter.NewStudentAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.PeopleResourceItem;
import com.stbl.stbl.item.UserCount;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OnFinalHttpCallback;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.widget.XListView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class MinePeopleResourceAct extends ThemeActivity implements View.OnClickListener, FinalHttpCallback {
    TextView tvCount1,tvCount2,tvCount3;
    XListView listNewStudent;
    NewStudentAdapter adapter;
    final int requestcount = 15;
    int page = 1;
    boolean isGetServerData  = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_people_resource_act);
        setLabel("人脉");

        listNewStudent = (XListView)findViewById(R.id.listNewStudent);
        listNewStudent.setPullRefreshEnable(false);
        listNewStudent.setPullLoadEnable(true);
        tvCount1 = (TextView)findViewById(R.id.tvCount1);
        tvCount2 = (TextView)findViewById(R.id.tvCount2);
        tvCount3 = (TextView)findViewById(R.id.tvCount3);
        findViewById(R.id.lin1).setOnClickListener(this);
        findViewById(R.id.lin2).setOnClickListener(this);
        findViewById(R.id.lin3).setOnClickListener(this);

        findViewById(R.id.btnShoutu).setOnClickListener(this);
        getData();

        listNewStudent.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {

            }

            @Override
            public void onLoadMore(XListView v) {
                page ++;
                getData();

            }
        });

        adapter = new NewStudentAdapter(true);
        listNewStudent.setAdapter(adapter);
    }


    void setNewStudent(boolean isGetNewStudent){
        if(isGetNewStudent){
            findViewById(R.id.linNoneNew).setVisibility(View.GONE);
            findViewById(R.id.linNew).setVisibility(View.VISIBLE);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.setListViewHeightBasedOnChildren(listNewStudent);
                }
            },100);
        }else {
            findViewById(R.id.linNoneNew).setVisibility(View.VISIBLE);
            findViewById(R.id.linNew).setVisibility(View.GONE);
        }


    }


    public void getData() {
        if(isGetServerData)return;
        isGetServerData = true;
        Params params = new Params();
        params.put("userid", app.getUserId());
        params.put("count", requestcount);
        params.put("page", page);
        new HttpEntity(this).commonPostData(Method.userGetPeopleResource, params, this);
    }

    @Override
    public void onClick(View v) {
        long userId = Long.valueOf(SharedToken.getUserId(this));
        Intent intent;
        switch (v.getId()) {
            case R.id.lin1:
                intent = new Intent(this, MineTudiListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                startActivity(intent);
                break;
            case R.id.lin2:
                intent = new Intent(this, MineAttenListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                startActivity(intent);
                break;
            case R.id.lin3:
                intent = new Intent(this, MineFansListAct.class);
                intent.putExtra(EXTRA.USER_ID, userId);
                startActivity(intent);
                break;
            case R.id.btnShoutu:
                enterAct(MyQrcodeActivity.class);
                break;

        }

    }


    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            setNewStudent(false);
            return;
        }
        String con = JSONHelper.getStringFromObject(item.getResult());
        if (methodName.equals(Method.userGetPeopleResource)) {
            isGetServerData = false;
            PeopleResourceItem itemBig = JSONHelper.getObject(con, PeopleResourceItem.class);
            if(itemBig==null){
                setNewStudent(false);
                return;
            }
            UserCount count = itemBig.getCountview();
            tvCount1.setText(count.getTudi_count() + "");
            tvCount2.setText(count.getAttention_count() + "");
            tvCount3.setText(count.getFans_count() + "");

            List<UserItem> listData = itemBig.getTudiview();
            if(page==1 && listData.size()==0){
                setNewStudent(false);
                listNewStudent.setPullLoadEnable(false);
                return;
            }

            adapter.addData(listData);
            if(listData.size()<requestcount){
                listNewStudent.setPullLoadEnable(false);
            }
            setNewStudent(true);
        }



    }




}