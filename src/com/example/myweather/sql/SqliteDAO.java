package com.example.myweather.sql;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteDAO {
	private SqliteCity sqliteHelper = null;
	private SQLiteDatabase db = null;

	public SqliteDAO(Context context) {
		// TODO Auto-generated constructor stub
		sqliteHelper = new SqliteCity(context);
	}

	public void addCity(City city) {
		db = sqliteHelper.getWritableDatabase();
		String sql = "insert into city(cityName) values(?)";
		db.execSQL(sql, new Object[] { city.getCityName() });
	}

	public void deleteCity(City city) {
		db = sqliteHelper.getWritableDatabase();
		String sql = "delete from city where cityName=?";
		db.execSQL(sql, new Object[] { city.getCityName() });
	}

	public void deleteAllCity() {
		db = sqliteHelper.getWritableDatabase();
		String sql = "delete from city";
		db.execSQL(sql);
	}

	public ArrayList<City> selectCity() {
		db = sqliteHelper.getWritableDatabase();
		String sql = "select * from city";
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<City> cityList = new ArrayList<City>();
		while (cursor.moveToNext()) {
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
			cityList.add(city);
		}
		return cityList;
	}
}
