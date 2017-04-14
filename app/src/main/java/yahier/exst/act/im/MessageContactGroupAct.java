package yahier.exst.act.im;

import com.stbl.stbl.R;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.common.ThemeActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MessageContactGroupAct extends ThemeActivity{
	ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_contact_group);
		setLabel("我的讨论组");
		listView = (ListView)findViewById(R.id.list);
		Adapter adapter = new Adapter(this);
		listView.setAdapter(adapter);
	}
	
	//获取数据
	void getList(){
		
	}

	
	public class Adapter extends CommonAdapter {
		Context mContext;
		MyApplication app;

		public Adapter(Context mContext) {
			this.mContext = mContext;
			app = (MyApplication) mContext.getApplicationContext();
		}

		@Override
		public int getCount() {
			return 6;
		}

		class CityHolder {
			TextView item_name;
			TextView item_num;
		}

		@Override
		public View getView(final int i, View con, ViewGroup parent) {
			CityHolder ho = null;
			if (con == null) {
				ho = new CityHolder();
				con = LayoutInflater.from(mContext).inflate(R.layout.contact_group_list_item, null);
				ho.item_name = (TextView) con.findViewById(R.id.name);
				ho.item_num = (TextView) con.findViewById(R.id.num);
				con.setTag(ho);
			} else
				ho = (CityHolder) con.getTag();


			return con;
		}

	}
	
	
}
