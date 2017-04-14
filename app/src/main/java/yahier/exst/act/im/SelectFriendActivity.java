package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.msg.SelectFriendAdapter;
import com.stbl.stbl.adapter.msg.SelectFriendAdapter.ISelectFriendAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.DiscussionMember;
import com.stbl.stbl.item.im.UserList;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SideBar;
import com.stbl.stbl.widget.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的选择好友页面
 */
public class SelectFriendActivity extends ThemeActivity {

    public static final String EXTRA_SELECTED_FRIEND_LIST = "selected_friend_list";
    private ArrayList<UserItem> mSelectedFriendList;

    private ListView mListView;
    private EditText mSearchEt;
    private ImageView mClearIv;

    private SideBar mSideBar;
    private TextView mFloatTv;

    private Button mConfirmBtn;

    private ArrayList<SortModel> mAllContactsList;
    private SelectFriendAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;
    private int maxSelected;//最大可选人数

    private boolean mShowIndexSelect;

    private boolean mUseSelectAll;//使用全选功能;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        initView();
        setListener();
        getContactsList();
        maxSelected = getIntent().getIntExtra("maxSelected", 1000);
        //新加。
        DiscussionMember discussionTeam = (DiscussionMember) getIntent().getSerializableExtra("members");
        if (discussionTeam != null) {
            List<UserItem> userList = discussionTeam.getMembers();
            mAdapter.setMembers(userList);
        }

    }

    private void initView() {
        mShowIndexSelect = getIntent().getBooleanExtra(KEY.SHOW_INDEX_SELECT, false);
        setLabel(mShowIndexSelect ? R.string.im_select_receiver : R.string.im_select_contacts);

        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mFloatTv = (TextView) findViewById(R.id.dialog);
        mSideBar.setTextView(mFloatTv);
        mClearIv = (ImageView) findViewById(R.id.ivClearText);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mListView = (ListView) findViewById(R.id.lv_contacts);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);

        /** 给ListView设置adapter **/
        mAllContactsList = new ArrayList<>();
        mAdapter = new SelectFriendAdapter(mAllContactsList);
        mAdapter.setShowIndexSelect(mShowIndexSelect);
        mListView.setAdapter(mAdapter);

        UserList userList = (UserList) getIntent().getSerializableExtra(
                EXTRA_SELECTED_FRIEND_LIST);
        if (userList == null) {
            mSelectedFriendList = new ArrayList<>();
        } else {
            mSelectedFriendList = (ArrayList<UserItem>) userList.getList();

        }


        mLoadingDialog = new LoadingDialog(this);

