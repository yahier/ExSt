package yahier.exst.adapter.msg;

import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Locale;

public class SelectFriendAdapter extends BaseAdapter implements SectionIndexer,
        OnClickListener {

    private ArrayList<SortModel> mList;
    private ArrayList<SortModel> mSelectedList;
    private SparseBooleanArray mIndexSelectArray;
    private SparseIntArray mIndexCountArray;

    private LayoutInflater mInflater;

    private ISelectFriendAdapter mInterface;

    private boolean mShowIndexSelect = false;

    public SelectFriendAdapter(ArrayList<SortModel> list) {
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mList = list;
        mSelectedList = new ArrayList<>();
        listMember = new ArrayList<>();
        mIndexSelectArray = new SparseBooleanArray();
        mIndexCountArray = new SparseIntArray();
    }

    List<UserItem> listMember;

    public void setMembers(List<UserItem> list) {
        if (list != null) {
            this.listMember = list;
        }
    }

    public List<UserItem> getMembers() {
        return listMember;
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
            convertView = mInflater.inflate(R.layout.item_contact_select,
                    parent, false);
            holder.mItemLayout = (RelativeLayout) convertView
                    .findViewById(R.id.layout_item);
            holder.mIndexLayout = (RelativeLayout) convertView
                    .findViewById(R.id.layout_index);
            holder.mIndexTv = (TextView) convertView
                    .findViewById(R.id.tv_index);
            holder.mIndexSelectIv = (ImageView) convertView
                    .findViewById(R.id.iv_index_select);
            holder.mSelectCb = (CheckBox) convertView
                    .findViewById(R.id.cb_select);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.mRelationTv = (TextView) convertView
                    .findViewById(R.id.tv_relation);
            holder.mAgeTv = (TextView) convertView.findViewById(R.id.tv_age);
            holder.mLocationTv = (TextView) convertView
                    .findViewById(R.id.tv_location);
            holder.imgAuthorized = convertView.findViewById(R.id.imgAuthorized);
            holder.ivGender = (ImageView) convertView.findViewById(R.id.iv_gender);
            holder.llNameTopLayout = (LinearLayout) convertView.findViewById(R.id.ll_name_top_layout);
            holder.mTopGrayView = convertView.findViewById(R.id.v_top);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SortModel model = mList.get(position);
        UserItem user = model.user;
        // 根据position获取分类的首字母的Char ascii值
        final int section = getSectionForPosition(position);
        holder.mIndexSelectIv.setVisibility(mShowIndexSelect ? View.VISIBLE : View.GONE);

        holder.mIndexSelectIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndexSelectArray.put(section, !(mIndexSelectArray.get(section)));
                if (mIndexSelectArray.get(section)) {
                    selectIndex(section);
                } else {
                    unselectIndex(section);
                }
                notifyDataSetChanged();
                if (mInterface != null) {
                    mInterface.onSelectIndex();
                }
            }
        });

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.mIndexLayout.setVisibility(View.VISIBLE);
            holder.mTopGrayView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.mIndexTv.setText(model.sortLetters);
            if (mIndexSelectArray.get(section)) {
                holder.mIndexSelectIv.setImageResource(R.drawable.contacts_select_all_selected);
            } else {
                holder.mIndexSelectIv.setImageResource(R.drawable.contacts_select_all);
            }
        } else {
            holder.mIndexLayout.setVisibility(View.GONE);
        }
        holder.mSelectCb.setChecked(isSelected(model));

        ImageUtils.loadCircleHead(user.getImgmiddleurl(), holder.mHeadIv);
        String name = user.getAlias();
        if (TextUtils.isEmpty(name)) {
            name = user.getNickname();
        }
        holder.mNameTv.setText(name);

        // 根据position获取分类的首字母的Char ascii值
        if(user.getCertification()==UserItem.certificationYes){
            holder.imgAuthorized.setVisibility(View.VISIBLE);
        }else{
            holder.imgAuthorized.setVisibility(View.GONE);
        }

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
            holder.mAgeTv.setText(String.format(MyApplication.getContext().getString(R.string.im_age),user.getAge()));
        }
        holder.ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    R.drawable.dongtai_gender_boy, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_blue_corner32);
            holder.ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    R.drawable.dongtai_gender_girl, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_red_corner32);
            holder.ivGender.setImageResource(R.drawable.icon_female);
        } else {
//            holder.mAgeTv.setCompoundDrawablesWithIntrinsicBounds(
//                    0, 0, 0, 0);
//            holder.mAgeTv.setBackgroundResource(R.drawable.shape_unknow_sex_bg);
            holder.ivGender.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(user.getCityname())){
            holder.mLocationTv.setVisibility(View.GONE);
        }else {
            holder.mLocationTv.setVisibility(View.VISIBLE);
            holder.mLocationTv.setText(user.getCityname());
        }
