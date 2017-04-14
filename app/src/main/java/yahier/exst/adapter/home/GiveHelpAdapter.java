package yahier.exst.adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.bangyibang.BangYiBangItem;
import com.stbl.stbl.model.bangyibang.Publisher;
import com.stbl.stbl.model.bangyibang.ShareUserInfo;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

public class GiveHelpAdapter extends BaseAdapter implements OnClickListener {

    private ArrayList<BangYiBangItem> mList;
    private LayoutInflater mInflater;

    private IHelpAdapter mInterface;

    private StringBuilder mBuilder;

    public GiveHelpAdapter(ArrayList<BangYiBangItem> list) {
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
            convertView = mInflater.inflate(R.layout.item_give_help, parent,
                    false);
            holder.mHead1Iv = (RoundImageView) convertView
                    .findViewById(R.id.iv_head1);
            holder.mIndustryTv = (TextView) convertView
                    .findViewById(R.id.tv_industry);
            holder.mTitleTv = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.mPublishContentTv = (TextView) convertView
                    .findViewById(R.id.tv_publish_content);
            holder.mPublishTimeTv = (TextView) convertView
                    .findViewById(R.id.tv_publish_time);
            holder.mHelperTv = (TextView) convertView
                    .findViewById(R.id.tv_helper);
            holder.mBountyTv = (TextView) convertView
                    .findViewById(R.id.tv_bounty);
            holder.mHelpTaTv = (TextView) convertView
                    .findViewById(R.id.tv_help_ta);
            holder.mViewLayout = (LinearLayout) convertView
                    .findViewById(R.id.layout_view);
            holder.mCloseTv = (TextView) convertView
                    .findViewById(R.id.tv_close);
            holder.mDeleteTv = (TextView) convertView
                    .findViewById(R.id.tv_delete);
            holder.mViewNumberTv = (TextView) convertView
                    .findViewById(R.id.tv_view_number);
            holder.mTagIv = (ImageView) convertView.findViewById(R.id.iv_tag);
            holder.mIRecommManLayout = (LinearLayout) convertView
                    .findViewById(R.id.layout_i_recomm_man);
            holder.mIRecommManIv = (RoundImageView) convertView
                    .findViewById(R.id.iv_i_recomm_man);
            holder.mIRecommManTv = (TextView) convertView
                    .findViewById(R.id.tv_i_recomm_man);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindViewHolder(position, holder);

        return convertView;
    }

