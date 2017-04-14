package yahier.exst.adapter.mine;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.AttendCon;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

public class FansListAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<AttendCon> mList;
    private LayoutInflater mInflater;

    private IFansListAdapter mInterface;
    private boolean isSelf = false;

    public FansListAdapter(ArrayList<AttendCon> list, boolean isSelf) {
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mList = list;
        this.isSelf = isSelf;
    }

    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_fans,
                    parent, false);
            holder.mIndexTv = (TextView) convertView
                    .findViewById(R.id.tv_index);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mRelationTv = (TextView) convertView
                    .findViewById(R.id.tv_relation);
            holder.mAddFriendTv = (TextView) convertView
                    .findViewById(R.id.tv_add);
            holder.mTvFriend = (TextView) convertView.findViewById(R.id.tv_friend);

            holder.mAgeTv = (TextView) convertView.findViewById(R.id.tv_age);
            holder.mLocationTv = (TextView) convertView
                    .findViewById(R.id.tv_location);
            holder.ivGender = (ImageView) convertView.findViewById(R.id.iv_gender);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindViewHolder(position, holder);

        return convertView;
    }

    private void bindViewHolder(int position, ViewHolder holder) {
        AttendCon item = mList.get(position);
        UserItem user = item.getUser();

        ImageUtils.loadCircleHead(user.getImgmiddleurl(), holder.mHeadIv);
        holder.mNameTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
//        holder.mNameTv.setText(user.getNickname());

        int relationFlag = user.getRelationflag();
        holder.mRelationTv.setText(StringUtil.getRelationString(relationFlag));
        if (Relation.isMaster(relationFlag)) {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_relation_master, 0, 0, 0);
        } else if (Relation.isStu(relationFlag)) {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_relation_student, 0, 0, 0);
        } else {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                    0);
        }
        if (user.getAge() <= 0) {
            holder.mAgeTv.setVisibility(View.GONE);
        } else {
            holder.mAgeTv.setVisibility(View.VISIBLE);
            holder.mAgeTv.setText(String.format(MyApplication.getContext().getString(R.string.im_age), user.getAge()));
        }
        holder.ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
            holder.ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
            holder.ivGender.setImageResource(R.drawable.icon_female);
        } else {
            holder.ivGender.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(user.getCityname())) {
            holder.mLocationTv.setVisibility(View.GONE);
        } else {
            holder.mLocationTv.setVisibility(View.VISIBLE);
            holder.mLocationTv.setText(user.getCityname());
        }

        if (Relation.isFocus(item.getIsattention())) {
            holder.mAddFriendTv.setVisibility(View.GONE);
            holder.mAddFriendTv.setOnClickListener(null);
            holder.mTvFriend.setVisibility(View.VISIBLE);
        } else {
            holder.mAddFriendTv.setVisibility(View.VISIBLE);
            holder.mAddFriendTv.setTag(position);
            holder.mAddFriendTv.setOnClickListener(this);
            holder.mTvFriend.setVisibility(View.GONE);
        }
        if (!isSelf) {
            holder.mAddFriendTv.setVisibility(View.GONE);
            holder.mTvFriend.setVisibility(View.GONE);
            holder.mRelationTv.setVisibility(View.GONE);
        }
    }

    private static class ViewHolder {
        public TextView mIndexTv;
        public RoundImageView mHeadIv;
        public TextView mNameTv;
        public TextView mRelationTv;
        public TextView mAddFriendTv;
        public TextView mTvFriend;

        public TextView mAgeTv;
        public TextView mLocationTv;
        public ImageView ivGender;//性别
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tv_add:
                int position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onFocusFriend(position);
                }
                break;
        }
    }

    public void setInterface(IFansListAdapter i) {
        mInterface = i;
    }

    public interface IFansListAdapter {

        void onFocusFriend(int position);

    }

}
