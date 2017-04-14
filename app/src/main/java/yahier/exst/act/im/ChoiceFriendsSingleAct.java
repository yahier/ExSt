package yahier.exst.act.im;//package com.stbl.stbl.act.im;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.ExpandableListView;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.stbl.stbl.R;
//import com.stbl.stbl.act.im.ApplyAddListAct.AppAdapter.GroupViewHolder;
//import com.stbl.stbl.act.im.ApplyAddListAct.AppAdapter.ViewHolder;
//import com.stbl.stbl.common.CommonAdapter;
//import com.stbl.stbl.item.BaseItem;
//import com.stbl.stbl.item.UserItem;
//import com.stbl.stbl.item.im.ApplyGotGroup;
//import com.stbl.stbl.item.im.GroupMemberList;
//import com.stbl.stbl.item.im.UserList;
//import com.stbl.stbl.util.FinalHttpCallback;
//import com.stbl.stbl.util.HttpEntity;
//import com.stbl.stbl.util.JSONHelper;
//import com.stbl.stbl.util.LogUtil;
//import com.stbl.stbl.util.Method;
//import com.stbl.stbl.util.Params;
//import com.stbl.stbl.util.PicassoUtil;
//import com.stbl.stbl.util.ToastUtil;
//import com.stbl.stbl.widget.expandablelistview.BaseSwipeMenuExpandableListAdapter;
//import com.stbl.stbl.widget.expandablelistview.ContentViewWrapper;
//
///**
// * 弃用.请使用SingleSelectFriendActivity
// *
// * @author lenovo
// *
// */
//public class ChoiceFriendsSingleAct extends Activity implements FinalHttpCallback, OnClickListener {
//	ExpandableListView listView;
//	List<GroupMemberList> list;
//	AppAdapter adapter;
//	// 选择模式
//	public static final int choiceModeSingle = 1;
//	public static final int choiceModeMult = 2;
//	int choiceMode = choiceModeSingle;// 默认是多选模式
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.choice_friends);
//		ToastUtil.showToast(this, "单选");
//		listView = (ExpandableListView) findViewById(R.id.list);
//		findViewById(R.id.theme_top_banner_left).setOnClickListener(this);
//		findViewById(R.id.theme_top_banner_right).setOnClickListener(this);
//		// choiceMode = getIntent().getIntExtra("choiceMode", choiceModeMult);
//		LogUtil.logE("choiceMode:" + choiceMode);
//		getData();
//	}
//
//	// 临时改变属性值
//	void getData() {
//		Params params = new Params();
//		params.put("grouptype", GroupMemberList.typeRequestCharacter);// 0-按用户关系分组，1-按用户昵称首字母分组
//		new HttpEntity(this).commonPostData(Method.getAppContacts, params, this);
//	}
//
//	@Override
//	public void onClick(View view) {
//
//		switch (view.getId()) {
//		case R.id.theme_top_banner_left:
//			finish();
//			break;
//		case R.id.theme_top_banner_right:
//			if (adapter == null) {
//				finish();
//				return;
//			}
//			UserList users = new UserList();
//			List<UserItem> list = adapter.getCheckedUsers();
//			users.setList(list);
//			Intent intent = new Intent();
//			intent.putExtra("users", users);
//			setResult(RESULT_OK, intent);
//			finish();
//			break;
//		}
//	}
//
//	// BaseSwipeMenu
//	class AppAdapter extends BaseExpandableListAdapter {
//		private Context mContext;
//		List<GroupMemberList> list;
//		ArrayList<ArrayList<Boolean>> listCheck = new ArrayList<ArrayList<Boolean>>();
//
//		public AppAdapter(Context mContext, List<GroupMemberList> list) {
//			this.mContext = mContext;
//			this.list = list;
//			// 添加选中的默认值 false
//			for (int i = 0; i < list.size(); i++) {
//				ArrayList<Boolean> childList = new ArrayList<Boolean>();
//				GroupMemberList group = list.get(i);
//				for (int j = 0; j < group.getGroupmembers().size(); j++) {
//					childList.add(false);
//				}
//				listCheck.add(childList);
//			}
//		}
//
//		/**
//		 * 获取选中的user列表
//		 */
//		public List<UserItem> getCheckedUsers() {
//			List<UserItem> userList = new ArrayList<UserItem>();
//			for (int i = 0; i < listCheck.size(); i++) {
//				List<UserItem> listUser = list.get(i).getGroupmembers();
//				ArrayList<Boolean> listChildCheck = listCheck.get(i);
//				for (int j = 0; j < listChildCheck.size(); j++) {
//					if (listChildCheck.get(j)) {
//						userList.add(listUser.get(j));
//					}
//				}
//
//			}
//			return userList;
//		}
//
//		/**
//		 * Whether this group item swipable
//		 *
//		 * @param groupPosition
//		 * @return
//		 * @see com.stbl.stbl.widget.expandablelistview.BaseSwipeMenuExpandableListAdapter#isGroupSwipable(int)
//		 */
//		// @Override
//		// public boolean isGroupSwipable(int groupPosition) {
//		// return false;
//		// }
//
//		/**
//		 * 事实证明这个判断非常的不靠谱。所以我封掉了if的判断 Whether this child item swipable
//		 *
//		 * @param groupPosition
//		 * @param childPosition
//		 * @return
//		 * @see com.stbl.stbl.widget.expandablelistview.BaseSwipeMenuExpandableListAdapter#isChildSwipable(int,
//		 *      int)
//		 */
//		// @Override
//		// public boolean isChildSwipable(int groupPosition, int childPosition)
//		// {
//		// // if (childPosition == 0)
//		// // return false;
//		// return true;
//		// }
//
//		// @Override
//		// public int getChildType(int groupPosition, int childPosition) {
//		// return childPosition % 3;
//		// // return
//		// //
//		// list.get(groupPosition).getGroupmembers().get(childPosition).getRelationflag();
//		// }
//
//		// @Override
//		// public int getChildTypeCount() {
//		// return 3;
//		// }
//
//		// @Override
//		// public int getGroupType(int groupPosition) {
//		// return groupPosition % 3;
//		// }
//		//
//		// @Override
//		// public int getGroupTypeCount() {
//		// return 3;
//		// }
//
//		class ViewHolder {
//			ImageView imgUser;
//			TextView tvName;
//			TextView tvStatus;
//			CheckBox check;
//
//			public ViewHolder(View view) {
//				imgUser = (ImageView) view.findViewById(R.id.imgUser);
//				tvName = (TextView) view.findViewById(R.id.tvName);
//				tvStatus = (TextView) view.findViewById(R.id.tvStatus);
//				check = (CheckBox) view.findViewById(R.id.check);
//				view.setTag(this);
//			}
//		}
//
//		class GroupViewHolder {
//			ImageView ivArrow;
//			TextView tv_name;
//
//			public GroupViewHolder(View view) {
//				ivArrow = (ImageView) view.findViewById(R.id.ivArrow);
//				tv_name = (TextView) view.findViewById(R.id.tvName);
//
//				view.setTag(this);
//			}
//		}
//
//		public void setAllUnChecked(int groupIndex, int childIndex, boolean checkState) {
//			for (int i = 0; i < listCheck.size(); i++) {
//				ArrayList<Boolean> childList = listCheck.get(i);
//				for (int j = 0; j < childList.size(); j++) {
//					childList.set(j, false);
//				}
//				listCheck.set(i, childList);
//			}
//			ArrayList<Boolean> childCheckeds = listCheck.get(groupIndex);
//			childCheckeds.set(childIndex, checkState);
//			listCheck.set(groupIndex, childCheckeds);
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public int getGroupCount() {
//			return list.size();
//		}
//
//		@Override
//		public int getChildrenCount(int groupPosition) {
//			return list.get(groupPosition).getGroupmembers().size();
//		}
//
//		@Override
//		public Object getGroup(int groupPosition) {
//			return null;// mAppList.get(groupPosition);
//		}
//
//		@Override
//		public Object getChild(int groupPosition, int childPosition) {
//			return "The " + childPosition + "'th child in " + groupPosition + "'th group.";
//		}
//
//		@Override
//		public long getGroupId(int groupPosition) {
//			return groupPosition;
//		}
//
//		@Override
//		public long getChildId(int groupPosition, int childPosition) {
//			return childPosition;
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			return false;
//		}
//
//		// 没有被调用
//		public ContentViewWrapper getGroupViewAndReUsable(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//			boolean reUseable = true;
//			if (convertView == null) {
//				convertView = View.inflate(getApplicationContext(), R.layout.contact_list_group_item, null);
//				convertView.setTag(new GroupViewHolder(convertView));
//				reUseable = false;
//			}
//			GroupViewHolder holder = (GroupViewHolder) convertView.getTag();
//			holder.tv_name.setText(list.get(groupPosition).getGroupname());
//			if (isExpanded) {
//				holder.ivArrow.setImageResource(R.drawable.icon_arrow_up);
//			} else {
//				holder.ivArrow.setImageResource(R.drawable.icon_arrow_down);
//			}
//			return new ContentViewWrapper(convertView, reUseable);
//		}
//
//		// //没有被调用
//		// public ContentViewWrapper getChildViewAndReUsable(final int
//		// groupPosition, final int childPosition, boolean isLastChild, View
//		// convertView, ViewGroup parent) {
//		// boolean reUseable = true;
//		// if (convertView == null) {
//		// convertView = View.inflate(getApplicationContext(),
//		// R.layout.choice_friends_list_item, null);
//		// convertView.setTag(new ViewHolder(convertView));
//		// reUseable = false;
//		// }
//		// ViewHolder holder = (ViewHolder) convertView.getTag();
//		// if (null == holder) {
//		// holder = new ViewHolder(convertView);
//		// }
//		//
//		// UserItem user =
//		// list.get(groupPosition).getGroupmembers().get(childPosition);
//		// PicassoUtil.load(mContext, user.getImgmiddleurl(), holder.imgUser);
//		// holder.tvName.setText(user.getNickname());
//		// //
//		// //
//		// if (listCheck.get(groupPosition).get(childPosition)) {
//		// holder.check.setChecked(true);
//		// } else {
//		// holder.check.setChecked(false);
//		// }
//		//
//		// LogUtil.logE("getChildViewAndReUsable "+groupPosition+" "+childPosition);
//		//
//		// holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener()
//		// {
//		//
//		// @Override
//		// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//		// LogUtil.logE("childPosition:"+childPosition+" arg1:"+arg1);
//		// // 如果是选中，就取消所有其它
//		// if (arg1 && choiceMode == choiceModeSingle) {
//		// //setAllUnChecked(groupPosition,childPosition);
//		// }
//		// ArrayList<Boolean> listChild = listCheck.get(groupPosition);
//		// listChild.set(childPosition, arg1);
//		// listCheck.set(groupPosition, listChild);
//		//
//		// notifyDataSetChanged();
//		// //如果之前选中了1，现在选中了2
//		// }
//		// });
//		// return new ContentViewWrapper(convertView, reUseable);
//		// }
//
//		@Override
//		public boolean isChildSelectable(int groupPosition, int childPosition) {
//			return true;
//		}
//
//		@Override
//		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//			boolean reUseable = true;
//			if (convertView == null) {
//				convertView = View.inflate(getApplicationContext(), R.layout.choice_friends_list_item, null);
//				convertView.setTag(new ViewHolder(convertView));
//				reUseable = false;
//			}
//			ViewHolder holder = (ViewHolder) convertView.getTag();
//			if (null == holder) {
//				holder = new ViewHolder(convertView);
//			}
//
//			UserItem user = list.get(groupPosition).getGroupmembers().get(childPosition);
//			PicassoUtil.load(mContext, user.getImgmiddleurl(), holder.imgUser);
//			holder.tvName.setText(user.getNickname());
//			//
//			//
//			if (listCheck.get(groupPosition).get(childPosition)) {
//				holder.check.setChecked(true);
//			} else {
//				holder.check.setChecked(false);
//			}
//
//			// LogUtil.logE("getChildViewAndReUsable "+groupPosition+" "+childPosition);
//
//			holder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton arg0, boolean checkState) {
//					LogUtil.logE("childPosition:" + childPosition + " arg1:" + checkState);
//					// 如果是选中，就取消所有其它
//					// if (arg1 && choiceMode == choiceModeSingle) {
//					if (checkState)
//						setAllUnChecked(groupPosition, childPosition, checkState);
//					// }
//
//					// ArrayList<Boolean> listChild =
//					// listCheck.get(groupPosition);
//					// listChild.set(childPosition, checkState);
//					// listCheck.set(groupPosition, listChild);
//					// notifyDataSetChanged();
//					// 如果之前选中了1，现在选中了2
//				}
//			});
//			return convertView;
//		}
//
//		@Override
//		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//			boolean reUseable = true;
//			if (convertView == null) {
//				convertView = View.inflate(getApplicationContext(), R.layout.contact_list_group_item, null);
//				convertView.setTag(new GroupViewHolder(convertView));
//				reUseable = false;
//			}
//			GroupViewHolder holder = (GroupViewHolder) convertView.getTag();
//			holder.tv_name.setText(list.get(groupPosition).getGroupname());
//			if (isExpanded) {
//				holder.ivArrow.setImageResource(R.drawable.icon_arrow_up);
//			} else {
//				holder.ivArrow.setImageResource(R.drawable.icon_arrow_down);
//			}
//			return convertView;
//		}
//	}
//
//	@Override
//	public void parse(String methodName, String result) {
//		BaseItem item = JSONHelper.getObject(result, BaseItem.class);
//		if (item.getIssuccess() != BaseItem.successTag) {
//			ToastUtil.showToast(this, item.getErr().getMsg());
//			return;
//		}
//		String obj = JSONHelper.getStringFromObject(item.getResult());
//		switch (methodName) {
//		case Method.getAppContacts:
//			list = JSONHelper.getList(obj, GroupMemberList.class);
//			if (list == null || list.size() == 0) {
//				ToastUtil.showToast(this, "还没有好友");
//				return;
//			}
//
//			adapter = new AppAdapter(this, list);
//			listView.setAdapter(adapter);
//			for (int i = 0; i < list.size(); i++) {
//				listView.expandGroup(i);
//			}
//			break;
//
//		}
//
//	}
//}
