package yahier.exst.adapter.ad;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.item.ad.AdBusinessItem;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.List;

/**
 * 商务合作申请列表适配器
 * Created by Administrator on 2016/10/1 0001.
 */

public class AdBusinessCooperationAdapter extends BaseAdapter {
    private Context mCtx;
    private List<AdBusinessItem> mData;
    private AdBusinessAdapterListener listener;

    public AdBusinessCooperationAdapter(Context context, List<AdBusinessItem> data) {
        this.mCtx = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() :0;
    }

    @Override
    public AdBusinessItem getItem(int position) {
        if (position >= mData.size()) return null;
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.ad_business_cooperation_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AdBusinessItem item = getItem(position);
        if (item == null) return convertView;
        PicassoUtil.load(mCtx, item.getApplyimgurl(), holder.rivUserIcon, R.drawable.icon_shifu_default);
        holder.tvNickname.setText(item.getApplynickname());
        holder.tvApplyTime.setText(DateUtil.getDateYMDHM(item.getCreatetime()));
        if (item.getIsfriend() != 0) {
            holder.tvCallTa.setText(R.string.ad_call_ta);
        } else {
            holder.tvCallTa.setText(R.string.ad_add_friend);
        }
        holder.rivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //站外申请不能点击头像
                if (item.getApplytype() != AdBusinessItem.type_app) return;

                Intent intent = new Intent(mCtx, TribeMainAct.class);
                intent.putExtra("userId", item.getApplyuserid());
                mCtx.startActivity(intent);
            }
        });
        if (item.getApplytype() != AdBusinessItem.type_app){
            holder.tvNotApp.setVisibility(View.VISIBLE);
            holder.tvCallTa.setVisibility(View.GONE);
        }else{
            holder.tvNotApp.setVisibility(View.GONE);
            holder.tvCallTa.setVisibility(View.VISIBLE);
        }
        holder.tvCallTa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) return;
                if (item.getIsfriend() != 0) {
                    listener.toChat(item.getApplyuserid(), item.getApplynickname());
                } else {
                    listener.addFriend(item.getApplyuserid(), item.getApplynickname());
                }
            }
        });
        holder.tvApplyFrom.setText(item.getSourcead());
        holder.tvApplyType.setText(item.getCotypename());

        holder.tvApplyName.setText(item.getApplyname());
        holder.tvApplyDescription.setText(item.getApplycontent());
        holder.tvApplyPhone.setText("+" + item.getApplyareacode() + item.getApplyphone());
        holder.tvApplyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+item.getApplyareacode() + item.getApplyphone()));
                mCtx.startActivity(intent);
            }
        });
        return convertView;
    }

    static class ViewHolder {

        public ViewHolder(View view) {
            rivUserIcon = (RoundImageView) view.findViewById(R.id.riv_user_icon);
            tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
            tvApplyTime = (TextView) view.findViewById(R.id.tv_apply_time);
            tvCallTa = (TextView) view.findViewById(R.id.tv_call_ta);
            tvApplyFrom = (TextView) view.findViewById(R.id.tv_apply_from);
            tvApplyType = (TextView) view.findViewById(R.id.tv_apply_type);
            tvApplyName = (TextView) view.findViewById(R.id.tv_apply_name);
            tvApplyPhone = (TextView) view.findViewById(R.id.tv_apply_phone);
            tvApplyDescription = (TextView) view.findViewById(R.id.tv_apply_description);
            tvNotApp = (TextView) view.findViewById(R.id.tv_not_app);
        }

        RoundImageView rivUserIcon; //申请人头像
        TextView tvNickname; //申请人昵称
        TextView tvApplyTime; //申请时间
        TextView tvCallTa; //联系他/加好友
        TextView tvApplyFrom; //来自
        TextView tvApplyType; //合作类型
        TextView tvApplyName; //申请填写的名字
        TextView tvApplyPhone; //联系电话
        TextView tvApplyDescription; //申请描述
        TextView tvNotApp; //站外好友标识

    }

    public void setAdBusinessAdapterListener(AdBusinessAdapterListener listener) {
        this.listener = listener;
    }

    public interface AdBusinessAdapterListener {
        void addFriend(long userid, String name); //加好友

        void toChat(long userid, String name); //联系ta
    }
}
