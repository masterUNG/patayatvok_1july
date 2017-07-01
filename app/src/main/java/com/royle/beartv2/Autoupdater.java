package com.royle.beartv2;


import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import meklib.MCrypt;

/**
 * Created by Naifun on 1/2/2559.
 */
public class Autoupdater{


    private Context context;
    private Runnable listener;

	/**
     * The link to the public file version information. It may be
     * Dropbox, its own hosting or any other service.
     *
     * สิ่งที่ได้จาก url นี้
     *
     * {
     "      versionCode":1,
     "      versionName":"1",
     "      downloadURL":"http://7topup.com/iLike4K/pattayaTV21.apk"
     }
     *
     */
    private static final String INFO_FILE = "http://7topup.com/iLike4K/Version.txt";

    /**
     * The version code established in AndroidManifest.xml version
     * Installed application. Is the numerical value used for Android
     * Differentiate versions.
     */
    private int currentVersionCode;

    /**
     * The name of the version established AndroidManifest.xml version
     * Installed. Is the text string that is used to identify the version
     * For the user.
     */
    private String currentVersionName;

    /**
     *The version code established in the last AndroidManifest.xml
     * Available version of the application.
     */
    private int latestVersionCode;

    /**
     * The version name set out in the last AndroidManifest.xml
     * Version.
     */
    private String latestVersionName;

    /**
     * Link direct download of the latest version available.
     */
    private String downloadURL;
    private MCrypt mcrypt = new MCrypt();

    /**
     * Constructor unico.
     * @param context Contexto sobre el cual se ejecutara el Instalador.
     */
    public Autoupdater(Context context) {
        this.context = context;
    }

    /**
     * Method to initialize the object. You must call ahead to any
     * Other, and in a separate thread (or AsyncTask) not to block the interface
     * Because it makes use of Internet.
     *
     *      param Context
     * The context of the application for information
     * The current version.
     */
    private void getData() {
        try{
            // Datos locales
            Log.d("AutoUpdater", "GetData");
            PackageInfo pckginfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionCode = pckginfo.versionCode;
            currentVersionName = pckginfo.versionName;

            // Remote data
            String data = downloadHttp(new URL(INFO_FILE));
            //String decrypted = new String(mcrypt.decrypt(data));

            JSONObject json = new JSONObject(data);
            latestVersionCode = json.getInt("versionCode");
            latestVersionName = json.getString("versionName");

            //ใส่ url ที่ apk file ที่ใช้ในการติดตั่ง แอฟใหม่ได้
            downloadURL = json.getString("downloadURL");
            Log.d("AutoUpdate", "Data obtained successfully");
        }catch(JSONException e){
            Log.e("AutoUpdate", "An error occurred with the JSON", e);
        }catch(PackageManager.NameNotFoundException e){
            Log.e("AutoUpdate", "There was an error with Packete: S", e);
        }catch(Exception e){
            Log.e("AutoUpdate", "There was an error downloading", e);
        }
    }

    /**
     *  Method to compare the current version with the latest.
     *
     * @return True if the current is a newer version available.
     */
    public boolean isNewVersionAvailable() {
        return getLatestVersionCode() > getCurrentVersionCode();
    }

    /**
     * Returns the current version.
     *
     * @return
     */
    public int getCurrentVersionCode() {
        return currentVersionCode;
    }

    /**
     * Returns the name current version.
     *
     * @return
     */
    public String getCurrentVersionName() {
        return currentVersionName;
    }

    /**
     * Returns the code of the latest version.
     *
     * @return
     */
    public int getLatestVersionCode() {
        return latestVersionCode;
    }

    /**
     * Returns the name of the latest version available.
     *
     * @return
     */
    public String getLatestVersionName() {
        return latestVersionName;
    }

	/**
     * Returns the link to download the latest version
     *
     * @return
     */
    public String getDownloadURL() {
        return downloadURL;
    }

    /**
     * Helper method used by getData () to read the information file.
     * Manager to connect to the network, download the file and convert it to
     * String.
     *
     * @param url
     *            The URL of the file you want to download.
     * @return String containing the file contents
     * @throws IOException
     *            If there is a connection problem
     */
    private static String downloadHttp(URL url) throws IOException {
        // CONNECTION Code, relevant to the topic.
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        c.setRequestMethod("GET");
        c.setReadTimeout(15 * 1000);
        c.setUseCaches(false);
        c.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            stringBuilder.append(line + "\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Method interface.
     * Connect to the server and get the latest version information of the application.
      *param OnFinishRunnable executable at the end Listener.
      * Code to be executed once the process is completed.
     */
    public void DownloadData(Runnable OnFinishRunnable){
        //Saves the listener.
        this.listener = OnFinishRunnable;
        //Run AsyncTask data loss.
        downloaderData.execute();
    }

    /**
     * Method Interface.
     * Second method to use.
     * It is responsible, once the data from the most recent version obtained, and in a separate thread,
     * check that there is indeed a more recent version, download and install it.
     * Prepare the application to be closed and uninstalled after this method.
     */
    public void InstallNewVersion(Runnable OnFinishRunnable){
        if(isNewVersionAvailable()){
           if(getDownloadURL() == "") return;
            listener = OnFinishRunnable;
            String params[] = {getDownloadURL()};
            downloadInstaller.execute(params);

        }
    }

    /**
     * AsyncTask Object Manager server to download information
     * and run the listener.
     */
    private AsyncTask downloaderData = new AsyncTask() {
        @Override
        protected Object doInBackground(Object[] objects) {
            //calls the helper method that typically set all variables.
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //After executing the main code, the listener runs
            // to let the main thread.
            if(listener != null)listener.run();
            listener = null;
        }
    };

    /**
     * Object Manager AsyncTask download and install the latest version of the application.
     * It is not cancelable.
     */
    private AsyncTask<String, Integer, Intent> downloadInstaller = new AsyncTask<String, Integer, Intent>() {
        @Override
        protected Intent doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String PATH = Environment.getExternalStorageDirectory() + "/download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "app.apk");
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();//till here, it works fine - .apk is download to my sd card in download file


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/app.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


//                try
//                {
//                    Runtime.getRuntime().exec(new String[] {"su", "-c", "pm install -r /mnt/sdcard/Download/app.apk"});
//
//                }
//                catch (IOException e)
//                {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/app.apk")), "application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
//                    context.startActivity(intent);
//                }
                //return intent;
            } catch (IOException e) {

                Log.e("Update error!", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            if(listener != null)listener.run();
            listener = null;
        }
    };


}
