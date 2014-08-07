package com.chaos.remotesms.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class OtherUtils {

	private static List<Map<String, Object>> list = null;
	private static Map<String, Object> map = null;

	/**
	 * getFileNameNoEx: 去除文件名的后缀名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * getBitmapStrBase64: 把Bitmap转换成Base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String getBitmapStrBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		return Base64.encodeToString(bytes, 0);
	}

	/**
	 * getBitmapFromStrBase64: 把Base64转换成Bitmap
	 * 
	 * @param iconBase64
	 * @return
	 */
	public static Bitmap getBitmapFromStrBase64(String iconBase64) {
		byte[] bitmapArray;
		bitmapArray = Base64.decode(iconBase64, 0);
		return BitmapFactory
				.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}

	/**
	 * rawContentToList: 将从edittext内获取的字符串格式的图文转换成图文混排的list
	 * 
	 * @param rawContent
	 * @return
	 */
	public static List<Map<String, Object>> rawContentToList(String rawContent) {
		list = new ArrayList<Map<String, Object>>();
		String string = rawContent;
		String[] strings = string.split("<<<[0-9][0-9]*>>>");
		String[] tempStrings = null;
		String tempString = null;
		for (String s : strings) {
			System.out.println(s);
			map = new HashMap<String, Object>();
			map.put("content", s);
			list.add(map);
			string = string.substring(s.length());
			if (0 == string.length()) {
				break;
			}
			tempStrings = string.split("[^0-9]");
			tempString = tempStrings[3];
			string = string.substring(tempString.length() + 6);
			// System.out.println(tempStrings[3]);
			map = new HashMap<String, Object>();
			map.put("imgIndex", tempStrings[3]);
			list.add(map);
		}
		return list;
	}

	/**
	 * saveToFile: 将可序列化的对象存入制定路径的文件
	 * 
	 * @param path
	 * @param object
	 */
	public static void saveToFile(String path, Object object) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		File f = new File(path);
		try {
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(object); // 括号内参数为要保存java对象
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * loadFromFile: 从制定路径的文件中取出可序列化的对象
	 * 
	 * @param path
	 * @return
	 */
	public static Object loadFromFile(String path) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File f = new File(path);
		if (!f.exists()) {
			return null;
		}
		try {
			fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			Object object = (Object) ois.readObject();// 强制类型转换
			return object;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
