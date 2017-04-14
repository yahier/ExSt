package yahier.exst.adapter.msg;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stbl.stbl.R;

/**
 * 通讯录adapter
 * @author lenovo
 *
 */
public class ContactListAdapter extends BaseExpandableListAdapter {

	private String[] ParentItem = { "师傅", "徒弟", "好友"};

	private String[][] ChildrenItem = { { "Activity", "Service", "Broadcast Receiver", "Content Provider", "纽带:Intent" }, { "相对布局", "线性布局", "表格布局", "绝对布局", "框架布局" },
			{ "文本控件", "按钮控件", "图像按钮", "开关按钮", "选择按钮", "图片控件", "时钟控件", "日期控件", "动画播放", "补间动画" }, { "数据适配器", "自动补全", "列表视图", "网格视图", "滚动视图", "滑块进度", "选项卡", "画廊控件", "下拉列表", "fragment+ViewPager" },
			{ "选项菜单", "上下文菜单", "对话框大全", "消息提示", "PopupWindow" }, { "文件存储数据（sd卡）", "SharedPreference共享参数", "SQLite数据库存储数据", "ContentProvider存储数据", "网络存储数据" } };

	private Context context;
	private ListView listView;
	private Button button; // 用于执行删除的button
	private TextView textView;
	private View frontView;

	public ContactListAdapter(Context context, ListView listView) {
		super();
		this.context = context;
		this.listView = listView;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int position, boolean arg2, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(context).inflate(R.layout.contact_list_item, null);
		//button = (Button) convertView.findViewById(R.id.btn_delete);
		textView = (TextView) convertView.findViewById(R.id.tvName);
		//frontView = convertView.findViewById(R.id.id_front);
	

		textView.setTextSize(20);
		textView.setTextColor(context.getResources().getColor(R.color.gray1));
		textView.setText(ChildrenItem[groupPosition][position]);

		return convertView;

	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return ChildrenItem[arg0].length;
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return ParentItem.length;
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean arg1, View arg2, ViewGroup arg3) {
		TextView tv = new TextView(context);
		tv.setTextSize(20);
		tv.setTextColor(context.getResources().getColor(R.color.gray1));
		tv.setText("    " + ParentItem[groupPosition]);

		return tv;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
