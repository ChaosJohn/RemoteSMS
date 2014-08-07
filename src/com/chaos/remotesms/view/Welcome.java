package com.chaos.remotesms.view;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.chaos.remotesms.R;
import com.chaos.remotesms.utils.ChildThread;
import com.chaos.remotesms.utils.ChildThread.WorkForMain;
import com.chaos.remotesms.utils.OtherUtils;

public class Welcome extends Activity {

	private Handler mainHandler = null, childHandler = null;
	private ChildThread childThread = null;
	/*
	 * MSG_xxx: 线程操作的消息
	 */
	final int MSG_GET = 0;
	final int MSG_UPLOAD = 1;
	final int MSG_ERROR = -1;
	final int MSG_DOWNLOAD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// Toast.makeText(getApplicationContext(), getSmsInPhone(),
		// Toast.LENGTH_LONG).show();

		mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MSG_GET == msg.what) {
					OtherUtils.saveToFile("/sdcard/remotesms/file1.txt",
							msg.obj);
					Toast.makeText(getApplicationContext(), "saved",
							Toast.LENGTH_LONG).show();
				}
			}
		};

		childThread = new ChildThread(new WorkForMain() {

			@Override
			public void doJob(Message msg) {
				// TODO Auto-generated method stub
				if (MSG_GET == msg.what) {
					Message toMainMessage = mainHandler.obtainMessage(MSG_GET);
					toMainMessage.obj = getSmsInPhone();
					mainHandler.sendMessage(toMainMessage);
				}
			}
		});

		childThread.start();
		childHandler = childThread.getHandler();
		childHandler.sendEmptyMessage(MSG_GET);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	public String getSmsInPhone() {
		final String SMS_URI_ALL = "content://sms/";
		final String SMS_URI_INBOX = "content://sms/inbox";
		final String SMS_URI_SEND = "content://sms/sent";
		final String SMS_URI_DRAFT = "content://sms/draft";

		StringBuilder smsBuilder = new StringBuilder();

		try {
			ContentResolver cr = getContentResolver();
			String[] projection = new String[] { "_id", "address", "person",
					"body", "date", "type" };
			Uri uri = Uri.parse(SMS_URI_ALL);
			Cursor cur = cr.query(uri, projection, null, null, "date desc");

			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;

				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");

				do {
					name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
					smsbody = cur.getString(smsbodyColumn);

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
					date = dateFormat.format(d);

					int typeId = cur.getInt(typeColumn);
					if (typeId == 1) {
						type = "接收";
					} else if (typeId == 2) {
						type = "发送";
					} else {
						type = "";
					}

					smsBuilder.append("[");
					smsBuilder.append(name + ",");
					smsBuilder.append(phoneNumber + ",");
					smsBuilder.append(smsbody + ",");
					smsBuilder.append(date + ",");
					smsBuilder.append(type);
					smsBuilder.append("] ");

					if (smsbody == null)
						smsbody = "";
				} while (cur.moveToNext());
			} else {
				smsBuilder.append("no result!");
			}

			smsBuilder.append("getSmsInPhone has executed!");
		} catch (SQLiteException ex) {
			Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		return smsBuilder.toString();
	}

}