//        holder.mLocationTv.setText(user.getCityname());

        holder.mItemLayout.setTag(position);
        holder.mItemLayout.setOnClickListener(this);

        //新加
        boolean isMember = false;
        for (int m = 0; m < listMember.size(); m++) {
            UserItem userMember = listMember.get(m);
            if (user.getUserid() == userMember.getUserid()) {
                isMember = true;
                break;
            }
        }
        if (isMember) {
            holder.mSelectCb.setBackgroundResource(R.drawable.selector_checkbox_contact_member);
            holder.mSelectCb.setChecked(true);
            holder.mSelectCb.setEnabled(false);
        } else {
            holder.mSelectCb.setBackgroundResource(R.drawable.selector_checkbox_contact);
            holder.mSelectCb.setEnabled(true);
        }

        return convertView;
    }

    public static class ViewHolder {
        public RelativeLayout mItemLayout;
        RelativeLayout mIndexLayout;
        public TextView mIndexTv;
        ImageView mIndexSelectIv;
        public CheckBox mSelectCb;
        public RoundImageView mHeadIv;
        public TextView mNameTv;
        public TextView mRelationTv;
        public TextView mAgeTv;
        public TextView mLocationTv;
        public View imgAuthorized;
        public ImageView ivGender;//性别
        public LinearLayout llNameTopLayout;//名字、关系父控
        View mTopGrayView;
    }

    public void initIndexCountArray() {
        if (mList.size() == 0) {
            return;
        }
        int section = 0;
        int count = 1;
        for (int i = 0; i < getCount(); i++) {
            SortModel item = mList.get(i);
            String sortStr = item.sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                count++;
            } else {
                if (i > 0) {
                    mIndexCountArray.put(section, count);
                    count = 1;
                }
                section = firstChar;
            }
        }
        mIndexCountArray.put(section, count);
    }

    public void initIndexSelectArray() {
        if (mIndexCountArray.size() == 0) {
            return;
        }
        for (int i = 0; i < mIndexCountArray.size(); i++) {
            int section = mIndexCountArray.keyAt(i);
            int count = 0;
            for (SortModel item : mSelectedList) {
                String sortStr = item.sortLetters;
                char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
                if (firstChar == section) {
                    count++;
                }
            }
            if (count == mIndexCountArray.get(section, 0)) {
                mIndexSelectArray.put(section, true);
            } else {
                mIndexSelectArray.put(section, false);
            }
        }
        notifyDataSetChanged();
    }

    private void toggleIndex(int position) {
        int section = getSectionForPosition(position);
        int count = 0;
        for (SortModel item : mSelectedList) {
            String sortStr = item.sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                count++;
            }
        }
        if (count == mIndexCountArray.get(section, 0)) {
            mIndexSelectArray.put(section, true);
        } else {
            mIndexSelectArray.put(section, false);
        }
        notifyDataSetChanged();
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

    public void selectIndex(int section) {
        for (int i = 0; i < getCount(); i++) {
            SortModel item = mList.get(i);
            String sortStr = item.sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                setSelected(i);
            }
        }
    }

    public void unselectIndex(int section) {
        for (int i = 0; i < getCount(); i++) {
            SortModel item = mList.get(i);
            String sortStr = item.sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                removeSelected(i);
            }
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    private boolean isSelected(SortModel model) {
        return mSelectedList.contains(model);
    }

    //需要加判断。如果
    public void toggleChecked(int position) {
//        long userId = mList.get(position).user.getUserid();
//        for (int i = 0; i < listMember.size(); i++) {
//            if (listMember.get(i).getUserid() == userId) {
//                return;
//            }
//        }
        if (isMember(position)) {
            return;
        }
        //新加以上判断
        if (isSelected(mList.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
        }
        notifyDataSetChanged();
    }


    boolean isMember(int position) {
        long userId = mList.get(position).user.getUserid();
        for (int i = 0; i < listMember.size(); i++) {
            if (listMember.get(i).getUserid() == userId) {
                return true;
            }
        }
        return false;
    }

    private void setSelected(int position) {
        if (!mSelectedList.contains(mList.get(position))) {
            mSelectedList.add(mList.get(position));
        }
    }

    private void removeSelected(int position) {
        if (mSelectedList.contains(mList.get(position))) {
            mSelectedList.remove(mList.get(position));
        }
    }

    public ArrayList<SortModel> getSelectedList() {
        return mSelectedList;
    }

    public void selectAll() {
        mSelectedList.clear();
        // mSelectedList.addAll(mList);
        for (int i = 0; i < mList.size(); i++) {
            if (isMember(i)) {
                //pass
            } else {
                mSelectedList.add(mList.get(i));
            }
        }
        notifyDataSetChanged();
    }


    public void selectNone() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public void setShowIndexSelect(boolean show) {
        mShowIndexSelect = show;
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.layout_item:
                position = (int) v.getTag();
                if (mInterface != null) {
                    toggleChecked(position);
                    toggleIndex(position);
                    mInterface.onItemClick(position);
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(ISelectFriendAdapter i) {
        mInterface = i;
    }

    public interface ISelectFriendAdapter {
        void onItemClick(int position);

        void onSelectIndex();
    }

}