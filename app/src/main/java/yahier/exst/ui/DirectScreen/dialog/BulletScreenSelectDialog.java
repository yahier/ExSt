package yahier.exst.ui.DirectScreen.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.stbl.stbl.R;
import com.stbl.stbl.ui.ItemAdapter.vp.BulletScreenPopupAdapter;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.ViewUtils;
import com.stbl.stbl.widget.LabelView;

import java.util.List;

/**
 * 选择弹幕的状态
 * Created by Administrator on 2016/3/9 0009.
 */
public class BulletScreenSelectDialog {
    private PopupWindow popupWindow;
    private ListView listView;
    private  int popupHeight;

    public BulletScreenSelectDialog(Context context,List<String> mDatas,int width, final OnBulletScreenListener listener){
        View view = LayoutInflater.from(context).inflate(R.layout.bullect_screen_onoff_popup_layout,null);
        popupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
//        popupWindow.setAnimationStyle(R.style.bottom_popupAnimation2);

        listView = (ListView) view.findViewById(R.id.lv_list);
        BulletScreenPopupAdapter adapter = new BulletScreenPopupAdapter(context,mDatas);
        listView.setAdapter(adapter);
        popupHeight = ViewUtils.getListHeight(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null){
                    listener.onItemClick((String) parent.getAdapter().getItem(position));
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null){
                    listener.onDissmiss();
                }
            }
        });
    }

    public boolean isShowing(){
        if (popupWindow != null && popupWindow.isShowing()){
            return true;
        }
        return false;
    }
    public void showPopup(View view){
        if (view == null) return;
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        if (popupWindow != null && !popupWindow.isShowing()){
            int y = location[1] - popupHeight;
            popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,location[0],y);
        }
    }

    public void dismissPopup(){
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public interface OnBulletScreenListener{
        void onItemClick(String item);
        void onDissmiss();
    }

}
