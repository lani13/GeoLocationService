package com.example.service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class DbSaveAsyncTask extends AsyncTask<String, Void, String>{

	Activity context;
	RegIdDataSource dbDataSource;
	
	
	public DbSaveAsyncTask(Activity context) {
		super();
		this.context = context;
	
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... p) {
	   
						
		   try{
			dbDataSource = new RegIdDataSource(context);
		    dbDataSource.open();
		    dbDataSource.insertIdTable(p[0]);
		    dbDataSource.close();
		   }
		   catch(Exception e){
			Log.i("Service1","B³¹d zapisu do bazy");
		    return "B³¹d zapisu do bazy";
		   }
		
		return "Zapisano wspó³rzêdne do bazy";
	}

	@Override
	protected void onPostExecute(String s) {
	
		super.onPostExecute(null);
	}

	

}
