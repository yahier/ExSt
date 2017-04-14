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
 * 排行榜 适配器
 *
 * @author lenovo
 */
public class DongtaiRankdapter1 extends CommonAdapter {
    Context mContext;
    List<Rank1Item> list;
    int type;
    public final static int typeHuoyue = 1;
    public final static int typeShouyi = 2;
    public final static int typeTuhao = 3;
    public final static int typeRenmai = 4;


    public DongtaiRankdapter1(Context mContext, int type) {
        this.mContext = mContext;
        this.type = type;
        list = new ArrayList<Rank1Item>();
    }


    public void setData(List<Rank1Item> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(List<Rank1Item> list) {
        if (this.list == null)
            this.list = new ArrayList<>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class Holder {
        TextView tvName,tvNum, tvTip;
        ImageView imgUser;
        TextView tvRank;
        ImageView ivSex;
        View vDivider;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.rank_item2_layout, null);
            ho.imgUser = (ImageView) con.findViewById(R.id.iv_User);
            ho.tvName = (TextView) con.findViewById(R.id.tv_Name);
            ho.tvTip = (TextView) con.findViewById(R.id.tv_Tip);
            ho.tvNum = (TextView) con.findViewById(R.id.tv_Num);
            ho.tvRank = (TextView) con.findViewById(R.id.tv_item_rank);
            ho.ivSex = (ImageView) con.findViewById(R.id.iv_Sex);
            ho.vDivider = con.findViewById(R.id.v_divider);
            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();

        final Rank1Item user = list.get(i);
        PicassoUtil.load(mContext, user.getImgurl(), ho.imgUser, R.drawable.def_head);
//        ho.tvName.setText(user.getName());
        ho.tvName.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getName() : user.getAlias());

        ho.ivSex.setVisibility(View.VISIBLE);
        if (Rank1Item.genderBoy.equals(user.getEx1())) {
            ho.ivSex.setImageResource(R.drawable.sex_man);
        } else if (Rank1Item.genderGirl.equals(user.getEx1())){
            ho.ivSex.setImageResource(R.drawable.sex_woman);
        }else{
            ho.ivSex.setVisibility(View.GONE);
        }

        switch (type) {
            case typeHuoyue:
                ho.tvTip.setText(R.string.rank_active_index);
                ho.tvNum.setText(user.getPoints());
                break;
            case typeShouyi:
                ho.tvTip.setText(R.string.rank_earnings_index);
                ho.tvNum.setText(user.getPoints());
                break;
//            case typeTuhao:
//                ho.tvTip.setText("本周消费");
//                ho.tvNum.setText(user.getPoints() + "元");
//                break;
            case typeRenmai:
                ho.tvTip.setText(R.string.rank_people_hub_index);
                ho.tvNum.setText(user.getPoints());
                break;
        }

        ho.tvRank.setText(String.valueOf(user.getRankings()));


        ho.imgUser.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              Intent intent = new Intent(mContext, TribeMainAct.class);
                                              intent.putExtra("userId", user.getBusid());
                                              mContext.startActivity(intent);

                                          }
                                      }

        );

        if (i == getCount()-1){
            ho.vDivider.setVisibility(View.GONE);
        }else{
            ho.vDivider.setVisibility(View.VISIBLE);
        }
        return con;
    }

}
