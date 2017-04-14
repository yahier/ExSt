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
import com.stbl.stbl.item.redpacket.RpSendLogItem;
import com.stbl.stbl.util.DateUtil;

import java.util.List;

/**
 * 收到红包记录适配器
 * Created by Alan Chueng on 2016/12/26 0026.
 */

public class RpSendRecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<RpSendLogItem> rpSendList;

    public RpSendRecordAdapter(Context context, List<RpSendLogItem> rpSendList){
        this.mContext = context;
        this.rpSendList = rpSendList;
    }

    public void setDataChange(List<RpSendLogItem> rpSendList){
        this.rpSendList = rpSendList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rpSendList == null ? 0 : rpSendList.size();
    }

    @Override
    public RpSendLogItem getItem(int position) {
        return rpSendList.get(position);
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
        RpSendLogItem item = getItem(position);
        if (item != null) {
            if (item.getRedpackettype() == 1){
                holder.tv_money_to_user.setText(R.string.rp_random);
            }else{
                holder.tv_money_to_user.setText(R.string.rp_common);
            }
            holder.tv_time.setText(DateUtil.getMD(item.getSendtime()));
            holder.tv_item_money_amount.setText(item.getAmount()+mContext.getString(R.string.rp_yuan));
            String redpacketstatus = "";
            if (item.getRedpacketstatus() == 2){
                redpacketstatus = mContext.getString(R.string.rp_done);
            }else if (item.getRedpacketstatus() == 3){
                redpacketstatus = mContext.getString(R.string.rp_timeout);
            }
            holder.tv_item_money_msg.setText(redpacketstatus+item.getReceivedsize()+"/"+item.getRedpacketsize()+mContext.getString(R.string.rp_ge));
            holder.tv_item_money_msg.setTextColor(mContext.getResources().getColor(R.color.gray_aaa));
        }
        return convertView;
    }

    static class ViewHolder{
        TextView tv_money_to_user;
        TextView tv_time;
        TextView tv_item_money_amount;
        TextView tv_item_money_msg;

        public ViewHolder(View view){
            tv_money_to_user = (TextView) view.findViewById(R.id.tv_money_to_user);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_item_money_amount = (TextView) view.findViewById(R.id.tv_item_money_amount);
            tv_item_money_msg = (TextView) view.findViewById(R.id.tv_item_money_msg);
        }
    }
}
