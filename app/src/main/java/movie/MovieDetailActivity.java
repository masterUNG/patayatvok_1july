package movie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.royle.beartv2.Mxplayer;
import com.royle.beartv2.R;
import com.royle.beartv2.SearchActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import user.ErrorActivity;
import user.LoginActivity;
import meklib.MCrypt;

public class MovieDetailActivity extends Activity {
	
	//widget
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private TextView txtName;
	private ImageView img;
	private ImageView imgAd;
	private Button btnLink;
	private Button btnLinkHd;
	private Button btnLinkMobile;
	private Button btn4k;
	private ImageButton btnSearch;
	private TextView txtSlide;
	private LinearLayout ln1;
	private LinearLayout ln2;
	
	private String name;
	private String pic_url;
	private String link;
	private String link_hd;
	private String link_mobile;
	private String link_4k;
	private String user_level;
	private int level_access;
	
	private String url_video;
	
	private DataStore dataStore;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions displayImageOptions;
	private MCrypt mcrypt = new MCrypt();
	
	ProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.movie_activity_movie_detail);
		
		dataStore = new DataStore(getBaseContext());
		progressDialog = new ProgressDialog(this);
		initWidget();
		getIntentValue();
		showContent();
		txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));

		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		user_level = dataStore.LoadSharedPreference(DataStore.USER_LEVEL_ID, "");
		checkUser();
		
	}
	
	public void checkUser(){
		if (true) {
			txtUsername.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtLevel.setVisibility(View.VISIBLE);
			txtUsername.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
			txtLevel.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
			txtExpire.setText("หมดอายุ : "+dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, ""));
		}else {
			txtUsername.setVisibility(View.GONE);
			txtExpire.setVisibility(View.GONE);
			txtLevel.setVisibility(View.GONE);
		}
	}
	
	public void showContent(){
		
		if (link.equals("no")) {
			btnLink.setVisibility(View.GONE);
		}
		if (link_hd.equals("no")) {
			btnLinkHd.setVisibility(View.GONE);
		}
		if (link_mobile.equals("no")) {
			btnLinkMobile.setVisibility(View.GONE);
		}
		if (link_4k.equals("no")) {
			btn4k.setVisibility(View.GONE);
		}
		
//		if (link.equals("no")&&link_hd.equals("no")) {
//			ln1.setVisibility(View.GONE)
//		}
		txtName.setText(name);
		imageLoader.displayImage(pic_url, img, displayImageOptions);
		imageLoader.displayImage(dataStore.LoadSharedPreference(DataStore.IMG_PATH, ""), imgAd, displayImageOptions);
		
		
		btnLink.setOnClickListener(new OnClickListener() {
			Intent intent;
			@Override
			public void onClick(View v) {
				if (true) {
					url_video = link;
					checkAcees(dataStore.LoadSharedPreference(DataStore.USER_ID, ""));
				}else {
					intent = new Intent(MovieDetailActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		btnLinkHd.setOnClickListener(new OnClickListener() {
			
			private Intent intent;

			@Override
			public void onClick(View v) {
				if (true) {
					url_video = link_hd;
					checkAcees(dataStore.LoadSharedPreference(DataStore.USER_ID, ""));
				}else {
					intent = new Intent(MovieDetailActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		
		btnLinkMobile.setOnClickListener(new OnClickListener() {
			
			private Intent intent;

			@Override
			public void onClick(View v) {

				if (true) {
					url_video = link_mobile;
					checkAcees(dataStore.LoadSharedPreference(DataStore.USER_ID, ""));
				}else {
					intent = new Intent(MovieDetailActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			
			}
		});
		btn4k.setOnClickListener(new OnClickListener() {
			
			private Intent intent;

			@Override
			public void onClick(View v) {

				if (true) {
					url_video = link_4k;
					checkAcees(dataStore.LoadSharedPreference(DataStore.USER_ID, ""));
				}else {
					intent = new Intent(MovieDetailActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			
			}
		});
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MovieDetailActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		
	}
	public void initWidget(){
		txtUsername = (TextView) findViewById(R.id.txtUsername_movieDetail);
		txtLevel = (TextView) findViewById(R.id.txtLevel_movieDetail);
		txtExpire = (TextView) findViewById(R.id.txtExpire_movieDetail);
		txtName = (TextView) findViewById(R.id.txtName_movieDetail);
		img  = (ImageView) findViewById(R.id.img_movieDetail);
		imgAd = (ImageView) findViewById(R.id.imgAd_movieDetail);
		btnLink = (Button) findViewById(R.id.btnLink_movieDetail);
		btnLink.setFocusable(true);
		btnLink.setFocusableInTouchMode(true);
		btnLink.requestFocus();
		btnLinkHd = (Button) findViewById(R.id.btnLinkHd_movieDetail);
		btnLinkMobile = (Button) findViewById(R.id.btnLinkMobile_movieDetail);
		btn4k = (Button) findViewById(R.id.btn4k_movieDetail);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);
		ln1 = (LinearLayout) findViewById(R.id.ln1_movie);
		ln2 = (LinearLayout) findViewById(R.id.ln2_movie);
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		displayImageOptions = new DisplayImageOptions.Builder()
		.showImageOnFail(R.drawable.ic_default)
		.showImageForEmptyUri(R.drawable.ic_default)
		.cacheOnDisc(true)
		.cacheInMemory(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	public void getIntentValue(){
		name = getIntent().getStringExtra("name");
		pic_url = getIntent().getStringExtra("pic");
		link = getIntent().getStringExtra("link");
		link_hd = getIntent().getStringExtra("link_hd");
		link_mobile = getIntent().getStringExtra("link_mobile");
		link_4k = getIntent().getStringExtra("link_4k");
		level_access = Integer.valueOf(getIntent().getStringExtra("level_access"));

		
	}
	
	
	public void checkAcees(String user_id){
		int level = Integer.valueOf(user_level);
		if (level>=level_access) {
			new CheckAccessTask().execute(user_id);
		}else {
			if (level_access==1) {

				Toast.makeText(getApplicationContext(), "Level  Member up", Toast.LENGTH_LONG).show();
			}else if (level_access==2) {

				Toast.makeText(getApplicationContext(), "Level Silver up", Toast.LENGTH_LONG).show();
			}else if (level_access==3) {

				Toast.makeText(getApplicationContext(), "Level Gold up", Toast.LENGTH_LONG).show();
			}else if (level_access==4) {

				Toast.makeText(getApplicationContext(), "Level Platinum up", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class CheckAccessTask extends AsyncTask<String, Void, Void>{
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
			String resultData = portalServices.makePortalCall(null, UrlApp.CHECK_EXPIRED+params[0]+"/"+dataStore.LoadSharedPreference(DataStore.USER_TOKEN, ""), PortalServices.GET);
			try {
				String decrypted = new String(mcrypt.decrypt(resultData));
				JSONObject jsonObject = new JSONObject(decrypted);
				String decrypted2 = new String(mcrypt.decrypt(otpData));
				JSONObject jsonObject2 = new JSONObject(decrypted2);
				String getotp = jsonObject2.getString("otp");
				otpfn = MCrypt.bytesToHex( mcrypt.encrypt(getotp));

				if (jsonObject.has("status")) {
					String status = jsonObject.getString("status");
					if (status.equals("1")) {
						check = true;
					}else {
						check = false;
					}
				}else {
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
			if (check) {

				// TODO Auto-generated method stub
				try {

					if (playerInstalledOrNot("com.mxtech.videoplayer.pro")) {
						//////added  by  tony
						Intent intent = new Intent();
						ComponentName comp = new ComponentName("com.mxtech.videoplayer.pro", "com.mxtech.videoplayer.ActivityScreen");

						intent.setComponent(comp);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url_video + "&signature_id=" + otpfn));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("SourceFrom", "Network");
						intent.putExtra("secure_uri", true);
						startActivity(intent);

					} else if (playerInstalledOrNot("com.mxtech.videoplayer.ad")) {
						//////added  by  tony
						Intent intent = new Intent();
						ComponentName comp = new ComponentName("com.mxtech.videoplayer.ad", "com.mxtech.videoplayer.ad.ActivityScreen");

						intent.setComponent(comp);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url_video + "&signature_id=" + otpfn));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("SourceFrom", "Network");
						intent.putExtra("secure_uri", true);
						startActivity(intent);

					} else if (playerInstalledOrNot("com.android.gallery3d")) {
						//////added  by  tony
						Intent intent = new Intent();
						ComponentName comp = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivity");

						intent.setComponent(comp);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url_video + "&signature_id=" + otpfn));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("SourceFrom", "Network");
						intent.putExtra("secure_uri", true);
						startActivity(intent);


					} else if (playerInstalledOrNot("com.mxtech.videoplayer.gold")) {
						//////added  by  tony
						Intent intent = new Intent();
						ComponentName comp = new ComponentName("com.mxtech.videoplayer.gold", "com.mxtech.videoplayer.ActivityScreen");

						intent.setComponent(comp);
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url_video + "&signature_id=" + otpfn));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("SourceFrom", "Network");
						intent.putExtra("secure_uri", true);
						startActivity(intent);


					} else {
						installgold atualizaApp = new installgold();
						atualizaApp.setContext(getApplicationContext());
						atualizaApp.execute("MXPlayer.apk");
						Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MovieDetailActivity.this, Mxplayer.class);
						startActivity(intent);
				}


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}else {

				Intent intent = new Intent(MovieDetailActivity.this, ErrorActivity.class);
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
