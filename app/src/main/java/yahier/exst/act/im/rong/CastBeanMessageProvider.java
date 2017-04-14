package yahier.exst.act.im.rong;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.CastBeanResultAct;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.im.RedPacket;
import com.stbl.stbl.item.im.RedPacketOpenResult;
import com.stbl.stbl.item.im.RedPacketPickResult;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.ToastUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * 自定义融云IM消息提供者
 *
 * @author lsy
 *
 */
@ProviderTag(messageContent = CastBeanMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class CastBeanMessageProvider extends IContainerItemProvider.MessageProvider<CastBeanMessage> implements FinalHttpCallback {

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
    public void bindView(View v, int position, CastBeanMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder) v.getTag();
        // 更改气泡样式
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            // 消息方向，自己发送的
            holder.view.setBackgroundResource(R.drawable.im_group_castbean_right_bg);
        } else {
            // 消息方向，别人发送的
            holder.view.setBackgroundResource(R.drawable.im_group_castbean_left_bg);
        }
        targetId = message.getTargetId();
        String str=  JSONHelper.getExParams(content.getEx_params());
        RedPacket pcikResult = JSONHelper.getObject(str, RedPacket.class);
        holder.message.setText(pcikResult.getHongbaodesc()); // 设置消息内容
    }

    @Override
    public Spannable getContentSummary(CastBeanMessage data) {
        return null;
    }

    @Override
    public void onItemClick(View view, int position, CastBeanMessage content, UIMessage message) {
        String str=  JSONHelper.getExParams(content.getEx_params());
        RedPacket pcikResult = JSONHelper.getObject(str, RedPacket.class);
        openRedPacket(pcikResult.getHongbaoid());
    }

    Dialog dialog;

    private void showWindow(final long hongbaoId) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.im_group__red_packet_window, null);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
        dialog.show();

        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.imgClose:
                        dialog.dismiss();
                        break;
                    case R.id.tvOpen:
                        dialog.dismiss();
                        pickRedPacket(hongbaoId);
                        break;
                }

            }
        };
        view.findViewById(R.id.imgClose).setOnClickListener(listener);
        view.findViewById(R.id.tvOpen).setOnClickListener(listener);
    }

    void openRedPacket(long hongbaoid) {
        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(mContext).commonPostData(Method.imOpenRedPacket, params, this);
    }

    void pickRedPacket(long hongbaoid) {
        Params params = new Params();
        params.put("hongbaoid", hongbaoid);
        new HttpEntity(mContext).commonPostData(Method.imPickRedPacket, params, this);
    }

    @Override
    public void onItemLongClick(View view, int position, CastBeanMessage content, UIMessage message) {


    }

    class ViewHolder {
        TextView message;
        View view;

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
            case Method.imPickRedPacket:
                RedPacketPickResult pcikResult = JSONHelper.getObject(obj, RedPacketPickResult.class);
                LogUtil.logE("imPickRedPacket states:" + pcikResult.getStatus());
                //enterResult(pcikResult.getHongbaoid());
                switch (pcikResult.getStatus()) {
                    case RedPacketPickResult.status_avilable:
                        //ToastUtil.showToast(mContext, "status_avilable");
                        sendNotiMessage();
                        enterResult(pcikResult.getHongbaoid());
                        break;
                    case RedPacketPickResult.status_expire:
                        ToastUtil.showToast(mContext, mContext.getString(R.string.im_exceed_time_limit));
                        break;
                    case RedPacketPickResult.status_finished:
                        ToastUtil.showToast(mContext, mContext.getString(R.string.im_grab_finish));
                        //enterResult(pcikResult.getHongbaoid());
                        break;
                    case RedPacketPickResult.status_hasGot:
                        ToastUtil.showToast(mContext, mContext.getString(R.string.im_congratulation_grab_red_package));
                        enterResult(pcikResult.getHongbaoid());
                        break;
                }
                break;
            case Method.imOpenRedPacket:
                RedPacketOpenResult openResult = JSONHelper.getObject(obj, RedPacketOpenResult.class);
                LogUtil.logE("imOpenRedPacket states:" + openResult.getStatus());
                //enterResult(openResult.getHongbaoid());
                switch (openResult.getStatus()) {
                    case RedPacketOpenResult.status_avilable:
                        showWindow(openResult.getHongbaoid());
                        break;
                    case RedPacketOpenResult.status_expire:
                        ToastUtil.showToast(mContext, mContext.getString(R.string.im_exceed_time_limit));
                        break;
                    case RedPacketOpenResult.status_finished://抢完了
                        enterResult(openResult.getHongbaoid());
                        break;
                    case RedPacketOpenResult.status_hasGot://已经抢了
                        enterResult(openResult.getHongbaoid());
                        break;
                }

                break;
        }

    }

    void sendNotiMessage(){
        LogUtil.logE("targetId:"+targetId);
        MyNotiMessage mesage = MyNotiMessage.obtain(String.valueOf(NotificationType.typeOpenCastBean));
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.GROUP, targetId, mesage, null, null, null);
    }

    void enterResult(long hongbaoId) {
        Intent intent = new Intent(mContext, CastBeanResultAct.class);
        intent.putExtra("hongbaoId", hongbaoId);
        mContext.startActivity(intent);
    }

}