package yahier.exst.ui.DirectScreen.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.stbl.stbl.R;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.StringUtils;

public class UpdateChatTopicDialog implements OnClickListener{
	
	private PopupWindow popupWindow;
	private View popupView;
	private Activity context;
	private View parentLayout;
	private EditText edtInput;
	private OnUpdateTopicListener listener;
	
	public UpdateChatTopicDialog(Activity context){
		this.context = context;
		
		popupView = LayoutInflater.from(context).inflate(R.layout.dialog_update_chat_topic, null);
		popupView.setFocusableInTouchMode(true);
		popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		popupWindow.setBackgroundDrawable(null);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(0);
		
		parentLayout = popupView.findViewById(R.id.popup_parent_layout);
		edtInput = (EditText) popupView.findViewById(R.id.topic_edt);
		
		popupView.findViewById(R.id.cancel_btn).setOnClickListener(this);
		popupView.findViewById(R.id.enter_btn).setOnClickListener(this);
		popupView.findViewById(R.id.popup_cancel).setOnClickListener(this);
		popupView.findViewById(R.id.popup_cancel2).setOnClickListener(this);
		
		popupView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				if(keyCode == KeyEvent.KEYCODE_BACK){
					if(listener != null)
						listener.onCanelTopic();
					
					onClick(v);
					return true;
				}
				return false;
			}
		});
	}

	public void showPopupWindow(){
		showPopupWindow(R.id.appParentId);
	}
	
	public void showPopupWindow(int resource){
		try{
			popupWindow.showAtLocation(context.findViewById(resource), Gravity.CENTER
					| Gravity.CENTER_HORIZONTAL, 0, 0);
//			parentLayout.startAnimation(getScaleAnimation());
		}catch(Exception e){
			LogUtil.logE(e.getMessage());
		}
	}
	
	private Animation getScaleAnimation(){
		Animation scaleAnimation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, 
		Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f); 
		scaleAnimation.setDuration(200); 
		scaleAnimation.setFillEnabled(true);
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setFillBefore(true);
		return scaleAnimation;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.enter_btn:
			if(listener != null){
				String topicValue = edtInput.getText().toString().trim();
				if(!StringUtils.isEmpty(topicValue)){
					listener.onUpdateTopic(topicValue);
					break;
				}else{
					ToastUtil.showToast(context.getResources().getString(R.string.hint_input_topic));
					return;
				}
			}
			break;
		case R.id.cancel_btn:
		case R.id.popup_cancel:
		case R.id.popup_cancel2:
			if(listener != null)
				listener.onCanelTopic();
			break;
		}
		
		if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
	}
	
	public void setOnUpdateTopicListener(OnUpdateTopicListener listener){
		this.listener = listener;
	}
	
	public interface OnUpdateTopicListener{
		
		public void onUpdateTopic(String topic);
		
		public void onCanelTopic();
	}

}
