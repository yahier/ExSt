package yahier.exst.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.stbl.stbl.common.MyApplication;

import java.util.ArrayList;

public abstract class OneTypeAdapter<T> extends BaseAdapter {

    protected ArrayList<T> mList;
    protected LayoutInflater mInflater;

    public OneTypeAdapter(ArrayList<T> list) {
        mList = list;
        mInflater = LayoutInflater.from(MyApplication.getContext());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(getLayoutId(), parent, false);
            holder = getViewHolder();
            holder.init(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(convertView, position);
        return convertView;
    }

    protected abstract int getLayoutId();

    protected abstract ViewHolder getViewHolder();

    protected ArrayList<T> getList() {
        return mList;
    }

    protected interface ViewHolder {
        void init(View v);

        void bind(View v, int position);
    }

}


