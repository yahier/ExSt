package yahier.exst.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * 新的通用dialog。替代DialogSimple
 * 
 * @author lenovo
 * 
 */
public class DialogSimple2 extends Dialog implements View.OnClickListener {

	View.OnClickListener okListener;
	TextView textTitle;
	TextView textMessage;
	Button btnOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_simple2);
		textTitle = (TextView) findViewById(R.id.window_title);
		textMessage = (TextView) findViewById(R.id.window_content);
		btnOk = (Button) findViewById(R.id.window_ok);
		btnOk.setOnClickListener(this);
		findViewById(R.id.window_close).setOnClickListener(this);
	}

	public DialogSimple2(Context mContext, View.OnClickListener okListener) {
		super(mContext, R.style.dialog);
		this.okListener = okListener;

	}

	public void setMessage(String message) {
		textMessage.setText(message);
	}

	public void setTitle(String title) {
		textTitle.setText(title);
	}
	
	public void setBtnText(String label){
		btnOk.setText(label);
	}

	public void setTitleCompoundDrawablesWithIntrinsicBounds(int left) {
		textTitle.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.window_ok:
			dismiss();
			if (okListener != null) {
				btnOk.setOnClickListener(okListener);
				btnOk.performClick();
			}
			break;
		case R.id.window_close:
			dismiss();
			break;
		}

	}
}
