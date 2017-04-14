package yahier.exst.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.stbl.stbl.R;

public class LoadingView {


	public static ProgressDialog createDefLoading(Context ctx) {
		ProgressDialog dialog = new ProgressDialog(ctx);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setMessage(ctx.getString(R.string.common_dispose));
		return dialog;
	}
}
