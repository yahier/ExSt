package yahier.exst.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.util.DimenUtils;

public class DropDownMenu extends PopupWindow implements OnClickListener {

	private OnMenuClickListener mListener;

	private DropDownMenu() {
		super();
	}

	public static DropDownMenu newInstance(
			ArrayList<DropDownMenuItem> itemList, OnMenuClickListener listener) {
		DropDownMenu dropDownMenu = new DropDownMenu();
		dropDownMenu.mListener = listener;

		final Context context = MyApplication.getContext();
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		LinearLayout contentView = (LinearLayout) layoutInflater.inflate(
				R.layout.drop_down_menu, null);

		LinearLayout containerLayout = (LinearLayout) contentView
				.findViewById(R.id.drop_down_menu_container_layout);

		for (int i = 0; i < itemList.size(); i++) {
			DropDownMenuItem item = itemList.get(i);
			TextView tv = (TextView) layoutInflater.inflate(
					R.layout.drop_down_menu_item, null);
			tv.setText(item.mText);
			tv.setTag(i);
			tv.setOnClickListener(dropDownMenu);
			int tvHeight = DimenUtils.dp2px(48);
			containerLayout.addView(tv, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, tvHeight));
			if (i < itemList.size() - 1) {
				View divider = new View(context);
				divider.setBackgroundColor(0xffe7e7e7);
				int dividerHeight = DimenUtils.dp2px(0.5f);
				int dividerMargin = DimenUtils.dp2px(12);
				LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, dividerHeight);
				dividerParams.setMargins(dividerMargin, 0, dividerMargin, 0);
				containerLayout.addView(divider, dividerParams);
			}
		}
		dropDownMenu.setContentView(contentView);
		dropDownMenu.setWidth(FrameLayout.LayoutParams.WRAP_CONTENT);
		dropDownMenu.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
		dropDownMenu.setFocusable(true);
		dropDownMenu.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
		dropDownMenu.setOutsideTouchable(true);
		dropDownMenu.setAnimationStyle(R.style.AnimationMessage);

		return dropDownMenu;
	}

	public void show(View anchorView) {
		if (!isShowing()) {
			showAsDropDown(anchorView, 0, 0);
		} else {
			dismiss();
		}
	}

	public static class DropDownMenuItem {

		public String mText;

		public DropDownMenuItem(String text) {
			mText = text;
		}
	}

	public interface OnMenuClickListener {
		void onMenuClick(View view, int position);
	}

	@Override
	public void onClick(View view) {
		dismiss();
		int position = (Integer) view.getTag();
		if (mListener != null) {
			mListener.onMenuClick(view, position);
		}
	}

}
