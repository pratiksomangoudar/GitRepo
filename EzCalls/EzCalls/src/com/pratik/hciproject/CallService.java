package com.pratik.hciproject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class CallService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int count = prefs.getInt("CallCount", 0);
		count=count++;
		prefs.edit().putInt("CallCount", count).commit();
		Log.d("PROJECT STATS", " NO of Calls made via EZ CALL"+count);
		Uri number=intent.getData();


		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		callIntent.setData(number);
		this.startActivity(callIntent);		

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
