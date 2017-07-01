package ebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.royle.beartv2.R;
import com.royle.beartv2.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import user.LoginActivity;
import user.RefillActivity;

public class EbookCategoryActivity extends Activity {
	// widget
	private GridView gridView;
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private ImageButton btnSearch;
	private TextView txtSlide;

	private ProgressDialog progressDialog;

	private ArrayList<EbookData> arrData = new ArrayList<EbookData>();
	private String main_id;

	DataStore dataStore;

	public String link;
	private String eBook_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ebook_category);

		progressDialog = new ProgressDialog(this);
		dataStore = new DataStore(getBaseContext());

		main_id = getIntent().getStringExtra("main_id");
		loadData(main_id);

		txtUsername = (TextView) findViewById(R.id.txtUsername_ebookCat);
		txtLevel = (TextView) findViewById(R.id.txtLevel_ebookCat);
		txtExpire = (TextView) findViewById(R.id.txtExpire_ebookCat);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EbookCategoryActivity.this,
						SearchActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		checkUser();
	}

	public void checkUser() {
		if (dataStore.checkUser()) {
			txtUsername.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtUsername.setText(": "
					+ dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
			txtLevel.setText(":"
					+ dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
			txtExpire
					.setText("หมดอายุ : "
							+ dataStore.LoadSharedPreference(
									DataStore.USER_EXPIRE, ""));
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
		EbookCategoryAdapter adapter = new EbookCategoryAdapter(getBaseContext(), R.layout.movie_row, arrData);
		gridView.setAdapter(adapter);
		if (arrData.size()!=0) {
			gridView.setSelection(0);
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {
			Intent intent;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dataStore.checkUser()) {
					link = arrData.get(position).getPdf_link();
					eBook_id = arrData.get(position).getId_ebook();
					checkAcees(dataStore.LoadSharedPreference(
							DataStore.USER_ID, ""));
				} else {
					intent = new Intent(EbookCategoryActivity.this,
							LoginActivity.class);
					startActivity(intent);
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
			EbookData data = new EbookData();
			arrData = data.getCate_Ebook(params[0]);
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
		new CheckAccessTask().execute(user_id);
	}

	public class CheckAccessTask extends AsyncTask<String, Void, Void> {
		Boolean check;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			PortalServices portalServices = new PortalServices();
			String resultData = portalServices.makePortalCall(null,
					UrlApp.CHECK_EXPIRED + params[0]+"/"+dataStore.LoadSharedPreference(DataStore.USER_TOKEN, ""), PortalServices.GET);
			try {
				JSONObject jsonObject = new JSONObject(resultData);
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
			} catch (JSONException e) {
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

				// Intent launchIntent =
				// getPackageManager().getLaunchIntentForPackage("com.mediatek.videoplayer");
				// launchIntent.setData(Uri.parse(link));
				// startActivity(launchIntent);

				// Intent intent = new Intent(Intent.ACTION_VIEW,
				// Uri.parse(link));
				// intent.setPackage("com.mediatek.videoplayer");
				// startActivity(intent);

				// MainActivity mainActivity = new MainActivity();
				// mainActivity.openApp(getBaseContext(), Uri.parse(link));

				Intent intent = new Intent(EbookCategoryActivity.this,WebViewEbookActivity.class);
				intent.putExtra("eBook_id", eBook_id);
				startActivity(intent);

				// Intent intent = new
				// Intent(MovieActivity.this,VideoViewActivity.class);
				// intent.putExtra("link", link);
				// startActivity(intent);
			} else {
				Intent intent = new Intent(EbookCategoryActivity.this,
						RefillActivity.class);
				startActivity(intent);
			}
		}

	}


}
