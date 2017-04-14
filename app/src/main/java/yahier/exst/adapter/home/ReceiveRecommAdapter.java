package yahier.exst.adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.bangyibang.Recommend;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

public class ReceiveRecommAdapter extends BaseAdapter implements
        OnClickListener {

    private ArrayList<Recommend> mList;
    private LayoutInflater mInflater;

    private IReceiveRecommAdapter mInterface;

    private int mIssueState;

    public ReceiveRecommAdapter(ArrayList<Recommend> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
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
            convertView = mInflater.inflate(R.layout.item_receive_recomm,
                    parent, false);
            holder.mRecommNickTv = (TextView) convertView
                    .findViewById(R.id.tv_recomm_nick);
            holder.mRecommTimeTv = (TextView) convertView
                    .findViewById(R.id.tv_recomm_time);
            holder.mHeadIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head);
            holder.mContactTv = (TextView) convertView
                    .findViewById(R.id.tv_contact_ta);
            holder.mTargetNickTv = (TextView) convertView
                    .findViewById(R.id.tv_target_nick);
            holder.mRecommContentTv = (TextView) convertView
                    .findViewById(R.id.tv_recomm_content);
            holder.mBountyTv = (TextView) convertView
                    .findViewById(R.id.tv_bounty);
            holder.mAdoptTv = (TextView) convertView
                    .findViewById(R.id.tv_adopt);
            holder.mAdoptTimeTv = (TextView) convertView
                    .findViewById(R.id.tv_adopt_time);
            holder.mTagIv = (ImageView) convertView.findViewById(R.id.iv_tag);
            holder.mRewardIv = (ImageView) convertView.findViewById(R.id.iv_reward);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindViewHolder(position, holder);

        return convertView;
    }

    private void bindViewHolder(int position, ViewHolder holder) {
        Recommend item = mList.get(position);

        ImageUtils.loadHead(item.getShareimgmiddleurl(), holder.mHeadIv);

        holder.mRecommNickTv.setText(item.getRecommendernickname());

        holder.mRecommTimeTv.setText(DateUtil.getHelpTime(item
                .getRecommendtime()));

        holder.mTargetNickTv.setText(item.getShareusername());
        holder.mRecommContentTv.setText(item.getSharereason());

        holder.mBountyTv.setText("赏金 " + item.getRewardbean());

        holder.mRecommContentTv.setText(item.getSharereason());

        holder.mHeadIv.setTag(position);
        holder.mHeadIv.setOnClickListener(this);

        holder.mAdoptTv.setTag(position);
        holder.mAdoptTv.setOnClickListener(this);

        holder.mRewardIv.setTag(position);
        holder.mRewardIv.setOnClickListener(this);

        if (item.getIscontacted() == 1) {
            holder.mContactTv.setText("已联系\n" + DateUtil.getHelpTime(item.getContacttime()));
        } else {
            holder.mContactTv.setText("联系Ta");
        }

        if (mIssueState == 0) { //初始状态
            holder.mAdoptTv.setVisibility(View.VISIBLE);
        } else { //已关闭或者已完成
            holder.mAdoptTv.setVisibility(View.GONE);
        }

        if (item.getIsselected() == 1) {
            holder.mTagIv.setVisibility(View.VISIBLE);
            holder.mAdoptTimeTv.setVisibility(View.VISIBLE);
            holder.mAdoptTimeTv.setText(DateUtil.getHelpTime(item
                    .getSelecttime()));
            //holder.mRewardIv.setVisibility(View.VISIBLE);
        } else {
            holder.mAdoptTimeTv.setVisibility(View.GONE);
            holder.mTagIv.setVisibility(View.GONE);
            //holder.mRewardIv.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {
        public TextView mRecommNickTv;
        public TextView mRecommTimeTv;
        public RoundImageView mHeadIv;
        public TextView mContactTv;
        public TextView mTargetNickTv;
        public TextView mRecommContentTv;
        public TextView mBountyTv;
        public TextView mAdoptTv;
        public TextView mAdoptTimeTv;
        public ImageView mTagIv;
        public ImageView mRewardIv;
    }

    @Override
    public void onClick(View v) {
        int position = 0;
        switch (v.getId()) {
            case R.id.iv_head:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onContact(position);
                }
                break;
            case R.id.tv_adopt:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onAdopt(position);
                }
                break;
            case R.id.iv_reward:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onReward(position);
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(IReceiveRecommAdapter i) {
        mInterface = i;
    }

    public interface IReceiveRecommAdapter {
        void onContact(int position);

        void onAdopt(int position);

        void onReward(int position);
    }

    public void setIssueState(int issueState) {
        mIssueState = issueState;
    }

}
