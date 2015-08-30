package com.example.service;

import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.location.Location;



public class ServerCommunication {

	private static final String USER = "kid";
	
	private static final String PARAMETER_USER = "user";
	private static final String PARAMETER_ACTION = "action";
	
	private static final String ACTION_REGISTER = "register";
	private static final String ACTION_UNREGISTER = "unregister";
	private static final String ACTION_SEND_COORD = "sendcoordinates";
	private static final String ACTION_CHECK_POS = "checkposition";
	
	private static final String SERVER_URL = "";
	
	
	private Context context;
	
	FileOutputStream outputStream;

	ArrayList<NameValuePair> list;
	
	public ServerCommunication(Context c) {
		context = c;
		list = new ArrayList<NameValuePair>();
	}

	
	public void sendCoordinatesToServer(Location loc, String action){
		
		double lat = loc.getLatitude();
		double lon = loc.getLongitude();
		
        list.add(new BasicNameValuePair(PARAMETER_USER, USER));
        list.add(new BasicNameValuePair(PARAMETER_ACTION, action));
        list.add(new BasicNameValuePair("lat", lat+""));
        list.add(new BasicNameValuePair("lon", lon+""));
	}
		
	public void registerToGoogleServices(String id){
		
	}
	
}
