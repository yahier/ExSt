package yahier.exst.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;
import com.stbl.stbl.R;
import com.stbl.stbl.adapter.CommonImagePagerAdapter;
import com.stbl.stbl.adapter.ImageUrlViewAdapter;
import com.stbl.stbl.barcoe.decode.BitmapDecoder;
import com.stbl.stbl.item.StatusesPic;
import com.stbl.stbl.util.CommonShare;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ThreadPool;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.DialogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 展示动态配图图 和 通用的List<String>
 *
 * @author lenovo
 *
 */

public class ImagePagerAct extends BaseActivity implements ImageUrlViewAdapter.OnPagerItemClickListener, CommonImagePagerAdapter.OnPagerItemClickListener {
	ViewPager pager;
	TextView itemIndex;
	TextView mSaveTv;
	int index;
	StatusesPic pics;

	int length = 0;
	List<String> list;

	/**用于记录解析出来的二维码结果*/
	private Map<Integer,String> mResultMap = new HashMap<>();

	private Dialog mActionSheet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_pager_act);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		pager = (ViewPager) findViewById(R.id.item_pager);
		itemIndex = (TextView) findViewById(R.id.item_index);
		mSaveTv = (TextView) findViewById(R.id.tv_save);
		// 取intent的值
		index = getIntent().getIntExtra("index", 0);
		pics = (StatusesPic) getIntent().getSerializableExtra("pics");
		list = getIntent().getStringArrayListExtra("list");
		if (pics != null) {
			length = pics.getPics().size();
			ImageUrlViewAdapter adapter = new ImageUrlViewAdapter(pics, this);
			adapter.setPagerItemOnClickListener(this);
			pager.setAdapter(adapter);
		} else if (list != null) {
			CommonImagePagerAdapter adapter = new CommonImagePagerAdapter(list, this);
			adapter.setPagerItemOnClickListener(this);
			pager.setAdapter(adapter);
			length = list.size();
		}
		itemIndex.setText((index + 1) + "/" + length);

		pager.setCurrentItem(index);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				index = position;
				itemIndex.setText((position + 1) + "/" + length);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		findViewById(R.id.findpeople_tview_left).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//
	}

	@Override
	protected void setStatusBar() {

	}

	private void showActionSheet(){
		ArrayList<String> actionList = new ArrayList<>();
		actionList.add(getString(R.string.common_save_to_phone));
		if (mResultMap != null && mResultMap.get(index) != null){
			actionList.add(getString(R.string.common_decode_qr));
		}
		mActionSheet = DialogFactory.createActionSheet(this, actionList, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mActionSheet.dismiss();
				if (position == 0) { //保存图片
					String url = "";
					if (pics != null) {
						url = pics.getOriginalpic() + pics.getPics().get(index);
					} else if (list != null) {
						url = list.get(index);
					}
					FileUtils.saveImage(url, System.currentTimeMillis() + "");
				}else if(position == 1){ //识别二维码
					afterHandle(mResultMap.get(index));
				}
			}
		});
		mActionSheet.show();
	}

	@Override
	public void onPagerItemClick() {
		finish();
	}

	@Override
	public void onLongClick() {
//		mActionSheet.show();
		showActionSheet();
	}

	/**解析二维码*/
	@Override
	public void decodeQr(final Bitmap bm, final int position){
		ThreadPool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				LogUtil.logE("LogUtil","解析二维码--"+position);
				BitmapDecoder decoder = new BitmapDecoder(ImagePagerAct.this);
				Result result = decoder.getRawResult(bm);

				//有解析结果，记录起来
				if (result != null) {
					String qrResutl = ResultParser.parseResult(result).toString();
					if (mResultMap != null)
						mResultMap.put(position,qrResutl);
				}

			}
		});
	}
	/**根据二维码做操作*/
	private void afterHandle(String result) {
		LogUtil.logE("CaptureActivity result=" + result);
		//包含ex=1的参数 表示在app打开
		if (result.contains("&ex=1&")) {
			String[] arr = result.split("&ex=1&");
			if (arr == null || arr.length <= 0) return;
			String url = "stbl://stbl/?" + arr[arr.length - 1];
			//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			//startActivity(intent);
			Intent intent = new Intent(this, CommonWebInteact.class);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, CommonWeb.class);
			intent.putExtra("url", result);
			intent.putExtra("title", getString(R.string.me_scan_a_scan));
			this.startActivity(intent);
		}
		finish();
	}
}
