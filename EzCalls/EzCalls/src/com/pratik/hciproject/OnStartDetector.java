package com.pratik.hciproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnStartDetector extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent(context, ProcessingService.class);
		serviceIntent.setAction("com.pratik.hciproject.ProcessingService");
		context.startService(serviceIntent);
	}

}
