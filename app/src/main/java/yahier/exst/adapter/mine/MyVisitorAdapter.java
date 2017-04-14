package yahier.exst.adapter.mine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.FootVisitor;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/4/6.
 */
public class MyVisitorAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<FootVisitor> mList;

    public MyVisitorAdapter(ArrayList<FootVisitor> list) {
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
            convertView = mInflater.inflate(R.layout.item_my_visitor, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FootVisitor item = mList.get(position);
        UserItem user = item.getVisiteduser();
        ImageUtils.loadHead(user.getImgmiddleurl(), holder.mHeadIv);
        holder.mContentTv.setText(item.getRemark());
        holder.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));

        return convertView;
    }

    private class ViewHolder {
        RoundImageView mHeadIv;
        TextView mContentTv;
        TextView mTimeTv;

        public ViewHolder(View v) {
            mHeadIv = (RoundImageView) v.findViewById(R.id.iv_head);
            mContentTv = (TextView) v.findViewById(R.id.tv_content);
            mTimeTv = (TextView) v.findViewById(R.id.tv_time);
        }
    }

}
