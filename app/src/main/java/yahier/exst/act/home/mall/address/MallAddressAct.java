package yahier.exst.act.home.mall.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.Address;
import com.stbl.stbl.ui.BaseClass.STBLBaseTableActivity;
import com.stbl.stbl.ui.ItemAdapter.MallAddressListAdapter;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.widget.XListView;

import java.util.List;

public class MallAddressAct extends STBLBaseTableActivity<Address> implements FinalHttpCallback {

    private int typeSource;
    public final static int typeSourceDefault = 0;
    public final static int typeSourceSelect = 1;//
    private TextView emptyView;
    private Button btnControls1;//添加新地址
    private boolean isNull = false;//地址是否为空
    private XListView xListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_addr_mng);

        navigationView.setTitleBar(getString(R.string.mall_address));
        navigationView.setClickLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMallAddressAct();
            }
        });

        btnControls1 = (Button) findViewById(R.id.controls1);
        typeSource = getIntent().getIntExtra("typeSource", typeSourceDefault);
        isNull = getIntent().getBooleanExtra("isNull", false);
        if (typeSource == typeSourceSelect) {
            if (isNull) {
                onClickNewAddress(null);
            }
        }
        xListView = (XListView) findViewById(R.id.lv_content);
        bindRefreshList(R.id.lv_content, new MallAddressListAdapter(this, arrayList));
        emptyView = (TextView) findViewById(R.id.empty_tv);
        emptyView.setText(R.string.mall_address_empty_create);

    }

    @Override
    protected void onResume() {
        super.onResume();

        startRefresh();
    }

    @Override
    public void onReload() {
        xListView.setPullLoadEnable(true);
        new HttpEntity(this).commonPostData(Method.getAddressList, null, this);
    }

    @Override
    public void loadMore() {
        new HttpEntity(this).commonPostData(Method.getAddressList, null, this);
    }
    //地址编辑
    public void toEditAddress(int position){
        Intent intent = new Intent();
        intent.setClass(MallAddressAct.this, MallAddressEditAct.class);
        intent.putExtra("type", 1);
        intent.putExtra("item", (Address) tableAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        switch (typeSource) {
            case typeSourceDefault:
                toEditAddress(position);
                break;
            case typeSourceSelect:
                intent.putExtra("item", (Address) tableAdapter.getItem(position));
                setResult(typeSourceSelect, intent);
                finish();
                break;
        }
    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        stopRefresh();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        stopRefresh();
        switch (methodName) {
            case Method.getAddressList:
                List<Address> addressList = JSONHelper.getList(valueObj, Address.class);
                LogUtil.logE("地址绑定");
                if (addressList != null) {
                    arrayList.clear();
                    arrayList.addAll(addressList);
                    tableAdapter.notifyDataSetChanged();
                    xListView.EndLoad();
                }

                emptyView.setVisibility(arrayList.size() == 0 ? View.VISIBLE : View.GONE);
                break;
        }
    }

    /**
     * Xml add new address on Click
     */
    public void onClickNewAddress(View v) {
        Intent intent = new Intent(this, MallAddressEditAct.class);
        if (isNull)
            intent.putExtra("isNull",isNull);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isNull){
            finishMallAddressAct();
        }else {
            startRefresh();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishMallAddressAct();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishMallAddressAct() {
        if (typeSource == typeSourceSelect) {
            Intent intent = new Intent();
            if (arrayList.size() > 0) {
                if (isNull)
                    intent.putExtra("item", arrayList.get(0));
            } else{
                intent.putExtra("address_empty", true);
            }
            setResult(typeSourceSelect, intent);
        }
        finish();
    }
}
