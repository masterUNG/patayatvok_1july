package com.royle.beartv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Naifun on 2/2/2559.
 */
public class UpdateCheck  extends Activity implements AlertDialog.OnClickListener{
    private Autoupdater updater;
    private RelativeLayout loadingPanel;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        try {
            loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
            //This works if the update is not done at first. It is not this case.
            //loadingPanel.setVisibility(View.GONE);
            comenzarActualizar();

        }catch (Exception ex){
            //For any errors.
            Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void comenzarActualizar(){
        //To the context at hand.
        context = this;
        //We create the AutoUpdate.
        updater = new Autoupdater(this);
        //We run the ProgressBar.
        loadingPanel.setVisibility(View.VISIBLE);
        //We run the first method of the Auto Update.
        updater.DownloadData(finishBackgroundDownload);
    }

    /**
     * Code to be run once finished downloading the data.
     */
    private Runnable finishBackgroundDownload = new Runnable() {
        @Override
        public void run() {
            //We return to the ProgressBar invisible.
            loadingPanel.setVisibility(View.GONE);
            //Check that there is new version.
            if(updater.isNewVersionAvailable()){

                //Create message version data.
                String msj = "New version: " + updater.isNewVersionAvailable();
                msj += "\n";
                msj += "\nCurrent Version: " + updater.getCurrentVersionName() + "(" + updater.getCurrentVersionCode() + ")";
                msj += "\nLastest Version: " + updater.getLatestVersionName() + "(" + updater.getLatestVersionCode() +")";
                msj += "\n";
                msj += "\n";
                msj += "\nกรุณากดตกลงเพื่อ Upgrade Version ใหม่ ";
                msj += "\n";
                //Create alert window.
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
                dialog1.setMessage(msj);
                dialog1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getBaseContext(), "กรุณาอัพเดทเพื่อให้เป็น Version ใหม่", Toast.LENGTH_SHORT).show();
                        //Return the ProgressBar while you download and install.

                        Intent intent = new Intent(UpdateCheck.this, SplashScreen.class);
                        startActivity(intent);
                    }
                });

                //Sets the OK button to do if selected.
                dialog1.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Return the ProgressBar while you download and install.
                        loadingPanel.setVisibility(View.VISIBLE);
                        //Autoupdate runs the command to install. You can put a listener or not
                        updater.InstallNewVersion(null);
                        Toast.makeText(getApplicationContext(), "กำลังอัพเดท กรุณารอสักครู่......แล้วค่อยเปิดแอพใหม่อีกครั้ง", Toast.LENGTH_LONG).show();
                    }
                });

                //Displays the window waiting for response.
                dialog1.show();
            }else{
                //No updates.
                Toast.makeText(getApplicationContext(), "Version ของคุณเป็นเวอร์ชั่นล่าสุดแล้ว", Toast.LENGTH_LONG).show();

//                logout(UpdateCheck.this);
                Intent intent = new Intent(UpdateCheck.this,ServiceCheck.class);
                stopService(intent);

                Thread timerThread = new Thread() {
                    public void run() {
                        try {
                            sleep(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent(UpdateCheck.this, MainActivity.class);
                            startActivity(intent);
//                            installplayer();
                        }
                    }
                };
                timerThread.start();

            }
        }
    };

    private void installplayer(){

        // TODO Auto-generated method stub
        try {
            if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {

                Toast.makeText(getApplicationContext(), "Gold Player พร้อมใช้งาน", Toast.LENGTH_LONG).show();

            } else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {

                Toast.makeText(getApplicationContext(), "MX Player พร้อมใช้งาน", Toast.LENGTH_LONG).show();

            } else if (playerInstalledOrNot("com.mxtech.videoplayer.pro")) {

                Toast.makeText(getApplicationContext(), "MX Player Pro พร้อมใช้งาน", Toast.LENGTH_LONG).show();

            } else {
                installgold atualizaApp = new installgold();
                atualizaApp.setContext(getApplicationContext());
                atualizaApp.execute("MXPlayer.apk");


            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
    }

    private boolean playerInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public class installgold extends AsyncTask<String, Void, Void> {
        private Context context;

        public void setContext(Context contextf) {
            context = contextf;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                String nameapp = (String) arg0[0];
                String link = "http://4kmoviestar.com/New_Apk/" + nameapp;
                URL url = new URL(link);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String PATH = "/mnt/sdcard/Download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, nameapp);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/" + nameapp)), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


//                try
//                {
//                    Runtime.getRuntime().exec(new String[] {"su", "-c", "pm install -r /mnt/sdcard/Download/" + nameapp});
//                }
//                catch (IOException e)
//                {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/" + nameapp)), "application/vnd.android.package-archive");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
//                    context.startActivity(intent);
//                }

            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
    }

//    public static void logout(Context context){
//        DataStore dataStore = new DataStore(context);
//        dataStore.SavedSharedPreference(DataStore.USER_ID, "");
//        dataStore.SavedSharedPreference(DataStore.USER_NAME, "");
//        dataStore.SavedSharedPreference(DataStore.PASSWORD, "");
//        dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "");
//        dataStore.SavedSharedPreference(DataStore.USER_LEVEL_ID, "");
//        dataStore.SavedSharedPreference(DataStore.USER_EXPIRE, "");
//        dataStore.SavedSharedPreference(DataStore.USER_TOKEN, "");
//        dataStore.ClearSharedPreference();
//    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        //Return the ProgressBar while you download and install.
        loadingPanel.setVisibility(View.VISIBLE);
        //Autoupdate runs the command to install. You can put a listener or not
        updater.InstallNewVersion(null);

    }


}

