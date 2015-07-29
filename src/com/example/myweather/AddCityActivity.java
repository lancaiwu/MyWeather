package com.example.myweather;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AddCityActivity extends Activity implements OnItemClickListener {
	private Button btnCloseAddCity = null;
	private EditText ed = null;
	private ListView findCityListView = null;
	private ArrayList<String> cityArray = null;
	private ArrayList<String> cityArrayBackups = null; // 备份城市列表
	private ArrayAdapter<String> arrayAdapter = null;
	private ArrayList<String> cityList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcity_activity);
		cityList = getIntent().getStringArrayListExtra("cityList");
		init();
		initData();
		showData();
	}

	private void init() {
		findCityListView = (ListView) findViewById(R.id.findCityListView);
		findCityListView.setOnItemClickListener(this); // item监听
		ed = (EditText) findViewById(R.id.edfindCity);
		ed.addTextChangedListener(textWatcher);// 文本修改监听

		btnCloseAddCity = (Button) findViewById(R.id.btncloseAddCity);
		btnCloseAddCity.setOnClickListener(new OnClickListener() {
			// 关闭按钮监听
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddCityActivity.this,
						CityActivity.class);
				setResult(202, intent); // 回传参数
				finish();// 销毁界面
			}
		});
	}

	private void initData() {
		cityArray = new ArrayList<String>();
		cityArrayBackups = new ArrayList<String>();
		String city[] = getResources().getStringArray(R.array.city);
		for (int i = 0; i < city.length; i++) {
			cityArray.add(city[i]);
			cityArrayBackups.add(city[i]);
		}
	}

	private void showData() {
		arrayAdapter = new ArrayAdapter<>(this,
				R.layout.add_city_listview_item, cityArray);
		findCityListView.setAdapter(arrayAdapter);
	}

	private void updateCity_re(String cityName) { // 移除不满足条件的城市
		Iterator<String> iter = cityArray.iterator();// 迭代器模式
		while (iter.hasNext()) {
			String name = iter.next();
			if (name.contains(cityName) == false) {
				// 当前循环的城市名字 不包含 输入的城市名字
				// cityArray.remove(name); 运用这个方法会报错
				iter.remove();
			}
		}
		if (cityArray.size() > 0) {
			setCitySort(cityName);// 城市排序
		}
	}

	private void updateCity_add(String cityName) {// 添加满足条件的城市
		if (cityName.equals("") || cityName == null) { // 输入框为空
			cityArray.clear();
			for (String name : cityArrayBackups) {
				cityArray.add(name);
			}
		} else {
			ArrayList<String> arrayCity = new ArrayList<String>();
			for (String name : cityArrayBackups) {
				if (name.contains(cityName)) {
					if (cityArray.size() > 0) {
						for (String n : cityArray) { // 当城市列表不为空时
							if (n.equals(name) == false) {// 判断是否已有该城市
								arrayCity.add(name);
							}
						}
					} else {// 当城市列表为空时
						arrayCity.add(name);
					}
				}
			}
			cityArray.addAll(arrayCity);// 添加新的城市
			setCitySort(cityName);// 城市排序
		}
	}

	private void setCitySort(String cityName) {
		// 排序 偷懒
		ArrayList<String> cityarray1 = new ArrayList<String>();
		ArrayList<String> cityarray2 = new ArrayList<String>();
		ArrayList<String> cityarray3 = new ArrayList<String>();
		ArrayList<String> cityarray4 = new ArrayList<String>();
		for (String name : cityArray) {
			int index = name.indexOf(cityName);
			switch (index) {
			case 0:
				cityarray1.add(name);
				break;
			case 1:
				cityarray2.add(name);
				break;
			case 2:
				cityarray3.add(name);
				break;
			case 3:
				cityarray4.add(name);
				break;
			default:
				break;
			}
		}
		cityArray.clear();
		cityArray.addAll(cityarray1);
		cityArray.addAll(cityarray2);
		cityArray.addAll(cityarray3);
		cityArray.addAll(cityarray4);
	}

	// EditText 内容改变监听
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// 内容改变了调用
			// 添加了的字符个数count 删除了的字符个数before
			if (count > 0) { // 字符添加了
				updateCity_re(ed.getText().toString());// 移除ArrayList里面的城市字符串
			} else if (before > 0) {// 字符删除了
				updateCity_add(ed.getText().toString());// 往ArrayList里面添加城市字符串
			}
			arrayAdapter.notifyDataSetChanged();// 刷新Listview控件
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// 编辑前调用
		}

		@Override
		public void afterTextChanged(Editable s) {
			// 最终内容调用
		}
	};

	// Listview item点击监听
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		boolean isExist = isExist(cityArray.get(position));
		if (isExist == false) {
			// 不存在
			Intent intent = new Intent(AddCityActivity.this, CityActivity.class);
			intent.putExtra("cityName", cityArray.get(position));
			setResult(201, intent); // 回传参数
			finish();// 销毁界面
		} else {
			// 存在
			Toast.makeText(AddCityActivity.this, "该城市已添加，请添加其他城市",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isExist(String cityName) {
		for (String cityName2 : cityList) {
			// 查找是否已经添加了该城市
			if (cityName2.equals(cityName)) {
				return true;// 存在
			}
		}
		return false;// 不存在
	}
}
