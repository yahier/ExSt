package yahier.exst.act.im;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * IM好友.当前不再用到
 */
public class FriendsDB extends SQLiteOpenHelper {
    final static String db_name = "friend.db";
    final String tableName = "friend";
    final String columnuserId = "userId";// 动态id。被查看或者评论 点赞过的。
    final String columnuserNick = "userNicl";
    final String columnuserImg = "userImg";
    public static FriendsDB mFriendsDB = null;

    public static FriendsDB getInstance(Context context){
        LogUtil.logE("LogUtil","FriendsDB getInstance ......");
        if (context == null) context = MyApplication.getContext();
        if (mFriendsDB == null){
            mFriendsDB = new FriendsDB(context);
        }
        return mFriendsDB;
    }

    public FriendsDB(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnuserId + " long," + columnuserNick + " text," + columnuserImg + " text" + ")";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public synchronized void insert(long userId, String userNick, String userImg) {
        if (isAdded(userId)) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(columnuserId, String.valueOf(userId));
            cv.put(columnuserNick, userNick);
            cv.put(columnuserImg, userImg);
            db.insert(tableName, null, cv);
            LogUtil.logE("FriendsDB","insert完成："+userNick);
        } finally {
            db.close();
        }
    }

//
    public synchronized void insert(UserItem userItem) {
        if (userItem == null) return;
        //insert(userItem.getUserid(), userItem.getNickname(), userItem.getImgurl());
    }


    public synchronized void delete(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(tableName, "userId=?", new String[]{String.valueOf(userId)});
        }finally {
            db.close();
        }
    }


    // 是否添加
    public synchronized boolean isAdded(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.query(tableName, new String[]{columnuserId}, "userId=?", new String[]{String.valueOf(userId)}, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count > 0 ? true : false;
        }finally {
            db.close();
        }
    }


    // 查看一个动态的点赞
	public List<UserItem> query(long statusesId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, "StatusesId=?", new String[] { String.valueOf(statusesId) }, null, null, "_id desc limit 3");
		List<UserItem> list = new ArrayList<UserItem>();
		while (cursor.moveToNext()) {
			UserItem user = new UserItem();
			int index1 = cursor.getColumnIndex(columnuserId);
			int index2 = cursor.getColumnIndex(columnuserNick);
			int index3 = cursor.getColumnIndex(columnuserImg);
			user.setUserid(Long.valueOf(cursor.getString(index1)));
			user.setNickname(cursor.getString(index2));
			user.setImgurl(cursor.getString(index3));
			list.add(user);
		}
		return list;

	}

    public Cursor queryAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.query(tableName, null, null, null, null, null, null);
        }finally {
            db.close();
        }
    }

    public synchronized void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(tableName, null, null);
        }finally {
            db.close();
        }
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(db_name);
    }
}
