package com.royle.beartv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import helper.DataStore;
import helper.PortalServices;
import iptv.IpTvActivity;
import meklib.MCrypt;
import movie.MovieActivity;
import movie.MovieData;
import movie.MovieDetailActivity;
import series.SeriesActivity;
import user.LoginActivity;
import user.RefillActivity;
import user.RegisterActivity;
import user.SupportActivity;
import user.UserProfileActivity;

import static com.royle.beartv2.SplashScreen.DIALOG_ERROR_CONNECTION;

public class MainActivity extends FragmentActivity {
    // widget
    private ImageButton btnUserProfile;
    private ImageButton btnRefill;
    private Button btnPayPal;
    private Button btnCode;
    private ImageButton btnSupport;
    private ImageButton btnSearch;

    private ImageButton imbTv;
    private ImageButton imbMovie;
    private ImageButton imbSeries;
    private ImageButton imbKaraoke;
    private ImageButton imbEbook;

    private ImageButton imgUpdate1;
    private ImageButton imgUpdate2;
    private ImageButton imgUpdate3;
    private MCrypt mcrypt = new MCrypt();


    private TextView txtSlide;
    SliderLayout sliderLayout;

    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;

    Intent intent;

    private ArrayList<MovieData> arrData = new ArrayList<MovieData>();
    private ArrayList<OtherData> arrDataSlide = new ArrayList<OtherData>();
    //public ProgressDialog progressDialog;

    private DataStore dataStore;
    private String username;
    private String password;
    private TextView txtExpire;
    public PortalServices portalServices;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initWidget();

        //progressDialog = new ProgressDialog(this);
        portalServices = new PortalServices();
        dataStore = new DataStore(getBaseContext());

        username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");
        password = dataStore.LoadSharedPreference(DataStore.PASSWORD, "");
        txtExpire = (TextView) findViewById(R.id.txtExpire_main);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));

        }


        btnRefill.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                initWidget();
                if (true) {
                    Intent intent = new Intent(MainActivity.this, RefillActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }

                //        new DialogComingFragment().show(getSupportFragmentManager(), "Coming");


            }
        });

//        btnPayPal.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (true) {
//                    intent = new Intent(MainActivity.this, WebViewPayPalActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//                } else {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            }
//        });

//        btnCode.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (true) {
//                    dialogCode();
//                } else {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            }
//        });

        imageButtonTV();

        imbMovie.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (dataStore.checkUser()) {
  //              if (true) {
                    intent = new Intent(MainActivity.this, MovieActivity.class);
                    startActivity(intent);

                    // TODO Auto-generated method stub
                    try {
                        if (playerInstalledOrNot("com.mxtech.videoplayer.pro1")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {

                        } else if (playerInstalledOrNot("com.android.gallery3d")) {


                        } else {
                            installgold atualizaApp = new installgold();
                            atualizaApp.setContext(getApplicationContext());
                            atualizaApp.execute("MXPlayer.apk");
                            Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Mxplayer.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }

            }
        });
        imbSeries.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (true) {
                    intent = new Intent(MainActivity.this, SeriesActivity.class);
                    startActivity(intent);

                    // TODO Auto-generated method stub
                    try {
                        if (playerInstalledOrNot("com.mxtech.videoplayer.pro")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {

                            Toast.makeText(getApplicationContext(), "มี GOLDPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.android.gallery3d")) {


                        } else {
                            installgold atualizaApp = new installgold();
                            atualizaApp.setContext(getApplicationContext());
                            atualizaApp.execute("MXPlayer.apk");
                            Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Mxplayer.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }

            }
        });

        imbKaraoke.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SportActivity.class));


            }

            // TODO Auto-generated method stub
