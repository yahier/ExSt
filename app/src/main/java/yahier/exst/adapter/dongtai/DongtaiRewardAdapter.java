package yahier.exst.adapter.dongtai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.StatusesReward;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * 动态详情打赏列表适配器
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiRewardAdapter extends BaseAdapter {

    private ArrayList<StatusesReward> mList;
    private LayoutInflater mInflater;

    private AdapterInterface mInterface;

    public DongtaiRewardAdapter(ArrayList<StatusesReward> list) {
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
            convertView = mInflater.inflate(R.layout.item_dongtai_reward, parent, false);
            holder.mHeadIv = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.mNickTv = (TextView) convertView.findViewById(R.id.tv_nick);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StatusesReward item = mList.get(position);
        UserItem user = item.getUser();
        ImageUtils.loadHead(user.getImgurl(), holder.mHeadIv);
        holder.mNickTv.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//        holder.mNickTv.setText(user.getNickname());
        holder.mTimeTv.setText(DateUtil.getTimeDifferenceOfSecond(item.getRewardtime()));

        holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onClickHead(item);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        RoundImageView mHeadIv;
        TextView mNickTv;
        TextView mTimeTv;
    }

    public void setInterface(AdapterInterface i) {
        mInterface = i;
    }

    public interface AdapterInterface {
        void onClickHead(StatusesReward item);
    }


}
