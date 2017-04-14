package yahier.exst.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.stbl.stbl.R;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.TipsDialog.OnTipsListener;
import com.stbl.stbl.util.ToastUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 网络图片浏览
 * @author ruilin
 *
 */
public class WebImageAct extends BaseActivity {
	WebImageAct mActivity;
	WebView web;
	String url = "http://baidu.com/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_image);
		mActivity = this;
		web = (WebView) findViewById(R.id.web);
		WebSettings setting = web.getSettings();
		// 自适应
		setting.setUseWideViewPort(true);
		setting.setLoadWithOverviewMode(true);
		// 设置支持缩放
		setting.setBuiltInZoomControls(true);
		setting.setSupportZoom(true);
		url = getIntent().getStringExtra("url");
		web.loadUrl(url);

		web.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				checkImageClicked(web);
				return false;
			}
		});
	}
	
	public void checkImageClicked(WebView web) {
        WebView.HitTestResult result = web.getHitTestResult();
        if (result != null) {
            int type = result.getType();
            if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                imgurl = result.getExtra();
                TipsDialog.popup(mActivity, getString(R.string.save_to_phone), getString(R.string.temp_account_cancel), getString(R.string.common_save), new OnTipsListener() {
					@Override
					public void onConfirm() {
						new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
					}
					@Override
					public void onCancel() {}
				});
            }
        }
	}

	private String imgurl = "";
	 
    /***
     * 异步保存图片
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                String fileName = new Date().getTime() + ext;
                file = new File(sdcard + "/Download/" + fileName);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                // 通知图库更新
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
                result = getString(R.string.common_photo_save_to) + file.getAbsolutePath();
            } catch (Exception e) {
                result = getString(R.string.common_save_error) + e.getLocalizedMessage();
            }
            return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
        	ToastUtil.showToast(mActivity, result);
        }
    }
}
