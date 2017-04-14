package yahier.exst.act.home.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.AlbumActivity;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.item.ImgUrl;
import com.stbl.stbl.task.CommonTask;
import com.stbl.stbl.util.Bimp;
import com.stbl.stbl.util.BitmapUtil;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.FileUtils;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.KEY;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.SharedPrefUtils;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.util.WheelMenu;
import com.stbl.stbl.util.WheelMenu.OnWheelMenuListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.rong.eventbus.EventBus;


/**
 * 退货申请
 * 
 * @author ruilin
 * 
 */
public class MallReturnApplyAct extends ThemeActivity implements OnClickListener, FinalHttpCallback {
	private final int MAX_PHOTO_NUM = 4;
	private final int REQUEST_TAKE_PICTURE = 1;
	private final int REQUEST_TAKE_PICTURE_SYS = 2;

	private final int CALL_PHOTO_TYPE_GOODS = 0;
	private final int CALL_PHOTO_TYPE_DESCRIBE = 1;
	private GridView gridview;
	private GridAdapter adapter;
	private PopupWindow pop;
	private WheelMenu subMenu;
	private String fileName;
	private TextView tv_reason;
	private TextView tv_edit_count;
	private Activity mContext;
	Button btnOk;
	EditText inputReason;
	long orderId;
	int reasontype;
	private final int maxLength = 140;
	private Map<String,Integer> returnGoodsType = new HashMap<>(); //退货类型选择
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.mall_return_apply);
		setLabel(getString(R.string.mall_return_apply));
		findViewById(R.id.lin_comment).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		inputReason = (EditText) findViewById(R.id.inputReason);
		tv_reason = (TextView) findViewById(R.id.tv_reason);
		tv_edit_count = (TextView) findViewById(R.id.tv_edit_count);
		initGridView();
		orderId = getIntent().getLongExtra("orderid", 0);
		if (orderId == 0) {
			showToast(getString(R.string.mall_no_orderid));
		}
		inputReason.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int editStart;
			private int editEnd;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = inputReason.getSelectionStart();
				editEnd = inputReason.getSelectionEnd();
				if (temp.length() > maxLength){
					ToastUtil.showToast(MallReturnApplyAct.this, getString(R.string.mall_can_not_exceed)+maxLength+getString(R.string.mall_a_word));
					s.delete(editStart -1,editEnd);
					int tempSelection = editStart;
					inputReason.setText(s);
					inputReason.setSelection(tempSelection);
				}
				tv_edit_count.setText(s.toString().length()+"/"+maxLength);
			}
		});
		initData();
	}

	private void initData(){
		String returnGoodsJson = (String) SharedPrefUtils.getFromPublicFile(KEY.refundgoodstype,"");
		if (returnGoodsJson.equals("")){
			CommonTask.getCommonDicBackground();
		}else{
			try {
				JSONArray arr = new JSONArray(returnGoodsJson);
				for (int i=0; i<arr.length(); i++){
					JSONObject obj = arr.optJSONObject(i);
					if (obj == null) continue;
					returnGoodsType.put(obj.optString("name"),obj.optInt("value"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	private void initGridView() {
		gridview = (GridView) findViewById(R.id.gv_img);
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					showGetPicWindow(CALL_PHOTO_TYPE_GOODS);
				} else {
					Intent intent = new Intent(mContext, GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lin_comment:
			if (null == subMenu) {
				final ArrayList<String> items = new ArrayList<>();
				for (String s :returnGoodsType.keySet()){
					items.add(s);
				}
				Collections.reverse(items);
				subMenu = new WheelMenu(this, items);
				subMenu.setOnWheelMenuListener(new OnWheelMenuListener() {
					@Override
					public void onTagOk(String tag, int index) {
						tv_reason.setText(tag);
						reasontype = returnGoodsType.get(tag);
					}
				});
			}
			subMenu.show();
			break;
		case R.id.btn_ok:
			if (reasontype == 0) {
				showToast(getString(R.string.mall_please_select_return_reason));
				return;
			}
			if (inputReason.getText().toString().equals("")) {
				showToast(getString(R.string.mall_need_input_return_reason));
				return;
			}
			if (Bimp.tempSelectBitmap.size() > 0) {
				imgUrls = new String[Bimp.tempSelectBitmap.size()];
				for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
					uploadImg(i);
				}
			}else{
				doReplyReturnGoods();
			}
			break;
		}
	}

	// 退货之上传图片
	public void uploadImg(int index) {
		Params params = new Params();
		params.put("attachid", orderId);
		params.put("index", index);
		params.put("userid", app.getUserId());
		params.put("pic", Bimp.tempSelectBitmap.get(index).getBitmap());
		new HttpEntity(this).commonPostImg(Method.orderApplyReturnUploadImg, params, this);
	}

	String[] imgUrls;

	// 申请退货
	@SuppressLint("NewApi")
	public void doReplyReturnGoods() {
		JSONObject json = new JSONObject();
		try {
			json.put("orderid", orderId);
			json.put("reasontype", reasontype);// 退货原因
			json.put("reason", inputReason.getText().toString());// 原因
			if (imgUrls != null && imgUrls.length > 0) {
				JSONArray jsonarr = new JSONArray(imgUrls);
				json.put("imgurls", jsonarr);// 原因
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.logE("LogUtil","json -- ;"+json.toString());
		new HttpEntity(this).commonPostJson(Method.orderApplyReturn, json.toString(), this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.update();
		ViewUtils.setAdapterViewHeight(gridview, 3, 10);
	}

	void clearUiData() {
		Bimp.tempSelectBitmap.clear();
		Bimp.bitmapSize = 0;
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// showToast("onDestroy");
		clearUiData();
		Bimp.tempSelectBitmap.clear();
	}

	private void hideInputSoft() {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示拍照 取图 window
	 */
	private void showGetPicWindow(final int type) {
		hideInputSoft();
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			return;
		}
		View view = getLayoutInflater().inflate(R.layout.window_get_pic, null);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				pop.dismiss();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				takePhotoFormCamera(type);
				pop.dismiss();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (type == CALL_PHOTO_TYPE_GOODS) {
					Intent intent = new Intent(mContext, AlbumActivity.class);
					intent.putExtra("MAX_NUM", MAX_PHOTO_NUM);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, REQUEST_TAKE_PICTURE_SYS);
				}
				pop.dismiss();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		view.measure(0, 0);
		// int titleH = findViewById(R.id.textView1).getHeight();
		pop = new PopupWindow(view, Device.getWidth(this), Device.getHeight(this));
		// pop.setAnimationStyle(R.style.bottom_popupAnimation);
		pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
	}

	public void takePhotoFormCamera(int type) {
		fileName = String.valueOf(System.currentTimeMillis());
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtils.getFile(fileName)));
		openCameraIntent.putExtra("type", type);
		startActivityForResult(openCameraIntent, REQUEST_TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < MAX_PHOTO_NUM && resultCode == RESULT_OK) {
				// Bitmap bm = (Bitmap) data.getExtras().get("data");// 得到压缩图
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);
				// opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts,
				// Device.getWidth(this), 600);
				opts.inSampleSize = BitmapUtil.calculateInSampleSize(opts, 500, 500);
				opts.inJustDecodeBounds = false;
				Bitmap bm = BitmapFactory.decodeFile(FileUtils.getFile(fileName).getAbsolutePath(), opts);

				FileUtils.saveBitmap(bm, fileName);
				ImageItem takePhoto = new ImageItem();
				//takePhoto.setBitmap(bm);
				takePhoto.setImagePath(FileUtils.getFile(fileName).getAbsolutePath());
				Bimp.tempSelectBitmap.add(takePhoto);
			}
			break;
		default:
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.tempSelectBitmap.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.dongtai_pulish_img_icon));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				//holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
				Bitmap bitmap = BitmapUtil.rotateUpright(Bimp.tempSelectBitmap.get(position).getBitmap(), Bimp.tempSelectBitmap.get(position).getImagePath(), false);
				holder.image.setImageBitmap(bitmap);
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		// 屏蔽以下方法后，没有显示出选中的图
		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						LogUtil.logE(null, "loading run");
						if (Bimp.bitmapSize == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.bitmapSize += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	@Override
	public void parse(String methodName, String result) {
		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
		if (item.getIssuccess() != BaseItem.successTag) {
			ToastUtil.showToast(this, item.getErr().getMsg());
			return;
		}
		String obj = JSONHelper.getStringFromObject(item.getResult());
		switch (methodName) {
		case Method.orderApplyReturn:
			showToast(getString(R.string.mall_apply_success_please));
//			EventBus.getDefault().post(new EventType(EventType.TYPE_REFRESH_ORDER_5));
			EventType event = new EventType(EventType.TYPE_REFRESH_ORDER_LIST);
			event.setParam(0); //0：在全部中才有退货
			EventBus.getDefault().post(event);
			finish();
			break;
		case Method.orderApplyReturnUploadImg:
			ImgUrl imgUrl = JSONHelper.getObject(obj, ImgUrl.class);
			imgUrls[imgUrl.getIndex()] = imgUrl.getSmall();
			// 判断图片是否上传完成
			boolean imgUploadFinished = true;
			for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				if (imgUrls[i] == null) {
					imgUploadFinished = false;
				}
			}

			if (imgUploadFinished) {
				doReplyReturnGoods();
			}

			break;
		}
	}
}
