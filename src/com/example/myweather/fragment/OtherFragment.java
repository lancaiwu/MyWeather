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
	private View view; // �������
	private String city = "";// ����
	private ArrayList<String> dataList;// ��������
	private String temperature = "";
	// ������ʾ���
	// ����
	private TextView weather_date;
	private TextView weather_date2;
	private TextView weather_date3;
	private TextView weather_date4;
	// ����
	private TextView weather;
	private TextView weather2;
	private TextView weather3;
	private TextView weather4;
	// ��
	private TextView weather_wind;
	private TextView weather_wind2;
	private TextView weather_wind3;
	private TextView weather_wind4;
	// �¶�����
	private TextView weather_temperature;
	private TextView weather_temperature2;
	private TextView weather_temperature3;
	private TextView weather_temperature4;

	// �¶�
	private TextView tvtemperature;
	// ����
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
					String date = dataList.get(2);// ��õ�ǰ���ڼ��¶�
					// ��ȡ��ǰ�¶�����
					if (date.length() >= 16) {
						int index = date.lastIndexOf("��");
						int index2 = date.lastIndexOf("��");
						temperature = date.substring(index + 1, index2);
					} else {
						temperature = dataList.get(7);
					}
					try {
						// ���粻��ʱ�������ָ���쳣
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
		 * dataList�����ݶ�Ӧ��i
		 * 
		 * 
		 * 2015-04-12 0 �Ž��� 1 ���� 04��12�� (ʵʱ��20��) 2
		 * http://api.map.baidu.com/images/weather/day/duoyun.png 3
		 * http://api.map.baidu.com/images/weather/night/qing.png 4 ����ת�� 5 ΢�� 6
		 * 23 ~ 7�� 7 ��һ 8 http://api.map.baidu.com/images/weather/day/qing.png 9
		 * http://api.map.baidu.com/images/weather/night/qing.png 10 �� 11 ΢�� 12
		 * 18 ~ 7�� 13 �ܶ� 14 http://api.map.baidu.com/images/weather/day/qing.png
		 * 15 http://api.map.baidu.com/images/weather/night/qing.png 16 �� 17 ΢��
		 * 18 21 ~ 8�� 19 ���� 20
		 * http://api.map.baidu.com/images/weather/day/duoyun.png 21
		 * http://api.map.baidu.com/images/weather/night/zhenyu.png 22 ����ת���� 23
		 * ΢�� 24 25 ~ 14�� 25 ���� 26 ������ 27 ����ָ�� 28
		 * �����ű����׻�ţ������ȷ�װ���������������żп�������ë�µȡ���ҹ�²�ϴ�ע���ʵ������·��� 29 ϴ�� 30 ������ 31 ϴ��ָ��
		 * 32 ������ϴ����δ��һ�����꣬������С����ϴһ�µ����������ܱ���һ�졣 33 ���� 34 ���� 35 ����ָ�� 36
		 * �����Ϻã���˿������Ӱ�������е����顣�¶���������΢����飬�������Ρ� 37 ��ð 38 �׷� 39 ��ðָ�� 40
		 * ��ҹ�²�ܴ��׷�����ð����ע���ʵ������·�����ǿ���ҷ��������ð�� 41 �˶� 42 ������ 43 �˶�ָ�� 44
		 * �����Ϻã�����ˮ���ţ������˽��и����˶����������½ϵͣ��ڻ����˶���ע��������� 45 ������ǿ�� 46 �� 47 ������ǿ��ָ�� 48
		 * ������ǿ�Ƚ������������ǰͿ��SPF��12-15֮�䡢PA+�ķ�ɹ����Ʒ�� 49 pm2.5 ���� 36 50
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
					tvweather_pm.setText("����pm2.5����");
				}
			}
		}
		if (dataList.get(2).length() >= 16) {
			// ֻ�е���ʵʱ�¶�ʱ�ŵ���
			ValueAnimator temperatureValueAnimator = ValueAnimator.ofInt(0,
					Integer.valueOf(temperature));
			temperatureValueAnimator.setDuration(2000);
			temperatureValueAnimator
					.addUpdateListener(new AnimatorUpdateListener() {

						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							// TODO Auto-generated method stub

							tvtemperature.setText(animation.getAnimatedValue()
									+ "��");

						}

					});
			// ���ò�ֵ�� �ȼ��ٺ����
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
			level = "��";
		} else if (35 <= pmVlaue && pmVlaue < 75) {
			level = "��";
		} else if (75 <= pmVlaue && pmVlaue < 115) {
			level = "�����Ⱦ";
		} else if (115 <= pmVlaue && pmVlaue < 150) {
			level = "�ж���Ⱦ";
		} else if (150 <= pmVlaue && pmVlaue < 250) {
			level = "�ض���Ⱦ";
		} else if (250 <= pmVlaue) {
			level = "������Ⱦ";
		}
		tvweather_pm.setText("��������  " + level + " " + String.valueOf(pmVlaue));
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
		// �������������ݷ��͸�activity
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