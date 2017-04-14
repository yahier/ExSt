package yahier.exst.ui.ItemAdapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.stbl.stbl.widget.dialog.STProgressDialog;

import java.util.List;

/**
 * Created by meteorshower on 16/3/24.
 */
public abstract class STBLBaseAdapter<T> extends BaseAdapter {

    private Context context;
    private List<T> arrayList;
    private LayoutInflater inflater;
    private STProgressDialog dialog;

    public STBLBaseAdapter(Context context, List<T> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public T getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getInflaterView(int resourceId){
        return inflater.inflate(resourceId, null);
    }

    public View getInflaterView(int resourceId, ViewGroup vp){
        return  inflater.inflate(resourceId, vp);
    }

    public Context getContext(){
        return context;
    }

    public STProgressDialog getDialog(){
        if(dialog == null)
            dialog = new STProgressDialog(getContext());
        return dialog;
    }

    public List<T> getListData(){
        return arrayList;
    }

    /** 获取资源字符串 */
    public String getResString(int resourceId){
        return context.getResources().getString(resourceId);
    }

    /** 获取资源颜色 */
    public int getResColor(int resourceId){
        return context.getResources().getColor(resourceId);
    }
}
