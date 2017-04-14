package yahier.exst.act.home.mall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.adapter.mall.CartAdapter;
import com.stbl.stbl.item.BaseItem;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.MallCart;
import com.stbl.stbl.model.MallCartGoods;
import com.stbl.stbl.model.MallCartShop;
import com.stbl.stbl.ui.BaseClass.STBLBaseTableActivity;
import com.stbl.stbl.util.DialogSimple2;
import com.stbl.stbl.util.HttpEntity;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.Params;
import com.stbl.stbl.util.StringUtil;
import com.stbl.stbl.util.TipsDialog;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.utils.SpannableUtils;
import com.stbl.stbl.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;

/**
 * 购物车
 *
 * @author yahier
 */
public class MallCartAct extends STBLBaseTableActivity<MallCartShop> implements CartAdapter.OnCartItemListener, OnClickListener {

    private CartAdapter adapter;
    private CheckBox checkBox;
    private TextView tvmoneyCount;
    private MallCart mallCart, mallCartDeliver;
    private boolean isSelectedAnyGoods = false;// 是否勾选了任何商品
    private Button btnCommit, btnDelete;
    private int delectedSize = 0;

    private TextView emptyView;
    private View parentBottomView;
    private int typeSource;
    /**
     * 已选商品数量
     */
    private int selectGoodsCount = 0;

    // 下单后更新购物车的code
    public static final int REQUEST_CODE = 0X001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall_shoppingcart);

        adapter = new CartAdapter(this, arrayList);
        bindRefreshList(R.id.lv_content, adapter);
        setPullLoad(false);
        adapter.setOnItemListener(this);

        navigationView.setTitleBar(getString(R.string.mall_shopping_car));
        navigationView.setClickLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvmoneyCount = (TextView) findViewById(R.id.moneyCount);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        checkBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    resetAll();
                }
                return true;
            }
        });
//        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
//                adapter.setAllChecked(isChecked);
//                onMoneyChanged(adapter.getListData());
////                float count = 0;
////                if(isChecked){
////                    for (int i = 0; i < mallCart.getCartshops().size(); i++) {
////                        MallCartShop info = mallCart.getCartshops().get(i);
////                        info.setSelected(true);
////                        if(info.getShopid() > 0) {
////                            for(int k = 0 ; k < info.getCartgoods().size() ; k++){
////                                MallCartGoods goodsInfo = info.getCartgoods().get(k);
////                                goodsInfo.setSelected(true);
////                                count += goodsInfo.getGoodscount() * goodsInfo.getRealprice();
////                            }
////                        }
////                    }
////                }
////                String countText = StringUtil.get2ScaleString(count).equals("") ? String.valueOf(count) : StringUtil.get2ScaleString(count);
////                tvmoneyCount.setText(SpannableUtils.formatSpannable("合计: ￥" + countText, 0, 3, UIUtils.getResColor(R.color.gray1), 0));
//            }
//        });

        btnDelete = (Button) findViewById(R.id.cart_deltet_btn);
        btnDelete.setOnClickListener(this);
        btnCommit = (Button) findViewById(R.id.cart_commit_btn);
        btnCommit.setOnClickListener(this);
        findViewById(R.id.all_change_tv).setOnClickListener(this);
        parentBottomView = findViewById(R.id.layout1);
        emptyView = (TextView) findViewById(R.id.empty_tv);
        emptyView.setText(R.string.mall_car_empty);
        getRefreshView().setEmptyView(emptyView);
        typeSource = SharedMallType.getType(this);

        setMenuRightExitClick();