//                try {
//                    if (playerInstalledOrNot("com.ais.mimo.AISOnAirTV")) {
//
//                        Toast.makeText(getApplicationContext(), "กรุณารอสักครู่...........", Toast.LENGTH_SHORT).show();
//                        Intent ais = new Intent(Intent.ACTION_MAIN);
//                        ais.setComponent(new ComponentName("com.ais.mimo.AISOnAirTV", "com.singtel.ais.Sports"));
//                        ais.addCategory(Intent.CATEGORY_LAUNCHER);
//                        startActivity(ais);
//
//                    } else {
//                        installgold atualizaAppS = new installgold();
//                        atualizaAppS.setContext(getApplicationContext());
//                        atualizaAppS.execute("AISPLAYBOX.apk");
//                        Toast.makeText(getApplicationContext(), "กรุณากด  >> ติดตั้ง <<  ...........", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }

        });


        imbEbook.setOnClickListener(new OnClickListener() {

//            @Override
//            public void onClick(View v) {

//                if (dataStore.checkUser()) {
//                    intent = new Intent(MainActivity.this, EbookActivity.class);
//                    startActivity(intent);
//                } else {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

//                }
//            }
//        });

            @Override
            public void onClick(View v) {
                new DialogComingFragment().show(getSupportFragmentManager(), "Coming");
            }
        });


        btnSupport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SupportActivity.class);
                startActivity(intent);

            }


        });


        clickForMember();


        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }   // Main Method

    private void clickForMember() {
        btnUserProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (true) {
                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void imageButtonTV() {

        imbTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //Check ว่ามี id user หรือเปล่า ถ้ามี True ไม่มี false
//                if (dataStore.checkUser()) {
                if (true) {

                    intent = new Intent(MainActivity.this, IpTvActivity.class);
                    intent.putExtra("id", "2");
                    startActivity(intent);

                    // TODO Auto-generated method stub
                    try {
                        if (playerInstalledOrNot("com.mxtech.videoplayer.pro")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {

                            Toast.makeText(getApplicationContext(), "มี MXPLAYER ในระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();

                        } else if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {

                        } else if (playerInstalledOrNot("com.android.gallery3d")) {

                        } else if (playerInstalledOrNot("org.videolan.vlc")) {


                        } else {
                            installgold atualizaApp = new installgold();
                            atualizaApp.setContext(getApplicationContext());
                            atualizaApp.execute("MXPlayer.apk");
                            Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Mxplayer.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }


            }
        });
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
//                String link = "http://4kmoviestar.com/New_Apk/" + nameapp;
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


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");
        new DownloadLastMovie().execute();
        checkUser();
    }

    public void checkUser() {
        if (true) {

            txtExpire.setVisibility(View.VISIBLE);

            txtExpire.setText("หมดอายุ : " + dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, ""));
            txtExpire.setTextColor(Color.GREEN);
        } else {

            //txtExpire.setVisibility(View.GONE);
            txtExpire.setText("เลือกหนังเพื่อเข้าสู่ระบบ");
            txtExpire.setTextColor(Color.RED);


        }
    }

    public void showContent() {


        txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));
        if (arrData.size() == 1) {
            imageLoader.displayImage(arrData.get(0).getMovie_img(), imgUpdate1, displayImageOptions);
            imgUpdate2.setVisibility(View.INVISIBLE);
            imgUpdate3.setVisibility(View.INVISIBLE);
            imgUpdate1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(0).getMovie_name());
                        intent.putExtra("pic", arrData.get(0).getMovie_img());
                        intent.putExtra("link", arrData.get(0).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(0).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(0).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(0).getMovie_link_4k());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }

                }
            });

        }
        if (arrData.size() == 2) {
            imageLoader.displayImage(arrData.get(0).getMovie_img(), imgUpdate1,
                    displayImageOptions);
            imageLoader.displayImage(arrData.get(1).getMovie_img(), imgUpdate2,
                    displayImageOptions);
            imgUpdate3.setVisibility(View.INVISIBLE);
            imgUpdate1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(0).getMovie_name());
                        intent.putExtra("pic", arrData.get(0).getMovie_img());
                        intent.putExtra("link", arrData.get(0).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(0).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(0).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(0).getMovie_link_4k());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }

                }
            });
            imgUpdate2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(1).getMovie_name());
                        intent.putExtra("pic", arrData.get(1).getMovie_img());
                        intent.putExtra("link", arrData.get(1).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(1).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(1).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(1).getMovie_link_4k());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }

                }
            });

        }
        if (arrData.size() == 3) {
            imageLoader.displayImage(arrData.get(0).getMovie_img(), imgUpdate1,
                    displayImageOptions);
            imageLoader.displayImage(arrData.get(1).getMovie_img(), imgUpdate2,
                    displayImageOptions);
            imageLoader.displayImage(arrData.get(2).getMovie_img(), imgUpdate3,
                    displayImageOptions);
            imgUpdate1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(0).getMovie_name());
                        intent.putExtra("pic", arrData.get(0).getMovie_img());
                        intent.putExtra("link", arrData.get(0).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(0).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(0).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(0).getMovie_link_4k());
                        intent.putExtra("level_access", arrData.get(0).getLevel_access());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }

                }
            });
            imgUpdate2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(1).getMovie_name());
                        intent.putExtra("pic", arrData.get(1).getMovie_img());
                        intent.putExtra("link", arrData.get(1).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(1).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(1).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(1).getMovie_link_4k());
                        intent.putExtra("level_access", arrData.get(1).getLevel_access());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }

                }
            });
            imgUpdate3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (true) {
                        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                        intent.putExtra("name", arrData.get(2).getMovie_name());
                        intent.putExtra("pic", arrData.get(2).getMovie_img());
                        intent.putExtra("link", arrData.get(2).getMovie_link());
                        intent.putExtra("link_hd", arrData.get(2).getMovie_link_hd());
                        intent.putExtra("link_mobile", arrData.get(2).getMovie_link_mobile());
                        intent.putExtra("link_4k", arrData.get(2).getMovie_link_4k());
                        intent.putExtra("level_access", arrData.get(2).getLevel_access());
                        startActivity(intent);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                }
            });
            sliderLayout.removeAllSliders();
            for (int i = 0; i < arrDataSlide.size(); i++) {
                final int j = i;
                DefaultSliderView defaultSliderView = new DefaultSliderView(
                        getBaseContext());
                defaultSliderView.image(arrDataSlide.get(i).getImgShow())
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(new OnSliderClickListener() {

                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                Intent intent = new Intent(MainActivity.this,
                                        NewsActivity.class);
                                intent.putExtra("img_path", arrDataSlide.get(j)
                                        .getImgShow());
                                intent.putExtra("news", arrDataSlide.get(j)
                                        .getNews_text());
                                startActivity(intent);
                            }
                        });
                sliderLayout.addSlider(defaultSliderView);
            }
        }
    }

    public void initWidget() {
        btnUserProfile = (ImageButton) findViewById(R.id.btnSetting_main);
        btnRefill = (ImageButton) findViewById(R.id.btnRefill_main);
//        btnPayPal = (Button) findViewById(R.id.btnPayPal);
//        btnCode = (Button) findViewById(R.id.btnCode);
        btnSupport = (ImageButton) findViewById(R.id.btnSupport_main);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);

        imbTv = (ImageButton) findViewById(R.id.imbTv_main);
        imbTv.setFocusable(true);
        imbTv.setFocusableInTouchMode(true);
        imbTv.requestFocus();
        imbMovie = (ImageButton) findViewById(R.id.imbMovie_main);
        imbSeries = (ImageButton) findViewById(R.id.imbSeries_main);
        imbKaraoke = (ImageButton) findViewById(R.id.imbKaraoke_main);
        imbEbook = (ImageButton) findViewById(R.id.imbEbook_main);

        imgUpdate1 = (ImageButton) findViewById(R.id.imgUpdate1_main);
        imgUpdate2 = (ImageButton) findViewById(R.id.imgUpdate2_main);
        imgUpdate3 = (ImageButton) findViewById(R.id.imgUpdate3_main);

        sliderLayout = (SliderLayout) findViewById(R.id.slider);


        txtSlide = (TextView) findViewById(R.id.txtSlide);
        txtSlide.setSelected(true);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_fail)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnLoading(R.drawable.ic_load)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .postProcessor(null)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(200))
                .build();
    }   // Initial View

    public int getWidth(Context context) {
        WindowManager windowm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }


    public class DownloadLastMovie extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//			progressDialog.show();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            MovieData movieData = new MovieData();
            arrData = movieData.getLastMovie();
            OtherData otherData = new OtherData(getBaseContext());
            otherData.getOther();
            arrDataSlide = otherData.getSlide();
            /*if (!username.equals("")) {
				LoginApp(username, password);
			}*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//			progressDialog.dismiss();
//            progressBar.setVisibility(View.GONE);
            showContent();
        }

    }

    public static class DialogComingFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.logo)
                    .setTitle("ยังไม่เปิดให้บริการ..")
                    .setPositiveButton("ปิด",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
            return builder.create();
        }
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    private static String macAddress = "";

    public static String getMacAddress(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        //String macAddress = info.getMacAddress();

        if (wifiInfo != null) {
            macAddress = wifiInfo.getMacAddress();

        } else {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return macAddress;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

   /* public void LoginApp(String username, String password) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("username", username));
        list.add(new BasicNameValuePair("password", password));
        list.add(new BasicNameValuePair("macAddress", getMacAddres(MainActivity.this)));
        String resultData = portalServices.makePortalCall(null, UrlApp.LOGIN,
                PortalServices.POST, list);
        Log.e("login", resultData);
        try {
            String decrypted = new String(mcrypt.decrypt(resultData));
            JSONObject jsonObject = new JSONObject(decrypted);
            if (jsonObject.has("status")) {

                String status = jsonObject.getString("status");
                if (status.equals("success")) {
                    JSONObject jData = jsonObject.getJSONObject("data");
                    String user_id = jData.getString("id");
                    String username1 = jData.getString("username");
                    String expiry_date = jData.getString("expiry_date");
                    String level = jData.getString("level_id");
                    String token = jData.getString("token");
                    DataStore dataStore = new DataStore(getApplicationContext());
                    dataStore.SavedSharedPreference(DataStore.USER_ID, user_id);
                    dataStore.SavedSharedPreference(DataStore.USER_NAME,
                            username1);
                    dataStore.SavedSharedPreference(DataStore.USER_EXPIRE,
                            expiry_date);
                    dataStore.SavedSharedPreference(DataStore.USER_LEVEL_ID,
                            level);
                    dataStore.SavedSharedPreference(DataStore.USER_TOKEN, token);
                    if (level.equals("1")) {
                        dataStore.SavedSharedPreference(DataStore.USER_LEVEL,
                                "Member");
                    } else if (level.equals("2")) {
                        dataStore.SavedSharedPreference(DataStore.USER_LEVEL,
                                "Silver");
                    } else if (level.equals("3")) {
                        dataStore.SavedSharedPreference(DataStore.USER_LEVEL,
                                "Gold");
                    } else if (level.equals("4")) {
                        dataStore.SavedSharedPreference(DataStore.USER_LEVEL,
                                "Platinum");
                    }
                } else {
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ไม่สามารถเข้าสู่ระบบได้", Toast.LENGTH_LONG).show();
        }
    }*/


    @Override
    public void onBackPressed() {
        //finish();
        //System.exit(0);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากแอพ");
        dialog.setIcon(R.drawable.logo);
        dialog.setCancelable(true);
        dialog.setMessage("ต้องการออกจากแอพใช่หรือไม่?");
        dialog.setPositiveButton("ไม่", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.setNegativeButton("ออกจากแอพ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {


                logout(MainActivity.this);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());


            }
        });

        dialog.show();
    }

    public static void logout(Context context) {
        DataStore dataStore = new DataStore(context);
        dataStore.SavedSharedPreference(DataStore.USER_ID, "");
        dataStore.SavedSharedPreference(DataStore.USER_NAME, "");
        dataStore.SavedSharedPreference(DataStore.PASSWORD, "");
        dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "");
        dataStore.SavedSharedPreference(DataStore.USER_LEVEL_ID, "");
        dataStore.SavedSharedPreference(DataStore.USER_EXPIRE, "");
        dataStore.SavedSharedPreference(DataStore.USER_TOKEN, "");
        dataStore.ClearSharedPreference();
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;
        switch (id) {

            case DIALOG_ERROR_CONNECTION:

                AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
                errorDialog.setTitle("ไม่ได้เชื่อมต่อ INTERNET");
                errorDialog.setMessage("กรุณาเชื่อมต่อ WIFI ก่อนแล้วเข้าแอพอีกครั้ง");
                errorDialog.setNeutralButton("ตกลง", new DialogInterface.OnClickListener() {

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

        }   // swift
        return dialog;

    } // onCreate Dialog

    private void dialogCode() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_code, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edtCode);
        dialogBuilder.setPositiveButton("ตกลง", null)
                .setNegativeButton("ยกเลิก", null);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = editText.getText().toString().trim();
                        DataStore dataStore = new DataStore(getApplicationContext());
                        String userId = dataStore.LoadSharedPreference(DataStore.USER_ID, "");
                        new RefillCodeTask(userId, code).execute();
                    }
                });
            }
        });

        alertDialog.show();
    }

    public class RefillCodeTask extends AsyncTask<Void, Void, String> {
        private String code;
        private String userId;

        public RefillCodeTask(String code, String userId) {
            this.code = code;
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("id", code));
            list.add(new BasicNameValuePair("code", userId));
            String resultData = portalServices.makePortalCall(null, "http://saleilike.4kmoviestar.com/code/re",
                    PortalServices.POST, list);
            Log.e("refill_code", resultData);
            return resultData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int status = jsonObject.getInt("status");
                if (status == 0) { //incorrect
                    String txt = jsonObject.getString("txt");
                    Toast.makeText(MainActivity.this, txt, Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
