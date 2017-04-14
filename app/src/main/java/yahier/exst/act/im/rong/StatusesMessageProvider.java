package yahier.exst.act.im.rong;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.item.Statuses;
import com.stbl.stbl.item.StatusesCollect;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.PicassoUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;


/**
 * 超级名片 IM消息提供者
 *
 * @author lsy
 */
@ProviderTag(messageContent = StatusesMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class StatusesMessageProvider extends IContainerItemProvider.MessageProvider<StatusesMessage> {

    Context mContext;

    /**
     * 初始化View
     */
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_message_statuses, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Device.getWidth()*2/3, ActionBar.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        ViewHolder holder = new ViewHolder();
        holder.view = view;
        holder.imgUrl = (ImageView) view.findViewById(R.id.link3_img);
        holder.tvContent = (TextView) view.findViewById(R.id.link3_content);
        holder.tv_publisher = (TextView)view.findViewById(R.id.tv_publisher);
        view.setTag(holder);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void bindView(View v, int position, StatusesMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();
        // 更改气泡样式
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.view.setBackgroundResource(R.drawable.im_message_white_bg_right);
        } else {
            holder.view.setBackgroundResource(R.drawable.im_message_white_bg_left);
        }

        String str = JSONHelper.getExParams(content.getEx_params());
        StatusesCollect goods = JSONHelper.getObject(str, StatusesCollect.class);
        //StatusesCollect goods = JSONHelper.getObject(content.getEx_params(), StatusesCollect.class);
        Statuses statuses = goods.getStatuses();
        String contentStr = statuses.getTitle();
        if (contentStr == null || contentStr.equals("")) {
            contentStr = statuses.getContent();
        }
        holder.tvContent.setText(contentStr);
        StatusesPic link3Pics = statuses.getStatusespic();
        if (link3Pics != null && link3Pics.getPics().size() > 0) {
            String imgUrl = link3Pics.getOriginalpic() + link3Pics.getPics().get(0);
            PicassoUtil.load(mContext, imgUrl, holder.imgUrl);
        } else {
            PicassoUtil.load(mContext, link3Pics.getEx(), holder.imgUrl);
        }
        String name = statuses.getUser().getAlias() == null || statuses.getUser().getAlias().equals("") ? statuses.getUser().getNickname() : statuses.getUser().getAlias();
        String publisher = "<font color='#F4B10B'>" + name + "</font>"+mContext.getString(R.string.statuses_by_someone);
//        String publisher = "<font color='#F4B10B'>" + statuses.getUser().getNickname() + "</font>"+mContext.getString(R.string.statuses_by_someone);
        holder.tv_publisher.setText(Html.fromHtml(publisher));

    }

    @Override
    public Spannable getContentSummary(StatusesMessage data) {
        return null;
    }

    @Override
    public void onItemClick(View view, int position, StatusesMessage content, UIMessage message) {
        String str = JSONHelper.getExParams(content.getEx_params());
        StatusesCollect goods = JSONHelper.getObject(str, StatusesCollect.class);
        Statuses statuses = goods.getStatuses();
        Intent intent = new Intent(mContext, DongtaiDetailActivity.class);
        intent.putExtra("statusesId", Long.valueOf(statuses.getStatusesid()));
        mContext.startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position, StatusesMessage content, UIMessage message) {

    }

    class ViewHolder {
        ImageView imgUrl;
        TextView tvContent;
        TextView tv_publisher;
        View view;

    }

}