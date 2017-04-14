package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.msg.ContactsAdapter;
import com.stbl.stbl.adapter.msg.ContactsAdapter.IContactsAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.EventUpdateAlias;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.GroupMemberList;
import com.stbl.stbl.item.im.IMEventType;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.DimenUtils;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SideBar;
import com.stbl.stbl.widget.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;

/**
 * 通讯录页面
 */
public class ContactsActivity extends ThemeActivity {

    private ListView mListView;
    private EditText mSearchEt;
    private ImageView mClearIv;

    private SideBar mSideBar;
    private TextView mFloatTv;

    private ArrayList<SortModel> mAllContactsList;
    private ContactsAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    private TextView footerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        EventBus.getDefault().register(this);
        initView();
        setListener();
        getContactsList();
        checkApplyCount();
    }

    void checkApplyCount() {
        int applyCount = getIntent().getIntExtra("applyCount", 0);
        if (applyCount > 0) {
            tvCount.setText(String.valueOf(applyCount));
            tvCount.setVisibility(View.VISIBLE);
        }
    }

    TextView tvCount;

    private void initView() {
        setLabel(R.string.contacts_list);

        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mFloatTv = (TextView) findViewById(R.id.dialog);
        mSideBar.setTextView(mFloatTv);
        mClearIv = (ImageView) findViewById(R.id.ivClearText);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mListView = (ListView) findViewById(R.id.lv_contacts);

        View headerView = LayoutInflater.from(this).inflate(
                R.layout.header_contacts_activity, null);
        tvCount = (TextView) headerView.findViewById(R.id.tvCount);
        headerView.findViewById(R.id.layout_add_friend).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ContactsActivity.this,
                                FriendAddAct.class);
                        startActivity(intent);
                    }
                });
        //新的朋友
        headerView.findViewById(R.id.layout_new_friend).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAct(ApplyAddListAct.class);

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvCount.setVisibility(View.GONE);
                    }
                }, 200);

            }
        });
        //讨论组
        headerView.findViewById(R.id.layout_discussion).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAct(DiscussionCollectionListAct.class);
            }
        });
        headerView.findViewById(R.id.layout_friend_category)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ContactsActivity.this,
                                FriendClassAct.class);
                        startActivity(intent);
                    }
                });
        mListView.addHeaderView(headerView);

        /** 给ListView设置adapter **/
        mAllContactsList = new ArrayList<SortModel>();
        mAdapter = new ContactsAdapter(mAllContactsList);
        mListView.setAdapter(mAdapter);

        mLoadingDialog = new LoadingDialog(this);

        footerview = new TextView(this);
        footerview.setTextSize(15);
        footerview.setGravity(Gravity.CENTER);
        footerview.setTextColor(getResources().getColor(R.color.gray_a5));
        footerview.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dp2px(60)));
    }

    private void setListener() {
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (getCurrentFocus() != null
                            && getCurrentFocus().getWindowToken() != null) {
                        InputMethodUtils.getInputMethodManager().hideSoftInputFromWindow(getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
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
                    addFootView(mAllContactsList.size());
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
                    mListView.setSelection(position + 1);
                }
            }
        });

        mAdapter.setInterface(mIContactsAdapter);
    }

    private IContactsAdapter mIContactsAdapter = new IContactsAdapter() {

        @Override
        public void onItemClick(UserItem user) {
            //SortModel model = mAllContactsList.get(position);
//            Intent intent = new Intent(ContactsActivity.this,
//                    TribeMainAct.class);
//            intent.putExtra("userId", userId);
//            startActivity(intent);
            Intent intent = new Intent(ContactsActivity.this,
                    FriendIntroActivity.class);
            intent.putExtra(KEY.USER_ITEM, user);
            startActivity(intent);
        }
    };

    public void onEvent(IMEventType type) {
        switch(type.getType()) {
            case IMEventType.typeUpdateContactList:
            case IMEventType.typeChangedContact:
                getContactsList();
                break;
        }
    }

    public void onEvent(EventUpdateAlias event) {
        if (event != null) {
            if (event.getUserid() != 0) {
                for (int i = 0; i < mAllContactsList.size(); i++) {
                    UserItem item = mAllContactsList.get(i).user;
                    if (item != null && item.getUserid() == event.getUserid()) {
                        item.setAlias(event.getAlias());
                    }
                }
//                mAdapter.notifyDataSetChanged();
                getContactsList();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mIsDestroy = true;
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 获取好友通讯录列表
     */
    private void getContactsList() {
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
        ContactsTask.getFriendList(GroupMemberList.typeRequestNoneGroup, 0, GroupMemberList.hasselfNo).setCallback(this, mGetContactsCallback).start();
    }

    private void addFootView(int count) {
        footerview.setText(String.format(getString(R.string.im_contact_count), count));
        if (mListView.getFooterViewsCount() == 0) {
            mListView.addFooterView(footerview);
        }
    }

    private SimpleTask.Callback<ArrayList<SortModel>> mGetContactsCallback = new Callback<ArrayList<SortModel>>() {

        @Override
        public void onError(TaskError e) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(e.getMessage());
        }

        @Override
        public void onCompleted(final ArrayList<SortModel> result) {
            mLoadingDialog.dismiss();
            mAllContactsList.clear();
            mAllContactsList.addAll(result);
            mAdapter.updateListView(mAllContactsList);
            if (mAllContactsList.size() > 0) {
                mListView.setSelection(0);
            }
            addFootView(mAllContactsList.size());

            //加好友数据库
//            HandleAsync hand = new HandleAsync();
//            hand.excute(new HandleAsync.Listener() {
//                @Override
//                public String getResult() {
//                    FriendsDB db = FriendsDB.getInstance(ContactsActivity.this);
//                    db.deleteAllData();
//                    for (int i = 0; i < result.size(); i++) {
//                        UserItem useritem = result.get(i).user;
//                        db.insert(useritem);
//                    }
//                    return null;
//                }
//
//                @Override
//                public void parse(String result) {
//
//                }
//            });


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

    private SimpleTask.Callback<ArrayList<SortModel>> mSearchCallback = new Callback<ArrayList<SortModel>>() {

        @Override
        public void onError(TaskError e) {

        }

        @Override
        public void onCompleted(ArrayList<SortModel> result) {
            mAdapter.updateListView(result);
            addFootView(result.size());
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
