package yahier.exst.util;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.example.dateselect.util.OnWheelScrollListener;
import com.example.dateselect.util.WheelAdapterCity;
import com.example.dateselect.util.WheelAdapterProvince;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.CityItem;
import com.stbl.stbl.item.ProvinceItem;

public class WheelCity implements OnClickListener {
	WheelView provinceWheel, cityWheel;

	MyApplication app;
	Context mContext;
	Dialog pop;
	WheelAdapterProvince provinceAdapter;
	WheelAdapterCity cityAdapter;

	private int provinceCurrentItem;
	private int cityCurrentItem;

	public void setCurrentItem(int provinceCurrentItem,int cityCurrentItem){
		this.provinceCurrentItem = provinceCurrentItem;
		this.cityCurrentItem = cityCurrentItem;
	}

	public void show(Context mContext, List<ProvinceItem> listProvince) {
		this.mContext = mContext;
		app = (MyApplication) mContext.getApplicationContext();

		pop = new Dialog(mContext, R.style.Common_Dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_city, null);
		view.findViewById(R.id.wheel_cancel).setOnClickListener(this);
		view.findViewById(R.id.wheel_ok).setOnClickListener(this);
		provinceWheel = (WheelView) view.findViewById(R.id.car_region_province);
		cityWheel = (WheelView) view.findViewById(R.id.car_region_city);
		provinceAdapter = new WheelAdapterProvince(listProvince);
		provinceWheel.setAdapter(provinceAdapter);

		provinceWheel.setCurrentItem(provinceCurrentItem);
		provinceCurrentItem = 0;

		provinceWheel.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				ProvinceItem provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());

				cityAdapter = new WheelAdapterCity(provinceItem.getCitys());
				cityWheel.setAdapter(cityAdapter);
				cityWheel.setCurrentItem(cityCurrentItem);
				cityCurrentItem = 0;
				// 将区域也跟着一起变动 更好的体验
				String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
				MyHandler.cityName = cityName;
				if (cityAdapter.getItemsCount() > 0) {
					CityItem city = cityAdapter.getSingleData(0);
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
				CityItem cityItem = cityAdapter.getSingleData(cityWheel.getCurrentItem());
				//whellListener.onCityChanged(cityItem.getCityname());

			}
		});

		// 进入时默认的显示第一个
		ProvinceItem provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
		cityAdapter = new WheelAdapterCity(provinceItem.getCitys());
		cityWheel.setAdapter(cityAdapter);
		cityWheel.setCurrentItem(cityCurrentItem);
		cityCurrentItem = 0;
		// 将区域也跟着一起变动 更好的体验
		String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
		MyHandler.cityName = cityName;
		if (cityAdapter.getItemsCount() > 0){
			CityItem city = cityAdapter.getSingleData(0);
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



	OnCityWheelListener whellListener;

	public void setOnCityWheelListener(OnCityWheelListener whellListener) {
		this.whellListener = whellListener;
	}

	public interface OnCityWheelListener {
		void onCityChoosed(String cityCode, String cityName);
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
				CityItem item2 = cityAdapter.getSingleData(cityWheel.getCurrentItem());

				whellListener.onCityChoosed(item2.getCitycode(),item2.getCityname());
				break;

		}
	}
}
