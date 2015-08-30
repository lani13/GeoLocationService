package com.example.service;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.example.service.GeoLocation.LocationResult;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


public class CloudMessageService extends Service  {

	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	
	private static final String ACTION_CHECK_POS = "checkposition";
    
	private static final String PARAMETER_USER = "user";
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_LAT = "lat";
	private static final String PARAMETER_LON = "lon";
	private static final String USER_KID = "kid";
	
	private static final String URL = "xxx";
    
    Context context;
    String regid;
    boolean received;
    Intent i;
    

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startid) {

		 Log.i("Usluga", "message service start");
		
		 context = getApplicationContext();
		 i = intent;
		 

		  Log.i("Usluga", "getting coords");
		  
		  GeoLocation geo = new GeoLocation(this);
		  LocationResult locRes = new LocationResult(){

			@Override
			public void gotLocation(Location location) {
				Log.i("Usluga", "Location: " + location.getLatitude() + " " + location.getLongitude());
				

				new SendCoordinatesAsyncTask().execute(location.getLatitude()+"",location.getLongitude()+"");

			}

			@Override
			public void stopMonitoringService() {
				CloudMessageReceiver.completeWakefulIntent(intent);
				stopSelf();
			}};
		  
		     
			geo.getLocation(locRes);
		
		 // jesli proces zostanie zabity, nie zostanie automatycznie zrestartowany
		 return START_NOT_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		Log.i("Usluga", "message service stop");
	}


	
private void sendToServer(String lat, String lon){
		
		HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);

        

        try
        {
       	 
       	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair(PARAMETER_ACTION, ACTION_CHECK_POS));
            nameValuePairs.add(new BasicNameValuePair(PARAMETER_USER, USER_KID));
            nameValuePairs.add(new BasicNameValuePair(PARAMETER_LAT, lat));
            nameValuePairs.add(new BasicNameValuePair(PARAMETER_LON, lon));
       	 
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = client.execute(httpPost);


            Log.i("Usluga", "Response: " + response.getStatusLine().getStatusCode());
            
         }catch (ClientProtocolException e) {
       	  Log.i("Usluga", "ClientProtocolException");
        } catch (UnsupportedEncodingException e) {
       	 Log.i("Usluga", "UnsupportedEncodingException");
        } catch (IOException e) {
       	 Log.i("Usluga", "IOException");
        }
		
       
	}
	
	
private class SendCoordinatesAsyncTask extends AsyncTask<String, Void, Void>{

			
		
		public SendCoordinatesAsyncTask() {
			super();
			
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(String... p) {
			Log.i("Usluga", "Check position - sending to server");		
			sendToServer(p[0],p[1]); //lat,lon
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
