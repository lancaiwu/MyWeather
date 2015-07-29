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
	private ArrayList<String> cityArrayBackups = null; // ���ݳ����б�
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
		findCityListView.setOnItemClickListener(this); // item����
		ed = (EditText) findViewById(R.id.edfindCity);
		ed.addTextChangedListener(textWatcher);// �ı��޸ļ���

		btnCloseAddCity = (Button) findViewById(R.id.btncloseAddCity);
		btnCloseAddCity.setOnClickListener(new OnClickListener() {
			// �رհ�ť����
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddCityActivity.this,
						CityActivity.class);
				setResult(202, intent); // �ش�����
				finish();// ���ٽ���
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

	private void updateCity_re(String cityName) { // �Ƴ������������ĳ���
		Iterator<String> iter = cityArray.iterator();// ������ģʽ
		while (iter.hasNext()) {
			String name = iter.next();
			if (name.contains(cityName) == false) {
				// ��ǰѭ���ĳ������� ������ ����ĳ�������
				// cityArray.remove(name); ������������ᱨ��
				iter.remove();
			}
		}
		if (cityArray.size() > 0) {
			setCitySort(cityName);// ��������
		}
	}

	private void updateCity_add(String cityName) {// ������������ĳ���
		if (cityName.equals("") || cityName == null) { // �����Ϊ��
			cityArray.clear();
			for (String name : cityArrayBackups) {
				cityArray.add(name);
			}
		} else {
			ArrayList<String> arrayCity = new ArrayList<String>();
			for (String name : cityArrayBackups) {
				if (name.contains(cityName)) {
					if (cityArray.size() > 0) {
						for (String n : cityArray) { // �������б�Ϊ��ʱ
							if (n.equals(name) == false) {// �ж��Ƿ����иó���
								arrayCity.add(name);
							}
						}
					} else {// �������б�Ϊ��ʱ
						arrayCity.add(name);
					}
				}
			}
			cityArray.addAll(arrayCity);// ����µĳ���
			setCitySort(cityName);// ��������
		}
	}

	private void setCitySort(String cityName) {
		// ���� ͵��
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

	// EditText ���ݸı����
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// ���ݸı��˵���
			// ����˵��ַ�����count ɾ���˵��ַ�����before
			if (count > 0) { // �ַ������
				updateCity_re(ed.getText().toString());// �Ƴ�ArrayList����ĳ����ַ���
			} else if (before > 0) {// �ַ�ɾ����
				updateCity_add(ed.getText().toString());// ��ArrayList������ӳ����ַ���
			}
			arrayAdapter.notifyDataSetChanged();// ˢ��Listview�ؼ�
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// �༭ǰ����
		}

		@Override
		public void afterTextChanged(Editable s) {
			// �������ݵ���
		}
	};

	// Listview item�������
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		boolean isExist = isExist(cityArray.get(position));
		if (isExist == false) {
			// ������
			Intent intent = new Intent(AddCityActivity.this, CityActivity.class);
			intent.putExtra("cityName", cityArray.get(position));
			setResult(201, intent); // �ش�����
			finish();// ���ٽ���
		} else {
			// ����
			Toast.makeText(AddCityActivity.this, "�ó�������ӣ��������������",
					Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isExist(String cityName) {
		for (String cityName2 : cityList) {
			// �����Ƿ��Ѿ�����˸ó���
			if (cityName2.equals(cityName)) {
				return true;// ����
			}
		}
		return false;// ������
	}
}
