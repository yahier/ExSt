package yahier.exst.adapter.msg;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.Locale;

public class ContactsSortAdapter extends BaseAdapter implements SectionIndexer,
        OnClickListener {

    private ArrayList<SortModel> mList;
    private LayoutInflater mInflater;

    private IContactsSortAdapter mInterface;

    public ContactsSortAdapter(ArrayList<SortModel> list) {
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
            mList = new ArrayList<SortModel>();
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
            convertView = mInflater.inflate(R.layout.item_contacts, parent,
                    false);
            holder.mItemLayout = (RelativeLayout) convertView
                    .findViewById(R.id.layout_item);
            holder.mIndexTv = (TextView) convertView
                    .findViewById(R.id.tv_index);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mPhoneTv = (TextView) convertView
                    .findViewById(R.id.tv_phone);
            holder.mAddTv = (TextView) convertView.findViewById(R.id.tv_add);
            holder.mInviteTv = (TextView) convertView
                    .findViewById(R.id.tv_invite);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SortModel model = mList.get(position);

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.mIndexTv.setVisibility(View.VISIBLE);
            holder.mIndexTv.setText(model.sortLetters);
        } else {
            holder.mIndexTv.setVisibility(View.GONE);
        }

        holder.mPhoneTv.setText(model.number);

        UserItem user = model.user;
        if (user != null && user.getUserid() != 0) {
            ImageUtils.loadCircleHead(user.getImgmiddleurl(), holder.mHeadIv);
            holder.mNameTv.setText(model.name + "(" + user.getNickname() + ")");
            holder.mAddTv.setVisibility(View.VISIBLE);
            holder.mInviteTv.setVisibility(View.GONE);
            if (Relation.isFriend(user.getRelationflag())) {
                holder.mAddTv.setText(R.string.added_friend);
                holder.mAddTv.setTextColor(0xff969696);
                holder.mAddTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.mAddTv.setBackgroundDrawable(null);
                holder.mAddTv.setEnabled(false);
            } else {
                holder.mAddTv.setText(R.string.plus_friend);
                holder.mAddTv.setTextColor(0xffff6c6c);
                holder.mAddTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_add, 0, 0, 0);
                holder.mAddTv.setBackgroundResource(R.drawable.red_line_bg);
                holder.mAddTv.setEnabled(true);
            }
            holder.mItemLayout.setTag(position);
            holder.mItemLayout.setOnClickListener(this);
        } else {
            holder.mNameTv.setText(model.name);
            holder.mHeadIv.setImageResource(R.drawable.phone_contact_no_register);
            holder.mAddTv.setVisibility(View.GONE);
            holder.mInviteTv.setVisibility(View.VISIBLE);
            holder.mItemLayout.setOnClickListener(null);
        }

        holder.mAddTv.setTag(position);
        holder.mAddTv.setOnClickListener(this);
        holder.mInviteTv.setTag(position);
        holder.mInviteTv.setOnClickListener(this);

        return convertView;
    }

    public static class ViewHolder {
        public RelativeLayout mItemLayout;
        public TextView mIndexTv;
        public RoundImageView mHeadIv;
        public TextView mNameTv;
        public TextView mPhoneTv;
        public TextView mAddTv;
        public TextView mInviteTv;
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
                    mInterface.onItemClick(position);
                }
                break;
            case R.id.tv_add:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onAddFriend(mList.get(position));
                }
                break;
            case R.id.tv_invite:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onInvite(mList.get(position));
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(IContactsSortAdapter i) {
        mInterface = i;
    }

    public interface IContactsSortAdapter {
        void onItemClick(int position);

        void onAddFriend(SortModel user);

        void onInvite(SortModel user);
    }

}