//        setRightTextListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                toggleSelect();
//            }
//        });

    }

    private void setListener() {
        mListView.setOnTouchListener(new View.OnTouchListener() {

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
                if (TextUtils.isEmpty(content)) {
                    mClearIv.setVisibility(View.INVISIBLE);
                    //setRightTextVisibility(View.VISIBLE);
                    mAdapter.setShowIndexSelect(mShowIndexSelect);
                    mAdapter.updateListView(mAllContactsList);
                } else {
                    mClearIv.setVisibility(View.VISIBLE);
                    //setRightTextVisibility(View.GONE);
                    mAdapter.setShowIndexSelect(false);
                    search(content);
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

        mAdapter.setInterface(mISelectFriendAdapter);

        mConfirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<SortModel> modelList = mAdapter.getSelectedList();
                int size = modelList.size();
                if (size == 0) {
                    ToastUtil.showToast(R.string.has_no_choose_friend);
                    return;
                }
                if (size > maxSelected) {
                    ToastUtil.showToast(String.format(getString(R.string.im_as_most_select), maxSelected));
                    return;
                }
                UserList userList = new UserList();
                ArrayList<UserItem> itemList = new ArrayList<UserItem>();
                for (SortModel model : modelList) {
                    itemList.add(model.user);
                }
                userList.setList(itemList);
                Intent intent = new Intent();
                intent.putExtra(KEY.USE_SELECT_ALL, mUseSelectAll);
                intent.putExtra("users", userList);
                setResult(RESULT_OK, intent);
                finish();
            }

        });
    }

    private ISelectFriendAdapter mISelectFriendAdapter = new ISelectFriendAdapter() {

        @Override
        public void onItemClick(int position) {
            // SelectFriendAdapter.ViewHolder holder =
            // (SelectFriendAdapter.ViewHolder) view
            // .getTag();
            // holder.mSelectCb.performClick();
            //mAdapter.toggleChecked(position);
            setConfirmBtnText();
            //setSelectText();
        }

        @Override
        public void onSelectIndex() {
            setConfirmBtnText();
            //setSelectText();
            mUseSelectAll = true;
        }
    };

    private void toggleSelect() {
        //应该是拿选中的和全部可选的来比较
        int size = mAdapter.getSelectedList().size();
        List<UserItem> listMembers = mAdapter.getMembers();
        int ableSizeMax = 0;
        for (int i = 0; i < mAllContactsList.size(); i++) {
            long userId = mAllContactsList.get(i).user.getUserid();
            boolean isAbleChoose = true;
            for (int j = 0; j < listMembers.size(); j++) {
                if (userId == listMembers.get(j).getUserid()) {
                    isAbleChoose = false;
                    break;
                }
            }
            if (isAbleChoose) {
                ableSizeMax++;
            }
        }


        if (size < ableSizeMax) {
            selectAll();
            //setSelectText();
        } else {
            selectNone();
            //setSelectText();
        }
    }

    private void selectAll() {
        if (mAllContactsList.size() > 0) {
            mAdapter.selectAll();
            setConfirmBtnText();
        }
    }

    private void selectNone() {
        if (mAllContactsList.size() > 0) {
            mAdapter.selectNone();
            setConfirmBtnText();
        }
    }

    private void setConfirmBtnText() {
        int size = mAdapter.getSelectedList().size();
        if (size > 0) {
            mConfirmBtn.setEnabled(true);
            mConfirmBtn.setText(getString(R.string.queding) + "(" + size + ")");
        } else {
            mConfirmBtn.setEnabled(false);
            mConfirmBtn.setText(R.string.queding);
        }
    }

    private void setSelectText() {
        int size = mAdapter.getSelectedList().size();
        List<UserItem> listMembers = mAdapter.getMembers();
        int ableSizeMax = 0;
        for (int i = 0; i < mAllContactsList.size(); i++) {
            long userId = mAllContactsList.get(i).user.getUserid();
            boolean isAbleChoose = true;
            for (int j = 0; j < listMembers.size(); j++) {
                if (userId == listMembers.get(j).getUserid()) {
                    isAbleChoose = false;
                    break;
                }
            }
            if (isAbleChoose) {
                ableSizeMax++;
            }
        }
        //新加判断
//        if (size < ableSizeMax) {
//            setActionText(R.string.choose_all);
//        } else {
//            setActionText(R.string.cancel_choose_all);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        ContactsTask.getFriendList(2, 0, 0).setCallback(this, mGetContactsCallback).start();
    }

    private SimpleTask.Callback<ArrayList<SortModel>> mGetContactsCallback = new Callback<ArrayList<SortModel>>() {

        @Override
        public void onError(TaskError e) {
            ToastUtil.showToast(e.getMessage());
            mLoadingDialog.dismiss();
        }

        @Override
        public void onCompleted(ArrayList<SortModel> result) {
            mLoadingDialog.dismiss();
            mAllContactsList.clear();
            mAllContactsList.addAll(result);
            mAdapter.initIndexCountArray();
            mAdapter.updateListView(mAllContactsList);
            if (mAllContactsList.size() > 0) {
                mListView.setSelection(0);
            }
            //setRightTextVisibility(View.VISIBLE);
            ArrayList<SortModel> selectedList = mAdapter.getSelectedList();
            if (mSelectedFriendList.size() == 0) {
                //setRightText(R.string.choose_all);
                return;
            }
            for (SortModel model : mAllContactsList) {
                for (UserItem item : mSelectedFriendList) {
                    if (model.user.getUserid() == item.getUserid()) {
                        selectedList.add(model);
                        break;
                    }
                }
            }
            mAdapter.initIndexSelectArray();
            mAdapter.notifyDataSetChanged();
            setConfirmBtnText();
            //setSelectText();
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
        }

        @Override
        public boolean onDestroy() {
            return mIsDestroy;
        }
    };

}
