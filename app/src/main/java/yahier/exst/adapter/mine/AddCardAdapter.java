package yahier.exst.adapter.mine;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.Locale;

public class AddCardAdapter extends BaseAdapter implements SectionIndexer,
        OnClickListener {

    private ArrayList<SortModel> mList;
    private LayoutInflater mInflater;

    private ISingleSelectFriendAdapter mInterface;

    public AddCardAdapter(ArrayList<SortModel> list) {
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mList = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(ArrayList<SortModel> list) {
        if (list == null) {
            mList = new ArrayList<>();
        } else {
            mList = list;
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mList.size();
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
            convertView = mInflater.inflate(R.layout.item_add_card,
                    parent, false);
            holder.mItemLayout = (RelativeLayout) convertView
                    .findViewById(R.id.layout_item);
            holder.mIndexTv = (TextView) convertView
                    .findViewById(R.id.tv_index);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mRelationTv = (TextView) convertView
                    .findViewById(R.id.tv_relation);
            holder.mAgeTv = (TextView) convertView.findViewById(R.id.tv_age);
            holder.ivGender = (ImageView) convertView.findViewById(R.id.iv_gender);
            holder.mLocationTv = (TextView) convertView
                    .findViewById(R.id.tv_location);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SortModel model = mList.get(position);
        UserItem user = model.user;
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.mIndexTv.setVisibility(View.VISIBLE);
            holder.mIndexTv.setText(model.sortLetters);
        } else {
            holder.mIndexTv.setVisibility(View.GONE);
        }

        ImageUtils.loadCircleHead(user.getImgmiddleurl(), holder.mHeadIv);
        holder.mNameTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
        holder.mRelationTv.setText(StringUtil.getRelationString(user
                .getRelationflag()));

        if (Relation.isMaster(user.getRelationflag())) {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_master, 0, 0, 0);
        } else if (Relation.isStu(user.getRelationflag())) {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_relation_student, 0, 0, 0);
        } else {
            holder.mRelationTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
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

        holder.mItemLayout.setTag(position);
        holder.mItemLayout.setOnClickListener(this);

        return convertView;
    }

    public static class ViewHolder {
        RelativeLayout mItemLayout;
        TextView mIndexTv;
        RoundImageView mHeadIv;
        TextView mNameTv;
        TextView mRelationTv;
        TextView mAgeTv;
        ImageView ivGender;
        TextView mLocationTv;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).sortLetters.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.layout_item:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onItemClick(mList.get(position));
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(ISingleSelectFriendAdapter i) {
        mInterface = i;
    }

    public interface ISingleSelectFriendAdapter {
        void onItemClick(SortModel model);
    }

}