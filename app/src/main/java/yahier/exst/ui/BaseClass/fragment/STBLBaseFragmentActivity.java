package yahier.exst.ui.BaseClass.fragment;

import android.support.v4.app.Fragment;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.stbl.stbl.widget.titlebar.NavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meteorshower on 16/3/2.
 */
public abstract class STBLBaseFragmentActivity extends STBLFragmentActivity implements FinalHttpCallback{

    protected STProgressDialog dialog;
    protected NavigationView navigationView;
    protected List<Fragment> fragmentList = new ArrayList<Fragment>();

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, true);
    }

    public void setContentView(int layoutResId, boolean initNavigation){
        super.setContentView(layoutResId);
        dialog = new STProgressDialog(this);
        if(initNavigation)
            navigationView = (NavigationView)findViewById(R.id.navigation_view);
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(this, item.getErr().getMsg());
            STBLBaseFragmentActivity.this.httpParseError(methodName, item);
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());

        STBLBaseFragmentActivity.this.httpParseResult(methodName, result, obj);
    }

    public abstract void httpParseResult(String methodName, String result,String valueObj);

    public abstract void httpParseError(String methodName,BaseItem baseItme);
}
