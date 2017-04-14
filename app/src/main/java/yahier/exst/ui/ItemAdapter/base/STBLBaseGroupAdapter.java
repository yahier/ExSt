package yahier.exst.ui.ItemAdapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stbl.stbl.R;
import com.stbl.stbl.util.Util;

import java.util.List;

/**
 * Created by meteorshower on 16/3/29.
 */
public abstract class STBLBaseGroupAdapter<T> {

    private Context context;
    private List<T> arrayList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private ViewGroup llPage;
    private int maxCount = 0;
    private View dividerLine = null;
    private int dividerColor = R.color.none_color;
    private int dividerHeight = 0;

    public STBLBaseGroupAdapter(Context context, List<T> arrayList){
        this.context = context;
        this.arrayList = arrayList;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount(){
        if (arrayList == null)
            return 0;
        return arrayList.size();
    }

    public T getItem(int position){
        if(arrayList == null)
            return null;
        return arrayList.get(position);
    }

    public abstract View getView(int position, View convertView, ViewGroup parentView);

    protected View getInflaterView(int layoutId){
        return getInflaterView(layoutId, null);
    }

    protected View getInflaterView(int layoutId,ViewGroup root){
        return inflater.inflate(layoutId, root);
    }

    /** 初始化数据 */
    public void setAdapter(ViewGroup llPage){
        setAdapter(llPage, getCount());
    }

    /** 初始化数据 */
    public void setAdapter(ViewGroup llPage,int maxCount){
        this.llPage = llPage;
        this.maxCount = maxCount;
        this.llPage.removeAllViews();
        for(int i = 0 ; i < maxCount; i++){
            View viewItem = this.getView(i, null, null);
            viewItem.setOnClickListener(new ViewItemClickListener(i));
            this.llPage.addView(viewItem);

            if (dividerHeight > 0) {
                this.dividerLine = new View(getContext());
                this.dividerLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                this.dividerLine.setBackgroundResource(dividerColor);
                this.llPage.addView(dividerLine);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    private class ViewItemClickListener implements View.OnClickListener{

        private int position;

        public ViewItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
                listener.onItemClick(position, v, null);
        }
    }

    public interface OnItemClickListener{

        public void onItemClick(int position, View view, ViewGroup parentView);
    }

    public void notifyDataSetChanged(){
        setAdapter(llPage,maxCount);
    }

    public Context getContext(){
        return context;
    }

    public List<T> getListData(){
        return arrayList;
    }

    public void setDividerColor(int resourcesId){
        this.dividerColor = resourcesId;
    }

    public void setDividerHeight(float heightValue){
        this.dividerHeight = Util.dip2px(getContext(), heightValue);
    }
}
