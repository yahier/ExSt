package yahier.exst.ui.BaseClass.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.stbl.stbl.api.utils.Logger;
import com.stbl.stbl.common.BaseActivity;

/**
 * Created by meteorshower on 16/3/2.
 */
public abstract class STBLFragmentActivity extends BaseActivity {

    protected Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.logger = new Logger(this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
