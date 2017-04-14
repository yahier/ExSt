package yahier.exst.act.im;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.item.im.IMAccount;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.SharedToken;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 在消息主页，会话页面出现的个人信息，群信息，都被保存在这里，避免每次都从服务器获取。
 */
public class RongDB extends SQLiteOpenHelper {
    final static String db_name = "rong_conversation.db";
    final String tableName = "rong_user";
    public final String id = "_id";
    final String columnTargetId = "targetid";// 哈 这里也可能是讨论组id
    final String columnName = "nameame";
    final String columnUserImg = "userImgUrl";
    final String columnType = "type";
    final String columnTime = "add_time";//添加时间.版本2增加 //0415增加的字段
    final String columnCertification = "certification";//认证。版本3增加 //0603增加的字段
    final String columnAlias = "alias";//别名。版本4增加 //0831增加的字段
    final String columnCurrentUserId = "currentUserId";//当前用户Id。版本5增加 //0902增加的字段

    final String columnPeopleNum = "peopleNum";//当前用户Id。版本6增加 //0909增加的字段
    final String tag = getClass().getSimpleName();
    String currentUserId;

    public final static int typeUser = 1;
    public final static int typeDiscussion = 2;
    public final static int typeGroup = 3;
    final long timeInterval = 3600 * 24 * 3;//暂时用3天做测试。

    public RongDB(Context context) {
        super(context, db_name, null, 6);
        currentUserId = SharedToken.getUserId(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "CREATE TABLE " + tableName + "(_id INTEGER primary key autoincrement," + columnType + " INTEGER," + columnPeopleNum + " INTEGER," + columnTargetId + " long," + columnCurrentUserId + " long," + columnTime + " long," + columnName + " text," + columnAlias + " text," + columnUserImg + " text, "
                + columnCertification + " integer default " + UserItem.certificationNo + ")";
        //LogUtil.logE(tag, "onCreate");
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //LogUtil.logE(tag, "onUpgrade oldVersion:"+oldVersion);
        switch(oldVersion) {
            case 1:
                String sql1 = "alter table " + tableName + " add " + columnTime + " long";
                db.execSQL(sql1);
            case 2:
                String sql2 = "alter table " + tableName + " add " + columnCertification + " integer default " + UserItem.certificationNo;
                db.execSQL(sql2);
            case 3:
                String sql3 = "alter table " + tableName + " add " + columnAlias + " text";
                db.execSQL(sql3);
            case 4:
                String sql4 = "alter table " + tableName + " add " + columnCurrentUserId + " long";
                db.execSQL(sql4);
            case 5:
                String sql5 = "alter table " + tableName + " add " + columnPeopleNum + " INTEGER";
                db.execSQL(sql5);
                break;
        }
    }


    //add new
    public void insert(IMAccount account) {
        if (account.getNickname() == null && account.getImgurl() == null)
            return;
        if (isAdded(account.getType(), account.getUserid())) {
            update(account);
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnTargetId, account.getUserid());
        cv.put(columnType, account.getType());
        cv.put(columnCertification, account.getCertification());
        //在这里加入时间
        cv.put(columnTime, System.currentTimeMillis() / 1000);
        if (account.getNickname() != null && !account.getNickname().equals(""))
            cv.put(columnName, account.getNickname());
        if (account.getImgurl() != null && !account.getImgurl().equals(""))
            cv.put(columnUserImg, account.getImgurl());
        if (account.getAlias() != null && !account.getAlias().equals("")) {
            cv.put(columnAlias, account.getAlias());
        }
        if (account.getPeopleNum() > 0) {
            cv.put(columnPeopleNum, account.getPeopleNum());
        }
        cv.put(columnCurrentUserId, currentUserId);
        db.insert(tableName, null, cv);
        //LogUtil.logE(tag, "insert:" + account.getNickname() + ":" + account.getAlias());
        db.close();

        //刷新融云
        String nameValue = account.getAlias();
        if (nameValue == null || nameValue.equals("")) {
            nameValue = account.getNickname();
        }
        Uri uri = null;
        if (account.getImgurl() != null) {
            uri = Uri.parse(account.getImgurl());
        }
        UserInfo user = new UserInfo(account.getUserid(), nameValue, uri);
        RongIM.getInstance().refreshUserInfoCache(user);
    }


    //add new
    public void update(IMAccount account) {
        //LogUtil.logE(tag, "update:" + account.getType() + ":" + account.getUserid() + ":" + account.getPeopleNum());

        if (account.getNickname() == null && account.getImgurl() == null)
            return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(columnTargetId, account.getUserid());
        cv.put(columnType, account.getType());
        if (account.getNickname() != null && !account.getNickname().equals(""))
            cv.put(columnName, account.getNickname());
        if (account.getImgurl() != null && !account.getImgurl().equals(""))
            cv.put(columnUserImg, account.getImgurl());
        if (account.getAlias() != null && !account.getAlias().equals("")) {
            cv.put(columnAlias, account.getAlias());
        }
        if (account.getPeopleNum() > 0) {
            cv.put(columnPeopleNum, account.getPeopleNum());
        }
        cv.put(columnCurrentUserId, currentUserId);//add new
        cv.put(columnCertification, account.getCertification());
        cv.put(columnTime, System.currentTimeMillis() / 1000);
        int lines = db.update(tableName, cv, "type=? and targetid=?", new String[]{String.valueOf(account.getType()), account.getUserid()});
       // LogUtil.logE(tag, "update lines  " + lines + "  alias:" + account.getAlias());
        db.close();

        //刷新融云
        String nameValue = account.getAlias();
        if (nameValue == null || nameValue.equals("")) {
            nameValue = account.getNickname();
        }
        Uri uri = null;
        if (account.getImgurl() != null) {
            uri = Uri.parse(account.getImgurl());
        }
        UserInfo user = new UserInfo(account.getUserid(), nameValue, uri);
        RongIM.getInstance().refreshUserInfoCache(user);


    }


    //在部落详情中调用。更新或者什么都不做
    public void updateOrNone(IMAccount account) {
        if (isAdded(account.getType(), account.getUserid())) {
            update(account);
            return;
        }
    }

    // 是否添加
    public boolean isAdded(int type, String targetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{id}, "type=? and targetid=? and currentUserId=?", new String[]{String.valueOf(type), String.valueOf(targetId), currentUserId}, null, null, null);
        boolean isAdd = cursor.getCount() > 0 ? true : false;
        cursor.close();
        db.close();
        return isAdd;
    }


