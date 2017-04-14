package yahier.exst.act.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.MasterPhotoAdapter;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventTypeCommon;
import com.stbl.stbl.item.MasterUser;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.item.UserCount;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * Created by lenovo on 2016/3/12.
 */
public class MasterRecommendAct extends ThemeActivity implements FinalHttpCallback {
    XListView listView;
    Adapter adapter;
    int page = 1;
    final int requestCount = 15;
    boolean isRefresh = true;
    int professionsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_recommend_act);
        setLabel(R.string.recommend_master);
        listView = (XListView) (findViewById(R.id.list));
        adapter = new Adapter(this);
        listView.setAdapter(adapter);
        professionsid = getIntent().getIntExtra("professionsid", 0);
        listView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                isRefresh = true;
                listView.setPullLoadEnable(true);
                page = 1;
                getData();
            }

            @Override
            public void onLoadMore(XListView v) {
                page += 1;
                isRefresh = false;
                getData();
            }
        });
        getData();
    }


    void getData() {
        WaitingDialog.show(this, "");
        Params params = new Params();
        params.put("count", requestCount);
        params.put("page", page);
        if (professionsid != 0) {
            params.put("professionsid", professionsid);
        }
        new HttpEntity(this).commonPostData(Method.getRecommendMasters, params, this);
    }


    public class Adapter extends CommonAdapter {
        Activity mContext;
        List<MasterUser> list;

        public Adapter(Activity mContext) {
            this.mContext = mContext;
            list = new ArrayList<MasterUser>();
        }

        public void setData(List<MasterUser> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void addData(List<MasterUser> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        class Holder {
            ImageView user_img;
            TextView name;
            TextView item_remark;
            GridView gridPhoto;
            TextView user_gender_age, user_city;
            TextView tvSelect;
            TextView tvCountFans, tvCountTudi;
            View tvPhotoTip;
        }

        @Override
        public View getView(final int i, View con, ViewGroup parent) {
            Holder ho = null;
            if (con == null) {
                ho = new Holder();
                con = LayoutInflater.from(mContext).inflate(R.layout.master_recommend_list_item, null);
                ho.gridPhoto = (GridView) con.findViewById(R.id.gridPhoto);
                ho.name = (TextView) con.findViewById(R.id.name);
                ho.user_img = (ImageView) con.findViewById(R.id.user_img);
                ho.user_gender_age = (TextView) con.findViewById(R.id.user_gender_age);
                ho.user_city = (TextView) con.findViewById(R.id.user_city);
                ho.tvSelect = (TextView) con.findViewById(R.id.tvSelect);
                ho.tvCountFans = (TextView) con.findViewById(R.id.tvCountFans);
                ho.tvCountTudi = (TextView) con.findViewById(R.id.tvCountTudi);
                ho.tvPhotoTip = con.findViewById(R.id.tvPhotoTip);
                con.setTag(ho);
            } else
                ho = (Holder) con.getTag();

            MasterUser master = list.get(i);

            final UserItem user = master.getUserview();
            ho.name.setText(user.getNickname());
            ho.user_gender_age.setText(user.getAge() + "");
            if (user.getGender() == UserItem.gender_boy) {
                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
                ho.user_gender_age.setBackgroundResource(R.drawable.shape_boy_bg);
            } else if (user.getGender() == UserItem.gender_girl) {
                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
                ho.user_gender_age.setBackgroundResource(R.drawable.shape_girl_bg);
            } else {
                ho.user_gender_age.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                ho.user_gender_age.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
            }
            PicassoUtil.load(mContext, user.getImgurl(), ho.user_img);
            //数目
            UserCount count = master.getCountview();
            ho.user_city.setText(user.getCityname());
            ho.tvCountFans.setText(count.getFans_count() + "");
            ho.tvCountTudi.setText(count.getTudi_count() + "");
            List<Photo> listPhoto = master.getPhotoview();
            if (listPhoto == null || listPhoto.size() == 0) {
                ho.tvPhotoTip.setVisibility(View.VISIBLE);
                ho.gridPhoto.setVisibility(View.GONE);
            } else {
                ho.gridPhoto.setVisibility(View.VISIBLE);
                ho.tvPhotoTip.setVisibility(View.GONE);
                ho.gridPhoto.setAdapter(new MasterPhotoAdapter(mContext, listPhoto));
                ViewUtils.setStatusesAdapterViewHeight(ho.gridPhoto, MasterPhotoAdapter.gridColumn);
            }


            ho.tvSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventTypeCommon typeEvent = new EventTypeCommon(user);
                    typeEvent.setType(EventTypeCommon.typeGetRecommendMaster);
                    EventBus.getDefault().post(typeEvent);
                    finish();
                }
            });
            return con;
        }

    }


    @Override
    public void parse(String methodName, String result) {
        listView.onLoadMoreComplete();
        listView.onRefreshComplete();
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(this, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.getRecommendMasters:
                List<MasterUser> list = JSONHelper.getList(obj, MasterUser.class);
                LogUtil.logE("" + list.size());
                if (isRefresh) {
                    adapter.setData(list);
                } else {
                    adapter.addData(list);
                }
                if (list != null && list.size() < requestCount) {
                    listView.EndLoad();
                }

                break;
        }


    }
}