package yahier.exst.adapter.dongtai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 小原图的adapter。现在用在了打赏栏。
 *
 * @author lenovo
 */
public class DongtaiDetailRewardAdapter extends CommonAdapter {

    Context mContext;
    List<StatusesReward> praisesList;

    public DongtaiDetailRewardAdapter(Context mContext) {
        this.mContext = mContext;
        praisesList = new ArrayList<StatusesReward>();
    }

    public DongtaiDetailRewardAdapter(Context mContext, List<StatusesReward> praisesList) {
        this.mContext = mContext;
        this.praisesList = praisesList;
    }

    @Override
    public int getCount() {
        if (praisesList == null)
            return 0;
        return praisesList.size() > 9 ? 9 : praisesList.size();
    }

    class Holder {
        ImageView img;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.item_dongtai_detail_reward, parent, false);
            ho.img = (ImageView) con.findViewById(R.id.item_iv);
            con.setTag(ho);
        } else {
            ho = (Holder) con.getTag();
        }
        String url = praisesList.get(i).getUser().getImgurl();
        ImageUtils.loadHead(url, ho.img);

        con.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent2 = new Intent(mContext, TribeMainAct.class);
                intent2.putExtra("userId", praisesList.get(i).getUser().getUserid());
                mContext.startActivity(intent2);

            }
        });
        return con;
    }

}
