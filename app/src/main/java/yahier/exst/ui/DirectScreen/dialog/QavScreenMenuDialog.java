package yahier.exst.ui.DirectScreen.dialog;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Util;

/**
 * @author meteorshower
 * 直播功能弹框
 */
public class QavScreenMenuDialog implements OnClickListener,OnItemClickListener{

	private PopupWindow popupWindow;
	private Activity context;
	private View popupView;
	private boolean isOwner = false;//房主
	private ListView lv;
	private MenuListAdapter adapter;
	public Integer[] ownerResId = { R.string.shot_off, R.string.follow, R.string.apply_friend, R.string.send_gift};
	public Integer[] memberResId = { R.string.report,R.string.follow, R.string.apply_friend, R.string.send_gift};
	private MenuDialogClickListener listener;
	private View parentLayout;
	
	public QavScreenMenuDialog(Activity context, boolean isOwner){
		this.context = context;
		this.isOwner = isOwner;
		Log.e("QavScreenMenuDialog", " Dialog is Owner : "+isOwner);
		
		popupView = LayoutInflater.from(context).inflate(R.layout.dialog_qav_screen_menu, null);
		popupView.setFocusableInTouchMode(true);
		popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(0);
		
		parentLayout = popupView.findViewById(R.id.popup_parent_layout);
		lv = (ListView) popupView.findViewById(R.id.popup_lv);
		popupView.findViewById(R.id.btn_cancel).setOnClickListener(this);
		popupView.findViewById(R.id.popup_cancel).setOnClickListener(this);
		
		adapter = new MenuListAdapter(isOwner ? ownerResId : memberResId);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}
	
	public void showPopupWindow(){
		showPopupWindow(R.id.appParentId);
	}
	
	public void showPopupWindow(int resource){
		try{
			popupWindow.showAtLocation(context.findViewById(resource), Gravity.CENTER
					| Gravity.CENTER_HORIZONTAL, 0, 0);
//			parentLayout.startAnimation(getTranslateAnimation(Util.getScreenPixHeight(context), 0, 500));
		}catch(Exception e){
			LogUtil.logE(e.getMessage());
		}
	}
	
	protected Animation getTranslateAnimation(int start,int end,int durationMillis){
		Animation translateAnimation = new TranslateAnimation(0, 0, start, end);
		translateAnimation.setDuration(durationMillis);
		translateAnimation.setFillEnabled(true);
		translateAnimation.setFillAfter(true);
		translateAnimation.setFillBefore(true);
		return translateAnimation;
	}

	@Override
	public void onClick(View v) {
		if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
	}
	
	private class MenuListAdapter extends BaseAdapter{
		
		private Integer[] arrays;
		
		public MenuListAdapter(Integer[] arrays){
			this.arrays = arrays;
		}

		@Override
		public int getCount() {
			return arrays.length;
		}

		@Override
		public Integer getItem(int position) {
			return arrays[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			TextView  tv = null;
			if(convertView == null){
				tv = new TextView(context);
				tv.setLayoutParams(new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.MATCH_PARENT, Util.dip2px(context, 45)));
				tv.setTextColor(context.getResources().getColor(R.color.black));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				tv.setGravity(Gravity.CENTER);
				
				convertView = tv;
			}else{
				tv = (TextView) convertView;
			}
			
			tv.setText(getItem(position));
			
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(listener != null)
			listener.onMenuDialogClick(adapter.getItem(arg2), isOwner);

		if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
	}
	
	public void setOnMenuDialogClickListener(MenuDialogClickListener listener){
		this.listener = listener;
	}
	
	public interface MenuDialogClickListener{
		
		public void onMenuDialogClick(int position, boolean isOwner);
	}
}
