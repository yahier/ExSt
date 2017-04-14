package yahier.exst.adapter.redpacket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.redpacket.RpReceiveLogItem;
import com.stbl.stbl.util.DateUtil;

import java.util.List;

/**
 * 收到红包记录适配器
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RpReceiveRecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<RpReceiveLogItem> rpReceiveList;

    public RpReceiveRecordAdapter(Context context,List<RpReceiveLogItem> rpReceiveList){
        this.mContext = context;
        this.rpReceiveList = rpReceiveList;
    }

    public void setDataChange(List<RpReceiveLogItem> rpReceiveList){
        this.rpReceiveList = rpReceiveList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rpReceiveList == null ? 0 : rpReceiveList.size();
    }

    @Override
    public RpReceiveLogItem getItem(int position) {
        return rpReceiveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null ){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_rp_received_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        RpReceiveLogItem item = getItem(position);
        if (item != null) {
            holder.tv_money_to_user.setText(item.getRpusername());
            holder.tv_time.setText(DateUtil.getMD(item.getTradetime()));
            holder.tv_item_money_amount.setText(item.getAmount()+mContext.getString(R.string.rp_yuan));
            if (item.getRptype() == 1){ //红包类型（0：普通，1：拼手气）
                holder.iv_random_icon.setVisibility(View.VISIBLE);
                holder.iv_random_icon.setImageResource(R.drawable.icon_pin);
            }else{
                holder.iv_random_icon.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    static class ViewHolder{
        TextView tv_money_to_user;
        ImageView iv_random_icon;
        TextView tv_time;
        TextView tv_item_money_amount;

        public ViewHolder(View view){
            tv_money_to_user = (TextView) view.findViewById(R.id.tv_money_to_user);
            iv_random_icon = (ImageView) view.findViewById(R.id.iv_random_icon);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_item_money_amount = (TextView) view.findViewById(R.id.tv_item_money_amount);
        }
    }
}
