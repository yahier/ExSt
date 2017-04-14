package yahier.exst.ui.DirectScreen.dialog;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.Gift;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.Util;

/**
 * @author meteorshower
 * 送礼物
 */
public class SendGiftDialog implements OnClickListener, FinalHttpCallback, OnItemClickListener{
	
	private PopupWindow popupWindow;
	private View popupView,parentLayout;
	private Activity context;
	private GridView gv;
	private GiftListAdapter adapter;
	private OnSendGiftCallBack callBack;
	public SendGiftDialog(Activity context){
		this.context = context;
		
		popupView = LayoutInflater.from(context).inflate(R.layout.dialog_send_gift, null);
		popupView.setFocusableInTouchMode(true);
		popupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(0);
		
		parentLayout = popupView.findViewById(R.id.popup_parent_layout);
		gv = (GridView) popupView.findViewById(R.id.gv);
		
		TextView emptyView = new TextView(context);
		emptyView.setLayoutParams(new android.widget.AbsListView.LayoutParams(
				android.widget.AbsListView.LayoutParams.MATCH_PARENT, Util.dip2px(context, 120)));
		emptyView.setTextColor(Color.BLACK);
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		emptyView.setText(R.string.msg_loading_new_gift);
		gv.setEmptyView(emptyView);
		
		popupView.findViewById(R.id.btn_cancel).setOnClickListener(this);
		popupView.findViewById(R.id.popup_cancel).setOnClickListener(this);
		popupView.findViewById(R.id.popup_cancel2).setOnClickListener(this);
		
		getGiftList();
	}
	
	// 获取礼物列表
	private void getGiftList() {
		new HttpEntity(context).commonPostData(Method.userGetGiftList, null, this);
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
	public void setOnSendGiftCallBack(OnSendGiftCallBack callBack){
		this.callBack = callBack;
	}

	@Override
	public void onClick(View v) {

		if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		// 错误提示
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(context, item.getErr().getMsg());
			return;
		}
		
		String obj = JSONHelper.getStringFromObject(item.getResult());
		if(methodName.equals(Method.userGetGiftList)){// 礼物列表
			List<Gift> listGift = JSONHelper.getList(obj, Gift.class);
			adapter = new GiftListAdapter(listGift);
			gv.setAdapter(adapter);
			gv.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
//		ToastUtil.createToast(context, "Gift Click : "+arg2).show();
		onClick(arg1);
		Gift gift = (Gift) arg0.getAdapter().getItem(arg2);
		if (callBack != null && gift != null){
			callBack.callback(gift);
		}
	}

	public interface OnSendGiftCallBack{
		void callback(Gift giftId);
	}
	private class GiftListAdapter extends BaseAdapter{
		
		private List<Gift> arrayList;
		private LayoutInflater inflater;
		
		public GiftListAdapter(List<Gift> arrayList){
			this.arrayList = arrayList;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return arrayList.size();
		}

		@Override
		public Gift getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder vh = null;
			if(convertView == null){
				vh = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_adapter_gift_list, null);

				vh.img = (ImageView) convertView.findViewById(R.id.img);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				vh.value = (TextView) convertView.findViewById(R.id.value);
				
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			
			convertView.setBackgroundResource(position%2 == 0 ? R.color.white : R.color.theme_bg);
			
			Gift gift = getItem(position);
			String typeValue = "";
			switch (gift.getCurrencytype()) {
			case Gift.type_renminbi:
				typeValue = "人民币";
				break;
			case Gift.type_jindou:
				typeValue = "金豆";
				break;
			case Gift.type_lvdou:
				typeValue = "绿豆";
				break;
			}
			
			String newMessageInfo = "对方获<font color='#00FFFF'><b>"+gift.getValue()+"</b></font>"+typeValue;
			PicassoUtil.load(context, gift.getGiftimg(), vh.img);
			vh.name.setText(gift.getGiftname());
			vh.value.setText(Html.fromHtml(newMessageInfo));
			
			return convertView;
		}
		
		private class ViewHolder{
			private ImageView img;
			private TextView name, value;
		}
		
	}
	
}
