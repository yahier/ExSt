package yahier.exst.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.stbl.stbl.R;
import com.stbl.stbl.model.ExternalPayResult;
import com.stbl.stbl.util.ACTION;
import com.stbl.stbl.util.EXTRA;
import com.stbl.stbl.util.LocalBroadcastHelper;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import io.rong.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayEntryActivity";

	private IWXAPI api;
	public final static int resultCode = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Log.e(TAG, "onReq = " + req.checkArgs());

	}

	//
	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "onPayFinish, errCode = " + resp.errCode+" "+resp.transaction);
		Log.e(TAG, "onPayFinish, 参数合法性: = " + resp.checkArgs());
		Log.e(TAG, "onPayFinish, type: = " + resp.getType());
		Log.e(TAG, "onPayFinish, 参数合法性: = " + resp.checkArgs());
		ExternalPayResult result = new ExternalPayResult(ExternalPayResult.TYPE_WEIXIN, resp.errCode, resp.transaction);
		Intent intent = new Intent(ACTION.WX_PAY_CALLBACK);
		intent.putExtra(EXTRA.PAY_RESULT, result);
		LocalBroadcastHelper.getInstance().send(intent);

		switch(resp.errCode){
				case BaseResp.ErrCode.ERR_OK:
					result.setErrCode(ExternalPayResult.errCodeSucceed);
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result.setErrCode(ExternalPayResult.errCodeCancel);
					break;
				case BaseResp.ErrCode.ERR_SENT_FAILED:
					result.setErrCode(ExternalPayResult.errCodeFailed);
					break;
				default:
					result.setErrCode(ExternalPayResult.errCodeFailed);
					break;
	}

		finish();
		EventBus.getDefault().post(result);
		//finish();
		

	}
}