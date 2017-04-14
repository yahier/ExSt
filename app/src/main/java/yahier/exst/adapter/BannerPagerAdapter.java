package yahier.exst.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stbl.stbl.model.Banner;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

public class BannerPagerAdapter extends PagerAdapter {

    private ArrayList<Banner> mList;

    public BannerPagerAdapter(ArrayList<Banner> bannerList) {
        mList = bannerList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageUtils.loadBanner(mList.get(position).getImgurl(), view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
