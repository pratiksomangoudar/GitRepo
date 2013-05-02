package com.pratik.hciproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_CALLDATA = "CALL_DATA";
	public static final String TABLE_PROCESS_DATA="PROCESSED_CALL_DATA";
	public static final String TABLE_COLOR_DATA="COLOR_DATA";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "contact_name";
	public static final String COLUMN_PHONE = "phone_number";
	public static final String COLUMN_TIME = "datetime";
	public static final String COLUMN_CALL_DURATION = "call_duration";
	public static final String COLUMN_LATT = "latitidue";
	public static final String COLUMN_LONG = "longitude";
	public static final String COLUMN_TYPE = "type";
	public static final String COlUMN_TIME_DIFF = "time_diff";
	public static final String COLUMN_LOCATION_DIFF = "location_diff";
	
	public static String COLUMN_CONTACT_ID = "contact_id";
	public static String COLUMN_COLOR_CONTACT_ID = "_id";
	public static String COLUMN_COLOR = "color";
	
	
	private static final String DATABASE_NAME = "CallRecord.db";
	private static final int DATABASE_VERSION = 1;

	
	public static final String DATABASE_CREATE = "create table "
			+ TABLE_CALLDATA + "(" 
			+ COLUMN_ID + " integer , " 
			+ COLUMN_NAME + " text not null, " 
			+ COLUMN_PHONE + " text not null, "
			+ COLUMN_TIME + " timestamp not null, "
			+ COLUMN_CALL_DURATION + " integer, "
			+ COLUMN_LATT + " float, " 
			+ COLUMN_LONG + " float, " 
			+ COLUMN_TYPE + " text" 
			+");";
	
	public static final String DATABASE_CREATE1="create table "
			+ TABLE_PROCESS_DATA + "(" 
			+ COLUMN_ID + " integer , " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_CONTACT_ID + " text not null, "
			+ COLUMN_PHONE + " text not null, "
			+ COlUMN_TIME_DIFF + " integer, "
			+ COLUMN_LOCATION_DIFF + " integer);";
	
	public static final String DATABASE_CREATE2="create table "
			+ TABLE_COLOR_DATA + "(" 
			+ COLUMN_COLOR_CONTACT_ID + " text primary key, "
			+ COLUMN_COLOR + " integer not null);";
	

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		//database.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLDATA);
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE1);
		database.execSQL(DATABASE_CREATE2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLDATA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCESS_DATA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLOR_DATA);
		onCreate(db);
	}

} 

