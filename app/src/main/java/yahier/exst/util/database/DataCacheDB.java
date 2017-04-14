package yahier.exst.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stbl.stbl.task.CommonTask;

/**
 * 用于缓存json数据
 * Created by Administrator on 2016/5/18 0018.
 */
public class DataCacheDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "all_data_cache.db";
    private final String TABLENAME = "data_cache";
    /**
     * 表字段
     */
    private final String TYPE = "type";
    /**
     * 表字段
     */
    private final String CACHE_JSON = "cache_json";
    /**
     * 商场首页的type
     */
    private final String TYPE_MALL = "type_mall_homepage";
    /**
     * 首页的type
     */
    private final String TYPE_HOMEPAGE = "type_homepage";
    /**
     * 动态关注列表的type
     */
    private final String TYPE_DYNAMIC = "type_dynamic_attend";
    /**
     * 动态广场列表的type
     */
    private final String TYPE_DYNAMIC_SQUARE = "type_dynamic_square";
    /**
     * 动态广场列表的type
     */
    private final String TYPE_DYNAMIC_SHOPPING_CIRCLE = "TYPE_DYNAMIC_SHOPPING_CIRCLE";
    /**
     * 活跃度排行的type
     */
    private final String TYPE_RANK1 = "type_rank1";
    /**
     * 收益排行的type
     */
    private final String TYPE_RANK2 = "type_rank2";
    /**
     * 土豪排行的type
     */
    private final String TYPE_RANK3 = "type_rank3";
    /**
     * 人脉排行的type
     */
    private final String TYPE_RANK4 = "type_rank4";
    /**
     * 帮群排行的type
     */
    private final String TYPE_RANK5 = "type_rank5";
    /**
     * 消息首页 师傅和我的帮群的type
     */
    private final String TYPE_IM_GROUPS = "type_im_groups";

    /**
     * 城市数据
     */
    private final String TYPE_CITY_TREE = "type_city_tree";

    /**
     * 字典数据
     */
    private final String TYPE_BIG_DICT = "type_big_dict";

    private static final String TYPE_HOME_BIG_CHIEF = "type_home_big_chief";
    private static final String TYPE_HOME_AD = "type_home_ad";

    public DataCacheDB(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String s = "CREATE TABLE " + TABLENAME + "(_id INTEGER primary key autoincrement," + TYPE + " text," + CACHE_JSON + " text)";
        // System.out.println("s is " + s);
        db.execSQL(s);
        // System.out.println("onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /**
     * 获取帮群排行的缓存json
     */
    public String getRank5CacheJson() {
        return getCacheJson(TYPE_RANK5);
    }

    /**
     * 保存帮群排行的缓存json
     */
    public void saveRank5CacheJson(String json) {
        insert(json, TYPE_RANK5);
    }

    /**
     * 获取人脉排行的缓存json
     */
    public String getRank4CacheJson() {
        return getCacheJson(TYPE_RANK4);
    }

    /**
     * 保存人脉排行的缓存json
     */
    public void saveRank4CacheJson(String json) {
        insert(json, TYPE_RANK4);
    }

    /**
     * 获取土豪排行的缓存json
     */
    public String getRank3CacheJson() {
        return getCacheJson(TYPE_RANK3);
    }

    /**
     * 保存土豪排行的缓存json
     */
    public void saveRank3CacheJson(String json) {
        insert(json, TYPE_RANK3);
    }

    /**
     * 获取收益排行的缓存json
     */
    public String getRank2CacheJson() {
        return getCacheJson(TYPE_RANK2);
    }

    /**
     * 保存收益排行的缓存json
     */
    public void saveRank2CacheJson(String json) {
        insert(json, TYPE_RANK2);
    }

    /**
     * 获取活跃度排行的缓存json
     */
    public String getRank1CacheJson() {
        return getCacheJson(TYPE_RANK1);
    }

    /**
     * 保存活跃度排行的缓存json
     */
    public void saveRank1CacheJson(String json) {
        insert(json, TYPE_RANK1);
    }

    /**
     * 获取动态的缓存json
     */
    public String getDynamicCacheJson() {
        return getCacheJson(TYPE_DYNAMIC);
    }

    /**
     * 保存动态的缓存json
     */
    public void saveDynamicCacheJson(String json) {
        insert(json, TYPE_DYNAMIC);
    }


    /**
     * 获取商圈列表的缓存json
     */
    public String getShoppingCirclrCacheJson() {
        return getCacheJson(TYPE_DYNAMIC_SHOPPING_CIRCLE);
    }

    /**
     * 保存商圈的缓存json
     */
    public void saveShoppingCircleCacheJson(String json) {
        insert(json, TYPE_DYNAMIC_SHOPPING_CIRCLE);
    }


    /**
     * 获取动态广场的缓存json
     */
    public String getDynamicSquareCacheJson() {
        return getCacheJson(TYPE_DYNAMIC_SQUARE);
    }

    /**
     * 保存动态广场的缓存json
     */
    public void saveDynamicSquareCacheJson(String json) {
        insert(json, TYPE_DYNAMIC_SQUARE);
    }

    /**
     * 获取动态的缓存json
     */
    public String getIMGroupsCacheJson() {
        return getCacheJson(TYPE_IM_GROUPS);
    }

    /**
     * 保存动态的缓存json
     */
    public void saveIMGroupsCacheJson(String json) {
        insert(json, TYPE_IM_GROUPS);
    }

    /**
     * 获取首页缓存json
     */
    public String getHomePageCacheJson() {
        return getCacheJson(TYPE_HOMEPAGE);
    }

    /**
     * 更新首页缓存json
     */
    public void saveHomePageCacheJson(String json) {
        insert(json, TYPE_HOMEPAGE);
    }

    public String getHomeBigChiefCacheJson() {
        return getCacheJson(TYPE_HOME_BIG_CHIEF);
    }

    public void saveHomeBigChiefCacheJson(String json) {
        insert(json, TYPE_HOME_BIG_CHIEF);
    }

    public String getHomeAdCacheJson() {
        return getCacheJson(TYPE_HOME_AD);
    }

    public void saveHomeAdCacheJson(String json) {
        insert(json, TYPE_HOME_AD);
    }

    /**
     * 获取商场首页缓存json
     */
    public String getMallHomePageCacheJson() {
        return getCacheJson(TYPE_MALL);
    }

    /**
     * 更新商场首页缓存json
     */
    public void saveMallHomePageCacheJson(String json) {
        insert(json, TYPE_MALL);
    }


    /**
     * 获取省市区缓存json
     */
    public String getCityTreeCacheJson() {
        return getCacheJson(TYPE_CITY_TREE);
    }

    /**
     * 保存省市区缓存json
     */
    public void saveCityTreeCacheJson(String json) {
        insert(json, TYPE_CITY_TREE);
    }


    /**
     * 获取大字典缓存
     */
    public String getBigDictCacheJson() {
        return getCacheJson(TYPE_BIG_DICT);
    }

    /**
     * 保存大字典
     */
    public void saveBigDictCacheJson(String json) {
        insert(json, TYPE_BIG_DICT);
    }

    /**
     * 获取某类型的缓存
     */
    private String getCacheJson(String type) {
        Cursor cs = query(type);
        String json = null;
        if (cs != null) {
            if (cs.moveToFirst()) {
                json = cs.getString(cs.getColumnIndex(CACHE_JSON));
            }
            cs.close();
        }
        return json;
    }

    /**
     * 插入一条type类型的数据，并保证该type类型只有一条数据
     **/
    private void insert(String cachejson, String type) {
        if (cachejson == null || cachejson.trim().equals("")) {
            return;
        }
        if (isAdded(type)) {
            deleteItem(type);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TYPE, type);
        cv.put(CACHE_JSON, cachejson);
        db.insert(TABLENAME, null, cv);
        db.close();
    }

    /**
     * 判断是否有该type类型的数据存在
     */
    private boolean isAdded(String type) {
        if (type == null || type.equals("")) return false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLENAME, new String[]{TYPE}, TYPE + "=?", new String[]{type}, null, null, null);
            boolean isAdded =  cursor.getCount() > 0 ? true : false;
            cursor.close();
            db.close();
            return isAdded;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public Cursor query() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.query(TABLENAME, null, null, null, null, null, "_id desc");
//    }

    /**
     * 查询该type类型的数据
     */
    private Cursor query(String type) {
        if (type == null || type.equals("")) return null;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLENAME, new String[]{CACHE_JSON}, TYPE + "=?", new String[]{type}, null, null, "_id desc");
    }

    // 删除顶部
//    public void deleteHead() {
//        SQLiteDatabase dbWrite = this.getWritableDatabase();
//        String sql = "delete from " + TABLENAME + " where _id = " + " (select min(_id) from " + TABLENAME+")";
//        dbWrite.execSQL(sql);
//    }

    /**
     * 删除TYPE=type类型的记录
     */
    private void deleteItem(String type) {
        if (type == null || type.equals("")) return;
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        String sql = "delete from " + TABLENAME + " where " + TYPE + " = '" + type + "'";
        dbWrite.execSQL(sql);
        dbWrite.close();
    }

    /**
     * 删除表所有数据
     */
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLENAME, null, null);
        db.close();
    }
}
