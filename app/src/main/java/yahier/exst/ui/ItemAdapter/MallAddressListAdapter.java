package yahier.exst.ui.ItemAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.address.MallAddressAct;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.model.Address;
import com.stbl.stbl.ui.ItemAdapter.base.STBLBaseAdapter;
import com.stbl.stbl.util.FinalHttpCallback;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;

import java.util.List;

/**
 * 地址列表适配器
 * Created by meteorshower on 16/3/24.
 */
public class MallAddressListAdapter extends STBLBaseAdapter<Address> implements FinalHttpCallback{

    private int operateIndex;

    public MallAddressListAdapter(Context context, List<Address> arrayList) {
        super(context, arrayList);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = getInflaterView(R.layout.address_list_item);

            vh.tvName = (TextView)convertView.findViewById(R.id.name);
            vh.tvPhone = (TextView)convertView.findViewById(R.id.phone);
            vh.tvDetails = (TextView)convertView.findViewById(R.id.address);
            vh.btnDefault = (Button)convertView.findViewById(R.id.address_type);
            vh.btnDelete = convertView.findViewById(R.id.delete);
            vh.btnEdit = convertView.findViewById(R.id.btn_edit);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }

        final Address info = getItem(position);
        vh.tvName.setText(info.getUsername());
        vh.tvPhone.setText(info.getPhone());
        vh.tvDetails.setText(getAddressDetails(info));

        if(info.getIsDefault()){//默认
            vh.btnDefault.setText(R.string.mall_default_address);
            vh.btnDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_xuanze, 0, 0, 0);
            vh.btnDefault.setOnClickListener(null);
        }else{
            vh.btnDefault.setText(R.string.mall_set_to_default_address);
            vh.btnDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_weixuanze, 0, 0, 0);
            vh.btnDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operateIndex = position;
                    getDialog().setMsgText(getContext().getString(R.string.mall_begin_set_default_address));
                    getDialog().show();
                    setDefault(info.getAddressid());
                }
            });
        }

        vh.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operateIndex = position;
                pormptDeleteAddress(info.getAddressid());
            }
        });
        vh.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof MallAddressAct){
                    ((MallAddressAct)getContext()).toEditAddress(position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder{
        private TextView tvName,tvPhone,tvDetails;
        private Button btnDefault;
        private View btnDelete,btnEdit;
    }

    private void pormptDeleteAddress(final int addressId){
        TipsDialog.popup(getContext(), getContext().getString(R.string.mall_delete_address_tips), getContext().getString(R.string.mall_cancel),
                getContext().getString(R.string.mall_confirm2), new TipsDialog.OnTipsListener() {

            @Override
            public void onConfirm() {
                getDialog().setMsgText(getContext().getString(R.string.mall_begin_delete_wait));
                getDialog().show();
                deleteAddress(addressId);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private String getAddressDetails(Address info){
        StringBuffer sb = new StringBuffer();
        sb.append(info.getCountryname());
        sb.append(info.getProvincename());
        sb.append(info.getCityname());
        sb.append(info.getDistrictname());
        sb.append(info.getAddress());
        return sb.toString();
    }

   private void setDefault(int addressid) {
        Params params = new Params();
        params.put("addressid", addressid);
        new HttpEntity(getContext()).commonPostData(Method.addressSetDefault, params, this);
    }

    private void deleteAddress(int addressid) {
        Params params = new Params();
        params.put("addressid", addressid);
        new HttpEntity(getContext()).commonPostData(Method.addressDelete, params, this);
    }

    @Override
    public void parse(String methodName, String result) {
        getDialog().dismiss();

        BaseItem item = JSONHelper.getObject(result, BaseItem.class);
        if (item.getIssuccess() != BaseItem.successTag) {
            ToastUtil.showToast(getContext(), item.getErr().getMsg());
            return;
        }
        String obj = JSONHelper.getStringFromObject(item.getResult());
        switch (methodName) {
            case Method.addressDelete:
                ToastUtil.showToast(getContext(), getContext().getString(R.string.mall_delete_success));
                getListData().remove(operateIndex);
                notifyDataSetChanged();
                if (getContext() instanceof MallAddressAct){
                    if (!((MallAddressAct) getContext()).isFinishing())
                        ((MallAddressAct)getContext()).startRefresh();
                }
                break;
            case Method.addressSetDefault:
                ToastUtil.showToast(getContext(), getContext().getString(R.string.mall_set_success));

                for (int i = 0; i < getListData().size(); i++) {
                    if (i == operateIndex) {
                        getListData().get(i).setIsdefault(Address.isdefaultYes);
                    } else {
                        getListData().get(i).setIsdefault(Address.isdefaultNo);
                    }
                }
                notifyDataSetChanged();
                break;
        }
    }
}
