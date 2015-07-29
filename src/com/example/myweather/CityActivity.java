package com.example.myweather;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myweather.sql.City;
import com.example.myweather.sql.SqliteDAO;

public class CityActivity extends Activity implements OnItemLongClickListener,
		OnItemClickListener {
	private Button btnaddCity = null;
	private ListView cityListView = null;
	private ArrayList<String> cityList = null;
	private ArrayAdapter<String> arrayAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_activity);
		String city = getIntent().getStringExtra("city");
		initCityData(city);
		initFindById();
		showData();
	}

	private void initCityData(String city) {
		cityList = new ArrayList<String>();
		if (city != null && !city.equals("")) {
			cityList.add("定位：" + city);
		} else {
			cityList.add("定位失败");
		}
		// 访问数据库获取保存的城市
		SqliteDAO sqlDao = new SqliteDAO(this);
		ArrayList<City> cityArr = new ArrayList<City>();
		cityArr = sqlDao.selectCity();
		for (City city2 : cityArr) {
			cityList.add(city2.getCityName());
		}
	}

	private void initFindById() {
		cityListView = (ListView) findViewById(R.id.cityListView);
		cityListView.setOnItemLongClickListener(this);// 添加item长按监听
		cityListView.setOnItemClickListener(this); // 添加item点击监听
		btnaddCity = (Button) findViewById(R.id.btnaddCity);
		btnaddCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CityActivity.this,
						AddCityActivity.class);
				intent.putStringArrayListExtra("cityList", cityList);
				startActivityForResult(intent, 200);
			}
		});
	}

	private void showData() {
		arrayAdapter = new ArrayAdapter<>(this, R.layout.city_listview_item,
				cityList);
		cityListView.setAdapter(arrayAdapter);
	}

	/**
	 * 回传的处理方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode 传过去的 resultCode 传回来的
		if (requestCode == 200 && resultCode == 201) {
			String cityName = data.getStringExtra("cityName");
			cityList.add(cityName);
			arrayAdapter.notifyDataSetChanged();
			// 用广播通知MainActivity加载新添加的城市天气
			Intent intent = new Intent();
			intent.setAction("com.example.myweather.AddCityWeatherFragmentBroadcastReceiver");
			intent.putExtra("cityName", cityName);
			sendBroadcast(intent);

			SqliteDAO sqlDao = new SqliteDAO(this);
			City city = new City();
			city.setCityName(cityName);
			sqlDao.addCity(city);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// item长按监听
		new AlertDialog.Builder(this).setTitle("删除").setMessage("是否删除该城市")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (position != 0) {
							SqliteDAO sqlDao = new SqliteDAO(CityActivity.this);
							City city = new City();
							city.setCityName(cityList.get(position));
							sqlDao.deleteCity(city);
							cityList.remove(position);
							arrayAdapter.notifyDataSetChanged();

							Intent intent = new Intent();
							intent.setAction("com.example.myweather.DelCityBroadcastReceiver");
							intent.putExtra("position", position);
							sendBroadcast(intent);
						} else {
							Toast.makeText(CityActivity.this, "定位城市不可以删除",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("否", null).create().show();
		return true;// true 表示响应长按事件
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}
