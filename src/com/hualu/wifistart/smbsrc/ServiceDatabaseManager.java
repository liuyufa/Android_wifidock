package com.hualu.wifistart.smbsrc;

import com.hualu.wifistart.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ServiceDatabaseManager
{
	@SuppressWarnings("unused")
	private Context		context;
	private SQLiteDatabase	sqlLiteOpenHelper;
	private String		nowPothString	= null;
	
	public ServiceDatabaseManager(Context context,
			SQLiteDatabase sqlLiteOpenHelper, String nowPothString) {
		this.context = context;
		this.sqlLiteOpenHelper = sqlLiteOpenHelper;
		this.nowPothString = nowPothString;
	}
	
	public ServiceDatabaseManager(Context context, String nowPothString) {
		this.context = context;
		this.nowPothString = nowPothString;
		sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context)
				.getWritableDatabase();
	}
	
	public String getNowPothString() {
		return nowPothString;
	}
	
	public void setNowPothString(String nowPothString) {
		this.nowPothString = nowPothString;
	}
	
	public void execSQL(String sqlString) {
		sqlLiteOpenHelper.execSQL(sqlString);
	}
	
	public String getInfoString() {
		Cursor cursor = sqlLiteOpenHelper.rawQuery(
				"select * from [pathInfo] where [path] = '?'",
				new String[] { nowPothString });
		if (cursor.moveToFirst()) { return cursor
				.getString(cursor.getColumnIndex("info")); 
		}		
		return null;
	}
	
	public static boolean checkInfoExist(String pathString,Context context) {
		
		SQLiteDatabase sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context).getWritableDatabase();
		
		Cursor cursor = sqlLiteOpenHelper.rawQuery("select * from pathInfo where [path] = ?",new String[] { pathString });
		
		boolean willRestString = cursor.moveToFirst();
		
		cursor.close();
		sqlLiteOpenHelper.close();
		
		return willRestString;
	}
	
	public static String getMorePathInfo(Context context,String pathString,String keyString,String defaultString) {
		
		SQLiteDatabase sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context).getWritableDatabase();
		
		Cursor cursor = sqlLiteOpenHelper.rawQuery("select * from pathInfo where [path] = ?",new String[] { pathString });
		
		String willRestString = cursor.moveToFirst() ? (String) cursor.getString(cursor.getColumnIndex(keyString)) : defaultString;
		
		cursor.close();
		sqlLiteOpenHelper.close();
		
		return willRestString;
	}
	
	public static String getInfoString(String pathString, Context context) {
		return getMorePathInfo(context,pathString,"info",context.getString(R.string.string_null));
	}
	
	public static String getSoftString(String pathString, Context context) {
		return getMorePathInfo(context,pathString,"soft",context.getString(R.string.string_null));
	}
	
	public static String getMoreDeleInfo(Context context,String pathString,String keyString,String defaultString) {
		SQLiteDatabase sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context).getWritableDatabase();
		
		Cursor cursor = sqlLiteOpenHelper.rawQuery("select * from deleInfo where [path] = ?",new String[] { pathString });
		
		String willRestString = cursor.moveToFirst() ?  (String) cursor.getString(cursor.getColumnIndex(keyString)) : defaultString;
		
		cursor.close();
		sqlLiteOpenHelper.close();
		
		return willRestString;
	}

	public static String getDeleInfoString(String pathString,Context context)  {
		return getMoreDeleInfo(context, pathString, "Fdele", context.getString(R.string.string_null));
	}
	
	public static String getDeleIndexString(String pathString,Context context)  {
		return getMoreDeleInfo(context, pathString, "index", "6");
	}
	
	public static String getDeleisDeteString(String pathString,Context context)  {
		return getMoreDeleInfo(context, pathString, "isDete", "0");
	}
	
	public static String getDeleisAlertString(String pathString,Context context)  {
		return getMoreDeleInfo(context, pathString, "isAlert", "0");
	}
	
	public static String getDeleAlertInfoString(String pathString,Context context)  {
		return getMoreDeleInfo(context, pathString, "alertInfo", context.getString(R.string.string_null));
	}
	
	public static void addNewPathInfo(String pathString,String nameString,String infoString,String softString,String fDeleString,String indexString,String isDeleString,String isAlertString,String alertInString, Context context) {
		SQLiteDatabase sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context).getWritableDatabase();
		
		String sqlString = String.format("insert into pathInfo([path],[name],[info],[soft]) values ('%1$s','%2$s','%3$s','%4$s')", pathString,nameString,infoString,softString);
		sqlLiteOpenHelper.execSQL(sqlString);
		
		sqlString = String.format("insert into deleInfo([path],[Fdele],[index],[isDete],[isAlert],[alertInfo]) values ('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s')", pathString,fDeleString,indexString,isDeleString,isAlertString,alertInString);
		sqlLiteOpenHelper.execSQL(sqlString);
		
		sqlLiteOpenHelper.close();
	}
	
	public static void updatePathInfo(String pathString,String nameString,String infoString,String softString,String fDeleString,String indexString,String isDeleString,String isAlertString,String alertInString, Context context) {
		SQLiteDatabase sqlLiteOpenHelper = new DatabaseManagerOpenHelper(context).getWritableDatabase();
		
		String sqlString = String.format("update pathInfo set [name]='%1$s',[info]='%2$s',[soft]='%3$s' where [path]='%4$s';",nameString,infoString,softString,pathString);
		sqlLiteOpenHelper.execSQL(sqlString);
		
		sqlString = String.format("update deleInfo set [Fdele]='%1$s',[index]='%2$s',[isDete]='%3$s',[isAlert]='%4$s',[alertInfo]='%5$s' where path='%6$s';",fDeleString,indexString,isDeleString,isAlertString,alertInString, pathString);
		sqlLiteOpenHelper.execSQL(sqlString);
		
		sqlLiteOpenHelper.close();
		
	}
	
}
