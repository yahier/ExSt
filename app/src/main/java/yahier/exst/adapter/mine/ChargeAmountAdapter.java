package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.ChargeAmount;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/13.
 */
public class ChargeAmountAdapter extends BaseAdapter {

    private ArrayList<ChargeAmount> mList;
    private ChargeAmount mSelectedAmount;
    private LayoutInflater mInflater;

    public ChargeAmountAdapter(ArrayList<ChargeAmount> list) {
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
            convertView = mInflater.inflate(R.layout.item_charge_amount, parent, false);
            holder.mBeanAmount = (TextView) convertView.findViewById(R.id.tv_bean_amount);
            holder.mRmbAmount = (TextView) convertView.findViewById(R.id.tv_rmb_amount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChargeAmount amount = mList.get(position);
        holder.mBeanAmount.setText(amount.getEx2() + "");
        holder.mRmbAmount.setText(amount.getEx1() + "元");

        convertView.setBackgroundResource(amount == mSelectedAmount ? R.drawable.bg_charge_amount_selected : R.drawable.bg_charge_amount);

        return convertView;
    }

    private static class ViewHolder {
        TextView mBeanAmount;
        TextView mRmbAmount;
    }

    public void init() {
        if (mList.size() > 0) {
            mSelectedAmount = mList.get(0);
            notifyDataSetChanged();
        }
    }

    public void toggleSelect(int position) {
        ChargeAmount amount = mList.get(position);
        if (amount != mSelectedAmount) {
            mSelectedAmount = amount;
            notifyDataSetChanged();
        }
    }
}
