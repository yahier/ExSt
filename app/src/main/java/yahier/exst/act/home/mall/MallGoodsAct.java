package yahier.exst.act.home.mall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;
import com.stbl.stbl.item.EventType;
import com.stbl.stbl.model.GoodsClass;
import com.stbl.stbl.util.ToastUtil;
import com.stbl.stbl.widget.SearchEditText;

import io.rong.eventbus.EventBus;

/**
 * 商城（买家）商品列表主页
 * 
 * @author ruilin
 * 
 */
public class MallGoodsAct extends BaseActivity implements OnClickListener {
	private SearchEditText et_search;
	public int classid;
	TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mall_goods_main);

		et_search = (SearchEditText) findViewById(R.id.et_search);
		et_search.setOnClickListener(this);
		findViewById(R.id.top_left).setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		EventBus.getDefault().register(this);
		GoodsClass goodsClass = (GoodsClass) getIntent().getSerializableExtra("item");
		if (goodsClass != null) {
			classid = goodsClass.getClassid();
			tvTitle.setText(goodsClass.getClassname());
			EventBus.getDefault().post(new GoodsClass(goodsClass.getClassid()));
		}else{
			ToastUtil.showToast(this,getString(R.string.mall_no_find_goods));
		}
	}

	public void onEvent(EventType type) {
		switch (type.getType()) {
			case EventType.TYPE_FINISH_GOODS_DETAIL://支付完了，结束商品分类页
				finish();
				break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_left:
			finish();
			break;
		case R.id.et_search:
			Intent it = new Intent(this, MallSearchAct2.class);
			it.putExtra("classid", classid);
			startActivity(it);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
