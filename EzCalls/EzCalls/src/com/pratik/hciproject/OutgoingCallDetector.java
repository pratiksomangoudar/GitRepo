package com.pratik.hciproject;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.pratik.hciproject.db.CallRecord;
import com.pratik.hciproject.db.SQLiteDataHepler;

public class OutgoingCallDetector extends BroadcastReceiver {

	private double latitude;
	private double longitude;
	private SQLiteDataHepler sqlData;
	private boolean isLocationNull;


	@Override
	public void onReceive(Context context, Intent intent) {


		sqlData =  new SQLiteDataHepler(context);
		// TODO Auto-generated method stub

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.d("HCI PROJECT", "CALL DETECTED _____________________");

			logCallLogs(number,context);
		}

	}
	public void logCallLogs(String phoneNumber, Context context) {

		CallRecord callRecord= new CallRecord();
		callRecord.setPhone_number(phoneNumber);
		callRecord.setDatetime(new Date());
		callRecord.setCall_duration("0");
		callRecord= fetchContactIdFromPhoneNumber(callRecord, context);



		getCurrentLocation(context);
		callRecord.setLatitude(""+latitude);
		callRecord.setLongitude(""+longitude);
		System.out.println("HERE");
		if(callRecord!=null && callRecord.getContact_name()!=null && !(callRecord.getContact_name().equals("") )){
			sqlData.open();
			if(isLocationNull){
				System.out.println(sqlData.insertTimeData(callRecord)+callRecord.getContact_name()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
			else{
				System.out.println(sqlData.insertLocationData(callRecord)+callRecord.getContact_name()+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
			sqlData.close();
		}
		isLocationNull=false;
		Intent serviceIntent = new Intent(context, ProcessingService.class);
		serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
		context.startService(serviceIntent);
	}
	private void getCurrentLocation(Context context) {
		// TODO Auto-generated method stub

		try{
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			final Criteria criteria = new Criteria();

			// criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			// criteria.setAltitudeRequired(false);
			// criteria.setBearingRequired(false);
			// criteria.setCostAllowed(true);
			// criteria.setPowerRequirement(Criteria.POWER_LOW);

			String provider = locationManager.getBestProvider(criteria, false);
			LocationListener loc_listener = new LocationListener() {
				public void onLocationChanged(Location l) {
				}

				public void onProviderEnabled(String p) {
				}

				public void onProviderDisabled(String p) {
				}



				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
					// TODO Auto-generated method stub

				}      
			};
			locationManager.requestLocationUpdates(provider,0 ,0, loc_listener);
			Location location = locationManager.getLastKnownLocation(provider);


			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}catch(NullPointerException e){
			isLocationNull = true;
			latitude = -90.0;
			longitude = -1.0;
			Log.d("HCI PROJECT", "LOCATION IS NULL, PUTTING SOUTH POLE COORDINATES");
		}

		System.out.println("LOCATION INFO------" + latitude + "  " + longitude);
	}

	public CallRecord fetchContactIdFromPhoneNumber(CallRecord callRecord,Context context) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(callRecord.getPhone_number()));
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);

		String contactId = "";

		if (cursor.moveToFirst()) {
			do {
				contactId = cursor.getString(cursor
						.getColumnIndex(PhoneLookup._ID));
				String displayName = cursor.getString(cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
				callRecord.setContact_name(displayName);
				callRecord.setId(contactId);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return callRecord;
	}
}