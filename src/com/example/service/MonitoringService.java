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
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MonitoringService extends Service {

	
	private static final String PARAMETER_USER = "user";
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_LAT = "lat";
	private static final String PARAMETER_LON = "lon";
	private static final String USER_KID = "kid";
	private static final String ACTION_SEND_COORD = "sendcoordinates";
	
	private static final String URL = "http://xxxx";
    
	

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {

		Log.i("Usluga", "start");
		
		
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		    	
		    	new SendCoordinatesAsyncTask().execute(location.getLatitude()+"",location.getLongitude()+"");

		    	Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_LONG).show();
		        Log.i("Usluga", location.toString());
		        		        
		    }

			@Override
			public void stopMonitoringService() {
				MonitoringAlarmReceiver.releaseLock();
				stopSelf();				
			}
		};
		
		GeoLocation myLocation = new GeoLocation(getApplicationContext());
		myLocation.getLocation(locationResult);
		
		
		
		
		// jesli proces zostanie zabity, nie zostanie automatycznie zrestartowany
		return START_NOT_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		Log.i("Usluga", "stop");
	}
	
	
	
private void sendToServer(String lat, String lon){
		
		HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);

        

        try
        {
       	 
       	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair(PARAMETER_ACTION, ACTION_SEND_COORD));
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
			Log.i("Usluga", "Sending to server");		
			sendToServer(p[0],p[1]); //lat,lon
			return null;
	
		}

		@Override
		protected void onPostExecute(Void v) {
		
			MonitoringAlarmReceiver.releaseLock();
			stopSelf();
			super.onPostExecute(null);
		}

		

	}
	
}
