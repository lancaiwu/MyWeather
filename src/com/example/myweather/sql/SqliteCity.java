package com.example.myweather.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteCity extends SQLiteOpenHelper {
	private static final int VERSION = 1;// ���ݿ�汾
	private static final String DB_NAME = "city.db";// ���ݿ���

	public SqliteCity(Context context) {
		super(context, DB_NAME, null, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// ��������ʱ���õķ���
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
