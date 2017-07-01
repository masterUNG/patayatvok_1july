package com.royle.beartv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Naifun
 */
public class SplashScreen extends Activity {

    //Explicit
    private final static int UNINSTALL_REQUEST_CODE = 0;
    private final static String m_sPackageToUninstall = "com.cmiptv.app"; //old client to be uninstall
    private SharedPreferences appSettings;
    private final static String appname = "beartv";
    private final static String Shortcutname = "beartv";
    static final int DIALOG_ERROR_CONNECTION = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //Check Internet onLine ? True ==> Internet OK, False ==> Internet False

        if (!isOnline(this)) {

            //Internet False
            showDialog(DIALOG_ERROR_CONNECTION); //displaying the created dialog.
            super.onPause();

        } else {
            //Internet available.

            //ไปดึกค่า ที่บันทึกไว้ในเครื่องว่า มีการ สร้าง ShortCut หรือยัง ?
            appSettings = getSharedPreferences(appname, MODE_PRIVATE);

            //Check Create Shortcut at Home Desktop ?
            if (!appSettings.getBoolean("shortcut", false)) {
                addShortcut();
            }

//        logout(SplashScreen.this);
            Intent intent = new Intent(SplashScreen.this, ServiceCheck.class);
            stopService(intent);

            // Check ว่ามีแอฟที่ใช้ package ==> com.cmiptv.app
            // อยู่ในเครื่องไหม ? ถ้ามี จะเข้าหน้า ถอดแอฟของ Setting
            checkOldApp();

        }   // if

    }   // Main Method

    private void checkOldApp() {

        try {
            PackageManager oPackageManager = getPackageManager();
            PackageInfo oPackageInfo = oPackageManager
                    .getPackageInfo(m_sPackageToUninstall, PackageManager.GET_ACTIVITIES);

            if (oPackageInfo != null) {

                Uri oPackageUri = Uri.parse("package:" + m_sPackageToUninstall);
                Intent oIntent = new Intent(Intent.ACTION_DELETE, oPackageUri);
                // wait till the uninstall is not completed
                startActivityForResult(oIntent, UNINSTALL_REQUEST_CODE);
                SystemClock.sleep(8000);

                launchSplash();


            } else {
                //launchApp();
                launchSplash();
            }
        } catch (PackageManager.NameNotFoundException e) {
            //launchApp();
            launchSplash();
        }
    }

    //Check Internet onLine
    public boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            //Internet OK can Connected Server
            return true;
        else
            // Internet False
            return false;

    }   // isOnline

    //การสร้าง Shortcut บน Desktop
    private void addShortcut() {

        Intent shortcutIntent = new Intent(getApplicationContext(), SplashScreen.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, Shortcutname);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.drawable.logo));
        addIntent.putExtra("duplicate", false);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

        //คือการบันทึกค่า true ไปที่ key shortcut
        SharedPreferences.Editor prefEditor = appSettings.edit();
        prefEditor.putBoolean("shortcut", true);
        prefEditor.commit();

        Toast.makeText(getApplicationContext(), "กำลังสร้าง... Shortcut", Toast.LENGTH_SHORT).show();

    }   // addShortcut


    private void launchSplash() {


        // Launch the new client
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView t = (TextView) findViewById(R.id.lin_lay);
        t.clearAnimation();
        t.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView ix = (TextView) findViewById(R.id.lin_lay);
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ix.clearAnimation();
        ix.startAnimation(anim);

        Animation animx = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animx.reset();
        ImageView lx = (ImageView) findViewById(R.id.logo);
        lx.clearAnimation();
        lx.startAnimation(animx);
        animx = AnimationUtils.loadAnimation(this, R.anim.translate);
        animx.reset();


        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreen.this, UpdateCheck.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

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
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    // ถ้าถอดแอฟเก่าเสร็จแล้ว ให้เล่น Animate
    protected void onActivityResult(int nRequestCode, int nResultCode, Intent oIntent) {
        super.onActivityResult(nRequestCode, nResultCode, oIntent);

        if (nRequestCode == UNINSTALL_REQUEST_CODE) {
            launchSplash();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case DIALOG_ERROR_CONNECTION:

                AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
                errorDialog.setTitle("ไม่ได้เชื่อมต่อ INTERNET");
                errorDialog.setMessage("กรุณาเชื่อมต่อ WIFI ก่อนแล้วเข้าแอพอีกครั้ง");
                errorDialog.setNeutralButton("ตกลง",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }
                        });

                AlertDialog errorAlert = errorDialog.create();
                return errorAlert;

            default:
                break;
        }
        return dialog;

    }   // onCreateDialog


}
