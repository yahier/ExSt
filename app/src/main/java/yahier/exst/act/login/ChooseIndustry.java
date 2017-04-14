package yahier.exst.act.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cunoraz.tagview.OnTagClickListener;
import com.cunoraz.tagview.OnTagDeleteListener;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Industry;
import com.stbl.stbl.item.UserTag;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedToken;
import com.stbl.stbl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/8.
 */
public class ChooseIndustry extends ThemeActivity implements FinalHttpCallback {
    ListView list;
    TagView tagGroup;
    SuperTagsAdapter adapter;
    int requestTag;
    public final static int requestTagRegister = 100;
    public final static int requestTagSetting = 101;
    int indexGroup = 0;
    com.stbl.stbl.item.Industry industryChild;
    UserTag userTag; //以前选择的


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_choose_industry);
        setLabel(R.string.select_industry);
        requestTag = getIntent().getIntExtra("requestTag", requestTagRegister);
        userTag = (UserTag) getIntent().getSerializableExtra("userTag");
        RegisterActList.add(this);
        tagGroup = (TagView) findViewById(R.id.tagGroup);
        list = (ListView) findViewById(R.id.list);
        switch(requestTag) {
            case requestTagRegister:
                //pass
                break;
            case requestTagSetting:
                setRightText(R.string.finish, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (industryChild == null) {
                            ToastUtil.showToast(R.string.please_select_industry);
                            return;
                        }
                        updateIndustry();
                    }
                });
                break;
        }


        tagGroup.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(com.cunoraz.tagview.Tag tag, int position) {
                industryChild = industryParent.getNodes().get(position);
                Intent intent;
                switch(requestTag) {
                    case requestTagRegister:
                        intent = new Intent(ChooseIndustry.this, MasterRecommendAct.class);
                        intent.putExtra("professionsid", industryChild.getValue());
                        startActivity(intent);
                        finish();
                        break;
                    case requestTagSetting:
                        break;
                }


            }
        });

        //set delete listener
        tagGroup.setOnTagDeleteListener(new OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final com.cunoraz.tagview.TagView view, final com.cunoraz.tagview.Tag tag, final int position) {
            }
        });
        adapter = new SuperTagsAdapter(this);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                indexChild = -1;
                indexGroup = i;
                adapter.selectItem(i);
                industryChild = null;

            }
        });
        //getIndustry();
        getAllProfessionalAndIndustry();
    }


    //更新
    void updateIndustry() {
        Params params = new Params();
        params.put("tradesid", industryParent.getValue());
        params.put("professionsid", industryChild.getValue());
        new HttpEntity(this).commonPostData(Method.tagUpdate, params, this);
    }


    com.stbl.stbl.item.Industry industryParent;

    void refreshJos(Industry industryParent) {
        List<Tag> tag = tagGroup.getTags();
        LogUtil.logE("size:" + tag.size());
        int size = tag.size();
        for (int i = 0; i < size; i++) {
            tagGroup.remove(0);
        }

        size = industryParent.getNodes().size();
        if (size > 0) {
            this.industryParent = industryParent;
            String[] jobs = new String[size];
            for (int i = 0; i < size; i++) {
                jobs[i] = industryParent.getNodes().get(i).getTitle();
            }
            tagGroup.addTags(jobs);
            tagGroup.setSelected(indexChild);
        }


    }



    int indexChild = -1;
    //设置之前的选中状态
    private void setPreSelected(){
        if(userTag==null)return;
        int indexParent = 0;


        List<Industry> tempChilds = new ArrayList();
        for(int i=0;i<listAllIndustry.size();i++){
            if(userTag.getTradesid() == listAllIndustry.get(i).getValue()){
                indexParent = i;
                tempChilds = listAllIndustry.get(i).getNodes();
                break;
            }
        }

        for(int i=0;i<tempChilds.size();i++){
            if(userTag.getProfessionsid() == tempChilds.get(i).getValue()){
                indexChild = i;
                break;
            }
        }



      adapter.selectItem(indexParent);






    }
    private void getAllProfessionalAndIndustry() {
        Params params = new Params();
        params.put("refreshtoken", SharedToken.getRefreshToken(this));
        new HttpEntity(this).commonPostData(Method.getAllIndustrys, params, this);
    }


    List<Industry> listAllIndustry;

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(item.getErr().getMsg());
            return;
        }
        String con = JSONHelper.getStringFromObject(item.getResult());
        switch(methodName) {
            case Method.getAllIndustrys:
                listAllIndustry = JSONHelper.getList(con, Industry.class);
                LogUtil.logE("listTrades size", listAllIndustry.size());

                adapter.setData(listAllIndustry);
                if (listAllIndustry.size() > 0){
                    refreshJos(listAllIndustry.get(0));
                    setPreSelected();
                }
                break;
            case Method.tagUpdate:
                ToastUtil.showToast(R.string.update_success);
                Intent intent = getIntent();
                UserTag tag = new UserTag();
                tag.setTradesid(industryParent.getValue());
                tag.setTradesname(industryParent.getTitle());
                tag.setProfessionsid(industryChild.getValue());
                tag.setProfessionsname(industryChild.getTitle());
                intent.putExtra("tag", tag);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;

        }

    }


    public class SuperTagsAdapter extends CommonAdapter {
        Context mContext;
        int selectedIndex = 0;
        List<com.stbl.stbl.item.Industry> list;

        public SuperTagsAdapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<>();

        }




        void selectItem(int index) {
            this.selectedIndex = index;
            notifyDataSetChanged();
            refreshJos(listAllIndustry.get(index));


        }

        void setData(List<com.stbl.stbl.item.Industry> list) {
            this.list = list;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        public com.stbl.stbl.item.Industry getItem(int position) {
            return list.get(position);
        }

        class Holder {
            TextView name;

        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.register_industry_item, null);
                ho.name = (TextView) con.findViewById(R.id.item);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            com.stbl.stbl.item.Industry tag = list.get(i);
            ho.name.setText(tag.getTitle());
            if (i == selectedIndex) {
                con.setBackgroundColor(Color.parseColor("#ffffff"));
            } else {
                con.setBackgroundColor(Color.parseColor("#F7F7F7"));
            }
            return con;
        }


    }

}
