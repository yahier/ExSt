package yahier.exst.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.item.StatusesComment;
import com.stbl.stbl.item.StatusesRemarkDBItem;
import com.stbl.stbl.item.UserItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论记录.先不考虑替换
 *
 * @author lenovo
 */
public class DongtaiRemarkDB extends SQLiteOpenHelper {
    final static String db_name = "dongtai_remark.db";
    final String tableName = "dongtai_remark";
    public final String id = "_id";
    public final String columnCurrentUserId = "curentUserId";
    public final String columnStatusesId = "StatusesId";// 动态id。被查看或者评论 点赞过的。
    public final String columnCommentId = "commentId";// 评论id
    public final String column1UserId = "user1Id";
    public final String colum1Name = "user1Name";
    public final String column2UserId = "user2Id";//被回复的人
    public final String colum2Name = "user2Name";//被回复的人
    public final String columnRemark = "remarkContent";
    public final String columnRemarkTime = "remarkTime";
    public final String columnRemarkSeconds = "RemarkSecondTimes";// 秒数

    public final long reseredTime = 3600 * 24 * 30;//7天 = 3600*24*7

    public DongtaiRemarkDB(Context context) {
        super(context, db_name, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(" + id + " INTEGER primary key autoincrement," + columnCurrentUserId + " long," + column1UserId + " long," + colum1Name + " text," + column2UserId + " long," + colum2Name + " text," + columnCommentId + " long," + columnStatusesId + " long," + columnRemark + " text," + columnRemarkTime + " text," + columnRemarkSeconds + " text"
                + ")";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                String sql2 = "alter table " + tableName + " add " + columnCurrentUserId + " long";
                db.execSQL(sql2);
                break;

        }

    }

//    // 添加。
//    public void insert(long statusesId, long commentId, String userId, String userName, String remarkContent, long remarkTime) {
//        if (isAddComment(commentId)) {
//            return;
//        }
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues cv = new ContentValues();
////        cv.put(columnStatusesId, statusesId);
////        cv.put(columnCommentId, commentId);
////        cv.put(columnUserId, userId);
////        cv.put(columName, userName);
////        cv.put(columnRemark, remarkContent);
////        cv.put(columnRemarkTime, DateUtil.getHm(String.valueOf(remarkTime)));
////        cv.put(columnRemarkSeconds, remarkTime);
//        db.insert(tableName, null, cv);
//        db.close();
//    }


    //删除过期的评论
    public void deleteExpiredRemark() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{columnCommentId, columnRemarkSeconds}, null, null, null, null, null);
        long now = System.currentTimeMillis() / 1000;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(columnRemarkSeconds);
            int indexComment = cursor.getColumnIndex(columnCommentId);
            long time = cursor.getLong(index);
            long timeOff = now - time;
            long commentId = Long.valueOf(cursor.getString(indexComment));
            //LogUtil.logE("timeOff:" + timeOff);
            if (timeOff > reseredTime) {
                deleteComment(commentId);
            }

        }

        cursor.close();
        db.close();


    }


    //最多插入四条数据
    public void insertToDB(List<StatusesComment> result) {
        int size = result.size();
        SQLiteDatabase db = this.getWritableDatabase();
        int startIndex = size - (Config.statusesRemarkDBCount + 1);
        if (startIndex >= 0) {
            startIndex = Config.statusesRemarkDBCount ;
        } else {
            startIndex = size - 1;
        }
        for (int i = startIndex; i >= 0 && i < size; i--) {
            StatusesComment comment = result.get(i);
            StatusesRemarkDBItem itemDB = new StatusesRemarkDBItem();
            itemDB.setCommentId(comment.getCommentid());
            itemDB.setRemarkContent(comment.getContent());
            //LogUtil.logE("content:" + comment.getContent());
            //itemDB.setRemarkTime(comment.getCreatetime());
            itemDB.setStatusesId(comment.getStatusesid());
            itemDB.setUser1Id(comment.getUser().getUserid());
            itemDB.setUser1Name(comment.getUser().getNickname());

            UserItem lastuser = comment.getLastuser();
            if (lastuser != null) {
                itemDB.setUser2Id(lastuser.getUserid());
                itemDB.setUser2Name(lastuser.getNickname());
            }
            insert(itemDB);
        }
        db.close();
    }

    public void insert(StatusesRemarkDBItem item) {
        if (isAddComment(item.getCommentId())) {
            return;
        }
        String currentUserId = SharedToken.getUserId(MyApplication.getContext());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnCurrentUserId, currentUserId);
        cv.put(columnStatusesId, item.getStatusesId());
        cv.put(columnCommentId, item.getCommentId());
        cv.put(column1UserId, item.getUser1Id());
        cv.put(colum1Name, item.getUser1Name());
        cv.put(column2UserId, item.getUser2Id());
        cv.put(colum2Name, item.getUser2Name());

        //LogUtil.logE("insert:name2:" + item.getUser2Name());
        cv.put(columnRemark, item.getRemarkContent());
        cv.put(columnRemarkTime, DateUtil.getHm(String.valueOf(item.getRemarkTime())));
        long remarkTime = System.currentTimeMillis() / 1000;
        cv.put(columnRemarkSeconds, remarkTime);
        db.insert(tableName, null, cv);
        db.close();
    }

    // 删除一条评论
    public void deleteComment(long commentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "commentId=?", new String[]{String.valueOf(commentId)});
        db.close();
    }

    // 是否已经添加本条记录
    public boolean isAddComment(long commentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentUserId = SharedToken.getUserId(MyApplication.getContext());
        Cursor cursor = db.query(tableName, new String[]{columnCommentId}, "commentId=? and curentUserId=?", new String[]{String.valueOf(commentId), currentUserId}, null, null, null);
        boolean result = cursor.getCount() > 0 ? true : false;
        cursor.close();
        db.close();
        return result;
    }


    public List<StatusesComment> query(long statusesId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentUserId = SharedToken.getUserId(MyApplication.getContext());
        Cursor cursor = db.query(tableName, null, "StatusesId=? and curentUserId=? ", new String[]{String.valueOf(statusesId), currentUserId}, null, null, id + " desc limit 4");
        List<StatusesComment> list = new ArrayList<StatusesComment>();
        while (cursor.moveToNext()) {
            StatusesComment comment = new StatusesComment();
            UserItem user = new UserItem();
            int index1 = cursor.getColumnIndex(column1UserId);
            int index2 = cursor.getColumnIndex(colum1Name);
            int index3 = cursor.getColumnIndex(columnRemark);
            int index4 = cursor.getColumnIndex(column2UserId);
            int index5 = cursor.getColumnIndex(colum2Name);
            int index6 = cursor.getColumnIndex(columnStatusesId);
            int index7 = cursor.getColumnIndex(columnCommentId);

            user.setUserid(Long.valueOf(cursor.getString(index1)));
            user.setNickname(cursor.getString(index2));
            String user2Id = cursor.getString(index4);
            String user2Name = cursor.getString(index5);
            comment.setUser(user);
            comment.setStatusesid(Long.valueOf(cursor.getString(index6)));
            comment.setContent(cursor.getString(index3));
            comment.setCommentid(Integer.valueOf(cursor.getString(index7)));
            if (user2Id != null && !user2Id.equals("0")) {
                UserItem userLast = new UserItem();
                userLast.setUserid(Long.valueOf(user2Id));
                userLast.setNickname(user2Name);
                comment.setLastuser(userLast);


            }
            //LogUtil.logE("db",statusesId+":"+comment.getContent());
            list.add(comment);

        }
        cursor.close();
        db.close();
        return list;

    }

    public boolean isAdded(long statusesId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String currentUserId = SharedToken.getUserId(MyApplication.getContext());
        Cursor cursor = db.query(tableName, new String[]{id}, "StatusesId=? and curentUserId=? ", new String[]{String.valueOf(statusesId), currentUserId}, null, null, null);
        boolean result = cursor.getCount() > 0 ? true : false;
        cursor.close();
        db.close();
        return result;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

}
