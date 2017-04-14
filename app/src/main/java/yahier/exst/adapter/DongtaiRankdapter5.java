package yahier.exst.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.Rank1Item;
import com.stbl.stbl.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜 活跃度
 *
 * @author lenovo
 */
public class DongtaiRankdapter5 extends CommonAdapter {
    Context mContext;
    List<Rank1Item> list;

    public DongtaiRankdapter5(Context mContext2) {
        this.mContext = mContext2;
        list = new ArrayList<Rank1Item>();

    }

    public void setData(List<Rank1Item> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(List<Rank1Item> list){
        if(this.list == null)
            this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class Holder {
        TextView tvName, tvPeopleNum, tvRank, tvGroupName;
        ImageView imgUser, imgRank;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.dongtai_rank_list5_item, null);
            ho.imgRank = (ImageView) con.findViewById(R.id.imgRank);
            ho.imgUser = (ImageView) con.findViewById(R.id.imgUser);
            ho.tvName = (TextView) con.findViewById(R.id.tvName);
            ho.tvGroupName = (TextView) con.findViewById(R.id.tvGroupMasterName);
            ho.tvPeopleNum = (TextView) con.findViewById(R.id.tvNum);
            ho.tvRank = (TextView) con.findViewById(R.id.tvRank);
            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();

        final Rank1Item item = list.get(i);
        ho.tvName.setText(item.getName());
        ho.tvGroupName.setText(item.getEx1());
        ho.tvPeopleNum.setText(item.getPoints()+"人");
        PicassoUtil.load(mContext, item.getImgurl(), ho.imgUser,R.drawable.def_head);
        switch (i) {
            case 0:
                ho.tvRank.setVisibility(View.GONE);
                ho.imgRank.setImageResource(R.drawable.rank1_icon);
                ho.imgRank.setVisibility(View.VISIBLE);
                break;
            case 1:
                ho.imgRank.setVisibility(View.VISIBLE);
                ho.imgRank.setImageResource(R.drawable.rank2_icon);
                ho.tvRank.setVisibility(View.GONE);
                break;
            case 2:
                ho.tvRank.setVisibility(View.GONE);
                ho.imgRank.setVisibility(View.VISIBLE);
                ho.imgRank.setImageResource(R.drawable.rank3_icon);
                break;
            default:
                ho.imgRank.setVisibility(View.GONE);
                ho.tvRank.setVisibility(View.VISIBLE);
                ho.tvRank.setText(String.valueOf(i + 1));
                break;

        }

        ho.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId", item.getBusid());
                mContext.startActivity(intent);

            }
        });
        return con;
    }

}
