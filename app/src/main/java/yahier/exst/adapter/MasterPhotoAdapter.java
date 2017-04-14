package yahier.exst.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.mine.PhotoImagePagerAct;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.item.Photo;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 相片Adapter
 *
 * @author lenovo
 */
public class MasterPhotoAdapter extends CommonAdapter {
    Activity mContext;
    int widthOfItem;
    List<Photo> listPhoto;
    FrameLayout.LayoutParams params;
    final int extraWidthDP = 20;
    public final static int gridColumn = 5;
    final String tag = getClass().getSimpleName();
    final int verticalSpace = 2;

    public MasterPhotoAdapter(Activity mContext, List<Photo> list) {
        this.mContext = mContext;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        widthOfItem = (wm.getDefaultDisplay().getWidth() - (int) Device.getDensity(mContext) * extraWidthDP) / 5 - (int) Device.getDensity(mContext) * verticalSpace;
        listPhoto = list;
        params = new FrameLayout.LayoutParams(widthOfItem, widthOfItem);
        //params.setMargins(0, 0, 3, 0);
    }

    public void setData(List<Photo> listPhoto) {
        this.listPhoto = listPhoto;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return listPhoto.size() > 5 ? 5 : listPhoto.size();
    }

    class CityHolder {
        ImageView img;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.gridview_img, null);
            ho.img = (ImageView) con.findViewById(R.id.img);

            con.setTag(ho);
        } else
            ho = (CityHolder) con.getTag();

        Photo photo = listPhoto.get(i);
        ho.img.setLayoutParams(params);
        PicassoUtil.loadCropCenter(mContext, photo.getMiddleurl(), widthOfItem, widthOfItem, ho.img);


        con.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                // 浏览模式
                Intent intent = new Intent(mContext, PhotoImagePagerAct.class);
                intent.putExtra("index", i);
                intent.putParcelableArrayListExtra("photo", (ArrayList<? extends Parcelable>) listPhoto);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.ani_zoom_in, 0);


            }
        });
        return con;
    }

    OnCheckedListener listener;

    public void setCheckedListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedListener {
        void onCheckedChanged(List<Boolean> listBoo);
    }

}
