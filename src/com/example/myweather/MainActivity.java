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
	private ArrayList<View> bottomShapeList = null;// 底部圆图标
	private int currentItem = 0;// 当前的页卡号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init(); // 控件绑定 数据初始化
		registerReceiver();// 广播注册
		initCityData(); // 初始化已添加的城市数据
		initBottomShape(); // 底部圆初始化
	}

	private void init() {
		viewPager = (ViewPager) findViewById(R.id.viewPage);
		viewPager.setOffscreenPageLimit(10);// 初始化加载10个
		bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);
		fragmentList = new ArrayList<Fragment>();
		dataDetailsList = new ArrayList<String>();
		bottomShapeList = new ArrayList<View>();
		cityList = new ArrayList<String>();
		cityList.add("定位失败");
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
		// 获取实时天气预报
		// 如果需要同时请求实时、未来三天天气，请确保定位获取位置后使用,分开调用，可忽略本句。
		mLocationManagerProxy.requestWeatherUpdates(
				LocationManagerProxy.WEATHER_TYPE_LIVE, this);

	}

	private void registerReceiver() {
		// 响应添加城市的广播 注册
		addCityWeatherFragmentBroadcastReceiver = new AddCityWeatherFragmentBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.example.myweather.AddCityWeatherFragmentBroadcastReceiver");
		this.registerReceiver(addCityWeatherFragmentBroadcastReceiver,
				intentFilter);
		// 返回添加城市的详细天气详情广播 注册
		addCityWeatherDetailsBroadcastReceiver = new AddCityWeatherDetailsBroadcastReceiver();
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2
				.addAction("com.example.myweather.AddCityWeatherDetailsBroadcastReceiver");
		this.registerReceiver(addCityWeatherDetailsBroadcastReceiver,
				intentFilter2);
		// 删除城市 的广播 注册
		delCityBroadcastReceiver = new DelCityBroadcastReceiver();
		IntentFilter intentFilter3 = new IntentFilter();
		intentFilter3
				.addAction("com.example.myweather.DelCityBroadcastReceiver");
		registerReceiver(delCityBroadcastReceiver, intentFilter3);
	}

	private void initCityData() {
		// 获得数据库保存的城市 初始化 ViewPager
		// 因为定位的Fragment的原因，所以要自己主动增加一个底部圆
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
		// 初始为第一个Fragment （定位的天气界面）
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
	 * 销毁定位
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
			// 天气预报成功回调 设置天气信息
			city = aMapLocalWeatherLive.getCity();
			cityList.set(0, city);
			Message msg = handler.obtainMessage();
			msg.what = 3;
			handler.sendMessage(msg);
			stopLocation();
		} else {
			// 获取天气预报失败
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	// 定位的Fragment使用的接口
	public interface LocationListeners {
		public void onLocationListeners(String city);
		// 1.传参给Fragment
		// 2.先建立接口供Fragment实现
		// 3. 然后Fragment返回实现了的接口
		// 4.然后接口调用内部方法传参给Fragment
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

		// 添加城市后要立马加载一下，不然会报空指针异常
		viewPager.setCurrentItem(fragmentList.size() - 1);
	}

	private void deleteFragment(int position) {
		int oldCurrentItem = currentItem;// 记录删除之前的当前Fragment索引
		fragmentList.remove(position);
		myFragmentPagerAdapter.notifyDataSetChanged();
		dataDetailsHasMap.remove(cityList.get(position));
		cityList.remove(position);
		bottomShapeList.remove(position);
		bottom_layout.removeViewAt(position);
		// 当 删除的Fragment界面的索引小于或者等于当前的Fragment界面索引时
		// 需要修改当前的底部圆
		// 需要修改当前的Fragment
		if (position < oldCurrentItem || position == oldCurrentItem) {
			if (position == fragmentList.size()) {
				// 当删除的是最后一页的Fragment时
				viewPager.setCurrentItem(oldCurrentItem - 1);
				View view = bottomShapeList.get(oldCurrentItem - 1);
				view.setBackgroundResource(R.drawable.bottom_focused);
			} else {
				viewPager.setCurrentItem(oldCurrentItem - 1);
				View view = bottomShapeList.get(oldCurrentItem - 1);
				view.setBackgroundResource(R.drawable.bottom_focused);
				if (oldCurrentItem == bottomShapeList.size()) {
					// 当fragment位于最后一个时，需要调整聚焦点
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
			updateFragment(cityName);// 添加Fragment
		}

	}

	class AddCityWeatherDetailsBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 添加的城市详细信息
			String city = intent.getStringExtra("cityName");
			ArrayList<String> dataDetailsList = intent
					.getStringArrayListExtra("dataDetailsList");
			dataDetailsHasMap.put(city, dataDetailsList);
		}

	}

	class DelCityBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 删除城市
			int position = intent.getIntExtra("position", 0);
			deleteFragment(position);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 注销广播
		unregisterReceiver(addCityWeatherFragmentBroadcastReceiver);
		unregisterReceiver(addCityWeatherDetailsBroadcastReceiver);
		unregisterReceiver(delCityBroadcastReceiver);
	}

	// ViewPager 界面切换监听
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		// 页卡切换状态
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		// 页卡滑动中
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		// 页卡滑动完成
		if (bottomShapeList != null && bottomShapeList.size() != 0) {
			View oldView = bottomShapeList.get(currentItem);
			oldView.setBackgroundResource(R.drawable.bottom_normal);
			View newView = bottomShapeList.get(arg0);
			newView.setBackgroundResource(R.drawable.bottom_focused);
			currentItem = arg0;
		}
	}
}