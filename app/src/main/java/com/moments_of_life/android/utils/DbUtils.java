package com.moments_of_life.android.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 0016/2017/10/16.
 * 创建时间： 0016/2017/10/16 11:40
 * 创建人：王亮（Loren wang）
 * 功能作用：数据库数据操作类
 *         需要方法：插入、删除、更新、查询、sql语句执行
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */
public class DbUtils extends SQLiteOpenHelper {
    private String TAG = getClass().getName();
    private static String DB_NAME;//数据库名称
    private final String DB_PATH = "/data/data/" + BuildConfig.APPLICATION_ID + "/database/";//数据库路径
    private static int DB_VERSION = BuildConfig.VERSION_CODE;//数据库版本号

    private static DbUtils dbUtils;
    private SQLiteDatabase sqLiteDatabase;

    //默认缺省的返回，如果失败了
    private Cursor defaultCursor = new Cursor() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public boolean move(int offset) {
            return false;
        }

        @Override
        public boolean moveToPosition(int position) {
            return false;
        }

        @Override
        public boolean moveToFirst() {
            return false;
        }

        @Override
        public boolean moveToLast() {
            return false;
        }

        @Override
        public boolean moveToNext() {
            return false;
        }

        @Override
        public boolean moveToPrevious() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return false;
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public boolean isBeforeFirst() {
            return false;
        }

        @Override
        public boolean isAfterLast() {
            return false;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return 0;
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            return new byte[0];
        }

        @Override
        public String getString(int columnIndex) {
            return null;
        }

        @Override
        public void copyStringToBuffer(int columnIndex, CharArrayBuffer createTableBuffer) {

        }

        @Override
        public short getShort(int columnIndex) {
            return 0;
        }

        @Override
        public int getInt(int columnIndex) {
            return 0;
        }

        @Override
        public long getLong(int columnIndex) {
            return 0;
        }

        @Override
        public float getFloat(int columnIndex) {
            return 0;
        }

        @Override
        public double getDouble(int columnIndex) {
            return 0;
        }

        @Override
        public int getType(int columnIndex) {
            return 0;
        }

