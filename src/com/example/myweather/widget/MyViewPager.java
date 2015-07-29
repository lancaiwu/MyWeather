package com.example.myweather.widget;

import com.example.myweather.R;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MyViewPager extends ViewPager {
	private View viewpager_header = null;

	/*
	 * public MyViewPager(Context context) { super(context); // TODO
	 * Auto-generated constructor stub }
	 */

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		viewpager_header = LinearLayout.inflate(context,
				R.layout.viewpager_header, null);
		
	}

}