    //新改。只获取当前用户的数据
    public IMAccount get(int type, String targetid) {
        String sql = "select * from " + tableName + " where type=" + type + " and targetid = '" + targetid + "' and currentUserId = '" + currentUserId + "' limit 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        // LogUtil.logE("db getCount:", cursor.getCount());
        String name = null;
        String imgUrl = null;
        String alias = null;
        int certification = UserItem.certificationNo;
        int peopleNum = 0;
        try {
            while (cursor.moveToNext()) {
                int index1 = cursor.getColumnIndex(columnName);
                int index2 = cursor.getColumnIndex(columnUserImg);
                int index3 = cursor.getColumnIndex(columnCertification);
                int index4 = cursor.getColumnIndex(columnAlias);
                int index5 = cursor.getColumnIndex(columnPeopleNum);
                name = cursor.getString(index1);
                imgUrl = cursor.getString(index2);
                certification = cursor.getInt(index3);
                alias = cursor.getString(index4);
                peopleNum = cursor.getInt(index5);
                //LogUtil.logE("db get ", name + ":" + alias + " currentUserId:" + currentUserId);
            }
        } catch(Exception e) {
            cursor.close();
            db.close();
            return null;
        }
        cursor.close();//新加
        db.close();
        if (name == null)
            return null;
        IMAccount userInfo;
        if (type == typeUser) {
            userInfo = new IMAccount(type, targetid, name, imgUrl, certification, alias);
        } else {
            userInfo = new IMAccount(targetid, name, imgUrl, UserItem.certificationNo);
            userInfo.setPeopleNum(peopleNum);
            userInfo.setType(type);
        }

        //LogUtil.logE(tag, " get:" + name + ":" + userInfo.getUserid() + ":" + alias);

        return userInfo;

    }

    public void queryAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        long now = System.currentTimeMillis() / 1000;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(columnTime);
            //int indexTargetId = cursor.getColumnIndex(columnTargetId);
            // int indexType= cursor.getColumnIndex(columnType);

            long time = cursor.getLong(index);//index为-1了
            // String targetId = cursor.getString(indexTargetId);
            long timeOff = now - time;
            // LogUtil.logE(tag, time + "___________" + (timeOff));
            if (timeOff > timeInterval) {
                // db.delete(tableName, "type=? and targetid=?", new String[]{String.valueOf(typeUser), String.valueOf(targetId)});
                db.delete(tableName, null, null);
            }
        }
        //新加
        cursor.close();
        db.close();
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

}
