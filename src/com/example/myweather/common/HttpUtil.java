package com.example.myweather.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;



import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.util.Log;

public class HttpUtil {
	private static String URL = "http://api.map.baidu.com/telematics/v3/weather?location=%E6%B7%B1%E5%9C%B3&output=xml&ak=A72e372de05e63c8740b2622d0ed8ab1";
	public static boolean isUtf = true;
	public static String Encoding = "utf-8";
	public static DefaultHttpClient getHttpClient()
	{
		HttpParams httpParams = new BasicHttpParams();
		//设置连接超时和获取超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		return new DefaultHttpClient(httpParams);
	}

	/**
	 * 
	 * @param url
	 *            url地址
	 * @param httpClient
	 *            httpClient
	 * @param setHeader
	 *            setHeader
	 * @return String
	 * @throws IOException
	 */
	public static String getUrl(String url, DefaultHttpClient httpClient,
			String setHeader) throws IOException {
		HttpGet request = new HttpGet(url);

		Log.i("test", url + "获得链接");

		request.setHeader("Referer", setHeader);
		HttpResponse response = httpClient.execute(request);
		Header[] headers = response.getAllHeaders();

		Log.i("test", "返回头");
		for (Header header : headers) {
			Log.i("test", header.toString());
		}

		System.out.println("");
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			System.out.println("成功返回");
			if (isUtf){
				return EntityUtils.toString(response.getEntity(),
						Encoding);
			}else {
				isUtf = true;
				return EntityUtils.toString(response.getEntity(), "GB2312");
			}
		} else {
			return "";
		}
	}


	public static byte[] getUrl_byte(String url, DefaultHttpClient httpClient,
			String setHeader) throws IOException {
		HttpGet request = new HttpGet(url);
		request.setHeader("Referer", setHeader);
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toByteArray(response.getEntity());
		} else {
			return null;
		}
	}


	public static File doGetFile(DefaultHttpClient httpclient, String url,
			String filePath) throws KeyManagementException,
			NoSuchAlgorithmException, ClientProtocolException, IOException {
		// CloseableHttpClient httpclient = createHttpsClient();
		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpGet);

		HttpEntity entity = response.getEntity();

		InputStream in = entity.getContent();

		// 包装 并读出流信息
		BufferedInputStream bis = new BufferedInputStream(in);
		File file = new File(filePath);
		FileOutputStream fs = new FileOutputStream(file);

		byte[] buf = new byte[1024];
		int len = bis.read(buf);
		if (len == -1 || len == 0) {
			file.delete();
			file = null;
		}
		while (len != -1) {
			fs.write(buf, 0, len);
			len = bis.read(buf);
		}
		fs.flush();
		fs.close();
		return file;

	}

	public static String postUrlSim(String url, List<BasicNameValuePair> pairs,
			DefaultHttpClient httpClient, String setHeader)
			throws ClientProtocolException, IOException {

		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		request.setHeader("Referer", setHeader);
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity());
		} else {
			return null;
		}

	}

	/**
	 * post提交数据
	 * 
	 * @param url
	 *            提交地址
	 * @param pairs
	 *            参数
	 * @param httpClient
	 *            httpClient
	 * @param setHeader
	 *            Header
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IOException
	 */

	public static String postUrl(String url, List<BasicNameValuePair> pairs,
			DefaultHttpClient httpClient, String setHeader)
			throws ClientProtocolException, IOException {

		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		try {
			@SuppressWarnings("unused")
			URI uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		request.setHeader("Referer", setHeader);

		request.getParams().setParameter(
				"http.protocol.allow-circular-redirects", false);
		request.getParams().setParameter("http.protocol.max-redirects", 10);

		MyRedirectHandler redirect = new MyRedirectHandler();
		httpClient.setRedirectHandler(redirect);
		httpClient.getParams().setParameter(
				"http.protocol.allow-circular-redirects", false);

		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000); // 设置请求超时时间
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				10000); // 读取超时

		HttpResponse response = null;
		try {
			response = httpClient.execute(request);

		} catch (Exception e) {
			e.printStackTrace();
		}

		int statuscode = response.getStatusLine().getStatusCode();

		if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)
				|| (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (statuscode == HttpStatus.SC_SEE_OTHER)
				|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			// 读取新的 URL 地址
			Log.i("test", "得到链接");
			Header locationHeader = response.getFirstHeader("Location");
			if (locationHeader == null) {
				return null;
			}

			return locationHeader.getValue();
		}

		else if (statuscode == HttpStatus.SC_OK) {
			return EntityUtils.toString(response.getEntity());
		} else {
			return null;
		}

	}

	static class MyRedirectHandler implements RedirectHandler {

		@Override
		public boolean isRedirectRequested(HttpResponse response,
				HttpContext context) {
			return false;
		}

		@Override
		public URI getLocationURI(HttpResponse response, HttpContext context)
				throws ProtocolException {

			Header locationHeader = response.getFirstHeader("Location");
			if (locationHeader == null) {
				return null;
			}

			URI uri = null;
			try {
				uri = new URI(locationHeader.getValue());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			return uri;
		}

	}
	
	//cjb 从服务器获取数据的方法
	public static String sendPostMathod(String url,String code){
		String result = "";
		HttpClient httpClient = HttpUtil.getHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			HttpResponse response = httpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				result = EntityUtils.toString(response.getEntity(),code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
}
