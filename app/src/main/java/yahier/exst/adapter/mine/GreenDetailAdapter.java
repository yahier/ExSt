package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Currency;
import com.stbl.stbl.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/16.
 */
public class GreenDetailAdapter extends BaseAdapter {

    private ArrayList<Currency> mList;
    private LayoutInflater mInflater;

    public GreenDetailAdapter(ArrayList<Currency> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_balance_detail, parent, false);
            holder.mTitleTv = (TextView) convertView.findViewById(R.id.tv_title);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);
            holder.mAmountTv = (TextView) convertView.findViewById(R.id.tv_amount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindViewHolder(holder, position);

        return convertView;
    }

    private void bindViewHolder(ViewHolder holder, int position) {
        Currency currency = mList.get(position);
        holder.mTitleTv.setText(currency.getSysremark());
        holder.mTimeTv.setText(DateUtil.getHelpTime(currency.getCreatetime()));
        if (currency.getOptype() == 0) {
            holder.mAmountTv.setText("+ " + ((int) currency.getAmount()) + "绿豆");
        } else {
            holder.mAmountTv.setText("- " + ((int) currency.getAmount()) + "绿豆");
        }

    }

    public static class ViewHolder {
        public TextView mTitleTv;
        public TextView mTimeTv;
        public TextView mAmountTv;
    }
}
