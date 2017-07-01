package iptv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.royle.beartv2.Mxplayer;
import com.royle.beartv2.R;
import com.royle.beartv2.SearchActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;
import user.ErrorActivity;
import user.LoginActivity;

public class TvCategoryActivity extends Activity {
    // widget
    private GridView gridView;
    private TextView txtUsername;
    private TextView txtLevel;
    private TextView txtExpire;
    private ImageButton btnSearch;
    private TextView txtSlide;
    private MCrypt mcrypt = new MCrypt();

    private ProgressDialog progressDialog;

    private ArrayList<IpTvData> arrData = new ArrayList<IpTvData>();
    private String main_id;
    private String id_tv;
    private String tag = "1JulyV2";

    DataStore dataStore;

    public String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iptv_activity_tv_category);

        progressDialog = new ProgressDialog(this);
        dataStore = new DataStore(getBaseContext());
       // Log.d("dataStore ==> " + dataStore.toString())


        main_id = getIntent().getStringExtra("main_id");
        id_tv = getIntent().getStringExtra("id");
        Log.d("1JulyV2", "main_id Receive ==> " + main_id);
        Log.d("1JulyV2", "id_tv Receive ==> " + id_tv);


        if (main_id != null) {
            loadData(main_id);
        } else {
            //ยังไง ก็ทำที่นี่
            loadData(id_tv);
        }

        txtUsername = (TextView) findViewById(R.id.txtUsername_iptvCat);
        txtLevel = (TextView) findViewById(R.id.txtLevel_iptvCat);
        txtExpire = (TextView) findViewById(R.id.txtExpire_iptvCat);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
        txtSlide = (TextView) findViewById(R.id.txtSlide);
        txtSlide.setSelected(true);

        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TvCategoryActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });

    }   // Main Method

    @Override
    protected void onResume() {
        super.onResume();
        checkUser();
    }

    public void checkUser() {
        if (true) {
            txtUsername.setVisibility(View.VISIBLE);
            txtExpire.setVisibility(View.VISIBLE);
            txtUsername.setText(": " + dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
            txtLevel.setText(": " + dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
            txtExpire.setText("หมดอายุxxxxx : " + dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, "xxx"));
            //	txtExpire.setText("หมดอายุxxxxx : " + dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, ""));
        } else {
            txtUsername.setVisibility(View.GONE);
            txtExpire.setVisibility(View.GONE);
            txtLevel.setVisibility(View.GONE);
        }
    }

    public void showContent() {
        txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));
        gridView = (GridView) findViewById(R.id.gridView_tvCat);
        gridView.setFocusable(true);
        gridView.setFocusableInTouchMode(true);
        gridView.requestFocus();
        IpTvCategoryAdapter adapter = new IpTvCategoryAdapter(getBaseContext(),
                R.layout.movie_row, arrData);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            Intent intent;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (main_id != null) {
                    intent = new Intent(TvCategoryActivity.this,
                            TvCategoryActivity.class);
                    intent.putExtra("id", arrData.get(position).getTv_id());
                    startActivity(intent);
                } else {
                    //ต้องทำ ที่นี่
                    if (true) {

                        link = arrData.get(position).getTv_link();
                        Log.d(tag, "link ==> " + link);

                        checkAcees(dataStore.LoadSharedPreference(
                                DataStore.USER_ID, ""));
                    } else {
                        intent = new Intent(TvCategoryActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void loadData(String main_id) {
        new DownloadDataTask().execute(main_id);
    }

    public class DownloadDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            IpTvData data = new IpTvData(getBaseContext());

            if (main_id != null) {
                arrData = data.getCategory(params[0]);
            } else {
                //จะทำงานตรงนี่เสมอ
                arrData = data.getList(params[0]);
                Log.d(tag, "arrData ==> " + arrData);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            showContent();
        }

    }

    public void checkAcees(String user_id) {

        Log.d(tag, "user_id ==> " + user_id);

        new CheckAccessTask().execute(user_id);
       // new CheckAccessTask().execute("2");
    }

    public class CheckAccessTask extends AsyncTask<String, Void, Void> {
        Boolean check;
        String otpfn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            PortalServices portalServices = new PortalServices();
            String otpData = portalServices.makePortalCall(null, UrlApp.OTP, PortalServices.GET);
            String resultData = portalServices.makePortalCall(null,
                    UrlApp.CHECK_EXPIRED + params[0] + "/" + dataStore.LoadSharedPreference(DataStore.USER_TOKEN, ""), PortalServices.GET);
            String lastResultData = "13e6b0014d8cb6acc72edc8f2cbd8aa786a66a84d86d774ee5a90f5dbfc50f0f";

            if (resultData.length() == 0) {
                resultData = lastResultData;
            }
            Log.d(tag, "resultData ==> " + resultData);
            try {
                String decrypted = new String(mcrypt.decrypt(resultData));
                JSONObject jsonObject = new JSONObject(decrypted);

                String decrypted2 = new String(mcrypt.decrypt(otpData));
                JSONObject jsonObject2 = new JSONObject(decrypted2);
                String getotp = jsonObject2.getString("otp");

                //Start Hear
                otpfn = MCrypt.bytesToHex(mcrypt.encrypt(getotp));
                Log.d(tag, "otpfn Calculated => " + otpfn);

                if (jsonObject.has("status")) {
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        check = true;
                    } else {
                        check = false;
                    }
                } else {
                    check = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                check = false;
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (true) {

                Log.d(tag, "onPost Working");

                // TODO Auto-generated method stub
                try {

                    Log.d(tag, "in try link ==> " + link);
                    Log.d(tag, "in try otpfn ==> " + otpfn);

                    if (playerInstalledOrNot("com.mxtech.videoplayer.pro")) {
                        //////added  by  tony
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.mxtech.videoplayer.pro", "com.mxtech.videoplayer.ActivityScreen");

                        intent.setComponent(comp);
                        intent.setAction(Intent.ACTION_VIEW);

                        if (otpfn.length() == 0) {
                            //otpfn = null
                            String s = "http://go.bear-tv.com/4k/iptv/playlist.m3v8?channel_id=1037&signature_id=0c06134caad9d753c68e43f35aadb72d37f802e5d5ac7e0177cc7d08d4854c316608fd4063982f68a1a96895c20828f8";
                            intent.setData(Uri.parse(s));

                        } else {
                            intent.setData(Uri.parse(link + "&signature_id=" + otpfn));
                            Log.d(tag, "URL ==> " + (link + "&signature_id=" + otpfn));
                        }






                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("SourceFrom", "Network");
                        intent.putExtra("secure_uri", true);
                        intent.putExtra("video_zoom", 0);
                        startActivity(intent);


                    } else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {
                        //////added  by  tony
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.mxtech.videoplayer.ad", "com.mxtech.videoplayer.ad.ActivityScreen");

                        intent.setComponent(comp);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link + "&signature_id=" + otpfn));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("SourceFrom", "Network");
                        intent.putExtra("secure_uri", true);
                        intent.putExtra("video_zoom", 0);
                        startActivity(intent);

                    } else if (playerInstalledOrNot("com.android.gallery3d")) {
                        //////added  by  tony
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivity");

                        intent.setComponent(comp);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link + "&signature_id=" + otpfn));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("SourceFrom", "Network");
                        intent.putExtra("secure_uri", true);
                        intent.putExtra("video_zoom", 0);
                        startActivity(intent);

                    } else if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {
                        //////added  by  tony
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("com.mxtech.videoplayer.gold", "com.mxtech.videoplayer.ActivityScreen");

                        intent.setComponent(comp);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link + "&signature_id=" + otpfn));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("SourceFrom", "Network");
                        intent.putExtra("secure_uri", true);
                        intent.putExtra("video_zoom", 0);
                        startActivity(intent);

                    } else if (playerInstalledOrNot("org.videolan.vlc")) {
                        //////added  by  tony
                        Intent intent = new Intent();
                        ComponentName comp = new ComponentName("org.videolan.vlc", "org.videolan.vlc.ActivityScreen");

                        intent.setComponent(comp);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link + "&signature_id=" + otpfn));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("SourceFrom", "Network");
                        intent.putExtra("secure_uri", true);
                        intent.putExtra("video_zoom", 0);
                        startActivity(intent);


                    } else {
                        installgold atualizaApp = new installgold();
                        atualizaApp.setContext(getApplicationContext());
                        atualizaApp.execute("MXPlayer.apk");
                        Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TvCategoryActivity.this, Mxplayer.class);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(tag, "e tryCast ==> " + e.toString());
                }


            } else {
                //Show When check ==> False
                Intent intent = new Intent(TvCategoryActivity.this, ErrorActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "การเชื่อมต่อ Internet ของท่านไม่เสถียร กรุณาลองใหม่อีกครั้งในภายหลัง", Toast.LENGTH_LONG).show();
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
                    String link = "http://pnsat.com/mx/" + nameapp;
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


//					try
//					{
//						Runtime.getRuntime().exec(new String[]{"su", "-c", "pm install -r /mnt/sdcard/Download/" + nameapp});
//
//					}
//					catch (IOException e)
//					{
//						Intent intent = new Intent(Intent.ACTION_VIEW);
//						intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/" + nameapp)), "application/vnd.android.package-archive");
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
//						context.startActivity(intent);
//					}

                } catch (Exception e) {
                    Log.e("UpdateAPP", "Update error! " + e.getMessage());
                }
                return null;
            }
        }


    }


}
