package yahier.exst.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.EmojiParseThread;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.Res;
import com.stbl.stbl.widget.EmojiTextView;

import java.util.List;

/**
 * 评论列表 现在用在动态主页，最多只显示3条
 *
 * @author lenovo
 */
public class RemarkAdapter extends CommonAdapter {
    Context mContext;
    List<StatusesComment> list;

    public RemarkAdapter(Context mContext, List<StatusesComment> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size() > 3 ? 3 : list.size();
    }

    class Holder {
        TextView item_name;
        EmojiTextView item_remark;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        Holder ho = null;
        if (con == null) {
            ho = new Holder();
            con = LayoutInflater.from(mContext).inflate(R.layout.list_remark_item, null);
            ho.item_name = (TextView) con.findViewById(R.id.item_name);
            ho.item_remark = (EmojiTextView) con.findViewById(R.id.item_remark);
            con.setTag(ho);
        } else
            ho = (Holder) con.getTag();

        final StatusesComment comment = list.get(i);
        //ho.item_name.setText(comment.getUser().getNickname()+":");

        ho.item_remark.setText("");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(comment.getUser().getAlias() == null || comment.getUser().getAlias().equals("")
                ? comment.getUser().getNickname() : comment.getUser().getAlias());
//        builder.append(comment.getUser().getNickname());
        int length = builder.length();
        //builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_comment_orange)), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //user1的点击
        ClickableSpan leftClickableSpan=new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent2 = new Intent(mContext, TribeMainAct.class);
                intent2.putExtra("userId", comment.getUser().getUserid());
                mContext.startActivity(intent2);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Res.getColor(R.color.font_orange));       //设置文件颜色
                ds.setUnderlineText(false);
            }
        };
        builder.setSpan(leftClickableSpan, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置user1的点击结束
        final UserItem lastUser = comment.getLastuser();
        if (lastUser != null) {
            //ho.item_remark.append("回复");
            builder.append(Res.getString(R.string.me_reply));
            builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_comment_gray)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            length = builder.length();

            builder.append("@" + (lastUser.getAlias() == null || lastUser.getAlias().equals("")
                    ? lastUser.getNickname() : lastUser.getAlias()));
//            builder.append("@" + lastUser.getNickname());
            //builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_comment_orange)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            ClickableSpan click2=new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent2 = new Intent(mContext, TribeMainAct.class);
                    intent2.putExtra("userId", lastUser.getUserid());
                    mContext.startActivity(intent2);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Res.getColor(R.color.font_orange));       //设置文件颜色
                    ds.setUnderlineText(false);
                }
            };
            builder.setSpan(click2,length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            length = builder.length();
        }
        //ho.item_remark.append(":");
        builder.append(": ");
        builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_comment_gray)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        length = builder.length();
        ho.item_remark.setMovementMethod(LinkMovementMethod.getInstance());
        builder.append(comment.getContent());
        builder.setSpan(new ForegroundColorSpan(Res.getColor(R.color.font_comment_gray)), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //ho.item_remark.setText(builder);
        EmojiParseThread.getInstance().parse(builder, ho.item_remark);

        con.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
                intent.putExtra("statusesId", comment.getStatusesid());
                intent.putExtra(KEY.SCROLL_TO_POSITION, true);
               // intent.putExtra(DongtaiDetailActivity.keyType, statuses.getStatusestype());
                mContext.startActivity(intent);

            }
        });
        return con;
    }

}
