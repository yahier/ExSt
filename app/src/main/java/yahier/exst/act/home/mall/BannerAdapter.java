package yahier.exst.act.home.mall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.GalleryActivity;
import com.stbl.stbl.common.ImagePagerAct;
import com.stbl.stbl.util.PicassoUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 该类已被弃用 Banner统一调用CommonBannerAdapter
 *
 * @author lenovo
 */
public class BannerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    HashMap<Integer, View> views;

    ArrayList<String> urls;

    int size;
    public final static float rateOfWH = 2.0f / 5;

    public BannerAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        views = new HashMap<Integer, View>();

    }

    public void setUrlArray(ArrayList<String> urls) {
        this.urls = urls;
        size = urls.size();
    }

    @Override
    public int getCount() {
        if (size > 1) {
            return size * 10000;
        } else {
            return size;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup group, int position) {

        while (position >= size) {
            position = position - size;
        }
        View view = views.get(position);

        if (view == null) {
            view = inflater.inflate(R.layout.mall_banner_item, group, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            if(urls != null){
                PicassoUtil.load(mContext, urls.get(position), imageView, R.drawable.img_goods_default);
            }
            views.put(position, view);
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        group.addView(view);
        final int index = position;
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Intent intent = new Intent(mContext, GalleryActivity.class);
//                intent.putStringArrayListExtra("urls", urls);
//                intent.putExtra("position", "1");
//                intent.putExtra("ID", index);
//                mContext.startActivity(intent);
                Intent intent = intent = new Intent(mContext, ImagePagerAct.class);
                intent.putExtra("index", index);
                intent.putExtra("list", urls);
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // while (position >= Config.listUrls.size()) {
        // position = position - Config.listUrls.size();
        // }
        while (position >= size) {
            position = position - size;
        }
        // LogUtil.logE("destroyItem——" + position);
        // container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    OnPagerItemClickListener onPagerItemClick;

    public void setPagerItemOnClickListener(OnPagerItemClickListener onPagerItemClick) {
        this.onPagerItemClick = onPagerItemClick;
    }

    public interface OnPagerItemClickListener {
        void onPagerItemClick();
    }
}
