package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.base.library.task.TaskCallback;
import com.stbl.base.library.task.TaskError;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mine.AddCardAdapter;
import com.stbl.stbl.common.BaseFragment;
import com.stbl.stbl.dialog.LoadingDialog;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.task.msg.ContactsTask;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.HttpTaskCallback;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SideBar;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class AddCardFragment extends BaseFragment {

    private ListView mListView;
    private EditText mSearchEt;
    private ImageView mClearIv;

    private SideBar mSideBar;
    private TextView mFloatTv;

    private ArrayList<SortModel> mAllContactsList;
    private AddCardAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_card, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListener();
        getContactsList();
    }

    private void initView() {
        mSideBar = (SideBar) getView().findViewById(R.id.sidrbar);
        mFloatTv = (TextView) getView().findViewById(R.id.dialog);
        mSideBar.setTextView(mFloatTv);
        mClearIv = (ImageView) getView().findViewById(R.id.ivClearText);
        mSearchEt = (EditText) getView().findViewById(R.id.et_search);
        mListView = (ListView) getView().findViewById(R.id.lv_contacts);

        mAllContactsList = new ArrayList<>();
        mAdapter = new AddCardAdapter(mAllContactsList);
        mListView.setAdapter(mAdapter);

        mLoadingDialog = new LoadingDialog(mActivity);
    }

    private void setListener() {
        mListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mActivity.getCurrentFocus() != null
                            && mActivity.getCurrentFocus().getWindowToken() != null) {
                        InputMethodUtils.getInputMethodManager().hideSoftInputFromWindow(mActivity.getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        /** 清除输入字符 **/
        mClearIv.setOnClickListener(new View.OnClickListener() {

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
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

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
    }

    private AddCardAdapter.ISingleSelectFriendAdapter mISelectFriendAdapter = new AddCardAdapter.ISingleSelectFriendAdapter() {

        @Override
        public void onItemClick(SortModel model) {
            Intent intent = new Intent();
            intent.putExtra(KEY.TYPE, PublishShoppingLinkActivity.TYPE_CARD);
            intent.putExtra(EXTRA.USER_ITEM, model.user);
            mActivity.setResult(mActivity.RESULT_OK, intent);
            mActivity.finish();
        }
    };

    /**
     * 获取好友通讯录列表
     */
    private void getContactsList() {
        mLoadingDialog.show();
        mTaskManager.start(ContactsTask.getContactsList(2, 0, 1)
                .setCallback(new HttpTaskCallback<ArrayList<SortModel>>(mActivity) {

                    @Override
                    public void onFinish() {
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onError(TaskError e) {
                        super.onError(e);
                        ToastUtil.showToast(e.msg);
                    }

                    @Override
                    public void onSuccess(ArrayList<SortModel> result) {
                        mAllContactsList.clear();
                        mAllContactsList.addAll(result);
                        mAdapter.updateListView(mAllContactsList);
                        if (mAllContactsList.size() > 0) {
                            mListView.setSelection(0);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }));
    }

    /**
     * 搜索
     *
     * @param str
     */
    private void search(String str) {
        mTaskManager.start(ContactsTask.searchContacts(mAllContactsList, str)
                .setCallback(new TaskCallback<ArrayList<SortModel>>() {

                    @Override
                    public void onError(TaskError e) {

                    }

                    @Override
                    public void onSuccess(ArrayList<SortModel> result) {
                        mAdapter.updateListView(result);
                    }
                }));
    }

}
