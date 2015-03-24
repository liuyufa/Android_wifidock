package com.hualu.wifistart.db;



import com.hualu.wifistart.db.FileColumn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 *
 */
public class DBHelper extends SQLiteOpenHelper{
    /**
     * 数据库名称常量
     */
    private static final String DATABASE_NAME = "MyMusic.db";
    /**
     *  数据库版本常量
     */
    private static final int DATABASE_VERSION = 1;
    /**
     *  表名称常量
     */
    public static final String TABLES_TABLE_NAME = "File_Table";
    private static final String DATABASE_CREATE = "CREATE TABLE " + FileColumn.TABLE +" ("
	+ FileColumn.ID+" integer primary key autoincrement,"
	+ FileColumn.NAME+" text,"
	+ FileColumn.PATH+" text,"
	+ FileColumn.SINGER+" text,"
	+ FileColumn.TIME +" long,"
	+ FileColumn.TITTLE+" text,"
	+ FileColumn.SORT+" integer,"
	+ FileColumn.TYPE+" text)";
	/**
	 *  构造方法
	 * @param context
	 */
	public DBHelper(Context context) {
		// 创建数据库
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
	}

	
	/**
	 *  创建时调用
	 */
	public void onCreate(SQLiteDatabase db) {
		/*Locale l = new Locale("zh", "CN");
		db.setLocale(l);*/
        db.execSQL(DATABASE_CREATE);      
	}

	/**
	 *  版本更新时调用
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 删除表
		db.execSQL("DROP TABLE IF EXISTS File_Table");
        onCreate(db);
	}

}
