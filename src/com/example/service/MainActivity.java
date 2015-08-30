package com.example.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	private static final String PARAMETER_USER = "user";
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_REG_ID = "regId"; 
	
	private static final String ACTION_REGISTER = "register";
	private static final String USER_KID = "kid";
	
	private static final String URL = "http://xxxx";
	
	Context context;
	Button startButton, stopButton, registerButton;
	AlarmManager manager;
	PendingIntent sender;
	
	GoogleCloudMessaging gcm;
	String regid;
	String SENDER_ID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stopButton = (Button)findViewById(R.id.stop_button);
		registerButton = (Button)findViewById(R.id.buttonRegister);
		
		
		int time = 1 * 50 * 1000; 
		context = getApplicationContext();
				
		manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

	    Intent intent = new Intent(context,MonitoringAlarmReceiver.class);
		sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 10);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), time, sender);
		Log.i("Usluga", "start alarm");		
		
	
		registerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				registerInBackground();
			}});
		
		
		
		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				manager.cancel(sender);
				Log.i("Usluga", "stop alarm");
				
			}});
		
	}

    
	private void registerInBackground() {
	    final Handler handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case 0:
	                	 if(regid != null){
	                		 RegIdDataSource source = new RegIdDataSource(context);
	                		 source.open();
	                		 source.insertIdTable(regid);
	                		 regid = source.getRegistrationId();
	                		 source.close();
	                		 Log.i("Usluga", "reg id saved to db");
	                		 
	                		 new RegisterAsyncTask().execute(regid);
	                	  }

	                    break;
	                case 1:
	                    break;
	            }
	            super.handleMessage(msg);
	        }

	    };
	    Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);

	                if (regid != null) {
	                	Log.i("Usluga", "reg id = " +regid);
	                	handler.sendEmptyMessage(0);
	                } else {
	                	Log.i("Usluga", "brak reg id");
	                    handler.sendEmptyMessage(1);
	                }
                
	            } catch (IOException e) {
	                handler.sendEmptyMessage(1);
	                Log.i("Usluga", "message service error while registering to google "+ e.toString());
	            }
	        }
	    };
	    new Thread(runnable).start();
	}
	
	
private class RegisterAsyncTask extends AsyncTask<String, Void, Void>{

			
		
		public RegisterAsyncTask() {
			super();
			
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... p) {
			Log.i("Usluga", "Sending to server - register "+ p[0]);		
			sendToServer(p[0]);
			return null;
	
		}

		@Override
		protected void onPostExecute(Void v) {
	
			super.onPostExecute(null);
		}
}
	

private void sendToServer(String reg){
	
	HttpClient client = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(URL);

    try
    {
   	 
   	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair(PARAMETER_ACTION, ACTION_REGISTER));
        nameValuePairs.add(new BasicNameValuePair(PARAMETER_USER, USER_KID));
        nameValuePairs.add(new BasicNameValuePair(PARAMETER_REG_ID, reg));
   	 
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
        HttpResponse response = client.execute(httpPost);

        int code = response.getStatusLine().getStatusCode();
        Log.i("Usluga", "Response: " + code);
        

     }catch (ClientProtocolException e) {
   	  Log.i("Usluga", "ClientProtocolException");
    } catch (UnsupportedEncodingException e) {
   	 Log.i("Usluga", "UnsupportedEncodingException");
    } catch (IOException e) {
   	 Log.i("Usluga", "IOException");
    }
	
   
}


	
}
