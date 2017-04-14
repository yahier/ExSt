package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.util.LogUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 搜索结果列表
 * @author ruilin
 *
 */
public class MallSearchRecordFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private Context mContext;
	private SearchRecordAdapter mAdapter;
	private ArrayList<String> recordlist;
	private final String SP_NAME = "MALL_SEARCH_RECORD00";
	private final String SP_KEY = "MALL_SEARCH_KEY";
	private final String SP_DELIMITER = "|";
	private final int MAX_NUM = 5;
	
	private View btn_clear;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		View footer = inflater.inflate(R.layout.mall_search_record_item_clear, null);
//		LinearLayout layout = (LinearLayout) view.findViewById(R.id.line_scroll);
		btn_clear = footer.findViewById(R.id.button1);
		btn_clear.setOnClickListener(this);
		ListView listview = new ListView(mContext);
		listview.setOnItemClickListener(this);
		listview.addFooterView(footer);
		readRecord();
		mAdapter = new SearchRecordAdapter(recordlist);
		listview.setAdapter(mAdapter);
//		LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//		layout.addView(listview, 0, params);
		
		return listview;
	}
	
	public void addRecord(String key) {
		for (int i = 0; i < recordlist.size(); i++) {
			String item = recordlist.get(i);
			if (0 == item.compareTo(key)) {
				recordlist.remove(i);
				break;
			}
		}
		if (recordlist.size() >= MAX_NUM && MAX_NUM > 0) {
			recordlist.remove(recordlist.size() - 1);
		}
		recordlist.add(0, key);
		SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		String[] array = new String[recordlist.size()];
		recordlist.toArray(array);
		String record = TextUtils.join(SP_DELIMITER, array);
		sp.edit().putString(SP_KEY, record).commit();
		mAdapter.notifyDataSetChanged();
	}
	
	public void readRecord() {
		SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		String record = sp.getString(SP_KEY, "");
		String[] keys = TextUtils.split(record, '['+SP_DELIMITER+']');
		if (null == recordlist) {
			recordlist = new ArrayList<String>(keys.length);
		} else {
			recordlist.clear();
		}
		for (int i = 0; i < keys.length; i++) {
			recordlist.add(keys[i]);
		}
	}
	
	public void cleanRecord() {
		SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
		recordlist.clear();
		mAdapter.notifyDataSetChanged();
	}
	class SearchRecordAdapter extends BaseAdapter {
		List<String> mData;
		
		public SearchRecordAdapter(ArrayList<String> data) {
			mData = data;
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.mall_search_record_item, null);
				holder.textview = (TextView) convertView.findViewById(R.id.tv_record);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String item = (String) getItem(position);
			holder.textview.setText(item);
			return convertView;
		}
		
		class ViewHolder {
			TextView textview;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_clear) {
			cleanRecord();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String key = recordlist.get(position);
		MallSearchAct act = (MallSearchAct) getActivity();
		act.setSearchKey(key);
		addRecord(key);
	}
	
}
