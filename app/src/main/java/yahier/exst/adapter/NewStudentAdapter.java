package yahier.exst.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.Relation;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yahier on 16/11/10.
 */

public class NewStudentAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<UserItem> mList;
    private LayoutInflater mInflater;
    private boolean isSelf = false;
    private com.stbl.stbl.adapter.NewStudentAdapter.IStudentListAdapter mInterface;

    public NewStudentAdapter(boolean isSelf) {
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mList = new ArrayList<>();
        this.isSelf = isSelf;
    }

    public void addData(List<UserItem> list){
        mList.addAll(list);
        notifyDataSetChanged();
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

    public View getView(final int position, View convertView, final ViewGroup parent) {
        com.stbl.stbl.adapter.NewStudentAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new com.stbl.stbl.adapter.NewStudentAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_new_student,
                    parent, false);
            holder.mIndexTv = (TextView) convertView
                    .findViewById(R.id.tv_index);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);

            holder.mAgeTv = (TextView) convertView.findViewById(R.id.tv_age);
            holder.mLocationTv = (TextView) convertView
                    .findViewById(R.id.tv_location);
            holder.ivGender = (ImageView) convertView.findViewById(R.id.iv_gender);
            holder.tvSignature= (TextView)convertView.findViewById(R.id.tvSignature);

            convertView.setTag(holder);
        } else {
            holder = (com.stbl.stbl.adapter.NewStudentAdapter.ViewHolder) convertView.getTag();
        }
        bindViewHolder(position, holder);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), TribeMainAct.class);
                intent.putExtra("userId",mList.get(position).getUserid());
                parent.getContext().startActivity(intent);

            }
        });
        return convertView;
    }

    private void bindViewHolder(int position, com.stbl.stbl.adapter.NewStudentAdapter.ViewHolder holder) {
        UserItem user = mList.get(position);

        ImageUtils.loadCircleHead(user.getImgmiddleurl(), holder.mHeadIv);
        holder.mNameTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());
//        holder.mNameTv.setText(user.getNickname());
        int relationFlag = user.getRelationflag();


        if (user.getAge() <= 0) {
            holder.mAgeTv.setVisibility(View.GONE);
        } else {
            holder.mAgeTv.setVisibility(View.VISIBLE);
            holder.mAgeTv.setText(String.format(MyApplication.getContext().getString(R.string.im_age),user.getAge()));
        }
        holder.ivGender.setVisibility(View.VISIBLE);
        if (user.getGender() == UserItem.gender_boy) {
            holder.ivGender.setImageResource(R.drawable.icon_male);
        } else if (user.getGender() == UserItem.gender_girl) {
            holder.ivGender.setImageResource(R.drawable.icon_female);
        } else {
            holder.ivGender.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(user.getCityname())){
            holder.mLocationTv.setVisibility(View.GONE);
        }else {
            holder.mLocationTv.setVisibility(View.VISIBLE);
            holder.mLocationTv.setText(user.getCityname());
        }

        if(TextUtils.isEmpty(user.getSignature())){
            holder.tvSignature.setVisibility(View.GONE);
        }else{
            holder.tvSignature.setVisibility(View.VISIBLE);
            holder.tvSignature.setText(user.getSignature());
        }

    }

    private static class ViewHolder {
        public TextView mIndexTv;
        public RoundImageView mHeadIv;
        public TextView mNameTv;

        public TextView mAgeTv;
        public TextView mLocationTv;
        public View imgAuthorized;
        public ImageView ivGender;//性别
        public TextView tvSignature;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tv_add:
                int position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onAddFriend(position);


                }
                break;
        }
    }

    public void setInterface(com.stbl.stbl.adapter.NewStudentAdapter.IStudentListAdapter i) {
        mInterface = i;
    }

    public interface IStudentListAdapter {

        void onAddFriend(int position);

    }

}

