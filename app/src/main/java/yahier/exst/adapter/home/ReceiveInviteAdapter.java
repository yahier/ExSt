package yahier.exst.adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.bangyibang.Invited;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

public class ReceiveInviteAdapter extends BaseAdapter implements
        OnClickListener {

    private ArrayList<Invited> mList;
    private LayoutInflater mInflater;

    private IReceiveInviteAdapter mInterface;

    private StringBuilder mBuilder;

    public ReceiveInviteAdapter(ArrayList<Invited> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
        mBuilder = new StringBuilder();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_receive_invite,
                    parent, false);
            holder.mHead1Iv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head1);
            holder.mHead2Iv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head2);
            holder.mIndustryTv = (TextView) convertView
                    .findViewById(R.id.tv_industry);
            holder.mTitleTv = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.mPublishContentTv = (TextView) convertView
                    .findViewById(R.id.tv_publish_content);
            holder.mPublishTimeTv = (TextView) convertView
                    .findViewById(R.id.tv_publish_time);
            holder.mRecommManTv = (TextView) convertView
                    .findViewById(R.id.tv_recommend_man);
            holder.mRecommTimeTv = (TextView) convertView
                    .findViewById(R.id.tv_recommend_time);
            holder.mRecommContentTv = (TextView) convertView
                    .findViewById(R.id.tv_recommend_content);
            holder.mContactLayout = (LinearLayout) convertView
                    .findViewById(R.id.layout_contact_publisher);
            holder.mContactTv = (TextView) convertView
                    .findViewById(R.id.tv_contact_publisher);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Invited item = mList.get(position);

        ImageUtils.loadHead(item.getPublisherimgmiddleurl(), holder.mHead1Iv);
        ImageUtils.loadHead(item.getPublisherimgmiddleurl(), holder.mHead2Iv);

        String type = item.getIssuetype();
        holder.mIndustryTv.setText(type);

        holder.mTitleTv.setText(setSpace(type.length()) + item.getIssuetitle());

        holder.mPublishContentTv.setText(item.getIssuedescription());
        holder.mPublishTimeTv.setText(DateUtil.getHelpTime(item.getPublishtime()));

        holder.mRecommManTv.setText(item.getRecommenderusername());
        holder.mRecommTimeTv.setText(DateUtil.getHelpTime(item.getRecommendtime()));
        holder.mRecommContentTv.setText(item.getSharereason());

        holder.mHead1Iv.setTag(position);
        holder.mHead1Iv.setOnClickListener(this);

        holder.mRecommManTv.setTag(position);
        holder.mRecommManTv.setOnClickListener(this);

        holder.mContactLayout.setTag(position);
        holder.mContactLayout.setOnClickListener(this);

        if (item.getIscontacted() == 1) {
            holder.mContactLayout.setBackgroundResource(R.drawable.shape_white_gray_stroke_big_corner);
            holder.mContactTv.setText(MyApplication.getContext().getString(R.string.me_contacted) + DateUtil.getHelpTime(item.getContacttime()));
            holder.mContactTv.setTextColor(0xff4c4c4c);
        } else {
            holder.mContactLayout.setBackgroundResource(R.drawable.shape_white_red_stroke);
            holder.mContactTv.setText(R.string.me_connection_demand_publisher);
            holder.mContactTv.setTextColor(0xfff35f65);
        }

        return convertView;
    }

    public static class ViewHolder {
        public RoundImageView mHead1Iv;
        public RoundImageView mHead2Iv;
        public TextView mIndustryTv;
        public TextView mTitleTv;
        public TextView mPublishContentTv;
        public TextView mPublishTimeTv;
        public TextView mRecommTimeTv;
        public TextView mRecommManTv;
        public TextView mRecommContentTv;
        public LinearLayout mContactLayout;
        public TextView mContactTv;
    }

    private String setSpace(int length) {
        mBuilder.delete(0, mBuilder.length());
        for (int i = 0; i < length; i++) {
            mBuilder.append("    ");
        }
        return mBuilder.append("  ").toString();
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.iv_head1:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onClickPublisher(position);
                }
                break;
            case R.id.tv_recommend_man:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onClickRecommendMan(position);
                }
                break;
            case R.id.layout_contact_publisher:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onContactPublisher(position);
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(IReceiveInviteAdapter i) {
        mInterface = i;
    }

    public interface IReceiveInviteAdapter {
        void onClickPublisher(int position);

        void onClickRecommendMan(int position);

        void onContactPublisher(int position);
    }

}
