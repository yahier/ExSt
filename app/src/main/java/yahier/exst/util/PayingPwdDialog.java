package yahier.exst.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.stbl.base.gridpasswordview.GridPasswordView;
import com.stbl.stbl.R;
/**
 * 支付密码回调类
 * @author lenovo
 *
 */
public class PayingPwdDialog {
	AlertDialog mDialog;

	public void show(final Context mContext) {
		if (null == mDialog) {
			mDialog = new AlertDialog.Builder(mContext).create();
			mDialog.show();
			View view = LayoutInflater.from(mContext).inflate(R.layout.pay_window, null);
			mDialog.getWindow().setContentView(view);
			mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			final GridPasswordView gridPwdview = (GridPasswordView) view.findViewById(R.id.gpv_normal);
			gridPwdview.postDelayed(new Runnable() { //不延时弹不出来~~
				@Override
				public void run() {
					gridPwdview.performClick();
				}
			},200);
			gridPwdview.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
				@Override
				public void onTextChanged(String psw) {

				}

				@Override
				public void onInputFinish(String psw) {
					// 验证密码
					listener.onInputFinished(psw);
					mDialog.dismiss();
				}
			});
//			final EditText inputPwd = (EditText) view.findViewById(R.id.inputPwd);
//			final LinearLayout linImg = (LinearLayout) view.findViewById(R.id.linImg);
//			final int maxLength = 6;
			//((InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE)).showSoftInput(inputPwd, 0);
//			InputMethodUtils.showInputMethodDelay(inputPwd);
//			inputPwd.addTextChangedListener(new TextListener() {
//
//				@Override
//				public void afterTextChanged(Editable edit) {
//					int length = edit.length();
//					for (int i = 0; i < maxLength; i++) {
//						if (i < length)
//							linImg.getChildAt(i).setVisibility(View.VISIBLE);
//						else
//							linImg.getChildAt(i).setVisibility(View.INVISIBLE);
//					}
//
//					if (length == maxLength) {
//						// 验证密码
//						mDialog.dismiss();
//						listener.onInputFinished(edit.toString());
//						mDialog.dismiss();
//					}
//
//				}
//			});
//
//			linImg.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					((InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE)).showSoftInput(inputPwd, 0);
//				}
//			});

		} else if (!mDialog.isShowing()) {
			mDialog.show();
		}

	}

	OnInputListener listener;

	public void setOnInputListener(OnInputListener listener) {
		this.listener = listener;
	}

	public interface OnInputListener {
		public void onInputFinished(String pwd);

	}

	public void setOnCancelListener(DialogInterface.OnCancelListener listener){
		if (mDialog != null){
			mDialog.setOnCancelListener(listener);
		}
	}

}
