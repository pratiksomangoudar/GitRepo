package com.pratik.hciproject.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.pratik.hciproject.common.Constants;
import com.pratik.hciproject.common.ContactDetail;

public class SQLiteDataHepler {

	private SQLiteHelper sqlhelper;
	private SQLiteDatabase database;
	private Context context;

	public SQLiteDataHepler(Context context) {
		this.context = context;
		this.sqlhelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = sqlhelper.getWritableDatabase();
	}

	public void close() {
		sqlhelper.close();
	}

	public boolean insertTimeData(CallRecord callrecord) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ID, callrecord.getId());
		values.put(SQLiteHelper.COLUMN_NAME, callrecord.getContact_name());
		values.put(SQLiteHelper.COLUMN_PHONE, callrecord.getPhone_number());
		values.put(SQLiteHelper.COLUMN_TIME, new SimpleDateFormat(
				Constants.SIMPLEDATEFORMAT).format(callrecord.getDatetime()));
		values.put(SQLiteHelper.COLUMN_CALL_DURATION,
				callrecord.getCall_duration());
		values.put(SQLiteHelper.COLUMN_TYPE, Constants.COLUMNTYPE_TIME);
		Long insertId = database.insert(SQLiteHelper.TABLE_CALLDATA, null,
				values);
		if (insertId == null)
			return false;
		return true;

	}

	public boolean insertLocationData(CallRecord callrecord) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ID, callrecord.getId());
		values.put(SQLiteHelper.COLUMN_NAME, callrecord.getContact_name());
		values.put(SQLiteHelper.COLUMN_PHONE, callrecord.getPhone_number());
		values.put(SQLiteHelper.COLUMN_TIME, new SimpleDateFormat(
				Constants.SIMPLEDATEFORMAT).format(callrecord.getDatetime()));
		values.put(SQLiteHelper.COLUMN_CALL_DURATION,
				callrecord.getCall_duration());
		values.put(SQLiteHelper.COLUMN_TYPE, Constants.COLUMNTYPE_LOCATION);
		values.put(SQLiteHelper.COLUMN_LATT, callrecord.getLatitude());
		values.put(SQLiteHelper.COLUMN_LONG, callrecord.getLongitude());
		Long insertId = database.insert(SQLiteHelper.TABLE_CALLDATA, null,
				values);
		if (insertId == null)
			return false;
		return true;

	}

	public long countRows() {
		long count = DatabaseUtils.queryNumEntries(database,
				SQLiteHelper.TABLE_CALLDATA);
		Log.d("SQLiteDataHepler", "Row Count in Table is : " + count);
		return count;

	}

	public ArrayList<CallRecord> getCallRecords() throws ParseException {
		// TODO Auto-generated method stub
		Cursor data = database.query(SQLiteHelper.TABLE_CALLDATA, null, null,
				null, null, null, null);
		ArrayList<CallRecord> list = new ArrayList<CallRecord>();
		int name = data.getColumnIndex(SQLiteHelper.COLUMN_NAME);
		int id = data.getColumnIndex(SQLiteHelper.COLUMN_ID);
		int number = data.getColumnIndex(SQLiteHelper.COLUMN_PHONE);
		int type = data.getColumnIndex(SQLiteHelper.COLUMN_TYPE);
		int time = data.getColumnIndex(SQLiteHelper.COLUMN_TIME);
		int latitude = data.getColumnIndex(SQLiteHelper.COLUMN_LATT);
		int longitude = data.getColumnIndex(SQLiteHelper.COLUMN_LONG);
		while (data.moveToNext()) {
			CallRecord callRecord = new CallRecord();
			callRecord.setId(data.getString(id));
			callRecord.setContact_name(data.getString(name));
			callRecord.setPhone_number(data.getString(number));
			callRecord.setDatetime(new SimpleDateFormat(
					Constants.SIMPLETIMEFORMAT).parse(new SimpleDateFormat(
					Constants.SIMPLETIMEFORMAT).format(new SimpleDateFormat(
					Constants.SIMPLEDATEFORMAT).parse(data.getString(time)))));
			callRecord.setLatitude(data.getString(latitude));
			callRecord.setLongitude(data.getString(longitude));
			callRecord.setType(data.getString(type));
			list.add(callRecord);
		}
		data.close();
		return list;
	}

	public boolean storeInProcessTable(CallRecord callrecord) {

		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_ID, callrecord.getId());
		values.put(SQLiteHelper.COLUMN_NAME, callrecord.getContact_name());
		values.put(SQLiteHelper.COLUMN_PHONE, callrecord.getPhone_number());
		values.put(SQLiteHelper.COlUMN_TIME_DIFF, callrecord.getTime_diff());
		values.put(SQLiteHelper.COLUMN_LOCATION_DIFF,
				callrecord.getLocation_diff());
		values.put(SQLiteHelper.COLUMN_CONTACT_ID, callrecord.getContact_id());
		Long insertId = database.insert(SQLiteHelper.TABLE_PROCESS_DATA, null,
				values);
		if (insertId == null)
			return false;
		return true;
	}

	public ArrayList<String> getMostProbable() {
		Cursor data = database.rawQuery("SELECT " + SQLiteHelper.COLUMN_PHONE
				+ "," + SQLiteHelper.COLUMN_NAME + ", count(*) AS count FROM "
				+ SQLiteHelper.TABLE_PROCESS_DATA + " GROUP BY "
				+ SQLiteHelper.COLUMN_PHONE + "," + SQLiteHelper.COLUMN_NAME
				+ " ORDER BY count DESC ", null);
		int id = data.getColumnIndex(SQLiteHelper.COLUMN_PHONE);
		ArrayList<String> list = new ArrayList<String>();
		while (data.moveToNext()) {
			list.add(data.getString(id));
		}
		data.close();
		return list;
	}

	public boolean initializeProcessdataTable() {
		database.execSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_PROCESS_DATA);
		database.execSQL(SQLiteHelper.DATABASE_CREATE1);

		return true;

	}

	public ContactDetail getColorForContact(ContactDetail contact) {
		Cursor data = database.rawQuery("SELECT "
				+ SQLiteHelper.COLUMN_COLOR_CONTACT_ID + ","
				+ SQLiteHelper.COLUMN_COLOR + " FROM "
				+ SQLiteHelper.TABLE_COLOR_DATA + " where "
				+ SQLiteHelper.COLUMN_COLOR_CONTACT_ID + " = '" + contact.getId()
				+ "'", null);
		int id = data.getColumnIndex(SQLiteHelper.COLUMN_CONTACT_ID);
		int color = data.getColumnIndex(SQLiteHelper.COLUMN_COLOR);

		if (data.moveToNext()) {

			color = data.getInt(color);
			contact.setColor(color);
		} else {
			Log.d("HCI-PROJECT", "Inserting new Contact color");
			color = this.getRandomColor();
			contact.setColor(color);
			ContentValues values = new ContentValues();
			values.put(SQLiteHelper.COLUMN_COLOR_CONTACT_ID, contact.getId());
			values.put(SQLiteHelper.COLUMN_COLOR, color);

			Long insertId = database.insert(SQLiteHelper.TABLE_COLOR_DATA,
					null, values);

		}
		data.close();

		return contact;
	}

	public ArrayList<ContactDetail> getAllColorsForContacts() {
		Cursor data = database.rawQuery("SELECT "
				+ SQLiteHelper.COLUMN_COLOR_CONTACT_ID + ","
				+ SQLiteHelper.COLUMN_COLOR + " FROM "
				+ SQLiteHelper.TABLE_COLOR_DATA, null);
		ArrayList<ContactDetail> contacts = new ArrayList<ContactDetail>();
		while (data.moveToNext()) {
			String id = data.getString(data
					.getColumnIndex(SQLiteHelper.COLUMN_COLOR_CONTACT_ID));
			int color = data.getInt(data
					.getColumnIndex(SQLiteHelper.COLUMN_COLOR));

			ContactDetail detail = new ContactDetail();
			detail.setId(id);
			detail.setColor(color);
			contacts.add(detail);

		}
		data.close();
		return contacts;
	}

	public boolean updateColorContacts(String id, int color) {
//		Cursor data = database.rawQuery("'Update " + SQLiteHelper.TABLE_COLOR_DATA
//				+ " set " + SQLiteHelper.COLUMN_COLOR + " = " + color + " where "
//				+ SQLiteHelper.COLUMN_COLOR_CONTACT_ID + " = " + id, null);
//		data.close();
		
		String strFilter = SQLiteHelper.COLUMN_COLOR_CONTACT_ID+"=" + id;
		ContentValues args = new ContentValues();
		args.put(SQLiteHelper.COLUMN_COLOR, color);
		database.update(SQLiteHelper.TABLE_COLOR_DATA, args, strFilter, null);
		return true;
	}

	private int getRandomColor() {
		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		int color = Color.argb(255, red, green, blue);
		return color;
	}

	public ArrayList<String> getMostProbableGeneral() {

		Cursor data = database.rawQuery("SELECT " + SQLiteHelper.COLUMN_PHONE
				+ "," + SQLiteHelper.COLUMN_NAME + ", count(*) AS count FROM "
				+ SQLiteHelper.TABLE_CALLDATA + " GROUP BY "
				+ SQLiteHelper.COLUMN_PHONE + "," + SQLiteHelper.COLUMN_NAME
				+ " ORDER BY count DESC ", null);

		int phone = data.getColumnIndex(SQLiteHelper.COLUMN_PHONE);
		ArrayList<String> list = new ArrayList<String>();
		while (data.moveToNext()) {
			list.add(data.getString(phone));
		}
		data.close();
		return list;

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

	public boolean init() {
		// TODO Auto-generated method stub
		database.execSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_CALLDATA);
		database.execSQL(SQLiteHelper.DATABASE_CREATE);
		initializeProcessdataTable();
		return true;
	}

	public boolean smartInsertTimeData(CallRecord callRecord) {
		Cursor data = database.rawQuery("SELECT " + SQLiteHelper.COLUMN_ID
				+ " FROM " + SQLiteHelper.TABLE_CALLDATA + " where "
				+ SQLiteHelper.COLUMN_ID + "=" + callRecord.getId(), null);
		int id = data.getColumnIndex(SQLiteHelper.COLUMN_ID);

		if (data.moveToNext()) {
			data.close();
			return true;
		} else {

			boolean insertId = insertTimeData(callRecord);

		}
		data.close();
		return true;
	}

	public boolean smartInsertLocationData(CallRecord callRecord) {
		Cursor data = database.rawQuery("SELECT " + SQLiteHelper.COLUMN_ID
				+ " FROM " + SQLiteHelper.TABLE_CALLDATA + " where "
				+ SQLiteHelper.COLUMN_ID + "=" + callRecord.getId(), null);
		int id = data.getColumnIndex(SQLiteHelper.COLUMN_ID);

		if (data.moveToNext()) {
			data.close();
			return true;
		} else {

			boolean insertId = insertLocationData(callRecord);

		}
		data.close();
		return true;
	}

}
