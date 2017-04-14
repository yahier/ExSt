package yahier.exst.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedUser;
import com.stbl.stbl.util.ToastUtil;

/**
 * Created by lenovo on 2016/4/26.
 * 新的添加好友类
 */
public class AddFriendUtil implements FinalHttpCallback {
    final int codeAdd = -200000;

    private Context mContext;
    private UserItem userItem;
    private FinalHttpCallback Finalback;

    /**
     * 当前类已经toast了服务端结果。如果调用类仍然需要处理，则需要传第二个参数。
     *
     * @param mContext
     * @param Finalback
     */
    public AddFriendUtil(Context mContext, FinalHttpCallback Finalback) {
        LogUtil.logE("AddFriendUtil");
        this.mContext = mContext;
        this.Finalback = Finalback;
    }

    /**
     * 直接添加好友
     */
    public void addFriendDirect(UserItem userItem) {
        this.userItem = userItem;
        Params params = new Params();
        params.put("objuserid", userItem.getUserid());
        new HttpEntity(mContext).commonPostData(Method.imAddFriendDirect, params, this);
    }

    /**
     * 直接添加好友
     */
    public void addFriendDirect(long userId, String nickName) {
        userItem = new UserItem();
        userItem.setUserid(userId);
        userItem.setNickname(nickName);
        addFriendDirect(userItem);
    }

    Dialog dialog;

    public void showAddWindow(final long userId, String userName) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (mContext == null) return;
        if (mContext != null && mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) return;
        }
        dialog = new Dialog(mContext, R.style.dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.im_add_friend_dialog, null);
        TextView window_title = (TextView) view.findViewById(R.id.window_title);
        int width = Device.getWidth() - 100;
        view.measure(0, 0);
        int height = view.getMeasuredHeight();
        dialog.setContentView(view, new LinearLayout.LayoutParams(width, height));
        dialog.show();
        // String newMessageInfo = "添加<font color='#DEA524'>" + userName + "</font>为好友";
        String value = String.format(mContext.getString(R.string.common_add_friend_to_user), userName);
        window_title.setText(Html.fromHtml(value));
        final EditText input = (EditText) view.findViewById(R.id.input);
        input.setText("我是" + SharedUser.getUserNick());
        InputMethodUtils.showInputMethodDelay(input, 200);
        final View btnOk = view.findViewById(R.id.btnOk);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnCancel:
                        dialog.dismiss();
                        break;
                    case R.id.btnOk:
                        btnOk.setEnabled(false);
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnOk.setEnabled(true);
                            }
                        }, Config.interClickTime);
                        String str = input.getText().toString();
                        addFriend(userId, str);
                        break;
                }

            }
        };
        view.findViewById(R.id.btnCancel).setOnClickListener(listener);

        btnOk.setOnClickListener(listener);
    }

    void addFriend(long touserid, String reason) {
        Params params = new Params();
        params.put("touserid", touserid);
        params.put("msg", reason);
        params.put("applytype", UserItem.addRelationTypeFriend);
        new HttpEntity(mContext).commonPostData(Method.imAddRelation, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        LogUtil.logE("add_util", "parse");
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            if (!methodName.equals(Method.imAddFriendDirect) && item.getIssuccess() != BaseItem.errorNoTaostTag) {
                ToastUtil.showToast(mContext, item.getErr().getMsg());
            }
            switch (methodName) {
                case Method.imAddFriendDirect:
                    if (item.getErr().getErrcode() == codeAdd) {
                        showAddWindow(userItem.getUserid(), userItem.getNickname());
                    } else {
                        ToastUtil.showToast(mContext, item.getErr().getMsg());
                    }
                    if (listener != null) {
                        listener.isSuccess(false);
                    }
                    break;
            }
            return;
        }
        //String obj = JSONHelper.getStringFromObject(item.getResult());
        if (Finalback != null)
            Finalback.parse(methodName, result);
        dialog.dismiss();
        switch (methodName) {
            case Method.imAddFriendDirect:
                ToastUtil.showToast("好友添加成功");
                if (listener != null) {
                    listener.isSuccess(true);
                }
//                FriendsDB db = new FriendsDB(mContext);
                // FriendsDB db = FriendsDB.getInstance(mContext);
                //db.insert(userItem.getUserid(),userItem.getNickname(),userItem.getImgurl());
                break;
            case Method.imAddRelation:
                ToastUtil.showToast("已经发送好友申请");
                break;
        }
    }

    private SendAddStatusListener listener;

    public AddFriendUtil setSendAddStatusListener(SendAddStatusListener listener) {
        this.listener = listener;
        return this;
    }
    /**加好友成功回调*/
    public interface SendAddStatusListener {
        void isSuccess(boolean isSuccess);
    }
}
