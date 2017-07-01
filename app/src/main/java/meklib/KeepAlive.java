package meklib;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import org.json.JSONObject;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import user.LoginActivity;
import user.UserProfileActivity;

/**
 * Created by Administrator on 9/12/2016.
 */
public class KeepAlive extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    Handler mHandler = new Handler();




    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

 /*   private void ping() {
        DataStore dataStore = new DataStore(getApplicationContext());
        String username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");

        if (username != "") {
            try {
                Toast.makeText(this, "server started", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Error", "In onStartCommand");
                e.printStackTrace();
            }

        }
        scheduleNext();
    }*/

    private void scheduleNext() {

        mHandler.postDelayed(new Runnable() {
            public void run() {
                    new KeepAliveTask().execute();
            }
        }, 60000);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DataStore dataStore = new DataStore(getApplicationContext());
        String username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");

        if (username != "") {
            mHandler = new android.os.Handler();
            new KeepAliveTask().execute();

        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service ended", Toast.LENGTH_SHORT).show();
    }

    private class KeepAliveTask extends AsyncTask<Void, Void, String>{

        private MCrypt mcrypt = new MCrypt();
        DataStore dataStore = new DataStore(KeepAlive.this);
        String username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");
        //String token = dataStore.LoadSharedPreference(DataStore.USER_TOKEN, "");

        public boolean isOnline(Context c) {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();

            if (ni != null && ni.isConnected())
                return true;
            else
                return false;
        }

        @Override
        protected String doInBackground(Void... params) {

            PortalServices portalServices = new PortalServices();
            if (username != ""){
                String resultData = portalServices.makePortalCall(null, UrlApp.KEEP_ALIVE + "?username=" + username + "&mac_address=" + MainActivity.getAndroidId(KeepAlive.this), PortalServices.GET);
                return resultData;
            }else{
            String resultData = "";
            return resultData;
            }


        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (result != "" && isOnline(KeepAlive.this)) {
                try {
                    String decrypted = new String(mcrypt.decrypt(result));
                    JSONObject jsonObject = new JSONObject(decrypted);
                    String keepalive_status = jsonObject.getString("status");
                    //Log.e("service", "keep_alive"+status);
                    if (keepalive_status.equals("disconnected") || keepalive_status.equals("error")) {
                        UserProfileActivity.logout(KeepAlive.this);
                        Intent dialogIntent = new Intent(KeepAlive.this, LoginActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(result != "" && !isOnline(KeepAlive.this)){
                UserProfileActivity.logout(KeepAlive.this);
                Intent dialogIntent = new Intent(KeepAlive.this, LoginActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }

                if (username != "") {
                    scheduleNext();
                }

        }

    }

}