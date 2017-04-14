package yahier.exst.act.dongtai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.DongtaiRankdapter5;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Rank1Group;
import com.stbl.stbl.item.Rank1Item;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.database.DataCacheDB;
import com.stbl.stbl.widget.XListView;

import java.util.List;

public class DongtaiRankFragment5 extends Fragment implements FinalHttpCallback {
    XListView listView;
    Context mContext;
    DongtaiRankdapter5 adapter;
    TextView tvName, tvPeopleNum, tvRank, tvGroupName;
    View linMine;
    View lineMineRank;
    TextView tvTip;
    ImageView imgUser;
    private int page = 1;
    private boolean isRefresh = true;
    private DataCacheDB cacheDB = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dongtai_rank_tab5, null);
        imgUser = (ImageView) view.findViewById(R.id.imgUser);
        linMine = view.findViewById(R.id.linMine);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvGroupName = (TextView) view.findViewById(R.id.tvGroupMasterName);
        tvPeopleNum = (TextView) view.findViewById(R.id.tvNum);
        tvRank = (TextView) view.findViewById(R.id.tvMineRank);
        lineMineRank = view.findViewById(R.id.lineMineRank);
        mContext = view.getContext();
        cacheDB = new DataCacheDB(mContext);
        listView = (XListView) view.findViewById(R.id.dongtai_rank_list);
        tvTip = (TextView) view.findViewById(R.id.tvTip);
        adapter = new DongtaiRankdapter5(mContext);
        listView.setAdapter(adapter);
        listView.setEmptyView(view.findViewById(R.id.empty));
        listView.setOnXListViewListener(new XListView.OnXListViewListener() {
            @Override
            public void onRefresh(XListView v) {
                isRefresh = true;
                listView.setPullLoadEnable(true);
                page = 1;
                getData(page);
            }

            @Override
            public void onLoadMore(XListView v) {
                page += 1;
                isRefresh = false;
                getData(page);
            }
        });
        if (cacheDB != null)
            updateRankList(cacheDB.getRank5CacheJson());
        if (adapter.getCount() == 0)
            getData(page);
        return view;
    }

    public void getData(int page) {
        Params params = new Params();
        params.put("type", DongtaiRankAct.typeGroup);
        params.put("page", page);
        params.put("count", DongtaiRankFragment1.requestCount);
        new HttpEntity(mContext).commonPostData(Method.getRankData, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        if (listView != null) {
            listView.onLoadMoreComplete();
            listView.onRefreshComplete();
        }
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mContext, item.getErr().getMsg());
            }
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        if (methodName.equals(Method.getRankData)) {
            updateRankList(obj);
        }

    }

    private void updateRankList(String json){
        if (json == null || json.equals("") || json.equals("{}")) return;
        Rank1Group group = JSONHelper.getObject(json, Rank1Group.class);
        if (group ==null) return;
        List<Rank1Item> list = group.getRankings();
        if (list == null) return;
        if (isRefresh) {
            if (cacheDB != null){
                cacheDB.saveRank5CacheJson(json);
            }
            adapter.setData(list);
        } else {
            adapter.addData(list);
        }
        if (list != null && list.size() < DongtaiRankFragment1.requestCount) {
            listView.EndLoad();
        }
        final Rank1Item mineData = group.getMyrankings();
        if (mineData != null) {
            PicassoUtil.load(mContext, mineData.getImgurl(), imgUser);
            lineMineRank.setVisibility(View.VISIBLE);
            linMine.setVisibility(View.VISIBLE);
            tvName.setText(mineData.getName());
            tvGroupName.setText(mineData.getEx1());
            tvRank.setVisibility(View.VISIBLE);
            tvPeopleNum.setText(mineData.getPoints() + mContext.getString(R.string.rank_people));
            if (mineData.getRankings() == 0) {
                tvRank.setText(R.string.rank_no_rank);
            } else {
                tvRank.setText(String.valueOf(mineData.getRankings()));
            }
            linMine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", mineData.getBusid());
                    mContext.startActivity(intent);
                }
            });
        } else {
            linMine.setVisibility(View.GONE);
        }
    }
}
