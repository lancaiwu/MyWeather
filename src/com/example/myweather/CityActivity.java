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
			cityList.add("��λ��" + city);
		} else {
			cityList.add("��λʧ��");
		}
		// �������ݿ��ȡ����ĳ���
		SqliteDAO sqlDao = new SqliteDAO(this);
		ArrayList<City> cityArr = new ArrayList<City>();
		cityArr = sqlDao.selectCity();
		for (City city2 : cityArr) {
			cityList.add(city2.getCityName());
		}
	}

	private void initFindById() {
		cityListView = (ListView) findViewById(R.id.cityListView);
		cityListView.setOnItemLongClickListener(this);// ���item��������
		cityListView.setOnItemClickListener(this); // ���item�������
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
	 * �ش��Ĵ�����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode ����ȥ�� resultCode ��������
		if (requestCode == 200 && resultCode == 201) {
			String cityName = data.getStringExtra("cityName");
			cityList.add(cityName);
			arrayAdapter.notifyDataSetChanged();
			// �ù㲥֪ͨMainActivity��������ӵĳ�������
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
		// item��������
		new AlertDialog.Builder(this).setTitle("ɾ��").setMessage("�Ƿ�ɾ���ó���")
				.setPositiveButton("��", new DialogInterface.OnClickListener() {

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
							Toast.makeText(CityActivity.this, "��λ���в�����ɾ��",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("��", null).create().show();
		return true;// true ��ʾ��Ӧ�����¼�
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
}
