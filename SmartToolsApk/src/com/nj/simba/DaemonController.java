package com.nj.simba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DaemonController extends BroadcastReceiver {
	public static String ACTION_START_SERVER="com.nj.simba.action.START_SERVER";
    public static String ACTION_RELEASE_SERVER="com.nj.simba.action.RELEASE_SERVER";
    
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("Simba", action);
		
		if (action.equalsIgnoreCase(ACTION_START_SERVER)) {
			context.startService(new Intent("com.nj.simba.service"));
		} else if (action.equalsIgnoreCase(ACTION_RELEASE_SERVER)){
			context.stopService(new Intent("com.nj.simba.service"));
		} else {
			Log.d("Simba", "Not support now!");
		}
	}

}
