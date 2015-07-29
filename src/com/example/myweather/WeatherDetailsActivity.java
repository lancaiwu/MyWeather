package com.example.myweather;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherDetailsActivity extends Activity {
	private ArrayList<String> dataDetailsList = null;
	private TextView tvchuanyi;
	private TextView tvshushidu;
	private TextView tvcyzsc;
	private TextView tvxc;
	private TextView tvxczs;
	private TextView tvxcssdc;
	private TextView tvly;
	private TextView tvlyssd;
	private TextView tvlyzsc;
	private TextView tvgm;
	private TextView tvgmssd;
	private TextView tvgmzsc;
	private TextView tvyd;
	private TextView tvydssd;
	private TextView tvydzsc;
	private TextView tvzwx;
	private TextView tvzwxssd;
	private TextView tvzwxzsc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weatherdetails_activity);
		dataDetailsList = getIntent()
				.getStringArrayListExtra("dataDetailsList");
		init();
		if (dataDetailsList == null || dataDetailsList.isEmpty()) {
			Toast.makeText(this, "暂无数据，请刷新重试！", Toast.LENGTH_LONG).show();
		} else {
			showData();
		}
	}

	private void init() {
		tvchuanyi = (TextView) findViewById(R.id.tvchuanyi);
		tvshushidu = (TextView) findViewById(R.id.tvshushidu);
		tvcyzsc = (TextView) findViewById(R.id.tvcyzsc);
		tvxc = (TextView) findViewById(R.id.tvxc);
		tvxczs = (TextView) findViewById(R.id.tvxczs);
		tvxcssdc = (TextView) findViewById(R.id.tvxcssdc);
		tvly = (TextView) findViewById(R.id.tvly);
		tvlyssd = (TextView) findViewById(R.id.tvlyssd);
		tvlyzsc = (TextView) findViewById(R.id.tvlyzsc);
		tvgm = (TextView) findViewById(R.id.tvgm);
		tvgmssd = (TextView) findViewById(R.id.tvgmssd);
		tvgmzsc = (TextView) findViewById(R.id.tvgmzsc);
		tvyd = (TextView) findViewById(R.id.tvyd);
		tvydssd = (TextView) findViewById(R.id.tvydssd);
		tvydzsc = (TextView) findViewById(R.id.tvydzsc);
		tvzwx = (TextView) findViewById(R.id.tvzwx);
		tvzwxssd = (TextView) findViewById(R.id.tvzwxssd);
		tvzwxzsc = (TextView) findViewById(R.id.tvzwxzsc);
	}

	private void showData() {
		tvchuanyi.setText(dataDetailsList.get(0));
		tvshushidu.setText(dataDetailsList.get(1));
		tvcyzsc.setText(dataDetailsList.get(3));
		tvxc.setText(dataDetailsList.get(4));
		tvxczs.setText(dataDetailsList.get(5));
		tvxcssdc.setText(dataDetailsList.get(7));
		tvly.setText(dataDetailsList.get(8));
		tvlyssd.setText(dataDetailsList.get(9));
		tvlyzsc.setText(dataDetailsList.get(11));
		tvgm.setText(dataDetailsList.get(12));
		tvgmssd.setText(dataDetailsList.get(13));
		tvgmzsc.setText(dataDetailsList.get(15));
		tvyd.setText(dataDetailsList.get(16));
		tvydssd.setText(dataDetailsList.get(17));
		tvydzsc.setText(dataDetailsList.get(19));
		tvzwx.setText(dataDetailsList.get(20));
		tvzwxssd.setText(dataDetailsList.get(21));
		tvzwxzsc.setText(dataDetailsList.get(23));
	}
}
