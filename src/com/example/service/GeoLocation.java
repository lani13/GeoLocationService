package com.example.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class GeoLocation {

	    LocationManager lm;
	   
	    boolean gps_enabled,network_enabled;
	    boolean islocationFound;
	    LocationResult location;
	   

	public GeoLocation(Context context){
		 gps_enabled = false;
	     network_enabled = false;
	     islocationFound = false;
	     lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}  
	    
	    
	public void getLocation(LocationResult locRes){
		
		try{
			gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception ex){}
		
        try{
        	network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex){
        	 Log.i("Usluga", "network exception "+ex.toString());
        }

        Log.i("Usluga", "network_enabled "+network_enabled);
        Log.i("Usluga", "gps_enabled "+gps_enabled);
        
		location = locRes;

        
        if(gps_enabled){
        	Log.i("Usluga", "gps");
        	lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,null);  //lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListenerGps,null);  //(String provider, LocationListener listener, Looper looper);  
        	
        }
        else{                              
            if(network_enabled){
            	Log.i("Usluga", "network");
            	lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null); //lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListenerNetwork,null);
            
            }
        }

        final Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
             public void run() {
            	 if(!islocationFound){  //&& (gps_enabled || network_enabled)
                      lm.removeUpdates(locationListener);
                      location.stopMonitoringService();
                      Log.i("Usluga", "aborted");
            	 }
             }
        }, 50000);
        
       
	}
	
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location loc) {
			islocationFound = true;
			location.gotLocation(loc);
		}
		
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };


    
    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
        public abstract void stopMonitoringService();
    }
    
}
