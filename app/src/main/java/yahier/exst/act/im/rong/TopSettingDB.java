package yahier.exst.act.im.rong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stbl.stbl.item.IMSettingStatus;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 置顶设置.在tabhome里访问到列表数据时m,会清除本数据库的数据。
 */
public class TopSettingDB extends SQLiteOpenHelper {
    final static String db_name = "messageTopSetting.db";
    final String tableName = "im_notify";

    final String columnType = "type";//
    final String columnTargetId = "targetId";//表示群组Id,讨论组Id,对方的UserId中的其中一个。
    final String columnuState = "state";
    public final static int stateTopYes = 1;
    public final static int stateTopNo = 0;

    final String columnUserId = "userId";//版本2新加的字段。留给以后备用
    String currentUserId;

    public TopSettingDB(Context context) {
        super(context, db_name, null, 2);
        currentUserId = SharedToken.getUserId(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnType + " integer," + columnTargetId + " long," + columnUserId + " long," + columnuState + " integer" + ")";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                String sql1 = "alter table " + tableName + " add " + columnUserId + " long";
                db.execSQL(sql1);
                break;
        }

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
        cv.put(columnUserId, currentUserId);
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
        cv.put(columnUserId, currentUserId);
        int numberCount = db.update(tableName, cv, "type=? and targetId=? and userId=?", new String[]{String.valueOf(type), String.valueOf(targetId), currentUserId});
        db.close();
    }


    public synchronized void delete(int type, String targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "type = ? and targetId=? and userId=?", new String[]{String.valueOf(type), targetId, currentUserId});
        db.close();
    }


    // 是否添加
    public synchronized boolean isAdded(int type, String targetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{columnTargetId}, "type = ? and targetId=? and userId=?", new String[]{String.valueOf(type), String.valueOf(targetId), currentUserId}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0 ? true : false;
    }


    //获取置顶列表
    public List<IMSettingStatus> getList() {
        String sql = "select * from " + tableName + " where userId = '" + currentUserId+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        // LogUtil.logE("db getCount:", cursor.getCount());

        List<IMSettingStatus> list = new ArrayList<IMSettingStatus>();
        long targetId;
        int type;
       // try {
            while (cursor.moveToNext()) {
                int index1 = cursor.getColumnIndex(columnType);
                int index2 = cursor.getColumnIndex(columnTargetId);
                type = Integer.parseInt(cursor.getString(index1));
                targetId = Long.valueOf(cursor.getString(index2));

                IMSettingStatus setting = new IMSettingStatus();
                setting.setBusinesstype(type);
                setting.setBusinessid(targetId);
                list.add(setting);

            }
//        } catch(Exception e) {
//            cursor.close();
//            db.close();
//            return null;
//        }
        cursor.close();//新加
        db.close();


        return list;

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
