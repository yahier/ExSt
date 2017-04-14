package yahier.exst.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

public class OvalLayout extends LinearLayout {

	private RoundImageView mIv;
	private TextView mTv;

	public OvalLayout(Context context) {
		this(context, null);
	}

	public OvalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_oval, this);

		mIv = (RoundImageView) findViewById(R.id.iv);
		mTv = (TextView) findViewById(R.id.tv);
	}

	public void setImageResource(int resId) {
		mIv.setImageResource(resId);
	}

	public void setText(CharSequence text) {
		mTv.setText(text);
	}

	public void setTextColor(int color) {
		mTv.setTextColor(color);
	}

}
