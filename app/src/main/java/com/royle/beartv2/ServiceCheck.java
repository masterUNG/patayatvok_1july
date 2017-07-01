package com.royle.beartv2;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import user.UserProfileActivity;
import meklib.MCrypt;

public class ServiceCheck extends Service{

	private MCrypt mcrypt = new MCrypt();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("service", "start");
		loop();
		return START_STICKY;
	}
	@Override
	public void onDestroy() {

		super.onDestroy();
		Log.e("service", "stop service");
	}

	private void loop(){
		long endTime = System.currentTimeMillis()+5*1000;
		while (System.currentTimeMillis()<endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
		Log.e("service", "check token");
		new CheckTokenTask().execute();
	}

	private class CheckTokenTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			DataStore dataStore = new DataStore(getApplicationContext());
			String token = dataStore.LoadSharedPreference(DataStore.USER_TOKEN, "");
			PortalServices portalServices = new PortalServices();
			String resultData = portalServices.makePortalCall(null, UrlApp.CHECK_TOKEN+token, PortalServices.GET);
			return resultData;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				String decrypted = new String(mcrypt.decrypt(result));
				JSONObject jsonObject = new JSONObject(decrypted);
				String token_status = jsonObject.getString("token_status");
				Log.e("service", "check_token"+token_status);
				if (token_status.equals("no")) {
					UserProfileActivity.logout(getApplicationContext());
					stopSelf();
					Log.e("service", "stop service");
				}else {
					loop();
				}
			} catch (Exception e) {
				e.printStackTrace();
				stopSelf();
				Log.e("service", "stop service");
			}
		}


	}

	private void check_token(){
		DataStore dataStore = new DataStore(this);
		String token = dataStore.LoadSharedPreference(DataStore.USER_TOKEN, "");
		PortalServices portalServices = new PortalServices();
		String resultData = portalServices.makePortalCall(null, UrlApp.CHECK_TOKEN+token, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			String token_status = jsonObject.getString("token_status");
			if (token_status.equals("no")) {
				UserProfileActivity.logout(this);
				stopSelf();
				Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show();

			}else {
				loop();
			}
		} catch (Exception e) {
			e.printStackTrace();
			stopSelf();
			Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show();
		}
	}


}	// Main Class
