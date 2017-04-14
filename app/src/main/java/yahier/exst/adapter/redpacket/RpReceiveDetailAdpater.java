package yahier.exst.adapter.redpacket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.redpacket.RedPacketDetailAct;
import com.stbl.stbl.item.redpacket.RpReceiveDetailItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;

import java.util.List;

/**
 * 红包详情的领取列表
 * Created by Alan Chueng on 2016/12/27 0027.
 */

public class RpReceiveDetailAdpater extends BaseAdapter {
    private Context mContext;
    private List<RpReceiveDetailItem> rpReceiveList;

    public RpReceiveDetailAdpater(Context context, List<RpReceiveDetailItem> rpReceiveList) {
        this.mContext = context;
        this.rpReceiveList = rpReceiveList;
    }

    @Override
    public int getCount() {
        return rpReceiveList == null ? 0 :rpReceiveList.size();
    }

    @Override
    public RpReceiveDetailItem getItem(int position) {
        return rpReceiveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<RpReceiveDetailItem> rpReceiveList) {
        this.rpReceiveList = rpReceiveList;
        notifyDataSetChanged();
    }

    public void addData(List<RpReceiveDetailItem> rpReceiveList) {
        this.rpReceiveList.addAll(rpReceiveList);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_rp_received_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       final RpReceiveDetailItem item = getItem(position);
        if (item != null) {
            holder.iv_avatar.setVisibility(View.VISIBLE);
            ImageUtils.loadCircleHead(item.getUsericon(), holder.iv_avatar);
            holder.tv_money_to_user.setText(item.getUsername());
            holder.tv_time.setText(DateUtil.getMdHm(String.valueOf(item.getTradetime())));
            holder.tv_item_money_amount.setText(item.getAmount() + "元");
            if (item.getIsmax() == RpReceiveDetailItem.ismaxYes) {
                holder.tv_item_money_msg.setVisibility(View.VISIBLE);
                holder.tv_item_money_msg.setText("手气最佳");
                holder.tv_item_money_msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sentiment,0,0,0);
                //holder.tv_item_money_msg.setCompoundDrawables(mContext.getResources().getDrawable(R.drawable.icon_sentiment), null, null, null);
            } else {
                holder.tv_item_money_msg.setVisibility(View.INVISIBLE);
            }
        }

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TribeMainAct.class);
                intent.putExtra("userId",item.getUserid());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_money_to_user;
        TextView tv_time;
        TextView tv_item_money_amount;
        TextView tv_item_money_msg;

        public ViewHolder(View view) {
            iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
            tv_money_to_user = (TextView) view.findViewById(R.id.tv_money_to_user);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_item_money_amount = (TextView) view.findViewById(R.id.tv_item_money_amount);
            tv_item_money_msg = (TextView) view.findViewById(R.id.tv_item_money_msg);
        }
    }
}
