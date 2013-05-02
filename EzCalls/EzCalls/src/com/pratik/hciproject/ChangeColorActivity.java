/**
 * 
 */
package com.pratik.hciproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pratik.hciproject.common.ContactDetail;
import com.pratik.hciproject.db.SQLiteDataHepler;

/**
 * @author pratiksomanagoudar
 *
 */
public class ChangeColorActivity extends Activity {




	private SQLiteDataHepler sqlData;
	private ArrayList<ContactDetail> contactsArray;
	private ListView listview;
	private MyContactColorAdapter colorAdapter;
	private SharedPreferences prefs;
	private boolean isSaved;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_color);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		isSaved= false;
		sqlData = new SQLiteDataHepler(this);  
		sqlData.open();
		contactsArray= sqlData.getAllColorsForContacts();
		sqlData.close();

		listview= (ListView) findViewById(R.id.colorlist);

		colorAdapter =  new MyContactColorAdapter(this, R.layout.listitem, contactsArray);


		Button saveButton = (Button) findViewById(R.id.savecolor);
		saveButton.setOnClickListener(new OnClickListener() {

			

			@Override
			public void onClick(View v) {
				for (ContactDetail key : contactsArray) {
					sqlData.open();
					boolean isDone= sqlData.updateColorContacts(key.getId(), key.getColor());
					sqlData.close();
				}
				Toast.makeText(getApplicationContext(), "Color Settings Saved", Toast.LENGTH_LONG).show();
				int count = prefs.getInt("ColorSaves", 0);
				count=count+1;
				prefs.edit().putInt("ColorSaves", count).commit();
				Log.d("PROJECT STATS", " NO of  Color setting saves made via EZ CALL"+count);
				isSaved=true;
			}
		});


		listview.setAdapter(colorAdapter);
		listview.setTextFilterEnabled(true);




		listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				ContactDetail cont = contactsArray.get(position);
				int color = getRandomColor();
				cont.setColor(color);
				contactsArray.remove(position);
				contactsArray.add(position, cont);
				colorAdapter.notifyDataSetChanged();		
			}});
	}

	private int getRandomColor() {
		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		int color = Color.argb(255, red, green, blue);
		return color;
	}

	public static  class MyContactColorAdapter extends ArrayAdapter<ContactDetail>{

		private ArrayList<ContactDetail> data;
		private int layoutResourceId;
		private Context context;

		public MyContactColorAdapter(Context context, int resource, List<ContactDetail> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.data = (ArrayList<ContactDetail>) objects; 
			this.layoutResourceId= resource;
			this.context= context;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ColorHolder holder = null;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new ColorHolder();
				holder.name = (TextView) row.findViewById(R.id.contactname);
				holder.color = (LinearLayout) row.findViewById(R.id.contactcolor);

				row.setTag(holder);
			}
			else
			{
				holder = (ColorHolder)row.getTag();
			}

			ContactDetail detail = data.get(position);
			String name = getFirstNamebyID(detail.getId());
			holder.name.setText(name);
			holder.color.setBackgroundColor(detail.getColor());

			return row;
		}

		static class ColorHolder
		{

			TextView name;
			LinearLayout color;
		}

		private String getFirstNamebyID(String id) {
			String displayName = new String();
			String whereName = ContactsContract.Data.MIMETYPE + " = ? AND "
					+ ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID
					+ " = ?";
			String[] whereNameParams = new String[] {
					ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,id };
			Cursor nameCur = context.getContentResolver().query(
					ContactsContract.Data.CONTENT_URI, null, whereName,
					whereNameParams,
					ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
			while (nameCur.moveToNext()) {
				displayName = nameCur
						.getString(nameCur
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
			}
			nameCur.close();
			return displayName;

		}
	}
	@Override
	protected void onPause() {
		if(isSaved){
		Thread t3 = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Log.d("HCI PROJECT", " on Color Change--");
				Intent serviceIntent = new Intent(getApplicationContext(), ProcessingService.class);
				serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
				startService(serviceIntent);

			}
		};
		t3.start();		
		isSaved=false;
		
	}
		super.onPause();
	}
}
