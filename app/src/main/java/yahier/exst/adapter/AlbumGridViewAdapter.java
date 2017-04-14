package yahier.exst.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.stbl.stbl.R;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 *
 * @author king
 * @version 2014年10月18日 下午11:49:35
 * @QQ:595163260
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private ArrayList<ImageItem> selectedDataList;
    private DisplayMetrics dm;

    public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList, ArrayList<ImageItem> selectedDataList) {
        mContext = c;
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public boolean isExists(String imgPath) {
        if (imgPath == null || imgPath.equals("")) return false;
        for (ImageItem item : selectedDataList) {
            if (imgPath.equals(item.imagePath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public ToggleButton toggleButton;
        public ImageView choosetoggle;
        public TextView textView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_imageview, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
            viewHolder.choosetoggle = (ImageView) convertView.findViewById(R.id.choosedbt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageItem item = dataList.get(position);
        ImageUtils.loadFile(R.drawable.default_square_image, item.file, viewHolder.imageView);

        viewHolder.toggleButton.setTag(position);
        viewHolder.choosetoggle.setTag(position);
        viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
        if (selectedDataList.contains(dataList.get(position)) || isExists(dataList.get(position).imagePath)) {
            viewHolder.toggleButton.setChecked(true);
            viewHolder.choosetoggle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.toggleButton.setChecked(false);
            viewHolder.choosetoggle.setVisibility(View.GONE);
        }
        return convertView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

    private class ToggleClickListener implements OnClickListener {
        View chooseBt;

        public ToggleClickListener(View choosebt) {
            this.chooseBt = choosebt;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) view;
                int position = (Integer) toggleButton.getTag();
                if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
                    mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
                }
            }
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        public void onItemClick(ToggleButton view, int position, boolean isChecked, View chooseBt);
    }

}
