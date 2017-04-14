package yahier.exst.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/18.
 */
public class DialogStringAdapter extends BaseAdapter {

    private ArrayList<String> mList;
    private LayoutInflater mInflater;

    public DialogStringAdapter(ArrayList<String> list) {
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

            convertView = mInflater.inflate(
                    R.layout.item_action_sheet_dialog, parent, false);

            holder.tv = (TextView) convertView
                    .findViewById(R.id.tv);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String item = mList.get(position);
        if (position == 0) {
            if (getCount() > 1) {
                convertView.setBackgroundResource(R.drawable.selector_white_top_corner_btn);
            } else {
                convertView.setBackgroundResource(R.drawable.selector_white_btn);
            }
        } else if (position == mList.size() - 1) {
            convertView
                    .setBackgroundResource(R.drawable.selector_white_bottom_corner_btn);
        } else {
            convertView
                    .setBackgroundResource(R.drawable.selector_white_no_corner_btn);
        }
        holder.tv.setText(item);
        return convertView;
    }

    private static class ViewHolder {
        TextView tv;
    }
}
