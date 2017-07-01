package series;

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
import com.royle.beartv2.SearchSRActivity;

import java.util.ArrayList;

import helper.DataStore;

public class SeriesCategoryActivity extends Activity{
	// widget
	private GridView gridView;
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private ImageButton btnSearch;
	private TextView txtSlide;

	private ProgressDialog progressDialog;

	private ArrayList<SeriesData> arrData = new ArrayList<SeriesData>();
	private String main_id;
	private String id_movie;

	DataStore dataStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.movie_activity_movie_category);

		progressDialog = new ProgressDialog(this);
		dataStore = new DataStore(getBaseContext());

		main_id = getIntent().getStringExtra("main_id");
		id_movie = getIntent().getStringExtra("id");


		if (main_id != null) {
			loadData(main_id);
		} else {
			loadData(id_movie);
		}

		txtUsername = (TextView) findViewById(R.id.txtUsername_movieCat);
		txtExpire = (TextView) findViewById(R.id.txtExpire_movieCat);
		gridView = (GridView) findViewById(R.id.gridView_movieCat);
		gridView.setFocusable(true);
		gridView.setFocusableInTouchMode(true);
		gridView.requestFocus();
		txtLevel = (TextView) findViewById(R.id.txtLevel_movieCat);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);
				

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SeriesCategoryActivity.this, SearchSRActivity.class);
				startActivity(intent);
			}
		});

	}

	public void showContent() {
		txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));
		SeriesCategoryAdapter adapter = new SeriesCategoryAdapter(
				getBaseContext(), R.layout.movie_row, arrData);
		gridView.setAdapter(adapter);
		if (arrData.size()!=0) {
			gridView.setSelection(0);
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {
			Intent intent;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (main_id != null) {
					intent = new Intent(SeriesCategoryActivity.this,
							SeriesCategoryActivity.class);
					intent.putExtra("id", arrData.get(position).getseries_id());
					startActivity(intent);
				} else {
					intent = new Intent(SeriesCategoryActivity.this,
							SeriesDetailActivity.class);
					intent.putExtra("id", arrData.get(position).getseries_id());
					intent.putExtra("name", arrData.get(position).getseries_name());
					intent.putExtra("pic", arrData.get(position).getseries_img());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkUser();
	}

	public void checkUser() {
		if (true) {
			txtUsername.setVisibility(View.VISIBLE);
			txtLevel.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtUsername.setText(": "
					+ dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
			txtLevel.setText(": "
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
			SeriesData data = new SeriesData();

			if (main_id != null) {
				arrData = data.getCategorySeries(params[0]);
			} else {
				arrData = data.getSeasonSeries(params[0]);
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



}
