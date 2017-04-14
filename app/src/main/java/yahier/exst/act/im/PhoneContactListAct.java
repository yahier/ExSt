package yahier.exst.act.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.msg.ContactsSortAdapter;
import com.stbl.stbl.adapter.msg.ContactsSortAdapter.IContactsSortAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.AddFriendDialog;
import com.stbl.stbl.dialog.AddFriendDialog.IAddFriendDialog;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.task.AddFriendUtil;
import com.stbl.stbl.task.mine.UserTask;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SideBar;
import com.stbl.stbl.widget.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PhoneContactListAct extends ThemeActivity {

    private ListView mListView;
    private EditText mSearchEt;
    private ImageView mClearIv;

    private SideBar mSideBar;
    private TextView mFloatTv;

    private ArrayList<SortModel> mAllContactsList;
    private ContactsSortAdapter mAdapter;

    private AddFriendDialog mAddDialog;
    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contacts);

        initView();
        setListener();
        getContactsList();
    }

    private void initView() {
        setLabel(R.string.phone_contacts);
        setRightText(R.string.obtain_the_last, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContactsList();
            }
        });

        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mFloatTv = (TextView) findViewById(R.id.dialog);
        mSideBar.setTextView(mFloatTv);
        mClearIv = (ImageView) findViewById(R.id.ivClearText);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mListView = (ListView) findViewById(R.id.lv_contacts);

        /** 给ListView设置adapter **/
        mAllContactsList = new ArrayList<SortModel>();
        mAdapter = new ContactsSortAdapter(mAllContactsList);
        mListView.setAdapter(mAdapter);

        mAdapter.setInterface(mAdapterInterface);

//        mAddDialog = new AddFriendDialog(this);
//        mAddDialog.setInterface(mDialogInterface);

        mLoadingDialog = new LoadingDialog(this);

    }

    private void setListener() {
        /** 清除输入字符 **/
        mClearIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSearchEt.setText("");
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                String content = mSearchEt.getText().toString();
                if ("".equals(content)) {
                    mClearIv.setVisibility(View.INVISIBLE);
                } else {
                    mClearIv.setVisibility(View.VISIBLE);
                }
                if (content.length() > 0) {
                    search(content);
                } else {
                    mAdapter.updateListView(mAllContactsList);
                }
                mListView.setSelection(0);
            }

        });

        // 设置右侧[A-Z]快速导航栏触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
//        if (mAddDialog.isShowing()) {
//            mAddDialog.dismiss();
//        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    SortModel selectedModel;
    private IContactsSortAdapter mAdapterInterface = new IContactsSortAdapter() {

        @Override
        public void onInvite(SortModel model) {
            selectedModel = model;
//            String mobile = model.number;
//            Uri smsToUri = Uri.parse("smsto:" + mobile);
//            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//            intent.putExtra("sms_body", "我在师徒部落，邀请码:" + SharedUser.getInviteCode(MyApplication.getContext())
//                    + "，快来加入喔！！#创立你自己的人脉资源平台# http://www.stbl.cc/");
//            startActivity(intent);
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
            getInvite();
        }

        @Override
        public void onAddFriend(SortModel user) {
//            if (!mAddDialog.isShowing()) {
//                mAddDialog.show();
//            }
            //mAddDialog.setUserInfo(user.user.getUserid(), user.user.getNickname());
            new AddFriendUtil(PhoneContactListAct.this, null).addFriendDirect(user.user);
        }

        @Override
        public void onItemClick(int position) {
            SortModel model = mAllContactsList.get(position);
            if (model.user != null && model.user.getUserid() != 0) {
                Intent intent = new Intent(PhoneContactListAct.this,
                        TribeMainAct.class);
                intent.putExtra("userId", model.user.getUserid());
                startActivity(intent);
            }
        }
    };

    private IAddFriendDialog mDialogInterface = new IAddFriendDialog() {

        @Override
        public void onConfirm(long userId, String msg) {
            addFriend(userId, msg);
        }
    };

    /**
     * 获取手机通讯录列表
     */
    private void getContactsList() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        ContactsTask.getPhoneContactsList().setCallback(this, mGetContactsCallback).start();
    }

    private SimpleTask.Callback<ArrayList<SortModel>> mGetContactsCallback = new Callback<ArrayList<SortModel>>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(ArrayList<SortModel> result) {
            mLoadingDialog.dismiss();
            mAllContactsList.clear();
            mAllContactsList.addAll(result);
            mAdapter.updateListView(mAllContactsList);
            if (mAllContactsList.size() > 0) {
                mListView.setSelection(0);
            }
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    /**
     * 搜索
     *
     * @param str
     */
    private void search(String str) {
        ContactsTask.search(mAllContactsList, str).setCallback(this, mSearchCallback).start();
    }


    /**
     * 获取邀请文字
     *
     * @param
     */
    private void getInvite() {
        ContactsTask.getInviteMsg().setCallback(this, mInviteCallback).start();
    }


    private SimpleTask.Callback<ArrayList<SortModel>> mSearchCallback = new Callback<ArrayList<SortModel>>() {

        @Override
        public void onError(TaskError e) {

        }

        @Override
        public void onCompleted(ArrayList<SortModel> result) {
            mAdapter.updateListView(result);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

    private void addFriend(long touserid, final String msg) {
        UserTask.addRelation(touserid, msg, UserItem.addRelationTypeFriend).setCallback(this, mAddFriendCallback).start();
    }

    private SimpleTask.Callback<Integer> mAddFriendCallback = new Callback<Integer>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(Integer result) {
            ToastUtil.showToast(R.string.sent_friend_apply);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };


    private SimpleTask.Callback<HashMap<String, Object>> mInviteCallback = new SimpleTask.Callback<HashMap<String, Object>>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(HashMap<String, Object> result) {
            mLoadingDialog.dismiss();
            if(selectedModel==null)return;
            String msg = (String) result.get("smstext");
            String mobile = selectedModel.number;
            Uri smsToUri = Uri.parse("smsto:" + mobile);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body",msg );
            startActivity(intent);
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}