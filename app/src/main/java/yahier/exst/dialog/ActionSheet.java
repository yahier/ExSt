package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.DialogStringAdapter;

import java.util.ArrayList;

/**
 * Created by tnitf on 2016/6/13.
 */
public class ActionSheet extends Dialog {

    private ListView mListView;

    public ActionSheet(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_action_sheet);
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    public void setAdapter(ArrayList<String> list, AdapterView.OnItemClickListener listener) {
        mListView.setAdapter(new DialogStringAdapter(list));
        mListView.setOnItemClickListener(listener);
    }
}
