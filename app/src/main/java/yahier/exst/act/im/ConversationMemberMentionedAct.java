package yahier.exst.act.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.act.im.rong.ConversationActivity;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.IMSetting;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.WaitingDialog;
import com.stbl.stbl.widget.RoundImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.mention.RongMentionManager;
import io.rong.imlib.model.UserInfo;


/**
 * Created by yahier on 17/2/24.
 */

public class ConversationMemberMentionedAct extends ThemeActivity implements FinalHttpCallback {
    EditText input;
    ListView listView;
    Adapter adapter;
    String groupid;
    ArrayList<UserItem> list;
    public final static int typeGroup = 1;
    public final static int typeDiscussion = 2;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_member_mentioned_act);
        setLabel("选择联系人");
        groupid = getIntent().getStringExtra("groupid");
        type = getIntent().getStringExtra("type");
        initViews();
        getData();

    }

    void initViews() {
        input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        input.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (list == null) return;
                ArrayList<UserItem> listNew = new ArrayList<UserItem>();
                String value = arg0.toString();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getNickname().contains(value)) {
                        listNew.add(list.get(i));
                    }
                }
                LogUtil.logE("size..", listNew.size());
                adapter.setData(listNew);

            }
        });
    }


    void getData() {
        WaitingDialog.show(this, R.string.loading);
        LogUtil.logE("groupid", groupid + "");
        Params params = new Params();
        params.put("groupid", groupid);
        switch (type) {
            case ConversationActivity.typeLocalGroup:
                params.put("type", typeGroup);
                break;
            case ConversationActivity.typeLocalDiscussion:
                params.put("type", typeDiscussion);
                break;
        }
        new HttpEntity(this).commonPostData(Method.imShowMembersTiny, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        WaitingDialog.dismiss();
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item == null || item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, getString(R.string.im_request_err));
            return;
        }
        switch (methodName) {
            case Method.imShowMembersTiny:
                String obj = JSONHelper.getStringFromObject(item.getResult());
                list = JSONHelper.getList(obj, UserItem.class);
                adapter.setData(list);
                break;
        }


    }


    /**
     * Created by yahier on 16/11/10.
     */

    class Adapter extends BaseAdapter {

        private ArrayList<UserItem> mList;
        private LayoutInflater mInflater;

        public Adapter() {
            mInflater = LayoutInflater.from(MyApplication.getContext());
            mList = new ArrayList<>();
        }

        public void setData(ArrayList<UserItem> list) {
            mList = list;
            notifyDataSetChanged();
        }

        public int getCount() {
            return mList.size();
        }

        public Object getItem(int position) {
            return mList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_conversation_member_mentioned, parent, false);

                holder.mNameTv = (TextView) convertView.findViewById(R.id.tv_name);
                holder.mHeadIv = (RoundImageView) convertView.findViewById(R.id.iv_head);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            bindViewHolder(position, holder);


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserItem userItem = mList.get(position);
                    Uri uri = Uri.parse(userItem.getImgurl());
                    UserInfo user = new UserInfo(String.valueOf(userItem.getUserid()), userItem.getNickname(), uri);
                    RongMentionManager.getInstance().mentionMember(user);
                    finish();

                }
            });
            return convertView;
        }

        private void bindViewHolder(int position, ViewHolder holder) {
            UserItem user = mList.get(position);
            ImageUtils.loadCircleHead(user.getImgurl(), holder.mHeadIv);
            holder.mNameTv.setText(TextUtils.isEmpty(user.getAlias()) ? user.getNickname() : user.getAlias());

        }

        class ViewHolder {
            public TextView mIndexTv;
            public RoundImageView mHeadIv;
            public TextView mNameTv;
        }


    }


}

