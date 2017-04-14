package yahier.exst.adapter.dongtai;

import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.DateUtil;
import com.stbl.stbl.util.EmojiParseThread;
import com.stbl.stbl.util.FaceConversionUtil;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.widget.EmojiTextView;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/6/12.
 */
public class DongtaiCommentAdapter extends BaseAdapter {

    private ArrayList<StatusesComment> mList;
    private LayoutInflater mInflater;

    private AdapterInterface mInterface;

    public DongtaiCommentAdapter(ArrayList<StatusesComment> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_dongtai_comment, parent, false);
            holder.mHeadIv = (RoundImageView) convertView.findViewById(R.id.iv_head);
            holder.mNickTv = (TextView) convertView.findViewById(R.id.tv_nick);
            holder.mTimeTv = (TextView) convertView.findViewById(R.id.tv_time);
            holder.mContentTv = (EmojiTextView) convertView.findViewById(R.id.tv_content);
            holder.mPraiseLayout = (LinearLayout) convertView.findViewById(R.id.layout_praise);
            holder.mPraiseIv = (ImageView) convertView.findViewById(R.id.iv_praise);
            holder.mPraiseTv = (TextView) convertView.findViewById(R.id.tv_praise);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StatusesComment item = mList.get(position);
        final UserItem user = item.getUser();
        ImageUtils.loadHead(user.getImgurl(), holder.mHeadIv);

        holder.mNickTv.setText(user.getAlias() == null || user.getAlias().equals("") ? user.getNickname() : user.getAlias());
//        holder.mNickTv.setText(user.getNickname());
        String content =  item.getContent();
        content = content.replace("\t","").replace("\n","");
        SpannableString contentSpannableString = FaceConversionUtil.getInstace().getExpressionString(MyApplication.getContext(), content);
        final UserItem lastUser = item.getLastuser();
        if (lastUser != null) {
            holder.mContentTv.setText("");
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("回复");
            builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_black1)), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int length = builder.length();

            builder.append(" @ " + (lastUser.getAlias() == null || lastUser.getAlias().equals("") ? lastUser.getNickname() : lastUser.getAlias()));
//            builder.append(" @ " + lastUser.getNickname());
            //user1的点击
            ClickableSpan lastClick = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(parent.getContext(), TribeMainAct.class);
                    intent.putExtra("userId", lastUser.getUserid());
                    parent.getContext().startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Res.getColor(R.color.font_orange));       //设置文件颜色
                    ds.setUnderlineText(false);
                }
            };
            builder.setSpan(lastClick, length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            length = builder.length();

            builder.append(": ");
            builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_black1)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            length = builder.length();

            builder.append(contentSpannableString);
            builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_black1)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mContentTv.setMovementMethod(LinkMovementMethod.getInstance());
            //holder.mContentTv.setText(builder);
            EmojiParseThread.getInstance().parse(builder, holder.mContentTv);
        } else {
            //holder.mContentTv.setText(contentSpannableString);
            EmojiParseThread.getInstance().parse(contentSpannableString, holder.mContentTv);
        }
        holder.mTimeTv.setText(DateUtil.getTimeOff(item.getCreatetime()));
        holder.mPraiseTv.setText(item.getPraisecount() + "");
        if (item.getPraisecount() <= 0) {
            holder.mPraiseTv.setVisibility(View.INVISIBLE);
        } else {
            holder.mPraiseTv.setVisibility(View.VISIBLE);
        }
        if (item.isIspraise()) {
            holder.mPraiseIv.setImageResource(R.drawable.dongtai_praise_presed);
            holder.mPraiseTv.setTextColor(Res.getColor(R.color.font_new_yellow));
        } else {
            holder.mPraiseIv.setImageResource(R.drawable.dongtai_item3);
            holder.mPraiseTv.setTextColor(Res.getColor(R.color.font_gray));
        }
        holder.mPraiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.praiseOrUnpraise(item);
                }
            }
        });

        holder.mHeadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onClickHead(item);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onItemClick(item);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        RoundImageView mHeadIv;
        TextView mNickTv;
        TextView mTimeTv;
        EmojiTextView mContentTv;
        LinearLayout mPraiseLayout;
        ImageView mPraiseIv;
        TextView mPraiseTv;
    }

    public void setInterface(AdapterInterface i) {
        mInterface = i;
    }

    public interface AdapterInterface {
        void onItemClick(StatusesComment item);

        void onClickHead(StatusesComment item);

        void praiseOrUnpraise(StatusesComment item);
    }


}
