package yahier.exst.act.dongtai;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.DongtaiRankdapter1;
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
import com.stbl.stbl.widget.RoundImageView;
import com.stbl.stbl.widget.XListView;

import java.util.ArrayList;
import java.util.List;
/**人脉排行榜*/
public class DongtaiRankFragment4 extends Fragment implements FinalHttpCallback {
    XListView listView;
    Context mContext;
    DongtaiRankdapter1 adapter;
    ImageView imgUser;
    //名字，活跃指数，排名
    TextView tvName, tvNum, tvRank;
    ImageView ivSex;//性别图标
    View linMine;
    TextView tvTip;
    private int page = 1;
    private boolean isRefresh = true;
    private DataCacheDB cacheDB = null;
    private View headView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dongtai_rank_tab1, null);
        mContext = view.getContext();
        cacheDB = new DataCacheDB(mContext);
        linMine = view.findViewById(R.id.linMine);
        listView = (XListView) view.findViewById(R.id.dongtai_rank_list);
        imgUser = (ImageView) view.findViewById(R.id.iv_User);
        tvName = (TextView) view.findViewById(R.id.tv_Name);
        tvNum = (TextView) view.findViewById(R.id.tv_Num);
        ivSex = (ImageView) view.findViewById(R.id.iv_Sex);
        tvRank = (TextView) view.findViewById(R.id.tv_MineRank);
        tvTip = (TextView) view.findViewById(R.id.tv_Tip);
        adapter = new DongtaiRankdapter1(mContext, DongtaiRankdapter1.typeRenmai);
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
            updateRankList(cacheDB.getRank4CacheJson());
        getData(page);
        return view;
    }

    public void getData(int page) {
        Params params = new Params();
        params.put("type", DongtaiRankAct.typeRenmai);
        params.put("page", page);
        params.put("count", DongtaiRankFragment1.requestCount);
        new HttpEntity(mContext).commonPostData(Method.getRankDataV1, params, this);
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
        if (methodName.equals(Method.getRankDataV1)) {
            updateRankList(obj);
        }

    }

    private void addHeadView(List<Rank1Item> list){
        if (list == null || list.size() < 3) return;
        if (headView != null) listView.removeHeaderView(headView);
        headView = LayoutInflater.from(mContext).inflate(R.layout.rank_headview_layout,null);
        RoundImageView ivUserImgTwo = (RoundImageView) headView.findViewById(R.id.iv_user_img_two);
        ImageView ivSexTwo = (ImageView) headView.findViewById(R.id.iv_sex_two);
        TextView tvNameTwo = (TextView) headView.findViewById(R.id.tv_name_two);
        TextView tvRankTipsTwo = (TextView) headView.findViewById(R.id.tv_rank_tips_two);
        TextView tvRankTipsNumTwo = (TextView) headView.findViewById(R.id.tv_rank_tips_num_two);

        RoundImageView ivUserImgOne = (RoundImageView) headView.findViewById(R.id.iv_user_img_one);
        ImageView ivSexOne = (ImageView) headView.findViewById(R.id.iv_sex_one);
        TextView tvNameOne = (TextView) headView.findViewById(R.id.tv_name_one);
        TextView tvRankTipsOne = (TextView) headView.findViewById(R.id.tv_rank_tips_one);
        TextView tvRankTipsNumOne = (TextView) headView.findViewById(R.id.tv_rank_tips_num_one);

        RoundImageView ivUserImgThree = (RoundImageView) headView.findViewById(R.id.iv_user_img_three);
        ImageView ivSexThree = (ImageView) headView.findViewById(R.id.iv_sex_three);
        TextView tvNameThree = (TextView) headView.findViewById(R.id.tv_name_three);
        TextView tvRankTipsThree = (TextView) headView.findViewById(R.id.tv_rank_tips_three);
        TextView tvRankTipsNumThree = (TextView) headView.findViewById(R.id.tv_rank_tips_num_three);

        final Rank1Item itemOne = list.get(0);
        final Rank1Item itemTwo = list.get(1);
        final Rank1Item itemThree = list.get(2);

        PicassoUtil.load(mContext,itemTwo.getImgurl(),ivUserImgTwo,R.drawable.def_head);
        PicassoUtil.load(mContext,itemOne.getImgurl(),ivUserImgOne,R.drawable.def_head);
        PicassoUtil.load(mContext,itemThree.getImgurl(),ivUserImgThree,R.drawable.def_head);
        ivUserImgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", itemTwo.getBusid());
                mContext.startActivity(intent);
            }
        });
        ivUserImgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", itemOne.getBusid());
                mContext.startActivity(intent);
            }
        });
        ivUserImgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", itemThree.getBusid());
                mContext.startActivity(intent);
            }
        });

        if (Rank1Item.genderBoy.equals(itemTwo.getEx1())){
            ivSexTwo.setImageResource(R.drawable.sex_man);
        }else if (Rank1Item.genderGirl.equals(itemTwo.getEx1())){
            ivSexTwo.setImageResource(R.drawable.sex_woman);
        }else{
            ivSexTwo.setVisibility(View.GONE);
        }
        if (Rank1Item.genderBoy.equals(itemOne.getEx1())){
            ivSexOne.setImageResource(R.drawable.sex_man);
        }else if (Rank1Item.genderGirl.equals(itemOne.getEx1())){
            ivSexOne.setImageResource(R.drawable.sex_woman);
        }else{
            ivSexOne.setVisibility(View.GONE);
        }
        if (Rank1Item.genderBoy.equals(itemThree.getEx1())){
            ivSexThree.setImageResource(R.drawable.sex_man);
        }else if (Rank1Item.genderGirl.equals(itemThree.getEx1())){
            ivSexThree.setImageResource(R.drawable.sex_woman);
        }else{
            ivSexThree.setVisibility(View.GONE);
        }

