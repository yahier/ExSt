package yahier.exst.util;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.item.StatusesPraise;
import com.stbl.stbl.item.UserItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 点赞记录
 * 
 */
public class DongtaiPraiseDB extends SQLiteOpenHelper {
	final static String db_name = "dongtai_praise.db";
	final String tableName = "dongtai_praise";
	final String columnStatusesId = "StatusesId";// 动态id。被查看或者评论 点赞过的。
	final String columnUserId = "userId";
	final String columnUserImg = "userImg";

	public DongtaiPraiseDB(Context context) {
		super(context, db_name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnUserId + " long," + columnStatusesId + " long,"
				+ columnUserImg + " text" + ")";
		db.execSQL(s);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	// 添加一个点赞
	public void insert(long userId, String userImg, long statusesId) {
		if (isAdded(String.valueOf(userId), statusesId)) {
			return;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(columnUserId, userId);
		cv.put(columnUserImg, userImg);
		cv.put(columnStatusesId, statusesId);
		db.insert(tableName, null, cv);
		db.close();
	}
	
	//删除一个点赞
	public void deletePraise(long statusesId, String userId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, "userId=? and StatusesId=?", new String[] { String.valueOf(userId), String.valueOf(statusesId) });
	}
	
	
	// 是否添加
	public boolean isAdded(String userId, long statusesId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, new String[] { columnUserId }, "userId=? and StatusesId=?",
				new String[] { String.valueOf(userId), String.valueOf(statusesId) }, null, null, null);

		return cursor.getCount() > 0 ? true : false;
	}

	// 查看一个动态的点赞
	public List<UserItem> query(long statusesId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, "StatusesId=?", new String[] { String.valueOf(statusesId) }, null, null, "_id desc limit 3");
		List<UserItem> list = new ArrayList<UserItem>();
		while (cursor.moveToNext()) {
			UserItem user = new UserItem();
			int index1 = cursor.getColumnIndex(columnUserId);
			int index2 = cursor.getColumnIndex(columnUserImg);
			user.setUserid(Long.valueOf(cursor.getString(index1)));
			user.setImgurl(cursor.getString(index2));
			list.add(user);
		}
		return list;

	}

	public Cursor queryAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(tableName, null, null, null, null, null, null);
	}

	public void deleteAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, null, null);
		db.close();
	}



	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(db_name);
	}
}
