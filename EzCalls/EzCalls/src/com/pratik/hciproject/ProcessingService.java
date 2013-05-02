package com.pratik.hciproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.pratik.hciproject.common.Constants;
import com.pratik.hciproject.common.ContactDetail;
import com.pratik.hciproject.db.CallRecord;
import com.pratik.hciproject.db.SQLiteDataHepler;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.RemoteViews;

public class ProcessingService extends Service {

	private SQLiteDataHepler sqlData;
	private ArrayList<CallRecord> callRecordsArray;
	private ArrayList<CallRecord> resultSet;
	private Context context;
	private static ArrayList<String> mostProbableCalled;
	private double latitude;
	private double longitude;
	public static ArrayList<ContactDetail> mostProbableContacts;
	public static final String IMAGE_BUTTON_1_ACTION_CLICK = "Button1Click";
	public static final String IMAGE_BUTTON_2_ACTION_CLICK = "Button2Click";
	public static final String IMAGE_BUTTON_3_ACTION_CLICK = "Button3Click";
	public static final String IMAGE_BUTTON_4_ACTION_CLICK = "Button4Click";

	private int[] imageButtons = { R.id.imageButton1, R.id.imageButton2,
			R.id.imageButton3, R.id.imageButton4 };
	private int[] textView = { R.id.textView1, R.id.textView2, R.id.textView3,
			R.id.textView4 };
	private int[] contactLayout = { R.id.contactLayout1, R.id.contactLayout2,
			R.id.contactLayout3, R.id.contactLayout4 };

	private SharedPreferences prefs;
	private int timeCriteria;
	private int locationCriteria;
	private boolean isLocationNull;
	private ArrayList<String> duplicationCheckArray;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sqlData = new SQLiteDataHepler(getApplicationContext());
		resultSet = new ArrayList<CallRecord>();
		System.out.println("Before getting records");
		context = getApplicationContext();
		getRecords();
		sqlData.open();
		sqlData.initializeProcessdataTable();
		sqlData.close();


		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(prefs.getBoolean(Constants.CUSTOM_TIME_BASED, false)){
			timeCriteria = Integer.parseInt(prefs.getString(Constants.CUSTOM_TIME_CRITERIA_KEY, "30"));
			timeCriteria=timeCriteria/2;
		}
		else{
			timeCriteria = Integer.parseInt(prefs.getString(Constants.TIME_CRITERIA_KEY, "30"));
		}


		if(prefs.getBoolean(Constants.CUSTOM_LOCATION_BASED, false)){
			locationCriteria= Integer.parseInt(prefs.getString(Constants.CUSTOM_LOCATION_CRITERIA_KEY, "500"));		
		}
		else{
			locationCriteria= Integer.parseInt(prefs.getString(Constants.LOCATION_CRITERIA_KEY, "500"));	
		}



