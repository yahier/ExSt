package yahier.exst.ui.BaseClass.fragment;

import android.os.Bundle;

import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.dialog.STProgressDialog;
import com.stbl.stbl.widget.titlebar.NavigationView;

/**
 * Created by meteorshower on 16/3/7.
 */
public abstract class STBLBaseFragment extends STBLFragment implements FinalHttpCallback {

    protected STProgressDialog dialog;
    protected NavigationView navigationView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialog = new STProgressDialog(getActivity());

        if(STBLBaseFragment.this.getNavigationResId() > 0 && getView() != null){
            navigationView = (NavigationView)getView().findViewById(STBLBaseFragment.this.getNavigationResId());
        }
    }

    @Override
    public void parse(String methodName, String result) {
        BaseItem item = JSONHelper.getObject(result, BaseItem.class);

        if (item == null){
            STBLBaseFragment.this.httpParseError(methodName, item);
            return;
        }

        if (item.getIssuccess() != BaseItem.successTag) {
            if(item.getIssuccess()!=BaseItem.errorNoTaostTag){
                ToastUtil.showToast(getActivity(), item.getErr().getMsg());
            }
            STBLBaseFragment.this.httpParseError(methodName, item);
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());

        STBLBaseFragment.this.httpParseResult(methodName, result, obj);
    }

    public abstract int getNavigationResId();

    public abstract void httpParseResult(String methodName, String result,String valueObj);

    public abstract void httpParseError(String methodName,BaseItem baseItme);
}
