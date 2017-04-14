package yahier.exst.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.api.utils.Logger;

public class STProgressDialog extends Dialog {
	private Logger logger = new Logger(this.getClass().getSimpleName());
	private View layout;
	private TextView msgTextView;
	private ImageView tipsprogerss_show;
	private boolean isDismiss = true;
	
	public STProgressDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.progress_dialog,(ViewGroup) findViewById(R.id.dialog_layout_root));
		
		msgTextView = (TextView) layout.findViewById(R.id.msgTextView);
		tipsprogerss_show = (ImageView) layout.findViewById(R.id.tipsprogerss_show);
		
		msgTextView.setText(R.string.msg_handling);
		isDismiss = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout);

		Window window = getWindow();
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
		
	}

	public void setMsgText(String text) {
		msgTextView.setText(text);
	}
	
	public void setMsgText(int resources){
		msgTextView.setText(getContext().getResources().getString(resources));
	}
	
	@Override
	public void show(){
		tipsprogerss_show.startAnimation(loadAnim());
		super.show();
	}
	
	public void dismiss(){
		try {
			tipsprogerss_show.clearAnimation();
			isDismiss = true;
			super.dismiss();
		} catch (Exception e) {
			logger.e("error: WJProgressDialog dismiss " + e.toString());
		}
	}
	
	public boolean isDismiss(){
		return isDismiss;
	}
	
	private Animation loadAnim(){
		return AnimationUtils.loadAnimation(getContext(), R.anim.common_loading);
	}
	

}
