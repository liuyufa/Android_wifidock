package com.hualu.wifistart.smbsrc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManagerOpenHelper extends SQLiteOpenHelper
{
	private final static int 		DATABASEVERSION 		= 1;
	private final static String 	DATABASENAME_STRING 	= "SDCard.db";
	
	@SuppressWarnings("unused")
	private Context context = null;
	
	public DatabaseManagerOpenHelper(Context context) {
		super(context, DATABASENAME_STRING, null, DATABASEVERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sqlString = "create table [pathInfo]([path] primary key  ,[name],[info],[soft])";
		arg0.execSQL(sqlString);
		sqlString  = "create table [deleInfo]([path] primary key ,[Fdele],[index],[isDete],[isAlert],[alertInfo])";
		arg0.execSQL(sqlString);
		sqlString = "create table [softInfo]([package]  primary key ,[name],[info])";
		arg0.execSQL(sqlString);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
}
