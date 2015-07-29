package com.example.myweather;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.example.myweather.adapter.MyFragmentPagerAdapter;
import com.example.myweather.fragment.OtherFragment;
import com.example.myweather.fragment.WeatherFragment;
import com.example.myweather.sql.City;
import com.example.myweather.sql.SqliteDAO;

public class MainActivity extends FragmentActivity implements
		AMapLocalWeatherListener, OnClickListener, OnPageChangeListener {
	private LocationListeners locationListeners = null;
	private String city = "";
	private LocationManagerProxy mLocationManagerProxy = null;
	private ViewPager viewPager = null;
	private Button btnWeatherDetails = null;
	private Button btnWeatherCity = null;
	private ArrayList<Fragment> fragmentList = null;
	private ArrayList<String> dataDetailsList = null;
	private MyFragmentPagerAdapter myFragmentPagerAdapter = null;
	private AddCityWeatherFragmentBroadcastReceiver addCityWeatherFragmentBroadcastReceiver = null;
	private AddCityWeatherDetailsBroadcastReceiver addCityWeatherDetailsBroadcastReceiver = null;
	private DelCityBroadcastReceiver delCityBroadcastReceiver = null;
	private ArrayList<String> cityList = null;
	private HashMap<String, ArrayList<String>> dataDetailsHasMap = null;
	private LinearLayout bottom_layout = null;
	private ArrayList<View> bottomShapeList = null;// �ײ�Բͼ��
	private int currentItem = 0;// ��ǰ��ҳ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init(); // �ؼ��� ���ݳ�ʼ��
		registerReceiver();// �㲥ע��
		initCityData(); // ��ʼ������ӵĳ�������
		initBottomShape(); // �ײ�Բ��ʼ��
	}

	private void init() {
		viewPager = (ViewPager) findViewById(R.id.viewPage);
		viewPager.setOffscreenPageLimit(10);// ��ʼ������10��
		bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);
		fragmentList = new ArrayList<Fragment>();
		dataDetailsList = new ArrayList<String>();
		bottomShapeList = new ArrayList<View>();
		cityList = new ArrayList<String>();
		cityList.add("��λʧ��");
		dataDetailsHasMap = new HashMap<String, ArrayList<String>>();
		final WeatherFragment weatherFragment = new WeatherFragment();
		fragmentList.add(weatherFragment);
		btnWeatherDetails = (Button) findViewById(R.id.btnWeatherDetails);
		btnWeatherCity = (Button) findViewById(R.id.btnWeatherCity);
		btnWeatherDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int current = 0;
				while (viewPager.getCurrentItem() != current) {
					current++;
				}
				dataDetailsList = dataDetailsHasMap.get(cityList.get(current));

				Intent intent = new Intent(MainActivity.this,
						WeatherDetailsActivity.class);
				intent.putExtra("dataDetailsList", dataDetailsList);
				startActivity(intent);
			}
		});
		btnWeatherCity.setOnClickListener(this);
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(myFragmentPagerAdapter);
		viewPager.setOnPageChangeListener(this);
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// ��ȡʵʱ����Ԥ��
		// �����Ҫͬʱ����ʵʱ��δ��������������ȷ����λ��ȡλ�ú�ʹ��,�ֿ����ã��ɺ��Ա��䡣
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);

	}

	private void registerReceiver() {
		// ��Ӧ��ӳ��еĹ㲥 ע��
		addCityWeatherFragmentBroadcastReceiver = new AddCityWeatherFragmentBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.example.myweather.AddCityWeatherFragmentBroadcastReceiver");
		this.registerReceiver(addCityWeatherFragmentBroadcastReceiver,
				intentFilter);
		// ������ӳ��е���ϸ��������㲥 ע��
		addCityWeatherDetailsBroadcastReceiver = new AddCityWeatherDetailsBroadcastReceiver();
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2
				.addAction("com.example.myweather.AddCityWeatherDetailsBroadcastReceiver");
		this.registerReceiver(addCityWeatherDetailsBroadcastReceiver,
				intentFilter2);
		// ɾ������ �Ĺ㲥 ע��
		delCityBroadcastReceiver = new DelCityBroadcastReceiver();
		IntentFilter intentFilter3 = new IntentFilter();
		intentFilter3
				.addAction("com.example.myweather.DelCityBroadcastReceiver");
		registerReceiver(delCityBroadcastReceiver, intentFilter3);
	}

	private void initCityData() {
		// ������ݿⱣ��ĳ��� ��ʼ�� ViewPager
		// ��Ϊ��λ��Fragment��ԭ������Ҫ�Լ���������һ���ײ�Բ
		View view = LinearLayout.inflate(this, R.layout.bottom_shape, null);
		bottomShapeList.add(view);
		bottom_layout.addView(view, 20, 20);

		SqliteDAO sqlDao = new SqliteDAO(this);
		ArrayList<City> cityArray = sqlDao.selectCity();
		for (City city : cityArray) {
			cityList.add(city.getCityName());
			updateFragment(city.getCityName());
		}
	}

	private void initBottomShape() {
		// ��ʼΪ��һ��Fragment ����λ���������棩
		viewPager.setCurrentItem(0);
		View view2 = bottomShapeList.get(0);
		view2.setBackgroundResource(R.drawable.bottom_focused);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 3) {
				locationListeners.onLocationListeners(city);
			} else if (msg.what == 0) {
				locationListeners.onLocationListeners("0");
			}
		}

	};

	/**
	 * ���ٶ�λ
	 */
	private void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.destory();
		}
		mLocationManagerProxy = null;
	}

	@Override
	public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

	}

	@Override
	public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {

		if (aMapLocalWeatherLive != null
				&& aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
			// ����Ԥ���ɹ��ص� ����������Ϣ
			city = aMapLocalWeatherLive.getCity();
			cityList.set(0, city);
			Message msg = handler.obtainMessage();
			msg.what = 3;
			handler.sendMessage(msg);
			stopLocation();
		} else {
			// ��ȡ����Ԥ��ʧ��
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	// ��λ��Fragmentʹ�õĽӿ�
	public interface LocationListeners {
		public void onLocationListeners(String city);
		// 1.���θ�Fragment
		// 2.�Ƚ����ӿڹ�Fragmentʵ��
		// 3. Ȼ��Fragment����ʵ���˵Ľӿ�
		// 4.Ȼ��ӿڵ����ڲ��������θ�Fragment
	}

	public void setInterface(LocationListeners locationListeners) {
		this.locationListeners = locationListeners;
	}

	public void updateFragment(String cityName) {
		OtherFragment otherFragment = new OtherFragment();
		fragmentList.add(otherFragment);
		otherFragment.setCity(cityName);
		myFragmentPagerAdapter.notifyDataSetChanged();

		View view = LinearLayout.inflate(this, R.layout.bottom_shape, null);
		bottomShapeList.add(view);
		bottom_layout.addView(view, 20, 20);
		View view2 = bottomShapeList.get(currentItem);
		view2.setBackgroundResource(R.drawable.bottom_normal);
		view.setBackgroundResource(R.drawable.bottom_focused);

		// ��ӳ��к�Ҫ�������һ�£���Ȼ�ᱨ��ָ���쳣
		viewPager.setCurrentItem(fragmentList.size() - 1);
	}

	private void deleteFragment(int position) {
		int oldCurrentItem = currentItem;// ��¼ɾ��֮ǰ�ĵ�ǰFragment����
		fragmentList.remove(position);
		myFragmentPagerAdapter.notifyDataSetChanged();
		dataDetailsHasMap.remove(cityList.get(position));
		cityList.remove(position);
		bottomShapeList.remove(position);
		bottom_layout.removeViewAt(position);
		// �� ɾ����Fragment���������С�ڻ��ߵ��ڵ�ǰ��Fragment��������ʱ
		// ��Ҫ�޸ĵ�ǰ�ĵײ�Բ
		// ��Ҫ�޸ĵ�ǰ��Fragment
		if (position < oldCurrentItem || position == oldCurrentItem) {
			if (position == fragmentList.size()) {
				// ��ɾ���������һҳ��Fragmentʱ
				viewPager.setCurrentItem(oldCurrentItem - 1);
				View view = bottomShapeList.get(oldCurrentItem - 1);
				view.setBackgroundResource(R.drawable.bottom_focused);
			} else {
				viewPager.setCurrentItem(oldCurrentItem - 1);
				View view = bottomShapeList.get(oldCurrentItem - 1);
				view.setBackgroundResource(R.drawable.bottom_focused);
				if (oldCurrentItem == bottomShapeList.size()) {
					// ��fragmentλ�����һ��ʱ����Ҫ�����۽���
					View view2 = bottomShapeList.get(oldCurrentItem - 2);
					view2.setBackgroundResource(R.drawable.bottom_normal);
				} else {
					View view2 = bottomShapeList.get(oldCurrentItem);
					view2.setBackgroundResource(R.drawable.bottom_normal);
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnWeatherCity:
			Intent intent = new Intent(MainActivity.this, CityActivity.class);
			intent.putExtra("city", city);
			startActivity(intent);
			break;
		default:
			break;
		}
	}


	class AddCityWeatherFragmentBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String cityName = intent.getStringExtra("cityName");
			cityList.add(cityName);
			updateFragment(cityName);// ���Fragment
		}

	}

	class AddCityWeatherDetailsBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��ӵĳ�����ϸ��Ϣ
			String city = intent.getStringExtra("cityName");
			ArrayList<String> dataDetailsList = intent
					.getStringArrayListExtra("dataDetailsList");
			dataDetailsHasMap.put(city, dataDetailsList);
		}

	}

	class DelCityBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ɾ������
			int position = intent.getIntExtra("position", 0);
			deleteFragment(position);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// ע���㲥
		unregisterReceiver(addCityWeatherFragmentBroadcastReceiver);
		unregisterReceiver(addCityWeatherDetailsBroadcastReceiver);
		unregisterReceiver(delCityBroadcastReceiver);
	}

	// ViewPager �����л�����
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		// ҳ���л�״̬
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		// ҳ��������
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		// ҳ���������
		if (bottomShapeList != null && bottomShapeList.size() != 0) {
			View oldView = bottomShapeList.get(currentItem);
			oldView.setBackgroundResource(R.drawable.bottom_normal);
			View newView = bottomShapeList.get(arg0);
			newView.setBackgroundResource(R.drawable.bottom_focused);
			currentItem = arg0;
		}
	}
}