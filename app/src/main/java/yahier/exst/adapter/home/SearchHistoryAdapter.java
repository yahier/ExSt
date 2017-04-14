package yahier.exst.adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/12.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    private ArrayList<String> mList;
    private LayoutInflater mInflater;

    public SearchHistoryAdapter(ArrayList<String> list) {
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
            convertView = mInflater.inflate(R.layout.item_search_history, parent, false);
            holder.mKeywordTv = (TextView) convertView.findViewById(R.id.tv_keyword);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String keyword = mList.get(position);
        holder.mKeywordTv.setText(keyword);

        return convertView;
    }

    public static class ViewHolder {
        TextView mKeywordTv;
    }
}
