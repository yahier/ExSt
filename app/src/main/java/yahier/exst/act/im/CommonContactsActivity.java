package yahier.exst.act.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.tribe.TribeMainAct;
import com.stbl.stbl.adapter.msg.ContactsAdapter;
import com.stbl.stbl.adapter.msg.ContactsAdapter.IContactsAdapter;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.TaskError;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.SimpleTask;
import com.stbl.stbl.util.SimpleTask.Callback;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SideBar;
import com.stbl.stbl.widget.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;

public class CommonContactsActivity extends ThemeActivity {

    public static final String EXTRA_RELATION_TYPE = "relation_type";
    public static final String EXTRA_RELATION_NAME = "relation_name";

    private ListView mListView;
    private EditText mSearchEt;
    private ImageView mClearIv;

    private SideBar mSideBar;
    private TextView mFloatTv;

    private ArrayList<SortModel> mAllContactsList;
    private ContactsAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private boolean mIsDestroy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_contacts);

        initView();
        setListener();

        getContactsList();
    }

    private void initView() {
        String relationName = getIntent().getStringExtra(EXTRA_RELATION_NAME);
        setLabel(relationName);

        mSideBar = (SideBar) findViewById(R.id.sidrbar);
        mFloatTv = (TextView) findViewById(R.id.dialog);
        mSideBar.setTextView(mFloatTv);
        mClearIv = (ImageView) findViewById(R.id.ivClearText);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mListView = (ListView) findViewById(R.id.lv_contacts);

        /** 给ListView设置adapter **/
        mAllContactsList = new ArrayList<SortModel>();
        mAdapter = new ContactsAdapter(mAllContactsList);
        mListView.setAdapter(mAdapter);

        mLoadingDialog = new LoadingDialog(this);

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

        mAdapter.setInterface(mIContactsAdapter);

    }

    private IContactsAdapter mIContactsAdapter = new IContactsAdapter() {

        @Override
        public void onItemClick(UserItem user) {
           // SortModel model = mAllContactsList.get(position);
            Intent intent = new Intent(CommonContactsActivity.this,
                    TribeMainAct.class);
            intent.putExtra("userId", user.getUserid());
            startActivity(intent);
        }
    };

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
        int relationflag = getIntent().getIntExtra(EXTRA_RELATION_TYPE, 0);
        ContactsTask.getFriendList(2, relationflag, 0).setCallback(this, mGetContactsCallback).start();
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
