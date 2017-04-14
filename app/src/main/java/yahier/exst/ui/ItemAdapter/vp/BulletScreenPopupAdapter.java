package yahier.exst.ui.ItemAdapter.vp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stbl.stbl.R;

import java.util.List;

/**
 * 弹幕状态选择弹框适配器
 * Created by Administrator on 2016/3/10 0010.
 */
public class BulletScreenPopupAdapter extends BaseAdapter {
    private List<String> datas ;
    private Context mContext;

    public BulletScreenPopupAdapter(Context context,List<String> datas){
        this.mContext = context;
        this.datas = datas;
    }
    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public String getItem(int position) {
        return datas == null ? null : datas.size() > position ? datas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bulletscreen_onoff_item_layout,null);
            holder = new TextHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (TextHolder) convertView.getTag();
        }
        String content = getItem(position);
        holder.text.setText(content);
        return convertView;
    }

    static class TextHolder{
        TextView text;
        public TextHolder(View view){
            text = (TextView) view.findViewById(R.id.tv_text);
        }
    }
}
