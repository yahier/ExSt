package yahier.exst.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DongtaiSearchRecordDB extends SQLiteOpenHelper {
	final static String db_name = "dongtai.db";
	final String tableName = "dongtai_search_hostory";
	public final static String word = "searchWord";

	public DongtaiSearchRecordDB(Context context) {
		super(context, db_name, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + word + " text" + ")";
		// System.out.println("s is " + s);
		db.execSQL(s);
		// System.out.println("onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void insert(String searchCon) {
		if (isAdded(searchCon)) {
			delete(searchCon);
		}
		if (query().getCount() >= 15) {
			// 删除第一个
			deleteHead();
		}
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(word, searchCon);
		db.insert(tableName, null, cv);
		db.close();
	}

	public boolean isAdded(String searchWord) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, new String[] { word }, "searchWord=?", new String[] { String.valueOf(searchWord) }, null, null, null);
		return cursor.getCount() > 0 ? true : false;
	}

	public Cursor query() {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(tableName, null, null, null, null, null, "_id desc");
	}

	public void delete(String con) {
		SQLiteDatabase dbWrite = this.getWritableDatabase();
		String sql = "delete from " + tableName + " where searchWord = '" + con+"'";
		dbWrite.execSQL(sql);

	}
	
	// 删除顶部
	public void deleteHead() {
		SQLiteDatabase dbWrite = this.getWritableDatabase();
		String sql = "delete from " + tableName + " where _id = " + " (select min(_id) from " + tableName+")";
		dbWrite.execSQL(sql);

	}

	public void deleteAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, null, null);
		db.close();
	}
}
