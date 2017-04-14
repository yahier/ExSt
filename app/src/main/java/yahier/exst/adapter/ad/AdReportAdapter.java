package yahier.exst.adapter.ad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.ad.AdBusinessType;

import java.util.List;

/**
 * 广告举报类别
 */
public class AdReportAdapter extends BaseAdapter {

    private List<AdBusinessType> mList;
    private LayoutInflater mInflater;
    private int selectedIndex = 0;

    public AdReportAdapter(List<AdBusinessType> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    public AdBusinessType getSelected() {
        if (selectedIndex == -1) return null;
        else return mList.get(selectedIndex);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.ad_report_list_item, parent, false);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvItem.setText(mList.get(position).getName());

        if (selectedIndex == position) {
            holder.img.setImageResource(R.drawable.button_ad_sel);
        } else {
            holder.img.setImageResource(R.drawable.button_ad_nor);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView tvItem;
        ImageView img;
    }
}
