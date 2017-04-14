package yahier.exst.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.Res;

/**
 * Created by tnitf on 2016/7/31.
 */
public class ShareGridAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private String[] mNameArray = new String[]{Res.getString(R.string.me_weixin_friend),
            Res.getString(R.string.me_weixin_circle),
            Res.getString(R.string.me_qq_friend),
            Res.getString(R.string.me_qzone),
            Res.getString(R.string.me_sina_weibo),
            Res.getString(R.string.me_facebook),
            Res.getString(R.string.me_copy_link),
            Res.getString(R.string.me_more_share)};
    private int[] mResArray = new int[]{R.drawable.icon_share_wechat_friend, R.drawable.icon_share_wechat_moments,
            R.drawable.icon_share_qq_friend, R.drawable.icon_share_qzone, R.drawable.icon_share_weibo
            , R.drawable.icon_share_facebook, R.drawable.icon_share_copy_link, R.drawable.icon_share_more};

    public ShareGridAdapter() {
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mNameArray.length;
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
            convertView = mInflater.inflate(R.layout.item_share_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setData(position);
        return convertView;
    }

    private class ViewHolder {
        ImageView mIconIv;
        TextView mNameTv;

        public ViewHolder(View v) {
            mIconIv = (ImageView) v.findViewById(R.id.iv_icon);
            mNameTv = (TextView) v.findViewById(R.id.tv_name);
        }

        public void setData(int position) {
            String name = mNameArray[position];
            int resId = mResArray[position];
            mIconIv.setImageResource(resId);
            mNameTv.setText(name);
        }
    }
}
