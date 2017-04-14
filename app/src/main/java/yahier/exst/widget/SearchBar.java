package yahier.exst.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.util.InputMethodUtils;
import com.stbl.stbl.util.ToastUtil;

/**
 * Created by lingrui on 2016/3/12.
 */
public class SearchBar extends RelativeLayout {

    private EditText mKeywordEt;

    private ISearchBar mInterface;

    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.bar_search, this);
        initView();
    }

    private void initView() {
        mKeywordEt = (EditText) findViewById(R.id.et_keyword);
        mKeywordEt.requestFocus();
        final ImageView clearIv = (ImageView) findViewById(R.id.iv_clear);

        mKeywordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                if (TextUtils.isEmpty(content)) {
                    clearIv.setVisibility(View.GONE);
                    if (mInterface != null) {
                        mInterface.onClear();
                    }
                } else {
                    clearIv.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onBack();
                }
            }
        });

        clearIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeywordEt.setText("");
            }
        });

        findViewById(R.id.tv_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mKeywordEt.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    ToastUtil.showToast(R.string.me_please_input_keyword);
                    return;
                }
                if (mInterface != null) {
                    mInterface.onSearch(keyword);
                }
            }
        });
    }

    public void setKeyword(String keyword) {
        mKeywordEt.setText(keyword);
        mKeywordEt.setSelection(mKeywordEt.getText().toString().length());
    }

    public void setSearchHint(CharSequence text) {
        mKeywordEt.setHint(text);
    }

    public void hideKeyboard() {
        InputMethodUtils.hideInputMethod(mKeywordEt);
    }

    public void showKeyboard() {
        InputMethodUtils.showInputMethod(mKeywordEt);
    }

    public void setInterface(ISearchBar i) {
        mInterface = i;
    }

    public interface ISearchBar {
        void onBack();

        void onSearch(String keyword);

        void onClear();
    }
}
