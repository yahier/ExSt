package yahier.exst.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 记录错误的数据库
 */
public class ErrorRecodeDB extends SQLiteOpenHelper {
    final static String db_name = "stbl_error.db";
    final String tableName = "error";
    final String columnId = "errorId";// 动态id。被查看或者评论 点赞过的。
    public final static String columnType = "type";
    public final static String columnMsg = "msg";

    public final static int typeIm = 1;

    public ErrorRecodeDB(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnId + " INTEGER," + columnType + " long,"
                + columnMsg + " text" + ")";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /**
     * 加入一条记录.如果已经加入，则返回
     *
     * @param type
     * @param msg
     */
    public void insert(int type, String msg) {
        if (isAdded(type, msg)) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnType, type);
        cv.put(columnMsg, msg);
        db.insert(tableName, null, cv);
        db.close();
    }

    //删除一个点赞
    public void delete(int type, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "type=? and msg=?", new String[]{String.valueOf(type), msg});
    }


    // 是否添加
    public boolean isAdded(int type, String msg) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{columnId}, "type =? and msg=?",
                new String[]{String.valueOf(type), msg}, null, null, null);

        return cursor.getCount() > 0 ? true : false;
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
