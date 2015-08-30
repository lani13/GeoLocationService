package com.example.service;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class CloudMessageReceiver extends WakefulBroadcastReceiver  {

	Intent serviceIntent;
	int time = 1 * 50 * 1000; 
	String regid;
	
    private static final String ACTION_CHECK_POS = "checkposition";
    private static final String ACTION_SEND_CONTACTS = "sendcontacts";
    
    private static final String ACTION_GCM_REGISTRATION =
            "com.google.android.c2dm.intent.REGISTRATION";

    private static final String ACTION_PACKAGE_REPLACED =
            "android.intent.action.PACKAGE_REPLACED";

	
	
	@Override
	public void onReceive(Context context, Intent i) {
		
		Log.i("Usluga", "cloud message receiver");
		

		
		Bundle extras = i.getExtras();
		String action = i.getAction();
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
	     
	    String messageType = gcm.getMessageType(i);
		
		if (!extras.isEmpty()) {
	    	  
	    	  Log.i("Usluga", "extras not empty");
	    	  
	    	  if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	    		  
	    		  
	    		  String userAction = extras.getString("action");
	    		  
	    		  Log.i("Usluga", "message received "+userAction);
	    		  
	    		  if(ACTION_CHECK_POS.equals(userAction)){
	    			  
	    			  Log.i("Usluga", "check coords");
	    			  Intent service = new Intent(context, CloudMessageService.class);
	    			  startWakefulService(context,service);			  
	    		  }
	    		  if(ACTION_SEND_CONTACTS.equals(userAction)){
	    			  
	    			  Log.i("Usluga", "send contacts");
	    			  Intent service = new Intent(context, CloudMessageContactsService.class);
	    			  startWakefulService(context,service);			  
	    		  }
	    		  
	    	  }else if (action.equals(ACTION_GCM_REGISTRATION)) {
	    		  Log.i("Usluga", extras.getString("registration_id"));
	          } else if (action.equals(ACTION_PACKAGE_REPLACED)) {
	        	  Log.i("Usluga", "package replaced");
	          }
	      }
	}

}
