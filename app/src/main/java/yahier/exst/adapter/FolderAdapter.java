package yahier.exst.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.ShowAllPhoto;
import com.stbl.stbl.util.ImageBucket;
import com.stbl.stbl.util.ImageItem;
import com.stbl.stbl.util.ImageUtils;

import java.util.ArrayList;

import io.rong.imlib.model.Conversation;

/**
 * 这个是显示所有包含图片的文件夹的适配器
 *
 * @author king
 * @version 2014年10月18日 下午11:49:44
 * @QQ:595163260
 */
public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private Intent mIntent;
    private DisplayMetrics dm;
    int maxNum = 0;
    final String TAG = getClass().getSimpleName();
    private Conversation conversation;

    private ArrayList<ImageBucket> mList;

    public FolderAdapter(Context c) {
        init(c);
    }

    public FolderAdapter(Context c, ArrayList<ImageBucket> list, Conversation conversation) {
        this.conversation = conversation;
        init(c);
        mList = list;
    }

    // 初始化
    public void init(Context c) {
        mContext = c;
        mIntent = ((Activity) mContext).getIntent();
        dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        //
        public ImageView backImage;
        // 封面
        public ImageView imageView;
        public ImageView choose_back;
        // 文件夹名称
        public TextView folderName;
        // 文件夹里面的图片数量
        public TextView fileNum;
    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_folder, null);
            holder = new ViewHolder();
            holder.backImage = (ImageView) convertView.findViewById(R.id.file_back);
            holder.imageView = (ImageView) convertView.findViewById(R.id.file_image);
            holder.choose_back = (ImageView) convertView.findViewById(R.id.choose_back);
            holder.folderName = (TextView) convertView.findViewById(R.id.name);
            holder.fileNum = (TextView) convertView.findViewById(R.id.filenum);
            // LinearLayout.LayoutParams lp = new
            // LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dipToPx(65));
            // lp.setMargins(50, 0, 50,0);
            // holder.imageView.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageBucket bucket = mList.get(position);
        // 给folderName设置值为文件夹名称
        holder.folderName.setText(mList.get(position).bucketName);

        if (bucket.imageList != null && bucket.imageList.size() > 0) {
            ImageItem item = bucket.imageList.get(0);
            ImageUtils.loadFile(R.drawable.default_square_image, item.file, holder.imageView);

            // 给fileNum设置文件夹内图片数量
            holder.fileNum.setText("" + bucket.imageList.size());
        } else {
            holder.imageView.setImageResource(R.drawable.default_square_image);
            holder.fileNum.setText("0");
        }
        // 为封面添加监听
        holder.imageView.setOnClickListener(new ImageViewClickListener(position, mIntent, holder.choose_back));

        return convertView;
    }

    // 为每一个文件夹构建的监听器
    private class ImageViewClickListener implements OnClickListener {
        private int position;
        private Intent intent;
        private ImageView choose_back;

        public ImageViewClickListener(int position, Intent intent, ImageView choose_back) {
            this.position = position;
            this.intent = intent;
            this.choose_back = choose_back;
        }

        public void onClick(View v) {
            ShowAllPhoto.dataList = mList.get(position).imageList;
            Intent intent = new Intent();
            String folderName = mList.get(position).bucketName;
            intent.putExtra("folderName", folderName);
            if (0 != maxNum) {
                intent.putExtra("maxNum", maxNum);
            }
            intent.setClass(mContext, ShowAllPhoto.class);
            intent.putExtra("conversation", conversation);
            mContext.startActivity(intent);
        }
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

}
