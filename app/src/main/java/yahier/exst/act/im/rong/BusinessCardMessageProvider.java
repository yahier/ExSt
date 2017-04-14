package yahier.exst.act.im.rong;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 超级名片 IM消息提供者
 *
 * @author lsy
 */
@ProviderTag (messageContent = BusinessCardMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class BusinessCardMessageProvider extends IContainerItemProvider.MessageProvider<BusinessCardMessage> {

    Context mContext;

    /**
     * 初始化View
     */
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_rong_message_businesscard2, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Device.getWidth() * 2 / 3, ActionBar.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder();
        holder.view = view.findViewById(R.id.rc_img);
        holder.user_img = (ImageView) view.findViewById(R.id.user_img);
        holder.tvName = (TextView) view.findViewById(R.id.tvName);

        holder.mAgeTv = (TextView) view.findViewById(R.id.tv_age);
        holder.mLocationTv = (TextView) view
                .findViewById(R.id.tv_location);
        holder.ivGender = (ImageView) view.findViewById(R.id.iv_gender);
        holder.linUserInfo = view.findViewById(R.id.linUserInfo);
        //holder.user_gender_age = (TextView) view.findViewById(R.id.user_gender_age);
        //holder.user_city = (TextView) view.findViewById(R.id.user_city);
        view.setTag(holder);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void bindView(View v, int position, BusinessCardMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();

        // 更改气泡样式
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            // 消息方向，自己发送的
            holder.view.setBackgroundResource(R.drawable.im_message_white_bg_right);
        } else {
            // 消息方向，别人发送的
            holder.view.setBackgroundResource(R.drawable.im_message_white_bg_left);
        }
        String str = JSONHelper.getExParams(content.getEx_params());
        UserItem user = JSONHelper.getObject(str, UserItem.class);
        PicassoUtil.load(mContext, user.getImgmiddleurl(), holder.user_img);
        holder.tvName.setText(user.getNickname()); // 设置消息内容
        if (user.getAge() == 0) {
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

        if (holder.mAgeTv.getVisibility() == View.GONE && holder.ivGender.getVisibility() == View.GONE && holder.mLocationTv.getVisibility() == View.GONE) {
            holder.linUserInfo.setVisibility(View.GONE);
        } else {
            holder.linUserInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Spannable getContentSummary(BusinessCardMessage data) {
        return null;
    }

    @Override
    public void onItemClick(View view, int position, BusinessCardMessage content, UIMessage message) {
        String str = JSONHelper.getExParams(content.getEx_params());
        UserItem user = JSONHelper.getObject(str, UserItem.class);
        if (user.getUserid() == 0) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.im_userid_null));
            return;
        }
        Intent intent = new Intent(mContext, TribeMainAct.class);
        intent.putExtra("userId", Long.valueOf(user.getUserid()));
        mContext.startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position, BusinessCardMessage content, UIMessage message) {

    }

    class ViewHolder {
        ImageView user_img;
        TextView tvName;
        //TextView user_gender_age,user_city;
        View view;
        View linUserInfo;

        public TextView mAgeTv;
        public TextView mLocationTv;
        public ImageView ivGender;//性别
    }

}