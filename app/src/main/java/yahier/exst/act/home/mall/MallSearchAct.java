package yahier.exst.act.home.mall;

import com.stbl.stbl.R;
import com.stbl.stbl.common.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 *已经做废弃类 
 * @author lenovo
 *
 */
public class MallSearchAct extends BaseActivity implements OnClickListener {
	private View btn_search;
	private EditText et_search;
	private MallSearchRecordFragment frag_search;
	MallGoodsFragment frag_goods;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.mall_search);
		View btn_back = findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_search = findViewById(R.id.button1);
		btn_search.setOnClickListener(this);
		et_search = (EditText) findViewById(R.id.et_search);
		frag_search = (MallSearchRecordFragment) getSupportFragmentManager().findFragmentById(R.id.id_fragment_search);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.button1:
			/* 保存搜索记录 */
			String text = et_search.getText().toString();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (text.length() != 0) {
				frag_search.addRecord(et_search.getText().toString());
				/* 跳转到搜索结果 */
				if (null == frag_goods) {
					frag_goods = new MallGoodsFragment();
					ft.add(R.id.id_fragment_search, frag_goods).commit();
				} else {
				}
				ft.show(frag_goods);
			} else {
				if (null != frag_goods) {
					ft.hide(frag_goods);
				}
				ft.show(frag_search);
			}
			break;
		}
	}
	
	public void setSearchKey(String key) {
		et_search.setText(key);
	}
}
