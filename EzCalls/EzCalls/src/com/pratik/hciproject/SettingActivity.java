/**
 * 
 */
package com.pratik.hciproject;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.pratik.hciproject.common.Constants;

/**
 * @author pratiksomanagoudar
 *
 */
public class SettingActivity extends PreferenceActivity {

	private Context context;
	private SharedPreferences prefs;
	private EditTextPreference CustomLocationPref;
	private EditTextPreference CustomTimePref;
	private Context ctx;
	static boolean isChanged;
	static boolean isUpdateIntervalChanged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context= getApplicationContext();
		ctx= this;
		isUpdateIntervalChanged = false;
		isChanged= false;
		addPreferencesFromResource(R.xml.setting);
		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {

				System.out.println("This is called!!"+key);
				if(key.equals(Constants.TIME_CRITERIA_KEY)){
					Toast.makeText(context, "Time Interval Saved.", Toast.LENGTH_SHORT).show();
				}
				if(key.equals(Constants.LOCATION_CRITERIA_KEY)){
					Toast.makeText(context, "Distance Setting Saved.", Toast.LENGTH_SHORT).show();
				}
				if(key.equals(Constants.CUSTOM_TIME_CRITERIA_KEY)){
					Toast.makeText(context, "Time Interval Saved.", Toast.LENGTH_SHORT).show();
				}
				if(key.equals(Constants.CUSTOM_LOCATION_CRITERIA_KEY)){
					Toast.makeText(context, "Distance Setting Saved.", Toast.LENGTH_SHORT).show();
				}
				
				if(key.equals(Constants.UPDATE_INTERVAL)){
					Toast.makeText(context, "Update Interval Saved.", Toast.LENGTH_SHORT).show();
					isUpdateIntervalChanged=true;
				}
				isChanged=true;
				
				
			}

		});
		
		CustomLocationPref = (EditTextPreference) getPreferenceScreen().findPreference(Constants.CUSTOM_LOCATION_CRITERIA_KEY);

		CustomLocationPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean rtnval = true;
                if (Integer.parseInt((String)newValue)>30000) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Input is too large");
                    builder.setMessage("Kindly enter a value less than 30000 meteres.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                }
                return rtnval;
            }

			
        });
		
		CustomTimePref = (EditTextPreference) getPreferenceScreen().findPreference(Constants.CUSTOM_TIME_CRITERIA_KEY);

		CustomTimePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean rtnval = true;
                if (Integer.parseInt((String)newValue)>720) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setTitle("Input is too large");
                    builder.setMessage("Kindly enter a value less than 720 minutes.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                }
                return rtnval;
            }

			
        });
	}

	@Override
	protected void onPause() {


		Thread t3 = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if(isUpdateIntervalChanged){
					if(prefs.getBoolean(Constants.CUSTOM_UPDATE_INTERVAL_FLAG, false)){
						Log.d("HCI PROJECT", "Loading custom update interval");
						Long time= Long.parseLong(prefs
								.getString(Constants.CUSTOM_UPDATE_INTERVAL, "600000"));
						time=time*60000;
						Log.d("HCI PROJECT", "Loading custom update interval : "+time);

						Log.d("HCI-PROJECT", "Registering Alarm Manager");
						Intent intent = new Intent(getApplicationContext(),
								ProcessingService.class);
						IntroActivity.pintent = PendingIntent.getService(context, 0, intent, 0);

						IntroActivity.alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

						IntroActivity.alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
								.getInstance().getTimeInMillis(),time,
								IntroActivity.pintent);
						isUpdateIntervalChanged=false;
					}
					else{
					Log.d("HCI-PROJECT", "Registering Alarm Manager");
					Intent intent = new Intent(getApplicationContext(),
							ProcessingService.class);
					IntroActivity.pintent = PendingIntent.getService(context, 0, intent, 0);

					IntroActivity.alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

					IntroActivity.alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
							.getInstance().getTimeInMillis(), Long.parseLong(prefs
							.getString(Constants.UPDATE_INTERVAL, "600000")),
							IntroActivity.pintent);
					System.out.println(prefs.getString(Constants.UPDATE_INTERVAL,
							
							"600000"));
					isUpdateIntervalChanged=false;
					}			
					
				}
				else{
					Intent serviceIntent = new Intent(getApplicationContext(), ProcessingService.class);
					serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
					startService(serviceIntent);
				}
			}
		};
		t3.start();
		if(isChanged){
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			int count = prefs.getInt("SettingSaves", 0);
			count=count+1;
			prefs.edit().putInt("SettingSaves", count).commit();
			Log.d("PROJECT STATS", " NO of setting saves made via EZ CALL"+count);
			SettingActivity.isChanged=false;
		}

		super.onPause();
	}





}
