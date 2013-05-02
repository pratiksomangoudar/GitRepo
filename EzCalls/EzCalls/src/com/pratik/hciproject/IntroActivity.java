package com.pratik.hciproject;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pratik.hciproject.common.Constants;
import com.pratik.hciproject.db.CallRecord;
import com.pratik.hciproject.db.SQLiteDataHepler;

public class IntroActivity extends Activity {

	private static SQLiteDataHepler sqlData;
	private SharedPreferences prefs;
	private ProgressDialog pd;
	static AlarmManager alarm;
	static PendingIntent pintent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.pd = ProgressDialog.show(this, "Working..",
				"Fetching Call logs...", true, false);
		setContentView(R.layout.activity_intro);
		final Context context = getApplicationContext();
		sqlData = new SQLiteDataHepler(context);

		Log.d("HCI PROJECT", "Activity started.......");
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("firstRun", true)) {
			Log.d("HCI PROJECT", "FIRST RUN--- INSIDE PREF BLOCK");
			Thread t1 = new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					Log.d("HCI PROJECT", "GET PREVIOUS CALL DETAILS");
					getCalledDetails();
				}

			};

			t1.start();

			try {
				t1.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			prefs.edit().putBoolean("firstRun", false).commit();
		}
		Thread t2 = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(prefs.getBoolean(Constants.CUSTOM_UPDATE_INTERVAL_FLAG, false)){
					Log.d("HCI PROJECT", "Loading custom update interval");
					Long time= Long.parseLong(prefs
							.getString(Constants.CUSTOM_UPDATE_INTERVAL, "600000"));
					time=time*60000;
					Log.d("HCI PROJECT", "Loading custom update interval : "+time);

					Log.d("HCI-PROJECT", "Registering Alarm Manager");
					Intent intent = new Intent(getApplicationContext(),
							ProcessingService.class);
					pintent = PendingIntent.getService(context, 0, intent, 0);

					alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

					alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
							.getInstance().getTimeInMillis(),time,
							pintent);
					
				}
				else{
				Log.d("HCI-PROJECT", "Registering Alarm Manager");
				Intent intent = new Intent(getApplicationContext(),
						ProcessingService.class);
				pintent = PendingIntent.getService(context, 0, intent, 0);

				alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

				alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
						.getInstance().getTimeInMillis(), Long.parseLong(prefs
						.getString(Constants.UPDATE_INTERVAL, "600000")),
						pintent);
				System.out.println(prefs.getString(Constants.UPDATE_INTERVAL,
						"600000"));
				}
			}
		};
		t2.start();

		pd.dismiss();

		Button setting = (Button) findViewById(R.id.setting_button);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("HCI PROJECT", "LOADING PREF SETTINGS");
				Intent i = new Intent(context, SettingActivity.class);
				startActivity(i);
			}
		});
		
		Button changeColorButton=  (Button) findViewById(R.id.colorchange_button);
		changeColorButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("HCI PROJECT", "LOADING  CHANGE COLOR ACTIVITY");
				Intent i = new Intent(context, ChangeColorActivity.class);
				startActivity(i);				
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		if(SettingActivity.isChanged){
//		
//		Thread t3 = new Thread() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				super.run();
////		Log.d("HCI PROJECT", "IntroActivity onResume--");
////		Intent serviceIntent = new Intent(getApplicationContext(), ProcessingService.class);
////		serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
////		startService(serviceIntent);
////		if (SettingActivity.isUpdateIntervalChanged) {
//			Intent intent = new Intent(getApplicationContext(),
//					ProcessingService.class);
//			pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
//			Log.d("HCI PROJECT",
//					"IntroActivity onResume--Registering New Alarm Manager");
//			alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//			SharedPreferences prefs = PreferenceManager
//					.getDefaultSharedPreferences(getApplicationContext());
//			alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance()
//					.getTimeInMillis(), Long.parseLong(prefs.getString(
//					Constants.UPDATE_INTERVAL, "600000")), pintent);
//			SettingActivity.isUpdateIntervalChanged = false;
//			
////		}
//			}
//		};
//		t3.start();
//		SettingActivity.isChanged=false;
//		prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		int count = prefs.getInt("SettingSaves", 0);
//		count=count+1;
//		prefs.edit().putInt("SettingSaves", count).commit();
//		Log.d("PROJECT STATS", " NO of setting saves made via EZ CALL"+count);
//		
//		}
	}

	private void getCalledDetails() {
		Cursor mCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
				null, null, null, null);
		int name = mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
		int id = mCursor.getColumnIndex(CallLog.Calls._ID);
		int number = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = mCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = mCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = mCursor.getColumnIndex(CallLog.Calls.DURATION);
		sqlData.open();
		while (mCursor.moveToNext()) {

			String contact_name = mCursor.getString(name);
			String contact_Id = mCursor.getString(id);
			String ph_Number = mCursor.getString(number);
			String call_Type = mCursor.getString(type);
			String call_Date = mCursor.getString(date);
			Date call_DayTime = new Date(Long.valueOf(call_Date));
			String call_Duration = mCursor.getString(duration);
			int dircode = Integer.parseInt(call_Type);
			if (dircode == CallLog.Calls.OUTGOING_TYPE) {
				if (contact_name != null) {

					CallRecord callRecord = new CallRecord();
					callRecord.setId(contact_Id);
					callRecord.setContact_name(contact_name);
					callRecord.setPhone_number(ph_Number);
					callRecord.setDatetime(call_DayTime);
					callRecord.setCall_duration(call_Duration);

					System.out.println(sqlData.smartInsertTimeData(callRecord)
							+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

				}
			}
		}
		sqlData.close();
		mCursor.close();
		Log.d("HCI-PROJECT", "Service Call");
		Intent serviceIntent = new Intent(this, ProcessingService.class);
		serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
		startService(serviceIntent);

	}

}
