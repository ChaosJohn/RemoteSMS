package com.chaos.remotesms.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import android.util.Log;

/**
 * NetUtils: 网络资源收发工具包（定义了get数据和post数据）
 * 
 * @author chaos
 * 
 */
public class NetUtils {

	/**
	 * getDataByUrl: 从uri获取数据，auth为权限字符串"username:password"
	 * 
	 * @param uri
	 * @param auth
	 * @return
	 */
	public static String getDataByUrl(String uri, String auth) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("Accept", "application/json");
		httpGet.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP));
		HttpResponse response;
		String content = "err";
		try {
			response = httpClient.execute(httpGet/* , localContext */);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				Log.i("log", "200");
				String rev = EntityUtils.toString(response.getEntity());
				CookieStore mCookieStore = ((AbstractHttpClient) httpClient)
						.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					// 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
					if ("JSESSIONID".equals(cookies.get(i).getName())) {
						// Toast.makeText(context, "ok\n" +
						// cookies.get(i).getValue() ,
						// Toast.LENGTH_LONG).show();
						break;
					}

				}
				content = rev;
				if (content.startsWith("<?")) {
					content = "err";
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	/**
	 * postDataByUrl: 向uri提交数据，auth为权限字符串"username:password"
	 * 
	 * @param uri
	 * @param dataString
	 * @param auth
	 * @return
	 */
	public static String postDataByUrl(String uri, String dataString,
			String auth) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(uri);
		HttpResponse response;
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("charset", HTTP.UTF_8);
		httpPost.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP));
		try {
			StringEntity entity = new StringEntity(dataString, HTTP.UTF_8);
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			int code = -1;
			code = response.getStatusLine().getStatusCode();
			// System.out.println(code);
			String returnString = EntityUtils.toString(response.getEntity());
			// System.out.println(returnString);
			if (201 == code) {
				return (returnString.startsWith("<?")) ? "err" : returnString;
			}
			return "err";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "err";
	}

	/**
	 * modifyDataByUrl: 向uri提交数据进行更改，auth为权限字符串"username:password"
	 * 
	 * @param uri
	 * @param dataString
	 * @param auth
	 * @return
	 */
	public static String modifyDataByUrl(String uri, String dataString,
			String auth) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(uri);
		HttpResponse response;
		httpPut.addHeader("Content-Type", "application/json");
		httpPut.addHeader("Accept", "application/json");
		httpPut.addHeader("charset", HTTP.UTF_8);
		httpPut.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP));
		try {
			StringEntity entity = new StringEntity(dataString, HTTP.UTF_8);
			httpPut.setEntity(entity);
			response = httpClient.execute(httpPut);
			int code = -1;
			code = response.getStatusLine().getStatusCode();
			// System.out.println(code);
			String returnString = EntityUtils.toString(response.getEntity());
			// System.out.println(returnString);
			if (200 == code) {
				return (returnString.startsWith("<")) ? "err" : returnString;
			}
			return "err";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "err";
	}

}