//        startRefresh();
        onReload();
    }
    //重置所有商品状态
    private void resetAll(){
        adapter.setAllChecked(!checkBox.isChecked());
        onMoneyChanged(adapter.getListData());
    }

    @Override
    public void loadMore() {
        getCartList();
    }

    @Override
    public void onReload() {
        getCartList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void httpParseError(String methodName, BaseItem baseItme) {
        dialog.dismiss();
        stopRefresh();
    }

    @Override
    public void httpParseResult(String methodName, String result, String valueObj) {
        stopRefresh();
        dialog.dismiss();

        switch (methodName) {
            case Method.cartShow: {
                mallCart = JSONHelper.getObject(valueObj, MallCart.class);
                if (mallCart == null) {
                    onMoneyChanged(null);
                    ToastUtil.showToast(MallCartAct.this, getString(R.string.mall_shopping_cart_empty));
                    return;
                }
                onMoneyChanged(mallCart.getCartshops());
                if (mallCart.getCartshops() != null) {
                    arrayList.clear();
                    arrayList.addAll(mallCart.getCartshops());
                    adapter.setAllChecked(checkBox.isChecked());
                    tableAdapter.notifyDataSetChanged();
                }

                if (arrayList.size() == 0) {
                    parentBottomView.setVisibility(View.GONE);
                    navigationView.setTextClickRight("", null);
                } else {
                    parentBottomView.setVisibility(View.VISIBLE);
                    setMenuRightExitClick();
                }
            }
            break;
            case Method.cartDelete:
                adapter.setDeleteFlag(false);
                btnCommit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
                startRefresh();
                EventBus.getDefault().post(new EventType(EventType.TYPE_MALL_NUM_CHANGE));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cart_deltet_btn:// 删除
                if (isSelectedAnyGoods == false) {
                    ToastUtil.showToast(MallCartAct.this, getString(R.string.mall_no_select_goods));
                    return;
                }
                DialogSimple2 dialog = new DialogSimple2(this, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        List<Integer> listIds = getSelectedCartsId();
                        for (int i = 0; i < listIds.size(); i++) {
                            cartDelete(listIds.get(i));
                        }
                    }
                });
                dialog.show();
                dialog.setMessage(getString(R.string.mall_is_delete_select));
                dialog.setBtnText(getString(R.string.mall_confirm2));
                break;
            case R.id.cart_commit_btn://结算
                if (isSelectedAnyGoods == false) {
                    ToastUtil.showToast(MallCartAct.this, getString(R.string.mall_no_select_goods));
                    return;
                }
                if (isHasSoldOut()) {
                    return;
                }
                Intent intent = new Intent(MallCartAct.this, MallOrderCommitAct.class);
                intent.putExtra("item", mallCartDeliver);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.all_change_tv:
                resetAll();
//                checkBox.setChecked(!checkBox.isChecked());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            getCartList();
        }
    }

    private void getCartList() {
        Params params = new Params();
        params.put("malltype", typeSource);
        new HttpEntity(this).commonPostData(Method.cartShow, params, this);
    }

    // 删除物品
    private void cartDelete(int cartId) {
        Params params = new Params();
        params.put("cartid", cartId);
        params.put("malltype", typeSource);
        new HttpEntity(this).commonPostData(Method.cartDelete, params, this);
    }

    /**
     * 获取选中的购物车id
     *
     * @return
     */
    private List<Integer> getSelectedCartsId() {
        if (mallCartDeliver == null || mallCart == null) {
            return null;
        }
        List<Integer> listGoodsId = new ArrayList<Integer>();
        List<MallCartShop> list = checkBox.isChecked() ? mallCart.getCartshops() : mallCartDeliver.getCartshops();
        for (int i = 0; i < list.size(); i++) {
            List<MallCartGoods> listGoods = list.get(i).getCartgoods();
            for (int j = 0; j < listGoods.size(); j++) {
                listGoodsId.add(listGoods.get(j).getCartid());
            }
        }
        return listGoodsId;
    }

    private void showTipsDialog(String goodsName) {
        if (goodsName == null || "".equals(goodsName)) return;
        SpannableString spannableString = new SpannableString(goodsName + getString(R.string.mall_sold_out));
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray_text)), goodsName.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TipsDialog.popup(MallCartAct.this, getString(R.string.mall_sold_out_tips), spannableString, getString(R.string.mall_confirm2));
    }

    //是否有下架商品
    private boolean isHasSoldOut() {
        if (mallCartDeliver.getCartshops() != null) {
            List<MallCartShop> cartShopList = mallCartDeliver.getCartshops();//购物车列表
            for (int i = 0; i < cartShopList.size(); i++) {
                if (cartShopList.get(i) != null) {
                    List<MallCartGoods> goodList = cartShopList.get(i).getCartgoods();//购物车列表item，商品列表
                    for (int k = 0; k < goodList.size(); k++) {
                        if (goodList.get(k) != null && goodList.get(k).getIsonshelf() == 0) {
                            showTipsDialog(goodList.get(k).getGoodsname());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void setMenuRightOverClick() {
        navigationView.setTextClickRight(getString(R.string.mall_done), new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCommit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.GONE);
                adapter.setDeleteFlag(false);
                setMenuRightExitClick();
            }
        });
    }

    private void setMenuRightExitClick() {
        navigationView.setTextClickRight(getString(R.string.mall_edit), new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCommit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
                adapter.setDeleteFlag(true);
                setMenuRightOverClick();
            }
        });
    }

    @Override
    public void onMoneyChanged(List<MallCartShop> list) {
        if (list == null) {
            tvmoneyCount.setText(SpannableUtils.formatSpannable(getString(R.string.mall_total)+" ￥0", 0, 3, UIUtils.getResColor(R.color.gray1), 0));
            return;
        }
        isSelectedAnyGoods = false;
        boolean isSelectedAll = true;
        selectGoodsCount = 0;
        LogUtil.logE("MallCartAct onMoneyChanged");
        List<MallCartShop> newlist = new ArrayList<MallCartShop>();
        newlist.clear();
        // 重建数据
        for (int i = 0; i < list.size(); i++) {
            List<MallCartGoods> newlistGoods = new ArrayList<MallCartGoods>();
            MallCartShop shop = list.get(i);
            List<MallCartGoods> listGoods = shop.getCartgoods();
            // 如果选中了物品，才传入店铺
            boolean isGoodsSelected = false;
            int goodsCount = listGoods.size();
            // 如果店铺的物品都没有了，也不要店铺了
            if (goodsCount == 0)
                continue;
            float subMoneyTotal = 0;
            for (int j = 0; j < goodsCount; j++) {
                // 如果物品选中了
                if (listGoods.get(j).isSelected()) {
                    selectGoodsCount += listGoods.get(j).getGoodscount();
                    isGoodsSelected = true;
                    isSelectedAnyGoods = true;
                    newlistGoods.add(listGoods.get(j));
                    subMoneyTotal += listGoods.get(j).getRealprice() * listGoods.get(j).getGoodscount();
                } else {
                    isSelectedAll = false;
                }
            }
            btnCommit.setText(getString(R.string.mall_settle_accounts)+"("+selectGoodsCount+")");
            if (isSelectedAll) { //如果全选
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            if (isGoodsSelected) {
                shop.setTotalamount(subMoneyTotal);
//                shop.setCartgoods(newlistGoods);//不能更改原本的数据，否则选择了其中一个，其他没选的就没了
                MallCartShop newShop = new MallCartShop(shop);
                newShop.setCartgoods(newlistGoods);
                newlist.add(newShop);
            }
        }

        mallCartDeliver = new MallCart();
        mallCartDeliver.setCartshops(newlist);
        float count = 0;
        for (int i = 0; i < newlist.size(); i++) {
            count += newlist.get(i).getTotalamount();
        }
        LogUtil.logE("count:" + count);
        String totalText = getString(R.string.mall_total);
        if (adapter.isDeleteFlag()) {
            tvmoneyCount.setText(SpannableUtils.formatSpannable(totalText+" ￥" + 0.00, 0, totalText.length(), UIUtils.getResColor(R.color.gray1), 0));
        } else {
            String countText = StringUtil.get2ScaleString(count).equals("") ? String.valueOf(count) : StringUtil.get2ScaleString(count);
            tvmoneyCount.setText(SpannableUtils.formatSpannable(totalText+" ￥" + countText, 0, totalText.length(), UIUtils.getResColor(R.color.gray1), 0));
        }
        adapter.notifyDataSetChanged();
    }
}
