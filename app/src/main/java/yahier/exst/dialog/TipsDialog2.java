package yahier.exst.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.DimenUtils;

/**
 * 提示框
 * @author ruilin
 *
 */
public class TipsDialog2 implements OnClickListener {
	AlertDialog dialog;
	Context mContext;
	View mView;
	private DialogInterface.OnCancelListener cancelListener;

	public TipsDialog2(Context ctx, CharSequence msg, String btnCancel, String btnOk) {
		init(ctx, msg, btnOk, btnCancel);
	}

	public TipsDialog2(Context ctx, CharSequence msg, String btnOk) {
		init(ctx, msg, btnOk, "");
		mView.findViewById(R.id.button1).setVisibility(View.GONE);
		mView.findViewById(R.id.v_middle).setVisibility(View.GONE);
		Button btn2 = (Button) mView.findViewById(R.id.button2);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn2.getLayoutParams();
		params.leftMargin = DimenUtils.dp2px(40);
		params.rightMargin = DimenUtils.dp2px(40);
		btn2.setLayoutParams(params);
	}

	private void init(Context ctx, CharSequence msg, String btnOk, String btnCancel) {
		mContext = ctx;
		mView = LayoutInflater.from(mContext).inflate(R.layout.tips_dialog_layout, null);
		Button btn = (Button) mView.findViewById(R.id.button1);
		btn.setOnClickListener(this);
		btn.setText(btnCancel);
		btn = (Button) mView.findViewById(R.id.button2);
		btn.setOnClickListener(this);
		btn.setText(btnOk);
		TextView contetnView = (TextView) mView.findViewById(R.id.tv_content);
		contetnView.setText(msg);

	}

	public void setCancelable(boolean cancelAble) {
		if (dialog == null) return;
		dialog.setCancelable(cancelAble);
		dialog.setCanceledOnTouchOutside(cancelAble);
	}

	public void setOnDissmissListener(DialogInterface.OnDismissListener dissmissListener){
		if (dialog != null)
		dialog.setOnDismissListener(dissmissListener);
	}

	public boolean isShow(){
		if (dialog != null){
			return dialog.isShowing();
		}
		return false;
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
			mView.measure(0, 0);
			dialog.setCanceledOnTouchOutside(true);
			if (cancelListener != null) dialog.setOnCancelListener(cancelListener);
		} else if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	public void setOnCancelListener(DialogInterface.OnCancelListener cancelListener){
		this.cancelListener = cancelListener;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button1:
				if (null != listener)
					listener.onCancel();
				break;
			case R.id.button2:
				if (null != listener)
					listener.onConfirm();
				break;
		}
		dialog.dismiss();
	}

	public void setOnTipsListener(OnTipsListener listener) {
		this.listener = listener;
	}

	OnTipsListener listener;

	public interface OnTipsListener {
		public void onConfirm();
		public void onCancel();
	}

	public static TipsDialog2 popup(Context ctx, CharSequence msg, String btnCancel, String btnOk, OnTipsListener listener) {
		TipsDialog2 dialog = new TipsDialog2(ctx, msg, btnCancel, btnOk);
		dialog.setOnTipsListener(listener);
		dialog.show();
		return dialog;
	}

	public static TipsDialog2 popup(Context ctx, int msg, int btnCancel, int btnOk, OnTipsListener listener) {
		return popup(ctx, ctx.getString(msg), ctx.getString(btnCancel), ctx.getString(btnOk),listener);
	}

	public static TipsDialog2 popup(Context ctx, CharSequence msg, String btnOk, OnTipsListener listener) {
		TipsDialog2 dialog = new TipsDialog2(ctx, msg,  btnOk);
		dialog.setOnTipsListener(listener);
		dialog.show();
		return dialog;
	}


	public static TipsDialog2 popup(Context ctx, int msg, int btnOk) {
		return popup(ctx, ctx.getString(msg), ctx.getString(btnOk));
	}


	public static TipsDialog2 popup(Context ctx, String msg, String btnOk) {
		TipsDialog2 dialog = new TipsDialog2(ctx, msg, btnOk);
		dialog.show();
		return dialog;
	}

	public static TipsDialog2 popup(Context ctx, CharSequence msg, String btnOk) {
		TipsDialog2 dialog = new TipsDialog2(ctx, msg, btnOk);
		dialog.show();
		return dialog;
	}

	/**
	 * 广告位客服热线
	 * @param ctx
	 */
	public static void showHotLine(final Context ctx){
		if (ctx == null) return;

		String text1 = ctx.getString(R.string.hotline_tips);
		String text2 = ctx.getString(R.string.hotline_tips2);
		String text3 = ctx.getString(R.string.hotline)+"\n";
		String text4 = ctx.getString(R.string.hotline_tips3);

		SpannableString sString = new SpannableString(text1+text2+text3+text4);

		sString.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.black)),0,text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sString.setSpan(new AbsoluteSizeSpan(DimenUtils.sp2px(14)),0,text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		sString.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.black)),
				text1.length(),text1.length()+text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sString.setSpan(new AbsoluteSizeSpan(DimenUtils.sp2px(16)),text1.length(),text1.length()+text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		sString.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.theme_red)),
				text1.length()+text2.length(),text1.length()+text2.length()+text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sString.setSpan(new AbsoluteSizeSpan(DimenUtils.sp2px(16)),text1.length()+text2.length(),
				text1.length()+text2.length()+text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		sString.setSpan(new ForegroundColorSpan(ctx.getResources().getColor(R.color.gray_a5)),
				text1.length()+text2.length()+text3.length(),sString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sString.setSpan(new AbsoluteSizeSpan(DimenUtils.sp2px(12)),text1.length()+text2.length()+text3.length(),
				sString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		popup(ctx, sString, ctx.getString(R.string.ad_close), ctx.getString(R.string.ad_tell_phone), new OnTipsListener() {
			@Override
			public void onConfirm() {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ctx.getString(R.string.orderHumanPhone)));
				ctx.startActivity(intent);
			}

			@Override
			public void onCancel() {

			}
		});
	}
}
