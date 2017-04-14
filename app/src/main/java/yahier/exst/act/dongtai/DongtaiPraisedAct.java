package yahier.exst.act.dongtai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.DongtaiMainAdapter;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.PraiseItem;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.UserInfo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.XListView;
import com.stbl.stbl.widget.XListView.OnXListViewListener;

import java.util.ArrayList;
import java.util.List;

public class DongtaiPraisedAct extends ThemeActivity implements FinalHttpCallback, OnXListViewListener {
    XListView listView;
    long statusesid;
    int lastid;

    int loadType = 0;// 加载模式
    final int loadTypeBottom = 0;// 底部加载
    final int loadTypeTop = 1;// 顶部加载。清除以前数据
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dongtai_praised_act);
        setLabel("点赞的人");
        statusesid = ((Statuses) getIntent().getSerializableExtra(DongtaiMainAdapter.key)).getStatusesid();
        adapter = new Adapter(this);
        listView = (XListView) findViewById(R.id.list);
        listView.setOnXListViewListener(this);
        listView.setAdapter(adapter);
        getPraisedList();
    }

    void getPraisedList() {
        Params params = new Params();
        params.put("statusesid", statusesid);
        params.put("lastid", lastid);
        params.put("count", Statuses.requestCount);//
        new HttpEntity(this).commonPostData(Method.weiboGetPraiseList, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.weiboGetPraiseList:
                    listView.onLoadMoreComplete();
                    listView.onRefreshComplete();
                    break;
            }
            return;
        }
        if (methodName.equals(Method.weiboGetPraiseList)) {
            listView.onLoadMoreComplete();
            listView.onRefreshComplete();
            String obj = JSONHelper.getStringFromObject(item.getResult());
            List<PraiseItem> list = JSONHelper.getList(obj, PraiseItem.class);
            if (list.size() > 0) {
                lastid = list.get(list.size() - 1).getStatusesagreeid();
                if (loadType == loadTypeBottom) {
                    adapter.addData(list);
                    if (list.size() < Statuses.requestCount) {
                        listView.EndLoad();
                    }
                } else {
                    adapter.setData(list);
                }
            } else {
                listView.EndLoad();
            }
        }

    }

    @Override
    public void onRefresh(XListView v) {
        loadType = loadTypeTop;
        lastid = 0;
        getPraisedList();
    }

    @Override
    public void onLoadMore(XListView v) {
        loadType = loadTypeBottom;
        getPraisedList();

    }

    public class Adapter extends CommonAdapter {
        Context mContext;
        List<PraiseItem> list;

        public Adapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<PraiseItem>();
        }

        public void setData(List<PraiseItem> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addData(List<PraiseItem> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        class Holder {
            ImageView img;
            TextView name;
            TextView user_gender_age;
            TextView city;
        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.dongtai_praise_list_item, null);
                ho.img = (ImageView) con.findViewById(R.id.praise_img);
                ho.name = (TextView) con.findViewById(R.id.name);
                ho.user_gender_age = (TextView) con.findViewById(R.id.user_gender_age);
                ho.city = (TextView) con.findViewById(R.id.city);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            final UserInfo user = list.get(i).getUser();
            PicassoUtil.load(mContext, user.getImgurl(), ho.img);
            ho.name.setText(user.getNickname());
            ho.user_gender_age.setText(user.getAge() + "");
            if (user.getGender() == UserItem.gender_boy) {
                ho.user_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
            } else {
                ho.user_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
            }
            if (user.getCityname() != null) {
                ho.city.setText(user.getCityname());
            }

            con.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId",user.getUserid());
                    mContext.startActivity(intent);
                }
            });
            return con;
        }

    }
}
