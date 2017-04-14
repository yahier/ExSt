package yahier.exst.act.im.rong;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RedPackectResultAct;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.RedPacket;
import com.stbl.stbl.item.im.RedPacketOpenResult;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 自定义融云IM消息提供者
 *
 * @author lsy
 */
@ProviderTag(messageContent = RedPackectMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class RedPacketMessageProvider extends IContainerItemProvider.MessageProvider<RedPackectMessage> implements FinalHttpCallback {

    Context mContext;
    String targetId;

    /**
     * 初始化View
     */
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.de_customize_message_red_packet, null);
        ViewHolder holder = new ViewHolder();
        holder.message = (TextView) view.findViewById(R.id.tvMessage);
        holder.view = (View) view.findViewById(R.id.rc_img);
        view.setTag(holder);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void bindView(View v, int position, RedPackectMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();
        // 更改气泡样式
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            // 消息方向，自己发送的
            holder.view.setBackgroundResource(R.drawable.im_group_redpacket_right_bg);
        } else {
            // 消息方向，别人发送的
            holder.view.setBackgroundResource(R.drawable.im_group_redpacket_left_bg);
        }
        targetId = message.getTargetId();
        String str = JSONHelper.getExParams(content.getEx_params());
        RedPacket pcikResult = JSONHelper.getObject(str, RedPacket.class);
        holder.message.setText(pcikResult.getHongbaodesc()); // 设置消息内容
    }

    @Override
    public Spannable getContentSummary(RedPackectMessage data) {
        return null;
    }

    @Override
    public void onItemClick(View view, int position, RedPackectMessage content, UIMessage message) {
        String str = JSONHelper.getExParams(content.getEx_params());
        RedPacket RedPacket = JSONHelper.getObject(str, RedPacket.class);
        if (RedPacket == null) return;
        openRedPacket(RedPacket.getHongbaoid());
    }


    @Override
    public void onItemLongClick(View view, int position, RedPackectMessage content, UIMessage message) {

    }

    class ViewHolder {
        TextView message;
        View view;

    }


    void openRedPacket(long hongbaoid) {
        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(mContext).commonPostData(Method.imOpenRedPacket, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(mContext, item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.imOpenRedPacket:
                RedPacketOpenResult openResult = JSONHelper.getObject(obj, RedPacketOpenResult.class);
                LogUtil.logE("imOpenRedPacket states:" + openResult.getStatus());
                switch (openResult.getStatus()) {
                    case RedPacketOpenResult.status_avilable:
                        enterResult(openResult.getHongbaoid());
                        break;
                    case RedPacketOpenResult.status_expire:
                        ToastUtil.showToast(mContext, mContext.getString(R.string.time_expired));
                        break;
                    case RedPacketOpenResult.status_finished:
                        //ToastUtil.showToast(mContext, "抢完啦");
                        enterResult(openResult.getHongbaoid());
                        break;
                    case RedPacketOpenResult.status_hasGot:
                        //ToastUtil.showToast(mContext, "你已经抢了");
                        enterResult(openResult.getHongbaoid());
                        break;
                }
                break;

        }
    }

    void enterResult(long hongbaoId) {
        Intent intent = new Intent(mContext, RedPackectResultAct.class);
        intent.putExtra("hongbaoId", hongbaoId);
        intent.putExtra("targetId",targetId);
        mContext.startActivity(intent);
    }

}