		System.out.println("Records Got $$$" + callRecordsArray.size() + "--------"+timeCriteria+"-----"+locationCriteria);

	}

	private void getRecords() {
		// TODO Auto-generated method stub
		try {
			sqlData.open();
			callRecordsArray = (ArrayList<CallRecord>) sqlData.getCallRecords();
			sqlData.close();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getRecords();
		boolean islocation= prefs.getBoolean(Constants.LOCATION_BASED, true);
		boolean istime = prefs.getBoolean(Constants.TIME_BASED, true);
		Log.d("HCI PROJECT", "LOCATION Setting ---"+islocation+"\n TIME Setting --- "+istime);



		if (islocation
				&& istime) {

			ArrayList<String> locationTimeResult = getResultLocationTimeBased();

			if (locationTimeResult == null || locationTimeResult.size() < 4) {

				ArrayList<String> temp1 = getResultLocationBased();
				locationTimeResult = addToResult(locationTimeResult, temp1);

				if (locationTimeResult == null || locationTimeResult.size() < 4) {

					ArrayList<String> temp2 = getResultTimeBased();
					locationTimeResult = addToResult(locationTimeResult, temp2);

					if (locationTimeResult == null
							|| locationTimeResult.size() < 4) {
						ArrayList<String> temp3 = getResultGeneralBased();
						locationTimeResult = addToResult(locationTimeResult,
								temp3);
					}
				}
			}
			Log.d("DEBUGGING AK", "##########################"
					+ locationTimeResult.size());

			if (locationTimeResult.size() > 3) {

				updateResult(locationTimeResult);
			}

		}
		else if (islocation
				&& !istime) {
			ArrayList<String> locationTimeResult = getResultLocationBased();

			if (locationTimeResult == null || locationTimeResult.size() < 4) {

				ArrayList<String> temp2 = getResultTimeBased();
				locationTimeResult = addToResult(locationTimeResult, temp2);

				if (locationTimeResult == null || locationTimeResult.size() < 4) {
					ArrayList<String> temp3 = getResultGeneralBased();
					locationTimeResult = addToResult(locationTimeResult, temp3);
				}
			}
			if (locationTimeResult.size() > 3) {
				updateResult(locationTimeResult);
			}

		}
		else if (istime
				&& !islocation) {

			ArrayList<String> locationTimeResult = getResultTimeBased();

			if (locationTimeResult == null || locationTimeResult.size() < 4) {
				ArrayList<String> temp3 = getResultGeneralBased();
				locationTimeResult = addToResult(locationTimeResult, temp3);
			}
			if (locationTimeResult.size() > 3) {
				updateResult(locationTimeResult);
			}

		}
		else{
			ArrayList<String> locationTimeResult = getResultGeneralBased();
			if (locationTimeResult.size() > 3) {
				updateResult(locationTimeResult);
			}
		}
		mostProbableContacts = new ArrayList<ContactDetail>();
		duplicationCheckArray = new ArrayList<String>();
		for (String key : mostProbableCalled) {
			boolean check=checkDuplication(key);
			if(check)
				continue;

			//	Log.d("DEBUGGER ----key", key);
			ContactDetail contact = new ContactDetail();
			contact.setId(fetchContactIdFromPhoneNumber(duplicationCheckArray.get(duplicationCheckArray.size()-1), context));
			sqlData.open();
			contact = sqlData.getColorForContact(contact);
			Log.d("DEBUGGING", contact.getId());
			if(contact.getId().equals(""))
				continue;
			Uri uri = getPhotoUri((Long.parseLong(contact.getId())), context);
			if (uri != null)
				contact.setImageURI(uri.toString());
			else {
				Log.d("HCI PROJECT", "Null URI------- Replacing with app Image");
				Uri path = Uri
						.parse("android.resource://com.pratik.hciproject/"
								+ R.drawable.blank);
				contact.setImageURI(path.toString());
			}
			contact.setNumber(key);

			contact = getFirstNamebyID(contact);
			Log.d("HCI PROJECT", "GUYS WHOM YOU CALL ---" + contact.getName());
			mostProbableContacts.add(contact);
			sqlData.close();
		}
		Intent i = new Intent(this, MyWidgetProvider.class);
		i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		// i.putExtra("numbers", mostProbableContacts);
		sendBroadcast(i);

		buildUpdate();
		Log.d("HCI PROJECT", "Service stopping.......");
		stopSelf();
		return super.onStartCommand(intent, flags, startId);

	}

	private boolean checkDuplication(String key) {

		boolean firstCheck = false;
		for (String checked : duplicationCheckArray) {
			firstCheck = PhoneNumberUtils.compare(checked, key);
			if(firstCheck)
				return true;
		}
		duplicationCheckArray.add(key);
		return firstCheck;	
	}

	private void buildUpdate() {
		// TODO Auto-generated method stub
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		;
		for (int i = 0; i < 4; i++) {
			ContactDetail contact = ProcessingService.mostProbableContacts
					.get(i);
			String uriStr = contact.getImageURI();

			if (uriStr.equals("")) {

			} else {

				Uri uri = Uri.parse(uriStr);
				remoteViews.setImageViewUri(imageButtons[i], uri);
			}
			remoteViews.setTextViewText(textView[i], contact.getName());
			remoteViews.setInt(contactLayout[i], "setBackgroundColor",
					contact.getColor());

			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
			PendingIntent configPendingIntent = PendingIntent.getActivity(
					context, 0, callIntent, 0);
			remoteViews.setOnClickPendingIntent(imageButtons[i],
					configPendingIntent);


			//			Intent intent = new Intent(context, CallService.class);
			//		      intent.putExtra("PhoneNo", contact.getNumber());
			//
			//		      PendingIntent pendingIntent = PendingIntent.getService(context,
			//		          0, intent,0);
			//		      remoteViews.setOnClickPendingIntent(imageButtons[i], pendingIntent);
		}
		ComponentName thisWidget = new ComponentName(this,
				MyWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, remoteViews);
	}

	private ArrayList<String> getResultLocationTimeBased() {
		// TODO Auto-generated method stub
		sqlData.open();
		sqlData.initializeProcessdataTable();
		calculateLocationTimeBased();
		for (CallRecord callRecord : resultSet) {

			String phoneNumber = callRecord.getPhone_number();
			String id = this
					.fetchContactIdFromPhoneNumber(phoneNumber, context);
			callRecord.setContact_id(id);

			sqlData.storeInProcessTable(callRecord);

		}
		Log.d("HCI PROJECT", "Getting location time based most probable");
		ArrayList<String> names = sqlData.getMostProbable();
		sqlData.close();
		return names;
	}

	private ArrayList<String> getResultLocationBased() {
		// TODO Auto-generated method stub
		sqlData.open();
		sqlData.initializeProcessdataTable();

		calculateLocationBased();

		for (CallRecord callRecord : resultSet) {

			String phoneNumber = callRecord.getPhone_number();
			String id = this
					.fetchContactIdFromPhoneNumber(phoneNumber, context);
			callRecord.setContact_id(id);

			sqlData.storeInProcessTable(callRecord);

			//			Log.d("DEBUGGING", callRecord.getId());

		}

		Log.d("HCI PROJECT", "Getting location based most probable");
		ArrayList<String> names = sqlData.getMostProbable();
		sqlData.close();
		return names;
	}

	private ArrayList<String> getResultTimeBased() {
		sqlData.open();
		sqlData.initializeProcessdataTable();

		Log.d("HCI PROJECT", "Calculating Time based ......");
		calculateTimeBased();

		for (CallRecord callRecord : resultSet) {

			String phoneNumber = callRecord.getPhone_number();
			String id = this
					.fetchContactIdFromPhoneNumber(phoneNumber, context);
			callRecord.setContact_id(id);

			sqlData.storeInProcessTable(callRecord);
			//			Log.d("DEBUGGING", callRecord.getId());
		}

		Log.d("HCI PROJECT", "Getting time based most probable");
		ArrayList<String> names = sqlData.getMostProbable();
		sqlData.close();
		return names;
	}

	private ArrayList<String> getResultGeneralBased() {

		sqlData.open();
		ArrayList<String> generalnames = sqlData.getMostProbableGeneral();
		sqlData.close();
		Log.d("HCI PROJECT", "No of general names" + generalnames.size());
		return generalnames;
	}

	private void calculateLocationTimeBased() {
		// TODO Auto-generated method stub
		getCurrentLocation(context);
		Date date = new Date();
		SimpleDateFormat sim = new SimpleDateFormat(Constants.SIMPLETIMEFORMAT);

		String datestr = sim.format(date);
		int minutes1 = calculateTimeInMinutes(datestr);


		for (CallRecord key : callRecordsArray) {
			//Log.d("DEBUGGING LOACTION", "*********" + key.getType());
			if (key.getType().equalsIgnoreCase(Constants.COLUMNTYPE_LOCATION)) {

				String time = sim.format(key.getDatetime());
				int minutes2 = calculateTimeInMinutes(time);

				int result = Math.abs(minutes1 - minutes2);
				String strLat = key.getLatitude();
				String strLng = key.getLongitude();
				float result1 = caluculateDistance(strLat, strLng, latitude,
						longitude);
				if(isLocationNull)
					result1=0;
				resultSet = new ArrayList<CallRecord>();
				if (result < timeCriteria
						&& result1 < locationCriteria) {
					key.setTime_diff(result);
					key.setLocation_diff(result1);
					resultSet.add(key);
				}
			}
		}
		isLocationNull=false;
	}

	private void calculateLocationBased() {
		getCurrentLocation(context);
		resultSet = new ArrayList<CallRecord>();
		for (CallRecord key : callRecordsArray) {
			if (key.getType().equalsIgnoreCase(Constants.COLUMNTYPE_LOCATION)) {
				String strLat = key.getLatitude();
				String strLng = key.getLongitude();
				float result = caluculateDistance(strLat, strLng, latitude,
						longitude);
				if(isLocationNull)
					result=0;
				if (result < locationCriteria) {
					key.setTime_diff(0);
					key.setLocation_diff(result);
					resultSet.add(key);
				}
			}
		}
		isLocationNull=false;
	}

	private void calculateTimeBased() {
		Date date = new Date();
		SimpleDateFormat sim = new SimpleDateFormat(Constants.SIMPLETIMEFORMAT);
		resultSet = new ArrayList<CallRecord>();
		String datestr = sim.format(date);
		int minutes1 = calculateTimeInMinutes(datestr);

		for (CallRecord key : callRecordsArray) {
			String time = sim.format(key.getDatetime());
			int minutes2 = calculateTimeInMinutes(time);

			int result = Math.abs(minutes1 - minutes2);

			if (result < timeCriteria) {
				key.setTime_diff(result);
				key.setLocation_diff(0);
				resultSet.add(key);
			}
		}

	}

	private void updateResult(ArrayList<String> locationTimeResult) {

		this.mostProbableCalled = new ArrayList<String>();
		for (int i = 0; i < locationTimeResult.size(); i++) {
			mostProbableCalled.add(locationTimeResult.get(i));
		}
		Log.d("HCI PROJECT",
				"Probable List Updated" + mostProbableCalled.toString());
	}

	private ArrayList<String> addToResult(ArrayList<String> locationTimeResult,
			ArrayList<String> temp) {
		for (int index = 0; index < temp.size(); index++) {

			if (locationTimeResult.contains(temp.get(index))) {
				continue;
			} else {
				Log.d("HCI PROJECT", "Added general contact");
				locationTimeResult.add(temp.get(index));
			}
		}
		return locationTimeResult;
	}

	private static int calculateTimeInMinutes(String d1) {
		// TODO Auto-generated method stub
		String[] time = d1.split(":");
		return Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
	}

	private float caluculateDistance(String latA, String lngA,
			double latitude2, double longitude2) {
		Location locationA = new Location("point A");
		Log.d("HCI PROJECT", "Distance To be calculated " + latA + " " + lngA);
		locationA.setLatitude(Double.parseDouble(latA));
		locationA.setLongitude(Double.parseDouble(lngA));

		Location locationB = new Location("point B");

		locationB.setLatitude(latitude2);
		locationB.setLongitude(longitude2);

		float distance = locationA.distanceTo(locationB);
		return distance;
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

	public String fetchContactIdFromPhoneNumber(String phoneNumber,
			Context context) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);

		String contactId = "";

		if (cursor.moveToFirst()) {
			do {
				contactId = cursor.getString(cursor
						.getColumnIndex(PhoneLookup._ID));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return contactId;
	}

	public Uri getPhotoUri(long contactId, Context context) {
		ContentResolver contentResolver = context.getContentResolver();

		try {
			Cursor cursor = contentResolver
					.query(ContactsContract.Data.CONTENT_URI,
							null,
							ContactsContract.Data.CONTACT_ID
							+ "="
							+ contactId
							+ " AND "

									+ ContactsContract.Data.MIMETYPE
									+ "='"
									+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
									+ "'", null, null);

			if (cursor != null) {
				if (!cursor.moveToFirst()) {
					cursor.close();
					return null; // no photo
				}
			} else {

				return null; // error in cursor process
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		return Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	private ContactDetail getFirstNamebyID(ContactDetail contact) {
		String whereName = ContactsContract.Data.MIMETYPE + " = ? AND "
				+ ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
				+ " = ?";
		String[] whereNameParams = new String[] {
				ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
				contact.getId() };
		Cursor nameCur = getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, null, whereName,
				whereNameParams,
				ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
		while (nameCur.moveToNext()) {
			String given = nameCur
					.getString(nameCur
							.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
			contact.setName(given);
		}
		nameCur.close();
		return contact;

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


}
