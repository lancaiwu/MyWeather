package com.example.myweather.common;

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.util.Log;

public class XMLjiexi {
	/**
	 * 解析xml天气
	 * @param city 城市
	 * @return dataList 城市天气
	 */
	public static ArrayList<String> getWeather(String city) {
		// 解析XML文件 dom4j
		ArrayList<String> dataList=new ArrayList<String>();
		String URL = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ city + "&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
		Log.i("url", URL);
		String weatherStr = HttpUtil.sendPostMathod(URL, "GB2312");
		try {
			// 将字符串转换为Document对象
			Document document = DocumentHelper.parseText(weatherStr);
			// 获得xml的根目录结点
			Element rootElement = document.getRootElement();
			// 获得天气获取状态
			Element statusElement = rootElement.element("status");

			if (statusElement.getText().equals("success")) {// 判断是否天气获取成功
				// 获得自己的子节点date节点
				Element dateElement = rootElement.element("date");
				// 获得date节点的文本
				String date = dateElement.getText();
				dataList.add(date);
				// 获得自己的子节点results节点
				Element resultsElement = rootElement.element("results");
				// 获得自己的子节点currentCity节点
				Element currentCityElement = resultsElement
						.element("currentCity");
				// 获得城市名字
				dataList.add(currentCityElement.getText());
				
				// 获得天气数据节点weather_data节点
				Element weather_dataElement = resultsElement
						.element("weather_data");
				// 获得weather_dataElement节点的所有子节点
				Iterator iterator_weather = weather_dataElement
						.elementIterator();

				while (iterator_weather.hasNext()) {// 循环weather_dataElement子元素
					Element ele = (Element) iterator_weather.next();
					dataList.add(ele.getText());
				}

				// 获得天气数据节点index节点
				Element indexElement = resultsElement.element("index");
				// 获得index节点的所有子节点
				Iterator iterator_index = indexElement.elementIterator();
				while (iterator_index.hasNext()) {// 循环index子元素
					Element ele = (Element) iterator_index.next();
					dataList.add(ele.getText());
				}

				// pm2.5数据
				Element pm25Element=resultsElement.element("pm25");
				dataList.add(pm25Element.getText());
				
				String st = "";
				for (int i = 0; i < dataList.size(); i++) {
					st = st + "    " + dataList.get(i)+i;
					
				}
				Log.i("st", st);
			} 
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
}
