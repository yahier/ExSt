package yahier.exst.act.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.TextListener;
import com.stbl.stbl.util.ToastUtil;

/**
 * Created by Administrator on 2016/11/10.
 */

public class EditTextActivity extends ThemeActivity {

    private EditText mEt;
    private String mOriginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        initView();
    }

    private void initView() {
        setLabel(getString(R.string.me_edit));

        mEt = (EditText) findViewById(R.id.et);
        findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEt.setText("");
            }
        });
        mOriginText = getIntent().getStringExtra(KEY.TEXT);
        if (!TextUtils.isEmpty(mOriginText)) {
            mEt.setText(mOriginText);
            mEt.setSelection(mOriginText.length());
        } else {
            mOriginText = "";
        }
        final LinearLayout llRoot = (LinearLayout) findViewById(R.id.ll_root);
        mEt.addTextChangedListener(new TextListener() {
            @Override
            public void afterTextChanged(Editable arg0) {
                llRoot.postInvalidateDelayed(10);
            }
        });
        setRightText(getString(R.string.me_done), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEt.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtil.showToast(R.string.me_content_cannot_null);
                    return;
                }
                if (text.equals(mOriginText)) {
                    finish();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(KEY.TEXT, text);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
