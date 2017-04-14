package yahier.exst.adapter.dongtai;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.DongtaiNotifyItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.FaceConversionUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.List;

/**
 * 动态通知适配器
 * Created by Administrator on 2016/6/12 0012.
 */
public class DongtaiNotifyAdapter extends BaseAdapter {
    private Context mContext;
    private List<DongtaiNotifyItem> mData;

    public DongtaiNotifyAdapter(Context context, List<DongtaiNotifyItem> data) {
        this.mData = data;
        this.mContext = context;
    }

    public void setData(List<DongtaiNotifyItem> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
//        return 10;
    }

    @Override
    public DongtaiNotifyItem getItem(int position) {
        return mData != null ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NotifyHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dongtai_notify_layout, null);
            mHolder = new NotifyHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (NotifyHolder) convertView.getTag();
        }

        final DongtaiNotifyItem item = getItem(position);
        if (item != null) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", item.getStatusesid());
                    intent.putExtra(KEY.SCROLL_TO_POSITION, true);
                    mContext.startActivity(intent);
                }
            });
            View.OnClickListener userClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TribeMainAct.class);
                    intent.putExtra("userId", item.getOpuserid());
                    mContext.startActivity(intent);
                }
            };
            mHolder.rivUserImg.setOnClickListener(userClick);
            mHolder.tvNickName.setOnClickListener(userClick);
            ImageUtils.loadHead(item.getOpuserimgurl(), mHolder.rivUserImg);
//            PicassoUtil.load(mContext,item.getOpuserimgurl(),mHolder.rivUserImg);
            mHolder.tvNickName.setText(item.getOpusername());

            mHolder.tvReplyTime.setText(DateUtil.getTimeOff(item.getRemindtime()));

            StringBuilder builder = new StringBuilder();
            builder.append("\u3000\u3000");
            if (item.getType() == DongtaiNotifyItem.NOTIFY_TYPE_PRAISE) {
                mHolder.tvOperate.setText(item.getTypename());
            } else {
                mHolder.tvOperate.setText(item.getTypename() + "：");
                builder.append("\u3000");
            }
            if (item.getType() == DongtaiNotifyItem.NOTIFY_TYPE_REPOST && TextUtils.isEmpty(item.getContent())) {
                mHolder.tvReplyContent.setText(builder.toString() + mContext.getString(R.string.repost_statuses));
            } else {
                SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(MyApplication.getContext(), builder.toString() + item.getContent());
                mHolder.tvReplyContent.setText(spannableString);
            }

            if (TextUtils.isEmpty(item.getImgurl())) {
                mHolder.tvContent.setVisibility(View.VISIBLE);
                mHolder.ivFirstImg.setVisibility(View.INVISIBLE);
                mHolder.tvContent.setText(item.getImgcontent());
            } else {
                mHolder.tvContent.setVisibility(View.INVISIBLE);
                mHolder.ivFirstImg.setVisibility(View.VISIBLE);
                PicassoUtil.load(mContext, item.getImgurl(), mHolder.ivFirstImg);
            }
            View.OnClickListener dynamicClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
                    intent.putExtra("statusesId", item.getStatusesid());
                    intent.putExtra(KEY.SCROLL_TO_POSITION, true);
                    mContext.startActivity(intent);
                }
            };
            mHolder.ivFirstImg.setOnClickListener(dynamicClick);
            mHolder.tvContent.setOnClickListener(dynamicClick);
        }
        return convertView;
    }

    static class NotifyHolder {
        /**
         * 头像
         */
        public RoundImageView rivUserImg;
        /**
         * 回复内容
         */
        public TextView tvReplyContent;
        /**
         * 回复时间
         */
        public TextView tvReplyTime;
        /**
         * 相关动态的图片
         */
        public ImageView ivFirstImg;
        /**
         * 相关动态的内容
         */
        public TextView tvContent;
        /**
         * 回复者昵称
         */
        public TextView tvNickName;
        /**
         * 操作类型
         */
        public TextView tvOperate;


        public NotifyHolder(View view) {
            if (view == null) return;
            rivUserImg = (RoundImageView) view.findViewById(R.id.riv_user_img);
            tvReplyContent = (TextView) view.findViewById(R.id.tv_reply_content);
            tvReplyTime = (TextView) view.findViewById(R.id.tv_reply_time);
            ivFirstImg = (ImageView) view.findViewById(R.id.iv_first_photo);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvNickName = (TextView) view.findViewById(R.id.tv_nickname);
            tvOperate = (TextView) view.findViewById(R.id.tv_reply_operate);
        }
    }
}
