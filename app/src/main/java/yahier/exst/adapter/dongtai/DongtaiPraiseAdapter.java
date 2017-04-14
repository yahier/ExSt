package yahier.exst.adapter.dongtai;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.PraiseItem;
import com.stbl.stbl.item.UserInfo;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * 动态详情点赞列表适配器
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiPraiseAdapter extends BaseAdapter {

    private ArrayList<PraiseItem> mList;
    private LayoutInflater mInflater;

    public DongtaiPraiseAdapter(ArrayList<PraiseItem> list) {
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
            convertView = mInflater.inflate(R.layout.item_dongtai_praise, parent, false);
            holder.mHeadIv = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.mNickTv = (TextView) convertView.findViewById(R.id.tv_nick);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PraiseItem item = mList.get(position);
        UserInfo user = item.getUser();
        ImageUtils.loadHead(user.getImgurl(), holder.mHeadIv);
        holder.mNickTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
//        holder.mNickTv.setText(user.getNickname());
        holder.mTimeTv.setText(DateUtil.getHmOrMdHm(String.valueOf(item.getPraisetime())));

        return convertView;
    }

    private static class ViewHolder {
        RoundImageView mHeadIv;
        TextView mNickTv;
        TextView mTimeTv;
    }


}
