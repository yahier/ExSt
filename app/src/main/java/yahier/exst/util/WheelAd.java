package yahier.exst.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.example.dateselect.util.OnWheelScrollListener;
import com.example.dateselect.util.WheelAdapterAd;
import com.example.dateselect.util.WheelAdapterAdParent;
import com.example.dateselect.util.WheelAdapterCity;
import com.example.dateselect.util.WheelAdapterProvince;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.CityItem;
import com.stbl.stbl.item.ProvinceItem;
import com.stbl.stbl.item.ad.AdType;
import com.stbl.stbl.item.ad.AdTypeParent;

import java.util.List;

public class WheelAd implements OnClickListener {
	WheelView provinceWheel, cityWheel;

	MyApplication app;
	Context mContext;
	Dialog pop;
	WheelAdapterAdParent provinceAdapter;
	WheelAdapterAd cityAdapter;
	int curritem1; //大类当前item
	int curritem2;//小类当前item

	public void show(Context mContext, List<AdTypeParent> listProvince) {
		this.mContext = mContext;
		app = (MyApplication) mContext.getApplicationContext();

		pop = new Dialog(mContext, R.style.Common_Dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_city, null);
		view.findViewById(R.id.wheel_cancel).setOnClickListener(this);
		view.findViewById(R.id.wheel_ok).setOnClickListener(this);
		provinceWheel = (WheelView) view.findViewById(R.id.car_region_province);
		cityWheel = (WheelView) view.findViewById(R.id.car_region_city);
		provinceAdapter = new WheelAdapterAdParent(listProvince);
		provinceWheel.setAdapter(provinceAdapter);
		if (curritem1 != 0){
			provinceWheel.setCurrentItem(curritem1);
		}
		provinceWheel.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				AdTypeParent provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());

				cityAdapter = new WheelAdapterAd(provinceItem.getChildnode());
				cityWheel.setAdapter(cityAdapter);
				cityWheel.setCurrentItem(0);
				// 将区域也跟着一起变动 更好的体验
				String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
				MyHandler.cityName = cityName;
				if (cityAdapter.getItemsCount() > 0) {
					AdType city = cityAdapter.getSingleData(0);
					//whellListener.onCityChanged(city.getCityname());
				}

			}
		});


		cityWheel.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				AdType cityItem = cityAdapter.getSingleData(cityWheel.getCurrentItem());
				//whellListener.onCityChanged(cityItem.getCityname());

			}
		});

		// 进入时默认的显示第一个
		AdTypeParent provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
		cityAdapter = new WheelAdapterAd(provinceItem.getChildnode());
		cityWheel.setAdapter(cityAdapter);
		cityWheel.setCurrentItem(curritem2);
		// 将区域也跟着一起变动 更好的体验
		String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
		MyHandler.cityName = cityName;
		if (cityAdapter.getItemsCount() > 0){
			AdType city = cityAdapter.getSingleData(0);
			//whellListener.onCityChanged(city.getCityname());
		}
		pop.setContentView(view);
		Window window = pop.getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(params);
		pop.show();
	}

	public void setCurrItem(int curritem1,int curritem2){
		this.curritem1 = curritem1;
		this.curritem2 = curritem2;
		if (provinceWheel != null && cityWheel != null){
			provinceWheel.setCurrentItem(curritem1);
			cityWheel.setCurrentItem(curritem2);
		}
	}

	OnCityWheelListener whellListener;

	public void setOnCityWheelListener(OnCityWheelListener whellListener) {
		this.whellListener = whellListener;
	}

	public interface OnCityWheelListener {
		void onChoosed(String title, AdType type, int curritem1, int curritem2);
		//void onCityChanged(String cityName);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.wheel_cancel:
				pop.dismiss();
				break;
			case R.id.wheel_ok:
				pop.dismiss();
				//ProvinceItem item1 = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
				AdTypeParent provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
				AdType item2 = cityAdapter.getSingleData(cityWheel.getCurrentItem());

				whellListener.onChoosed(provinceItem.getTitle(),item2,provinceWheel.getCurrentItem(),cityWheel.getCurrentItem());
				break;

		}
	}
}
