package yahier.exst.adapter.msg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.im.DiscussionTeam;

import java.util.List;

/**
 * 收藏的讨论组列表适配器
 * Created by Administrator on 2016/8/31 0031.
 */

public class DiscussionListAdapter extends BaseAdapter {
    private Context mCtx;
    private List<DiscussionTeam> mData;//讨论组列表数据

    public DiscussionListAdapter(Context context, List<DiscussionTeam> mData) {
        this.mCtx = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public DiscussionTeam getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.im_discussion_collection_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvDiscussionName.setText(getItem(position).getGroupname());
        return convertView;
    }

    static class ViewHolder{
        public TextView tvDiscussionName; //讨论组名称

        public ViewHolder(View view) {
            tvDiscussionName = (TextView) view.findViewById(R.id.tv_discussion_name);
        }
    }
}
