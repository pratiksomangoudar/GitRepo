package com.pratik.hciproject;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.pratik.hciproject.common.ContactDetail;

public class MyWidgetProvider extends AppWidgetProvider {

	public static final String IMAGE_BUTTON_1_ACTION_CLICK= "Button1Click";
	public static final String IMAGE_BUTTON_2_ACTION_CLICK= "Button2Click";
	public static final String IMAGE_BUTTON_3_ACTION_CLICK= "Button3Click";
	public static final String IMAGE_BUTTON_4_ACTION_CLICK= "Button4Click";


	public static ArrayList<ContactDetail> names;
	private int[] imageButtons = {R.id.imageButton1,R.id.imageButton2,R.id.imageButton3,R.id.imageButton4};
	private int[] textView     = {R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4};
	private int[] contactLayout = {R.id.contactLayout1,R.id.contactLayout2,
			R.id.contactLayout3,R.id.contactLayout4};
	
	

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);




		System.out.println("Before");
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){


			Log.d("HCI PROJECT", "ON RECIEVE WIDGET");
		}


		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {


		ComponentName thisWidget = new ComponentName(context,
				MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		if(ProcessingService.mostProbableContacts!=null){
			
			for (int widgetId : allWidgetIds) {
				RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
						R.layout.widget_layout);
				Log.d("HCI PROJECT", "WIDGET UPDATING............");
				for (int i=0;i<4;i++) {
					ContactDetail contact= ProcessingService.mostProbableContacts.get(i);
					String uriStr = contact.getImageURI();
					if(uriStr.equals("")){

					}
					else{

						Uri uri= Uri.parse(uriStr);
						remoteViews.setImageViewUri(imageButtons[i], uri);
					}
					remoteViews.setTextViewText(textView[i],contact.getName());
					remoteViews.setInt(contactLayout[i], "setBackgroundColor", contact.getColor());

					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+contact.getNumber()));
					PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, callIntent, 0);
					remoteViews.setOnClickPendingIntent(imageButtons[i], configPendingIntent);
//					Intent intent = new Intent(context, CallService.class);
//					intent.setData(Uri.parse("tel:"+contact.getNumber()));
//				      PendingIntent pendingIntent = PendingIntent.getService(context,
//				          0, intent, 0);
//				      remoteViews.setOnClickPendingIntent(imageButtons[i], pendingIntent);
				}

				if(remoteViews!=null)
				{
					appWidgetManager.updateAppWidget(widgetId, remoteViews);
				}

			}
		}
	}


	@Override
	public IBinder peekService(Context myContext, Intent service) {
		// TODO Auto-generated method stub
		return super.peekService(myContext, service);
	}



}
