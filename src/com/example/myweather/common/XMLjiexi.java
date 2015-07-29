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
	 * ����xml����
	 * @param city ����
	 * @return dataList ��������
	 */
	public static ArrayList<String> getWeather(String city) {
		// ����XML�ļ� dom4j
		ArrayList<String> dataList=new ArrayList<String>();
		String URL = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ city + "&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
		Log.i("url", URL);
		String weatherStr = HttpUtil.sendPostMathod(URL, "GB2312");
		try {
			// ���ַ���ת��ΪDocument����
			Document document = DocumentHelper.parseText(weatherStr);
			// ���xml�ĸ�Ŀ¼���
			Element rootElement = document.getRootElement();
			// ���������ȡ״̬
			Element statusElement = rootElement.element("status");

			if (statusElement.getText().equals("success")) {// �ж��Ƿ�������ȡ�ɹ�
				// ����Լ����ӽڵ�date�ڵ�
				Element dateElement = rootElement.element("date");
				// ���date�ڵ���ı�
				String date = dateElement.getText();
				dataList.add(date);
				// ����Լ����ӽڵ�results�ڵ�
				Element resultsElement = rootElement.element("results");
				// ����Լ����ӽڵ�currentCity�ڵ�
				Element currentCityElement = resultsElement
						.element("currentCity");
				// ��ó�������
				dataList.add(currentCityElement.getText());
				
				// ����������ݽڵ�weather_data�ڵ�
				Element weather_dataElement = resultsElement
						.element("weather_data");
				// ���weather_dataElement�ڵ�������ӽڵ�
				Iterator iterator_weather = weather_dataElement
						.elementIterator();

				while (iterator_weather.hasNext()) {// ѭ��weather_dataElement��Ԫ��
					Element ele = (Element) iterator_weather.next();
					dataList.add(ele.getText());
				}

				// ����������ݽڵ�index�ڵ�
				Element indexElement = resultsElement.element("index");
				// ���index�ڵ�������ӽڵ�
				Iterator iterator_index = indexElement.elementIterator();
				while (iterator_index.hasNext()) {// ѭ��index��Ԫ��
					Element ele = (Element) iterator_index.next();
					dataList.add(ele.getText());
				}

				// pm2.5����
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
