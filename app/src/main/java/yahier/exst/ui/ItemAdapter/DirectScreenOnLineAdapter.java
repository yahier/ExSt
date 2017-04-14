package yahier.exst.ui.ItemAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.api.data.MemberInfo;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.SharedToken;

import java.util.List;

/**
 * Created by Administrator on 2016/3/8 0008.
 */
public class DirectScreenOnLineAdapter extends BaseAdapter {
    private Context mContext;
    private List<MemberInfo> memberList;
    private final static int usertype_common = 0;
    private final static int usertype_guest = 1;
    private final static int usertype_room = 2   ;
    private long homeOwner;//房主

    public DirectScreenOnLineAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<MemberInfo> data){
        this.memberList = data;
        notifyDataSetChanged();
    }

    public void deleteItem(long memberId){
        if (memberList != null){
            for (int i=0; i<memberList.size(); i++){
                MemberInfo info = memberList.get(i);
                if (info.getMemberid() == memberId){
                    memberList.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public long getHomeOwner(){
        return homeOwner;
    }
    @Override
    public int getCount() {
        return memberList == null ? 0 : memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList == null ? null : memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserHolder userHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.direct_screen_online_item_layout,null);
            userHolder = new UserHolder(convertView);
            convertView.setTag(userHolder);
        }else{
            userHolder = (UserHolder) convertView.getTag();
        }
        final MemberInfo member = (MemberInfo) getItem(position);
        if (member != null) {
            PicassoUtil.load(mContext, member.getImgurl(),userHolder.ivUserImg);
            userHolder.tvUserName.setText(member.getNickname());
            userHolder.tvUserAge.setText(String.valueOf(member.getAge()));
            userHolder.tvCity.setText(member.getCityname());

            if (member.getMembertype() == usertype_guest){ //嘉宾
                userHolder.tvUserType.setText("嘉宾");
            }else if (member.getMembertype() == usertype_room){ //房主
                userHolder.tvUserType.setText("房主");
                homeOwner = member.getMemberid();
            }else if(member.getMembertype() == usertype_common){ //普通
                userHolder.tvUserType.setText("观众");
            }
            if (member.getGender() == UserItem.gender_boy) {
                userHolder.tvUserAge.setBackgroundResource(R.drawable.shape_boy_bg);
                userHolder.tvUserAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_boy, 0, 0, 0);
            } else {
                userHolder.tvUserAge.setBackgroundResource(R.drawable.shape_girl_bg);
                userHolder.tvUserAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dongtai_gender_girl, 0, 0, 0);
            }
            userHolder.ivUserImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId",member.getMemberid());
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    static class UserHolder{
        ImageView ivUserImg; //头像
        TextView tvUserName; //名字
        TextView tvUserAge; //岁数
        TextView tvCity; //所在城市
        TextView tvUserType; //房主、嘉宾...

        public UserHolder(View view){
            ivUserImg = (ImageView) view.findViewById(R.id.iv_user_img);
            tvUserName = (TextView) view.findViewById(R.id.tv_username);
            tvUserAge = (TextView) view.findViewById(R.id.tv_user_age);
            tvCity = (TextView) view.findViewById(R.id.tv_city);
            tvUserType = (TextView) view.findViewById(R.id.tv_user_type);

        }
    }
}
