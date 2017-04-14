package yahier.exst.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.stbl.stbl.R;

public class SearchLayout extends RelativeLayout {

	private EditText mSearchEt;
	private ImageView mClearIv;

	private ISearchLayout mInterface;

	public SearchLayout(Context context) {
		this(context, null);
	}

	public SearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_search, this);

		mSearchEt = (EditText) findViewById(R.id.et_search);
		mClearIv = (ImageView) findViewById(R.id.iv_clear);

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
				} else {
					mClearIv.setVisibility(View.VISIBLE);
				}
				if (mInterface != null) {
					mInterface.afterTextChanged(e);
				}
			}
		});

	}

	public void setHint(CharSequence hint) {
		mSearchEt.setHint(hint);
	}

	public void setInterface(boolean editable, ISearchLayout i) {
		mInterface = i;
		if (!editable) {
			mSearchEt.setInputType(InputType.TYPE_NULL);
			mSearchEt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mInterface != null) {
						mInterface.onClick(v);
					}
				}
			});
		}
	}

	public interface ISearchLayout {

		void onClick(View v);

		void afterTextChanged(Editable e);
	}

}
