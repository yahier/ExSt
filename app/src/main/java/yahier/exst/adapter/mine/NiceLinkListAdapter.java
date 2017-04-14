package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.LinkBean;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/3/16.
 */
public class NiceLinkListAdapter extends BaseAdapter {

    private ArrayList<LinkBean> mList;
    private LayoutInflater mInflater;

    public NiceLinkListAdapter(ArrayList<LinkBean> list) {
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
            convertView = mInflater.inflate(R.layout.item_nice_link_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setData(position);
        return convertView;
    }

    public class ViewHolder {
        ImageView mCoverIv;
        TextView mTitleTv;

        public ViewHolder(View v) {
            mCoverIv = (ImageView) v.findViewById(R.id.iv_cover);
            mTitleTv = (TextView) v.findViewById(R.id.tv_title);
        }

        public void setData(int position) {
            LinkBean item = mList.get(position);
            ImageUtils.loadIcon(item.getPiclarurl(), mCoverIv);
            mTitleTv.setText(item.getLinktitle());
        }

    }
}