        @Override
        public boolean isNull(int columnIndex) {
            return false;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public boolean requery() {
            return false;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void registerContentObserver(ContentObserver observer) {

        }

        @Override
        public void unregisterContentObserver(ContentObserver observer) {

        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void setNotificationUri(ContentResolver cr, Uri uri) {

        }

        @Override
        public Uri getNotificationUri() {
            return null;
        }

        @Override
        public boolean getWantsAllOnMoveCalls() {
            return false;
        }

        @Override
        public void setExtras(Bundle extras) {

        }

        @Override
        public Bundle getExtras() {
            return null;
        }

        @Override
        public Bundle respond(Bundle extras) {
            return null;
        }
    };

    public static DbUtils getInstance(){
        if(dbUtils == null){
            dbUtils = new DbUtils();
        }
        return dbUtils;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    static {
        switch (BuildConfig.ENVIRONMENT_VERSION_CONFIG){
            case AppCommon.APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_DEV:
                DB_NAME = "moments_of_life_dev.db";//数据库名称
                break;
            case AppCommon.APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_TEST:
                DB_NAME = "moments_of_life_test.db";//数据库名称
                break;
            case AppCommon.APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_PRODUCTION:
            default:
                DB_NAME = "moments_of_life.db";//数据库名称
                break;
        }
    }

    public DbUtils() {
        super(AppCommon.APP_CONTEXT, DB_NAME, null, DB_VERSION);
        try {
            TAG = getClass().getName();
            sqLiteDatabase = getWritableDatabase();//获得SQLiteDatabase
        }catch (Exception e){
            sqLiteDatabase = null;
        }
    }

    /**
     * 未创建数据库的时候调用该方法
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;//获得SQLiteDatabase
    }

    /**
     * 创建了数据库同时数据库版本更新了调用该方法
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, int i, int i1) {
        this.sqLiteDatabase = sqLiteDatabase;//获得SQLiteDatabase
    }

    /**
     * 插入数据
     * @param table
     * @param values
     * @return 插入,返回值为行号
     */
    public Long insert(String table, ContentValues values) {
        // String sql = "insert into person(name,age) values('rose',21)";
        // db.execSQL(sql);
        try {
            return sqLiteDatabase.insert(table, null, values);
        }catch (Exception e){
            return 0l;
        }
    }

    /**
     * 更新
     *
     * @param table
     *            表名
     * @param values
     *            修改后的值
     * @param whereClause
     *            条件语句,可以使用占位符，？
     * @param whereArgs
     *            使用数组中的值替换占位符
     * @return 返回值为影响数据表的行数
     */
    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {

        // 表名
        // values,修改后的值
        // whereClause:条件语句,可以使用占位符，？
        // whereArgs:使用数组中的值替换占位符
        try {
            return sqLiteDatabase.update(table, values, whereClause, whereArgs);
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * 删除
     *
     * @param table
     *            表名
     * @param whereClause
     *            条件语句,可以使用占位符，？
     * @param whereArgs
     *            使用数组中的值替换占位符
     * @return 返回影响表的行数
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        // String sql = "delete from person where _id=1";
        // db.execSQL(sql);
        try {
            return sqLiteDatabase.delete(table, whereClause, whereArgs);
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * 查询
     *
     * @param sql
     *            sql语句，查询语句，可以包含条件,sql语句不用使用分号结尾，系统自动添加
     * @param selectionArgs
     *            sql的查询条件可以使用占位符，占位符可以使用selectionArgs替代
     * @return 返回值，Cursor，游标，可以比作结果集
     */
    public Cursor select1(String sql, String[] selectionArgs) {
        // 1.sql语句，查询语句，可以包含条件,sql语句不用使用分号结尾，系统自动添加
        // 2.selectionArgs,sql的查询条件可以使用占位符，占位符可以使用selectionArgs替代
        // select * from person where name=?

        try {
            return sqLiteDatabase.rawQuery(sql, selectionArgs);
        }catch (Exception e){
            return defaultCursor;
        }
    }

    /**
     * 查询
     *
     * distinct：消除重复数据（去掉重复项）
     *
     * limit：进行分页查询
     *
     //	 * @param 1、table，表名
     //	 * @param 2、columns，查询的列（字段）*
     //	 * @param 3、selection：where后的条件子句，可以使用占位符
     //	 * @param 4、selectionArgs,替换占位符的值，
     //	 * @param 5、groupBy：根据某个字段进行分组
     //	 * @param 6、having：分组之后再进一步过滤
     //	 * @param 7、orderby:排序
     *
     * @return
     */
    public Cursor select2(String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having,
                          String orderBy) {

        // distinct：消除重复数据（去掉重复项）
        // 1、table，表名
        // 2、columns，查询的列（字段）
        // 3、selection：where后的条件子句，可以使用占位符
        // 4、selectionArgs,替换占位符的值，
        // 5、groupBy：根据某个字段进行分组
        // 6、having：分组之后再进一步过滤
        // 7、orderby:排序
        // limit：进行分页查询

        try {
            return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    having, orderBy);
        }catch (Exception e){
            return defaultCursor;
        }
    }
    /**
     * 查询
     *
     * distinct：消除重复数据（去掉重复项）
     *
     * limit：进行分页查询
     *
     //	 * @param 1、table，表名
     //	 * @param 2、columns，查询的列（字段）*
     //	 * @param 3、selection：where后的条件子句，可以使用占位符
     //	 * @param 4、selectionArgs,替换占位符的值，
     //	 * @param 5、groupBy：根据某个字段进行分组
     //	 * @param 6、having：分组之后再进一步过滤
     //	 * @param 7、orderby:排序
     *
     * @return
     */
    public Cursor select3(String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having,
                          String orderBy,String limit) {

        // distinct：消除重复数据（去掉重复项）
        // 1、table，表名
        // 2、columns，查询的列（字段）
        // 3、selection：where后的条件子句，可以使用占位符
        // 4、selectionArgs,替换占位符的值，
        // 5、groupBy：根据某个字段进行分组
        // 6、having：分组之后再进一步过滤
        // 7、orderby:排序
        // limit：进行分页查询,参数例如：20,10-----就是从第20行开始向后取十行数据


        try {
            return sqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    having, orderBy,limit);
        }catch (Exception e){
            return defaultCursor;
        }
    }
    /**
     * 清空表数据(只用通用表可以清空表数据)
     * @param tableName
     */
    public boolean deleteDatabaseTableData(String tableName) {
        if(sqLiteDatabase != null) {
            try {
                Cursor cursor = select2(tableName, null, null, null, null, null, null);//查找表，如果抛出异常则代表不存在这个表，否则的话就是存在表不做处理
                cursor.close();
                String sql = "delete from " + tableName;
                sqLiteDatabase.execSQL(sql);
                return true;
            }catch (Exception e) {
                LogUtils.logD(TAG,e.getMessage());
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean execSQL(String sql){
        try {
            sqLiteDatabase.execSQL(sql);
            return true;
        }catch (Exception e){
            return false;
        }
    }




    /**
     * 获取数据库中所有的表名
     * @return
     */
    public List<String> getAllTableName(){
        List<String> tableNameList = new ArrayList<>();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table' order by name", null);
            while (cursor.moveToNext()){
                tableNameList.add(cursor.getString(cursor.getColumnIndex("tbl_name")));
            }
            LogUtils.logD(TAG,tableNameList.toString());
            cursor.close();
            cursor = null;
        }catch (Exception e){
            LogUtils.logE(TAG,"search table name list fail");
        }
        return tableNameList;
    }
}
