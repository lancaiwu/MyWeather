package com.example.myweather.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteCity extends SQLiteOpenHelper {
	private static final int VERSION = 1;// 数据库版本
	private static final String DB_NAME = "city.db";// 数据库名

	public SqliteCity(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建数据时调用的方法
		String sql = "CREATE TABLE city "
				+ "(id integer PRIMARY KEY autoincrement,"
				+ "cityName VARCHER(30) NOT NULL)";
		db.execSQL(sql);
		Log.i("sql", sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
