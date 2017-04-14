package yahier.exst.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.ImageUtils;
import com.stbl.stbl.util.PicassoUtil;
import com.stbl.stbl.widget.PhotoView;
import com.stbl.stbl.widget.ViewPagerFixed;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:47:53
 */
public class GalleryActivity extends ThemeActivity implements OnClickListener {
	private Intent intent;
	// 返回按钮
	private Button back_bt;
	// 删除按钮
	private Button del_bt;
	// 顶部显示预览图片位置的textview
	private TextView positionTextView;
	// 获取前一个activity传过来的position
	//private int position;
	// 当前的位置
	private int location = 0;

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	private ArrayList<String> urls;
	private Context mContext;

	RelativeLayout photo_relativeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_gallery);
		setLabel(getString(R.string.common_check_look));
		//setRightImage(R.drawable.plugin_camera_del_state, this);
		// PublicWay.activityList.add(this);
		mContext = this;
		back_bt = (Button) findViewById(R.id.gallery_back);

		del_bt = (Button) findViewById(R.id.gallery_del);
		back_bt.setOnClickListener(new BackListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		//Bundle bundle = intent.getExtras();
		//position = Integer.parseInt(intent.getStringExtra("position"));
		// isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		urls = intent.getStringArrayListExtra("urls");
		pager.setOnPageChangeListener(pageChangeListener);
		if (null == urls) {
			for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				if (listViews == null) {
					listViews = new ArrayList<>();
				}
				PhotoView img = new PhotoView(this);
				img.setBackgroundColor(0xff000000);
				img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				listViews.add(img);
				ImageUtils.loadFile(Bimp.tempSelectBitmap.get(i).imagePath, img);
			}
		} else {
			setRightGone();
			for (int i = 0; i < urls.size(); i++) {
				initListViews(urls.get(i));
			}
		}

		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		// pager.setPageMargin((int)
		// getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private void initListViews(String url) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
//		PicassoUtil.load(this, url, img);
		loadImg(this, url, img);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}

	private void loadImg(Context context, String url,ImageView img){
		if(url != null){
			if(url.startsWith("http://") || url.startsWith("https://")){
				PicassoUtil.load(context, url, img);
			}else{
				PicassoUtil.load(context, new File(url), img);
			}
		}
	}

	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		public void onClick(View v) {
			//intent.setClass(GalleryActivity.this, ImageFile.class);
			//startActivity(intent);
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				Bimp.bitmapSize = 0;
				// send_bt.setText(Res.getString("finish") + "(" +
				// Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				Intent intent = new Intent("data.broadcast.action");
				sendBroadcast(intent);
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				Bimp.bitmapSize--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				// send_bt.setText(Res.getString("finish") + "(" +
				// Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("result", true);
		setResult(Activity.RESULT_OK);
		super.finish();
	}

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	@Override
	public void onClick(View view) {

//		switch (view.getId()) {
//			case R.id.theme_top_banner_right:
//
//				if (listViews.size() == 1) {
//					Bimp.tempSelectBitmap.clear();
//					Bimp.bitmapSize = 0;
//					// send_bt.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
//					Intent intent = new Intent("data.broadcast.action");
//					sendBroadcast(intent);
//					finish();
//				} else {
//					Bimp.tempSelectBitmap.remove(location);
//					Bimp.bitmapSize--;
//					pager.removeAllViews();
//					listViews.remove(location);
//					adapter.setListViews(listViews);
//					// send_bt.setText(Res.getString("finish") + "(" +
//					// Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
//					adapter.notifyDataSetChanged();
//				}
//				break;
//		}
	}
}
