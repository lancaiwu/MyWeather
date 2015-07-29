package com.example.myweather.fragment;

import java.util.ArrayList;

import com.example.myweather.R;
import com.example.myweather.R.id;
import com.example.myweather.R.layout;
import com.example.myweather.common.XMLjiexi;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

public class OtherFragment extends Fragment {
	private View view; // 缓存界面
	private String city = "";// 城市
	private ArrayList<String> dataList;// 天气数据
	private String temperature = "";
	// 天气显示组件
	// 日期
	private TextView weather_date;
	private TextView weather_date2;
	private TextView weather_date3;
	private TextView weather_date4;
	// 天气
	private TextView weather;
	private TextView weather2;
	private TextView weather3;
	private TextView weather4;
	// 风
	private TextView weather_wind;
	private TextView weather_wind2;
	private TextView weather_wind3;
	private TextView weather_wind4;
	// 温度区间
	private TextView weather_temperature;
	private TextView weather_temperature2;
	private TextView weather_temperature3;
	private TextView weather_temperature4;

	// 温度
	private TextView tvtemperature;
	// 城市
	private TextView tvCity;
	// pm2.5
	private TextView tvweather_pm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dataList = new ArrayList<String>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		} else {

			view = inflater.inflate(R.layout.location__weather_activity, null);

			weather_date = (TextView) view.findViewById(R.id.weather_date);
			weather_date2 = (TextView) view.findViewById(R.id.weather_date2);
			weather_date3 = (TextView) view.findViewById(R.id.weather_date3);
			weather_date4 = (TextView) view.findViewById(R.id.weather_date4);

			weather = (TextView) view.findViewById(R.id.weather);
			weather2 = (TextView) view.findViewById(R.id.weather2);
			weather3 = (TextView) view.findViewById(R.id.weather3);
			weather4 = (TextView) view.findViewById(R.id.weather4);

			weather_wind = (TextView) view.findViewById(R.id.weather_wind);
			weather_wind2 = (TextView) view.findViewById(R.id.weather_wind2);
			weather_wind3 = (TextView) view.findViewById(R.id.weather_wind3);
			weather_wind4 = (TextView) view.findViewById(R.id.weather_wind4);

			weather_temperature = (TextView) view
					.findViewById(R.id.weather_temperature);
			weather_temperature2 = (TextView) view
					.findViewById(R.id.weather_temperature2);
			weather_temperature3 = (TextView) view
					.findViewById(R.id.weather_temperature3);
			weather_temperature4 = (TextView) view
					.findViewById(R.id.weather_temperature4);

			tvtemperature = (TextView) view.findViewById(R.id.temperature);
			tvCity = (TextView) view.findViewById(R.id.weather_city);
			tvweather_pm = (TextView) view.findViewById(R.id.weather_pm);
		}
		return view;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 4) {
				try {
					String date = dataList.get(2);// 获得当前日期及温度
					// 截取当前温度数据
					if (date.length() >= 16) {
						int index = date.lastIndexOf("：");
						int index2 = date.lastIndexOf("℃");
						temperature = date.substring(index + 1, index2);
					} else {
						temperature = dataList.get(7);
					}
					try {
						// 网络不好时会产生空指针异常
						showWeather();
						getWeatherDetails();
					} catch (NullPointerException e) {
						// TODO: handle exception
					}
				} catch (IndexOutOfBoundsException e) {
					// TODO: handle exception
				}

			}
		}

	};

	private void showWeather() {
		/**
		 * dataList总数据对应的i
		 * 
		 * 
		 * 2015-04-12 0 九江市 1 周日 04月12日 (实时：20℃) 2
		 * http://api.map.baidu.com/images/weather/day/duoyun.png 3
		 * http://api.map.baidu.com/images/weather/night/qing.png 4 多云转晴 5 微风 6
		 * 23 ~ 7℃ 7 周一 8 http://api.map.baidu.com/images/weather/day/qing.png 9
		 * http://api.map.baidu.com/images/weather/night/qing.png 10 晴 11 微风 12
		 * 18 ~ 7℃ 13 周二 14 http://api.map.baidu.com/images/weather/day/qing.png
		 * 15 http://api.map.baidu.com/images/weather/night/qing.png 16 晴 17 微风
		 * 18 21 ~ 8℃ 19 周三 20
		 * http://api.map.baidu.com/images/weather/day/duoyun.png 21
		 * http://api.map.baidu.com/images/weather/night/zhenyu.png 22 多云转阵雨 23
		 * 微风 24 25 ~ 14℃ 25 穿衣 26 较舒适 27 穿衣指数 28
		 * 建议着薄外套或牛仔衫裤等服装。年老体弱者宜着夹克衫、薄毛衣等。昼夜温差较大，注意适当增减衣服。 29 洗车 30 较适宜 31 洗车指数
		 * 32 较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。 33 旅游 34 适宜 35 旅游指数 36
		 * 天气较好，但丝毫不会影响您出行的心情。温度适宜又有微风相伴，适宜旅游。 37 感冒 38 易发 39 感冒指数 40
		 * 昼夜温差很大，易发生感冒，请注意适当增减衣服，加强自我防护避免感冒。 41 运动 42 较适宜 43 运动指数 44
		 * 天气较好，无雨水困扰，较适宜进行各种运动，但因气温较低，在户外运动请注意增减衣物。 45 紫外线强度 46 弱 47 紫外线强度指数 48
		 * 紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。 49 pm2.5 数据 36 50
		 */
		for (int i = 0; i < dataList.size(); i++) {
			if (i == 1) {
				tvCity.setText(dataList.get(i));
			} else if (i == 2) {
				weather_date.setText(dataList.get(i));
			} else if (i == 5) {
				weather.setText(dataList.get(i));
			} else if (i == 6) {
				weather_wind.setText(dataList.get(i));
			} else if (i == 7) {
				weather_temperature.setText(dataList.get(i));
			} else if (i == 8) {
				weather_date2.setText(dataList.get(i));
			} else if (i == 11) {
				weather2.setText(dataList.get(i));
			} else if (i == 12) {
				weather_wind2.setText(dataList.get(i));
			} else if (i == 13) {
				weather_temperature2.setText(dataList.get(i));
			} else if (i == 14) {
				weather_date3.setText(dataList.get(i));
			} else if (i == 17) {
				weather3.setText(dataList.get(i));
			} else if (i == 18) {
				weather_wind3.setText(dataList.get(i));
			} else if (i == 19) {
				weather_temperature3.setText(dataList.get(i));
			} else if (i == 20) {
				weather_date4.setText(dataList.get(i));
			} else if (i == 23) {
				weather4.setText(dataList.get(i));
			} else if (i == 24) {
				weather_wind4.setText(dataList.get(i));
			} else if (i == 25) {
				weather_temperature4.setText(dataList.get(i));
			} else if (i == 50) {
				if (dataList.get(i) != null && dataList.get(i) != "") {
					setPm(Integer.valueOf(dataList.get(i)));
				}else{
					tvweather_pm.setText("暂无pm2.5数据");
				}
			}
		}
		if (dataList.get(2).length() >= 16) {
			// 只有当有实时温度时才调用
			ValueAnimator temperatureValueAnimator = ValueAnimator.ofInt(0,
					Integer.valueOf(temperature));
			temperatureValueAnimator.setDuration(2000);
			temperatureValueAnimator
					.addUpdateListener(new AnimatorUpdateListener() {

						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							// TODO Auto-generated method stub

							tvtemperature.setText(animation.getAnimatedValue()
									+ "℃");

						}

					});
			// 设置插值器 先加速后减速
			temperatureValueAnimator
					.setInterpolator(new AccelerateDecelerateInterpolator());
			temperatureValueAnimator.start();
		} else {
			tvtemperature.setText(temperature);
		}
	}

	private void setPm(int pmVlaue) {
		String level = "";
		if (0 <= pmVlaue && pmVlaue < 35) {
			level = "优";
		} else if (35 <= pmVlaue && pmVlaue < 75) {
			level = "良";
		} else if (75 <= pmVlaue && pmVlaue < 115) {
			level = "轻度污染";
		} else if (115 <= pmVlaue && pmVlaue < 150) {
			level = "中度污染";
		} else if (150 <= pmVlaue && pmVlaue < 250) {
			level = "重度污染";
		} else if (250 <= pmVlaue) {
			level = "严重污染";
		}
		tvweather_pm.setText("空气质量  " + level + " " + String.valueOf(pmVlaue));
	}

	public void setCity(String c) {
		this.city = c;
		new Thread() {
			public void run() {
				dataList = XMLjiexi.getWeather(city);
				Message msg = handler.obtainMessage();
				msg.what = 4;
				handler.sendMessage(msg);
			};
		}.start();
	}

	public void getWeatherDetails() {
		// 将天气详情数据发送给activity
		ArrayList<String> dataDetailsList = new ArrayList<String>();
		if (dataList.size() == 51) {
			for (int i = 26; i < 50; i++) {
				dataDetailsList.add(dataList.get(i));
			}
		}
		Intent intent = new Intent();
		intent.setAction("com.example.myweather.AddCityWeatherDetailsBroadcastReceiver");
		intent.putExtra("cityName", city);
		intent.putStringArrayListExtra("dataDetailsList", dataDetailsList);
		getActivity().sendBroadcast(intent);
	}
}