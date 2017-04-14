package yahier.exst.widget;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DialogStringAdapter;

import java.util.ArrayList;

public class DialogFactory {

    public static Dialog createActionSheet(Activity activity, ArrayList<String> list, AdapterView.OnItemClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.Common_Dialog);
        View v = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_action_sheet, null);
        ListView listView = (ListView) v.findViewById(R.id.lv);
        listView.setAdapter(new DialogStringAdapter(list));
        listView.setOnItemClickListener(listener);
        v.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        return dialog;
    }

    public static Dialog createLoadingDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity, R.style.Common_Dialog);
        View v = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_loading, null);
        TextView msgTv = (TextView) v.findViewById(R.id.tv_msg);
        msgTv.setText(msg);
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        return dialog;
    }

    public static Dialog createConfirmDialog(Activity activity, String title, String content, final View.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity, R.style.Common_Dialog);
        View v = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_confirm, null);
        TextView titleTv = (TextView) v.findViewById(R.id.tv_title);
        titleTv.setText(title);

        TextView contentTv = (TextView) v.findViewById(R.id.tv_content);
        contentTv.setText(content);

        v.findViewById(R.id.btn_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        v.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onClick(v);
            }
        });
        dialog.setContentView(v);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        return dialog;
    }

}
