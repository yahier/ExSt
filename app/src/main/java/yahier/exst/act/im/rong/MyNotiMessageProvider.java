package yahier.exst.act.im.rong;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.im.RongDB;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;

import java.util.StringTokenizer;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

/**
 * 通知信息。即 显示在会话页面水平中间的消息，当前只用作讨论组的创建，邀请，(和撒豆，打开金豆.当前关闭了这些功能)
 *
 * @author lsy
 */
@ProviderTag(messageContent = MyNotiMessage.class, showPortrait = false, showProgress = true, centerInHorizontal = true, showSummaryWithName = false)
// 会话界面自定义UI注解
public class MyNotiMessageProvider extends IContainerItemProvider.MessageProvider<MyNotiMessage> {

    Context mContext;

    /**
     * 初始化View
     */
    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_message_noti, null);
        ViewHolder holder = new ViewHolder();
        holder.tvContent = (TextView) view.findViewById(R.id.tvContent);
        view.setTag(holder);
        mContext = view.getContext();
        return view;
    }

    @Override
    public void bindView(View v, int position, MyNotiMessage content, UIMessage message) {
        if (content.getType() == null) return;
        ViewHolder holder = (ViewHolder) v.getTag();
        String senderUserId = message.getSenderUserId();
        RongDB db = new RongDB(mContext);
        IMAccount user = db.get(RongDB.typeUser, senderUserId);
        String userName = content.getOpname();
        //LogUtil.logE("userName 1",userName);
        if (user != null) {
            userName = user.getNickname();
            if (!TextUtils.isEmpty(user.getAlias())) {
                userName = user.getAlias();
            }
        }
        //LogUtil.logE("userName 2",userName);
        //如果发送者是当前人。就换个提示啦。
        if (senderUserId.equals(SharedToken.getUserId(mContext))) {
            userName = mContext.getString(R.string.im_you);
        }
        //LogUtil.logE("userName 3",userName);
        if (userName == null) userName = "";
        switch (content.getType()) {
            case NotificationType.typeAddDis:
                holder.tvContent.setText(userName + mContext.getString(R.string.im_Joined_the_group_discussion));
                break;
            case NotificationType.typeCreateDiscussion:
                holder.tvContent.setText(userName + mContext.getString(R.string.im_created_group_discussion));
                break;
            case NotificationType.typeOpenCastBean:
                holder.tvContent.setText(userName + mContext.getString(R.string.im_open_cast_bean));
                break;
            case NotificationType.typeOpenRedPackect:
                holder.tvContent.setText(userName + mContext.getString(R.string.im_open_red_package));
                break;
            case NotificationType.typeInviteJoin:
                //LogUtil.logE("no opIds", content.getOpedid());
                //LogUtil.logE("no name", content.getName());
                // String opname = content.getOpname();
                String name = content.getName();
                String id = content.getOpedid();
                StringBuffer contentValue = new StringBuffer();
                if (id != null) {
                    String[] names = name.split(",");
                    String[] ids = id.split(",");
                    try {
                        for (int i = 0; i < names.length; i++) {
                            if (ids[i].endsWith(SharedToken.getUserId())) {
                                contentValue.append("您,");
                                continue;
                            }
                            IMAccount account = db.get(RongDB.typeUser, ids[i]);
                            //LogUtil.logE("no ids", ids[i]);
                            if (account == null) {
                                contentValue.append(names[i]);
                                //  LogUtil.logE("append value 1", names[i]);
                            } else {
                                String value = account.getAlias();
                                if (value == null || value.equals("")) {
                                    value = account.getNickname();
                                }

                                // LogUtil.logE("append value 2", value);
                                contentValue.append(value);
                            }
                            contentValue.append(",");
                        }
                        if (names.length > 0) {
                            contentValue.deleteCharAt(contentValue.length() - 1);
                        }
                    } catch (Exception e) {
                        contentValue.append(name);
                    }

                } else {
                    contentValue.append(name);
                }


                // String name = content.getName();
                holder.tvContent.setText(userName + mContext.getString(R.string.im_invite) + contentValue.toString() + mContext.getString(R.string.im_join_group_discussion));
                break;
        }


    }


    @Override
    public Spannable getContentSummary(MyNotiMessage data) {
        return null;
    }

    @Override
    public void onItemClick(View view, int position, MyNotiMessage content, UIMessage message) {

    }

    @Override
    public void onItemLongClick(View view, int position, MyNotiMessage content, UIMessage message) {

    }

    class ViewHolder {
        //ImageView user_img;
        TextView tvContent;
        //TextView user_gender_age,user_city;
        //View view;
    }

}
