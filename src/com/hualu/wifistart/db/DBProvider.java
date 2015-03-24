package com.hualu.wifistart.db;

import com.hualu.wifistart.db.FileColumn;

import android.content.ContentProvider;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.util.Log;

public class DBProvider extends ContentProvider {
	private DBHelper dbOpenHelper;
	public static final String AUTHORITY = "WIFISHAREMUSIC";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + FileColumn.TABLE);

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		try {
			db.delete(FileColumn.TABLE, arg1, arg2);
			Log.i("info", "delete");
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("error", "delete");
		}
		return 1;
	}
	/**
	 * 待实现
	 */
	@Override
	public String getType(Uri uri) {
		return null;
	}
	/**
	 * 插入
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		long count = 0;
		try {
			count = db.insert(FileColumn.TABLE, null, values);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("error", "insert");
		}
		if (count > 0)
			return uri;
		else
			return null;

	}

	@Override
	public boolean onCreate() {
		dbOpenHelper = new DBHelper(getContext());

		return true;
	}
	/**
	 * 根据条件查询
	 * @return 数据集
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		// 依次参数为：表明，查询字段，where语句,占位符替换，group by(分组)，having(分组条件),order by(排序)
		Cursor cur = db.query(FileColumn.TABLE, projection, selection,
				selectionArgs, null, null, sortOrder);
		return cur;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int i = 0;
		try {
			i = db.update(FileColumn.TABLE, values, selection, null);
			return i;
		} catch (Exception ex) {
			Log.e("error", "update");
		}
		return 0;
	}

}