    private void bindViewHolder(int position, ViewHolder holder) {
        BangYiBangItem item = mList.get(position);
        Publisher publisher = item.getPublisher();

        // 填充数据
        ImageUtils.loadCircleHead(publisher.getImgmiddleurl(), holder.mHead1Iv);

        String industry = item.getIssuetype();
        holder.mIndustryTv.setText(industry);

        holder.mTitleTv.setText(setSpace(industry.length()) + StringUtil.replaceAllLineBreak(item.getIssuetitle()));

        holder.mPublishContentTv.setText(item.getIssuedescription());
        holder.mPublishTimeTv.setText(DateUtil.getTimeOff(item.getPublishtime()));

        holder.mHelperTv.setText(createHelperString(item
                .getRecommendernamearray()));
        holder.mBountyTv.setText(MyApplication.getContext().getString(R.string.bang_money_reward) + item.getRewardbean());
        int recommCount = item.getRecommendcount();
        if (recommCount <= 0) {
            holder.mViewNumberTv.setText(R.string.bang_check);
        } else {
            holder.mViewNumberTv.setText(MyApplication.getContext().getString(R.string.bang_check) + recommCount);
        }

        holder.mHead1Iv.setTag(position);
        holder.mHead1Iv.setOnClickListener(this);

        holder.mViewLayout.setTag(position);
        holder.mViewLayout.setOnClickListener(this);

        holder.mCloseTv.setTag(position);
        holder.mCloseTv.setOnClickListener(this);

        holder.mDeleteTv.setTag(position);
        holder.mDeleteTv.setOnClickListener(this);

        holder.mHelpTaTv.setTag(position);
        holder.mHelpTaTv.setOnClickListener(this);

        holder.mIRecommManLayout.setTag(position);
        holder.mIRecommManLayout.setOnClickListener(this);

        holder.mIRecommManLayout.setVisibility(View.GONE);
        holder.mHelpTaTv.setVisibility(View.GONE);
        holder.mViewLayout.setVisibility(View.GONE);
        holder.mCloseTv.setVisibility(View.GONE);
        holder.mDeleteTv.setVisibility(View.GONE);
        holder.mTagIv.setVisibility(View.GONE);

        int issuestate = item.getIssuestate(); // 0-已激活，10-已关闭，20-已完成
        int userintype = item.getUserintype(); // 0-无关系， 10-发布者， 20-推荐者，30-被推荐者

        // issue的状态控制字体颜色
        if (issuestate == 0) { //初始状态
            holder.mTitleTv.setTextColor(Res.getColor(R.color.font_black));
            holder.mPublishContentTv.setTextColor(Res.getColor(R.color.font_black_light));
            holder.mPublishTimeTv.setTextColor(Res.getColor(R.color.font_gray_light));
            holder.mHelperTv.setTextColor(Res.getColor(R.color.font_black));
        } else {  // 已关闭或者已完成
            holder.mTitleTv.setTextColor(Res.getColor(R.color.font_gray_light));
            holder.mPublishContentTv.setTextColor(Res.getColor(R.color.font_gray_light));
            holder.mPublishTimeTv.setTextColor(Res.getColor(R.color.font_gray_light));
            holder.mHelperTv.setTextColor(Res.getColor(R.color.font_gray_light));
        }

        //控制显示tag
        if (issuestate == 10) { //已关闭
            holder.mTagIv.setVisibility(View.VISIBLE);
            holder.mTagIv.setImageResource(R.drawable.ic_closed_tag);
        } else if (issuestate == 20) { //已完成
            holder.mTagIv.setVisibility(View.VISIBLE);
            if (userintype == 21) { //我是推荐者而且被采纳了
                holder.mTagIv.setImageResource(R.drawable.ic_adopted_tag);
            } else {  //已完成 或者 我是推荐者但没有被采纳
                holder.mTagIv.setImageResource(R.drawable.ic_completed_tag);
            }
        }

        // user的type控制操作(有交互的UI显示)
        if (userintype == 10) { // 发布者
            holder.mViewLayout.setVisibility(View.VISIBLE);
            if (issuestate == 10) { // 已关闭
                holder.mDeleteTv.setVisibility(View.VISIBLE);
            } else if (issuestate == 0) { // 激活
                holder.mCloseTv.setVisibility(View.VISIBLE);
            }
        } else if (userintype == 20 || userintype == 21) { // 推荐者（无论有没有被采纳）
            ShareUserInfo userInfo = item.getShareinfo().getShareuserinfo();
            if (userInfo != null) {
                holder.mIRecommManLayout.setVisibility(View.VISIBLE);
                ImageUtils.loadCircleHead(userInfo.getImgmiddleurl(), holder.mIRecommManIv);
                holder.mIRecommManTv.setText(userInfo.getNickname());
            }
        } else { //不是发布者，也不是推荐者
            if (issuestate == 0) { // 激活(未关闭 未完成)
                holder.mHelpTaTv.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class ViewHolder {
        public RoundImageView mHead1Iv;
        public TextView mIndustryTv;
        public TextView mTitleTv;
        public TextView mPublishContentTv;
        public TextView mPublishTimeTv;
        public TextView mHelperTv;
        public TextView mCloseTv;
        public TextView mDeleteTv;
        public LinearLayout mViewLayout;
        public TextView mBountyTv;
        public TextView mHelpTaTv;
        public TextView mViewNumberTv;
        public ImageView mTagIv;
        public LinearLayout mIRecommManLayout;
        public RoundImageView mIRecommManIv;
        public TextView mIRecommManTv;
    }

    private String setSpace(int length) {
        mBuilder.delete(0, mBuilder.length());
        for (int i = 0; i < length; i++) {
            mBuilder.append("    ");
        }
        return mBuilder.append("  ").toString();
    }

    private String createHelperString(String[] array) {
        mBuilder.delete(0, mBuilder.length());
        mBuilder.append(MyApplication.getContext().getString(R.string.bang_help_people)+"(");
        mBuilder.append(array.length);
        mBuilder.append("）：");
        int length = array.length;
        if (length == 0) {
            return mBuilder.toString();
        }
        int size = length > 4 ? 4 : length;
        for (int i = 0; i < size; i++) {
            mBuilder.append(array[i]);
            if (i == size - 1) {
                mBuilder.append(".");
            } else {
                mBuilder.append("、");
            }
        }
        return mBuilder.toString();
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
            case R.id.layout_view:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onView(position);
                }
                break;
            case R.id.tv_close:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onClose(position);
                }
                break;
            case R.id.tv_delete:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onDelete(position);
                }
                break;
            case R.id.tv_help_ta:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onHelp(position);
                }
                break;
            case R.id.layout_i_recomm_man:
                position = (int) v.getTag();
                if (mInterface != null) {
                    mInterface.onClickIRecommMan(position);
                }
                break;
            default:
                break;
        }
    }

    public void setInterface(IHelpAdapter i) {
        mInterface = i;
    }

    public interface IHelpAdapter {
        void onClickPublisher(int position);

        void onView(int position);

        void onClose(int position);

        void onDelete(int position);

        void onHelp(int position);

        void onClickIRecommMan(int position);
    }

}