//        tvNameTwo.setText(itemTwo.getName());
//        tvNameOne.setText(itemOne.getName());
//        tvNameThree.setText(itemThree.getName());
        tvNameTwo.setText(itemTwo.getAlias() == null || itemTwo.getAlias().equals("") ? itemTwo.getName() : itemTwo.getAlias());
        tvNameOne.setText(itemOne.getAlias() == null || itemOne.getAlias().equals("") ? itemOne.getName() : itemOne.getAlias());
        tvNameThree.setText(itemThree.getAlias() == null || itemThree.getAlias().equals("") ? itemThree.getName() : itemThree.getAlias());

        tvRankTipsTwo.setText(R.string.rank_people_hub_index);
        tvRankTipsOne.setText(R.string.rank_people_hub_index);
        tvRankTipsThree.setText(R.string.rank_people_hub_index);

        tvRankTipsNumTwo.setText(itemTwo.getPoints());
        tvRankTipsNumOne.setText(itemOne.getPoints());
        tvRankTipsNumThree.setText(itemThree.getPoints());

        listView.addHeaderView(headView);
    }

    private void updateRankList(String json){
        if (json == null || json.equals("") || json.equals("{}")) return;
        Rank1Group group = JSONHelper.getObject(json, Rank1Group.class);
        if (group ==null) return;
        List<Rank1Item> list = group.getRankings();
        if (list == null) return;
        if (isRefresh) {
            if (cacheDB != null)
                cacheDB.saveRank4CacheJson(json);
            if (list.size() >= 3){
                List<Rank1Item> topThreeList = new ArrayList<>();
                topThreeList.add(list.get(0));
                topThreeList.add(list.get(1));
                topThreeList.add(list.get(2));
                list.remove(2);
                list.remove(1);
                list.remove(0);
                addHeadView(topThreeList);
            }
            adapter.setData(list);
        } else {
            adapter.addData(list);
        }
        int requestCount2 = DongtaiRankFragment1.requestCount;
        if (isRefresh){
            requestCount2 = DongtaiRankFragment1.requestCount - 3;
        }
        if (list != null && list.size() < requestCount2) {
            listView.EndLoad();
        }
        final Rank1Item mineData = group.getMyrankings();
        if (mineData != null) {
            linMine.setVisibility(View.VISIBLE);
            PicassoUtil.load(mContext, mineData.getImgurl(), imgUser,R.drawable.def_head);
            tvName.setText(mineData.getName());

            tvTip.setText(R.string.rank_people_hub_index);
            tvNum.setText(mineData.getPoints());

            String rank = String.valueOf(mineData.getRankings());
            String ranks = String.format(mContext.getString(R.string.rank_di),mineData.getRankings());
            int index = ranks.indexOf(rank);
            SpannableString ss = new SpannableString(ranks);
            if (isAdded())
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),index,index + rank.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_red)),1,ranks.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvRank.setText(ss);

            ivSex.setVisibility(View.VISIBLE);
            if (Rank1Item.genderBoy.equals(mineData.getEx1())) {
                ivSex.setImageResource(R.drawable.sex_man);
            } else if (Rank1Item.genderGirl.equals(mineData.getEx1())){
                ivSex.setImageResource(R.drawable.sex_woman);
            }else{
                ivSex.setVisibility(View.GONE);
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
