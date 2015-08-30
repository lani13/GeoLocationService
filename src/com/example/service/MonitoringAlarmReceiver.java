package com.example.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class MonitoringAlarmReceiver extends BroadcastReceiver {

	Intent serviceIntent;
	private static WakeLock wakeLock = null;
	

	public static synchronized void acquireLock(Context ctx){
		if (wakeLock == null){
			PowerManager mgr = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
			wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "usluga");
			wakeLock.setReferenceCounted(true);
		}
		wakeLock.acquire();
		Log.i("Usluga", "lock start");
	}
	
	public static synchronized void releaseLock(){
		if (wakeLock != null){
			wakeLock.release();
			Log.i("Usluga", "lock stop");
		}
	}
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		acquireLock(context);
		Log.i("Usluga", "alarm receiver");
		serviceIntent = new Intent(context,	MonitoringService.class);
		context.startService(serviceIntent);
	}

}
