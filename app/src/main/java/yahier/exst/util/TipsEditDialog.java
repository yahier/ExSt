package yahier.exst.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stbl.stbl.R;

/**
 * 提示框
 * @author ruilin
 *
 */
public class TipsEditDialog implements OnClickListener {
	AlertDialog dialog;
	Context mContext;
	View mView;
	TextView titleView;
	private EditText inputEdit;
	private DialogInterface.OnCancelListener cancelListener;
	private int MaxInput = 140; //最大输入数

	public TipsEditDialog(Context ctx, String title,String textHint, String btnCancel, String btnOk) {
		init(ctx, title,textHint, btnOk, btnCancel);
		mView.findViewById(R.id.btn_one).setVisibility(View.GONE);
		mView.findViewById(R.id.btn_two).setVisibility(View.VISIBLE);
	}

	public TipsEditDialog(Context ctx, String title, String btnOk) {
		init(ctx, title,"", btnOk, "");
		mView.findViewById(R.id.btn_one).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.btn_two).setVisibility(View.GONE);
	}

	private void init(Context ctx, String title,String textHint, String btnOk, String btnCancel) {
		mContext = ctx;
		mView = LayoutInflater.from(mContext).inflate(R.layout.mall_pop_edit_tips, null);
		Button btn = (Button) mView.findViewById(R.id.button1);
		btn.setOnClickListener(this);
		btn.setText(btnCancel);
		btn = (Button) mView.findViewById(R.id.button2);
		btn.setOnClickListener(this);
		btn.setText(btnOk);
		btn = (Button) mView.findViewById(R.id.button3);
		btn.setOnClickListener(this);
		btn.setText(btnOk);
		titleView = (TextView) mView.findViewById(R.id.title);
		titleView.setText(title);
		final TextView tv_text_length = (TextView) mView.findViewById(R.id.tv_text_length);
		tv_text_length.setText("0/"+MaxInput);
		mView.findViewById(R.id.inputLin).setVisibility(View.VISIBLE);
		inputEdit = (EditText) mView.findViewById(R.id.inputContent);
		if (textHint != null && !textHint.equals(""))
			inputEdit.setHint(textHint);
		inputEdit.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart ;
			private int editEnd ;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = inputEdit.getSelectionStart();
				editEnd = inputEdit.getSelectionEnd();
				if (temp.length() > MaxInput) {
					ToastUtil.showToast(mContext,mContext.getString(R.string.mall_can_not_exceed)+MaxInput+mContext.getString(R.string.mall_a_word));
					s.delete(editStart-1, editEnd);
					int tempSelection = editStart;
					inputEdit.setText(s);
					inputEdit.setSelection(tempSelection);
				}
				tv_text_length.setText(s.toString().length()+"/"+MaxInput);
			}
		});
	}

	public void setMaxInput(int max){
		MaxInput = max;
	}
	public void setCancelable(boolean cancelAble) {
		if (dialog == null) return;
		dialog.setCancelable(cancelAble);
		dialog.setCanceledOnTouchOutside(cancelAble);
	}

	public void show() {
		if(mContext == null ) return;
		if((mContext instanceof Activity)){
			if(((Activity)mContext).isFinishing()) return;
		}
		if (null == dialog) {
			dialog = new AlertDialog.Builder(mContext).create();
			dialog.show();
			Window win = dialog.getWindow();
			win.setContentView(mView);
			win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			win.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mView.measure(0, 0);
			dialog.setCanceledOnTouchOutside(true);
			if (cancelListener != null) dialog.setOnCancelListener(cancelListener);
		} else if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	private void hideSoftInput(){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputEdit.getWindowToken(), 0);
	}
	public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener){
		this.cancelListener = cancelListener;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button1:
				hideSoftInput();
				if (null != listener)
					listener.onCancel();
				break;
			case R.id.button2:
			case R.id.button3:
				hideSoftInput();
				String text = inputEdit.getText().toString();
				if (null != listener && text.length() <= MaxInput) {
					listener.onConfirm(text);
				}else {
					ToastUtil.showToast(mContext, mContext.getString(R.string.mall_input_word_exceed) + MaxInput);
					return;
				}
				break;
		}
		dialog.dismiss();
	}

	public void setTitleCompoundDrawables(int left) {
		titleView.setCompoundDrawablesWithIntrinsicBounds(left, 0, 0, 0);
	}

	public void setTitleColor(int color) {
		titleView.setTextColor(color);
	}

	public void setOnTipsListener(OnTipsListener listener) {
		this.listener = listener;
	}

	OnTipsListener listener;

	public interface OnTipsListener {
		public void onConfirm(String input);//回调输入的内容
		public void onCancel();
	}
}
