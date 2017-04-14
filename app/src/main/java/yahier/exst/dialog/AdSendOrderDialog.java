package yahier.exst.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
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
import com.stbl.stbl.item.ad.AdGoodsItem;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.RoundImageView;

/**
 * 找人代付弹框
 * @author ruilin
 *
 */
public class AdSendOrderDialog implements OnClickListener {
	AlertDialog dialog;
	Context mContext;
	View mView;
	private RoundImageView rivToUserIcon; //好友头像
	private TextView tvToUsername; //好友昵称
	private TextView tvAdUsername; //广告主昵称
	private TextView tvServiceType; //服务类型
	private TextView tvServicePrice; //服务价格
	private EditText etInputContent;
	private DialogInterface.OnCancelListener cancelListener;
	private int MaxInput = 100; //最大输入数
	private boolean isCancelAble = true;
	private AdGoodsItem goodsItem;//广告位信息

	public AdSendOrderDialog(Context ctx, String usericonurl, String toUsername, String adUsername, String btnOk, String btnCancel, AdGoodsItem goodsItem) {
		this.goodsItem = goodsItem;
		init(ctx, usericonurl,toUsername,adUsername, btnOk, btnCancel);
	}

	private void init(Context ctx, String usericonurl,String toUsername,String adUsername, String btnOk, String btnCancel) {
		mContext = ctx;
		mView = LayoutInflater.from(mContext).inflate(R.layout.ad_send_order_dialog, null);

		rivToUserIcon = (RoundImageView) mView.findViewById(R.id.riv_to_user_icon);
		tvToUsername = (TextView) mView.findViewById(R.id.tv_to_username);
		tvAdUsername = (TextView) mView.findViewById(R.id.tv_ad_username);
		tvServiceType = (TextView) mView.findViewById(R.id.tv_service_type);
		tvServicePrice = (TextView) mView.findViewById(R.id.tv_service_price);

		PicassoUtil.load(ctx,usericonurl,rivToUserIcon,R.drawable.icon_shifu_default);
		tvToUsername.setText(toUsername);
		tvAdUsername.setText(adUsername);

		Button btn = (Button) mView.findViewById(R.id.button1);
		btn.setOnClickListener(this);
		btn.setText(btnCancel);
		btn = (Button) mView.findViewById(R.id.button2);
		btn.setOnClickListener(this);
		btn.setText(btnOk);
		final TextView tvTextLength = (TextView) mView.findViewById(R.id.tv_text_length);
		tvTextLength.setText("0/"+MaxInput);

		etInputContent = (EditText) mView.findViewById(R.id.et_input_content);
		etInputContent.addTextChangedListener(new TextWatcher() {
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
				editStart = etInputContent.getSelectionStart();
				editEnd = etInputContent.getSelectionEnd();
				if (temp.length() > MaxInput) {
					ToastUtil.showToast(mContext,mContext.getString(R.string.mall_can_not_exceed)+MaxInput+mContext.getString(R.string.mall_a_word));
					s.delete(editStart-1, editEnd);
					int tempSelection = editStart;
					etInputContent.setText(s);
					etInputContent.setSelection(tempSelection);
				}
				tvTextLength.setText(s.toString().length()+"/"+MaxInput);
			}
		});
		if (goodsItem != null) {
			etInputContent.setText(String.format(ctx.getString(R.string.ad_send_order_tips), toUsername,goodsItem.getPrice()));
			tvServiceType.setText(goodsItem.getGoodsname());
			tvServicePrice.setText("￥"+goodsItem.getPrice());
		}

	}

	public void setMaxInput(int max){
		MaxInput = max;
	}
	public void setCancelable(boolean cancelAble) {
		isCancelAble = cancelAble;
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
			WindowManager windowManager = win.getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			dialog.getWindow().setAttributes(lp);
			win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			win.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mView.measure(0, 0);
			dialog.setCancelable(isCancelAble);
			dialog.setCanceledOnTouchOutside(isCancelAble);
			if (cancelListener != null) dialog.setOnCancelListener(cancelListener);
		} else if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	private void hideSoftInput(){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etInputContent.getWindowToken(), 0);
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
				hideSoftInput();
				String text = etInputContent.getText().toString();
				if (null != listener && text.length() <= MaxInput) {
					listener.onConfirm(text);
				}else {
					ToastUtil.showToast(mContext, mContext.getString(R.string.mall_input_word_exceed) + MaxInput);
					return;
				}
				break;
		}
	}
	public boolean isShow(){
		return dialog.isShowing();
	}
	public void dismiss(){
		dialog.dismiss();
	}

	public void setOnTipsListener(OnTipsListener listener) {
		this.listener = listener;
	}

	OnTipsListener listener;

	public interface OnTipsListener {
		void onConfirm(String input);//回调输入的内容
		void onCancel();
	}
}
