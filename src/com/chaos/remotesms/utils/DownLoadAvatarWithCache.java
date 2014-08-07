package com.chaos.remotesms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * DownLoadAvatarWithCache: 下载头像图片（圆形），设有sdcard缓存
 * 
 * @author chaos
 * 
 */
public class DownLoadAvatarWithCache extends AsyncTask<String, Void, Bitmap> {

	private ImageView imageView = null;

	public DownLoadAvatarWithCache(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... urls) {
		// TODO Auto-generated method stub
		String url = (String) urls[0];
		Bitmap bitmap = null;
		int fileSize = 0;
		int loopTimes = 0;
		String bitmapName = url.substring(url.lastIndexOf("/") + 1);
		bitmapName = OtherUtils.getFileNameNoEx(bitmapName);
		String dirSrc = "/mnt/sdcard/forkids/";
		String bitmapSrc = dirSrc + bitmapName;
		File cacheDir = new File(dirSrc);
		File bitmapFile = new File(bitmapSrc);
		FileOutputStream fileOutputStream = null;

		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}

		while (true) {
			if (bitmapFile.exists()) {
				try {
					fileSize = new FileInputStream(bitmapFile).available();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (fileSize != 0) { // 如果图像文件存在并且大小不为0，跳出循环
					break;
				} else
					// 如果图像文件存在但大小为0（即为空文件），则删除该文件
					bitmapFile.delete();
			}
			// 创建图像文件
			try {
				bitmapFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 从网络获取资源
			try {
				InputStream inputStream = new URL(url).openStream();
				bitmap = null;
				bitmap = BitmapFactory.decodeStream(inputStream);
				if (null == bitmap) {
					return doInBackground("http://www.baidu.com/img/bdlogo.gif");
				}
			} catch (MalformedURLException e) {
				return doInBackground("http://www.baidu.com/img/bdlogo.gif");
			} catch (IOException e) {
				return doInBackground("http://www.baidu.com/img/bdlogo.gif");
			}

			// 保存图像到文件
			try {
				fileOutputStream = new FileOutputStream(bitmapFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream);
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			loopTimes++;
		}

		if (loopTimes == 0) {
			return BitmapFactory.decodeFile(bitmapSrc);
		} else {
			return bitmap;
		}

		// return (bitmap != null) ? bitmap : null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		imageView.setImageBitmap(ImageUtils.toRoundBitmap(bitmap));
	}

}
