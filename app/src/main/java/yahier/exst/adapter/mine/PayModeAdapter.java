package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.PayMode;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/13.
 */
public class PayModeAdapter extends BaseAdapter {

    private ArrayList<PayMode> mList;
    private PayMode mSelectedMode;
    private LayoutInflater mInflater;

    public PayModeAdapter(ArrayList<PayMode> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_pay_mode, parent, false);
            holder.mPayTv = (TextView) convertView.findViewById(R.id.tv_pay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PayMode item = mList.get(position);
        holder.mPayTv.setText(item.getName());

        convertView.setBackgroundResource(item == mSelectedMode ? R.drawable.bg_charge_amount_selected : R.drawable.bg_charge_amount);

        return convertView;
    }

    private static class ViewHolder {
        TextView mPayTv;
    }

    public void init() {
        if (mList.size() > 0) {
            mSelectedMode = mList.get(0);
            notifyDataSetChanged();
        }
    }

    public void toggleSelect(int position) {
        PayMode mode = mList.get(position);
        if (mode != mSelectedMode) {
            mSelectedMode = mode;
            notifyDataSetChanged();
        }
    }
}
