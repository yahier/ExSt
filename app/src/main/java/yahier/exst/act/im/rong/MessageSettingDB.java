package yahier.exst.act.im.rong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 消息设置-免打扰
 */
public class MessageSettingDB extends SQLiteOpenHelper {
    final static String db_name = "messageSetting.db";
    final String tableName = "im_notify";

    final String columnType = "type";//
    final String columnTargetId = "targetId";
    final String columnuState = "state";

    //类型
    public final static int typeGroup = 1;
    public final static int typeDiscussion = 2;
    public final static int typePrivate = 3;
    //免打扰的状态
    public final static int stateOn = 1;
    public final static int stateOff = 2;//免打扰。

    public MessageSettingDB(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnType + " integer," + columnTargetId + " long," + columnuState + " integer" + ")";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public synchronized void insert(int type, String targetId, int state) {
        if (isAdded(type, targetId)) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnType, type);
        cv.put(columnTargetId, targetId);
        cv.put(columnuState, state);
        db.insert(tableName, null, cv);
        db.close();
    }


    public void update(int type, String targetId, int state) {
        //LogUtil.logE(db_name, "开始更新");
        //if (name == null && imgUrl == null) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnType, type);
        cv.put(columnTargetId, targetId);
        cv.put(columnuState, state);
        int numberCount = db.update(tableName, cv, "type=? and targetId=?", new String[]{String.valueOf(type), String.valueOf(targetId)});
        //LogUtil.logE(db_name, "开始完成 state：" + state + " targetId:" + targetId+" numberCount:"+numberCount);
        db.close();
    }

    //是否免打扰
    public boolean isNotNotify(int type, String targetid) {
        String sql = "select * from " + tableName + " where type=" + type + " and targetId = '" + targetid + "' limit 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int state = 0;
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(columnuState);
            state = cursor.getInt(index1);
        }
       // LogUtil.logE(db_name, "isNotNotify state：" + state);
        cursor.close();
        db.close();
        if (state == 0) {
           // LogUtil.logE(db_name, "state is 0");
            insert(type, targetid, stateOn);
        }
        if (state == stateOff) {
            return true;
        }
        return false;

    }


    public synchronized void delete(int type, long targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "type = ? and targetId=? ", new String[]{String.valueOf(type), String.valueOf(targetId)});
        db.close();
    }


    // 是否添加
    public synchronized boolean isAdded(int type, String targetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{columnTargetId}, "type = ? and targetId=? ", new String[]{String.valueOf(type), String.valueOf(targetId)}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0 ? true : false;
    }

    public Cursor queryAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(tableName, null, null, null, null, null, null);
    }

    public synchronized void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(db_name);
    }
}
