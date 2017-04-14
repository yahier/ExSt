package yahier.exst.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dateselect.util.ArrayWheelAdapter;
import com.example.dateselect.util.WheelView;
import com.stbl.stbl.R;
import com.stbl.stbl.widget.EmptyView;

import java.util.ArrayList;

public class StringWheelDialog extends Dialog {

    private EmptyView mEmptyView;
    private WheelView mWheelView;
    private ArrayList<String> mList;

    private IStringWheelDialog mInterface;

    public StringWheelDialog(Context context) {
        super(context, R.style.Common_Dialog);
        setContentView(R.layout.dialog_string_wheel);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        initView();
    }

    private void initView() {
        mEmptyView = (EmptyView) findViewById(R.id.empty_view);
        mWheelView = (WheelView) findViewById(R.id.wheel_view);

        mEmptyView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterface != null) {
                    mInterface.onRetry();
                }
            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mList == null || mList.size() == 0) {
                    return;
                }
                int position = mWheelView.getCurrentItem();
                if (mInterface != null) {
                    mInterface.onConfirm(position);
                }
            }
        });
    }

    public void setData(ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            mList = list;
            mWheelView.setAdapter(new ArrayWheelAdapter(mList));
            mEmptyView.hide();
            mWheelView.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<String> getData() {
        return mList;
    }

    public void showRetry() {
        mEmptyView.showRetry();
    }

    public void showEmpty() {
        mEmptyView.showEmpty();
    }

    public void setInterface(IStringWheelDialog i) {
        mInterface = i;
    }

    public interface IStringWheelDialog {
        void onConfirm(int position);

        void onRetry();
    }
}
