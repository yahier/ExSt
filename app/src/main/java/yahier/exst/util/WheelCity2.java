package yahier.exst.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.example.dateselect.util.OnWheelScrollListener;
import com.example.dateselect.util.WheelAdapterArea;
import com.example.dateselect.util.WheelAdapterCity;
import com.example.dateselect.util.WheelAdapterProvince;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.AreaItem;
import com.stbl.stbl.item.CityItem;
import com.stbl.stbl.item.ProvinceItem;

public class WheelCity2 implements OnClickListener {
    WheelView provinceWheel, cityWheel, areaWheel;

    MyApplication app;
    Context mContext;
    PopupWindow window;
    WheelAdapterProvince provinceAdapter;
    WheelAdapterCity cityAdapter;
    WheelAdapterArea areaAdapter;

    private int provinceCurrentItem;
    private int cityCurrentItem;
    private int areaCurrentItem;

    public void show(Context mContext, List<ProvinceItem> listProvince) {
        this.mContext = mContext;
        app = (MyApplication) mContext.getApplicationContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.wheel_province_city_area, null);
        view.measure(0, 0);
        view.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        view.findViewById(R.id.wheel_ok).setOnClickListener(this);
        window = new PopupWindow(view, LayoutParams.FILL_PARENT, view.getMeasuredHeight(), true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(view, Gravity.CENTER, 0, 1000);

        provinceWheel = (WheelView) view.findViewById(R.id.car_region_province);
        cityWheel = (WheelView) view.findViewById(R.id.car_region_city);
        areaWheel = (WheelView) view.findViewById(R.id.car_region_area);
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
//                cityWheel.setCurrentItem(0);
                cityWheel.setCurrentItem(cityCurrentItem);
                cityCurrentItem = 0;
                // 将区域也跟着一起变动 更好的体验
                String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
                MyHandler.cityName = cityName;
                if (cityAdapter.getItemsCount() > 0) {
                    whellListener.onCityChoosed(cityAdapter.getSingleData(0).getCitycode());
                } else {
                    // 清空区域
                    List<AreaItem> list = new ArrayList<AreaItem>();
                    showArea(list);
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
                whellListener.onCityChoosed(cityItem.getCitycode());

            }
        });

        // 进入时默认的显示第一个
        ProvinceItem provinceItem = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
        cityAdapter = new WheelAdapterCity(provinceItem.getCitys());
        cityWheel.setAdapter(cityAdapter);
//		cityWheel.setCurrentItem(0);
        cityWheel.setCurrentItem(cityCurrentItem);
        cityCurrentItem = 0;
        // 将区域也跟着一起变动 更好的体验
        String cityName = cityWheel.getAdapter().getItem(cityWheel.getCurrentItem());
        MyHandler.cityName = cityName;
        if (cityAdapter.getItemsCount() > 0)
            whellListener.onCityChoosed(cityAdapter.getSingleData(cityWheel.getCurrentItem()).getCitycode());
//            whellListener.onCityChoosed(cityAdapter.getSingleData(0).getCitycode());
    }

    public void showArea(List<AreaItem> list) {
        areaAdapter = new WheelAdapterArea(list);
        areaWheel.setAdapter(areaAdapter);

        areaWheel.setCurrentItem(areaCurrentItem);
        areaCurrentItem = 0;
    }

    OnCityWheelListener whellListener;

    public void setOnCityWheelListener(OnCityWheelListener whellListener) {
        this.whellListener = whellListener;
    }

    public interface OnCityWheelListener {
        void onChoosedOk(String value, String code1, String code2, String code3);

        void onCityChoosed(String cityCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wheel_cancel:
                window.dismiss();
                break;
            case R.id.wheel_ok:
                window.dismiss();
                provinceCurrentItem = provinceWheel.getCurrentItem();
                cityCurrentItem = cityWheel.getCurrentItem();
                areaCurrentItem = areaWheel.getCurrentItem();

                ProvinceItem item1 = provinceAdapter.getSingleData(provinceWheel.getCurrentItem());
                CityItem item2 = cityAdapter.getSingleData(cityWheel.getCurrentItem());
                AreaItem item3 = areaAdapter.getSingleData(areaWheel.getCurrentItem());

                StringBuffer sbName = new StringBuffer();
                String code1 = null;
                String code3 = null;
                String code2 = null;
                if (item1 != null) {
                    sbName.append(item1.getProvincename());
                    code1 = item1.getAdcode();

                }
                if (item2 != null) {
                    sbName.append(item2.getCityname());
                    code2 = item2.getAdcode();
                }

                if (item3 != null) {
                    sbName.append(item3.getDistrictname());
                    code3 = item3.getAdcode();
                }
                whellListener.onChoosedOk(sbName.toString(), code1, code2, code3);
                break;

        }
    }
}
