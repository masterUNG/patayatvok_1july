package series;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.royle.beartv2.Mxplayer;
import com.royle.beartv2.R;
import com.royle.beartv2.SearchSRActivity;

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
import user.ErrorActivity;
import meklib.MCrypt;
import user.RefillActivity;

public class SeriesDetailActivity extends Activity {

	//widget
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private TextView txtName;
	private ImageView img;
	private ImageButton btnSearch;
	private TextView txtSlide;
	private GridView gridView;

	private String series_id;
	private String name;
	private String pic_url;
	private String user_level;

	private String url_video;

	private DataStore dataStore;

	private ImageLoader imageLoader;
	private DisplayImageOptions displayImageOptions;
	private MCrypt mcrypt = new MCrypt();

	ProgressDialog progressDialog;

	private ArrayList<SeriesData> arrData = new ArrayList<>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_series_detail);

		dataStore = new DataStore(getBaseContext());
		progressDialog = new ProgressDialog(this);
		initWidget();
		getIntentValue();
		showContent();
		new DownloadTask().execute();
		txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));



	}

	@Override
	protected void onResume() {
		super.onResume();
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
		txtName.setText(name);
		imageLoader.displayImage(pic_url, img, displayImageOptions);


		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SeriesDetailActivity.this,SearchSRActivity.class);
				startActivity(intent);
			}
		});

	}
	public void initWidget(){
		txtUsername = (TextView) findViewById(R.id.txtUsername_seriesDetail);
		txtLevel = (TextView) findViewById(R.id.txtLevel_seriesDetail);
		txtExpire = (TextView) findViewById(R.id.txtExpire_seriesDetail);
		txtName = (TextView) findViewById(R.id.txtName_seriesDetail);
		img  = (ImageView) findViewById(R.id.img_seriesDetail);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
		gridView = (GridView) findViewById(R.id.gridView_series);
		gridView.setFocusable(true);
		gridView.setFocusableInTouchMode(true);
		gridView.requestFocus();
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);

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
		series_id = getIntent().getStringExtra("id");
		name = getIntent().getStringExtra("name");
		pic_url = getIntent().getStringExtra("pic");

		user_level = dataStore.LoadSharedPreference(DataStore.USER_LEVEL, "");
	}

	public void checkAcees(String user_id){
		new CheckAccessTask().execute(user_id);
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
				//////added  by  tony
				//		Intent intent = new Intent();

				//		ComponentName comp = new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity");
				//		ComponentName comp = new ComponentName("com.gretech.gomplayerko", "com.gretech.activities.GPlayerActivity");
				//		ComponentName comp = new ComponentName("vidon.me.player", "vidon.me.player.view.activity.CorePlayerActivity");
				//		ComponentName comp = new ComponentName("com.softwinner.TvdVideo", "com.softwinner.TvdVideo.TvdVideoActivity");
				//		ComponentName comp = new ComponentName("com.rockplayer", "com.rockplayer.player.RockPlayerActivity");
				//		ComponentName comp = new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.MovieActivity");
				//		ComponentName comp = new ComponentName("com.clov4r.moboplayer.android.nil", "com.clov4r.moboplayer.android.nil.MainInterface");
				//		ComponentName comp = new ComponentName("android.rk.RockVideoPlayer", "android.rk.RockVideoPlayer.VideoPlayActivity");
				//		ComponentName comp = new ComponentName("com.mxtech.videoplayer.ad", "com.mxtech.videoplayer.ad.ActivityScreen");


				//intent.setComponent(comp);
				//intent.setAction(Intent.ACTION_VIEW);
				//intent.setData(Uri.parse(url_video + "&signature_id=" + otpfn));
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.putExtra("SourceFrom","Network");
				//intent.putExtra("secure_uri", true);
				//startActivity(intent);

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
						intent.putExtra("SourceFrom","Network");
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
						intent.putExtra("SourceFrom","Network");
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
						intent.putExtra("SourceFrom","Network");
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
						intent.putExtra("SourceFrom","Network");
						intent.putExtra("secure_uri", true);
						startActivity(intent);


					} else {
						installgold atualizaApp = new installgold();
						atualizaApp.setContext(getApplicationContext());
						atualizaApp.execute("MXPlayer.apk");
						Toast.makeText(getApplicationContext(), "กรุณารอสักครู่ และกด  >> ติดตั้ง <<  ..........", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(SeriesDetailActivity.this, Mxplayer.class);
						startActivity(intent);
					}


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}else {

				Intent intent = new Intent(SeriesDetailActivity.this, ErrorActivity.class);
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
	public class DownloadTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			SeriesData seriesData = new SeriesData();
			arrData = seriesData.getSeries(series_id);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			SeriesDetailAdapter adapter = new SeriesDetailAdapter(getApplicationContext(), R.layout.row_series, arrData);
			gridView.setAdapter(adapter);
			if (arrData.size()!=0) {
				gridView.setSelection(0);
			}
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					if (true) {
						url_video = arrData.get(position).getseries_img();
						checkAcees(dataStore.LoadSharedPreference(DataStore.USER_ID, ""));
					} else {
						Intent intent = new Intent(SeriesDetailActivity.this, RefillActivity.class);
						startActivity(intent);
						Toast.makeText(getApplicationContext(), "ไม่สามารถเล่นหนังได้ กรุณาเติมเงิน.", Toast.LENGTH_LONG).show();
					}

				}
			});
		}

	}



}
